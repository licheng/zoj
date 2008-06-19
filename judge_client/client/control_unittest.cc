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

#include "unittest.h"

#include <errno.h>
#include <sys/wait.h>
#include <unistd.h>

#include "trace.h"
#include "util.h"

DECLARE_ARG(string, info);

int ControlMain(const string& root, const string& queue_address, int queue_port, int uid, int gid);

class ControlMainTest: public TestFixture {
    protected:
        virtual void SetUp() {
            ARG_info = "test";
            root_ = tmpnam(NULL);
            ASSERT_EQUAL(0, mkdir(root_.c_str(), 0755));
            ASSERT_EQUAL(0, chdir(root_.c_str()));
            ASSERT_EQUAL(0, mkdir("working", 0755));
            ASSERT_EQUAL(0, mkdir("script", 0750));
            ASSERT_EQUAL(0, mkdir("prob", 0750));
            ASSERT_EQUAL(0, symlink((TESTDIR + "/../../script/compile.sh").c_str(), "script/compile.sh"));
            server_sock_ = socket(PF_INET, SOCK_STREAM, 6);
            if (server_sock_ == -1) {
                FAIL(strerror(errno));
            }
            int option_value = 1;
            if (setsockopt(server_sock_, SOL_SOCKET, SO_REUSEADDR, &option_value, sizeof(option_value)) == -1) {
                FAIL(strerror(errno));
            }
            sockaddr_in address;
            memset(&address, 0, sizeof(address));
            address.sin_family = AF_INET;
            address.sin_addr.s_addr = htonl(INADDR_ANY);
            address.sin_port = 0;
            if (bind(server_sock_, (struct sockaddr*)&address, sizeof(address)) == -1) {
                FAIL(strerror(errno));
            }
            if (listen(server_sock_, 32) == -1) {
                FAIL(strerror(errno));
            }
            socklen_t len = sizeof(address);
            if (getsockname(server_sock_, (struct sockaddr*)&address, &len) == -1) {
                FAIL(strerror(errno));
            }
            port_ = ntohs(address.sin_port);
            int flags = fcntl(server_sock_, F_GETFL, 0);
            ASSERT(flags >= 0);
            ASSERT_EQUAL(0, fcntl(server_sock_, F_SETFL, flags | O_NONBLOCK));
            client_socks_.clear();
            address_ = "127.0.0.1";
            buf_size_ = 0;
        }

        virtual void TearDown() {
            UninstallHandlers();
            if (pid_ > 0) {
                kill(pid_, SIGKILL);
                waitpid(pid_, NULL, 0);
            }
            if (server_sock_ >= 0) {
                close(server_sock_);
            }
            for (int i = 0; i < client_socks_.size(); ++i) {
                if (client_socks_[i] >= 0) {
                    close(client_socks_[i]);
                }
            }
            system(("rm -rf " + root_).c_str());
        }

        void RunControlMain(bool terminate) {
            pid_ = fork();
            if (pid_ < 0) {
                FAIL("Fail to fork");
            }
            if (pid_ == 0) {
                close(server_sock_);
                global::terminated = terminate;
                exit(ControlMain(root_, address_, port_, 0, 0));
            }
        }

