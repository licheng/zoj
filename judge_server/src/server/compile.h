#ifndef __COMPILE_H
#define __COMPILE_H

#include <string>

// Compiles the specified source file and stores the error message into buffer
// if compilation fails. If the length of the error message is greater than
// bufferSize bytes, it is truncated. bufferSize will be updated to reflect the
// actual length of the error message.
// Returns 0 if compilation succeeded, or -1 on error.
int compile(const std::string& sourceFilename, char buffer[], int* bufferSize);

// Compiles the specified source file. See the Communication Protocol for the
// description of possible meessages written to fd.
// Returns 0 if compilation succeeded, or -1 on error.
int doCompile(int fd, const std::string& sourceFilename);

#endif
