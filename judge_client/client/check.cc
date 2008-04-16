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

class TextFile {
    public:
        TextFile(const string& filename) : filename_(filename) {
            fd_ = open(filename.c_str(), O_RDONLY);
            if (fd_ < 0) {
                LOG(SYSCALL_ERROR)<<"Fail to open "<<filename;
            }
            buffer_size_ = sizeof(buffer_);
            ptr_ = buffer_ + buffer_size_;
        }

        ~TextFile() {
            if (fd_ >= 0) {
                close(fd_);
            }
        }

        // Returns the next character in the file. Returns 0 if EOF is reached,
        // -1 if any error occurs.
        int read() {
            if (fd_ < 0) {
                return -1;
            }
            if (ptr_ - buffer_ >= buffer_size_) {
                if (buffer_size_ < sizeof(buffer_)) {
                    // The previous Readn returns less characters than requested,
                    // which means EOF is reached. It is not necessary to invoke
                    // it again.
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
            return *ptr_++;
        }

        // Returns the next non-white-space character. Returns 0 if EOF is
        // reached, -1 if any error occurs.
        int SkipWhiteSpaces() {
            int ret;
            do {
                ret = read();
            } while (ret > 0 && isspace(ret));
            return ret;
        }

        // Returns true if fail to open the file
        int fail() {
            return fd_ < 0;
        }

    private:
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

// This function compares two text files and returns
// 1. ACCEPTED
//    If these two files are exactly the same
// 2. PRESENTATION_ERROR
//    If these two files are the same after normalization.
//    The file is normalized by
//    a) Removing all white spaces at the beginning or end of the file.
//    b) Reducing consecutive white space characters to a single space(0x20).
//    See the C library function "isspace" for the definition of white space
//    characters.
// 3. WRONG_ANSWER
//    Neither ACCEPTED nor PRESENTATION_ERROR
int CompareTextFiles(const string& output_filename,
                     const string& program_output_filename) {
    int ret = ACCEPTED;
    TextFile f1(output_filename), f2(program_output_filename);
    if (f1.fail() || f2.fail()) {
        return INTERNAL_ERROR;
    }
    int c1 = f1.read();
    int c2 = f2.read();
    while (c1 > 0 && c2 > 0) { // neither EOF is reached
        if (c1 == c2) {
            c1 = f1.read();
            c2 = f2.read();
        } else if (!isspace(c1) && !isspace(c2)) {
            return WRONG_ANSWER;
        } else {
            if (isspace(c1)) {
                c1 = f1.SkipWhiteSpaces();
            }
            if (isspace(c2)) {
                c2 = f2.SkipWhiteSpaces();
            }
            while (c1 > 0 && c2 > 0) {
                if (c1 != c2) {
                    return WRONG_ANSWER;
                }
                c1 = f1.SkipWhiteSpaces();
                c2 = f2.SkipWhiteSpaces();
            }
            ret = PRESENTATION_ERROR;
        }
    }
    if (isspace(c1)) {
        c1 = f1.SkipWhiteSpaces();
        ret = PRESENTATION_ERROR;
    }
    if (isspace(c2)) {
        c2 = f2.SkipWhiteSpaces();
        ret = PRESENTATION_ERROR;
    }
    if (c1 < 0 || c2 < 0) {
        return INTERNAL_ERROR;
    }
    if (c1 > 0 || c2 > 0) {
        return WRONG_ANSWER;
    }
    return ret;
}

int RunSpecialJudgeExe(int uid,
                       string special_judge_filename,
                       const string& input_filename,
                       const string& program_output_filename) {
    LOG(INFO)<<"Running special judge "<<special_judge_filename;
    string working_dir = 
        special_judge_filename.substr(0, special_judge_filename.rfind('/'));
    special_judge_filename =
        special_judge_filename.substr(working_dir.size() + 1);
    char path[PATH_MAX + 1];
    getcwd(path, sizeof(path));
    const char* commands[] = {
        special_judge_filename.c_str(),
        special_judge_filename.c_str(),
        program_output_filename.substr(working_dir.size() + 1).c_str(),
        input_filename.substr(working_dir.size() + 1).c_str(),
        input_filename.substr(working_dir.size() + 1).c_str(),
        NULL};
    StartupInfo info;
    info.stdin_filename = program_output_filename.c_str();
    info.uid = uid;
    info.time_limit = 10;
    info.memory_limit = 256 * 1024;
    info.output_limit = 16;
    info.file_limit = 6; // stdin, stdout, stderr, input
    info.trace = 1;
    info.working_dir = working_dir.c_str();
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

int DoCheck(int sock,
            int special_judge_uid,
            const string& input_filename,
            const string& output_filename,
            const string& program_output_filename,
            const string& special_judge_filename) {
    LOG(INFO)<<"Judging";
    SendReply(sock, JUDGING);
    int result;
    if (access(special_judge_filename.c_str(), F_OK) == 0) {
        string working_dir = 
            special_judge_filename.substr(0, special_judge_filename.rfind('/'));
        string temp =
            StringPrintf("%s/p%d.out", working_dir.c_str(), getpid());
        unlink(temp.c_str());
        if (symlink(program_output_filename.c_str(), temp.c_str()) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to link from "<<program_output_filename
                              <<" to "<<temp;
            result = INTERNAL_ERROR;
        } else {
            result = RunSpecialJudgeExe(special_judge_uid,
                                        special_judge_filename,
                                        input_filename,
                                        temp);
            unlink(temp.c_str());
        }
    } else {
        result = CompareTextFiles(output_filename,
                                  program_output_filename);
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

