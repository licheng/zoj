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
#include "trace.h"

DECLARE_ARG(string, root);

int JavaRunner::Run(int sock, int time_limit, int memory_limit, int output_limit, int uid, int gid) {
    TraceCallback callback;
    LOG(INFO)<<"Running";
    int port;
    int server_sock = CreateServerSocket(&port);
    if (server_sock < 0) {
        return -1;
    }
    pid_t pid = fork();
    if (pid < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to fork";
        return -1;
    }
    if (pid > 0) {
        int status;
        for (;;) {
            struct sockaddr_un un;
            socklen_t len = sizeof(un);
            int client_sock = accept(server_sock, (struct sockaddr*)&un, &len);
            if (client_sock < 0) {
                if (errno != EINTR) {
                    LOG(SYSCALL_ERROR)<<"Fail to accept";
                    close(server_sock);
                    WriteUint32(sock, INTERNAL_ERROR);
                    return -1;
                } else if (callback.HasExited()) {
                    break;
                }
            } else {
                uint32_t time_consumption;
                uint32_t memory_consumption;
                while (ReadUint32(client_sock, &time_consumption) >= 0 &&
                       ReadUint32(client_sock, &memory_consumption) >= 0) {
                    SendRunningMessage(sock, time_consumption, memory_consumption);
                }
                break;
            }
        }
        while (waitpid(pid, &status, 0) < 0) {
            if (errno != EINTR) {
                LOG(SYSCALL_ERROR)<<"Fail to waitpid";
                kill(pid, SIGKILL);
                WriteUint32(sock, INTERNAL_ERROR);
                return -1;
            }
        }
        if (WIFSIGNALED(status)) {
            LOG(ERROR)<<"Terminated by signal "<<WTERMSIG(status);
            return -1;
        } else {
            status = WEXITSTATUS(status);
            switch (status) {
              case 0:
                return 0;
              case TIME_LIMIT_EXCEEDED:
              case OUTPUT_LIMIT_EXCEEDED:
              case MEMORY_LIMIT_EXCEEDED:
              case RUNTIME_ERROR:
                WriteUint32(sock, status);
                return 1;
              default:
                if (status != INTERNAL_ERROR) {
                    LOG(ERROR)<<"Invalid exit status "<<status;
                }
                WriteUint32(sock, INTERNAL_ERROR);
                return -1;
            }
        }
    } else {
        close(sock);
        close(server_sock);
        Log::Close();
        execlp("java",
               StringPrintf("-Xmx%dk", memory_limit + 150).c_str(),
               StringPrintf("-Djava.library.path=%s", ARG_root.c_str()).c_str(),
               "-Djava.class.path=",
               "-jar", "JavaSandbox.jar",
               StringPrintf("%d", port).c_str(),
               StringPrintf("%d", time_limit).c_str(),
               StringPrintf("%d", memory_limit).c_str(),
               StringPrintf("%d", output_limit).c_str(),
               StringPrintf("%d", uid).c_str(),
               StringPrintf("%d", gid).c_str(),
               NULL);
        LOG(SYSCALL_ERROR)<<"Fail to run java";
        return -1;
    }
}
