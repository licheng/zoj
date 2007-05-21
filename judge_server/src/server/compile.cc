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

#include "compile.h"

#include <string>

#include <sys/wait.h>
#include <unistd.h>

#include "logging.h"
#include "params.h"
#include "trace.h"
#include "util.h"

namespace {

class Callback: public TraceCallback {
    public:
        virtual void onSIGCHLD(pid_t) { }
};

}

int compile(const std::string& sourceFilename, char buffer[], int* bufferSize) {
    int fdPipe[2];
    if (pipe(fdPipe) < 0) {
        LOG(SYSCALL_ERROR);
        return -1;
    }
    std::string command =
        JUDGE_ROOT + "/script/compile.sh '" + sourceFilename + "'";
    StartupInfo info;
    info.fdStderr = fdPipe[1];
    info.timeLimit = 30;
    Callback callback;
    pid_t pid = createShellProcess(command.c_str(), info);
    callback.setPid(pid);
    close(fdPipe[1]);
    if (pid < 0) {
        close(fdPipe[0]);
        return -1;
    }
    *bufferSize = readn(fdPipe[0], buffer, *bufferSize);
    close(fdPipe[0]);
    if (bufferSize < 0) {
        return -1;
    }
    int status;
    waitpid(pid, &status, 0);
    if (WIFSIGNALED(status)) {
        LOG(ERROR)<<"Compilation terminated by signal "<<WTERMSIG(status);
        return -1;
    }
    if (WEXITSTATUS(status)) {
        return 1;
    }
    return 0;
}

static char buffer[16385];

int doCompile(int fdSocket, const std::string& sourceFilename) {
    sendReply(fdSocket, COMPILING);
    int bufferSize;
    int result = compile(sourceFilename, buffer, &bufferSize);
    if (result) {
        if (result == -1) {
            sendReply(fdSocket, SERVER_ERROR);
            return -1;
        } else {
            sendReply(fdSocket, COMPILATION_ERROR);
            writen(fdSocket, buffer, bufferSize);
            return -1;
        }
    }
    return 0;
}
