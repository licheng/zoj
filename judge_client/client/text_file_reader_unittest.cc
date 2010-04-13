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
#include "text_file_reader.h"

#include <string.h>

#include <fcntl.h>

class TextFileReaderTest : public TestFixture {
  protected:
    void SetUp() {
        filename_ = tmpnam(NULL);
        make_file_ = 1;
    }

    void TearDown() {
        delete file_;
        if (system(("rm -f " + filename_).c_str())) {
        }
    }

    void MakeFile(const string& content) {
        if (make_file_) {
            int fd = open(filename_.c_str(), O_RDWR | O_CREAT | O_TRUNC, 0600);
            ASSERT(fd >= 0);
            ASSERT_EQUAL((int)content.size(), (int)write(fd, content.c_str(), content.size()));
            close(fd);
        }
        file_ = new TextFileReader(filename_);
    }

    int make_file_;
    string filename_;
    TextFileReader* file_;
};

TEST_F(TextFileReaderTest, Read) {
    MakeFile("abcd");
    ASSERT_EQUAL((int)'a', file_->Read());
    ASSERT_EQUAL((int)'b', file_->Read());
    ASSERT_EQUAL((int)'c', file_->Read());
}

TEST_F(TextFileReaderTest, ReadFailure) {
    make_file_ = 0;
    MakeFile("");
    ASSERT_EQUAL(-1, file_->Read());
}

TEST_F(TextFileReaderTest, ReadEOF) {
    MakeFile("");
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileReaderTest, ReadCR) {
    MakeFile("\ra");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL((int)'a', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileReaderTest, ReadCREOF) {
    MakeFile("\r");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileReaderTest, ReadCRLF) {
    MakeFile("\r\n");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileReaderTest, ReadCRCRLF) {
    MakeFile("\r\r\n");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileReaderTest, ReadCRLFLF) {
    MakeFile("\r\n\n");
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileReaderTest, ReadCROnBufferBorder) {
    char buf[1025];
    memset(buf, 'a', sizeof(buf));
    buf[1023] = '\r';
    buf[1024] = 0;
    MakeFile(buf);
    for (int i = 0; i < 1023; ++i) {
        ASSERT_EQUAL((int)'a', file_->Read());
    }
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileReaderTest, ReadCRLFOnBufferBorder) {
    char buf[1026];
    memset(buf, 'a', sizeof(buf));
    buf[1023] = '\r';
    buf[1024] = '\n';
    buf[1025] = 0;
    MakeFile(buf);
    for (int i = 0; i < 1023; ++i) {
        ASSERT_EQUAL((int)'a', file_->Read());
    }
    ASSERT_EQUAL((int)'\n', file_->Read());
    ASSERT_EQUAL(0, file_->Read());
}

TEST_F(TextFileReaderTest, ReadHuge) {
    char buf[4097];
    for (int i = 0; i < 4096; ++i) {
        buf[i] = ' ' + i % 90;
    }
    buf[4096] = 0;
    MakeFile(buf);
    for (int i = 0; i < 4096; ++i) {
        ASSERT_EQUAL(' ' + i % 90, file_->Read());
    }
    ASSERT_EQUAL(0, file_->Read());
}



