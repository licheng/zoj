/*
 * Copyright 2010 Li, Cheng <hanshuiys@gmail.com>
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
#include "script_runner.h"

#include <stdlib.h>
#include <sys/stat.h>

#include <signal.h>

#include "protocol.h"
#include "strutil.h"
#include "test_util-inl.h"

static const int LANGUAGE_PYTHON    = 5;
static const int LANGUAGE_PERL      = 6;
static const int LANGUAGE_GUILE     = 7;
static const int LANGUAGE_PHP       = 8;

DECLARE_ARG(string, root);

class ScriptRunnerTest {
  protected:
    void SetUp() {
        root_ = tmpnam(NULL);
        ARG_root = root_;
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, symlink((CURRENT_WORKING_DIR + "/PythonLoader.py").c_str(), "PythonLoader.py"));
        ASSERT_EQUAL(0, symlink((CURRENT_WORKING_DIR + "/PerlLoader.pm").c_str(), "PerlLoader.pm"));
        ASSERT_EQUAL(0, symlink((CURRENT_WORKING_DIR + "/PHPLoader.php").c_str(), "PHPLoader.php"));
        ASSERT_EQUAL(0, symlink((CURRENT_WORKING_DIR + "/guile_loader").c_str(), "guile_loader"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "input"));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        time_limit_ = 10;
        memory_limit_ = 32767;
        output_limit_ = 1000;
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

    int Run(int language_id) {
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        ScriptRunner runner(fd_[1], time_limit_, memory_limit_, output_limit_, 0, 0, language_id);
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

TEST_F(ScriptRunnerTest, PythonSuccess) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.py").c_str(), "p.py"));

    ASSERT_EQUAL(0, Run(LANGUAGE_PYTHON));

    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (reply == -1) {
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PythonSuccessOnOutputLimitExceededBoundary) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole_boundary.py").c_str(), "p.py"));
    output_limit_ = 1;

    ASSERT_EQUAL(0, Run(LANGUAGE_PYTHON));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PythonTimeLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle.py").c_str(), "p.py"));
    time_limit_ = 1;

    ASSERT_EQUAL(1, Run(LANGUAGE_PYTHON));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PythonMemoryLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/mle.py").c_str(), "p.py"));
    memory_limit_ = 100;

    ASSERT_EQUAL(1, Run(LANGUAGE_PYTHON));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PythonOutputLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole.py").c_str(), "p.py"));
    output_limit_ = 1;

    ASSERT_EQUAL(1, Run(LANGUAGE_PYTHON));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(OUTPUT_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PythonFloatingPointError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/fpe.py").c_str(), "p.py"));

    ASSERT_EQUAL(1, Run(LANGUAGE_PYTHON));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(FLOATING_POINT_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PythonRuntimeErrorRestrictedFunctionLink) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_link.py").c_str(), "p.py"));

    ASSERT_EQUAL(1, Run(LANGUAGE_PYTHON));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PythonRuntimeErrorRestrictedFunctionInvalidOpen) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_invalid_open.py").c_str(), "p.py"));

    ASSERT_EQUAL(1, Run(LANGUAGE_PYTHON));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PythonRuntimeErrorRestrictedFunctionSleep) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_sleep.py").c_str(), "p.py"));

    ASSERT_EQUAL(1, Run(LANGUAGE_PYTHON));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

// ==================================================================


TEST_F(ScriptRunnerTest, PerlSuccess) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.pl").c_str(), "p.pl"));

    ASSERT_EQUAL(0, Run(LANGUAGE_PERL));

    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (reply == -1) {
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PerlSuccessOnOutputLimitExceededBoundary) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole_boundary.pl").c_str(), "p.pl"));
    output_limit_ = 1;

    ASSERT_EQUAL(0, Run(LANGUAGE_PERL));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PerlTimeLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle.pl").c_str(), "p.pl"));
    time_limit_ = 1;

    ASSERT_EQUAL(1, Run(LANGUAGE_PERL));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PerlMemoryLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/mle.pl").c_str(), "p.pl"));
    memory_limit_ = 100;

    ASSERT_EQUAL(1, Run(LANGUAGE_PERL));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PerlOutputLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole.pl").c_str(), "p.pl"));
    output_limit_ = 1;

    ASSERT_EQUAL(1, Run(LANGUAGE_PERL));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(OUTPUT_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PerlFloatingPointError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/fpe.pl").c_str(), "p.pl"));

    ASSERT_EQUAL(1, Run(LANGUAGE_PERL));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(FLOATING_POINT_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PerlRuntimeErrorRestrictedFunctionLink) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_link.pl").c_str(), "p.pl"));

    ASSERT_EQUAL(1, Run(LANGUAGE_PERL));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PerlRuntimeErrorRestrictedFunctionInvalidOpen) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_invalid_open.pl").c_str(), "p.pl"));

    ASSERT_EQUAL(1, Run(LANGUAGE_PERL));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

// ==================================================================

TEST_F(ScriptRunnerTest, PHPSuccess) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.php").c_str(), "p.php"));

    ASSERT_EQUAL(0, Run(LANGUAGE_PHP));

    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (reply == -1) {
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PHPSuccessOnOutputLimitExceededBoundary) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole_boundary.php").c_str(), "p.php"));
    output_limit_ = 1;

    ASSERT_EQUAL(0, Run(LANGUAGE_PHP));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PHPTimeLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle.php").c_str(), "p.php"));
    time_limit_ = 1;

    ASSERT_EQUAL(1, Run(LANGUAGE_PHP));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PHPMemoryLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/mle.php").c_str(), "p.php"));
    memory_limit_ = 2000;

    ASSERT_EQUAL(1, Run(LANGUAGE_PHP));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PHPOutputLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole.php").c_str(), "p.php"));
    output_limit_ = 1;

    ASSERT_EQUAL(1, Run(LANGUAGE_PHP));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(OUTPUT_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PHPFloatingPointError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/fpe.php").c_str(), "p.php"));

    ASSERT_EQUAL(1, Run(LANGUAGE_PHP));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(FLOATING_POINT_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PHPRuntimeErrorRestrictedFunctionLink) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_link.php").c_str(), "p.php"));

    ASSERT_EQUAL(1, Run(LANGUAGE_PHP));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, PHPRuntimeErrorRestrictedFunctionInvalidOpen) {
    FILE* fout = fopen("p.php", "w");
    ASSERT_EQUAL(0, (int)!fout);
    fprintf(fout, "<?php  $handle = fopen('%s/prob/somefile', 'r');", ARG_root.c_str());
    fclose(fout);

    ASSERT_EQUAL(1, Run(LANGUAGE_PHP));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

// ==================================================================

TEST_F(ScriptRunnerTest, GuileSuccess) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ac.scm").c_str(), "p.scm"));

    ASSERT_EQUAL(0, Run(LANGUAGE_GUILE));

    ASSERT(!system(StringPrintf("diff p.out %s/1.out", TESTDIR.c_str()).c_str()));
    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (reply == -1) {
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, GuileSuccessOnOutputLimitExceededBoundary) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole_boundary.scm").c_str(), "p.scm"));
    output_limit_ = 1;

    ASSERT_EQUAL(0, Run(LANGUAGE_GUILE));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, GuileTimeLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/tle.scm").c_str(), "p.scm"));
    time_limit_ = 1;

    ASSERT_EQUAL(1, Run(LANGUAGE_GUILE));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(TIME_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, GuileMemoryLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/mle.scm").c_str(), "p.scm"));
    memory_limit_ = 2000;

    ASSERT_EQUAL(1, Run(LANGUAGE_GUILE));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(MEMORY_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, GuileOutputLimitExceeded) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/ole.scm").c_str(), "p.scm"));
    output_limit_ = 1;

    ASSERT_EQUAL(1, Run(LANGUAGE_GUILE));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(OUTPUT_LIMIT_EXCEEDED, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

/*
TEST_F(ScriptRunnerTest, GuileFloatingPointError) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/fpe.scm").c_str(), "p.scm"));

    ASSERT_EQUAL(1, Run(LANGUAGE_GUILE));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(FLOATING_POINT_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}
*/

TEST_F(ScriptRunnerTest, GuileRuntimeErrorRestrictedFunctionLink) {
    ASSERT_EQUAL(0, symlink((TESTDIR + "/rf_link.scm").c_str(), "p.scm"));

    ASSERT_EQUAL(1, Run(LANGUAGE_GUILE));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

TEST_F(ScriptRunnerTest, GuileRuntimeErrorRestrictedFunctionInvalidOpen) {
    FILE* fout = fopen("p.scm", "w");
    ASSERT_EQUAL(0, (int)!fout);
    fprintf(fout, "(open-file \"%s/prob/somefile\" \"r\")", ARG_root.c_str());
    fclose(fout);

    ASSERT_EQUAL(1, Run(LANGUAGE_GUILE));

    for (;;) {
        int reply = TryReadUint32(fd_[0]);
        int time = TryReadUint32(fd_[0]);
        int memory = TryReadUint32(fd_[0]);
        if (time < 0) {
            ASSERT_EQUAL(RUNTIME_ERROR, reply);
            break;
        }
        ASSERT_EQUAL(RUNNING, reply);
        ASSERT(time >= 0);
        ASSERT(memory >= 0);
    }
}

