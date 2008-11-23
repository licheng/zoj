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

#ifndef __UNITTEST_H__
#define __UNITTEST_H__

#include <iostream>
#include <string>

using namespace std;

#include <cppunit/extensions/HelperMacros.h>
#include <cppunit/extensions/TestFactoryRegistry.h>
#include <cppunit/TestAssert.h>
#include <cppunit/TestFixture.h>
#include <cppunit/ui/text/TestRunner.h>

static inline string GetWorkingDir() {
    char path[PATH_MAX + 1];
    if (getcwd(path, sizeof(path)) == NULL) {
        CPPUNIT_FAIL("Fail to get the current working dir");
    }
    return path;
}

class TestFixture : public CppUnit::TestFixture {
    public:
        TestFixture():
                CURRENT_WORKING_DIR(GetWorkingDir()),
                TESTDIR(CURRENT_WORKING_DIR + "/testdata") { }
    protected:
        virtual void SetUp() { }
        virtual void TearDown() { }
        const string CURRENT_WORKING_DIR;
        const string TESTDIR;
};

#define TEST(name) \
    class __ ## name ## Test: public CPPUNIT_NS::TestCase {\
        CPPUNIT_TEST_SUITE(__ ## name ## Test);\
        CPPUNIT_TEST(_test);\
        CPPUNIT_TEST_SUITE_END();\
        public:\
            void setUp() { done_ = false; }\
            void tearDown() {\
                if (done_) {\
                    cout<<"\x1b[32mPass\x1b[0m"<<endl<<endl;\
                } else {\
                    cout<<"\x1b[31mFailed\x1b[0m"<<endl<<endl;\
                }\
            }\
            void _test() {\
                cout<<endl<<"****** " #name " ******"<<endl;\
                test();\
                done_ = true;\
            }\
            void test();\
        private:\
            bool done_;\
    };\
    CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(__ ## name ## Test, "alltest");\
    void __ ## name ## Test::test()

#define TEST_F(name, method) \
    class __ ## name ## _ ## method : public name {\
        CPPUNIT_TEST_SUITE(__ ## name ## _ ## method);\
        CPPUNIT_TEST(_test);\
        CPPUNIT_TEST_SUITE_END();\
        public:\
            void setUp() {\
                done_ = false;\
                name::SetUp();\
            }\
            void tearDown() {\
                if (done_) {\
                    cout<<"\x1b[32mPass\x1b[0m"<<endl<<endl;\
                } else {\
                    cout<<"\x1b[31mFailed\x1b[0m"<<endl<<endl;\
                }\
                name::TearDown();\
            }\
            void _test() {\
                cout<<endl<<"****** " #name "." #method " ******"<<endl;\
                test();\
                done_ = true;\
            }\
            void test();\
        private:\
            bool done_;\
    };\
    CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(__ ## name ## _ ## method,\
                                          "alltest");\
    void __ ## name ## _ ## method::test()

#define ASSERT_EQUAL CPPUNIT_ASSERT_EQUAL
#define ASSERT CPPUNIT_ASSERT
#define FAIL CPPUNIT_FAIL

int main() {
    CPPUNIT_NS::TextUi::TestRunner runner;
    CPPUNIT_NS::TestFactoryRegistry &registry =
        CPPUNIT_NS::TestFactoryRegistry::getRegistry("alltest");
    runner.addTest(registry.makeTest());
    return !runner.run();
}

#endif // __UNITTEST_H__
