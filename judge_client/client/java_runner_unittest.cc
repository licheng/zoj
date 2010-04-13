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
#include "java_runner.h"

#include "protocol.h"
#include "strutil.h"
#include "test_util-inl.h"

DEFINE_ARG(string, root, "");

class JavaRunnerTest : public TestFixture {
  protected:
    void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, symlink((CURRENT_WORKING_DIR + "/JavaSandbox.jar").c_str(), "JavaSandbox.jar"));
        ASSERT_EQUAL(0, symlink((CURRENT_WORKING_DIR + "/libsandbox.so").c_str(), "libsandbox.so"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        time_limit_ = 10;
        memory_limit_ = 8192;
        output_limit_ = 1000;
        ARG_root = root_;
    }

    void TearDown() {
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        if (system(("rm -rf " + root_).c_str())) {
        }
    }

    int Run() {
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        JavaRunner runner(fd_[1], time_limit_, memory_limit_, output_limit_, 0, 0);
        int ret = runner.Run();
        ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
        return ret;
    }

    string root_;
    int fd_[2];
    int time_limit_;
    int memory_limit_;
    int output_limit_;
};

TEST_F(JavaRunnerTest, Success) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.class").c_str(), "Main.class"));

    ASSERT_EQUAL(0, Run());

    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    ASSERT_EQUAL(-1, ReadUntilNotRunning(fd_[0]));
}

TEST_F(JavaRunnerTest, SuccessNonPublicClass) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac_non_public_class.class").c_str(), "Main.class"));

    ASSERT_EQUAL(0, Run());

    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    ASSERT_EQUAL(-1, ReadUntilNotRunning(fd_[0]));
}

TEST_F(JavaRunnerTest, SuccessMultipleClasses) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac_multiple_classes.class").c_str(), "Main.class"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac_multiple_classes$1.class").c_str(), "Main$1.class"));

    ASSERT_EQUAL(0, Run());

    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    ASSERT_EQUAL(-1, ReadUntilNotRunning(fd_[0]));
}

TEST_F(JavaRunnerTest, GregorianCalendar) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac_gregorian_calendar.class").c_str(), "Main.class"));

    ASSERT_EQUAL(0, Run());

    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    ASSERT_EQUAL(-1, ReadUntilNotRunning(fd_[0]));
}

TEST_F(JavaRunnerTest, JavaThreadForbiddenTest) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/thread_forbidden.class").c_str(), "Main.class"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(RUNTIME_ERROR, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}

TEST_F(JavaRunnerTest, JavaRunTimeTest) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/java_time.class").c_str(), "Main.class"));
    time_limit_ = 10;

    ASSERT_EQUAL(0, Run());

    int last_time_ = -1;
    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);

        if (time < 0) {
            ASSERT(last_time_ > 2500);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
        last_time_ = time;
    }
}

TEST_F(JavaRunnerTest, TimeLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle.class").c_str(), "Main.class"));
    time_limit_ = 5;

    ASSERT_EQUAL(1, Run());

    int last_time = -1;
    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, reply);
            ASSERT_EQUAL(5001, last_time);
            break;
        }
        last_time = time;
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(JavaRunnerTest, TimeLimitExceededStaticInitializer) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle_static_initializer.class").c_str(), "Main.class"));
    time_limit_ = 1;

    ASSERT_EQUAL(1, Run());

    int last_time = -1;
    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, reply);
            ASSERT_EQUAL(1001, last_time);
            break;
        }
        last_time = time;
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(JavaRunnerTest, TimeLimitExceededMultipleClassesStaticInitializer) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle_multiple_classes_static_initializer.class").c_str(), "Main.class"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle_multiple_classes_static_initializer_T.class").c_str(), "T.class"));
    time_limit_ = 1;

    ASSERT_EQUAL(1, Run());

    int last_time = -1;
    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, reply);
            ASSERT_EQUAL(1001, last_time);
            break;
        }
        last_time = time;
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(JavaRunnerTest, MemoryLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/mle.class").c_str(), "Main.class"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}

TEST_F(JavaRunnerTest, OutputLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole.class").c_str(), "Main.class"));
    output_limit_ = 1;

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(OUTPUT_LIMIT_EXCEEDED, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}

TEST_F(JavaRunnerTest, RuntimeError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rte.class").c_str(), "Main.class"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(RUNTIME_ERROR, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}

TEST_F(JavaRunnerTest, RuntimeErrorInvalidMain) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rte_invalid_main.class").c_str(), "Main.class"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(RUNTIME_ERROR, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}

TEST_F(JavaRunnerTest, RuntimeErrorNonStaticMain) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rte_nonstatic_main.class").c_str(), "Main.class"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(RUNTIME_ERROR, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}

TEST_F(JavaRunnerTest, RuntimeErrorHasPackage) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rte_has_package.class").c_str(), "Main.class"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(RUNTIME_ERROR, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}

TEST_F(JavaRunnerTest, RuntimeErrorWait) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rte_wait.class").c_str(), "Main.class"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(RUNTIME_ERROR, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}

TEST_F(JavaRunnerTest, RuntimeErrorSleep) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rte_sleep.class").c_str(), "Main.class"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(RUNTIME_ERROR, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}

TEST_F(JavaRunnerTest, RuntimeErrorCatchError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rte_catch_error.class").c_str(), "Main.class"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(RUNTIME_ERROR, ReadUntilNotRunning(fd_[0]));
    ASSERT(Eof(fd_[0]));
}
