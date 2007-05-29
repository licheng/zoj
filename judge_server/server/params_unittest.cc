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

#include "params.h"

TEST(parseArgumentsPort) {
    optind = 0;
    char* argv[] = {"", "--port=1234"};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(2, argv));
    CPPUNIT_ASSERT_EQUAL(1234, SERVER_PORT);
}

TEST(parseArgumentsInvalidPort) {
    optind = 0;
    char* argv[] = {"", "--port=invalid"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(2, argv));
}

TEST(parseArgumentsUID) {
    optind = 0;
    char* argv[] = {"", "--uid=1234"};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(2, argv));
    CPPUNIT_ASSERT_EQUAL(1234, JOB_UID);
}

TEST(parseArgumentsInvalidUID) {
    optind = 0;
    char* argv[] = {"", "--uid=invalid"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(2, argv));
}

TEST(parseArgumentsGID) {
    optind = 0;
    char* argv[] = {"", "--gid=1234"};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(2, argv));
    CPPUNIT_ASSERT_EQUAL(1234, JOB_GID);
}

TEST(parseArgumentsInvalidGID) {
    optind = 0;
    char* argv[] = {"", "--gid=invalid"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(2, argv));
}

TEST(parseArgumentsLANG) {
    optind = 0;
    char* argv[] = {"", "--lang=a,b,c"};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(2, argv));
    CPPUNIT_ASSERT_EQUAL(std::string(",a,b,c,"), LANG);
}

TEST(parseArgumentsMaxJobs) {
    optind = 0;
    char* argv[] = {"", "--maxjobs=1234"};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(2, argv));
    CPPUNIT_ASSERT_EQUAL(1234, MAX_JOBS);
}

TEST(parseArgumentsInvalidMaxJobs) {
    optind = 0;
    char* argv[] = {"", "--maxjobs=invalid"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(2, argv));
}

TEST(parseArgumentsInvalidOption) {
    optind = 0;
    char* argv[] = {"", "--invalid=invalid"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(2, argv));
}

TEST(parseArgumentsDefaultOption) {
    optind = 0;
    char* argv[] = {"", ""};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(2, argv));
}

TEST(parseArgumentsMixed) {
    optind = 0;
    char* argv[] = {
        "", "--port=1", "--uid=2", "--gid=3", "--lang=4", "--maxjobs=5"};
    CPPUNIT_ASSERT_EQUAL(
            0, parseArguments(sizeof(argv) / sizeof(argv[0]), argv));
    CPPUNIT_ASSERT_EQUAL(1, SERVER_PORT);
    CPPUNIT_ASSERT_EQUAL(2, JOB_UID);
    CPPUNIT_ASSERT_EQUAL(3, JOB_GID);
    CPPUNIT_ASSERT_EQUAL(std::string(",4,"), LANG);
    CPPUNIT_ASSERT_EQUAL(5, MAX_JOBS);
}
