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

#include "java_runner.h"

#include <stdlib.h>
#include <string>

using namespace std;

#include <errno.h>
#include <sys/un.h>
#include <sys/wait.h>
#include <unistd.h>

#include "args.h"
#include "common_io.h"
#include "logging.h"
#include "net_util.h"
#include "protocol.h"
#include "strutil.h"

DECLARE_ARG(string, root);

namespace {
void sigchld_handler(int) {
}

int HandleSIGCHLD() {
    struct sigaction act;
    act.sa_handler = sigchld_handler;
    sigemptyset(&act.sa_mask);
    act.sa_flags = 0;
    sigaction(SIGCHLD, &act, NULL);
    return 0;
}

}

int __to_install_sigchld_handler = HandleSIGCHLD();

void JavaRunner::InternalRun() {
    LOG(INFO)<<"Running";
    int port;
    int server_sock = CreateServerSocket(&port);
    if (server_sock < 0) {
        result_ = INTERNAL_ERROR;
        return;
    }
    pid_ = fork();
    if (pid_ < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to fork";
        result_ = INTERNAL_ERROR;
        return;
    }
    if (pid_ > 0) {
        int status;
        for (;;) {
            struct sockaddr_un un;
            socklen_t len = sizeof(un);
            int client_sock = accept(server_sock, (struct sockaddr*)&un, &len);
            if (client_sock < 0) {
                if (errno != EINTR) {
                    LOG(SYSCALL_ERROR)<<"Fail to accept";
                    close(server_sock);
                    result_ = INTERNAL_ERROR;
                    return;
                } else if (waitpid(pid_, &status, WNOHANG) > 0) {
                    break;
                }
            } else {
                uint32_t ts, ms;
                while (ReadUint32(client_sock, &ts) >= 0 &&
                       ReadUint32(client_sock, &ms) >= 0) {
                    DLOG<<ts<<' '<<ms;
                    time_consumption_ = ts;
                    memory_consumption_ = ms;
                    SendRunningMessage();
                }
                close(client_sock);
                while (waitpid(pid_, &status, 0) < 0 && errno != ECHILD) {
                }
                break;
            }
        }
        close(server_sock);
        if (WIFSIGNALED(status)) {
            LOG(ERROR)<<"Terminated by signal "<<WTERMSIG(status);
            result_ = INTERNAL_ERROR;
            return;
        } else {
            result_ = WEXITSTATUS(status);
            switch (result_) {
              case 0:
              case TIME_LIMIT_EXCEEDED:
              case OUTPUT_LIMIT_EXCEEDED:
              case MEMORY_LIMIT_EXCEEDED:
              case RUNTIME_ERROR:
                return;
              default:
                if (status != INTERNAL_ERROR) {
                    LOG(ERROR)<<"Invalid exit status "<<status;
                }
                result_ = INTERNAL_ERROR;
                return;
            }
        }
    } else {
        close(sock_);
        close(server_sock);
        Log::Close();
        execlp("java",
               StringPrintf("-Xms%dk", memory_limit_ / 2 + 190).c_str(),
               StringPrintf("-Xmx%dk", memory_limit_ + 190).c_str(),
               StringPrintf("-Djava.library.path=%s", ARG_root.c_str()).c_str(),
               "-Djava.class.path=",
               "-jar", StringPrintf("%s/JavaSandbox.jar", ARG_root.c_str()).c_str(),
               StringPrintf("%d", port).c_str(),
               StringPrintf("%d", time_limit_).c_str(),
               StringPrintf("%d", memory_limit_).c_str(),
               StringPrintf("%d", output_limit_).c_str(),
               StringPrintf("%d", uid_).c_str(),
               StringPrintf("%d", gid_).c_str(),
               NULL);
        LOG(SYSCALL_ERROR)<<"Fail to run java";
        exit(-1);
        return;
    }
}
