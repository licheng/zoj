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

#include "logging.h"

#include <sys/socket.h>
#include <sys/un.h>

#include "args.h"
#include "common_io.h"
#include "strutil.h"
#include "util.h"

LogFile* Log::log_ = NULL;
bool Log::log_to_stderr_ = true;

Log::Log(const char* filename, int line_number, int level) {
    static const char* LEVEL_NAME[] = {"ERROR", "DEBUG", "WARNING", "ERROR", "FATAL", "INFO"};
    if ((log_ || log_to_stderr_) && level != RAW) {
        message_stream_<<GetLocalTimeAsString("%Y-%m-%d %H:%M:%S")<<' '
                       <<filename<<':'<<line_number<<' '<<LEVEL_NAME[level]<<' ';
        if (level == SYSCALL_ERROR) {
            message_stream_<<strerror(errno)<<". ";
        }
    }
}

Log::~Log() {
    string message;
    if (log_ || log_to_stderr_) {
        message_stream_<<endl;
        message = message_stream_.str();
    }
    if (log_) {
        log_->Write(message);
    }
    if (log_to_stderr_) {
        fprintf(stderr, "%s", message.c_str());
        fflush(stderr);
    }
}

LogFile::~LogFile() {
}

void DiskLogFile::Write(const string& message) {
    if (fd_ < 0) {
        this->CreateNewFile();
    }
    if (fd_ >= 0) {
        if (fd_ >= 0) {
            Writen(fd_, message.c_str(), message.size());
        }
    }
}

void DiskLogFile::CreateNewFile() {
    string filename = StringPrintf("%sjudge.log", log_root_.c_str());
    fd_ = open(filename.c_str(), O_RDWR | O_CREAT | O_APPEND, 0600);
    if (fd_ < 0) {
        string error_message = strerror(errno);
        openlog("ZOJ Judge Client", 0, LOG_USER);
        syslog(LOG_ERR, "Fail to create file %s: %s", filename.c_str(), error_message.c_str());
    }
}

DiskLogFile::~DiskLogFile() {
    Close();
}

void DiskLogFile::Close() {
    if (fd_ >= 0) {
        close(fd_);
        fd_ = -1;
    }
}

UnixDomainSocketLogFile::UnixDomainSocketLogFile(const string& server_sock_name, const string& client_sock_name)
    : server_sock_name_(server_sock_name),
      client_sock_name_(client_sock_name),
      sock_(-1),
      prefix_(StringPrintf("[%d] ", getpid())) {
}

UnixDomainSocketLogFile::~UnixDomainSocketLogFile() {
    Close();
}

void UnixDomainSocketLogFile::Write(const string& message) {
    if (sock_ < 0) {
        Connect();
    }
    if (sock_ >= 0) {
        Writen(sock_, prefix_.c_str(), prefix_.size());
        Writen(sock_, message.c_str(), message.size());
    }
}

void UnixDomainSocketLogFile::Close() {
    if (sock_ >= 0) {
        close(sock_);
        sock_ = -1;
    }
}

void UnixDomainSocketLogFile::Connect() {
    sock_ = socket(AF_UNIX, SOCK_STREAM, 0);
    if (sock_ < 0) {
        string error_message = strerror(errno);
        openlog("ZOJ Judge Client", 0, LOG_USER);
        syslog(LOG_ERR, "Fail to create socket: %s", error_message.c_str());
        return;
    }
    struct sockaddr_un un;
    memset(&un, 0, sizeof(un));
    un.sun_family = AF_UNIX; 
    unlink(client_sock_name_.c_str());
    strcpy(un.sun_path, client_sock_name_.c_str());
    if (bind(sock_, (struct sockaddr*)&un, offsetof(struct sockaddr_un, sun_path) + strlen(un.sun_path)) < 0) {
        string error_message = strerror(errno);
        openlog("ZOJ Judge Client", 0, LOG_USER);
        syslog(LOG_ERR, "Fail to bind: %s", error_message.c_str());
        return;
    }
    if (chmod(un.sun_path, S_IRWXU) < 0) {
        string error_message = strerror(errno);
        openlog("ZOJ Judge Client", 0, LOG_USER);
        syslog(LOG_ERR, "Fail to chmod: %s", error_message.c_str());
        return;
    }
    memset(&un, 0, sizeof(un));
    un.sun_family = AF_UNIX; 
    strcpy(un.sun_path, server_sock_name_.c_str());
    if (connect(sock_, (struct sockaddr*)&un, offsetof(struct sockaddr_un, sun_path) + server_sock_name_.size()) < 0) {
        string error_message = strerror(errno);
        openlog("ZOJ Judge Client", 0, LOG_USER);
        syslog(LOG_ERR, "Fail to connect: %s", error_message.c_str());
        return;
    }
}

