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

class Reader {
    public:
        Reader() {
            set_sock(-1);
        }

        int ReadUint8() {
            if (ReadTo(pbuf_ - buf_ + sizeof(uint8_t)) < 0) {
                return -1;
            }
            int ret = *(uint8_t*)pbuf_;
            pbuf_ += sizeof(uint8_t);
            return ret;
        }

        int ReadUint16() {
            if (ReadTo(pbuf_ - buf_ + sizeof(uint16_t)) < 0) {
                return -1;
            }
            int ret = ntohs(*(uint16_t*)pbuf_);
            pbuf_ += sizeof(uint16_t);
            return ret;
        }

        void Rewind() {
            pbuf_ = buf_;
        }

        void Clear() {
            buf_size_ = 0;
            Rewind();
        }

        bool Eof() {
            return eof_;
        }

        void set_sock(int sock) {
            sock_ = sock;
            if (sock < 0) {
                eof_ = true;
            } else {
                eof_ = false;
            }
            Clear();
        }

    private:
        int ReadTo(int offset) {
            if (sock_ < 0) {
                return -1;
            }
            if (offset > buf_size_) {
                int count = read(sock_, buf_ + buf_size_, offset - buf_size_);
                if (count < 0) {
                    if (errno != EINTR) {
                        LOG(SYSCALL_ERROR)<<"Fail to read";
                        eof_ = true;
                    }
                    return -1;
                }
                if (count == 0) {
                    LOG(ERROR)<<"EOF";
                    eof_ = true;
                    return -1;
                }
                buf_size_ += count;
                if (offset > buf_size_) {
                    return -1;
                }
            }
            return 0;
        }

        int sock_;
        char buf_[32];
        int buf_size_;
        char* pbuf_;
        bool eof_;
};

int ControlMain(const string& root, const string& queue_address, int queue_port, int uid, int gid) {
    if (ChangeToWorkingDir(root, NULL) < 0) {
        return 1;
    }

    InstallSignalHandler(SIGCHLD, sigchldHandler);
    global::socket_closed = true;

    uint8_t max_jobs = 0;
    int sock = -1;
    current_jobs = 0;
    Reader reader;
    // Loops until SIGTERM is received.
    while (!global::terminated) {
        if (reader.Eof() || global::socket_closed) {
            if (sock >= 0) {
                close(sock);
            }
            for (int i = 1; ; i *= 2) {
                sock = ConnectTo(queue_address, queue_port);
                if (sock >= 0) {
                    global::socket_closed = false;
                    reader.set_sock(sock);
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

