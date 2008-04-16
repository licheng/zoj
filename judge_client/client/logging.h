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

#ifndef __LOGGING_H
#define __LOGGING_H

#include <string>
#include <sstream>
#include <errno.h>
#include <syslog.h>

using namespace std;

#define DEBUG 1
#define WARNING 2
#define ERROR 3
#define FATAL 4
#define INFO 5
#define SYSCALL_ERROR 0
#define LOG(level) Log(__FILE__, __LINE__, level).GetStream()

class LogFile {
    public:
        virtual ~LogFile();

        virtual void Write(const string& message) = 0;
};

class DiskLogFile: public LogFile {
    public:
        DiskLogFile(const string& root) : root_(root), fd_(-1) { }
        virtual ~DiskLogFile();

        virtual void Write(const string& message);

    private:
        void CreateNewFile();

        string root_;
        int fd_;
        int size_;
};

class PipeLogFile: public LogFile {
    public:
        PipeLogFile(int pipe): pipe_(pipe) { }
        virtual ~PipeLogFile();

        virtual void Write(const string& message);

    private:
        int pipe_;
};

class Log {
    public:
        Log(const char* filename, int lineNumber, int level);
        ~Log();

        static LogFile* GetLogFile() { return log_; }

        static void SetLogFile(LogFile* log) { 
            if (log_) {
                delete log_;
            }
            log_ = log;
        }

        static void SetLogToStderr(bool value) { log_to_stderr_ = value; }

        ostream& GetStream() { return message_stream_; }

    private:
        ostringstream message_stream_;
        static LogFile* log_;
        static bool log_to_stderr_;
};

#endif
