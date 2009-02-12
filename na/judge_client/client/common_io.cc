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

#include <unistd.h>

#include "global.h"
#include "logging.h"

int Readn(int fd, void* buffer, size_t count) {
    char* p = (char*)buffer;
    while (count > 0 && !global::terminated) {
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
    return p - (char*)buffer;
}

int Writen(int fd, const void* buffer, size_t count) {
    const char*p = (const char*)buffer;
    while (count > 0 && !global::terminated) {
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
    return 0;
}

int Copy(int from, int to) {
    char buf[4096];
    int count;
    do {
        count = Readn(from, buf, sizeof(buf));
        if (count < 0 || (count > 0 && Writen(to, buf, count) < 0)) {
            return -1;
        }
    } while (count == sizeof(buf));
    return 0;
}
