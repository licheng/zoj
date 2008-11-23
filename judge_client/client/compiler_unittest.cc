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
#include "compiler.h"

#include <sys/socket.h>
#include <sys/stat.h>

#include "common_io.h"
#include "environment.h"
#include "protocol.h"

class CompilerTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, mkdir("script", 0700));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/../../script/compile.sh").c_str(), "script/compile.sh"));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        Environment::instance()->set_root(root_);
        compiler_id_ = 2;
        compiler_name_ = "g++";
        source_file_extension_ = "cc";
        source_filename_ = "p.cc";
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

    int Run() {
        Compiler compiler(compiler_id_, compiler_name_, source_file_extension_);
        int ret = compiler.Compile(fd_[1], source_filename_);
        ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
        return ret;
    }

    int fd_[2];
    char buf_[1024 * 16];
    string root_;
    int compiler_id_;
    string compiler_name_;
    string source_file_extension_;
    string source_filename_;
    Compiler* compiler_;
};

TEST_F(CompilerTest, Success) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.cc").c_str(), "p.cc"));

    ASSERT_EQUAL(0, Run());

    uint32_t t;
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILING, (int)t);
    ASSERT_EQUAL(0, read(fd_[0], buf_, 1));
}

TEST_F(CompilerTest, Failure) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ce.cc").c_str(), "p.cc"));

    ASSERT_EQUAL(1, Run());

    uint32_t t;
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILING, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILATION_ERROR, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT((int)t);
    ASSERT_EQUAL((ssize_t)t, read(fd_[0], buf_, t + 1));
}

TEST_F(CompilerTest, TooLongErrorMessage) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ce_long_error.cc").c_str(), "p.cc"));

    ASSERT_EQUAL(1, Run());

    uint32_t t;
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILING, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILATION_ERROR, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(4096, (int)t);
    ASSERT_EQUAL((ssize_t)t, read(fd_[0], buf_, t + 1));
}

TEST_F(CompilerTest, HugeOutput) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ce_huge_output.c").c_str(), "p.cc"));
    compiler_id_ = 1;
    compiler_name_ = "gcc";
    source_file_extension_ = "c";
    source_filename_ = "p.c";

    ASSERT_EQUAL(1, Run());

    uint32_t t;
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILING, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILATION_ERROR, (int)t);
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT((int)t);
    ASSERT_EQUAL((ssize_t)t, read(fd_[0], buf_, t + 1));
}

TEST_F(CompilerTest, CCompilationWithLibMath) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/math.c").c_str(), "p.c"));
    compiler_id_ = 1;
    compiler_name_ = "gcc";
    source_file_extension_ = "c";
    source_filename_ = "p.c";

    ASSERT_EQUAL(0, Run());

    uint32_t t;
    ASSERT_EQUAL(0, ReadUint32(fd_[0], &t));
    ASSERT_EQUAL(COMPILING, (int)t);
    ASSERT_EQUAL(0, read(fd_[0], buf_, 1));
}

// TODO add a unittest for invalid chars
