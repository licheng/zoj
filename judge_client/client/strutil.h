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
