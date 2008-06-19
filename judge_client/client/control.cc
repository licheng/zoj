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
#include "command_reader.h"
#include "compile.h"
#include "logging.h"
#include "util.h"

DEFINE_ARG(string, info, "Information about the host machine which is sent to the judge server");

static uint8_t current_jobs;

void sigchldHandler(int sig) {
    pid_t pid;
    while ((pid = waitpid(-1, NULL, WNOHANG)) > 0 || pid < 0 && errno == EINTR) {
        if (pid > 0) {
            --current_jobs;
        }
    }
}

int JudgeMain(const string& root, const string& queue_address, int queue_port, int uid, int gid);

int ControlMain(const string& root, const string& queue_address, int queue_port, int uid, int gid) {
    if (ChangeToWorkingDir(root, NULL) < 0) {
        return 1;
    }

    InstallSignalHandler(SIGCHLD, sigchldHandler);
    global::socket_closed = true;

    uint8_t max_jobs = 0;
    int sock = -1;
    current_jobs = 0;
    CommandReader reader(true);
    // Loops until SIGTERM is received.
    while (!global::terminated) {
        if (reader.error() || global::socket_closed) {
            if (sock >= 0) {
                close(sock);
            }
            for (int i = 1; ; i *= 2) {
                sock = ConnectTo(queue_address, queue_port);
                if (sock >= 0) {
                    global::socket_closed = false;
                    reader.set_sock(sock);
                    Writen(sock, "C", 1);
                    SendMessage(sock, ARG_info);
                    break;
                } else if (global::terminated) {
                    return 0;
                }
                if (i > 64) {
                    i = 64;
                }
                sleep(i);
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
        int command = reader.ReadUint8();
        if (command < 0) {
            reader.Rewind();
            continue;
        }
        if (command == CMD_CHANGE_MAX_JOBS) {
            int new_max_jobs = reader.ReadUint8();
            if (new_max_jobs < 0) {
                reader.Rewind();
                continue;
            }
            int checksum = reader.ReadUint16();
            if (CheckSum(CMD_CHANGE_MAX_JOBS) + CheckSum(new_max_jobs) != checksum) {
                LOG(ERROR)<<"Invalid checksum: "<<checksum;
                SendReply(sock, INVALID_INPUT);
                global::socket_closed = true;
                continue;
            }
            LOG(INFO)<<"Max jobs: "<<new_max_jobs;
            if (new_max_jobs > MAX_JOBS) {
                LOG(ERROR)<<"too many jobs: "<<new_max_jobs;
                SendReply(sock, INVALID_INPUT);
                global::socket_closed = true;
            } else {
                max_jobs = new_max_jobs;
                SendReply(sock, READY);
            }
            reader.Clear();
        } else {
            LOG(ERROR)<<"Invalid command "<<command;
            SendReply(sock, INVALID_INPUT);
            global::socket_closed = true;
        }
    }
    return 0;
}

