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

#include <errno.h>
#include <sys/wait.h>
#include <unistd.h>

#include "environment.h"
#include "test_util-inl.h"
#include "trace.h"

DECLARE_ARG(string, compiler);

bool IsSupportedCompiler(const string& sourceFileType);

class IsSupportedCompilerTest : public TestFixture {
};

TEST_F(IsSupportedCompilerTest, NoneSupported) {
    ARG_compiler = "";
    ASSERT(!IsSupportedCompiler("g++"));
}

TEST_F(IsSupportedCompilerTest, OneSupported) {
    ARG_compiler = "g++";
    ASSERT(IsSupportedCompiler("g++"));
    ASSERT(!IsSupportedCompiler("gcc"));
}

TEST_F(IsSupportedCompilerTest, TwoSupported) {
    ARG_compiler = "g++,gcc";
    ASSERT(IsSupportedCompiler("g++"));
    ASSERT(IsSupportedCompiler("gcc"));
    ASSERT(!IsSupportedCompiler("fp"));
}

TEST_F(IsSupportedCompilerTest, ThreeSupported) {
    ARG_compiler = "g++,gcc,fp";
    ASSERT(IsSupportedCompiler("g++"));
    ASSERT(IsSupportedCompiler("gcc"));
    ASSERT(IsSupportedCompiler("fp"));
}

int ExecJudgeCommand(int sock, int* problem_id, int* revision);

class ExecJudgeCommandTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        submission_id_ = 1234;
        problem_id_ = 100;
        revision_ = 101;
        checksum_ = CheckSum(CMD_JUDGE) +
                    CheckSum(submission_id_) +
                    CheckSum(problem_id_) +
                    CheckSum(revision_);
    }

    virtual void TearDown() {
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        system(("rm -rf " + root_).c_str());
    }

    void SendCommand() {
        submission_id_ = htonl(submission_id_);
        problem_id_ = htonl(problem_id_);
        revision_ = htonl(revision_);
        checksum_ = htonl(checksum_);
        Writen(fd_[0], &submission_id_, sizeof(submission_id_)); 
        Writen(fd_[0], &problem_id_, sizeof(problem_id_)); 
        Writen(fd_[0], &revision_, sizeof(revision_)); 
        Writen(fd_[0], &checksum_, sizeof(checksum_));
    }

    int Run() {
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        Environment::instance()->set_root(root_);
        int ret = ExecJudgeCommand(fd_[1], &problem_id_output_, &revision_output_);
        ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
        return ret;
    }

    int fd_[2];
    char buf_[32];
    string root_;
    uint32_t submission_id_;
    uint32_t problem_id_;
    uint32_t revision_;
    uint32_t checksum_;
    int problem_id_output_;
    int revision_output_;
};

TEST_F(ExecJudgeCommandTest, ReadCommandFailure) {
    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(0, Readn(fd_[0], buf_, 1));
}

TEST_F(ExecJudgeCommandTest, InvalidCheckSum) {
    checksum_ = 0;
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecJudgeCommandTest, NoSuchProblem) {
    SendCommand();

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(NO_SUCH_PROBLEM, ReadLastUint32(fd_[0]));
}

TEST_F(ExecJudgeCommandTest, Success) {
    ASSERT_EQUAL(0, mkdir("prob", 0700));
    ASSERT_EQUAL(0, mkdir("prob/100", 0700));
    ASSERT_EQUAL(0, mkdir("prob/100/101", 0700));
    SendCommand();

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(READY, ReadLastUint32(fd_[0]));
    ASSERT_EQUAL(100, problem_id_output_);
    ASSERT_EQUAL(101, revision_output_);
}



int ExecCompileCommand(int sock, int* compiler);

class ExecCompileCommandTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, mkdir("script", 0700));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/../../script/compile.sh").c_str(), "script/compile.sh"));
        fd_[0] = fd_[1] = temp_fd_ = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        compiler_id_ = global::COMPILER_LIST[COMPILER_GPP].id;
        source_filename_ = TESTDIR + "/ac.cc";
        struct stat stat; 
        lstat(source_filename_.c_str(), &stat);
        source_file_size_ = stat.st_size;
        checksum_ = CheckSum(CMD_COMPILE) +
                    CheckSum(compiler_id_) +
                    CheckSum(source_file_size_);
        ARG_compiler = "g++";
    }

    virtual void TearDown() {
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        if (temp_fd_ >= 0) {
            close(temp_fd_);
        }
        system(("rm -rf " + root_).c_str());
    }

    void SendCommand() {
        compiler_id_ = htonl(compiler_id_);
        source_file_size_ = htonl(source_file_size_);
        checksum_ = htonl(checksum_);
        Writen(fd_[0], &compiler_id_, sizeof(compiler_id_)); 
        Writen(fd_[0], &source_file_size_, sizeof(source_file_size_));
        Writen(fd_[0], &checksum_, sizeof(checksum_));
        if (!source_filename_.empty()) {
            temp_fd_ = open(source_filename_.c_str(), O_RDONLY);
            ASSERT(temp_fd_ != -1);
            int size = Readn(temp_fd_, buf_, sizeof(buf_));
            Writen(fd_[0], buf_, size);
        }
    }

    int Run() {
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        Environment::instance()->set_root(root_);
        int ret = ExecCompileCommand(fd_[1], &compiler_);
        ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
        return ret;
    }

    int fd_[2];
    int temp_fd_;
    char buf_[1024 * 16];
    string root_;
    uint32_t compiler_id_;
    uint32_t source_file_size_;
    uint32_t checksum_;
    int compiler_;
    string source_filename_;
};

TEST_F(ExecCompileCommandTest, ReadCommandFailure) {
    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(0, Readn(fd_[0], buf_, 1));
}

TEST_F(ExecCompileCommandTest, InvalidCheckSum) {
    checksum_ = 0;
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecCompileCommandTest, InvalidCompiler) {
    compiler_id_ = 255;
    checksum_ = CheckSum(CMD_COMPILE) +
                CheckSum(compiler_id_) +
                CheckSum(source_file_size_);
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecCompileCommandTest, UnsupportedCompiler) {
    ARG_compiler = "";
    checksum_ = CheckSum(CMD_COMPILE) +
                CheckSum(compiler_id_) +
                CheckSum(source_file_size_);
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecCompileCommandTest, SaveFailure) {
    source_filename_ = "";
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INTERNAL_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(ExecCompileCommandTest, Success) {
    SendCommand();

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadLastUint32(fd_[0]));
    ASSERT_EQUAL(0, access("p", F_OK));
}

TEST_F(ExecCompileCommandTest, CompilationError) {
    source_filename_ = TESTDIR + "/ce.cc";
    struct stat stat;
    lstat(source_filename_.c_str(), &stat);
    source_file_size_ = stat.st_size;
    checksum_ = CheckSum(CMD_COMPILE) +
                CheckSum(compiler_id_) +
                CheckSum(source_file_size_);
    SendCommand();

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILATION_ERROR, ReadUint32(fd_[0]));
    int len = ReadUint32(fd_[0]);
    ASSERT(len);
    ASSERT_EQUAL(len, Readn(fd_[0], buf_, len + 1));
}


int ExecTestCaseCommand(int sock, int problem_id, int revision, int compiler, int uid, int gid);

class ExecTestCaseCommandTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, mkdir("prob", 0700));
        ASSERT_EQUAL(0, mkdir("prob/0", 0700));
        ASSERT_EQUAL(0, mkdir("prob/0/0", 0700));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.in").c_str(), "prob/0/0/1.in"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/1.out").c_str(), "prob/0/0/1.out"));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/ac").c_str(), "p"));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        testcase_ = 1;
        time_limit_ = 10;
        memory_limit_ = 1000;
        output_limit_ = 1001;
        checksum_ = CheckSum(CMD_TESTCASE) +
                    CheckSum(testcase_) +
                    CheckSum(time_limit_) +
                    CheckSum(memory_limit_) +
                    CheckSum(output_limit_);
        InstallHandlers();
        problem_id_ = revision_ = 0;
        compiler_ = COMPILER_GPP;
    }

    virtual void TearDown() {
        UninstallHandlers();
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        system(("rm -rf " + root_).c_str());
    }

    void SendCommand() {
        testcase_ = htonl(testcase_);
        time_limit_ = htonl(time_limit_);
        memory_limit_ = htonl(memory_limit_);
        output_limit_ = htonl(output_limit_);
        checksum_ = htonl(checksum_);
        Writen(fd_[0], &testcase_, sizeof(testcase_)); 
        Writen(fd_[0], &time_limit_, sizeof(time_limit_)); 
        Writen(fd_[0], &memory_limit_, sizeof(memory_limit_)); 
        Writen(fd_[0], &output_limit_, sizeof(output_limit_)); 
        Writen(fd_[0], &checksum_, sizeof(checksum_));
    }

    int Run() {
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        Environment::instance()->set_root(root_);
        int ret = ExecTestCaseCommand(fd_[1], problem_id_, revision_, compiler_, 0, 0);
        ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
        return ret;
    }

    int fd_[2];
    char buf_[32];
    string root_;
    uint32_t testcase_;
    uint32_t time_limit_;
    uint32_t memory_limit_;
    uint32_t output_limit_;
    uint32_t checksum_;
    int problem_id_;
    int revision_;
    int compiler_;
};

TEST_F(ExecTestCaseCommandTest, ReadCommandFailure) {
    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(0, Readn(fd_[0], buf_, 1));
}

TEST_F(ExecTestCaseCommandTest, InvalidCheckSum) {
    checksum_ = 0;
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecTestCaseCommandTest, InvalidProblemId) {
    SendCommand();
    problem_id_ = 1;

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecTestCaseCommandTest, InvalidRevision) {
    SendCommand();
    revision_ = 1;

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecTestCaseCommandTest, InvalidTestCase) {
    testcase_ = 2;
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecTestCaseCommandTest, RunFailure) {
    ASSERT_EQUAL(0, unlink("p"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/fpe").c_str(), "p"));
    SendCommand();

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(RUNNING, ReadUint32(fd_[0]));
    ReadUint32(fd_[0]);
    ReadUint32(fd_[0]);
    ASSERT_EQUAL(FLOATING_POINT_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(ExecTestCaseCommandTest, CheckFailure) {
    ASSERT_EQUAL(0, unlink("p"));
    ASSERT_EQUAL(0, symlink((TESTDIR + "/wa").c_str(), "p"));
    SendCommand();

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(RUNNING, ReadUint32(fd_[0]));
    ReadUint32(fd_[0]);
    ReadUint32(fd_[0]);
    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(WRONG_ANSWER, ReadLastUint32(fd_[0]));
}

TEST_F(ExecTestCaseCommandTest, Success) {
    SendCommand();

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(RUNNING, ReadUint32(fd_[0]));
    ReadUint32(fd_[0]);
    ReadUint32(fd_[0]);
    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadLastUint32(fd_[0]));
}

TEST_F(ExecTestCaseCommandTest, ExistingSymlinkInputOutput) {
    ASSERT_EQUAL(0, symlink(".", "input"));
    ASSERT_EQUAL(0, symlink(".", "output"));
    SendCommand();

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(RUNNING, ReadUint32(fd_[0]));
    ReadUint32(fd_[0]);
    ReadUint32(fd_[0]);
    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadLastUint32(fd_[0]));
}



int CheckData(int sock, const string& data_dir);

class CheckDataTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, mkdir("script", 0700));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/../../script/compile.sh").c_str(), "script/compile.sh"));
        ASSERT_EQUAL(0, mkdir("data", 0700));
        ASSERT_EQUAL(0, close(open("data/1.in", O_RDWR | O_CREAT)));
        ASSERT_EQUAL(0, close(open("data/2.in", O_RDWR | O_CREAT)));
        ASSERT_EQUAL(0, close(open("data/3.in", O_RDWR | O_CREAT)));
        ASSERT_EQUAL(0, close(open("data/1.out", O_RDWR | O_CREAT)));
        ASSERT_EQUAL(0, close(open("data/2.out", O_RDWR | O_CREAT)));
        ASSERT_EQUAL(0, close(open("data/3.out", O_RDWR | O_CREAT)));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, pipe(fd_));
        ARG_compiler = "g++";
    }

    virtual void TearDown() {
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        system(("rm -rf " + root_).c_str());
    }

    int Run() {
        Environment::instance()->set_root(root_);
        int ret = CheckData(fd_[1], root_ + "/data");
        close(fd_[1]);
        return ret;
    }

    int fd_[2];
    char buf_[1024 * 16];
    string root_;
};

