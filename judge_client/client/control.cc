/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either revision 3 of the License, or
 * (at your option) any later revision.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */
#include <dirent.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include "global.h"
#include "compile.h"
#include "logging.h"
#include "util.h"

static uint8_t current_jobs = 0;

void sigchldHandler(int sig) {
    pid_t pid;
    while ((pid = waitpid(-1, NULL, WNOHANG)) > 0 || pid < 0 && errno == EINTR) {
        if (pid > 0) {
            --current_jobs;
        }
    }
}

int JudgeMain(const string& root, const string& queue_address, int queue_port,
              int uid, int gid);

int ControlMain(const string& root, const string& queue_address,
                int queue_port, int uid, int gid) {
    if (chdir((root + "/working").c_str()) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to change working dir to "<<root<<"/working";
        return 1;
    }

    InstallSignalHandler(SIGCHLD, sigchldHandler);
    global::socket_closed = 1;
    int sock = -1;
    uint8_t max_jobs = 0;

    // Loops until SIGTERM is received.
    while (!global::terminated) {
        if (global::socket_closed) {
            if (sock >= 0) {
                close(sock);
            }
            sock = ConnectTo(queue_address, queue_port);
            if (sock >= 0) {
                global::socket_closed = 0;
            } else {
                sleep(10);
                continue;
            }
        }
        while (current_jobs < max_jobs) {
            pid_t pid = fork();
            if (pid < 0) {
                LOG(SYSCALL_ERROR)<<"Fail to create new job";
                break;
            }
            if (pid == 0) {
                close(sock);
                Log::Close();
                exit(JudgeMain(root, queue_address, queue_port, uid, gid));
            }
            ++current_jobs;
        }
        uint8_t command;
        if (Readn(sock, &command, 1) != -1) {
            if (command == CMD_TYPE) {
                SendReply(sock, TYPE_CONTROL);
            } else if (command == CMD_CHANGE_MAX_JOBS) {
                uint8_t new_max_jobs;
                if (Readn(sock, &new_max_jobs, sizeof(new_max_jobs)) != -1) {
                    if (new_max_jobs > MAX_JOBS) {
                        LOG(ERROR)<<"too many jobs: "<<new_max_jobs;
                        SendReply(sock, INVALID_INPUT);
                    } else {
                        max_jobs = new_max_jobs;
                        SendReply(sock, READY);
                    }
                }
            } else {
                LOG(ERROR)<<"Invalid command "<<command;
                SendReply(sock, INVALID_INPUT);
            }
        }
    }
    return 0;
}

