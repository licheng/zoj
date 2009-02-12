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

#include <map>
#include <vector>

#include "strutil.h"

using namespace std;

DEFINE_ARG(int, int, "");
DEFINE_OPTIONAL_ARG(int, opt_int, 1001, "");
DEFINE_ARG(bool, bool, "");
DEFINE_OPTIONAL_ARG(bool, opt_bool, false, "");
DEFINE_ARG(string, string, "");
DEFINE_OPTIONAL_ARG(string, opt_string, "opt", "");

class ParseArgumentsTest : public TestFixture {
  protected:
    void SetUp() {
        args_["int"] = "1234";
        args_["bool"] = 0;
        args_["string"] = "test";
    }

    int Run() {
        vector<string> t;
        t.push_back("");
        for (map<const char*, const char*>::iterator it = args_.begin();
             it != args_.end(); ++it) {
            if (it->second) {
                t.push_back(StringPrintf("--%s=%s", it->first, it->second));
            } else {
                t.push_back(StringPrintf("--%s", it->first));
            }
        }
        const char* argv[10];
        for (int i = 0; i < t.size(); ++i) {
            argv[i] = t[i].c_str();
        }
        return ParseArguments(t.size(), argv);
    }

    map<const char*, const char*> args_;
};

TEST_F(ParseArgumentsTest, AllValid) {
    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(1234, ARG_int);
    ASSERT_EQUAL(true, ARG_bool);
    ASSERT_EQUAL(string("test"), ARG_string);
    ASSERT_EQUAL(1001, ARG_opt_int);
    ASSERT_EQUAL(false, ARG_opt_bool);
    ASSERT_EQUAL(string("opt"), ARG_opt_string);
}

TEST_F(ParseArgumentsTest, NoInt) {
    args_.erase("int");
    ASSERT_EQUAL(-1, Run());
}

TEST_F(ParseArgumentsTest, InvalidInt) {
    args_["int"] = "invalid";
    ASSERT_EQUAL(-1, Run());
}

TEST_F(ParseArgumentsTest, EmptyInt) {
    args_["int"] = "";
    ASSERT_EQUAL(-1, Run());
}

TEST_F(ParseArgumentsTest, NoBool) {
    args_.erase("bool");
    ASSERT_EQUAL(-1, Run());
}

TEST_F(ParseArgumentsTest, InvalidBool) {
    args_["bool"] = "invalid";
    ASSERT_EQUAL(-1, Run());
}

TEST_F(ParseArgumentsTest, EmptyBool) {
    args_["bool"] = "";
    ASSERT_EQUAL(-1, Run());
}

TEST_F(ParseArgumentsTest, ValidBoolTrue) {
    args_["bool"] = "true";
    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(true, ARG_bool);
}

TEST_F(ParseArgumentsTest, ValidBoolFalse) {
    args_["bool"] = "false";
    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(false, ARG_bool);
}

TEST_F(ParseArgumentsTest, NoString) {
    args_.erase("string");
    ASSERT_EQUAL(-1, Run());
}

TEST_F(ParseArgumentsTest, EmptyString) {
    args_["string"] = "";
    ASSERT_EQUAL(0, Run());

    ASSERT(ARG_string.empty());
}

TEST_F(ParseArgumentsTest, OptionalInt) {
    args_["opt_int"] = "1111";
    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(1111, ARG_opt_int);
}

TEST_F(ParseArgumentsTest, OptionalBool) {
    args_["opt_bool"] = 0;
    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(true, ARG_opt_bool);
}

TEST_F(ParseArgumentsTest, OptionalString) {
    args_["opt_string"] = "test";
    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(string("test"), ARG_opt_string);
}
