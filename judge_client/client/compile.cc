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

#include <arpa/inet.h>
#include <sys/wait.h>

#include "args.h"
#include "logging.h"
#include "trace.h"
#include "util.h"

const int COMPILATION_TIME_LIMIT = 10;
const int COMPILATION_OUTPUT_LIMIT = 4096;

int DoCompile(int sock, const string& root, int compiler, const string& source_filename) {
    LOG(INFO)<<"Compiling";
    SendReply(sock, COMPILING);
    string command = StringPrintf("%s/script/compile.sh '%s' '%s'",
                                  root.c_str(),
                                  global::COMPILER_LIST[compiler].compiler,
                                  source_filename.c_str());
    LOG(INFO)<<"Command: "<<command;
    int fd_pipe[2];
    if (pipe(fd_pipe) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create pipe";
        SendReply(sock, INTERNAL_ERROR);
        return -1;
    }
    StartupInfo info;
    info.fd_stderr = fd_pipe[1];
    info.time_limit = COMPILATION_TIME_LIMIT;
    info.output_limit = COMPILATION_OUTPUT_LIMIT;
    TraceCallback callback;
    pid_t pid = CreateShellProcess(command.c_str(), info);
    close(fd_pipe[1]);
    if (pid < 0) {
        LOG(INFO)<<"Compilation failed";
        close(fd_pipe[0]);
        SendReply(sock, INTERNAL_ERROR);
        return -1;
    }
    static signed char error_message[4096];
    int count = Readn(fd_pipe[0], error_message, sizeof(error_message));
    close(fd_pipe[0]);
    if (count < 0) {
        LOG(ERROR)<<"Fail to read error messages";
        SendReply(sock, INTERNAL_ERROR);
        return -1;
    }
    int status = 0;
    if (count == sizeof(error_message)) {
        kill(pid, SIGKILL);
    }
    while (waitpid(pid, &status, 0) < 0) {
        if (errno != EINTR) {
            LOG(SYSCALL_ERROR);
            return INTERNAL_ERROR;
        }
    }
    if (count == sizeof(error_message)) {
        status = 1;
    } else {
        if (WIFSIGNALED(status)) {
            LOG(ERROR)<<"Compilation terminated by signal "<<WTERMSIG(status);
            SendReply(sock, INTERNAL_ERROR);
            return -1;
        }
        status = WEXITSTATUS(status);
    }
    if (status) {
        if (status >= 126) {
            LOG(INFO)<<"Running compile.sh failed";
            SendReply(sock, INTERNAL_ERROR);
            return -1;
        } else {
            LOG(INFO)<<"Compilation error";
            SendReply(sock, COMPILATION_ERROR);
            uint32_t len = htonl(count);
            Writen(sock, &len, sizeof(len));
            for (int i = 0; i < count; ++i) {
                if (error_message[i] <= 0) {
                    error_message[i] = '?';
                }
            }
            Writen(sock, error_message, count);
            return 1;
        }
    }
    LOG(INFO)<<"Compilation done";
    return 0;
}
