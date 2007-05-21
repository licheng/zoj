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

#include "run.h"

#include <map>
#include <string>
#include <vector>

#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include "logging.h"
#include "params.h"
#include "trace.h"
#include "util.h"

int monitor(int fdSocket,
            pid_t pid,
            int timeLimit,
            int memoryLimit,
            const TraceCallback& callback) {
    int result = -1;
    double timeConsumption = 0;
    int memoryConsumption = 0;
    do {
        double ts;
        int ms;
        if (callback.getResult() == JUDGING) {
            ts = callback.getTimeConsumption();
            ms = callback.getMemoryConsumption();
        } else {
            ts = readTimeConsumption(pid);
            ms = readMemoryConsumption(pid);
        }
        if (ts > timeConsumption) {
            timeConsumption = ts;
        }
        if (ms > memoryConsumption) {
            memoryConsumption = ms;
        }
        if (timeConsumption > timeLimit) {
            result = TIME_LIMIT_EXCEEDED;
        }
        if (result == TIME_LIMIT_EXCEEDED) {
            timeConsumption = timeLimit + 0.01;
        }
        if (memoryConsumption > memoryLimit) {
            result = MEMORY_LIMIT_EXCEEDED;
        }
        if (result == MEMORY_LIMIT_EXCEEDED) {
            memoryConsumption = memoryLimit + 1;
        }
        if (result < 0 &&
            callback.getResult() &&
            callback.getResult() != RUNNING) {
            result = callback.getResult();
        }
        char buffer[128];
        snprintf(buffer, sizeof(buffer), "%.3lf %d\n",
                 timeConsumption, memoryConsumption);
        if (writen(fdSocket, buffer, strlen(buffer)) < 0) {
            if (!callback.hasExited()) {
                kill(pid, SIGKILL);
            }
            result = SERVER_ERROR;
        }
        struct timespec request, remain;
        request.tv_sec = 1;
        request.tv_nsec = 0;
        while (result < 0 &&
               !callback.hasExited() &&
               nanosleep(&request, &remain) < 0) {
            if (errno != EINTR) {
                LOG(SYSCALL_ERROR);
                kill(pid, SIGKILL);
                result = SERVER_ERROR;
                break;
            }
            request = remain;
        }
    } while (result < 0);
    return result;
}

static pid_t pid;

int runExe(int fdSocket,
            const std::string& exeFilename,
            const std::string& stdinFilename,
            const std::string& stdoutFilename,
            int timeLimit,
            int memoryLimit,
            int outputLimit) {
    const char* commands[] = {exeFilename.c_str(), exeFilename.c_str(), NULL};
    StartupInfo info;
    info.stdinFilename = stdinFilename.c_str();
    info.stdoutFilename = stdoutFilename.c_str();
    info.uid = JOB_UID;
    info.gid = JOB_GID;
    info.timeLimit = timeLimit;
    info.memoryLimit = memoryLimit;
    info.outputLimit = outputLimit;
    info.procLimit = 1;
    info.fileLimit = 5;
    info.trace = 1;
    pid = createProcess(commands, info);
    if (pid == -1) {
        return SERVER_ERROR;
    }
    ExecutiveCallback callback;
    int result = monitor(fdSocket, pid, timeLimit, memoryLimit, callback);
    if (writen(fdSocket, "-1 -1\n", 6) < 0) {
        return -1;
    }
    return result;
}

inline int isNativeExe(const std::string& sourceFileType) {
    return sourceFileType == "cc" ||
           sourceFileType == "c" ||
           sourceFileType == "pas";
}

int doRun(int fdSocket,
          const std::string& programName,
          const std::string& sourceFileType,
          const std::string& stdinFilename,
          const std::string& stdoutFilename,
          int timeLimit,
          int memoryLimit,
          int outputLimit) {
    sendReply(fdSocket, RUNNING);
    int result;
    if (isNativeExe(sourceFileType)) {
        result = runExe(fdSocket,
                        programName,
                        stdinFilename,
                        stdoutFilename,
                        timeLimit,
                        memoryLimit,
                        outputLimit);
    } else {
        return -1;
    }
    if (result) {
        sendReply(fdSocket, result);
        return -1;
    }
    return 0;
}
