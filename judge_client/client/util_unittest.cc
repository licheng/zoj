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

#include <unistd.h>

#include "util.h"

TEST(readTimeConsumptionInvalidPID) {
    CPPUNIT_ASSERT_EQUAL(-1.0, readTimeConsumption(-1));
}

TEST(readTimeConsumptionNormal) {
    double ts = readTimeConsumption(getpid());
    // should be negative
    CPPUNIT_ASSERT(ts >= 0);
    // can not be too large
    CPPUNIT_ASSERT(ts < 100000);
}

TEST(readMemoryConsumptionInvalidPID) {
    CPPUNIT_ASSERT_EQUAL(-1, readMemoryConsumption(-1));
}

TEST(readMemoryConsumptionNormal) {
    int ms = readMemoryConsumption(getpid());
    // should be positive
    CPPUNIT_ASSERT(ms > 0);
    // can not be too large
    CPPUNIT_ASSERT(ms < 32 * 1024 * 1024);
}
