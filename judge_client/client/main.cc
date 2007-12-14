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

#include <stdio.h>
#include <stdlib.h>

#include <algorithm>
#include <iostream>
#include <string>
#include <sstream>

#include <arpa/inet.h>
#include <asm/param.h>
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

// The root directory which contains problems, scripts and working directory of
// the client
DEFINE_ARG(string, root, "");

// The ip address of the queue service to which this client connects
DEFINE_ARG(string, queue_address, "");

// The port of the queue service to which this client connects
DEFINE_ARG(int, queue_port, "");

// All languages supported by this client
DEFINE_ARG(string, lang, "");

#define MAX_TIME_LIMIT 300
#define MAX_MEMORY_LIMIT (1024 * 1024)
#define MAX_OUTPUT_LIMIT (16 * 1024)
#define MAX_DATA_FILE_SIZE (16 * 1024)

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
    unsigned char header[9];
    int num = readn(fdSocket, header, sizeof(header));
    if (num < sizeof(header)) {
        LOG(ERROR)<<"Fail to read header";
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    const char* sourceFileTypes[] = {"cc", "cpp", "pas", "c", "java", "cs"};
    if (header[0] == 0 ||
        header[0] > sizeof(sourceFileTypes) / sizeof(sourceFileTypes[0])) {
        LOG(ERROR)<<"Invalid source file type "<<header[0];
        sendReply(fdSocket, UNSUPPORTED_SOURCE_FILE_TYPE);
        return -1;
    }
    *sourceFileType = sourceFileTypes[header[0] - 1];
    if (!isSupportedSourceFileType(*sourceFileType)) {
        LOG(ERROR)<<"Unsupported source file type "<<*sourceFileType;
        sendReply(fdSocket, UNSUPPORTED_SOURCE_FILE_TYPE);
        return -1;
    }
    *problemId = ntohl(*(long*)(header + 1));
    *version = ntohl(*(long*)(header + 5));
    return 0;
}

