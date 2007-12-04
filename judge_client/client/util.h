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

#ifndef __UTIL_H
#define __UTIL_H

#include <sstream>
#include <string>
#include <vector>

#include <fcntl.h>
#include <signal.h>
#include <unistd.h>

#include "judge_result.h"
#include "logging.h"

using namespace std;

// A wrapper function for setrlimit. Set the soft limit to limit and the hard
// limit to limit + 1.
// Returns 0 on success, -1 otherwise.
int setLimit(int resource, unsigned int limit);

// Returns the current time consumption of the specified process in seconds,
// including both of the user time and the system time. If no such process
// exists, returns -1.
double readTimeConsumption(int pid);

// Returns the current maximum virtual memory consumption of the specified
// process in kilobytes. VmExe and VmLib are excluded. If no such process
// exists, returns -1.
int readMemoryConsumption(int pid);

// A struct including all restrictions to fork a child process.
struct StartupInfo {
    // Initialization
    StartupInfo()
        : fdStdin(0), fdStdout(0), fdStderr(0),
          stdinFilename(NULL), stdoutFilename(NULL), stderrFilename(NULL),
          uid(0), gid(0),
          timeLimit(0), memoryLimit(0), outputLimit(0),
          procLimit(0), fileLimit(0),
          trace(0),
          workingDirectory(NULL) { }

    // The file descriptor of the standard input of the child process. If zero,
    // the parent's standard input will be inherited.
    int fdStdin;

    // The file descriptor of the standard output of the child process. If zero,
    // the parent's standard output will be inherited.
    int fdStdout;

    // The file descriptor of the standard error of the child process. If zero,
    // the parent's standard error will be inherited.
    int fdStderr;
    
    // If not null, the file will used as the standard input of the child
    // process regardless of the value of fdStdin.
    const char* stdinFilename;

    // If not null, the standard output of the child process will be saved
    // to this file regardless of the value of fdStdout.
    const char* stdoutFilename;

    // If not null, the standard error of the child process regardless of the
    // value of fdStderr.
    const char* stderrFilename;

    // The UID of the child process. If zero, the parent's UID will be
    // inheried.
    int uid;

    // The GID of the child process. If zero, the parent's GID will be
    // inheried.
    int gid;

    // The CPU time limit of the child process, in seconds.
    int timeLimit;

    // The maximum size of the child process's data segment, in kilobytes.
    int memoryLimit;

    // The maximum size of files that the child process may create, in
    // kilobytes.
    int outputLimit;

    // The maximum number of processes that can be created.
    int procLimit;

    // The maximum file descriptor number that can be opened by the child
    // process.
    int fileLimit;

    // True if the child process will be traced
    int trace;

    // The working directory of the child process. If null, the current working
    // directory will be inherited.
    const char* workingDirectory;
};

// Executes a program in a child process and returns its process ID. The first
// element of commands is the path to the program and the rest of commands are
// argument strings passed to the program. The last element of commands should
// be null. processInfo contains all restrictions to the child process.
// Returns -1 if any error occurs.
int createProcess(const char* commands[], const StartupInfo& processInfo);

// Executes a shell command in a child process and returns its process ID. This
// is a wrapper of the createProcess function above, just like
// createProcess({"/bin/sh", "-c", command}, processInfo)
int createShellProcess(const char* command, const StartupInfo& processInfo);

// Executes a shell command in a child process and waits until it finishes. Returns its exit code. The standard error output of the shell command is stored in errorMessge, at most maxErrorMessageLength bytes. timeLimit, if not zero,  is the time limit of the shell command execution.
int runShellCommand(const char* command, char errorMessage[], int* maxErrorMessageLength, int timeLimit = 0);

// Reads from the specified file descriptor into buffer. count is the maximum
// number of bytes to read.
// Returns the number of bytes actually read, or -1 if any error occurs.
ssize_t readn(int fd, void* buffer, size_t count);

// Writes bytes in the buffer to the specified file descriptor. count is the
// number of bytes to write.
// Returns 0 if success, or -1 if any error occurs.
int writen(int fd, const void* buffer, size_t count);

// Copies the whole content in the source to the destination.
// Returns 0 if success, or -1 if any error occurs.
int copyFile(int fdSource, int fdDestination);

// Reads the whole content from the source file descriptor and writes the file
// specified by outputFilename. Creates the file if not exists.
// Return 0 if sucdess, or -1 if any error occurs.
int saveFile(int fdSource, const string& outputFilename);

// Writes reply in decimal followed by a '\n' to the specified socket.
// Return 0 if sucdess, or -1 if any error occurs.
int sendReply(int fdSocket, int reply);

sighandler_t installSignalHandler(int signal, sighandler_t handler);

sighandler_t installSignalHandler(int signum, sighandler_t handler, int flags);

sighandler_t installSignalHandler(
        int signum, sighandler_t handler, int flags, sigset_t mask);

template <class T>
inline string toString(T obj) {
    ostringstream os;
    os<<obj;
    return os.str();
}

void SplitString(const string& str, char separator, vector<string>* output);

string StringPrintf(const char *format, ...);

static inline int isFlagsReadOnly(int flags) {
    return !((flags & O_WRONLY) == O_WRONLY ||
             (flags & O_RDWR) == O_RDWR ||
             (flags & O_CREAT) == O_CREAT ||
             (flags & O_APPEND) == O_APPEND);
}

// Locks the specified file. cmd can be F_GETLK, F_SETLK or F_SETLKW.
// Returns 0 on success, -1 otherwise.
int lockFile(int fd, int cmd);

// Returns the string representation of the current local time in the specified
// format. The format string the same as the one used in strftime().
string getLocalTimeAsString(const char* format);

#define MAX_TIME_LIMIT 300
#define MAX_MEMORY_LIMIT (1280 * 1024)
#define MAX_OUTPUT_LIMIT (16 * 1024)
#define MAX_BUFFER_SIZE 256

#endif
