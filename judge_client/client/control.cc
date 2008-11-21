/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either revision 3 of the License, or
 * (at your option) any later revision.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */
#include <set>

#include <dirent.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include "common_io.h"
#include "environment.h"
#include "global.h"
#include "logging.h"
#include "strutil.h"
#include "util.h"

DECLARE_ARG(string, compiler);
DEFINE_OPTIONAL_ARG(int, max_heart_beat_interval, 60000, "The max heart beat interval in milliseconds");

int ControlMain(const string& queue_address, int queue_port, int port) {
    if (Environment::instance()->ChangeToWorkingDir() < 0) {
        return 1;
    }

    int sock = -1;
    global::socket_closed = true;
    vector<string> supported_compilers;
    SplitString(ARG_compiler, ',', &supported_compilers);
    set<int> supported_compiler_ids;
    for (int i = 0; i < supported_compilers.size(); ++i) {
        for (int j = 0; j < global::COMPILER_NUM; ++j) {
            if (supported_compilers[i] == global::COMPILER_LIST[j].compiler) {
                supported_compiler_ids.insert(global::COMPILER_LIST[j].id);
            }
        }
    }
    // Loops until SIGTERM is received.
    while (!global::terminated) {
        if (global::socket_closed) {
            if (sock >= 0) {
                close(sock);
            }
            for (int i = 1; ; i *= 2) {
                sock = ConnectTo(queue_address, queue_port, ARG_max_heart_beat_interval);
                if (sock >= 0) {
                    global::socket_closed = false;
                    break;
                } else if (global::terminated) {
                    return 0;
                }
                if (i > 64) {
                    i = 64;
                }
                sleep(i);
            }
        }
        uint32_t command;
        if (ReadUint32(sock, &command) < 0) {
            LOG(ERROR)<<"Fail to read command";
            global::socket_closed = true;
        } else if (command == CMD_PING) {
            WriteUint32(sock, READY);
        } else if (command == CMD_INFO) {
            uint32_t buf[128] = {port, supported_compiler_ids.size()};
            int n = 2;
            for (set<int>::iterator it = supported_compiler_ids.begin();
                 it != supported_compiler_ids.end(); ++it) {
                buf[n++] = *it;
            }
            for (int i = 0; i < n; ++i) {
                buf[i] = htonl(buf[i]);
            }
            Writen(sock, buf, n * sizeof(buf[0]));
        } else {
            LOG(ERROR)<<"Invalid command "<<command;
            WriteUint32(sock, INVALID_INPUT);
            global::socket_closed = true;
        }
    }
    return 0;
}

