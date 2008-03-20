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

#include "args.h"
#include "check.h"
#include "judge_result.h"
#include "trace.h"

DEFINE_ARG(string, root, "");
DEFINE_ARG(int, uid, "");
DEFINE_ARG(int, gid, "");

class DoCheckTest: public TestFixture {
  protected:
    void setUp() {
        ARG_root = tmpnam(NULL);
        system(("testdata/create_test_env.sh " + ARG_root).c_str());
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
    char buf_[32];
};

TEST_F(DoCheckTest, Accepted) {
    ASSERT_EQUAL(0, doCheck(fd_,
                            ARG_root + "/" TESTDIR "/a+b.in",
                            ARG_root + "/" TESTDIR "/a+b.out",
                            ARG_root + "/" TESTDIR "/ac.out",
                            ""));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, WrongAnswer) {
    ASSERT_EQUAL(0, doCheck(fd_,
                            ARG_root + "/" TESTDIR "/a+b.in",
                            ARG_root + "/" TESTDIR "/a+b.out",
                            ARG_root + "/" TESTDIR "/wa.out",
                            ""));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(WRONG_ANSWER, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, PresentationError) {
    ASSERT_EQUAL(0, doCheck(fd_,
                            ARG_root + "/" TESTDIR "/a+b.in",
                            ARG_root + "/" TESTDIR "/a+b.out",
                            ARG_root + "/" TESTDIR "/pe.out",
                            ""));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(PRESENTATION_ERROR, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, SpecialJudgeAccepted) {
    ASSERT_EQUAL(0, doCheck(fd_,
                            ARG_root + "/" TESTDIR "/a+b.in",
                            ARG_root + "/" TESTDIR "/a+b.out",
                            ARG_root + "/" TESTDIR "/ac.out",
                            ARG_root + "/" TESTDIR "/spj"));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, SpecialJudgeWrongAnswer) {
    ASSERT_EQUAL(0, doCheck(fd_,
                            ARG_root + "/" TESTDIR "/a+b.in",
                            ARG_root + "/" TESTDIR "/a+b.out",
                            ARG_root + "/" TESTDIR "/wa.out",
                            ARG_root + "/" TESTDIR "/spj"));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(WRONG_ANSWER, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(DoCheckTest, SpecialJudgePresentationError) {
    ASSERT_EQUAL(0, doCheck(fd_,
                            ARG_root + "/" TESTDIR "/a+b.in",
                            ARG_root + "/" TESTDIR "/a+b.out",
                            ARG_root + "/" TESTDIR "/pe.out",
                            ARG_root + "/" TESTDIR "/spj"));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2));
    ASSERT_EQUAL(JUDGING, (int)buf_[0]);
    ASSERT_EQUAL(PRESENTATION_ERROR, (int)buf_[1]);
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}
