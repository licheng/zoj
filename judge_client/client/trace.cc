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

#include "trace.h"

#include <string>
#include <vector>

#include <sys/wait.h>
#include <sys/syscall.h>
#include <unistd.h>

#include "judge_result.h"
#include "kmmon-lib.h"
#include "logging.h"
#include "util.h"

TraceCallback* TraceCallback::instance_;

bool TraceCallback::onExecve() {
    if (result_ < 0) {
        result_ = RUNNING;
        return true;
    } else {
        return false;
    }
}

void TraceCallback::onMemoryLimitExceeded() {
    result_ = MEMORY_LIMIT_EXCEEDED;
}

void TraceCallback::onExit(pid_t pid) {
    timeConsumption_ = readTimeConsumption(pid);
    memoryConsumption_ = readMemoryConsumption(pid);
    result_ = 0;
}

void TraceCallback::onSIGCHLD(pid_t pid) {
    exited_ = true;
}

void TraceCallback::onError() {
    result_ = INTERNAL_ERROR;
}

bool TraceCallback::onOpen(const string& path, int flags) {
    if ((flags & O_WRONLY) == O_WRONLY ||
        (flags & O_RDWR) == O_RDWR ||
        (flags & O_CREAT) == O_CREAT ||
        (flags & O_APPEND) == O_APPEND) {
        LOG(INFO)<<"Opening "<<path<<" with flags 0x"
                 <<hex<<flags<<" is not allowed";
        return false;
    }
    if (path.empty()) {
        LOG(INFO)<<"Can not open an empty file";
        return false;
    }
    if (path[0] == '/' || path[0] == '.') {
        if (!(StringStartsWith(path, "/proc/") ||
              StringEndsWith(path, ".so") ||
              StringEndsWith(path, ".a"))) {
            LOG(INFO)<<"Opening "<<path<<" with flags 0x"
                     <<hex<<flags<<" is not allowed";
            return false;
        }
    }
    return true;
}

void TraceCallback::processResult(int status) {
    switch (result_) {
        case -1:
            // Before the first execve is invoked.
            result_ = INTERNAL_ERROR;
            break;
        case RUNNING:
            switch (WTERMSIG(status)) {
                case SIGXCPU:
                    LOG(INFO)<<"Time limit exceeded";
                    result_ = TIME_LIMIT_EXCEEDED;
                    break;
                case SIGSEGV:
                case SIGBUS:
                    LOG(INFO)<<"Segmentation fault";
                    result_ = SEGMENTATION_FAULT;
                    break;
                case SIGXFSZ:
                    LOG(INFO)<<"Output limit exceeded";
                    result_ = OUTPUT_LIMIT_EXCEEDED;
                    break;
                case SIGFPE:
                    LOG(INFO)<<"Floating point error";
                    result_ = FLOATING_POINT_ERROR;
                    break;
                case SIGKILL:
                    LOG(INFO)<<"Runtime error";
                    result_ = RUNTIME_ERROR;
                    break;
                default:
                    LOG(ERROR)<<"Unexpected signal "<<WTERMSIG(status);
                    result_ = INTERNAL_ERROR;
            }
            break;
        case MEMORY_LIMIT_EXCEEDED:
            LOG(INFO)<<"Memory limit exceeded";
            break;
    }
}

static void sigchldHandler(int sig, siginfo_t* siginfo, void* context) {
    if (TraceCallback::getInstance()) {
        TraceCallback::getInstance()->onSIGCHLD(siginfo->si_pid);
    }
}

int readStringFromTracedProcess(pid_t pid,
                                int address,
                                char* buffer,
                                int maxLength) {
    for (int i = 0; i < maxLength; i += 4) {
        int data;
        if (kmmon_readmem(pid, address + i, &data) < 0) {
            return -1;
        }
        char* p = (char*) &data;
        for (int j = 0; j < 4; j++, p++) {
            if (*p && i + j < maxLength) {
                buffer[i + j] = *p;
            } else {
                buffer[i + j] = 0;
                return 0;
            }
        }
    }
    buffer[maxLength] = 0;
    return 0;
}

static void sigkmmonHandler(int sig, siginfo_t* siginfo, void* context) {
    TraceCallback* callback = TraceCallback::getInstance();
    pid_t pid = siginfo->si_pid;
    if (!callback) {
        LOG(INFO)<<"No callback instance found";
        kmmon_continue(pid);
        return;
    }
    int syscall = siginfo->si_int;
    if (syscall == SYS_exit || syscall == SYS_exit_group) {
        callback->onExit(pid);
        kmmon_continue(pid);
    } else if (syscall == SYS_brk) {
        callback->onMemoryLimitExceeded();
        kmmon_kill(pid);
    } else if (syscall == SYS_clone ||
               syscall == SYS_fork ||
               syscall == SYS_vfork) {
        callback->onClone();
    } else if (syscall == SYS_execve) {
        if (callback->onExecve()) {
            kmmon_continue(pid);
        } else {
            kmmon_kill(pid);
        }
    } else if (syscall == SYS_open) {
        char buffer[PATH_MAX + 1];
        int address, flags;
        if (kmmon_getreg(pid, KMMON_REG_EBX, &address) < 0 ||
            kmmon_getreg(pid, KMMON_REG_ECX, &flags) < 0) {
            LOG(ERROR)<<"Fail to read register values from traced process";
            callback->onError();
        } else if (readStringFromTracedProcess(
                    pid, address, buffer, sizeof(buffer)) < 0) {
            LOG(ERROR)<<"Fail to read memory from traced process";
            callback->onError();
            kmmon_kill(pid);
        } else if (callback->onOpen(buffer, flags)) {
            kmmon_continue(pid);
        } else {
            kmmon_kill(pid);
        }
    } else {
        LOG(ERROR)<<"Unexpected syscall "<<syscall;
        TraceCallback::getInstance()->onError();
        kmmon_kill(pid);
    }
}

static struct sigaction sigchld_act;

void installHandlers() {
    struct sigaction act;
    act.sa_sigaction = sigkmmonHandler;
    sigemptyset(&act.sa_mask);
    act.sa_flags = SA_SIGINFO;
    sigaction(KMMON_SIG, &act, NULL);
    act.sa_sigaction = sigchldHandler;
    sigaction(SIGCHLD, &act, &sigchld_act);
}

void uninstallHandlers() {
    sigaction(SIGCHLD, &sigchld_act, NULL);
}
