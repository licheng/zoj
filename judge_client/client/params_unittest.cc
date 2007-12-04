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

DEFINE_ARG(int, int, "");
DEFINE_OPTIONAL_ARG(int, opt_int, 1001, "");
DEFINE_ARG(bool, bool, "");
DEFINE_OPTIONAL_ARG(bool, opt_bool, false, "");
DEFINE_ARG(string, string, "");
DEFINE_OPTIONAL_ARG(string, opt_string, "opt", "");

TEST(parseArgumentsAllValid) {
    char* argv[] = {"",
                    "--int=1234",
                    "--bool",
                    "--string=test"};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(4, argv));
    CPPUNIT_ASSERT_EQUAL(1234, ARG_int);
    CPPUNIT_ASSERT_EQUAL(true, ARG_bool);
    CPPUNIT_ASSERT_EQUAL(string("test"), ARG_string);
    CPPUNIT_ASSERT_EQUAL(1001, ARG_opt_int);
    CPPUNIT_ASSERT_EQUAL(false, ARG_opt_bool);
    CPPUNIT_ASSERT_EQUAL(string("opt"), ARG_opt_string);
}

TEST(parseArgumentsNoInt) {
    char* argv[] = {"",
                    "--bool",
                    "--string=test"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(3, argv));
}

TEST(parseArgumentsInvalidInt) {
    char* argv[] = {"",
                    "--int=invalid",
                    "--bool",
                    "--string=test"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(4, argv));
}

TEST(parseArgumentsEmptyInt) {
    char* argv[] = {"",
                    "--int=",
                    "--bool",
                    "--string=test"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(4, argv));
}

TEST(parseArgumentsNoBool) {
    char* argv[] = {"",
                    "--int=1234",
                    "--string=test"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(3, argv));
}

TEST(parseArgumentsInvalidBool) {
    char* argv[] = {"",
                    "--int=1234",
                    "--bool=invalid",
                    "--string=test"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(4, argv));
}

TEST(parseArgumentsEmptyBool) {
    char* argv[] = {"",
                    "--int=1234",
                    "--bool=",
                    "--string=test"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(4, argv));
}

TEST(parseArgumentsValidBoolTrue) {
    char* argv[] = {"",
                    "--int=1234",
                    "--bool=true",
                    "--string=test"};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(4, argv));
    CPPUNIT_ASSERT_EQUAL(true, ARG_bool);
}

TEST(parseArgumentsValidBoolFalse) {
    char* argv[] = {"",
                    "--int=1234",
                    "--bool=false",
                    "--string=test"};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(4, argv));
    CPPUNIT_ASSERT_EQUAL(false, ARG_bool);
}

TEST(parseArgumentsNoString) {
    char* argv[] = {"",
                    "--int=1234",
                    "--bool"};
    CPPUNIT_ASSERT_EQUAL(-1, parseArguments(3, argv));
}

TEST(parseArgumentsEmptyString) {
    char* argv[] = {"",
                    "--int=1234",
                    "--bool",
                    "--string="};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(4, argv));
    CPPUNIT_ASSERT(ARG_string.empty());
}

TEST(parseArgumentsOptionalInt) {
    char* argv[] = {"",
                    "--int=1234",
                    "--opt_int=1111",
                    "--bool",
                    "--string="};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(5, argv));
    CPPUNIT_ASSERT_EQUAL(1111, ARG_opt_int);
}

TEST(parseArgumentsOptionalBool) {
    char* argv[] = {"",
                    "--int=1234",
                    "--bool",
                    "--opt_bool",
                    "--string="};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(5, argv));
    CPPUNIT_ASSERT_EQUAL(true, ARG_opt_bool);
}

TEST(parseArgumentsOptionalString) {
    char* argv[] = {"",
                    "--int=1234",
                    "--bool",
                    "--string=",
                    "--opt_string=test"};
    CPPUNIT_ASSERT_EQUAL(0, parseArguments(5, argv));
    CPPUNIT_ASSERT_EQUAL(string("test"), ARG_opt_string);
}
