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
#include "checker.h"

#include "protocol.h"
#include "test_util-inl.h"

class CheckerTest : public TestFixture, public Checker {
  protected:
    void SetUp() {
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
    }

    void TearDown() {
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
    }

    int Run() {
        int ret = Check(fd_[1]);
        ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
        return ret;
    }

    virtual int InternalCheck(int sock) {
        ASSERT_EQUAL(fd_[1], sock);
        return result_;
    }

    int fd_[2];
    int result_;
};

TEST_F(CheckerTest, Accepted) {
    result_ = ACCEPTED;

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadLastUint32(fd_[0]));
}

TEST_F(CheckerTest, WrongAnswer) {
    result_ = WRONG_ANSWER;

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(WRONG_ANSWER, ReadLastUint32(fd_[0]));
}

TEST_F(CheckerTest, PresentationError) {
    result_ = PRESENTATION_ERROR;

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(PRESENTATION_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(CheckerTest, InternalError) {
    result_ = -1;

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INTERNAL_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(CheckerTest, InvalidResult) {
    result_ = -2;

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INTERNAL_ERROR, ReadLastUint32(fd_[0]));
}
