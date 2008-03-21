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

#include <stdio.h>

#include <arpa/inet.h>
#include <fcntl.h>

#include "args.h"
#include "judge_result.h"
#include "main.h"
#include "trace.h"
#include "util.h"

DECLARE_ARG(string, root);
DECLARE_ARG(string, lang);

bool isSupportedSourceFileType(const string& sourceFileType);

class IsSupportedSourceFileTypeTest : public TestFixture {
};

TEST_F(IsSupportedSourceFileTypeTest, NoneSupported) {
    ARG_lang = "";
    ASSERT(!isSupportedSourceFileType("cc"));
}

TEST_F(IsSupportedSourceFileTypeTest, OneSupported) {
    ARG_lang = "cc";
    ASSERT(isSupportedSourceFileType("cc"));
    ASSERT(!isSupportedSourceFileType("c"));
}

TEST_F(IsSupportedSourceFileTypeTest, TwoSupported) {
    ARG_lang = "cc,c";
    ASSERT(isSupportedSourceFileType("cc"));
    ASSERT(isSupportedSourceFileType("c"));
}

TEST_F(IsSupportedSourceFileTypeTest, ThreeSupported) {
    ARG_lang = "pas,cc,c";
    ASSERT(isSupportedSourceFileType("pas"));
    ASSERT(isSupportedSourceFileType("cc"));
    ASSERT(isSupportedSourceFileType("c"));
}

const char* createHeader(char sourceFileType,
                         unsigned int problemId,
                         unsigned int version) {
    static char header[HEADER_SIZE];
    header[0] = sourceFileType;
    problemId = htonl(problemId);
    memcpy(header + 1, &problemId, sizeof(problemId));
    version = htonl(version);
    memcpy(header + 1 + sizeof(problemId), &version, sizeof(version));
    return header;
}

int readHeader(int fdSocket,
               string* sourceFileType,
               unsigned int* problemId,
               unsigned int* version);

class ReadHeaderTest : public TestFixture {
  protected:
    void setUp() {
        fp_ = tmpfile();
        fd_ = fileno(fp_);
    }

    void tearDown() {
        fclose(fp_);
    }

    FILE* fp_;
    int fd_;
    string sourceFileType_;
    unsigned int problemId_;
    unsigned int version_;
    char buf_[32];
};

