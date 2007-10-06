/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ Judge Server.
 *
 * ZOJ Judge Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZOJ Judge Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ Judge Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

#ifndef __LOGGING_H
#define __LOGGING_H

#include <string>
#include <sstream>
#include <errno.h>
#include <syslog.h>

using namespace std;

#define DEBUG LOG_DEBUG
#define ERROR LOG_ERR
#define WARNING LOG_WARNING
#define INFO LOG_INFO
#define FATAL LOG_FATAL
#define SYSCALL_ERROR -1
#define LOG(level) Log(__FILE__, __LINE__, level).stream()

class Log {
	public:
		Log(const char* filename, int lineNumber, int level)
            : filename(filename),
              lineNumber(lineNumber),
              level(level),
              messageStream() {
			if (level == SYSCALL_ERROR) {
				messageStream<<strerror(errno)<<". ";
				this->level = LOG_ERR;
			}
		}
		
		~Log() {
			openlog("ZOJ judge", 0, LOG_USER);
			syslog(level, "%s:%d: %s", filename, lineNumber, messageStream.str().c_str());
		}

		ostream& stream() {
			return this->messageStream;
		}
		
	private:
        Log(const Log&);
        void operator=(const Log&);

		const char* filename;
		int lineNumber;
		int level;
		ostringstream messageStream;
};

#endif
