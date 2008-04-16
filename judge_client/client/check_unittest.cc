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

#include "check.h"
#include "global.h"
#include "trace.h"

class DoCheckTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(),
                                (root_ + "/1.in").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(),
                                (root_ + "/1.out").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/judge").c_str(),
                                (root_ + "/judge").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.out").c_str(),
                                (root_ + "/ac.out").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/wa.out").c_str(),
                                (root_ + "/wa.out").c_str()));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/pe.out").c_str(),
                                (root_ + "/pe.out").c_str()));
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
    char buf_[32];
    string root_;
};

TEST_F(DoCheckTest, Accepted) {
    ASSERT_EQUAL(0, DoCheck(fd_,
                            0,
                            root_ + "/1.in",
                            root_ + "/1.out",
                            root_ + "/ac.out",
                            ""));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, WrongAnswer) {
    ASSERT_EQUAL(0, DoCheck(fd_,
                            0,
                            root_ + "/1.in",
                            root_ + "/1.out",
                            root_ + "/wa.out",
                            ""));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(WRONG_ANSWER, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, PresentationError) {
    ASSERT_EQUAL(0, DoCheck(fd_,
                            0,
                            root_ + "/1.in",
                            root_ + "/1.out",
                            root_ + "/pe.out",
                            ""));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(PRESENTATION_ERROR, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, SpecialJudgeAccepted) {
    ASSERT_EQUAL(0, DoCheck(fd_,
                            0,
                            root_ + "/1.in",
                            root_ + "/1.out",
                            root_ + "/ac.out",
                            root_ + "/judge"));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, SpecialJudgeWrongAnswer) {
    ASSERT_EQUAL(0, DoCheck(fd_,
                            0,
                            root_ + "/1.in",
                            root_ + "/1.out",
                            root_ + "/wa.out",
                            root_ + "/judge"));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(WRONG_ANSWER, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, SpecialJudgePresentationError) {
    ASSERT_EQUAL(0, DoCheck(fd_,
                            0,
                            root_ + "/1.in",
                            root_ + "/1.out",
                            root_ + "/pe.out",
                            root_ + "/judge"));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(PRESENTATION_ERROR, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}
