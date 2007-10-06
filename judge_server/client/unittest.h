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

#ifndef __UNITTEST_H
#define __UNITTEST_H

#include <cppunit/extensions/HelperMacros.h>
#include <cppunit/extensions/TestFactoryRegistry.h>
#include <cppunit/TestAssert.h>
#include <cppunit/TestFixture.h>
#include <cppunit/ui/text/TestRunner.h>

#define TEST(name) \
    class __ ## name ## Test: public CPPUNIT_NS::TestCase {\
        CPPUNIT_TEST_SUITE(__ ## name ## Test);\
        CPPUNIT_TEST(test);\
        CPPUNIT_TEST_SUITE_END();\
        public:\
            void test();\
    };\
    CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(__ ## name ## Test, "alltest");\
    void __ ## name ## Test::test()

#define TEST_F(name, method) \
    class __ ## name ## _ ## method : public name {\
        CPPUNIT_TEST_SUITE(__ ## name ## _ ## method);\
        CPPUNIT_TEST(test);\
        CPPUNIT_TEST_SUITE_END();\
        public:\
               void setUp() { name::setUp(); }\
               void tearDown() { name::tearDown(); }\
               void test();\
    };\
    CPPUNIT_TEST_SUITE_NAMED_REGISTRATION(__ ## name ## _ ## method,\
                                          "alltest");\
    void __ ## name ## _ ## method::test()

int main() {
    CPPUNIT_NS::TextUi::TestRunner runner;
    CPPUNIT_NS::TestFactoryRegistry &registry =
        CPPUNIT_NS::TestFactoryRegistry::getRegistry("alltest");
    runner.addTest(registry.makeTest());
    runner.run();
    return 0;
}

#define TESTDIR "testdata"

#endif
