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

#include "log_server.h"

#include <vector>

#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>

#include "global.h"
#include "logging.h"
#include "util.h"

class LogClient {
    public:
        LogClient(int sock) : sock_(sock), pbuf_(buf_) { }
        ~LogClient() {
            close(sock_);
        }

        int sock() { return sock_; }

        int ReadLine(string* line) {
            int ret = 0;
            int count = read(sock_, pbuf_, sizeof(buf_) - (pbuf_ - buf_));
            if (count < 0) {
                if (errno != EINTR) {
                    LOG(SYSCALL_ERROR)<<"Fail to read log";
                }
            } else {
                for (char* p = pbuf_; p - pbuf_ < count; ++p) {
                    if (*p == '\n') {
                        line->assign(buf_, p - buf_);
                        count -= p + 1 - pbuf_;
                        memmove(buf_, p + 1, count);
                        pbuf_ = buf_;
                        ret = 1;
                        break;
                    }
                }
                pbuf_ += count;
            }
            return ret;
        }

    private:
        int sock_;
        char buf_[1024];
        char* pbuf_;
};

LogServer* LogServer::Create(const string& root) {
    int server_sock = socket(AF_UNIX, SOCK_STREAM, 0);
    if (server_sock < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create socket";
        return NULL;
    }
    struct sockaddr_un un;
    memset(&un, 0, sizeof(un));
    un.sun_family = AF_UNIX; 
    string sock_name = root + "/log.sock";
    unlink(sock_name.c_str());
    strcpy(un.sun_path, sock_name.c_str());
    if (bind(server_sock, (struct sockaddr*)&un, (int)&((struct sockaddr_un*)0)->sun_path + sock_name.size()) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to bind";
        return NULL;
    }
    if (listen(server_sock, MAX_JOBS + 2) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to listen";
        return NULL;
    }
    return new LogServer(root, server_sock);
}

LogServer::~LogServer() {
    close(server_sock_);
    for (int i = 0; i < clients_.size(); ++i) {
        delete clients_[i];
    }
}

void LogServer::Start() {
    Log::SetLogFile(new DiskLogFile(root_ + "/log" + GetLocalTimeAsString("%Y-%m-%d-%H-%M-%S") + ".log"));
    while (!global::terminated) {
        int nfds = server_sock_;
        fd_set fdset;
        FD_ZERO(&fdset);
        FD_SET(server_sock_, &fdset);
        for (int i = 0; i < clients_.size(); ++i) {
            FD_SET(clients_[i]->sock(), &fdset);
            if (clients_[i]->sock() > nfds) {
                nfds = clients_[i]->sock();
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
        if (FD_ISSET(server_sock_, &fdset)) {
            socklen_t len;
            struct sockaddr_un un;
            int client_sock = accept(server_sock_, (struct sockaddr*)&un, &len);
            if (client_sock < 0) {
                LOG(SYSCALL_ERROR)<<"Fail to accept";
                continue;
            }
            clients_.push_back(new LogClient(client_sock));
        } else {
            for (int i = 0; i < clients_.size(); ++i) {
                string line;
                if (FD_ISSET(clients_[i]->sock(), &fdset) && clients_[i]->ReadLine(&line)) {
                    LOG(RAW)<<line;
                }
            }
        }
    }
}
