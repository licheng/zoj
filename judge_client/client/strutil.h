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

#ifndef __STRUTIL_H__
#define __STRUTIL_H__

#include <string>
#include <vector>

using namespace std;

// Splits str by separator and puts the results in output.
void SplitString(const string& str, char separator, vector<string>* output);

// Like ssptrinf except that returns a string instead of outputing the result in the provided buffer.
string StringPrintf(const char *format, ...);

// Returns true if str starts with suffix
static inline bool StringStartsWith(const string& str, const string& prefix) {
    return prefix.size() <= str.size() && str.substr(0, prefix.size()) == prefix;
}

// Returns true if str ends with suffix
static inline bool StringEndsWith(const string& str, const string& suffix) {
    return suffix.size() <= str.size() && str.substr(str.size() - suffix.size()) == suffix;
}

// Returns the string representation of the current local time in the specified format. The format string the same as
// the one used in strftime().
string GetLocalTimeAsString(const char* format);


#endif // __STRUTIL_H__
