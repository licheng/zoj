#ifndef __CHECK_H
#define __CHECK_H

#include <string>

int doCheck(int fdSocket,
            const std::string& inputFilename,
            const std::string& outputFilename,
            const std::string& programOutputFilename,
            const std::string& specialJudgeFilename);

#endif