        int WaitForConnection(bool control) {
            sockaddr_in address;
            size_t len = sizeof(address);
            client_socks_.push_back(-1);
            for (int i = 0; i < 3; ++i) {
                client_socks_.back() = accept(server_sock_, (struct sockaddr*)&address, &len);
                if (client_socks_.back() >= 0) {
                    break;
                }
                if (errno != EWOULDBLOCK) {
                    FAIL(strerror(errno));
                }
                usleep(500);
            }
            if (client_socks_.back() < 0) {
                return -1;
            }
            struct timeval tv;
            tv.tv_sec = 5;
            tv.tv_usec = 0;
            ASSERT_EQUAL(0, setsockopt(client_socks_.back(), SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)));
            ASSERT_EQUAL(0, setsockopt(client_socks_.back(), SOL_SOCKET, SO_SNDTIMEO, &tv, sizeof(tv)));
            if (control && client_socks_.size() > 1) {
                client_socks_[0] = client_socks_.back();
                client_socks_.pop_back();
            }
            return 0;
        }

        int WaitForTermination() {
            int status;
            ASSERT_EQUAL(pid_, waitpid(pid_, &status, 0));
            pid_ = -1;
            ASSERT(WIFEXITED(status));
            return WEXITSTATUS(status);
        }

        void AppendCheckSum() {
            uint16_t checksum = 0;
            for (int i = 0; i < buf_size_; ++i) {
                checksum += (unsigned char)buf_[i];
            }
            checksum = htons(checksum);
            *(uint16_t*)(buf_ + buf_size_) = checksum;
            buf_size_ += sizeof(uint16_t);
        }

        void AppendUint8(uint8_t value) {
            *(uint8_t*)(buf_ + buf_size_) = value;
            buf_size_ += sizeof(uint8_t);
        }

        void AppendUint16(uint16_t value) {
            value = htons(value);
            *(uint16_t*)(buf_ + buf_size_) = value;
            buf_size_ += sizeof(uint16_t);
        }

        void SendCommand() {
            Writen(client_socks_[0], buf_, buf_size_);
        }

        void SendChangeMaxJobsCommand(int max_jobs, int checksum=-1) {
            buf_size_ = 0;
            AppendUint8(CMD_CHANGE_MAX_JOBS);
            AppendUint8(max_jobs);
            if (checksum < 0) {
                AppendCheckSum();
            } else {
                AppendUint16(checksum);
            }
            SendCommand();
        }

        int server_sock_;
        vector<int> client_socks_;
        int port_;
        pid_t pid_;
        string address_;
        string root_;
        char buf_[1024 * 16];
        int buf_size_;
};

TEST_F(ControlMainTest, InvalidAddress) {
    address_ = "invalid";
    RunControlMain(false);
    ASSERT_EQUAL(-1, WaitForConnection(true));
}

TEST_F(ControlMainTest, CannotCreateWorkingDir) {
    ASSERT_EQUAL(0, rmdir("working"));
    RunControlMain(false);
    ASSERT_EQUAL(-1, WaitForConnection(true));
    ASSERT_EQUAL(1, WaitForTermination());
}

TEST_F(ControlMainTest, SIGTERM) {
    RunControlMain(true);
    ASSERT_EQUAL(-1, WaitForConnection(true));
    ASSERT_EQUAL(0, WaitForTermination());
}

TEST_F(ControlMainTest, ReadCommandFailure) {
    RunControlMain(false);
    ASSERT_EQUAL(0, WaitForConnection(true));
    ASSERT_EQUAL(0, ReadUint8(client_socks_[0], (uint8_t*)buf_));
    ASSERT_EQUAL('C', buf_[0]);
    ASSERT_EQUAL(0, ReadUint32(client_socks_[0], (uint32_t*)buf_));
    ASSERT_EQUAL((uint32_t)ARG_info.size(), *(uint32_t*)buf_);
    ASSERT_EQUAL((int)ARG_info.size(), Readn(client_socks_[0], buf_, ARG_info.size()));
    ASSERT_EQUAL(ARG_info, string(buf_));
    ASSERT_EQUAL(0, shutdown(client_socks_[0], SHUT_WR));
    ASSERT_EQUAL(0, Readn(client_socks_[0], buf_, 1));
    ASSERT_EQUAL(0, WaitForConnection(true));
}

TEST_F(ControlMainTest, InvalidCommand) {
    RunControlMain(false);
    ASSERT_EQUAL(0, WaitForConnection(true));
    ASSERT_EQUAL(0, ReadUint8(client_socks_[0], (uint8_t*)buf_));
    ASSERT_EQUAL('C', buf_[0]);
    ASSERT_EQUAL(0, ReadUint32(client_socks_[0], (uint32_t*)buf_));
    ASSERT_EQUAL((uint32_t)ARG_info.size(), *(uint32_t*)buf_);
    ASSERT_EQUAL((int)ARG_info.size(), Readn(client_socks_[0], buf_, ARG_info.size()));
    ASSERT_EQUAL(ARG_info, string(buf_));
    buf_[0] = 0;
    Writen(client_socks_[0], buf_, 1);
    ASSERT_EQUAL(1, Readn(client_socks_[0], buf_, 2));
    ASSERT_EQUAL(INVALID_INPUT, (int)buf_[0]);
    ASSERT_EQUAL(0, WaitForConnection(true));
}

