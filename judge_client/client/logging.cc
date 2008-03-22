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

#include <time.h>

#include "logging.h"
#include "args.h"
#include "util.h"

DECLARE_ARG(string, root);

// If true, all logs are written to stderr as well
DEFINE_OPTIONAL_ARG(bool, logtostderr, false, "If true, all logs are written to stderr as well");

#define MAX_LOG_FILE_SIZE 262144 // 256KB

class LogFile {
  public:
    ~LogFile() {
        if (file_) {
            fclose(file_);
        }
    }

    void Create() {
        string dir = ARG_root + "/log";
        if (mkdir(dir.c_str(), 0777) < 0 && errno != EEXIST) {
            openlog("ZOJ Judge Client", 0, LOG_USER);
            syslog(LOG_ERR, "Fail to create dir %s", dir.c_str());
            return;
        }
        string filename =
                StringPrintf("%s/%d.%s.log",
                             dir.c_str(),
                             getpid(),
                             getLocalTimeAsString("%Y%m%d%H%M%S").c_str());
        file_ = fopen(filename.c_str(), "w");
        if (file_ == NULL) {
            openlog("ZOJ Judge Client", 0, LOG_USER);
            syslog(LOG_ERR, "Fail to create file %s", filename.c_str());
        }
        filesize_ = 0;
    }
    
    void Write(const string& message) {
        if (file_ == NULL) {
            this->Create();
        }
        if (file_ || ARG_logtostderr) {
            if (file_) {
                filesize_ += message.size();
                if (filesize_ > MAX_LOG_FILE_SIZE) {
                    fclose(file_);
                    this->Create();
                    filesize_ += message.size();
                }
                fprintf(file_, "%s", message.c_str());
                fflush(file_);
            }
            if (ARG_logtostderr) {
                fprintf(stderr, "%s", message.c_str());
                fflush(stderr);
            }
        }
    }

  private:
    FILE* file_;
    int filesize_;
} logFile;

Log::Log(const char* filename, int lineNumber, int level) {
    static const char* LEVEL_NAME[] =
            {"ERROR", "DEBUG", "WARNING", "ERROR", "FATAL", "INFO"};
    messageStream_<<getLocalTimeAsString("%Y-%m-%d %H:%M:%S")<<' '<<filename
                  <<':'<<lineNumber<<' '<<LEVEL_NAME[level]<<' ';
    if (level == SYSCALL_ERROR) {
        messageStream_<<strerror(errno)<<". ";
    }
}

Log::~Log() {
    messageStream_<<endl;
    logFile.Write(messageStream_.str());
}
