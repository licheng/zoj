#include "unittest.h"

#include "compile.h"

TEST(compileSuccess) {
    char buf[128];
    int bufSize = sizeof(buf);
    CPPUNIT_ASSERT_EQUAL(0, compile(TESTDIR "/ac.cc", buf, &bufSize));
    CPPUNIT_ASSERT_EQUAL(0, bufSize);
}
