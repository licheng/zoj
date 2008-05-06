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

#define SYSCALL_ERROR 0
#define DEBUG 1
#define WARNING 2
#define ERROR 3
#define FATAL 4
#define INFO 5
#define RAW 6
#define LOG(level) Log(__FILE__, __LINE__, level).GetStream()

class LogFile {
    public:
        virtual ~LogFile();

        virtual void Write(const string& message) = 0;

        virtual void Close() = 0;
};

class DiskLogFile: public LogFile {
    public:
        DiskLogFile(const string& filename) : filename_(filename), fd_(-1) { }
        virtual ~DiskLogFile();

        virtual void Write(const string& message);

        virtual void Close();

    private:
        void CreateNewFile();

        string filename_;
        int fd_;
        int size_;
};

class UnixDomainSocketLogFile: public LogFile {
    public:
        UnixDomainSocketLogFile(const string& root) : sock_(-1), root_(root) { }
        virtual ~UnixDomainSocketLogFile();

        virtual void Write(const string& message);

        virtual void Close();

    private:
        void Connect();
        int sock_;
        string root_;
};

class Log {
    public:
        Log(const char* filename, int lineNumber, int level);
        ~Log();

        static void SetLogFile(LogFile* log) { 
            if (log_) {
                delete log_;
            }
            log_ = log;
        }

        static void Close() {
            if (log_) {
                log_->Close();
            }
        }

        static void SetLogToStderr(bool value) { log_to_stderr_ = value; }

        ostream& GetStream() { return message_stream_; }

    private:
        ostringstream message_stream_;
        static LogFile* log_;
        static bool log_to_stderr_;
};

#endif
