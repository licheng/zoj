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