TEST_F(ReadHeaderTest, InvalidHeader) {
    ASSERT_EQUAL(-1, readHeader(fd_, &sourceFileType_,
                                &problemId_, &version_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadHeaderTest, InvalidSourceFileTypeZero) {
    write(fd_, createHeader(0, 0, 0), HEADER_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, readHeader(fd_, &sourceFileType_,
                                &problemId_, &version_));
    lseek(fd_, HEADER_SIZE, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_SOURCE_FILE_TYPE, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(HEADER_SIZE + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadHeaderTest, InvalidSourceFileTypeMaxPlusOne) {
    write(fd_, createHeader(7, 0, 0), HEADER_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, readHeader(fd_, &sourceFileType_,
                                &problemId_, &version_));
    lseek(fd_, HEADER_SIZE, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_SOURCE_FILE_TYPE, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(HEADER_SIZE + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadHeaderTest, UnsupportedSourceFileType) {
    ARG_lang = "";
    write(fd_, createHeader(1, 0, 0), HEADER_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, readHeader(fd_, &sourceFileType_,
                                &problemId_, &version_));
    lseek(fd_, HEADER_SIZE, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(UNSUPPORTED_SOURCE_FILE_TYPE, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(HEADER_SIZE + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadHeaderTest, ValidHeaderSourceFileTypeOne) {
    ARG_lang = "cc";
    write(fd_, createHeader(1, 0, 0), HEADER_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, readHeader(fd_, &sourceFileType_,
                               &problemId_, &version_));
    ASSERT_EQUAL((off_t)HEADER_SIZE, lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadHeaderTest, ValidHeaderSourceFileTypeMax) {
    ARG_lang = "cs";
    write(fd_, createHeader(6, 0, 0), HEADER_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, readHeader(fd_, &sourceFileType_,
                               &problemId_, &version_));
    ASSERT_EQUAL((off_t)HEADER_SIZE, lseek(fd_, 0, SEEK_END));
}

const char* createTestcase(unsigned int testcase,
                           unsigned short timeLimit,
                           unsigned int memoryLimit,
                           unsigned short outputLimit) {
    static char message[TESTCASE_MSG_SIZE];
    message[0] = testcase;
    timeLimit = htons(timeLimit);
    memcpy(message + 1, &timeLimit, sizeof(timeLimit));
    memoryLimit = htonl(memoryLimit);
    memcpy(message + 3, &memoryLimit, sizeof(memoryLimit));
    outputLimit = htons(outputLimit);
    memcpy(message + 7, &outputLimit, sizeof(outputLimit));
    return message;
}

class ReadTestcaseTest : public TestFixture {
  protected:
    void setUp() {
        fp_ = tmpfile();
        fd_ = fileno(fp_);
    }

    void tearDown() {
        fclose(fp_);
    }

    FILE* fp_;
    int fd_;
    unsigned int testcase_;
    unsigned int timeLimit_;
    unsigned int memoryLimit_;
    unsigned int outputLimit_;
    char buf_[32];
};

TEST_F(ReadTestcaseTest, InvalidTestcase) {
    ASSERT_EQUAL(-1, readTestcase(fd_, &testcase_, &timeLimit_,
                                  &memoryLimit_, &outputLimit_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadTestcaseTest, InvalidTimeLimitZero) {
    write(fd_, createTestcase(0, 0, 1, 1), TESTCASE_MSG_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, readTestcase(fd_, &testcase_, &timeLimit_,
                                  &memoryLimit_, &outputLimit_));
    lseek(fd_, TESTCASE_MSG_SIZE, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_TIME_LIMIT, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(TESTCASE_MSG_SIZE + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadTestcaseTest, InvalidTimeLimitMaxPlusOne) {
    write(fd_, createTestcase(0, MAX_TIME_LIMIT + 1, 1, 1), TESTCASE_MSG_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, readTestcase(fd_, &testcase_, &timeLimit_,
                                  &memoryLimit_, &outputLimit_));
    lseek(fd_, TESTCASE_MSG_SIZE, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_TIME_LIMIT, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(TESTCASE_MSG_SIZE + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadTestcaseTest, InvalidMemoryLimitZero) {
    write(fd_, createTestcase(0, 1, 0, 1), TESTCASE_MSG_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, readTestcase(fd_, &testcase_, &timeLimit_,
                                  &memoryLimit_, &outputLimit_));
    lseek(fd_, TESTCASE_MSG_SIZE, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_MEMORY_LIMIT, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(TESTCASE_MSG_SIZE + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadTestcaseTest, InvalidMemoryLimitMaxPlusOne) {
    write(fd_, createTestcase(0, 1, MAX_MEMORY_LIMIT + 1, 1),
          TESTCASE_MSG_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, readTestcase(fd_, &testcase_, &timeLimit_,
                                  &memoryLimit_, &outputLimit_));
    lseek(fd_, TESTCASE_MSG_SIZE, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_MEMORY_LIMIT, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(TESTCASE_MSG_SIZE + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadTestcaseTest, InvalidOutputLimitZero) {
    write(fd_, createTestcase(0, 1, 1, 0), TESTCASE_MSG_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, readTestcase(fd_, &testcase_, &timeLimit_,
                                  &memoryLimit_, &outputLimit_));
    lseek(fd_, TESTCASE_MSG_SIZE, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_OUTPUT_LIMIT, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(TESTCASE_MSG_SIZE + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadTestcaseTest, InvalidOutputLimitMaxPlusOne) {
    write(fd_, createTestcase(0, 1, 1, MAX_OUTPUT_LIMIT + 1),
          TESTCASE_MSG_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, readTestcase(fd_, &testcase_, &timeLimit_,
                                  &memoryLimit_, &outputLimit_));
    lseek(fd_, TESTCASE_MSG_SIZE, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_OUTPUT_LIMIT, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(TESTCASE_MSG_SIZE + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadTestcaseTest, ValidTestcaseAllLimitOne) {
    write(fd_, createTestcase(0, 1, 1, 1),
          TESTCASE_MSG_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, readTestcase(fd_, &testcase_, &timeLimit_,
                                 &memoryLimit_, &outputLimit_));
    ASSERT_EQUAL((off_t)TESTCASE_MSG_SIZE, lseek(fd_, 0, SEEK_END));
}

TEST_F(ReadTestcaseTest, ValidTestcaseAllLimitMax) {
    write(fd_,
          createTestcase(0, MAX_TIME_LIMIT, MAX_MEMORY_LIMIT, MAX_OUTPUT_LIMIT),
          TESTCASE_MSG_SIZE);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, readTestcase(fd_, &testcase_, &timeLimit_,
                                 &memoryLimit_, &outputLimit_));
    ASSERT_EQUAL((off_t)TESTCASE_MSG_SIZE, lseek(fd_, 0, SEEK_END));
}

class SaveFileTest : public TestFixture {
  protected:
    void setUp() {
        fp_ = tmpfile();
        fd_ = fileno(fp_);
        fn_ = tmpnam(NULL);
        f_ = 0;
    }

    void tearDown() {
        fclose(fp_);
        if (f_) {
            close(f_);
        }
        unlink(fn_);
    }

    FILE* fp_;
    int fd_;
    int f_;
    const char* fn_;
    char buf_[32];
};

TEST_F(SaveFileTest, CannotCreateFile) {
    fn_ = "";
    ASSERT_EQUAL(-1, saveFile(fd_, fn_, 0));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveFileTest, InvalidData) {
    ASSERT_EQUAL(-1, saveFile(fd_, fn_, 1));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveFileTest, Normal) {
    write(fd_, "test", 4);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, saveFile(fd_, fn_, 4));
    ASSERT_EQUAL((off_t)4, lseek(fd_, 0, SEEK_END));
    f_ = open(fn_, O_RDWR);
    ASSERT(f_);
    ASSERT_EQUAL((ssize_t)4, read(f_, buf_, 4));
    ASSERT_EQUAL((off_t)4, lseek(f_, 0, SEEK_END));
    ASSERT_EQUAL(0, strncmp(buf_, "test", 4));
}

TEST_F(SaveFileTest, LargeFile) {
    char buf[16384];
    for (int i = 0; i < sizeof(buf); ++i) {
        buf[i] = 'a' + i % 26;
    }
    write(fd_, buf, sizeof(buf));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, saveFile(fd_, fn_, sizeof(buf)));
    ASSERT_EQUAL((off_t)sizeof(buf), lseek(fd_, 0, SEEK_END));
    f_ = open(fn_, O_RDWR);
    ASSERT(f_);
    ASSERT_EQUAL((ssize_t)sizeof(buf), read(f_, buf, sizeof(buf)));
    ASSERT_EQUAL((off_t)sizeof(buf), lseek(f_, 0, SEEK_END));
    for (int i = 0; i < sizeof(buf); ++i) {
        ASSERT_EQUAL(char('a' + i % 26), buf[i]);
    }
}

class SaveSourceFileTest : public TestFixture {
  protected:
    void setUp() {
        fp_ = tmpfile();
        fd_ = fileno(fp_);
        fn_ = tmpnam(NULL);
        f_ = 0;
    }

    void tearDown() {
        fclose(fp_);
        if (f_) {
            close(f_);
        }
        unlink(fn_);
    }

    FILE* fp_;
    int fd_;
    int f_;
    const char* fn_;
    char buf_[32];
};

TEST_F(SaveSourceFileTest, InvalidDataSize) {
    ASSERT_EQUAL(-1, saveSourceFile(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA_SIZE, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveSourceFileTest, InvalidSize) {
    unsigned short size = htons(1);
    write(fd_, &size, sizeof(size));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, saveSourceFile(fd_, fn_));
    lseek(fd_, sizeof(size), SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(sizeof(size) + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveSourceFileTest, Normal) {
    unsigned short size = htons(4);
    write(fd_, &size, sizeof(size));
    write(fd_, "test", 4);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, saveSourceFile(fd_, fn_));
    ASSERT_EQUAL((off_t)6, lseek(fd_, 0, SEEK_END));
    f_ = open(fn_, O_RDWR);
    ASSERT(f_);
    ASSERT_EQUAL((ssize_t)4, read(f_, buf_, 4));
    ASSERT_EQUAL((off_t)4, lseek(f_, 0, SEEK_END));
    ASSERT_EQUAL(0, strncmp(buf_, "test", 4));
}

class CheckDataTest : public TestFixture {
  protected:
    void setUp() {
        fp_ = tmpfile();
        fd_ = fileno(fp_);
        fn_ = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(fn_, 0700));
        close(open(StringPrintf("%s/3.in", fn_).c_str(), O_RDWR | O_CREAT));
        close(open(StringPrintf("%s/1.in", fn_).c_str(), O_RDWR | O_CREAT));
        close(open(StringPrintf("%s/2.in", fn_).c_str(), O_RDWR | O_CREAT));
        close(open(StringPrintf("%s/2.out", fn_).c_str(), O_RDWR | O_CREAT));
        close(open(StringPrintf("%s/3.out", fn_).c_str(), O_RDWR | O_CREAT));
        close(open(StringPrintf("%s/1.out", fn_).c_str(), O_RDWR | O_CREAT));
    }

    void tearDown() {
        fclose(fp_);
        system(StringPrintf("rm -rf %s", fn_).c_str());
    }

    FILE* fp_;
    int fd_;
    const char* fn_;
    char buf_[4096];
};

TEST_F(CheckDataTest, NonExistingDir) {
    system(StringPrintf("rm -rf %s", fn_).c_str());
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, EmptyDir) {
    system(StringPrintf("rm -f %s/*", fn_).c_str());
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, NonRegularFile) {
    mkdir(StringPrintf("%s/4.in", fn_).c_str(), 0700);
    mkdir(StringPrintf("%s/4.out", fn_).c_str(), 0700);
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, InvalidName1) {
    close(open(StringPrintf("%s/test", fn_).c_str(), O_RDWR | O_CREAT));
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, InvalidName2) {
    close(open(StringPrintf("%s/a.in", fn_).c_str(), O_RDWR | O_CREAT));
    close(open(StringPrintf("%s/a.out", fn_).c_str(), O_RDWR | O_CREAT));
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, InvalidName3) {
    close(open(StringPrintf("%s/judge", fn_).c_str(), O_RDWR | O_CREAT));
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, InvalidName4) {
    close(open(StringPrintf("%s/.in", fn_).c_str(), O_RDWR | O_CREAT));
    close(open(StringPrintf("%s/.out", fn_).c_str(), O_RDWR | O_CREAT));
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, UnsupportedJudgeSourceFileType) {
    system(StringPrintf("cp %s %s", TESTDIR "/judge.cc", fn_).c_str());
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, JudgeOnly) {
    system(StringPrintf("rm -f %s/*", fn_).c_str());
    system(StringPrintf("cp %s %s", TESTDIR "/judge.cc", fn_).c_str());
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, UnmatchedTestcase1) {
    close(open(StringPrintf("%s/4.in", fn_).c_str(), O_RDWR | O_CREAT));
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, UnmatchedTestcase2) {
    close(open(StringPrintf("%s/4.out", fn_).c_str(), O_RDWR | O_CREAT));
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, UnmatchedTestcase3) {
    unlink(StringPrintf("%s/3.in", fn_).c_str());
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, UnmatchedTestcase4) {
    unlink(StringPrintf("%s/3.out", fn_).c_str());
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, UnmatchedTestcase5) {
    unlink(StringPrintf("%s/1.out", fn_).c_str());
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, CompilationErrorJudge) {
    ARG_root = "..";
    ARG_lang = "cc";
    system(StringPrintf("cp %s %s/judge.cc", TESTDIR "/ce.cc", fn_).c_str());
    ASSERT_EQUAL(-1, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)4, read(fd_, buf_, 4));
    ASSERT_EQUAL(COMPILING, (int)buf_[0]);
    ASSERT_EQUAL(COMPILATION_ERROR, (int)buf_[1]);
    int len = ntohs(*(uint16_t*)(buf_ + 2));
    ASSERT(len);
    ASSERT_EQUAL((ssize_t)len, read(fd_, buf_, len));
    off_t pos = lseek(fd_, 0, SEEK_CUR);
    ASSERT_EQUAL(pos, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, NormalNoJudge) {
    ASSERT_EQUAL(0, checkData(fd_, fn_));
    ASSERT_EQUAL((off_t)0, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, NormalWithDataZip) {
    close(open(StringPrintf("%s/data.zip", fn_).c_str(), O_RDWR | O_CREAT));
    ASSERT_EQUAL(0, checkData(fd_, fn_));
    ASSERT_EQUAL((off_t)0, lseek(fd_, 0, SEEK_END));
}

TEST_F(CheckDataTest, NormalWithJudge) {
    ARG_root = "..";
    ARG_lang = "cc";
    system(StringPrintf("cp %s %s", TESTDIR "/judge.cc", fn_).c_str());
    ASSERT_EQUAL(0, checkData(fd_, fn_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1));
    ASSERT_EQUAL(COMPILING, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

class SaveDataTest : public TestFixture {
  protected:
    void setUp() {
        fp_ = tmpfile();
        fd_ = fileno(fp_);
        ARG_root = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(ARG_root.c_str(), 0700));
        ASSERT_EQUAL(0, mkdir((ARG_root + "/prob").c_str(), 0700));
        ASSERT_EQUAL(0, mkdir((ARG_root + "/script").c_str(), 0700));
        ASSERT_EQUAL(0, link("../script/compile.sh", 
                             (ARG_root + "/script/compile.sh").c_str()));
    }

    void tearDown() {
        fclose(fp_);
        system(StringPrintf("rm -rf %s", ARG_root.c_str()).c_str());
    }

    FILE* fp_;
    int fd_;
    const char* fn_;
    char buf_[4096];
};

TEST_F(SaveDataTest, CannotCreateProblemDir) {
    ASSERT_EQUAL(0, rmdir((ARG_root + "/prob").c_str()));
    ASSERT_EQUAL(-1, saveData(fd_, 0, 0));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveDataTest, CannotCreateTempDir) {
    ASSERT_EQUAL(0, rmdir((ARG_root + "/prob").c_str()));
    ASSERT_EQUAL(0, close(open((ARG_root + "/prob").c_str(),
                               O_RDWR | O_CREAT)));
    ASSERT_EQUAL(-1, saveData(fd_, 0, 0));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveDataTest, InvalidDataSize) {
    ASSERT_EQUAL(-1, saveData(fd_, 0, 0));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA_SIZE, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveDataTest, MaxFileSizePlusOne) {
    unsigned long size = ntohl(MAX_DATA_FILE_SIZE + 1);
    write(fd_, &size, sizeof(size));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, saveData(fd_, 0, 0));
    lseek(fd_, sizeof(size), SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA_SIZE, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(sizeof(size) + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveDataTest, CannotSaveFile) {
    unsigned long size = ntohl(1);
    write(fd_, &size, sizeof(size));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, saveData(fd_, 0, 0));
    lseek(fd_, sizeof(size), SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(sizeof(size) + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveDataTest, CannotUnzip) {
    unsigned long size = ntohl(1);
    write(fd_, &size, sizeof(size));
    write(fd_, "\0", 1);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, saveData(fd_, 0, 0));
    lseek(fd_, sizeof(size) + 1, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(sizeof(size) + 2), lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveDataTest, CannotRename) {
    int fd = open(TESTDIR "/data.zip", O_RDONLY);
    unsigned long size = lseek(fd, 0, SEEK_END);
    lseek(fd, 0, SEEK_SET);
    read(fd, buf_, size);
    close(fd);
    size = htonl(size);
    write(fd_, &size, sizeof(size));
    size = ntohl(size);
    write(fd_, buf_, size);
    mkdir((ARG_root + "/prob/0").c_str(), 0700);
    mkdir((ARG_root + "/prob/0/0").c_str(), 0700);
    mkdir((ARG_root + "/prob/0/0/0").c_str(), 0700);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, saveData(fd_, 0, 0));
    lseek(fd_, sizeof(size) + size, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2)); 
    ASSERT_EQUAL(COMPILING, (int)buf_[0]);
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[1]);
    ASSERT_EQUAL((off_t)(sizeof(size) + size + 2), lseek(fd_, 0, SEEK_END));
}

TEST_F(SaveDataTest, Normal) {
    int fd = open(TESTDIR "/data.zip", O_RDONLY);
    unsigned long size = lseek(fd, 0, SEEK_END);
    lseek(fd, 0, SEEK_SET);
    read(fd, buf_, size);
    close(fd);
    size = htonl(size);
    write(fd_, &size, sizeof(size));
    size = ntohl(size);
    write(fd_, buf_, size);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, saveData(fd_, 0, 0));
    lseek(fd_, sizeof(size) + size, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2)); 
    ASSERT_EQUAL(COMPILING, (int)buf_[0]);
    ASSERT_EQUAL(READY, (int)buf_[1]);
    ASSERT_EQUAL((off_t)(sizeof(size) + size + 2), lseek(fd_, 0, SEEK_END));
}

class ProcessTest : public TestFixture {
  protected:
    void setUp() {
        fp_ = tmpfile();
        fd_ = fileno(fp_);
        ARG_root = tmpnam(NULL);
        ASSERT_EQUAL(0, mkdir(ARG_root.c_str(), 0700));
        ASSERT_EQUAL(0, mkdir((ARG_root + "/prob").c_str(), 0700));
        ASSERT_EQUAL(0, mkdir((ARG_root + "/prob/0").c_str(), 0700));
        ASSERT_EQUAL(0, mkdir((ARG_root + "/prob/0/0").c_str(), 0700));
        ASSERT_EQUAL(0, link(TESTDIR "/1.in",
                             (ARG_root + "/prob/0/0/1.in").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/1.out",
                             (ARG_root + "/prob/0/0/1.out").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/2.in",
                             (ARG_root + "/prob/0/0/2.in").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/2.out",
                             (ARG_root + "/prob/0/0/2.out").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/3.in",
                             (ARG_root + "/prob/0/0/3.in").c_str()));
        ASSERT_EQUAL(0, link(TESTDIR "/3.out",
                             (ARG_root + "/prob/0/0/3.out").c_str()));
        ASSERT_EQUAL(0, mkdir((ARG_root + "/working").c_str(), 0700));
        ASSERT_EQUAL(0, mkdir(StringPrintf("%s/working/%d",
                                           ARG_root.c_str(),
                                           getpid()).c_str(),
                              0700));
        ASSERT_EQUAL(0, mkdir((ARG_root + "/script").c_str(), 0700));
        ASSERT_EQUAL(0, link("../script/compile.sh", 
                             (ARG_root + "/script/compile.sh").c_str()));
        ARG_lang = "cc";
        source_file_ = "ac.cc";
        data_file_ = "data.zip";
        installHandlers();
    }

    void tearDown() {
        uninstallHandlers();
        fclose(fp_);
        system(StringPrintf("rm -rf %s", ARG_root.c_str()).c_str());
    }

    void writeHeader() {
        write(fd_, createHeader(1, 0, 0), HEADER_SIZE);
        header_end_position_ = HEADER_SIZE;
    }

    void writeSourceFile() {
        int fd = open((TESTDIR "/" + source_file_).c_str(), O_RDONLY);
        unsigned short size = lseek(fd, 0, SEEK_END);
        lseek(fd, 0, SEEK_SET);
        read(fd, buf_, size);
        close(fd);
        size = htons(size);
        write(fd_, &size, sizeof(size));
        size = ntohs(size);
        write(fd_, buf_, size);
        source_end_position_ = header_end_position_ + sizeof(size) + size;
        data_end_position_ = source_end_position_;
    }

    void writeData() {
        int fd = open((TESTDIR "/" + data_file_).c_str(), O_RDONLY);
        unsigned long size = lseek(fd, 0, SEEK_END);
        lseek(fd, 0, SEEK_SET);
        read(fd, buf_, size);
        close(fd);
        size = htonl(size);
        write(fd_, buf_, padding_);
        write(fd_, &size, sizeof(size));
        size = ntohl(size);
        write(fd_, buf_, size);
        data_end_position_ = source_end_position_ + padding_ + sizeof(size) + size;
    }

    void writeTestcase(int testcase) {
        if (testcase_end_positions_.empty()) {
            testcase_end_positions_.push_back(data_end_position_);
        } else {
            testcase_end_positions_.push_back(testcase_end_positions_.back());
        }
        write(fd_, buf_, padding_);
        write(fd_, createTestcase(testcase, 10, 1000, 1000), TESTCASE_MSG_SIZE);
        testcase_end_positions_.back() += TESTCASE_MSG_SIZE + padding_;
    }

    FILE* fp_;
    int fd_;
    const char* fn_;
    char buf_[4096];
    int header_end_position_;
    int source_end_position_;
    int data_end_position_;
    int padding_;
    vector<int> testcase_end_positions_;
    string source_file_;
    string data_file_;
};

TEST_F(ProcessTest, InvalidHeader) {
    ASSERT_EQUAL(-1, process(fd_));
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[0]);
    ASSERT_EQUAL((off_t)1, lseek(fd_, 0, SEEK_END));
}

TEST_F(ProcessTest, InvalidSourceFile) {
    writeHeader();
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, process(fd_));
    lseek(fd_, header_end_position_, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INVALID_DATA_SIZE, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(header_end_position_ + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ProcessTest, CannotAccessProblemDir) {
    system(StringPrintf("rm -rf %s/prob", ARG_root.c_str()).c_str());
    close(open((ARG_root + "/prob").c_str(), O_RDWR | O_CREAT));
    writeHeader();
    writeSourceFile();
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, process(fd_));
    lseek(fd_, source_end_position_, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[0]);
    ASSERT_EQUAL((off_t)(source_end_position_ + 1), lseek(fd_, 0, SEEK_END));
}

TEST_F(ProcessTest, DataSynchronizationFailure) {
    system(StringPrintf("rm -rf %s/prob/0/0", ARG_root.c_str()).c_str());
    writeHeader();
    writeSourceFile();
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, process(fd_));
    lseek(fd_, source_end_position_, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2)); 
    ASSERT_EQUAL(NO_SUCH_PROBLEM, (int)buf_[0]);
    ASSERT_EQUAL(INVALID_DATA_SIZE, (int)buf_[1]);
    ASSERT_EQUAL((off_t)(source_end_position_ + 2), lseek(fd_, 0, SEEK_END));
}

TEST_F(ProcessTest, InvalidTestcase) {
    writeHeader();
    writeSourceFile();
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(-1, process(fd_));
    lseek(fd_, source_end_position_, SEEK_SET);
    ASSERT_EQUAL((ssize_t)3, read(fd_, buf_, 3)); 
    ASSERT_EQUAL(READY, (int)buf_[0]);
    ASSERT_EQUAL(COMPILING, (int)buf_[1]);
    ASSERT_EQUAL(INTERNAL_ERROR, (int)buf_[2]);
    ASSERT_EQUAL((off_t)(source_end_position_ + 3), lseek(fd_, 0, SEEK_END));
}

TEST_F(ProcessTest, NoTestcase) {
    writeHeader();
    writeSourceFile();
    padding_ = 2;
    writeTestcase(0);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, process(fd_));
    lseek(fd_, source_end_position_, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2)); 
    ASSERT_EQUAL(READY, (int)buf_[0]);
    ASSERT_EQUAL(COMPILING, (int)buf_[1]);
    ASSERT_EQUAL((off_t)(testcase_end_positions_.back()), lseek(fd_, 0, SEEK_END));
}

TEST_F(ProcessTest, MultipleTestcase) {
    writeHeader();
    writeSourceFile();
    padding_ = 2;
    writeTestcase(1);
    padding_ = 11;
    writeTestcase(3);
    writeTestcase(2);
    writeTestcase(0);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, process(fd_));
    lseek(fd_, source_end_position_, SEEK_SET);
    ASSERT_EQUAL((ssize_t)2, read(fd_, buf_, 2)); 
    ASSERT_EQUAL(READY, (int)buf_[0]);
    ASSERT_EQUAL(COMPILING, (int)buf_[1]);
    lseek(fd_, testcase_end_positions_[0], SEEK_SET);
    ASSERT_EQUAL((ssize_t)11, read(fd_, buf_, 11)); 
    ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    ASSERT_EQUAL(JUDGING, (int)buf_[9]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[10]);
    lseek(fd_, testcase_end_positions_[1], SEEK_SET);
    ASSERT_EQUAL((ssize_t)11, read(fd_, buf_, 11)); 
    ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    ASSERT_EQUAL(JUDGING, (int)buf_[9]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[10]);
    lseek(fd_, testcase_end_positions_[2], SEEK_SET);
    ASSERT_EQUAL((ssize_t)11, read(fd_, buf_, 11)); 
    ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    ASSERT_EQUAL(JUDGING, (int)buf_[9]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[10]);
    ASSERT_EQUAL((off_t)(testcase_end_positions_.back()), lseek(fd_, 0, SEEK_END));
}

TEST_F(ProcessTest, DataSynchronization) {
    system(StringPrintf("rm -rf %s/prob/0/0", ARG_root.c_str()).c_str());
    writeHeader();
    writeSourceFile();
    padding_ = 1;
    writeData();
    padding_ = 3;
    writeTestcase(1);
    padding_ = 11;
    writeTestcase(3);
    writeTestcase(2);
    writeTestcase(0);
    lseek(fd_, 0, SEEK_SET);
    ASSERT_EQUAL(0, process(fd_));
    lseek(fd_, source_end_position_, SEEK_SET);
    ASSERT_EQUAL((ssize_t)1, read(fd_, buf_, 1)); 
    ASSERT_EQUAL(NO_SUCH_PROBLEM, (int)buf_[0]);
    lseek(fd_, data_end_position_, SEEK_SET);
    ASSERT_EQUAL((ssize_t)3, read(fd_, buf_, 3)); 
    ASSERT_EQUAL(COMPILING, (int)buf_[0]);
    ASSERT_EQUAL(READY, (int)buf_[1]);
    ASSERT_EQUAL(COMPILING, (int)buf_[2]);
    lseek(fd_, testcase_end_positions_[0], SEEK_SET);
    ASSERT_EQUAL((ssize_t)11, read(fd_, buf_, 11)); 
    ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    ASSERT_EQUAL(JUDGING, (int)buf_[9]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[10]);
    lseek(fd_, testcase_end_positions_[1], SEEK_SET);
    ASSERT_EQUAL((ssize_t)11, read(fd_, buf_, 11)); 
    ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    ASSERT_EQUAL(JUDGING, (int)buf_[9]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[10]);
    lseek(fd_, testcase_end_positions_[2], SEEK_SET);
    ASSERT_EQUAL((ssize_t)11, read(fd_, buf_, 11)); 
    ASSERT_EQUAL(RUNNING, (int)buf_[0]);
    ASSERT_EQUAL(JUDGING, (int)buf_[9]);
    ASSERT_EQUAL(ACCEPTED, (int)buf_[10]);
    ASSERT_EQUAL((off_t)(testcase_end_positions_.back()), lseek(fd_, 0, SEEK_END));
}

