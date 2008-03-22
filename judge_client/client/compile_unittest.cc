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
#include "judge_result.h"

DEFINE_ARG(string, root, "");

class DoCompileTest: public TestFixture {
  protected:
    void setUp() {
        ARG_root = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(ARG_root.c_str(), 0700));
        ASSERT_EQUAL(0, mkdir((ARG_root + "/script").c_str(), 0700));
        ASSERT_EQUAL(0, link("../script/compile.sh", 
                             (ARG_root + "/script/compile.sh").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/ac.cc", (ARG_root + "/ac.cc").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/ce.cc", (ARG_root + "/ce.cc").c_str()));
        fp_ = tmpfile();
        fd_ = fileno(fp_);
    }

    void tearDown() {
        fclose(fp_);
        system(("rm -rf " + ARG_root).c_str());
    }

    FILE* fp_;
    int fd_;
    char buf_[1024 * 16];
};

TEST_F(DoCompileTest, Success) {
    ASSERT_EQUAL(0, doCompile(fd_, ARG_root +  "/ac.cc"));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(COMPILING, (int)buf_[0]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCompileTest, Failure) {
    ASSERT_EQUAL(-1, doCompile(fd_, ARG_root + "/ce.cc"));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)4, read(fd_, buf_, 4));
    ASSERT_EQUAL(COMPILING, (int)buf_[0]);
    ASSERT_EQUAL(COMPILATION_ERROR, (int)buf_[1]);
    int len = ntohs(*(uint16_t*)(buf_ + 2));
    ASSERT(len);
    ASSERT_EQUAL((ssize_t)len, read(fd_, buf_, len));
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

// TODO add a unittest for invalid chars
