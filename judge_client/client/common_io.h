#ifndef __COMMON_IO_H__
#define __COMMON_IO_H__

#include <string>

#include <arpa/inet.h>

using namespace std;

// Reads the specified number of bytes from a file descriptor.
//
// The function will keep reading until the specified number of bytes are read
// or EOF is reached or any error occurs or the program is terminated by
// SIGTERM. It will not be interrupted by signals.
//
// PARAMETERS:
//     fd: the file descriptor to read from
//     buffer: the buffer to store data
//     count: the number of bytes to read
//
// RETURNS:
//     the number of bytes read, or -1 if any error occurs or the program is
//     terminated by SIGTERM.
int Readn(int fd, void* buffer, size_t count);


// Writes the specified number of bytes to a file descriptor.
//
// The function will keep writing until the specified number of bytes are
// written or EOF is reached or any error occurs or the program is terminated
// by SIGTERM. It will not be interrupted by signals.
//
// PARAMETERS:
//     fd: the file descriptor to written to
//     buffer: the data to written
//     count: the number of bytes to write
//
// RETURNS:
//     0 if all bytes are written successfully, or -1 otherwise
int Writen(int fd, const void* buffer, size_t count);

// A wrapper of Readn to read an uint8. Returns 0 if read successfully, or -1
// otherwise.
static inline int ReadUint8(int fd, uint8_t* value) {
    if (Readn(fd, value, sizeof(*value)) == sizeof(*value)) {
        return 0;
    }
    return -1;
}

// A wrapper of Readn to read an uint32. Returns 0 if read successfully, or -1
// otherwise.
static inline int ReadUint32(int fd, uint32_t* value) {
    if (Readn(fd, value, sizeof(*value)) < (int) sizeof(*value)) {
        return -1;
    }
    *value = ntohl(*value);
    return 0;
}

// A wrapper of Writen to write an uint8. Returns 0 if written successfully, or
// -1 otherwise.
static inline int WriteUint8(int fd, uint8_t value) {
    return Writen(fd, &value, sizeof(value));
}

// A wrapper of Writen to write an uint32. Returns 0 if written successfully, or
// -1 otherwise.
static inline int WriteUint32(int fd, uint32_t value) {
    value = htonl(value);
    return Writen(fd, &value, sizeof(value));
}

// Writes a string to the file descriptor. This function always writes the
// length of the string in network order followed by the whole string.
//
// Returns 0 when the string is written successfully, or -1 otherwise.
static inline int WriteString(int fd, const string& str) {
    if (WriteUint32(fd, str.size()) == 0 &&
        Writen(fd, str.c_str(), str.size()) == 0) {
        return 0;
    }
    return -1;
}

// Copies data from one file descriptor to another.
// Returns 0 if all data are copied successfully, or -1 otherwise.
int Copy(int from, int to);

#endif // __COMMON_IO_H__
