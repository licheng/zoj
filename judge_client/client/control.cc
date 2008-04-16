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

class LogPipe {
    public:
        LogPipe() {
            pipe_[0] = -1;
            pbuf_ = buf_;
        }

        pid_t GetPID() {
            return pid_;
        }

        void SetPID(pid_t pid) {
            if (pipe_[0] == -1) {
                if (pipe(pipe_) == -1) {
                    LOG(SYSCALL_ERROR)<<"Fail to create pipe";
                }
            }
            pid_ = pid;
            if (pid == -1) {
                pbuf_ = buf_;
            }
        }

        int GetPipeIn() {
            return pipe_[0];
        }

        int GetPipeOut() {
            return pipe_[1];
        }

        string ReadLine() {
            string ret;
            int count = read(pipe_[0], pbuf_, sizeof(buf_) - (pbuf_ - buf_));
            if (count < 0) {
                if (errno != EINTR) {
                    LOG(SYSCALL_ERROR)<<"Fail to read log";
                }
            } else {
                for (char* p = pbuf_; p - pbuf_ < count; ++p) {
                    if (*p == '\n') {
                        ret.assign(buf_, p - buf_);
                        count -= p + 1 - pbuf_;
                        memmove(buf_, p + 1, count);
                        pbuf_ = buf_;
                        break;
                    }
                }
                pbuf_ += count;
            }
            return ret;
        }

    private:
        pid_t pid_;
        int pipe_[2];
        char buf_[1024];
        char* pbuf_;
};

static LogPipe pipes[MAX_JOBS];

static uint8_t current_jobs = 0;

void sigchldHandler(int sig) {
    pid_t pid;
    while ((pid = waitpid(-1, NULL, WNOHANG)) > 0 || pid < 0 && errno == EINTR) {
        if (pid > 0) {
            for (int i = 0; i < MAX_JOBS; ++i) {
                if (pipes[i].GetPID() == pid) {
                    pipes[i].SetPID(-1);
                }
            }
            --current_jobs;
        }
    }
}

int JudgeMain(const string& root, const string& queue_address, int queue_port,
              int uid, int gid);

int ControlMain(const string& root, const string& queue_address,
                int queue_port, int uid, int gid) {
    InstallSignalHandler(SIGCHLD, sigchldHandler);
    global::socket_closed = 1;
    int sock = -1;
    uint8_t max_jobs = 0;

    // Loops until SIGTERM is received.
    while (!global::terminated) {
        if (global::socket_closed) {
            sock = ConnectTo(queue_address, queue_port);
            if (sock >= 0) {
                global::socket_closed = 0;
            } else {
                sleep(10);
                continue;
            }
        }
        while (current_jobs < max_jobs) {
            int k = -1;
            for (int i = 0; i <= current_jobs; ++i) {
                if (pipes[i].GetPID() == -1) {
                    k = i;
                    break;
                }
            }
            if (k < 0) {
                LOG(ERROR)<<"No log pipe available";
                break;
            }
            pid_t pid = fork();
            if (pid < 0) {
                LOG(SYSCALL_ERROR)<<"Fail to create new job";
                break;
            }
            if (pid == 0) {
                close(sock);
                Log::SetLogFile(new PipeLogFile(pipes[k].GetPipeOut()));
                exit(JudgeMain(root, queue_address, queue_port, uid, gid));
            }
            pipes[k].SetPID(pid);
            ++current_jobs;
        }
        int nfds = sock;
        fd_set fdset;
        FD_ZERO(&fdset);
        FD_SET(sock, &fdset);
        for (int i = 0; i < current_jobs; ++i) {
            if (pipes[i].GetPID() >= 0) {
                FD_SET(pipes[i].GetPipeIn(), &fdset);
                if (pipes[i].GetPipeIn() > nfds) {
                    nfds = pipes[i].GetPipeIn();
                }
            }
        }
        int ready = select(nfds + 1, &fdset, NULL, NULL, NULL);
        if (ready == -1) {
            if (errno != EINTR) {
                LOG(SYSCALL_ERROR)<<"Fail to select";
                sleep(10);
            }
            continue;
        }
        for (int i = 0; i < current_jobs; ++i) {
            if (pipes[i].GetPID() >= 0 &&
                FD_ISSET(pipes[i].GetPipeIn(), &fdset)) {
                string message = pipes[i].ReadLine();
                if (!message.empty()) {
                    Log::GetLogFile()->Write(message);
                }
            }
        }
        uint8_t command;
        if (FD_ISSET(sock, &fdset) && Readn(sock, &command, 1) != -1) {
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