TEST_F(CheckDataTest, NonExistingDir) {
    ASSERT_EQUAL(0, system("rm -rf data"));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INTERNAL_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, EmptyDir) {
    ASSERT_EQUAL(0, system("rm data/*"));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, NonRegularFile) {
    ASSERT_EQUAL(0, mkdir("data/4.in", 0700));
    ASSERT_EQUAL(0, close(open("data/4.out", O_RDWR | O_CREAT)));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, InvalidName1) {
    ASSERT_EQUAL(0, close(open("data/test", O_RDWR | O_CREAT)));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, InvalidName2) {
    ASSERT_EQUAL(0, close(open("data/a.in", O_RDWR | O_CREAT)));
    ASSERT_EQUAL(0, close(open("data/a.out", O_RDWR | O_CREAT)));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, InvalidName3) {
    ASSERT_EQUAL(0, close(open("data/judge", O_RDWR | O_CREAT)));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, InvalidName4) {
    ASSERT_EQUAL(0, close(open("data/.in", O_RDWR | O_CREAT)));
    ASSERT_EQUAL(0, close(open("data/.out", O_RDWR | O_CREAT)));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, UnsupportedJudgeSourceFileType) {
    ARG_compiler = "";
    ASSERT_EQUAL(0, link((TESTDIR + "/judge.cc").c_str(), "data/judge.cc"));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, JudgeOnly) {
    ASSERT_EQUAL(0, system("rm data/*"));
    ASSERT_EQUAL(0, link((TESTDIR + "/judge.cc").c_str(), "data/judge.cc"));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, UnmatchedTestcase1) {
    ASSERT_EQUAL(0, close(open("data/4.in", O_RDWR | O_CREAT)));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, UnmatchedTestcase2) {
    ASSERT_EQUAL(0, close(open("data/4.out", O_RDWR | O_CREAT)));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, UnmatchedTestcase3) {
    ASSERT_EQUAL(0, unlink("data/1.out"));

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(CheckDataTest, JudgeCompilationError) {
    ASSERT_EQUAL(0, link((TESTDIR + "/ce.cc").c_str(), "data/judge.cc"));

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILATION_ERROR, ReadUint32(fd_[0]));
    int len = ReadUint32(fd_[0]);
    ASSERT(len);
    ASSERT_EQUAL(len, Readn(fd_[0], buf_, len + 1));
}

TEST_F(CheckDataTest, SuccessNoJudge) {
    ASSERT_EQUAL(0, Run());
 
    ASSERT_EQUAL(0, Readn(fd_[0], buf_, 1));
}

TEST_F(CheckDataTest, SuccessHasDataZip) {
    ASSERT_EQUAL(0, close(open("data/data.zip", O_RDWR | O_CREAT)));

    ASSERT_EQUAL(0, Run());
    
    ASSERT_EQUAL(0, Readn(fd_[0], buf_, 1));
}

TEST_F(CheckDataTest, SuccessHasJudge) {
    ASSERT_EQUAL(0, link((TESTDIR + "/judge.cc").c_str(), "data/judge.cc"));

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(COMPILING, ReadLastUint32(fd_[0]));
}


int ExecDataCommand(int sock, unsigned int problem_id, unsigned int revision);

class ExecDataCommandTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0700));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, mkdir("script", 0700));
        ASSERT_EQUAL(0, mkdir("prob", 0700));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/../../script/compile.sh").c_str(), "script/compile.sh"));
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        ARG_compiler = "g++";
        data_filename_ = TESTDIR + "/data.zip";
        struct stat stat; 
        lstat(data_filename_.c_str(), &stat);
        data_file_size_ = stat.st_size;
        checksum_ = CheckSum(CMD_DATA) + CheckSum(data_file_size_);
    }

    virtual void TearDown() {
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        if (temp_fd_ >= 0) {
            close(temp_fd_);
        }
        system(("rm -rf " + root_).c_str());
    }

    void SendCommand() {
        data_file_size_ = htonl(data_file_size_);
        checksum_ = htonl(checksum_);
        Writen(fd_[0], &data_file_size_, sizeof(data_file_size_));
        Writen(fd_[0], &checksum_, sizeof(checksum_));
        if (!data_filename_.empty()) {
            temp_fd_ = open(data_filename_.c_str(), O_RDONLY);
            ASSERT(temp_fd_ != -1);
            int size = Readn(temp_fd_, buf_, sizeof(buf_));
            Writen(fd_[0], buf_, size);
        }
    }

    int Run() {
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        Environment::instance()->set_root(root_);
        int ret = ExecDataCommand(fd_[1], 0, 0);
        ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
        return ret;
    }

    int fd_[2];
    char buf_[1024 * 16];
    int temp_fd_;
    string root_;
    uint32_t data_file_size_;
    uint32_t checksum_;
    string data_filename_;
};

TEST_F(ExecDataCommandTest, ReadCommandFailure) {
    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(0, Readn(fd_[0], buf_, 1));
}

