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

#include <algorithm>
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

#include "judge.h"
#include "judge_result.h"
#include "kmmon-lib.h"
#include "logging.h"
#include "params.h"
#include "run.h"
#include "trace.h"
#include "util.h"

// Returns true if the specified file type is supported by the server
bool isSupportedSourceFileType(const string& sourceFileType) {
    return find(LANG.begin(), LANG.end(), sourceFileType) != LANG.end();
}

#define INPUT_FAIL(fd, messages) if(0);else{\
    LOG(ERROR)<<messages;\
    sendReply(fd, INTERNAL_ERROR);\
    ostringstream message;\
    message<<messages;\
    string s = message.str();\
    writen(fd, s.c_str(), s.size());\
    return;}

// Validates the command and invokes appropriate functions. Returns 0 if the
// command is executed successfully, -1 otherwise.
/*int dispatch(int fdSocket, const char* command_line) {
    istringstream is(command_line);
    string command;
    is>>command;
    if (command == "save") {
        string problem_name;
        string version;
        is>>problem_name>>version;
        if (is.fail()) {
            INPUT_FAIL(fdSocket, "Invalid command: "<<command_line);
        }
        const string zipFile =
            JUDGE_ROOT + "/prob/" + problem_name + "_" + version + ".zip";
        if (saveFile(fdSocket, zipFile) == -1) {
            return -1;
        }
        string command = JUDGE_ROOT + "/script/save_problem.sh '" +
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
    } else {
        INPUT_FAIL(fdSocket, "Unrecognized command: "<<command);
        return -1;
    }
}*/

// Deal with a single judge request
void process(int fdSocket) {
    char command_line[128];
    int num = readn(fdSocket, command_line, sizeof(command_line) - 1);
    if (num == 0) {
        sendReply(fdSocket, INTERNAL_ERROR);
        writen(fdSocket, "No input", 8);
        return;
    }
    command_line[num] = 0;
    LOG(INFO)<<command_line;
    string sourceFileType;
    string problem_name;
    string testcase;
    string version;
    int time_limit;
    int memory_limit;
    int output_limit;
    istringstream is(command_line);
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
    execJudgeCommand(fdSocket, sourceFileType, problem_name, testcase,
                     version, time_limit, memory_limit, output_limit);
    // clear all temporary files.
    system("rm -f *");
}

int terminated = 0;

void sigtermHandler(int sig) {
    terminated = 1;
}

int main(int argc, char* argv[]) {
    if (parseArguments(argc, argv) < 0) {
        return 1;
    }
    sigset_t mask;
    sigemptyset(&mask);
    installSignalHandler(SIGTERM, sigtermHandler, 0, mask);

    // prevents SIGPIPE to terminate the process.
    installSignalHandler(SIGPIPE, SIG_IGN);

    // installs handlers for tracing.
    installHandlers();

    char working_root[MAX_BUFFER_SIZE];
    sprintf(working_root, "working/%d", getpid());
    if (mkdir(working_root, 0777) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create dir "<<working_root;
        return 1;
    }
    if (chdir(working_root) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to change working dir to "<<working_root;
        return 1;
    }
    int fdSocket = socket(AF_INET, SOCK_STREAM, 0);
    struct sockaddr_in servaddr;
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(QUEUE_ADDRESS.second);
    if (inet_pton(AF_INET, QUEUE_ADDRESS.first.c_str(), &servaddr.sin_addr) <= 0) {
        LOG(SYSCALL_ERROR)<<"Invalid address "<<QUEUE_ADDRESS.first;
        return 1;
    }
    if (connect(fdSocket, (const sockaddr*)&servaddr, sizeof(servaddr)) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to connect to "
                          <<QUEUE_ADDRESS.first<<":"<<QUEUE_ADDRESS.second;
    }

    // Loops until SIGTERM is received.
    while (!terminated) {
        process(fdSocket);
    }
    close(fdSocket);
    system(StringPrintf("rm -rf %s", working_root).c_str());
    return 0;
}
