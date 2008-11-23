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
#include "special_checker.h"

#include <sys/socket.h>
#include <sys/stat.h>

#include "args.h"
#include "common_io.h"
#include "protocol.h"
#include "test_util-inl.h"
#include "trace.h"

DEFINE_OPTIONAL_ARG(int, uid, 0, "");
DECLARE_ARG(int, special_judge_run_time_limit);

class SpecialCheckerTest : public TestFixture {
  protected:
    virtual void SetUp() {
        checker_ = NULL;
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        InstallHandlers();
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "output"));
        ARG_special_judge_run_time_limit = 1;
    }

    virtual void TearDown() {
        if (checker_) {
            delete checker_;
        }
        UninstallHandlers();
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        system(("rm -rf " + root_).c_str());
    }

    int Run() {
        ASSERT_EQUAL(0, symlink((TESTDIR + "/" + output_).c_str(), "p.out"));
        checker_ = new SpecialChecker(TESTDIR + "/" + judge_);
        return checker_->InternalCheck(fd_[1]);
    }

    string root_;
    int fd_[2];
    string output_;
    string judge_;
    SpecialChecker* checker_;
};

TEST_F(SpecialCheckerTest, Accepted) {
    output_ = "ac.out";
    judge_ = "judge";

    ASSERT_EQUAL(ACCEPTED, Run());
}

TEST_F(SpecialCheckerTest, WrongAnswer) {
    output_ = "wa.out";
    judge_ = "judge";

    ASSERT_EQUAL(WRONG_ANSWER, Run());
}

TEST_F(SpecialCheckerTest, PresentationError) {
    output_ = "pe.out";
    judge_ = "judge";

    ASSERT_EQUAL(PRESENTATION_ERROR, Run());
}

TEST_F(SpecialCheckerTest, TimeLimitExceeded) {
    output_ = "ac.out";
    judge_ = "tle";

    ASSERT_EQUAL(-1, Run());
}

