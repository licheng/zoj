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

#include "global.h"
#include "kmmon-lib.h"
#include "logging.h"
#include "protocol.h"
#include "strutil.h"
#include "util.h"

TraceCallback* TraceCallback::instance_;

bool TraceCallback::OnExecve() {
    if (result_ < 0) {
        result_ = RUNNING;
        return true;
    } else {
        return false;
    }
}

void TraceCallback::OnMemoryLimitExceeded() {
    result_ = MEMORY_LIMIT_EXCEEDED;
}

void TraceCallback::OnExit(pid_t pid) {
    time_consumption_ = ReadTimeConsumption(pid);
    memory_consumption_ = ReadMemoryConsumption(pid);
    result_ = 0;
}

void TraceCallback::OnSIGCHLD(pid_t pid) {
    exited_ = true;
}

void TraceCallback::OnError() {
    result_ = INTERNAL_ERROR;
}

bool TraceCallback::OnOtherSyscall(int syscall) {
    return false;
}

bool TraceCallback::OnOpen(const string& path, int flags) {
    if ((flags & O_WRONLY) == O_WRONLY ||
        (flags & O_RDWR) == O_RDWR ||
        (flags & O_CREAT) == O_CREAT ||
        (flags & O_APPEND) == O_APPEND) {
        LOG(INFO)<<"Opening "<<path<<" with flags 0x"<<hex<<flags<<" is not allowed";
        return false;
    }
    if (path.empty()) {
        LOG(INFO)<<"Can not open an empty file";
        return false;
    }
    if (path[0] == '/' || path[0] == '.') {
        if (!(StringStartsWith(path, "/proc/") || StringEndsWith(path, ".so") || StringEndsWith(path, ".a"))) {
            LOG(INFO)<<"Opening "<<path<<" with flags 0x"<<hex<<flags<<" is not allowed";
            return false;
        }
    }
    return true;
}

void TraceCallback::ProcessResult(int status) {
    switch (result_) {
        case -1:
            // Before the first execve is invoked.
            result_ = INTERNAL_ERROR;
            break;
        case RUNNING:
            switch (WTERMSIG(status)) {
                case SIGXCPU:
                    result_ = TIME_LIMIT_EXCEEDED;
                    break;
                case SIGSEGV:
                case SIGBUS:
                    result_ = SEGMENTATION_FAULT;
                    break;
                case SIGXFSZ:
                    result_ = OUTPUT_LIMIT_EXCEEDED;
                    break;
                case SIGFPE:
                    result_ = FLOATING_POINT_ERROR;
                    break;
                case SIGKILL:
                case SIGILL:
                    result_ = RUNTIME_ERROR;
                    break;
                default:
                    LOG(ERROR)<<"Unexpected signal "<<WTERMSIG(status);
                    result_ = INTERNAL_ERROR;
            }
            break;
        case MEMORY_LIMIT_EXCEEDED:
            break;
    }
}

static void SIGCHLDHandler(int sig, siginfo_t* siginfo, void* context) {
    if (TraceCallback::GetInstance()) {
        TraceCallback::GetInstance()->OnSIGCHLD(siginfo->si_pid);
    }
}

int ReadStringFromTracedProcess(pid_t pid, int address, char* buffer, int max_length) {
    for (int i = 0; i < max_length; i += 4) {
        int data;
        if (kmmon_readmem(pid, address + i, &data) < 0) {
            return -1;
        }
        char* p = (char*) &data;
        for (int j = 0; j < 4; j++, p++) {
            if (*p && i + j < max_length) {
                buffer[i + j] = *p;
            } else {
                buffer[i + j] = 0;
                return 0;
            }
        }
    }
    buffer[max_length] = 0;
    return 0;
}

static void SIGKMMONHandler(int sig, siginfo_t* siginfo, void* context) {
    TraceCallback* callback = TraceCallback::GetInstance();
    pid_t pid = siginfo->si_pid;
    if (!callback) {
        LOG(INFO)<<"No callback instance found";
        kmmon_continue(pid);
        return;
    }
    int syscall = siginfo->si_int;
    if (syscall == SYS_exit || syscall == SYS_exit_group) {
        callback->OnExit(pid);
        kmmon_continue(pid);
    } else if (syscall == SYS_brk) {
        callback->OnMemoryLimitExceeded();
        kmmon_kill(pid);
    } else if (syscall == SYS_clone ||
               syscall == SYS_fork ||
               syscall == SYS_vfork) {
        if (callback->OnClone()) {
            kmmon_continue(pid);
        } else {
            kmmon_kill(pid);
        }
    } else if (syscall == SYS_execve) {
        if (callback->OnExecve()) {
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
            callback->OnError();
            kmmon_kill(pid);
        } else if (ReadStringFromTracedProcess(pid, address, buffer, sizeof(buffer)) < 0) {
            LOG(ERROR)<<"Fail to read memory with start address "<<address<<" from traced process";
            // Don't call OnError so that it will be treated as Restricted Function.
            // This error can occur when the traced program invokes open with an invalid address, for example, NULL.
            kmmon_kill(pid);
        } else if (callback->OnOpen(buffer, flags)) {
            kmmon_continue(pid);
        } else {
            kmmon_kill(pid);
        }
    } else if (callback->OnOtherSyscall(syscall)) {
        kmmon_continue(pid);
    } else {
        LOG(INFO)<<"Restricted syscall "<<syscall;
        kmmon_kill(pid);
    }
}

static struct sigaction sigchld_act;

void InstallHandlers() {
    struct sigaction act;
    act.sa_sigaction = SIGKMMONHandler;
    sigemptyset(&act.sa_mask);
    act.sa_flags = SA_SIGINFO;
    sigaction(KMMON_SIG, &act, NULL);
    act.sa_sigaction = SIGCHLDHandler;
    sigaction(SIGCHLD, &act, &sigchld_act);
}

void UninstallHandlers() {
    sigaction(SIGCHLD, &sigchld_act, NULL);
}
