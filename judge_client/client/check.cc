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
        TextFile(const string& filename):
            fd(0),
            // necessary, otherwise readn will think EOF is reached.
            bufferSize(sizeof(this->buffer)),
            p(buffer),
            failed(0),
            filename(filename) {
            this->p = this->buffer;
            this->fd = open(filename.c_str(), O_RDONLY);
            this->failed = this->fd < 0;
            if (this->failed) {
                LOG(SYSCALL_ERROR)<<"Fail to open "<<filename;
            }
        }

        ~TextFile() {
            if (this->fd >= 0) {
                close(this->fd);
            }
        }

        // Returns the next character in the file. Returns 0 if EOF is reached,
        // -1 if any error occurs.
        int read() {
            if (this->p - this->buffer >= this->bufferSize) {
                if (this->bufferSize < (int)sizeof(this->buffer)) {
                    // The previous readn returns less characters than requested,
                    // which means EOF is reached. It is not necessary to invoke
                    // it again.
                    return 0;
                }
                this->bufferSize = readn(this->fd,
                                          this->buffer,
                                          sizeof(this->buffer) - 1);
                if (this->bufferSize < 0) {
                    LOG(SYSCALL_ERROR)<<"Fail to read from "<<this->filename;
                    return -1;
                }
                if (this->bufferSize == 0) {
                    return 0;
                }
                this->p = this->buffer;
            }
            return *this->p++;
        }

        // Returns the next non-white-space character. Returns 0 if EOF is
        // reached, -1 if any error occurs.
        int skipWhiteSpaces() {
            int ret;
            do {
                ret = this->read();
            } while (ret > 0 && isspace(ret));
            return ret;
        }

        // Returns true if fail to open the file
        int fail() {
            return this->failed;
        }

    private:
        TextFile(const TextFile&);
        void operator=(const TextFile&);

        int fd; // the file descriptor
        char buffer[1024]; // A internal buffer used to store unread characters
        int bufferSize; // The number of available characters in the buffer
        char* p; // A pointer pointing to the next available character in the
                 // buffer
        int failed; // A flag indicating whether the file is opened successfully
        const string& filename; // the name of file reading from
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
    int c1 = f1.skipWhiteSpaces(), c2 = f2.skipWhiteSpaces();
    while (c1 > 0 && c2 > 0) { // neither EOF is reached
        if (c1 == c2) {
            c1 = f1.read();
            c2 = f2.read();
        } else if (!isspace(c1) || !isspace(c2)) {
            return WRONG_ANSWER;
        } else {
            c1 = f1.skipWhiteSpaces();
            c2 = f2.skipWhiteSpaces();
            ret = PRESENTATION_ERROR;
        }
    }
    if (c1 < 0 || c2 < 0) {
        return INTERNAL_ERROR;
    }
    if (c1 > 0 || c2 > 0) {
        if (c1 > 0) {
            if (isspace(c1)) {
                c1 = f1.skipWhiteSpaces();
            }
            if (c1 > 0) {
                return WRONG_ANSWER;
            }
        } else {
            if (isspace(c2)) {
                c2 = f2.skipWhiteSpaces();
            }
            if (c2 > 0) {
                return WRONG_ANSWER;
            }
        }
        if (c1 < 0 || c2 < 0) {
            return INTERNAL_ERROR;
        }
        return PRESENTATION_ERROR;
    }
    return ret;
}

int runSpecialJudgeExe(const string& specialJudgeFilename,
                       const string& inputFilename,
                       const string& programOutputFilename) {
    string workingDirectory = 
        specialJudgeFilename.substr(0, specialJudgeFilename.rfind('/'));
    string relativeProgramOutputFilename =
        ARG_root + "/working/" + toString(getpid()) + "/" +
        programOutputFilename;
    const char* commands[] = {
        "judge",
        "judge",
        relativeProgramOutputFilename.c_str(),
        inputFilename.substr(inputFilename.rfind('/') + 1).c_str(),
        NULL};
    StartupInfo info;
    info.stdinFilename = programOutputFilename.c_str();
    info.uid = ARG_uid;
    info.timeLimit = 10;
    info.memoryLimit = 256 * 1024;
    info.outputLimit = 16;
    info.fileLimit = 6; // stdin, stdout, stderr,
                        // input, output, program output
    info.trace = 1;
    info.workingDirectory = workingDirectory.c_str();
    ExecutiveCallback callback;
    pid_t pid = createProcess(commands, info);
    if (pid == -1) {
        return INTERNAL_ERROR;
    }
    int status;
    if (waitpid(pid, &status, 0) < 0) {
        pid = 0;
        LOG(SYSCALL_ERROR);
        return INTERNAL_ERROR;
    }
    if (WIFSIGNALED(status)) {
        LOG(ERROR)<<"Judge terminated by signal "<<WTERMSIG(status);
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
    sendReply(fdSocket, JUDGING);
    int result;
    if (access(specialJudgeFilename.c_str(), F_OK) == 0) {
        result = runSpecialJudgeExe(specialJudgeFilename,
                                    inputFilename,
                                    programOutputFilename);
    } else {
        result = compareTextFiles(outputFilename,
                                  programOutputFilename);
    }
    sendReply(fdSocket, result);
    return 0;
}

