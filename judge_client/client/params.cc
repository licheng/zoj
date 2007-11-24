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

#include "util.h"

string JUDGE_ROOT;

int JOB_UID;

int JOB_GID;

pair<string, int> QUEUE_ADDRESS;

vector<string> LANG;

void printUsage() {
    printf("Usage: judge [--queue=<queue address>] "
                        "[--uid=<uid>] "
                        "[--gid=<gid>] "
                        "[--lang=<comma separated supported languages>] ");
}

int parseArguments(int argc, char* argv[]) {
    static struct option options[] = {
        {"queue", 1, 0, 'q'},
        {"uid", 1, 0, 'u'},
        {"gid", 1, 0, 'g'},
        {"lang", 1, 0, 'l'},
        {0, 0, 0, 0}
    };
    const char* p;
    for (;;) {
        switch (getopt_long(argc, argv, "", options, NULL)) {
            case 'q':
                p = strstr(optarg, ":");
                if (!p) {
                    fprintf(stderr, "Invalid queue address %s\n", optarg);
                    return -1;
                }
                QUEUE_ADDRESS.first = string(optarg, p - optarg);
                QUEUE_ADDRESS.second = atoi(p + 1);
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
                SplitString(optarg, ',', &LANG);
                break;
            case -1:
                return 0;
            default:
                return -1;
        }
    }
}
