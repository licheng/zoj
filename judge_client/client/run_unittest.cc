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

#include <unistd.h>

#include "args.h"
#include "judge_result.h"
#include "run.h"
#include "trace.h"
#include "util.h"

DECLARE_ARG(string, root);

class DoRunTest: public TestFixture {
  protected:
    void setUp() {
        ARG_root = "..";
        fp_ = tmpfile();
        fd_ = fileno(fp_);
        tmpnam(fn_);
        installHandlers();
    }
    
    void tearDown() {
        fclose(fp_);
        unlink(fn_);
    }

    FILE* fp_;
    int fd_;
    char fn_[1024];
    char buf_[32];
};

TEST_F(DoRunTest, Success) {
    ASSERT_EQUAL(0, doRun(fd_, TESTDIR "/ac", "cc", TESTDIR "/a+b.in", fn_,
                          10, 1000, 1000));
    ASSERT(!system(StringPrintf("diff %s " TESTDIR "/a+b.out", fn_).c_str()));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)9, read(fd_, buf_, 9));
    ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoRunTest, TimeLimitExceeded) {
    ASSERT_EQUAL(-1, doRun(fd_, TESTDIR "/tle", "cc", TESTDIR "/a+b.in", fn_,
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
