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
/*
class ProcessTest: public TestFixture {
  protected:
    void setUp() {
        ARG_root = tmpnam(NULL);
        system(("testdata/create_test_env.sh " + ARG_root).c_str());
        fp_ = tmpfile();
        fd_ = fileno(fp_);
        installHandlers();
    }

    void tearDown() {
        uninstallHandlers();
        fclose(fp_);
        system(("rm -rf " + ARG_root).c_str());
    }

    FILE* fp_;
    int fd_;
    char buf_[1024 * 16];
};

TEST_F(ProcessTest, Accepted) {

}*/
