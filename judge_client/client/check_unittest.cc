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

#include <fcntl.h>
#include <sys/socket.h>

#include "check.h"
#include "global.h"
#include "trace.h"
#include "test_util-inl.h"

class DoCheckTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        InstallHandlers();
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "output"));
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        output_ = judge_ = "";
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
    }

    int Run() {
        ASSERT_EQUAL(0, symlink((TESTDIR + "/" + output_).c_str(), "p.out"));
        int ret = DoCheck(fd_[1], 0, judge_);
        shutdown(fd_[1], SHUT_WR);
        return ret;
    }

    int fd_[2];
    char buf_[32];
    string root_;
    string output_;
    string judge_;
};

TEST_F(DoCheckTest, Accepted) {
    output_ = "ac.out";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, WrongAnswer) {
    output_ = "wa.out";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(WRONG_ANSWER, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, PresentationError) {
    output_ = "pe.out";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(PRESENTATION_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, SpecialJudgeAccepted) {
    output_ = "ac.out";
    judge_ = "judge";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, SpecialJudgeWrongAnswer) {
    output_ = "wa.out";
    judge_ = "judge";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(WRONG_ANSWER, ReadLastUint32(fd_[0]));
}

TEST_F(DoCheckTest, SpecialJudgePresentationError) {
    output_ = "pe.out";
    judge_ = "judge";

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(PRESENTATION_ERROR, ReadLastUint32(fd_[0]));
}
