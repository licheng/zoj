/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */
#include "main.h"

#include <stdio.h>
#include <stdlib.h>

#include <algorithm>
#include <iostream>
#include <string>
#include <sstream>

#include <arpa/inet.h>
#include <asm/param.h>
#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <netinet/in.h>
#include <sys/resource.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <sys/user.h>
#include <sys/wait.h>
#include <unistd.h>

#include "check.h"
#include "compile.h"
#include "judge_result.h"
#include "kmmon-lib.h"
#include "logging.h"
#include "args.h"
#include "run.h"
#include "trace.h"
#include "util.h"

DEFINE_ARG(string, root, "The root directory of the client");

DEFINE_ARG(string, queue_address, "The ip address of the queue service to which"
                                  "this client connects");

DEFINE_ARG(int, queue_port, "The port of the queue service to which this client"
                            "connects");

DEFINE_ARG(string, lang, "All programming languages supported by this client");

DEFINE_ARG(int, uid, "The uid for executing the program to be judged");

DEFINE_ARG(int, gid, "The uid for executing the program to be judged");

DEFINE_OPTIONAL_ARG(bool, daemonize, true, "If true, the program will be "
                                           "started as a daemon process");

// Returns true if the specified file type is supported by the server
bool isSupportedSourceFileType(const string& sourceFileType) {
    vector<string> supportedLanguages;
    SplitString(ARG_lang, ',', &supportedLanguages);
    return find(supportedLanguages.begin(),
                supportedLanguages.end(),
                sourceFileType) != supportedLanguages.end();
}