int readTestcase(int fdSocket,
                 unsigned int* testcase,
                 unsigned int* timeLimit,
                 unsigned int* memoryLimit,
                 unsigned int* outputLimit) {
    unsigned char message[9];
    int num = readn(fdSocket, message, sizeof(message));
    if (num < sizeof(message)) {
        LOG(ERROR)<<"Fail to read testcase request";
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    *testcase = message[0];
    *timeLimit = ntohs(*(short*)(message + 1));
    if (*timeLimit == 0 || *timeLimit > MAX_TIME_LIMIT) {
        LOG(ERROR)<<"Invalid time limit"<<*timeLimit;
        sendReply(fdSocket, INVALID_TIME_LIMIT);
        return -1;
    }
    *memoryLimit = ntohl(*(long*)(message + 3));
    if (*memoryLimit == 0 || *memoryLimit > MAX_MEMORY_LIMIT) {
        LOG(ERROR)<<"Invalid memory limit"<<*memoryLimit;
        sendReply(fdSocket, INVALID_MEMORY_LIMIT);
        return -1;
    }
    *outputLimit = ntohs(*(short*)(message + 7));
    if (*outputLimit == 0 || *outputLimit > MAX_OUTPUT_LIMIT) {
        LOG(ERROR)<<"Invalid output limit"<<*outputLimit;
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
                      O_RDWR | O_CREAT | O_TRUNC);
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
            return -1;
        }
        if (writen(fdFile, buffer, count) == -1) {
            LOG(ERROR)<<"Fail to write to "<<outputFilename;
            sendReply(fdSocket, INTERNAL_ERROR);
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
    if (readn(fdSocket, &size, 2) < 2) {
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

bool isValidDataStructure(const string& dir) {
    return true;
}

int saveData(int fdSocket, unsigned int problemId, unsigned int version) {
    string problemDir = StringPrintf("../../prob/%u", problemId);
    string versionDir = problemDir + StringPrintf("/%u", version);
    string tempDir = versionDir +
                     StringPrintf(".%u", getpid()) +
                     getLocalTimeAsString("%Y%m%d%H%M%S");
    LOG(INFO)<<"Creating temporary directory";
    if (mkdir(tempDir.c_str(), 0750) == -1) {
        if (errno == ENOENT) {
            if (mkdir(problemDir.c_str(), 0750) == -1) {
                if (errno != EEXIST) {
                    LOG(SYSCALL_ERROR)<<"Fail to create dir "<<problemDir;
                    sendReply(fdSocket, INTERNAL_ERROR);
                    return -1;
                }
            }
            if (mkdir(tempDir.c_str(), 0750) == -1) {
                LOG(SYSCALL_ERROR)<<"Fail to create dir "<<tempDir;
                sendReply(fdSocket, INTERNAL_ERROR);
                return -1;
            }
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
    LOG(DEBUG)<<"File size: "<<size;
    if (size > MAX_DATA_FILE_SIZE) {
        LOG(ERROR)<<"File size too large: "<<size;
        sendReply(fdSocket, INVALID_DATA_SIZE);
    }
    if (saveFile(fdSocket, tempDir + "/data.zip", size) == -1) {
        return -1;
    }
    LOG(INFO)<<"Unzipping data file";
    string command = "unzip '" + tempDir + "/data.zip' -d '" + tempDir + "'";
    LOG(DEBUG)<<command;
    int result = system(command.c_str());
    if (result) {
        LOG(ERROR)<<"Fail to unzip data file. Command: "<<command;
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    LOG(INFO)<<"Checking data";
    if (!isValidDataStructure(tempDir)) {
        sendReply(fdSocket, INVALID_DATA);
        return -1;
    }
    if (rename(tempDir.c_str(), versionDir.c_str()) == -1) {
        if (errno != EEXIST && errno != ENOTEMPTY) {
            LOG(SYSCALL_ERROR)<<"Fail to rename "<<tempDir<<" to "<<versionDir;
            sendReply(fdSocket, INTERNAL_ERROR);
            system(("rm -rf '" + tempDir + "'").c_str());
            return -1;
        }
    }
    return 0;
}

// Deal with a single judge request
void process(int fdSocket) {
    string sourceFileType;
    unsigned int problemId;
    unsigned int version;
    if (readHeader(fdSocket,
                   &sourceFileType,
                   &problemId,
                   &version) == -1) {
        return;
    }
    LOG(INFO)<<StringPrintf("%u.%s version:%u",
                            problemId,
                            sourceFileType.c_str(),
                            version);
    string sourceFilename = "prob." + sourceFileType;
    if (isLocalHost(ARG_queue_address)) {
        LOG(INFO)<<"Reading source file path";
        if (readSourceFilename(fdSocket, &sourceFilename) == -1) {
            return;
        }
    } else {
        LOG(INFO)<<"Saving source file";
        if (saveSourceFile(fdSocket, sourceFilename) == -1) {
            return;
        }
    }

    string problemDir = StringPrintf("../../prob/%u/%u", problemId, version);
    if (access(problemDir.c_str(), F_OK) == -1) {
        if (errno != ENOENT) {
            LOG(SYSCALL_ERROR)<<"Fail to access "<<problemDir;
            sendReply(fdSocket, INTERNAL_ERROR);
            return;
        }
        sendReply(fdSocket, NO_SUCH_PROBLEM);
        LOG(INFO)<<"Begin data synchronization";
        if (saveData(fdSocket, problemId, version) == -1) {
            LOG(INFO)<<"Data synchronization failed";
            return;
        }
        LOG(INFO)<<"Data synchronization succeeded";
    }

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
            return;
        }
        LOG(INFO)<<StringPrintf("Testcase %u TL:%u ML:%u OL:%u",
                                testcase,
                                timeLimit,
                                memoryLimit,
                                outputLimit);
        if (testcase == 0) {
            break;
        }
        string inputFilename = problemDir + StringPrintf("/%u.in", testcase);
        string outputFilename = problemDir + StringPrintf("/%u.out", testcase);
        string specialJudgeFilename = problemDir + "/judge";
        if (access(inputFilename.c_str(), F_OK) == -1) {
            LOG(ERROR)<<"Invalid test case "<<testcase;
            sendReply(fdSocket, INVALID_TESTCASE);
        } else {
            doCompile(fdSocket, sourceFilename) == 0 &&
            doRun(fdSocket,
                  "prob",
                  sourceFileType,
                  inputFilename,
                  "out",
                  timeLimit,
                  memoryLimit,
                  outputLimit) == 0 &&
            doCheck(fdSocket,
                    inputFilename,
                    outputFilename,
                    "out",
                    specialJudgeFilename) == 0;
        }
    }
}

int terminated = 0;

void sigtermHandler(int sig) {
    terminated = 1;
}

int main(int argc, char* argv[]) {
    if (parseArguments(argc, argv) < 0) {
        return 1;
    }
    if (chdir(ARG_root.c_str()) < 0) {
        cerr<<strerror(errno)<<endl
            <<"Fail to change working dir to "<<ARG_root<<endl;
        return 1;
    }
    sigset_t mask;
    sigemptyset(&mask);
    installSignalHandler(SIGTERM, sigtermHandler, 0, mask);

    // prevents SIGPIPE to terminate the process.
    installSignalHandler(SIGPIPE, SIG_IGN);

    // installs handlers for tracing.
    installHandlers();

    string working_root = StringPrintf("working/%d", getpid());
    if (mkdir(working_root.c_str(), 0777) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create dir "<<working_root;
        return 1;
    }
    if (chdir(working_root.c_str()) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to change working dir to "<<working_root;
        return 1;
    }
    int fdSocket = socket(AF_INET, SOCK_STREAM, 0);
    struct sockaddr_in servaddr;
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(ARG_queue_port);
    if (inet_pton(AF_INET,
                  ARG_queue_address.c_str(),
                  &servaddr.sin_addr) <= 0) {
        LOG(SYSCALL_ERROR)<<"Invalid address "<<ARG_queue_address;
        return 1;
    }
    if (connect(fdSocket, (const sockaddr*)&servaddr, sizeof(servaddr)) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to connect to "
                          <<ARG_queue_address<<":"<<ARG_queue_port;
    }

    // Loops until SIGTERM is received.
    while (!terminated) {
        process(fdSocket);
        // clear all temporary files.
        system("rm -f *");
    }
    close(fdSocket);
    chdir("..");
    system(StringPrintf("rm -rf %d", getpid()).c_str());
    return 0;
}
