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
#include "global.h"
#include "run.h"
#include "trace.h"
#include "util.h"

class DoRunTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        fn_ = root_ + "/output";
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(),
                                (root_ + "/1.in").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(),
                                (root_ + "/1.out").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/ac").c_str(),
                                (root_ + "/ac").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/wa").c_str(),
                                (root_ + "/wa").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/tle").c_str(),
                                (root_ + "/tle").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/ole").c_str(),
                                (root_ + "/ole").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/mle").c_str(),
                                (root_ + "/mle").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/mle_mmap").c_str(),
                                (root_ + "/mle_mmap").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/fpe").c_str(),
                                (root_ + "/fpe").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_link").c_str(),
                                (root_ + "/rf_link").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_open").c_str(),
                                (root_ + "/rf_open").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/pe").c_str(),
                                (root_ + "/pe").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/sigsegv").c_str(),
                                (root_ + "/sigsegv").c_str()));
        fp_ = tmpfile();
        fd_ = fileno(fp_);
        InstallHandlers();
    }
    
    virtual void TearDown() {
        UninstallHandlers();
        if (fp_) {
            fclose(fp_);
        }
        system(("rm -rf " + root_).c_str());
    }

    FILE* fp_;
    int fd_;
    string fn_;
    char buf_[32];
    string root_;
};

TEST_F(DoRunTest, Success) {
    ASSERT_EQUAL(0, DoRun(fd_, (TESTDIR + "/ac").c_str(), "cc",
                          (TESTDIR + "/1.in").c_str(), fn_,
                          10, 1000, 1000, 0, 0));
    ASSERT(!system(StringPrintf("diff %s %s", (TESTDIR + "/1.out").c_str(),
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
    ASSERT_EQUAL(1, DoRun(fd_, (TESTDIR + "/tle").c_str(), "cc",
                          (TESTDIR + "/1.in").c_str(), fn_,
                          1, 1000, 1000, 0, 0));
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
    ASSERT_EQUAL(1, DoRun(fd_, (TESTDIR + "/mle").c_str(), "cc",
                          (TESTDIR + "/1.in").c_str(), fn_,
                          10, 1, 1000, 0, 0));
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

TEST_F(DoRunTest, MemoryLimitExceededMMap) {
    ASSERT_EQUAL(1, DoRun(fd_, (TESTDIR + "/mle_mmap").c_str(), "cc",
                          (TESTDIR + "/1.in").c_str(), fn_,
                          10, 100000, 1000, 0, 0));
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
    ASSERT_EQUAL(1, DoRun(fd_, (TESTDIR + "/ole").c_str(), "cc",
                          (TESTDIR + "/1.in").c_str(), fn_,
                          10, 1000, 1, 0, 0));
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
    ASSERT_EQUAL(1, DoRun(fd_, (TESTDIR + "/sigsegv").c_str(), "cc",
                          (TESTDIR + "/1.in").c_str(), fn_,
                          10, 1000, 1000, 0, 0));
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
    ASSERT_EQUAL(1, DoRun(fd_, (TESTDIR + "/fpe").c_str(), "cc",
                          (TESTDIR + "/1.in").c_str(), fn_,
                          10, 1000, 1000, 0, 0));
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

TEST_F(DoRunTest, RuntimeErrorRestrictedFunctionLink) {
    ASSERT_EQUAL(1, DoRun(fd_, (TESTDIR + "/rf_link").c_str(), "cc",
                          (TESTDIR + "/1.in").c_str(), fn_,
                          10, 1000, 1000, 0, 0));
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

TEST_F(DoRunTest, RuntimeErrorRestrictedFunctionOpen) {
    ASSERT_EQUAL(1, DoRun(fd_, (TESTDIR + "/rf_open").c_str(), "cc",
                          (TESTDIR + "/1.in").c_str(), fn_,
                          10, 1000, 1000, 0, 0));
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