int readHeader(int fdSocket,
               string* sourceFileType,
               unsigned int* problemId,
               unsigned int* version) {
    unsigned char header[HEADER_SIZE];
    int num = readn(fdSocket, header, sizeof(header));
    if (num < sizeof(header)) {
        LOG(ERROR)<<"Fail to read header";
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    const char* sourceFileTypes[] = {"cc", "cpp", "pas", "c", "java", "cs"};
    if (header[0] == 0 ||
        header[0] > sizeof(sourceFileTypes) / sizeof(sourceFileTypes[0])) {
        LOG(ERROR)<<"Invalid source file type "<<(int)header[0];
        sendReply(fdSocket, INVALID_SOURCE_FILE_TYPE);
        return -1;
    }
    *sourceFileType = sourceFileTypes[header[0] - 1];
    if (!isSupportedSourceFileType(*sourceFileType)) {
        LOG(ERROR)<<"Unsupported source file type "<<*sourceFileType;
        sendReply(fdSocket, UNSUPPORTED_SOURCE_FILE_TYPE);
        return -1;
    }
    *problemId = ntohl(*(long*)(header + 1));
    *version = ntohl(*(long*)(header + 1 + sizeof(*problemId)));
    return 0;
}

int readTestcase(int fdSocket,
                 unsigned int* testcase,
                 unsigned int* timeLimit,
                 unsigned int* memoryLimit,
                 unsigned int* outputLimit) {
    unsigned char message[TESTCASE_MSG_SIZE];
    int num = readn(fdSocket, message, sizeof(message));
    if (num < sizeof(message)) {
        LOG(ERROR)<<"Fail to read testcase request";
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    *testcase = message[0];
    *timeLimit = ntohs(*(short*)(message + 1));
    if (*testcase && *timeLimit == 0 || *timeLimit > MAX_TIME_LIMIT) {
        LOG(ERROR)<<"Invalid time limit "<<*timeLimit;
        sendReply(fdSocket, INVALID_TIME_LIMIT);
        return -1;
    }
    *memoryLimit = ntohl(*(long*)(message + 3));
    if (*testcase && *memoryLimit == 0 || *memoryLimit > MAX_MEMORY_LIMIT) {
        LOG(ERROR)<<"Invalid memory limit "<<*memoryLimit;
        sendReply(fdSocket, INVALID_MEMORY_LIMIT);
        return -1;
    }
    *outputLimit = ntohs(*(short*)(message + 7));
    if (*testcase && *outputLimit == 0 || *outputLimit > MAX_OUTPUT_LIMIT) {
        LOG(ERROR)<<"Invalid output limit "<<*outputLimit;
        sendReply(fdSocket, INVALID_OUTPUT_LIMIT);
        return -1;
    }
    return 0;
}

// Reads the file content from the given file descriptor and writes the file
// specified by outputFilename. Creates the file if not exists.
//
// Return 0 if success, or -1 if any error occurs.
int saveFile(int fdSocket, const string& outputFilename, size_t size) {
    int fdFile = open(outputFilename.c_str(),
                      O_RDWR | O_CREAT | O_TRUNC, 0640);
    if (fdFile == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create file "<<outputFilename;
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    static char buffer[4096];
    while (size) {
        size_t count = min(size, sizeof(buffer));
        if (readn(fdSocket, buffer, count) < count) {
            LOG(ERROR)<<"Fail to read file";
            sendReply(fdSocket, INVALID_DATA);
            close(fdFile);
            return -1;
        }
        if (writen(fdFile, buffer, count) == -1) {
            LOG(ERROR)<<"Fail to write to "<<outputFilename;
            sendReply(fdSocket, INTERNAL_ERROR);
            close(fdFile);
            return -1;
        }
        size -= count;
    }
    close(fdFile);
    return 0;
}

int readSourceFilename(int fdSocket, string* sourceFilename) {
    unsigned short size;
    if (readn(fdSocket, &size, 2) < 2) {
        LOG(ERROR)<<"Fail to read the length of the source file path";
        sendReply(fdSocket, INVALID_DATA_SIZE);
        return -1;
    }
    size = ntohs(size);
    char path[PATH_MAX + 1];
    if (readn(fdSocket, path, size) < size) {
        LOG(ERROR)<<"Fail to read the source file path";
        sendReply(fdSocket, INVALID_DATA);
        return -1;
    }
    path[size] = 0;
    if (access(path, F_OK) == -1) {
        LOG(ERROR)<<"Fail to access the source file at "<<path;
        sendReply(fdSocket, INVALID_DATA);
        return -1;
    }
    *sourceFilename = path;
    return 0;
}

int saveSourceFile(int fdSocket, const string& sourceFileName) {
    unsigned short size;
    if (readn(fdSocket, &size, sizeof(size)) < sizeof(size)) {
        LOG(ERROR)<<"Fail to read file size";
        sendReply(fdSocket, INVALID_DATA_SIZE);
        return -1;
    }
    size = ntohs(size);
    if (saveFile(fdSocket, sourceFileName, size) == -1) {
        return -1;
    }
    return 0;
}

int checkData(int fdSocket, const string& data_dir) {
    DIR* dir = opendir(data_dir.c_str());
    if (dir == NULL) {
        LOG(SYSCALL_ERROR)<<"Can not open dir "<<data_dir;
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    int ret = 0;
    vector<int> in, out;
    string judge;
    for (;;) {
        struct dirent* entry = readdir(dir);
        if (entry == NULL) {
            break;
        }
        if (strcmp(entry->d_name, ".") == 0 ||
            strcmp(entry->d_name, "..") == 0 ||
            strcmp(entry->d_name, "data.zip") == 0) {
            continue;
        }
        struct stat status;
        lstat(StringPrintf("%s/%s", data_dir.c_str(), entry->d_name).c_str(),
              &status);
        if (!S_ISREG(status.st_mode)) {
            LOG(ERROR)<<"Invalid file "<<entry->d_name;
            ret = -1;
            break;
        }
        int index;
        if (StringEndsWith(entry->d_name, ".in")) {
            if (sscanf(entry->d_name, "%d.in", &index) != 1) {
                LOG(ERROR)<<"Invalid filename "<<entry->d_name;
                ret = -1;
                break;
            }
            in.push_back(index);
        } else if (StringEndsWith(entry->d_name, ".out")) {
            if (sscanf(entry->d_name, "%d.out", &index) != 1) {
                LOG(ERROR)<<"Invalid filename "<<entry->d_name;
                ret = -1;
                break;
            }
            out.push_back(index);
        } else if (StringStartsWith(entry->d_name, "judge.")) {
            if (!isSupportedSourceFileType(entry->d_name + 6)) {
                LOG(ERROR)<<"Unsupported file type "<<entry->d_name + 6;
                ret = -1;
                break;
            }
            judge = entry->d_name;
        } else {
            LOG(ERROR)<<"Invalid filename "<<entry->d_name;
            ret = -1;
            break;
        }
    }
    closedir(dir);
    if (ret == 0) {
        if (in.empty()) {
            LOG(ERROR)<<"Empty directory "<<data_dir;
            sendReply(fdSocket, INVALID_DATA);
            return -1;
        }
        sort(in.begin(), in.end());
        sort(out.begin(), out.end());
        if (judge.empty()) {
            for (int i = 0; i < in.size(); ++i) {
                if (i >= out.size() || in[i] < out[i]) {
                    LOG(ERROR)<<"No "<<in[i]<<".out found for "<<in[i]<<".in";
                    ret = -1;
                    break;
                } else if (in[i] > out[i]) {
                    LOG(ERROR)<<"No "<<out[i]<<".in found for "<<out[i]<<".out";
                    ret = -1;
                    break;
                }
            }
            if (out.size() > in.size()) {
                LOG(ERROR)<<"No "<<out[in.size()]<<".in found for "
                          <<out[in.size()]<<".out";
                ret = -1;
            }
        } else if (doCompile(fdSocket, data_dir + "/" + judge) == -1) {
            return -1;
        }
    }
    if (ret < 0) {
        sendReply(fdSocket, INVALID_DATA);
    }
    return ret;
}

int saveData(int fdSocket, unsigned int problemId, unsigned int version) {
    string problemDir = StringPrintf("%s/prob/%u", ARG_root.c_str(), problemId);
    string versionDir = StringPrintf("%s/%u", problemDir.c_str(), version);
    string tempDir = StringPrintf("%s.%u.%s",
                                  versionDir.c_str(),
                                  getpid(),
                                  getLocalTimeAsString("%Y%m%d%H%M%S").c_str());
    LOG(INFO)<<"Creating temporary directory "<<tempDir;
    if (mkdir(tempDir.c_str(), 0750) == -1) {
        if (errno == ENOENT) {
            LOG(INFO)<<"Up level directory missing";
            LOG(INFO)<<"Creating problem directory "<<problemDir;
            if (mkdir(problemDir.c_str(), 0750) == -1) {
                if (errno != EEXIST) {
                    LOG(SYSCALL_ERROR)<<"Fail to create dir "<<problemDir;
                    sendReply(fdSocket, INTERNAL_ERROR);
                    return -1;
                }
            }
            LOG(INFO)<<"Creating temporary directory "<<tempDir;
            if (mkdir(tempDir.c_str(), 0750) == -1) {
                LOG(SYSCALL_ERROR)<<"Fail to create dir "<<tempDir;
                sendReply(fdSocket, INTERNAL_ERROR);
                return -1;
            }
        } else if (errno != EEXIST) {
            LOG(SYSCALL_ERROR)<<"Fail to create dir "<<tempDir;
            sendReply(fdSocket, INTERNAL_ERROR);
            return -1;
        }
    }
    LOG(INFO)<<"Saving data file";
    unsigned long size;
    if (readn(fdSocket, &size, 4) < 4) {
        LOG(ERROR)<<"Fail to read file size";
        sendReply(fdSocket, INVALID_DATA_SIZE);
        return -1;
    }
    size = ntohl(size);
    LOG(INFO)<<"File size: "<<size;
    if (size > MAX_DATA_FILE_SIZE) {
        LOG(ERROR)<<"File size too large: "<<size;
        sendReply(fdSocket, INVALID_DATA_SIZE);
        return -1;
    }
    if (saveFile(fdSocket, tempDir + "/data.zip", size) == -1) {
        return -1;
    }
    LOG(INFO)<<"Unzipping data file";
    string command = StringPrintf("unzip '%s/data.zip' -d '%s'",
                                  tempDir.c_str(), tempDir.c_str());
    int result = system(command.c_str());
    if (result) {
        LOG(ERROR)<<"Fail to unzip data file. Command: "<<command;
        sendReply(fdSocket, INVALID_DATA);
        return -1;
    }
    LOG(INFO)<<"Checking data";
    if (checkData(fdSocket, tempDir) == -1) {
        return -1;
    }
    if (link((tempDir + "/1.in").c_str(),
             (tempDir + "/input").c_str()) == -1) {
        LOG(SYSCALL_ERROR)<<"Can not create input";
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    if (link((tempDir + "/1.out").c_str(),
             (tempDir + "/output").c_str()) == -1) {
        LOG(SYSCALL_ERROR)<<"Can not create output";
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    if (rename(tempDir.c_str(), versionDir.c_str()) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to rename "<<tempDir<<" to "<<versionDir;
        sendReply(fdSocket, INTERNAL_ERROR);
        system(StringPrintf("rm -rf '%s'", tempDir.c_str()).c_str());
        return -1;
    }
    sendReply(fdSocket, READY);
    return 0;
}

// Deal with a single judge request
int process(int fdSocket) {
    string sourceFileType;
    unsigned int problemId;
    unsigned int version;
    if (readHeader(fdSocket,
                   &sourceFileType,
                   &problemId,
                   &version) == -1) {
        return -1;
    }
    LOG(INFO)<<StringPrintf("%u.%s version:%u",
                            problemId,
                            sourceFileType.c_str(),
                            version);
    string working_root =
        StringPrintf("%s/working/%d", ARG_root.c_str(), getpid());
    string binaryFilename = working_root + "/prob";
    string sourceFilename = binaryFilename + "." + sourceFileType;
    string programOutputFilename = working_root + "/out";
    if (isLocalHost(ARG_queue_address)) {
        LOG(INFO)<<"Reading source file path";
        if (readSourceFilename(fdSocket, &sourceFilename) == -1) {
            return -1;
        }
    } else {
        LOG(INFO)<<"Saving source file";
        if (saveSourceFile(fdSocket, sourceFilename) == -1) {
            return -1;
        }
    }

    string problemDir =
        StringPrintf("%s/prob/%u/%u", ARG_root.c_str(), problemId, version);
    if (access(problemDir.c_str(), F_OK) == 0) {
        sendReply(fdSocket, READY);
    } else if (errno != ENOENT) {
        LOG(SYSCALL_ERROR)<<"Fail to access "<<problemDir;
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    } else {
        sendReply(fdSocket, NO_SUCH_PROBLEM);
        LOG(INFO)<<"Begin data synchronization";
        if (saveData(fdSocket, problemId, version) == -1) {
            LOG(INFO)<<"Data synchronization failed";
            return -1;
        }
        LOG(INFO)<<"Data synchronization succeeded";
    }

    if (doCompile(fdSocket, sourceFilename) == -1) {
        return -1;
    }
    sendReply(fdSocket, READY);

    for (;;) {
        unsigned int testcase;
        unsigned int timeLimit;
        unsigned int memoryLimit;
        unsigned int outputLimit;
        if (readTestcase(fdSocket,
                         &testcase,
                         &timeLimit,
                         &memoryLimit,
                         &outputLimit) == -1) {
            return -1;
        }
        LOG(INFO)<<StringPrintf("Testcase %u TL:%u ML:%u OL:%u",
                                testcase,
                                timeLimit,
                                memoryLimit,
                                outputLimit);
        if (testcase == 0) {
            break;
        }
        string inputFilename =
            StringPrintf("%s/%u.in", problemDir.c_str(), testcase);
        string outputFilename =
            StringPrintf("%s/%u.out", problemDir.c_str(), testcase);
        string specialJudgeFilename = problemDir + "/judge";
        if (access(inputFilename.c_str(), F_OK) == -1) {
            LOG(ERROR)<<"Invalid test case "<<testcase;
            sendReply(fdSocket, INVALID_TESTCASE);
            return -1;
        } else {
            int result = doRun(fdSocket,
                               binaryFilename,
                               sourceFileType,
                               inputFilename,
                               programOutputFilename,
                               timeLimit,
                               memoryLimit,
                               outputLimit);
            if (result == -1 ||
                result == 0 && doCheck(fdSocket,
                                       inputFilename,
                                       outputFilename,
                                       programOutputFilename,
                                       specialJudgeFilename) == -1) {
                return -1;
            }
        }
    }
    return 0;
}

int createServerSocket(int* port) {
    int fdServerSocket = socket(PF_INET, SOCK_STREAM, 6);
    if (fdServerSocket == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create socket";
        return -1;
    }
    int optionValue = 1;
    if (setsockopt(fdServerSocket, 
                   SOL_SOCKET,
                   SO_REUSEADDR,
                   &optionValue,
                   sizeof(optionValue)) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to set socket option";
        close(fdServerSocket);
        return -1;
    }
    sockaddr_in address;
    memset(&address, 0, sizeof(address));
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = htonl(INADDR_ANY); 
    address.sin_port = 0;
    if (bind(fdServerSocket,
             (struct sockaddr*)&address,
             sizeof(address)) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to bind";
        close(fdServerSocket);
        return -1;
    }
    if (listen(fdServerSocket, 32) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to listen";
        close(fdServerSocket);
        return -1;
    }
    socklen_t len = sizeof(address);
    if (getsockname(fdServerSocket,
                    (struct sockaddr*)&address,
                    &len) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to get socket port";
        close(fdServerSocket);
        return -1;
    }
    *port = ntohs(address.sin_port);
    return fdServerSocket;
}

void daemonize() {
    umask(0);
    int pid = fork();
    if (pid < 0) {
        LOG(SYSCALL_ERROR);
        exit(1);
    } else if (pid > 0) {
        exit(0);
    }

    // start a new session
    setsid();

    // ignore SIGHUP
    if (installSignalHandler(SIGHUP, SIG_IGN) == SIG_ERR) {
        LOG(SYSCALL_ERROR)<<"Fail to ignore SIGHUP";
        exit(1);
    }

    // attach file descriptor 0, 1, 2 to /dev/null
    int fd = open("/dev/null", O_RDWR);
    dup2(fd, 0);
    dup2(fd, 1);
    dup2(fd, 2);
    
    // close all other file descriptors
    for (int i = 3; i < 100; i++) {
        close(i);
    }
}

int alreadyRunning() {
    int fd = open((ARG_root + "/judge.pid").c_str(), O_RDWR | O_CREAT, 0640);
    if (fd < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to open judge.pid";
        exit(1);
    }
    if (lockFile(fd, F_SETLK) == -1) {
        if (errno == EACCES || errno == EAGAIN) {
            close(fd);
            return 1;
        } else {
            LOG(SYSCALL_ERROR)<<"Fail to lock judge.pid";
            exit(1);
        }
    }
    ftruncate(fd, 0);
    char buffer[20];
    sprintf(buffer, "%ld", (long)getpid());
    write(fd, buffer, strlen(buffer) + 1);
    return 0;
}

int connectTo(const string& address, int port) {
    int fdSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (fdSocket == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create socket";
        return -1;
    }
    struct sockaddr_in servaddr;
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(port);
    if (inet_pton(AF_INET, address.c_str(), &servaddr.sin_addr) <= 0) {
        LOG(SYSCALL_ERROR)<<"Invalid address "<<address;
        close(fdSocket);
        return -1;
    }
    LOG(INFO)<<"Connecting to "<<address<<":"<<port;
    if (connect(fdSocket, (const sockaddr*)&servaddr, sizeof(servaddr)) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to connect to "<<address<<":"<<port;
        close(fdSocket);
        return -1;
    }
    LOG(INFO)<<"Connected";
    return fdSocket;
}

int terminated = 0;
int socket_closed = 0;

void sigtermHandler(int sig) {
    terminated = 1;
}

void sigpipeHandler(int sig) {
    socket_closed = 1;
}

void sigchldHandler(int sig) {
    while (waitpid(-1, NULL, WNOHANG) > 0 || errno == EINTR);
}

void notifyQueue(int port) {
    port = htonl(port);
    socket_closed = 1;
    // Loops until SIGTERM is received.
    int fdSocket = -1;
    while (!terminated) {
        if (socket_closed) {
            if (fdSocket >= 0) {
                close(fdSocket);
            }
            fdSocket = connectTo(ARG_queue_address, ARG_queue_port);
            if (fdSocket >= 0) {
                socket_closed = 0;
            }
        }
        if (!socket_closed && !terminated) {
            writen(fdSocket, &port, sizeof(port));
        }
        sleep(10);
    }
    if (fdSocket >= 0) {
        close(fdSocket);
    }
}

int childMain(int fdSocket) {
    string working_root =
        StringPrintf("%s/working/%d", ARG_root.c_str(), getpid());
    if (mkdir(working_root.c_str(), 0777) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create dir "<<working_root;
        sendReply(fdSocket, INTERNAL_ERROR);
        return 1;
    }

    // installs handlers for tracing.
    installHandlers();

    // Loops until SIGTERM is received.
    while (!socket_closed && !terminated) {
        if (process(fdSocket) == -1) {
            return -1;
        }
        // clear all temporary files.
        system(StringPrintf("rm -f %s/*", working_root.c_str()).c_str());
    }
    close(fdSocket);
    system(StringPrintf("rm -rf %s", working_root.c_str()).c_str());
    return 0;
}

int execMain(int argc, char* argv[]) {
    if (parseArguments(argc, argv) < 0) {
        return 1;
    }
    if (ARG_daemonize) {
        daemonize();
        if (alreadyRunning()) {
            LOG(ERROR)<<"Another instance of judge_client exists";
            return 1;
        }
    }

    if (chdir(ARG_root.c_str()) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to change working dir to "<<ARG_root<<endl;
        return 1;
    }

    char path[PATH_MAX + 1];
    if (getcwd(path, sizeof(path)) == NULL) {
        LOG(SYSCALL_ERROR)<<"Fail to get the current working dir";
        return 1;
    }
    ARG_root = path;

    installSignalHandler(SIGCHLD, sigchldHandler);

    installSignalHandler(SIGTERM, sigtermHandler);

    // prevents SIGPIPE to terminate the processes.
    installSignalHandler(SIGPIPE, sigpipeHandler);

    int port;
    int fdServerSocket = createServerSocket(&port);
    if (fdServerSocket == -1) {
        return 1;
    }
    LOG(INFO)<<"Listening on port "<<port;

    pid_t pid = fork();
    if (pid < 0) {
        LOG(SYSCALL_ERROR);
        return 1;
    } else if (pid == 0) {
        notifyQueue(port);
        return 0;
    }
    
    // Waiting for judge queue to connect
    while (!terminated) {
        sockaddr_in address;
        socklen_t addressLength = sizeof(sockaddr_in);
        int fdSocket = accept(
                fdServerSocket, (struct sockaddr*)&address, &addressLength);
        if (fdSocket == -1) {
            if (errno != EINTR) {
                LOG(SYSCALL_ERROR);
            }
            continue;
        }
        LOG(INFO)<<"Received connection from "<<address.sin_addr.s_addr<<":"<<address.sin_port;
        pid_t pid = fork();
        if (pid < 0) {
            LOG(SYSCALL_ERROR);
            return 1;
        } else if (pid == 0) {
            exit(childMain(fdSocket));
        }
        close(fdSocket);
    }
    return 0;
}
