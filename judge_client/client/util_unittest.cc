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

#include <sys/resource.h>
#include <unistd.h>

#include "util.h"

#include "args.h"

DEFINE_ARG(string, root, "");

class TestSetLimit : public TestFixture {
  protected:
    virtual void setUp() {
        getrlimit(RLIMIT_FSIZE, &original_);;
    }

    virtual void tearDown() {
        setrlimit(RLIMIT_FSIZE, &original_);
    }

    struct rlimit original_;
};

TEST_F(TestSetLimit, Normal) {
    setLimit(RLIMIT_FSIZE, 1024 * 1024);
    struct rlimit t;
    getrlimit(RLIMIT_FSIZE, &t);
    ASSERT_EQUAL(1024 * 1024, (int)t.rlim_cur);
    ASSERT_EQUAL(1024 * 1024 + 1, (int)t.rlim_max);
}

TEST_F(TestSetLimit, Invalid) {
    ASSERT_EQUAL(-1, setLimit(RLIMIT_FSIZE, -1));
}

TEST(readTimeConsumptionInvalidPID) {
    ASSERT_EQUAL(-1.0, readTimeConsumption(-1));
}

TEST(readTimeConsumptionNormal) {
    double ts = readTimeConsumption(getpid());
    // should be negative
    ASSERT(ts >= 0);
    // can not be too large
    ASSERT(ts < 100000);
}

TEST(readMemoryConsumptionInvalidPID) {
    ASSERT_EQUAL(-1, readMemoryConsumption(-1));
}

TEST(readMemoryConsumptionNormal) {
    int ms = readMemoryConsumption(getpid());
    // should be positive
    ASSERT(ms > 0);
    // can not be too large
    ASSERT(ms < 32 * 1024 * 1024);
}
