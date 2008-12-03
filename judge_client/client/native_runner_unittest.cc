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
#include "native_runner.h"

#include <stdlib.h>

#include <signal.h>

#include "protocol.h"
#include "strutil.h"
#include "test_util-inl.h"

class NativeRunnerTest {
  protected:
    void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        time_limit_ = 10;
        memory_limit_ = output_limit_ = 1000;
    }

    void TearDown() {
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        if (system(("rm -rf " + root_).c_str())) {
        }
    }

    int Run() {
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        NativeRunner runner(fd_[1], time_limit_, memory_limit_, output_limit_, 0, 0);
        int ret = runner.Run();
        ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
        return ret;
    }

    string root_;
    int fd_[2];
    int time_limit_;
    int memory_limit_;
    int output_limit_;
};

TEST_F(NativeRunnerTest, Success) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac").c_str(), "p"));

    ASSERT_EQUAL(0, Run());

    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (reply == -1) {
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(NativeRunnerTest, TimeLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle").c_str(), "p"));
    time_limit_ = 2;

    ASSERT_EQUAL(1, Run());

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(NativeRunnerTest, MemoryLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/mle").c_str(), "p"));
    memory_limit_ = 100;

    ASSERT_EQUAL(1, Run());

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(NativeRunnerTest, MemoryLimitExceededMMap) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/mle_mmap").c_str(), "p"));
    memory_limit_ = 10000;

    ASSERT_EQUAL(1, Run());

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(NativeRunnerTest, OutputLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole").c_str(), "p"));
    output_limit_ = 1;

    ASSERT_EQUAL(1, Run());

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(OUTPUT_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(NativeRunnerTest, SegmentationFaultSIGSEGV) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/sigsegv").c_str(), "p"));

    ASSERT_EQUAL(1, Run());

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(SEGMENTATION_FAULT, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(NativeRunnerTest, FloatingPointError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/fpe").c_str(), "p"));

    ASSERT_EQUAL(1, Run());

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(FLOATING_POINT_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(NativeRunnerTest, RuntimeErrorRestrictedFunctionLink) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_link").c_str(), "p"));

    ASSERT_EQUAL(1, Run());

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(NativeRunnerTest, RuntimeErrorRestrictedFunctionOpen) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_open").c_str(), "p"));

    ASSERT_EQUAL(1, Run());

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(NativeRunnerTest, RuntimeErrorRestrictedFunctionInvalidOpen) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_invalid_open").c_str(), "p"));

    ASSERT_EQUAL(1, Run());

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}
