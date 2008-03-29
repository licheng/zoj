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

#include "run.h"

#include <string>

#include <arpa/inet.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include "logging.h"
#include "args.h"
#include "trace.h"
#include "util.h"

// The uid for executing the program to be judged
DECLARE_ARG(int, uid);

// The uid for executing the program to be judged
DECLARE_ARG(int, gid);

int sendRunningMessage(int fdSocket,
                       int timeConsumption,
                       int memoryConsumption) {
    char message[9];
    message[0] = RUNNING;
    *(unsigned int*)(message + 1) =
        htonl((unsigned int)(timeConsumption));
    *(unsigned int*)(message + 5) = htonl(memoryConsumption);
    if (writen(fdSocket, message, sizeof(message)) == -1) {
        LOG(ERROR)<<"Fail to send running message";
        return -1;
    }
    return 0;
}

int monitor(int fdSocket,
            pid_t pid,
            int timeLimit,
            int memoryLimit,
            TraceCallback* callback) {
    int result = -1;
    int timeConsumption = 0;
    int memoryConsumption = 0;
    timeLimit *= 1000;
    while (result < 0 && !callback->hasExited()) {
        struct timespec request, remain;
        request.tv_sec = 1;
        request.tv_nsec = 0;
        while (result < 0 &&
               !callback->hasExited() &&
               nanosleep(&request, &remain) < 0) {
            if (errno != EINTR) {
                LOG(SYSCALL_ERROR)<<"Fail to sleep";
                kill(pid, SIGKILL);
                result = INTERNAL_ERROR;
            }
            request = remain;
        }
        int ts;
        int ms;
        if (result < 0 && !callback->hasExited()) {
            ts = readTimeConsumption(pid);
            ms = readMemoryConsumption(pid);
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
                timeConsumption = timeLimit + 1;
            }
            if (memoryConsumption > memoryLimit) {
                result = MEMORY_LIMIT_EXCEEDED;
            }
            if (result == MEMORY_LIMIT_EXCEEDED) {
                memoryConsumption = memoryLimit + 1;
            }
            if (sendRunningMessage(fdSocket,
                                   timeConsumption,
                                   memoryConsumption) == -1) {
                if (!callback->hasExited()) {
                    kill(pid, SIGKILL);
                }
                result = INTERNAL_ERROR;
                break;
            }
        }
    }
    int status;
    while (waitpid(pid, &status, 0) < 0) {
        if (errno != EINTR) {
            LOG(SYSCALL_ERROR);
            return INTERNAL_ERROR;
        }
    }
    if (result < 0) {
        if (callback->getResult() == 0) {
            timeConsumption = callback->getTimeConsumption();
            memoryConsumption = callback->getMemoryConsumption();
        }
        callback->processResult(status);
        result = callback->getResult();
        if (result == TIME_LIMIT_EXCEEDED) {
            timeConsumption = timeLimit + 1;
        }
        if (result == MEMORY_LIMIT_EXCEEDED) {
            memoryConsumption = memoryLimit + 1;
        }
        if (sendRunningMessage(fdSocket,
                               timeConsumption,
                               memoryConsumption) == -1) {
            result = INTERNAL_ERROR;
        }
    }
    return result;
}

int runExe(int fdSocket,
            const string& exeFilename,
            const string& inputFilename,
            const string& programOutputFilename,
            int timeLimit,
            int memoryLimit,
            int outputLimit) {
    LOG(INFO)<<"Running";
    const char* commands[] = {exeFilename.c_str(), exeFilename.c_str(), NULL};
    StartupInfo info;
    info.stdinFilename = inputFilename.c_str();
    info.stdoutFilename = programOutputFilename.c_str();
    info.uid = ARG_uid;
    info.gid = ARG_gid;
    info.timeLimit = timeLimit;
    info.memoryLimit = memoryLimit;
    info.outputLimit = outputLimit;
    info.procLimit = 1;
    info.fileLimit = 5;
    info.trace = 1;
    TraceCallback callback;
    pid_t pid = createProcess(commands, info);
    if (pid == -1) {
        LOG(ERROR)<<"Fail to execute the program";
        return INTERNAL_ERROR;
    }
    return monitor(fdSocket, pid, timeLimit, memoryLimit, &callback);
}

inline int isNativeExe(const string& sourceFileType) {
    return sourceFileType == "cc" ||
           sourceFileType == "c" ||
           sourceFileType == "pas";
}

int doRun(int fdSocket,
          const string& programName,
          const string& sourceFileType,
          const string& inputFilename,
          const string& programOutputFilename,
          int timeLimit,
          int memoryLimit,
          int outputLimit) {
    int result;
    if (isNativeExe(sourceFileType)) {
        result = runExe(fdSocket,
                        programName,
                        inputFilename,
                        programOutputFilename,
                        timeLimit,
                        memoryLimit,
                        outputLimit);
    } else {
        return -1;
    }
    if (result) {
        sendReply(fdSocket, result);
        if (result == INTERNAL_ERROR) {
            return -1;
        } else {
            return 1;
        }
    }
    return 0;
}
