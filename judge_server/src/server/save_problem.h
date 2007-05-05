#ifndef __SAVE_PROBLEM_H
#define __SAVE_PROBLEM_H

#include <string>

int execSaveprobCommand(int fdSocket,
                        const std::string& problem_name,
                        const std::string& version);

#endif
