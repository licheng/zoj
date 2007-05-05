#ifndef __LOGGING_H
#define __LOGGING_H

#include <string>
#include <sstream>
#include <errno.h>
#include <syslog.h>

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

		std::ostream& stream() {
			return this->messageStream;
		}
		
	private:
        Log(const Log&);
        void operator=(const Log&);

		const char* filename;
		int lineNumber;
		int level;
		std::ostringstream messageStream;
};

#endif
