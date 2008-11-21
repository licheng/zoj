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

#include "environment.h"
#include "trace.h"
#include "test_util-inl.h"

DECLARE_ARG(int, max_heart_beat_interval);
DEFINE_OPTIONAL_ARG(string, compiler, "g++,gcc,fpc", "");

int ControlMain(const string& queue_address, int queue_port, int port);

class ControlMainTest: public TestFixture {
    protected:
        virtual void SetUp() {
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
            global::terminated = false;
            ARG_max_heart_beat_interval = 100;
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
                close(client_socks_[i]);
            }
            system(("rm -rf " + root_).c_str());
        }

        void RunControlMain() {
            pid_ = fork();
            if (pid_ < 0) {
                FAIL("Fail to fork");
            }
            if (pid_ == 0) {
                close(server_sock_);
                Environment::instance()->set_root(root_);
                exit(ControlMain(address_, port_, port_));
            }
        }

        int WaitForConnection() {
            sockaddr_in address;
            size_t len = sizeof(address);
            int sock = -1;
            for (int i = 0; i < 3; ++i) {
                sock = accept(server_sock_, (struct sockaddr*)&address, &len);
                if (sock >= 0) {
                    break;
                }
                if (errno != EWOULDBLOCK) {
                    FAIL(strerror(errno));
                }
                usleep(50000);
            }
            if (sock < 0) {
                return -1;
            }
            struct timeval tv;
            tv.tv_sec = 5;
            tv.tv_usec = 0;
            ASSERT_EQUAL(0, setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)));
            ASSERT_EQUAL(0, setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &tv, sizeof(tv)));
            client_socks_.push_back(sock);
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
            uint32_t checksum = 0;
            for (int i = 0; i < buf_size_; ++i) {
                checksum += (unsigned char)buf_[i];
            }
            checksum = htonl(checksum);
            *(uint32_t*)(buf_ + buf_size_) = checksum;
            buf_size_ += sizeof(uint32_t);
        }

        void AppendUint32(uint32_t value) {
            value = htonl(value);
            *(uint32_t*)(buf_ + buf_size_) = value;
            buf_size_ += sizeof(uint32_t);
        }

        void SendCommand() {
            Writen(client_socks_.back(), buf_, buf_size_);
        }

        void SendPingCommand() {
            buf_size_ = 0;
            AppendUint32(CMD_PING);
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

    RunControlMain();

    ASSERT_EQUAL(-1, WaitForConnection());
}

TEST_F(ControlMainTest, CannotCreateWorkingDir) {
    ASSERT_EQUAL(0, rmdir("working"));

    RunControlMain();

    ASSERT_EQUAL(-1, WaitForConnection());
    ASSERT_EQUAL(1, WaitForTermination());
}

TEST_F(ControlMainTest, SIGTERM) {
    global::terminated = true;
    RunControlMain();

    ASSERT_EQUAL(-1, WaitForConnection());
    ASSERT_EQUAL(0, WaitForTermination());
}

TEST_F(ControlMainTest, ReadCommandFailure) {
    RunControlMain();

    ASSERT_EQUAL(0, WaitForConnection());
    ASSERT_EQUAL(0, shutdown(client_socks_.back(), SHUT_WR));
    ASSERT_EQUAL(0, WaitForConnection());
}

TEST_F(ControlMainTest, InvalidCommand) {
    RunControlMain();

    ASSERT_EQUAL(0, WaitForConnection());
    AppendUint32(0);
    SendCommand();
    ASSERT_EQUAL(INVALID_INPUT, ReadUint32(client_socks_.back()));
    ASSERT_EQUAL(0, WaitForConnection());
}

TEST_F(ControlMainTest, HeartBeatFailure) {
    RunControlMain();

    ASSERT_EQUAL(0, WaitForConnection());
    usleep(500000);
    ASSERT_EQUAL(0, WaitForConnection());
}

TEST_F(ControlMainTest, PingSuccess) {
    RunControlMain();

    ASSERT_EQUAL(0, WaitForConnection());
    AppendUint32(CMD_PING);    
    SendCommand();
    ASSERT_EQUAL(READY, ReadUint32(client_socks_.back()));
}

TEST_F(ControlMainTest, InfoSuccess) {
    RunControlMain();

    ASSERT_EQUAL(0, WaitForConnection());
    AppendUint32(CMD_INFO);    
    SendCommand();
    ASSERT_EQUAL(port_, ReadUint32(client_socks_.back()));
    ASSERT_EQUAL(3, ReadUint32(client_socks_.back()));
    ASSERT_EQUAL(global::COMPILER_LIST[COMPILER_GCC].id, ReadUint32(client_socks_.back()));
    ASSERT_EQUAL(global::COMPILER_LIST[COMPILER_GPP].id, ReadUint32(client_socks_.back()));
    ASSERT_EQUAL(global::COMPILER_LIST[COMPILER_FREE_PASCAL].id, ReadUint32(client_socks_.back()));
}
