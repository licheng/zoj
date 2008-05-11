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

class DoCheckTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
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
    }

    int fd_[2];
    char buf_[32];
    string root_;
};

TEST_F(DoCheckTest, Accepted) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "output"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.out").c_str(), "p.out"));
    ASSERT_EQUAL(0, DoCheck(fd_[1], 0, ""));
    ASSERT_EQUAL((ssize_t)2, read(fd_[0], buf_, 3));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[1]);
}

TEST_F(DoCheckTest, WrongAnswer) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "output"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/wa.out").c_str(), "p.out"));
    ASSERT_EQUAL(0, DoCheck(fd_[1], 0, ""));
    ASSERT_EQUAL((ssize_t)2, read(fd_[0], buf_, 3));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(WRONG_ANSWER, (int)buf_[1]);
}

TEST_F(DoCheckTest, PresentationError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "output"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/pe.out").c_str(), "p.out"));
    ASSERT_EQUAL(0, DoCheck(fd_[1], 0, ""));
    ASSERT_EQUAL((ssize_t)2, read(fd_[0], buf_, 3));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(PRESENTATION_ERROR, (int)buf_[1]);
}

TEST_F(DoCheckTest, SpecialJudgeAccepted) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "output"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.out").c_str(), "p.out"));
    ASSERT_EQUAL(0, DoCheck(fd_[1], 0, "judge"));
    ASSERT_EQUAL((ssize_t)2, read(fd_[0], buf_, 3));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[1]);
}

TEST_F(DoCheckTest, SpecialJudgeWrongAnswer) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "output"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/wa.out").c_str(), "p.out"));
    ASSERT_EQUAL(0, DoCheck(fd_[1], 0, "judge"));
    ASSERT_EQUAL((ssize_t)2, read(fd_[0], buf_, 3));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(WRONG_ANSWER, (int)buf_[1]);
}

TEST_F(DoCheckTest, SpecialJudgePresentationError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "output"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/pe.out").c_str(), "p.out"));
    ASSERT_EQUAL(0, DoCheck(fd_[1], 0, "judge"));
    ASSERT_EQUAL((ssize_t)2, read(fd_[0], buf_, 3));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(PRESENTATION_ERROR, (int)buf_[1]);
}
