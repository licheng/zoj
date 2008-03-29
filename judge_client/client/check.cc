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

#include "judge_result.h"
#include "logging.h"
#include "args.h"
#include "trace.h"
#include "util.h"

// The root directory which contains problems, scripts and working directory of
// the client
DECLARE_ARG(string, root);

// The uid for executing the program to be judged
DECLARE_ARG(int, uid);

class TextFile {
    public:
        TextFile(const string& filename) : filename_(filename) {
            fd_ = open(filename.c_str(), O_RDONLY);
            if (fd_ < 0) {
                LOG(SYSCALL_ERROR)<<"Fail to open "<<filename;
            }
            bufferSize_ = sizeof(buffer_);
            ptr_ = buffer_ + bufferSize_;
        }

        ~TextFile() {
            if (fd_ >= 0) {
                close(fd_);
            }
        }

        // Returns the next character in the file. Returns 0 if EOF is reached,
        // -1 if any error occurs.
        int read() {
            if (ptr_ - buffer_ >= bufferSize_) {
                if (bufferSize_ < sizeof(buffer_)) {
                    // The previous readn returns less characters than requested,
                    // which means EOF is reached. It is not necessary to invoke
                    // it again.
                    return 0;
                }
                bufferSize_ = readn(fd_, buffer_, sizeof(buffer_));
                if ((int)bufferSize_ < 0) {
                    LOG(SYSCALL_ERROR)<<"Fail to read from "<<filename_;
                    return -1;
                }
                if (bufferSize_ == 0) {
                    return 0;
                }
                ptr_ = buffer_;
            }
            return *ptr_++;
        }

        // Returns the next non-white-space character. Returns 0 if EOF is
        // reached, -1 if any error occurs.
        int skipWhiteSpaces() {
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
        size_t bufferSize_;

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
int compareTextFiles(const string& outputFilename,
                     const string& programOutputFilename) {
    int ret = ACCEPTED;
    TextFile f1(outputFilename), f2(programOutputFilename);
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
                c1 = f1.skipWhiteSpaces();
            }
            if (isspace(c2)) {
                c2 = f2.skipWhiteSpaces();
            }
            while (c1 > 0 && c2 > 0) {
                if (c1 != c2) {
                    return WRONG_ANSWER;
                }
                c1 = f1.skipWhiteSpaces();
                c2 = f2.skipWhiteSpaces();
            }
            ret = PRESENTATION_ERROR;
        }
    }
    if (isspace(c1)) {
        c1 = f1.skipWhiteSpaces();
        ret = PRESENTATION_ERROR;
    }
    if (isspace(c2)) {
        c2 = f2.skipWhiteSpaces();
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

int runSpecialJudgeExe(string specialJudgeFilename,
                       const string& inputFilename,
                       const string& programOutputFilename) {
    LOG(INFO)<<"Running special judge "<<specialJudgeFilename;
    string workingDirectory = 
        specialJudgeFilename.substr(0, specialJudgeFilename.rfind('/'));
    specialJudgeFilename =
        specialJudgeFilename.substr(workingDirectory.size() + 1);
    char path[PATH_MAX + 1];
    getcwd(path, sizeof(path));
    const char* commands[] = {
        specialJudgeFilename.c_str(),
        specialJudgeFilename.c_str(),
        programOutputFilename.substr(workingDirectory.size() + 1).c_str(),
        inputFilename.substr(workingDirectory.size() + 1).c_str(),
        inputFilename.substr(workingDirectory.size() + 1).c_str(),
        NULL};
    StartupInfo info;
    info.stdinFilename = programOutputFilename.c_str();
    info.uid = ARG_uid;
    info.timeLimit = 10;
    info.memoryLimit = 256 * 1024;
    info.outputLimit = 16;
    info.fileLimit = 6; // stdin, stdout, stderr, input
    info.trace = 1;
    info.workingDirectory = workingDirectory.c_str();
    TraceCallback callback;
    pid_t pid = createProcess(commands, info);
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
    callback.processResult(status);
    if (callback.getResult()) {
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

int doCheck(int fdSocket,
            const string& inputFilename,
            const string& outputFilename,
            const string& programOutputFilename,
            const string& specialJudgeFilename) {
    LOG(INFO)<<"Judging";
    sendReply(fdSocket, JUDGING);
    int result;
    if (access(specialJudgeFilename.c_str(), F_OK) == 0) {
        string workingDirectory = 
            specialJudgeFilename.substr(0, specialJudgeFilename.rfind('/'));
        string temp =
            StringPrintf("%s/p%d.out", workingDirectory.c_str(), getpid());
        unlink(temp.c_str());
        if (symlink(programOutputFilename.c_str(), temp.c_str()) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to link from "<<programOutputFilename
                              <<" to "<<temp;
            result = INTERNAL_ERROR;
        } else {
            result = runSpecialJudgeExe(specialJudgeFilename,
                                        inputFilename,
                                        temp);
            unlink(temp.c_str());
        }
    } else {
        result = compareTextFiles(outputFilename,
                                  programOutputFilename);
    }
    sendReply(fdSocket, result);
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