TEST_F(ExecDataCommandTest, InvalidCheckSum) {
    checksum_ = 0;
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecDataCommandTest, MaxFileSizePlusOne) {
    data_file_size_ = MAX_DATA_FILE_SIZE + 1;
    checksum_ = CheckSum(CMD_DATA) + CheckSum(data_file_size_);
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecDataCommandTest, CannotCreateProblemDir) {
    ASSERT_EQUAL(0, rmdir("prob"));
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INTERNAL_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(ExecDataCommandTest, CannotCreateTempDir) {
    ASSERT_EQUAL(0, rmdir("prob"));
    ASSERT_EQUAL(0, close(open("prob", O_RDWR | O_CREAT)));
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(INTERNAL_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(ExecDataCommandTest, CannotSaveFile) {
    data_filename_ = "";
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INTERNAL_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(ExecDataCommandTest, CannotUnzip) {
    data_filename_ = TESTDIR + "/1.in";
    struct stat stat; 
    lstat(data_filename_.c_str(), &stat);
    data_file_size_ = stat.st_size;
    checksum_ = CheckSum(CMD_DATA) + CheckSum(data_file_size_);
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecDataCommandTest, CheckDataFailure) {
    data_filename_ = TESTDIR + "/data_empty.zip";
    struct stat stat; 
    lstat(data_filename_.c_str(), &stat);
    data_file_size_ = stat.st_size;
    checksum_ = CheckSum(CMD_DATA) + CheckSum(data_file_size_);
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(ExecDataCommandTest, RenameFailureDirectoryExists) {
    ASSERT_EQUAL(0, mkdir("prob/0", 0700));
    ASSERT_EQUAL(0, mkdir("prob/0/0", 0700));
    SendCommand();

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadLastUint32(fd_[0]));
}

TEST_F(ExecDataCommandTest, RenameFailureOther) {
    ASSERT_EQUAL(0, mkdir("prob/0", 0700));
    ASSERT_EQUAL(0, symlink("prob", "prob/0/0"));
    SendCommand();

    ASSERT_EQUAL(-1, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INTERNAL_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(ExecDataCommandTest, Success) {
    SendCommand();

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadLastUint32(fd_[0]));
}


int JudgeMain(int sock, int uid, int gid);

class JudgeMainTest: public TestFixture {
  protected:
    virtual void SetUp() {
        root_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(root_.c_str(), 0755));
        ASSERT_EQUAL(0, chdir(root_.c_str()));
        ASSERT_EQUAL(0, mkdir("working", 0755));
        ASSERT_EQUAL(0, mkdir("script", 0750));
        ASSERT_EQUAL(0, mkdir("prob", 0750));
        ASSERT_EQUAL(0, symlink((TESTDIR + "/../../script/compile.sh").c_str(), "script/compile.sh"));
        temp_fd_ = fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        buf_size_ = 0;
        global::terminated = false;
        ARG_compiler = "g++";
    }

    virtual void TearDown() {
        UninstallHandlers();
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
        if (temp_fd_ >= 0) {
            close(temp_fd_);
        }
        system(("rm -rf " + root_).c_str());
    }

    void AppendCheckSum() {
        uint32_t checksum = 0;
        for (int i = 0; i < buf_size_; ++i) {
            checksum += (unsigned char)buf_[i];
        }
        checksum = htonl(checksum);
        *(uint32_t*)(buf_ + buf_size_) = checksum;
        buf_size_ += sizeof(uint32_t);
    }

    void AppendUint32(uint32_t value) {
        value = htonl(value);
        *(uint32_t*)(buf_ + buf_size_) = value;
        buf_size_ += sizeof(uint32_t);
    }

    void AppendFile(const string& filename, int size) {
        if (temp_fd_ >= 0) {
            close(temp_fd_);
        }
        temp_fd_ = open(filename.c_str(), O_RDONLY);
        ASSERT(temp_fd_ >= 0);
        ASSERT_EQUAL(size, Readn(temp_fd_, buf_ + buf_size_, size));
        buf_size_ += size;
    }

    void SendCommand() {
        Writen(fd_[0], buf_, buf_size_);
    }

    void SendPingCommand() {
        buf_size_ = 0;
        AppendUint32(CMD_PING);
        SendCommand();
    }

    void SendJudgeCommand(int problem, int revision) {
        buf_size_ = 0;
        AppendUint32(CMD_JUDGE);
        AppendUint32(0);
        AppendUint32(problem);
        AppendUint32(revision);
        AppendCheckSum();
        SendCommand();
    }

    void SendCompileCommand(const string& source_filename) {
        struct stat stat; 
        lstat(source_filename.c_str(), &stat);
        int source_file_size = stat.st_size;
        buf_size_ = 0;
        AppendUint32(CMD_COMPILE);
        AppendUint32(global::COMPILER_LIST[COMPILER_GPP].id);
        AppendUint32(source_file_size);
        AppendCheckSum();
        AppendFile(source_filename, source_file_size);
        SendCommand();
    }

    void SendTestCaseCommand(int testcase, int time_limit, int memory_limit, int output_limit) {
        buf_size_ = 0;
        AppendUint32(CMD_TESTCASE);
        AppendUint32(testcase);
        AppendUint32(time_limit);
        AppendUint32(memory_limit);
        AppendUint32(output_limit);
        AppendCheckSum();
        SendCommand();
    }

    void SendDataCommand(const string& data_filename) {
        struct stat stat; 
        lstat(data_filename.c_str(), &stat);
        int data_file_size = stat.st_size;
        buf_size_ = 0;
        AppendUint32(CMD_DATA);
        AppendUint32(data_file_size);
        AppendCheckSum();
        AppendFile(data_filename, data_file_size);
        SendCommand();
    }

    void SendUint32(uint32_t command) {
        command = htonl(command);
        Writen(fd_[0], &command, sizeof(command));
    }

    int Run() {
        ASSERT_EQUAL(0, shutdown(fd_[0], SHUT_WR));
        Environment::instance()->set_root(root_);
        return JudgeMain(fd_[1], 0, 0);
    }

    int fd_[2];
    int temp_fd_;
    int port_;
    string address_;
    string root_;
    char buf_[1024 * 16];
    int buf_size_;
};

TEST_F(JudgeMainTest, CannotCreateWorkingDir) {
    ASSERT_EQUAL(0, rmdir("working"));

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(INTERNAL_ERROR, ReadLastUint32(fd_[0]));
}

TEST_F(JudgeMainTest, SIGTERM) {
    global::terminated = true;

    ASSERT_EQUAL(0, Run());

    ASSERT_EQUAL(-1, Readn(fd_[0], buf_, 1));
}

TEST_F(JudgeMainTest, ReadCommandFailure) {
    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(1, Readn(fd_[0], buf_, 1));
}

TEST_F(JudgeMainTest, InvalidCommand) {
    SendUint32(0);

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(JudgeMainTest, FirstCommandData) {
    SendUint32(CMD_DATA);

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(JudgeMainTest, FirstCommandTestCase) {
    SendUint32(CMD_TESTCASE);

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(JudgeMainTest, MultipleData) {
    SendJudgeCommand(0, 0);
    SendDataCommand(TESTDIR + "/data.zip");
    SendUint32(CMD_DATA);

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(NO_SUCH_PROBLEM, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(JudgeMainTest, TestBeforeDataSynchirnized) {
    SendJudgeCommand(0, 0);
    SendCompileCommand(TESTDIR + "/ac.cc");
    SendUint32(CMD_TESTCASE);

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(NO_SUCH_PROBLEM, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(JudgeMainTest, TestBeforeCompiled) {
    SendJudgeCommand(0, 0);
    SendDataCommand(TESTDIR + "/data.zip");
    SendUint32(CMD_TESTCASE);

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(NO_SUCH_PROBLEM, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(JudgeMainTest, UnnecessaryData) {
    SendJudgeCommand(0, 0);
    SendDataCommand(TESTDIR + "/data.zip");
    SendJudgeCommand(0, 0);
    SendUint32(CMD_DATA);

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(NO_SUCH_PROBLEM, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(INVALID_INPUT, ReadLastUint32(fd_[0]));
}

TEST_F(JudgeMainTest, Ping) {
    SendPingCommand();

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
}

TEST_F(JudgeMainTest, Success) {
    SendPingCommand();
    SendJudgeCommand(0, 0);
    SendDataCommand(TESTDIR + "/data.zip");
    SendPingCommand();
    SendCompileCommand(TESTDIR + "/ac.cc");
    SendTestCaseCommand(1, 10, 1000, 1000);
    SendTestCaseCommand(3, 10, 1000, 1000);
    SendTestCaseCommand(2, 10, 1000, 1000);
    SendPingCommand();
    SendTestCaseCommand(1, 10, 1000, 1000);
    SendPingCommand();
    SendCompileCommand(TESTDIR + "/ce.cc");
    SendJudgeCommand(0, 0);
    SendCompileCommand(TESTDIR + "/wa.cc");
    SendTestCaseCommand(3, 10, 1000, 1000);

    ASSERT_EQUAL(1, Run());

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));

    ASSERT_EQUAL(NO_SUCH_PROBLEM, ReadUint32(fd_[0]));

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    
    ASSERT_EQUAL(RUNNING, ReadUint32(fd_[0]));
    ReadUint32(fd_[0]);
    ReadUint32(fd_[0]);
    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadUint32(fd_[0]));

    ASSERT_EQUAL(RUNNING, ReadUint32(fd_[0]));
    ReadUint32(fd_[0]);
    ReadUint32(fd_[0]);
    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadUint32(fd_[0]));

    ASSERT_EQUAL(RUNNING, ReadUint32(fd_[0]));
    ReadUint32(fd_[0]);
    ReadUint32(fd_[0]);
    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadUint32(fd_[0]));

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));

    ASSERT_EQUAL(RUNNING, ReadUint32(fd_[0]));
    ReadUint32(fd_[0]);
    ReadUint32(fd_[0]);
    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(ACCEPTED, ReadUint32(fd_[0]));

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILATION_ERROR, ReadUint32(fd_[0]));
    int len = ReadUint32(fd_[0]);
    ASSERT_EQUAL(len, Readn(fd_[0], buf_, len));

    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));
    ASSERT_EQUAL(COMPILING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(READY, ReadUint32(fd_[0]));

    ASSERT_EQUAL(RUNNING, ReadUint32(fd_[0]));
    ReadUint32(fd_[0]);
    ReadUint32(fd_[0]);
    ASSERT_EQUAL(JUDGING, ReadUint32(fd_[0]));
    ASSERT_EQUAL(WRONG_ANSWER, ReadUint32(fd_[0]));
}
