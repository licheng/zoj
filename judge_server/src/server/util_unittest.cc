/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ Judge Server.
 *
 * ZOJ Judge Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZOJ Judge Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ Judge Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
