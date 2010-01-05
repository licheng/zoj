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

#include "strutil.h"

#include <cstdio>
#include <stdarg.h>

void SplitString(const string& str, char separator, vector<string>* output) {
    int k = 0;
    for (int i = 0; i < str.size(); ++i) {
        if (str[i] == separator) {
            if (i > k) {
                output->push_back(str.substr(k, i - k));
            }
            k = i + 1;
        }
    }
    if (k < str.size()) {
        output->push_back(str.substr(k, str.size() - k));
    }
}

string StringPrintf(const char* format, ...) {
    va_list args;
    char buffer[1024];
    va_start(args, format);
    vsnprintf(buffer, sizeof(buffer), format, args);
    va_end(args);
    return buffer;
}

string GetLocalTimeAsString(const char* format) {
    time_t t = time(NULL);
    struct tm tm;
    localtime_r(&t, &tm);
    char buf[1024];
    strftime(buf, sizeof(buf), format, &tm);
    return buf;
}
