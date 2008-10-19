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

DEFINE_ARG(int, int, "");
DEFINE_OPTIONAL_ARG(int, opt_int, 1001, "");
DEFINE_ARG(bool, bool, "");
DEFINE_OPTIONAL_ARG(bool, opt_bool, false, "");
DEFINE_ARG(string, string, "");
DEFINE_OPTIONAL_ARG(string, opt_string, "opt", "");

TEST(ParseArgumentsAllValid) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--bool",
                    "--string=test"};
    ASSERT_EQUAL(0, ParseArguments(4, argv));
    ASSERT_EQUAL(1234, ARG_int);
    ASSERT_EQUAL(true, ARG_bool);
    ASSERT_EQUAL(string("test"), ARG_string);
    ASSERT_EQUAL(1001, ARG_opt_int);
    ASSERT_EQUAL(false, ARG_opt_bool);
    ASSERT_EQUAL(string("opt"), ARG_opt_string);
}

TEST(ParseArgumentsNoInt) {
    const char* argv[] = {"",
                    "--bool",
                    "--string=test"};
    ASSERT_EQUAL(-1, ParseArguments(3, argv));
}

TEST(ParseArgumentsInvalidInt) {
    const char* argv[] = {"",
                    "--int=invalid",
                    "--bool",
                    "--string=test"};
    ASSERT_EQUAL(-1, ParseArguments(4, argv));
}

TEST(ParseArgumentsEmptyInt) {
    const char* argv[] = {"",
                    "--int=",
                    "--bool",
                    "--string=test"};
    ASSERT_EQUAL(-1, ParseArguments(4, argv));
}

TEST(ParseArgumentsNoBool) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--string=test"};
    ASSERT_EQUAL(-1, ParseArguments(3, argv));
}

TEST(ParseArgumentsInvalidBool) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--bool=invalid",
                    "--string=test"};
    ASSERT_EQUAL(-1, ParseArguments(4, argv));
}

TEST(ParseArgumentsEmptyBool) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--bool=",
                    "--string=test"};
    ASSERT_EQUAL(-1, ParseArguments(4, argv));
}

TEST(ParseArgumentsValidBoolTrue) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--bool=true",
                    "--string=test"};
    ASSERT_EQUAL(0, ParseArguments(4, argv));
    ASSERT_EQUAL(true, ARG_bool);
}

TEST(ParseArgumentsValidBoolFalse) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--bool=false",
                    "--string=test"};
    ASSERT_EQUAL(0, ParseArguments(4, argv));
    ASSERT_EQUAL(false, ARG_bool);
}

TEST(ParseArgumentsNoString) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--bool"};
    ASSERT_EQUAL(-1, ParseArguments(3, argv));
}

TEST(ParseArgumentsEmptyString) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--bool",
                    "--string="};
    ASSERT_EQUAL(0, ParseArguments(4, argv));
    ASSERT(ARG_string.empty());
}

TEST(ParseArgumentsOptionalInt) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--opt_int=1111",
                    "--bool",
                    "--string="};
    ASSERT_EQUAL(0, ParseArguments(5, argv));
    ASSERT_EQUAL(1111, ARG_opt_int);
}

TEST(ParseArgumentsOptionalBool) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--bool",
                    "--opt_bool",
                    "--string="};
    ASSERT_EQUAL(0, ParseArguments(5, argv));
    ASSERT_EQUAL(true, ARG_opt_bool);
}

TEST(ParseArgumentsOptionalString) {
    const char* argv[] = {"",
                    "--int=1234",
                    "--bool",
                    "--string=",
                    "--opt_string=test"};
    ASSERT_EQUAL(0, ParseArguments(5, argv));
    ASSERT_EQUAL(string("test"), ARG_opt_string);
}
