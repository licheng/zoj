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

#include "check.h"

#include <string>
#include <stdlib.h>

#include <sys/wait.h>
#include <unistd.h>

#include "global.h"
#include "logging.h"
#include "args.h"
#include "trace.h"
#include "util.h"

TextFile::TextFile(const string& filename) : filename_(filename) {
    fd_ = open(filename.c_str(), O_RDONLY);
    if (fd_ < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to open "<<filename;
    }
    buffer_size_ = sizeof(buffer_);
    ptr_ = buffer_ + buffer_size_;
}

TextFile::~TextFile() {
    if (fd_ >= 0) {
        close(fd_);
    }
}

int TextFile::Read() {
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


int TextFile::SkipWhiteSpaces() {
    int ret;
    do {
        ret = Read();
    } while (ret > 0 && isspace(ret) && ret != '\n');
    return ret;
}

int TextFile::Fail() {
    return fd_ < 0;
}

int TextFile::Next() {
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

// This function compares two text files and returns
// 1. ACCEPTED
//    If these two files are exactly the same
// 2. PRESENTATION_ERROR
//    If these two files are the same after normalization.
//    The file is normalized by
//      a) Removing all white spaces at the beginning or ending of lines
//      b) Removing all blank lines
//      c) Replacing all consecutive white space characters by a single space(0x20).
//    See the C library function "isspace" for the definition of white space characters.
// 3. WRONG_ANSWER
//    Neither ACCEPTED nor PRESENTATION_ERROR
int CompareTextFiles(const string& output_filename, const string& program_output_filename) {
    int ret = ACCEPTED;
    TextFile f1(output_filename), f2(program_output_filename);
    if (f1.Fail() || f2.Fail()) {
        return INTERNAL_ERROR;
    }
    for (;;) {
        // Find the first non-space character at the beginning of line.
        // Blank lines are skipped.
        int c1 = f1.Read();
        int c2 = f2.Read();
        while (isspace(c1) || isspace(c2)) {
            if (c1 != c2) {
                ret = PRESENTATION_ERROR;
            }
            if (isspace(c1)) {
                c1 = f1.Read();
            }
            if (isspace(c2)) {
                c2 = f2.Read();
            }
        }
        // Compare the current line.
        for (;;) {
            // Read until 2 files return a space or 0 together.
            while (!isspace(c1) && c1 || !isspace(c2) && c2) {
                if (c1 < 0 || c2 < 0) {
                    return INTERNAL_ERROR;
                }
                if (c1 != c2) {
                    // Consecutive non-space characters should be all exactly the same
                    return WRONG_ANSWER;
                }
                c1 = f1.Read();
                c2 = f2.Read();
            }
            // Find the next non-space character or \n.
            while (isspace(c1) && c1 != '\n' || isspace(c2) && c2 != '\n') {
                if (c1 != c2) {
                    ret = PRESENTATION_ERROR;
                }
                if (isspace(c1) && c1 != '\n') {
                    c1 = f1.Read();
                }
                if (isspace(c2) && c2 != '\n') {
                    c2 = f2.Read();
                }
            }
            if (c1 < 0 || c2 < 0) {
                return INTERNAL_ERROR;
            }
            if (!c1 && !c2) {
                return ret;
            }
            if ((c1 == '\n' || !c1) && (c2 == '\n' || !c2)) {
                break;
            }
        }
    }
}

int RunSpecialJudgeExe(int uid, string special_judge_filename) {
    LOG(INFO)<<"Running special judge "<<special_judge_filename;
    char path[PATH_MAX + 1];
    getcwd(path, sizeof(path));
    const char* commands[] = {
        special_judge_filename.c_str(),
        special_judge_filename.c_str(),
        "p.out",
        NULL};
    StartupInfo info;
    info.stdin_filename = "p.out";
    info.uid = uid;
    info.time_limit = 10;
    info.memory_limit = 256 * 1024;
    info.output_limit = 16;
    info.file_limit = 6; // stdin, stdout, stderr, input
    info.trace = 1;
    TraceCallback callback;
    pid_t pid = CreateProcess(commands, info);
    if (pid == -1) {
        LOG(ERROR)<<"Fail to execute special judge";
        return INTERNAL_ERROR;
    }
    int status;
    while (waitpid(pid, &status, 0) < 0) {
        if (errno != EINTR) {
            LOG(SYSCALL_ERROR);
            return INTERNAL_ERROR;
        }
    }
    callback.ProcessResult(status);
    if (callback.GetResult()) {
        return INTERNAL_ERROR;
    }
    switch (WEXITSTATUS(status)) {
        case 0:
            return ACCEPTED;
        case 2:
            return PRESENTATION_ERROR;
        default:
            return WRONG_ANSWER;
    }
}

int DoCheck(int sock, int special_judge_uid, const string& special_judge_filename) {
    LOG(INFO)<<"Judging";
    SendReply(sock, JUDGING);
    int result;
    if (access(special_judge_filename.c_str(), F_OK) == 0) {
        result = RunSpecialJudgeExe(special_judge_uid, special_judge_filename);
    } else {
        result = CompareTextFiles("output", "p.out");
    }
    SendReply(sock, result);
    switch(result) {
        case ACCEPTED:
            LOG(INFO)<<"Accepted";
            break;
        case WRONG_ANSWER:
            LOG(INFO)<<"Wrong Answer";
            break;
        case PRESENTATION_ERROR:
            LOG(INFO)<<"Presentation Error";
            break;
    }
    return 0;
}

