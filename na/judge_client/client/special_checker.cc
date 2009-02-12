/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */

#include "special_checker.h"

#include <string>

using namespace std;

#include <sys/wait.h>

#include "common_io.h"
#include "logging.h"
#include "protocol.h"
#include "tracer.h"
#include "util.h"

DECLARE_ARG(int, uid);

DEFINE_OPTIONAL_ARG(int, special_judge_run_time_limit, 10, "The run time limit of special judges in seconds");
DEFINE_OPTIONAL_ARG(int, special_judge_memory_limit, 256 * 1024, "The memory limit of special judges in kb");
DEFINE_OPTIONAL_ARG(int, special_judge_output_limit, 16, "The output limit of special judges in kb");

int SpecialChecker::InternalCheck(int sock) {
    LOG(INFO)<<"Running special judge "<<special_judge_filename_;
    char path[FILENAME_MAX + 1];
    if (getcwd(path, sizeof(path))) {
    }
    const char* commands[] = {
        special_judge_filename_.c_str(),
        special_judge_filename_.c_str(),
        "p.out",
        NULL};
    StartupInfo info;
    info.stdin_filename = "p.out";
    info.uid = ARG_uid;
    info.time_limit = ARG_special_judge_run_time_limit;
    info.memory_limit = ARG_special_judge_memory_limit;
    info.output_limit = ARG_special_judge_output_limit;
    info.file_limit = 6; // stdin, stdout, stderr, input
    info.trace = 1;
    pid_t pid = CreateProcess(commands, info);
    if (pid == -1) {
        LOG(ERROR)<<"Fail to execute special judge";
        return -1;
    }
    Tracer tracer(pid);
    do {
        alarm(1);
        tracer.Trace();
        DLOG<<"Judging";
        WriteUint32(sock, JUDGING);
    } while (!tracer.HasExited());
    int status = tracer.GetStatus();
    if (WIFEXITED(status)) {
        switch (WEXITSTATUS(status)) {
            case 0:
                return ACCEPTED;
            case 2:
                return PRESENTATION_ERROR;
            default:
                return WRONG_ANSWER;
        }
    }
    return -1;
}
