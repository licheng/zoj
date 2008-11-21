#include <unistd.h>

#include "global.h"
#include "logging.h"

int Readn(int fd, void* buffer, size_t count) {
    char* p = (char*)buffer;
    while (count > 0 && !global::terminated && !global::socket_closed) {
        ssize_t num = read(fd, p, count);
        if (num == -1) {
            if (errno == EINTR) {
                // interrupted by a signals, read again
                continue;
            }
            LOG(SYSCALL_ERROR)<<"Fail to read from file";
            return -1;
        }
        if (num == 0) {
            // EOF
            break;
        }
        p += num;
        count -= num;
    }
    if (global::terminated) {
        LOG(INFO)<<"Terminated";
        if (count > 0) {
            return -1;
        }
    }
    if (global::socket_closed) {
        LOG(ERROR)<<"Socket error";
        return -1;
    }
    return p - (char*)buffer;
}

int Writen(int fd, const void* buffer, size_t count) {
    const char*p = (const char*)buffer;
    while (count > 0 && !global::terminated && !global::socket_closed) {
        int num = write(fd, p, count);
        if (num == -1) {
            if (errno == EINTR) {
                // interrupted by a signals, write again
                continue;
            }
            LOG(SYSCALL_ERROR)<<"Fail to write to file";
            return -1;
        }
        p += num;
        count -= num;
    }
    if (global::terminated) {
        LOG(INFO)<<"Terminated";
        if (count > 0) {
            return -1;
        }
    }
    if (global::socket_closed) {
        LOG(ERROR)<<"Socket error";
        return -1;
    }
    return 0;
}

int Copy(int from, int to) {
    char buf[4096];
    int count;
    do {
        count = Readn(from, buf, sizeof(buf));
        if (count < 0 || count > 0 && Writen(to, buf, count) < 0) {
            return -1;
        }
    } while (count == sizeof(buf));
    return 0;
}
