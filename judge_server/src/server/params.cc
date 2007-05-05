#include "params.h"

#include <stdio.h>
#include <getopt.h>

std::string JUDGE_ROOT;

int JOB_UID;

int JOB_GID;

int SERVER_PORT = 8725;

std::string LANG;

int MAX_JOBS;

inline void printUsage() {
    printf("Usage: judge [--port=<port>] "
                        "[--uid=<uid>] "
                        "[--gid=<gid>] "
                        "[--lang=<comma separated supported languages>] "
                        "[--maxjobs=<max jobs>]");
}

int parseArguments(int argc, char* argv[]) {
    static struct option options[] = {
        {"port", 1, 0, 'p'},
        {"uid", 0, 0, 'u'},
        {"gid", 0, 0, 'g'},
        {"lang", 1, 0, 'l'},
        {"maxjobs", 1, 0, 'm'},
        {0, 0, 0, 0}
    };
    for (;;) {
        switch (getopt_long (argc, argv, "", options, NULL)) {
            case 'p':
                if (sscanf(optarg, "%d", &SERVER_PORT) < 1) {
                    fprintf(stderr, "Invalid port %s\n", optarg);
                    return -1;
                }
                break;
            case 'u':
                if (sscanf(optarg, "%d", &JOB_UID) < 1) {
                    fprintf(stderr, "Invalid uid %s\n", optarg);
                    return -1;
                }
                break;
            case 'g':
                if (sscanf(optarg, "%d", &JOB_GID) < 1) {
                    fprintf(stderr, "Invalid gid %s\n", optarg);
                    return -1;
                }
                break;
            case 'l':
                LANG = optarg;
                LANG += ',';
                break;
            case 'm':
                if (sscanf(optarg, "%d", &MAX_JOBS) < 1) {
                    fprintf(stderr, "Invalid maxjobs %s\n", optarg);
                    return -1;
                }
                break;
            case -1:
                return 0;
            default:
                return -1;
        }
    }
}
