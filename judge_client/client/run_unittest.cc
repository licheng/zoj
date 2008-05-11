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
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        InstallHandlers();
    }
    
    virtual void TearDown() {
        UninstallHandlers();
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        system(("rm -rf " + root_).c_str());
        chdir(CURRENT_WORKING_DIR.c_str());
    }

    int fd_[2];
    string fn_;
    char buf_[32];
    string root_;
};

TEST_F(DoRunTest, Success) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac").c_str(), "p"));
    ASSERT_EQUAL(0, DoRun(fd_[1], COMPILER_GPP, 10, 1000, 1000, 0, 0));
    close(fd_[1]);
    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    for (;;) {
        int size = read(fd_[0], buf_, 9);
        if (!size) {
            break;
        }
        ASSERT_EQUAL(9, size);
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    }
}

TEST_F(DoRunTest, TimeLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle").c_str(), "p"));
    ASSERT_EQUAL(1, DoRun(fd_[1], COMPILER_GPP, 1, 1000, 1000, 0, 0));
    for (;;) {
        int size = read(fd_[0], buf_, 9);
        if (size < 9) {
            ASSERT_EQUAL(1, size);
            ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, (int)buf_[0]);
            break;
        }
        ASSERT_EQUAL(9, size);
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    }
}

TEST_F(DoRunTest, MemoryLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/mle").c_str(), "p"));
    ASSERT_EQUAL(1, DoRun(fd_[1], COMPILER_GPP, 10, 1, 1000, 0, 0));
    for (;;) {
        int size = read(fd_[0], buf_, 9);
        if (size < 9) {
            ASSERT_EQUAL(1, size);
            ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, (int)buf_[0]);
            break;
        }
        ASSERT_EQUAL(9, size);
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    }
}

TEST_F(DoRunTest, MemoryLimitExceededMMap) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/mle_mmap").c_str(), "p"));
    ASSERT_EQUAL(1, DoRun(fd_[1], COMPILER_GPP, 10, 100000, 1000, 0, 0));
    for (;;) {
        int size = read(fd_[0], buf_, 9);
        if (size < 9) {
            ASSERT_EQUAL(1, size);
            ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, (int)buf_[0]);
            break;
        }
        ASSERT_EQUAL(9, size);
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    }
}

TEST_F(DoRunTest, OutputLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole").c_str(), "p"));
    ASSERT_EQUAL(1, DoRun(fd_[1], COMPILER_GPP, 10, 1000, 1, 0, 0));
    for (;;) {
        int size = read(fd_[0], buf_, 9);
        if (size < 9) {
            ASSERT_EQUAL(1, size);
            ASSERT_EQUAL(OUTPUT_LIMIT_EXCEEDED, (int)buf_[0]);
            break;
        }
        ASSERT_EQUAL(9, size);
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    }
}

TEST_F(DoRunTest, SegmentationFaultSIGSEGV) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/sigsegv").c_str(), "p"));
    ASSERT_EQUAL(1, DoRun(fd_[1], COMPILER_GPP, 10, 1000, 1000, 0, 0));
    for (;;) {
        int size = read(fd_[0], buf_, 9);
        if (size < 9) {
            ASSERT_EQUAL(1, size);
            ASSERT_EQUAL(SEGMENTATION_FAULT, (int)buf_[0]);
            break;
        }
        ASSERT_EQUAL(9, size);
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    }
}

TEST_F(DoRunTest, FloatingPointError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/fpe").c_str(), "p"));
    ASSERT_EQUAL(1, DoRun(fd_[1], COMPILER_GPP, 10, 1000, 1000, 0, 0));
    for (;;) {
        int size = read(fd_[0], buf_, 9);
        if (size < 9) {
            ASSERT_EQUAL(1, size);
            ASSERT_EQUAL(FLOATING_POINT_ERROR, (int)buf_[0]);
            break;
        }
        ASSERT_EQUAL(9, size);
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    }
}

TEST_F(DoRunTest, RuntimeErrorRestrictedFunctionLink) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_link").c_str(), "p"));
    ASSERT_EQUAL(1, DoRun(fd_[1], COMPILER_GPP, 10, 1000, 1000, 0, 0));
    for (;;) {
        int size = read(fd_[0], buf_, 9);
        if (size < 9) {
            ASSERT_EQUAL(1, size);
            ASSERT_EQUAL(RUNTIME_ERROR, (int)buf_[0]);
            break;
        }
        ASSERT_EQUAL(9, size);
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    }
}

TEST_F(DoRunTest, RuntimeErrorRestrictedFunctionOpen) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_open").c_str(), "p"));
    ASSERT_EQUAL(1, DoRun(fd_[1], COMPILER_GPP, 10, 1000, 1000, 0, 0));
    for (;;) {
        int size = read(fd_[0], buf_, 9);
        if (size < 9) {
            ASSERT_EQUAL(1, size);
            ASSERT_EQUAL(RUNTIME_ERROR, (int)buf_[0]);
            break;
        }
        ASSERT_EQUAL(9, size);
        ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    }
}

//TODO Add INTERNAL_ERROR unittest
