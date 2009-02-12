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

#include "text_file_reader.h"

#include <fcntl.h>

#include "common_io.h"
#include "logging.h"

TextFileReader::TextFileReader(const string& filename) : filename_(filename) {
    fd_ = open(filename.c_str(), O_RDONLY);
    if (fd_ < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to open "<<filename;
    }
    buffer_size_ = sizeof(buffer_);
    ptr_ = buffer_ + buffer_size_;
}

TextFileReader::~TextFileReader() {
    if (fd_ >= 0) {
        close(fd_);
    }
}

int TextFileReader::Read() {
    if (fd_ < 0) {
        return -1;
    }
    int ret = Next();
    if (ret > 0) {
        ++ptr_;
    }
    if (ret != '\r') {
        return ret;
    }

    // Treat \r\n as \n
    int t = Next();
    if (t == '\n') {
        ++ptr_;
    }
    return '\n';
}


int TextFileReader::SkipWhiteSpaces() {
    int ret;
    do {
        ret = Read();
    } while (ret > 0 && isspace(ret) && ret != '\n');
    return ret;
}

int TextFileReader::Fail() {
    return fd_ < 0;
}

int TextFileReader::Next() {
    if (ptr_ - buffer_ >= buffer_size_) {
        if (buffer_size_ < sizeof(buffer_)) {
            // The previous Readn returns less characters than requested, which means EOF is reached.
            // It is not necessary to invoke it again.
            return 0;
        }
        buffer_size_ = Readn(fd_, buffer_, sizeof(buffer_));
        if ((int)buffer_size_ < 0) {
            LOG(SYSCALL_ERROR)<<"Fail to read from "<<filename_;
            return -1;
        }
        if (buffer_size_ == 0) {
            return 0;
        }
        ptr_ = buffer_;
    }
    return *ptr_;
}
