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

#include "compile.h"

#include <string>

#include <sys/wait.h>

#include "args.h"
#include "logging.h"
#include "trace.h"
#include "util.h"

// The root directory which contains problems, scripts and working directory of
// the client
DECLARE_ARG(string, root);

int doCompile(int fdSocket, const string& sourceFilename) {
    LOG(INFO)<<"Compiling";
    sendReply(fdSocket, COMPILING);
    string command =
        ARG_root + "/script/compile.sh '" + sourceFilename + "'";
    LOG(INFO)<<"Command: "<<command;
    int fdPipe[2];
    if (pipe(fdPipe) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create pipe";
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    StartupInfo info;
    info.fdStderr = fdPipe[1];
    info.timeLimit = 30;
    class Callback: public TraceCallback {
        public:
            // Nothing special should be done when the compiling process
            // terminates.
            virtual void onSIGCHLD(pid_t) { }
    } callback;
    pid_t pid = createShellProcess(command.c_str(), info);
    close(fdPipe[1]);
    if (pid < 0) {
        LOG(INFO)<<"Compilation failed";
        close(fdPipe[0]);
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    static char errorMessage[16384];
    int count = readn(fdPipe[0], errorMessage, sizeof(errorMessage));
    close(fdPipe[0]);
    if (count < 0) {
        LOG(ERROR)<<"Fail to read error messages";
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    int status;
    while (waitpid(pid, &status, 0) < 0) {
        if (errno != EINTR) {
            LOG(SYSCALL_ERROR);
            return INTERNAL_ERROR;
        }
    }
    if (WIFSIGNALED(status)) {
        LOG(ERROR)<<"Compilation terminated by signal "<<WTERMSIG(status);
        sendReply(fdSocket, INTERNAL_ERROR);
        return -1;
    }
    status = WEXITSTATUS(status);
    if (status) {
        if (status >= 126) {
            LOG(INFO)<<"Compilation failed";
            sendReply(fdSocket, INTERNAL_ERROR);
        } else {
            LOG(INFO)<<"Compilation error";
            sendReply(fdSocket, COMPILATION_ERROR);
        }
        return -1;
    }
    return 0;
}
