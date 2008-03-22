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

#include <fcntl.h>
#include <unistd.h>

#include "args.h"
#include "judge_result.h"
#include "run.h"
#include "trace.h"
#include "util.h"

DEFINE_ARG(string, root, "");
DEFINE_ARG(int, uid, "");
DEFINE_ARG(int, gid, "");
DECLARE_ARG(bool, logtostderr);

class DoRunTest: public TestFixture {
  protected:
    void setUp() {
        ARG_logtostderr = true;
        ARG_root = tmpnam(NULL);
        fn_ = ARG_root + "/output";
        ASSERT_EQUAL(0, mkdir(ARG_root.c_str(), 0700));
        ASSERT_EQUAL(0, link(TESTDIR "/1.in", (ARG_root + "/1.in").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/1.out", (ARG_root + "/1.out").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/ac", (ARG_root + "/ac").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/wa", (ARG_root + "/wa").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/tle", (ARG_root + "/tle").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/ole", (ARG_root + "/ole").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/mle", (ARG_root + "/mle").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/fpe", (ARG_root + "/fpe").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/rf", (ARG_root + "/rf").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/pe", (ARG_root + "/pe").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/sigsegv",
                             (ARG_root + "/sigsegv").c_str()));
        fp_ = tmpfile();
        fd_ = fileno(fp_);
        installHandlers();
    }
    
    void tearDown() {
        uninstallHandlers();
        fclose(fp_);
        system(("rm -rf " + ARG_root).c_str());
    }

    FILE* fp_;
    int fd_;
    string fn_;
    char buf_[32];
};

TEST_F(DoRunTest, Success) {
    ASSERT_EQUAL(0, doRun(fd_, TESTDIR "/ac", "cc", TESTDIR "/1.in", fn_,
                          10, 1000, 1000));
    ASSERT(!system(StringPrintf("diff %s %s", TESTDIR "/1.out",
                                fn_.c_str()).c_str()));
    off_t size = lseek(fd_, 0, SEEK_END);
    ASSERT_EQUAL(0, (int)size % 9);
    lseek(fd_, 0, SEEK_SET);
    while (size > 0) {
        ASSERT_EQUAL((ssize_t)9, read(fd_, buf_, 9));
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
        size -= 9;
    }
}

TEST_F(DoRunTest, TimeLimitExceeded) {
    ASSERT_EQUAL(1, doRun(fd_, TESTDIR "/tle", "cc", TESTDIR "/1.in", fn_,
                          1, 1000, 1000));
    off_t size = lseek(fd_, 0, SEEK_END);
    ASSERT_EQUAL(1, (int)size % 9);
    lseek(fd_, 0, SEEK_SET);
    while (size > 9) {
        ASSERT_EQUAL((ssize_t)9, read(fd_, buf_, 9));
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
        size -= 9;
    }
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, (int)buf_[0]);
}

TEST_F(DoRunTest, MemoryLimitExceeded) {
    ASSERT_EQUAL(1, doRun(fd_, TESTDIR "/mle", "cc", TESTDIR "/1.in", fn_,
                          10, 1, 1000));
    off_t size = lseek(fd_, 0, SEEK_END);
    ASSERT_EQUAL(1, (int)size % 9);
    lseek(fd_, 0, SEEK_SET);
    while (size > 9) {
        ASSERT_EQUAL((ssize_t)9, read(fd_, buf_, 9));
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
        size -= 9;
    }
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, (int)buf_[0]);
}

TEST_F(DoRunTest, OutputLimitExceeded) {
    ASSERT_EQUAL(1, doRun(fd_, TESTDIR "/ole", "cc", TESTDIR "/1.in", fn_,
                          10, 1000, 1));
    off_t size = lseek(fd_, 0, SEEK_END);
    ASSERT_EQUAL(1, (int)size % 9);
    lseek(fd_, 0, SEEK_SET);
    while (size > 9) {
        ASSERT_EQUAL((ssize_t)9, read(fd_, buf_, 9));
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
        size -= 9;
    }
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(OUTPUT_LIMIT_EXCEEDED, (int)buf_[0]);
}

TEST_F(DoRunTest, SegmentationFaultSIGSEGV) {
    ASSERT_EQUAL(1, doRun(fd_, TESTDIR "/sigsegv", "cc", TESTDIR "/1.in", fn_,
                          10, 1000, 1000));
    off_t size = lseek(fd_, 0, SEEK_END);
    ASSERT_EQUAL(1, (int)size % 9);
    lseek(fd_, 0, SEEK_SET);
    while (size > 9) {
        ASSERT_EQUAL((ssize_t)9, read(fd_, buf_, 9));
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
        size -= 9;
    }
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(SEGMENTATION_FAULT, (int)buf_[0]);
}

TEST_F(DoRunTest, FloatingPointError) {
    ASSERT_EQUAL(1, doRun(fd_, TESTDIR "/fpe", "cc", TESTDIR "/1.in", fn_,
                          10, 1000, 1000));
    off_t size = lseek(fd_, 0, SEEK_END);
    ASSERT_EQUAL(1, (int)size % 9);
    lseek(fd_, 0, SEEK_SET);
    while (size > 9) {
        ASSERT_EQUAL((ssize_t)9, read(fd_, buf_, 9));
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
        size -= 9;
    }
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(FLOATING_POINT_ERROR, (int)buf_[0]);
}

TEST_F(DoRunTest, RuntimeErrorRestrictedFunction) {
    ASSERT_EQUAL(1, doRun(fd_, TESTDIR "/rf", "cc", TESTDIR "/1.in", fn_,
                          10, 1000, 1000));
    off_t size = lseek(fd_, 0, SEEK_END);
    ASSERT_EQUAL(1, (int)size % 9);
    lseek(fd_, 0, SEEK_SET);
    while (size > 9) {
        ASSERT_EQUAL((ssize_t)9, read(fd_, buf_, 9));
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
        size -= 9;
    }
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(RUNTIME_ERROR, (int)buf_[0]);
}

//TODO Add INTERNAL_ERROR unittest
