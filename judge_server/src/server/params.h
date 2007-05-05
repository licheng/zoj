#ifndef __CONFIGURATION_H
#define __CONFIGURATION_H

#include <string>

extern std::string JUDGE_ROOT;

extern int JOB_UID;

extern int JOB_GID;

extern int SERVER_PORT;

extern std::string LANG;

extern int MAX_JOBS;

int parseArguments(int argc, char* argv[]);

#endif
