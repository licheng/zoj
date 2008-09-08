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

#include <stdio.h>

#include <arpa/inet.h>
#include <fcntl.h>

#include "args.h"
#include "compile.h"
#include "global.h"
#include "util.h"

class DoCompileTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, mkdir("script", 0700));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/../../script/compile.sh").c_str(), "script/compile.sh"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.cc").c_str(), "ac.cc"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/ce.cc").c_str(), "ce.cc"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/ce_long_error.cc").c_str(), "ce_long_error.cc"));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
    }

    virtual void TearDown() {
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        system(("rm -rf " + root_).c_str());
    }

    int fd_[2];
    char buf_[1024 * 16];
    string root_;
};

TEST_F(DoCompileTest, Success) {
    ASSERT_EQUAL(0, DoCompile(fd_[1], root_, COMPILER_GPP, "ac.cc"));
    shutdown(fd_[1], SHUT_WR);
    uint32_t t;
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILING, (int)t);
    ASSERT_EQUAL(0, read(fd_[0], buf_, 1));
}

TEST_F(DoCompileTest, Failure) {
    ASSERT_EQUAL(1, DoCompile(fd_[1], root_, COMPILER_GPP, "ce.cc"));
    shutdown(fd_[1], SHUT_WR);
    uint32_t t;
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILING, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILATION_ERROR, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT((int)t);
    ASSERT_EQUAL((ssize_t)t, read(fd_[0], buf_, t + 1));
}

TEST_F(DoCompileTest, TooLongErrorMessage) {
    ASSERT_EQUAL(1, DoCompile(fd_[1], root_, COMPILER_GPP, "ce_long_error.cc"));
    shutdown(fd_[1], SHUT_WR);
    uint32_t t;
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILING, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILATION_ERROR, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(4096, (int)t);
    ASSERT_EQUAL((ssize_t)t, read(fd_[0], buf_, t + 1));
}

// TODO add a unittest for invalid chars
