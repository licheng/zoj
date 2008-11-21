#include "strutil.h"

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


