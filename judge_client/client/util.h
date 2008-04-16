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

#ifndef __UTIL_H
#define __UTIL_H

#include <sstream>
#include <string>
#include <vector>

#include <arpa/inet.h>
#include <fcntl.h>
#include <signal.h>
#include <unistd.h>

#include "args.h"
#include "global.h"

using namespace std;

// A wrapper function for setrlimit. Set the soft limit to limit and the hard
// limit to limit + 1.
// Returns 0 on success, -1 otherwise.
int SetLimit(int resource, unsigned int limit);

// Returns the current time consumption of the specified process in
// milliseconds, including both of the user time and the system time. If no
// such process exists, returns -1.
int ReadTimeConsumption(int pid);

// Returns the current maximum virtual memory consumption of the specified
// process in kilobytes. VmExe and VmLib are excluded. If no such process
// exists, returns -1.
int ReadMemoryConsumption(int pid);

// A struct including all restrictions to fork a child process.
struct StartupInfo {
    // Initialization
    StartupInfo()
        : fd_stdin(0), fd_stdout(0), fd_stderr(0),
          stdin_filename(NULL), stdout_filename(NULL), stderr_filename(NULL),
          uid(0), gid(0),
          time_limit(0), memory_limit(0), output_limit(0),
          proc_limit(0), file_limit(0),
          trace(0),
          working_dir(NULL) { }

    // The file descriptor of the standard input of the child process. If zero,
    // the parent's standard input will be inherited.
    int fd_stdin;

    // The file descriptor of the standard output of the child process. If zero,
    // the parent's standard output will be inherited.
    int fd_stdout;

    // The file descriptor of the standard error of the child process. If zero,
    // the parent's standard error will be inherited.
    int fd_stderr;
    
    // If not null, the file will used as the standard input of the child
    // process regardless of the value of fd_stdin.
    const char* stdin_filename;

    // If not null, the standard output of the child process will be saved
    // to this file regardless of the value of fd_stdout.
    const char* stdout_filename;

    // If not null, the standard error of the child process regardless of the
    // value of fd_stderr.
    const char* stderr_filename;

    // The UID of the child process. If zero, the parent's UID will be
    // inheried.
    int uid;

    // The GID of the child process. If zero, the parent's GID will be
    // inheried.
    int gid;

    // The CPU time limit of the child process, in seconds. If zero, no time
    // limit is applied.
    int time_limit;

    // The maximum size of the child process's data segment, in kilobytes. If
    // zero, no memory limit is applied.
    int memory_limit;

    // The maximum size of files that the child process may create, in
    // kilobytes. If zero, no output limit is applied.
    int output_limit;

    // The maximum number of processes that can be created. If zero, no process
    // limit is applied.
    int proc_limit;

    // The maximum file descriptor number that can be opened by the child
    // process. If zero, no file limit is applied.
    int file_limit;

    // True if the child process will be traced
    int trace;

    // The working directory of the child process. If null, the current working
    // directory will be inherited.
    const char* working_dir;
};

// Executes a program in a child process and returns its process ID. The first
// element of commands is the path to the program and the rest of commands are
// argument strings passed to the program. The last element of commands should
// be null. process_info contains all restrictions to the child process.
// Returns -1 if any error occurs.
int CreateProcess(const char* commands[], const StartupInfo& process_info);

// Executes a shell command in a child process and returns its process ID. This
// is a wrapper of the CreateProcess function above, just like
// CreateProcess({"/bin/sh", "-c", command}, process_info)
int CreateShellProcess(const char* command, const StartupInfo& process_info);

// Reads from the specified file descriptor into buffer. count is the maximum
// number of bytes to read.
// Returns the number of bytes actually read, or -1 if any error occurs.
ssize_t Readn(int fd, void* buffer, size_t count);

static inline int ReadUint8(int fd, uint8_t* var) {
    return Readn(fd, var, sizeof(*var));
}

static inline int ReadUint16(int fd, uint16_t* var) {
    if (Readn(fd, var, sizeof(*var)) == -1) {
        return -1;
    }
    *var = ntohs(*var);
    return 0;
}

static inline int ReadUint32(int fd, uint32_t* var) {
    if (Readn(fd, var, sizeof(*var)) == -1) {
        return -1;
    }
    *var = ntohl(*var);
    return 0;
}

// Writes bytes in the buffer to the specified file descriptor. count is the
// number of bytes to write.
// Returns 0 if success, or -1 if any error occurs.
int Writen(int fd, const void* buffer, size_t count);

// Sends 1 byte code back to the queue services. Return 0 if success, or -1 if
// any error occurs.
static inline int SendReply(int sock, uint8_t reply) {
    return Writen(sock, &reply, 1);
}

sighandler_t InstallSignalHandler(int signal, sighandler_t handler);

sighandler_t InstallSignalHandler(int signum, sighandler_t handler, int flags);

sighandler_t InstallSignalHandler(
        int signum, sighandler_t handler, int flags, sigset_t mask);

void SplitString(const string& str, char separator, vector<string>* output);

string StringPrintf(const char *format, ...);

static inline bool StringStartsWith(const string& s, const string& prefix) {
    return prefix.size() <= s.size() && s.substr(0, prefix.size()) == prefix;
}

static inline bool StringEndsWith(const string& s, const string& suffix) {
    return suffix.size() <= s.size() &&
        s.substr(s.size() - suffix.size()) == suffix;
}

// Locks the specified file. cmd can be F_GETLK, F_SETLK or F_SETLKW.
// Returns 0 on success, -1 otherwise.
int LockFile(int fd, int cmd);

// Returns the string representation of the current local time in the specified
// format. The format string the same as the one used in strftime().
string GetLocalTimeAsString(const char* format);

// Returns true if the address represents the localhost
static inline bool IsLocalHost(const string& address) {
    return false;
    return address == "127.0.0.1" || address == "localhost";
}

static inline int CheckSum(int value) {
    return (value & 0xff) + ((value >> 8) & 0xff) +
           ((value >> 16) & 0xff) + ((value >> 24) & 0xff);
}

int ConnectTo(const string& address, int port);

int SaveFile(int sock, const string& output_filename, size_t size);

DECLARE_ARG(string, lang);

// Returns true if the specified file type is supported by the server
static inline bool IsSupportedSourceFileType(const string& sourceFileType) {
    vector<string> supportedLanguages;
    SplitString(ARG_lang, ',', &supportedLanguages);
    return find(supportedLanguages.begin(),
                supportedLanguages.end(),
                sourceFileType) != supportedLanguages.end();
}

#endif