TEST_F(ControlMainTest, ChangeMaxJobsInvalidCheckSum) {
    RunControlMain(false);
    ASSERT_EQUAL(0, WaitForConnection(true));
    ASSERT_EQUAL(0, ReadUint8(client_socks_[0], (uint8_t*)buf_));
    ASSERT_EQUAL('C', buf_[0]);
    ASSERT_EQUAL(0, ReadUint32(client_socks_[0], (uint32_t*)buf_));
    ASSERT_EQUAL((uint32_t)ARG_info.size(), *(uint32_t*)buf_);
    ASSERT_EQUAL((int)ARG_info.size(), Readn(client_socks_[0], buf_, ARG_info.size()));
    ASSERT_EQUAL(ARG_info, string(buf_));
    SendChangeMaxJobsCommand(1, 0);
    ASSERT_EQUAL(1, Readn(client_socks_[0], buf_, 2));
    ASSERT_EQUAL(INVALID_INPUT, (int)buf_[0]);
    ASSERT_EQUAL(0, WaitForConnection(true));
}

TEST_F(ControlMainTest, ChangeMaxJobsMaxJobsPlusOne) {
    RunControlMain(false);
    ASSERT_EQUAL(0, WaitForConnection(true));
    ASSERT_EQUAL(0, ReadUint8(client_socks_[0], (uint8_t*)buf_));
    ASSERT_EQUAL('C', buf_[0]);
    ASSERT_EQUAL(0, ReadUint32(client_socks_[0], (uint32_t*)buf_));
    ASSERT_EQUAL((uint32_t)ARG_info.size(), *(uint32_t*)buf_);
    ASSERT_EQUAL((int)ARG_info.size(), Readn(client_socks_[0], buf_, ARG_info.size()));
    ASSERT_EQUAL(ARG_info, string(buf_));
    SendChangeMaxJobsCommand(MAX_JOBS + 1);
    ASSERT_EQUAL(1, Readn(client_socks_[0], buf_, 2));
    ASSERT_EQUAL(INVALID_INPUT, (int)buf_[0]);
    ASSERT_EQUAL(0, WaitForConnection(true));
}

TEST_F(ControlMainTest, ChangeMaxJobsSuccess) {
    RunControlMain(false);
    ASSERT_EQUAL(0, WaitForConnection(true));
    ASSERT_EQUAL(0, ReadUint8(client_socks_[0], (uint8_t*)buf_));
    ASSERT_EQUAL('C', buf_[0]);
    ASSERT_EQUAL(0, ReadUint32(client_socks_[0], (uint32_t*)buf_));
    ASSERT_EQUAL((uint32_t)ARG_info.size(), *(uint32_t*)buf_);
    ASSERT_EQUAL((int)ARG_info.size(), Readn(client_socks_[0], buf_, ARG_info.size()));
    ASSERT_EQUAL(ARG_info, string(buf_));
    SendChangeMaxJobsCommand(2);
    ASSERT_EQUAL(0, WaitForConnection(false));
    ASSERT_EQUAL(0, WaitForConnection(false));
    SendChangeMaxJobsCommand(1);
    ASSERT_EQUAL(-1, WaitForConnection(false));
    SendChangeMaxJobsCommand(3);
    ASSERT_EQUAL(0, WaitForConnection(false));
    SendChangeMaxJobsCommand(5);
    ASSERT_EQUAL(0, WaitForConnection(false));
    ASSERT_EQUAL(0, WaitForConnection(false));
    ASSERT_EQUAL(-1, WaitForConnection(false));
}
