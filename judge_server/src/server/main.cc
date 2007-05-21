/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ Judge Server.
 *
 * ZOJ Judge Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZOJ Judge Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ Judge Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

#include <stdio.h>
#include <stdlib.h>

#include <string>
#include <sstream>

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

#include "judge.h"
#include "judge_result.h"
#include "kmmon-lib.h"
#include "logging.h"
#include "params.h"
#include "run.h"
#include "trace.h"
#include "util.h"

// The file descriptor of the server socket
static int fdServerSocket = 0;

// PIDs of all child processes
static pid_t* pids;

// Returns true if the specified file type is supported by the server
bool isSupportedSourceFileType(const std::string& sourceFileType) {
    return (int) LANG.find("," + sourceFileType + ",") >= 0;
}

#define INPUT_FAIL(fd, messages) if(0);else{\
    LOG(ERROR)<<messages;\
    sendReply(fd, INTERNAL_ERROR);\
    std::ostringstream message;\
    message<<messages;\
    std::string s = message.str();\
    writen(fd, s.c_str(), s.size());\
    return -1;}

// Validates the command and invokes appropriate functions. Returns 0 if the
// command is executed successfully, -1 otherwise.
int dispatch(int fdSocket, const char* command_line) {
    std::istringstream is(command_line);
    std::string command;
    is>>command;
    if (command == "save") {
        std::string problem_name;
        std::string version;
        is>>problem_name>>version;
        if (is.fail()) {
            INPUT_FAIL(fdSocket, "Invalid command: "<<command_line);
        }
        const std::string zipFile =
            JUDGE_ROOT + "/prob/" + problem_name + "_" + version + ".zip";
        if (saveFile(fdSocket, zipFile) == -1) {
            return -1;
        }
        std::string command = JUDGE_ROOT + "/script/save_problem.sh '" +
                              problem_name + "' '" + version + "'";
        char errorMessage[128];
        int errorMessageLength = sizeof(errorMessage) - 1;
        if (runShellCommand(command.c_str(),
                            errorMessage,
                            &errorMessageLength)) {
            errorMessage[errorMessageLength] = 0;
            INPUT_FAIL(fdSocket, errorMessage);
            return -1;
        }
        return 0;
    } else if (command == "judge") {
        std::string sourceFileType;
        std::string problem_name;
        std::string testcase;
        std::string version;
        int time_limit;
        int memory_limit;
        int output_limit;
        is>>sourceFileType>>problem_name>>testcase>>version
            >>time_limit>>memory_limit>>output_limit;
        if (is.fail()) {
            INPUT_FAIL(fdSocket, "Invalid command: "<<command_line);
        }
        if (!isSupportedSourceFileType(sourceFileType)) {
            INPUT_FAIL(fdSocket,
                       "Unsupported source file type "<<sourceFileType);
        }
        if (time_limit < 0 || time_limit > MAX_TIME_LIMIT) {
            INPUT_FAIL(fdSocket, "Invalid time limit "<<time_limit);
        }
        if (memory_limit < 0 || memory_limit > MAX_TIME_LIMIT) {
            INPUT_FAIL(fdSocket, "Invalid memory limit "<<memory_limit);
        }
        if (output_limit < 0 || output_limit > MAX_TIME_LIMIT) {
            INPUT_FAIL(fdSocket, "Invalid output limit "<<output_limit);
        }
        int ret = execJudgeCommand(fdSocket,
                                   sourceFileType,
                                   problem_name,
                                   testcase,
                                   version,
                                   time_limit,
                                   memory_limit,
                                   output_limit);
        // clear all temporary files.
        system("rm -f *");
        return ret;
    } else {
        INPUT_FAIL(fdSocket, "Unrecognized command: "<<command);
        return -1;
    }
}

// Writes the current PID to judge.pid, creates it if not exists.
// Return 0 if successful, 1 if the file is already locked by another process,
// or -1 if any error occurs.
int alreadyRunning() {
    int fd = open("judge.pid", O_RDWR | O_CREAT, 0640);
    if (fd < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to open judge.pid";
        return -1;
    }
    if (lockFile(fd, F_SETLK) == -1) {
        if (errno == EACCES || errno == EAGAIN) {
            close(fd);
            return 1;
        } else {
            LOG(SYSCALL_ERROR)<<"Fail to lock judge.pid";
            return -1;
        }
    }
    ftruncate(fd, 0);
    char buffer[20];
    sprintf(buffer, "%ld", (long)getpid());
    write(fd, buffer, strlen(buffer));
    return 0;
}

static int terminated = 0;

static void sigtermHandler(int signum) {
    terminated = 1;
}

// The main function of the child process.
void childMain() {
    // prevents SIGPIPE to terminate the process.
    installSignalHandler(SIGPIPE, SIG_IGN);

    // installs handlers for tracing.
    installHandlers();

    // Loops until SIGTERM is received.
    while (!terminated) {
        sockaddr_in address;
        socklen_t addressLength = sizeof(sockaddr_in);
        int fdClientSocket = accept(
                fdServerSocket, (struct sockaddr*)&address, &addressLength);
        if (fdClientSocket == -1) {
            LOG(SYSCALL_ERROR);
            continue;
        }
        char buffer[65];
        int num = readn(fdClientSocket, buffer, sizeof(buffer) - 1);
        if (num == 0) {
            sendReply(fdClientSocket, INTERNAL_ERROR);
            writen(fdClientSocket, "No input", 8);
        } else if (num > 0) {
            while (num > 0 && buffer[num - 1] == ' ') {
                num--;
            }
            buffer[num] = 0;
            LOG(INFO)<<buffer;
            dispatch(fdClientSocket, buffer);
        }
        close(fdClientSocket);
    }
}

// Creates a judge process.
pid_t createChild() {
    pid_t pid = fork();
    if (pid < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create child";
        return -1;
    } else if (pid > 0) {
        return pid;
    } else {
        char buffer[MAX_BUFFER_SIZE];
        sprintf(buffer, "working/%d", getpid());
        if (mkdir(buffer, 0777) < 0) {
            LOG(SYSCALL_ERROR)<<"Fail to create dir "<<buffer;
            exit(1);
        }
        if (chdir(buffer) < 0) {
            LOG(SYSCALL_ERROR)<<"Fail to change working dir to "<<buffer;
            exit(1);
        }
        childMain();
        exit(0);
    }
}

int main(int argc, char* argv[]) {
    if (parseArguments(argc, argv) < 0) {
        return 1;
    }
    daemonize();
    if (alreadyRunning()) {
        return 1;
    }
    sigset_t mask;
    sigemptyset(&mask);
    installSignalHandler(SIGTERM, sigtermHandler, 0, mask);
    fdServerSocket = createServerSocket(SERVER_PORT);
    if (fdServerSocket == -1) {
        return 1;
    }
    pids = (pid_t*) malloc(sizeof(pid_t) * MAX_JOBS);
    memset(pids, 0, sizeof(pid_t) * MAX_JOBS);
    for (int i = 0; i < MAX_JOBS; i++) {
        pids[i] = createChild();
    }
    while (!terminated) {
        pause();
    }
    for (int i = 0; i < MAX_JOBS; i++) {
        kill(pids[i], SIGTERM);
    }
    for (;;) {
        wait(NULL);
        if (errno == ECHILD) {
            break;
        }
    }
    system("rm -rf working/*");
    return 0;
}
