#ifndef __JUDGE_H
#define __JUDGE_H

#include <string>

int execJudgeCommand(int fdSocket,
                     const std::string& sourceFileType,
                     const std::string& problemName,
                     const std::string& testcase,
                     const std::string& version,
                     int timeLimit,
                     int memoryLimit,
                     int outputLimit);

#endif
