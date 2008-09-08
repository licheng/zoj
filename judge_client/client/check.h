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

#ifndef __CHECK_H
#define __CHECK_H

#include <string>

using namespace std;

class TextFile {
  public:
    TextFile(const string& filename);
    ~TextFile();

    // Returns the next character in the file. Returns 0 if EOF is reached, -1 if any error occurs.
    int Read();

    // Returns the next non-white-space character. Returns 0 if EOF is
    // reached, -1 if any error occurs.
    int SkipWhiteSpaces();

    // Returns true if fail to open the file
    int Fail();

  private:
    // Returns the next character in the file, but do not forward the pointer.
    // Returns 0 if EOF is reached, -1 if any error occurs.
    int Next();

    // the file descriptor
    int fd_; 

    // A internal buffer used to store unread characters
    unsigned char buffer_[1024];
    
    // The number of available characters in the buffer
    size_t buffer_size_;

    // A pointer pointing to the next available character in the buffer
    unsigned char* ptr_;

    // the filename associated with this instance
    const string filename_;
};

int CompareTextFiles(const string& output_filename, const string& program_output_filename);

int DoCheck(int sock, int special_judge_uid, const string& special_judge_filename);

#endif
