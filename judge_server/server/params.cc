/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ Judge Server.
 *
 * ZOJ Judge Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZOJ Judge Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ Judge Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
        {"uid", 1, 0, 'u'},
        {"gid", 1, 0, 'g'},
        {"lang", 1, 0, 'l'},
        {"maxjobs", 1, 0, 'm'},
        {0, 0, 0, 0}
    };
    for (;;) {
        switch (getopt_long(argc, argv, "", options, NULL)) {
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
                LANG = ',' + LANG + ',';
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
