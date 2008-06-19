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

#include "command_reader.h"

class CommandReaderTest: public TestFixture {
  protected:
    virtual void SetUp() {
        reader_ = new CommandReader(false);
        fd_[0] = fd_[1] = -1;
        ASSERT_EQUAL(0, socketpair(AF_UNIX, SOCK_STREAM, 0, fd_));
        reader_->set_sock(fd_[0]);
    }

    virtual void TearDown() {
        delete reader_;
        if (fd_[0] >= 0) {
            close(fd_[0]);
        }
        if (fd_[1] >= 0) {
            close(fd_[1]);
        }
    }

    int fd_[2];
    CommandReader* reader_;
};

TEST_F(CommandReaderTest, SetSockNegative) {
    reader_->set_sock(-1);
    ASSERT(reader_->error());
}

TEST_F(CommandReaderTest, ReadUint8) {
    ASSERT_EQUAL(4, write(fd_[1], "test", 4));
    ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
    ASSERT_EQUAL('t', (char)reader_->ReadUint8());
    ASSERT_EQUAL('e', (char)reader_->ReadUint8());
    ASSERT_EQUAL('s', (char)reader_->ReadUint8());
    ASSERT_EQUAL('t', (char)reader_->ReadUint8());
    ASSERT_EQUAL(-1, reader_->ReadUint8());
    ASSERT(reader_->error());
}

TEST_F(CommandReaderTest, ReadUint16) {
    ASSERT_EQUAL(4, write(fd_[1], "test", 4));
    ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
    ASSERT_EQUAL(ntohs(*(uint16_t*)"te"), (uint16_t)reader_->ReadUint16());
    ASSERT_EQUAL(ntohs(*(uint16_t*)"st"), (uint16_t)reader_->ReadUint16());
    ASSERT_EQUAL(-1, reader_->ReadUint16());
    ASSERT(reader_->error());
}

TEST_F(CommandReaderTest, ReadUint32) {
    ASSERT_EQUAL(4, write(fd_[1], "test", 4));
    ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
    ASSERT_EQUAL(ntohl(*(uint32_t*)"test"), (uint32_t)reader_->ReadUint32());
    ASSERT_EQUAL(-1, reader_->ReadUint32());
    ASSERT(reader_->error());
}

TEST_F(CommandReaderTest, ReadUnitMixed) {
    ASSERT_EQUAL(8, write(fd_[1], "testtest", 8));
    ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
    ASSERT_EQUAL('t', (char)reader_->ReadUint8());
    ASSERT_EQUAL(ntohs(*(uint16_t*)"es"), (uint16_t)reader_->ReadUint16());
    ASSERT_EQUAL(ntohl(*(uint32_t*)"ttes"), (uint32_t)reader_->ReadUint32());
    ASSERT_EQUAL('t', (char)reader_->ReadUint8());
    ASSERT_EQUAL(-1, reader_->ReadUint32());
    ASSERT(reader_->error());
}

TEST_F(CommandReaderTest, Rewind) {
    ASSERT_EQUAL(4, write(fd_[1], "test", 4));
    ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
    ASSERT_EQUAL('t', (char)reader_->ReadUint8());
    ASSERT_EQUAL('e', (char)reader_->ReadUint8());
    reader_->Rewind();
    ASSERT_EQUAL(ntohl(*(uint32_t*)"test"), (uint32_t)reader_->ReadUint32());
    ASSERT_EQUAL(-1, reader_->ReadUint32());
    ASSERT(reader_->error());
}

TEST_F(CommandReaderTest, Clear) {
    ASSERT_EQUAL(4, write(fd_[1], "test", 4));
    ASSERT_EQUAL(0, shutdown(fd_[1], SHUT_WR));
    ASSERT_EQUAL('t', (char)reader_->ReadUint8());
    ASSERT_EQUAL('e', (char)reader_->ReadUint8());
    reader_->Clear();
    ASSERT_EQUAL(-1, reader_->ReadUint32());
    ASSERT(reader_->error());
}
