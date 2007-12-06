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

TraceCallback* TraceCallback::instance;

int TraceCallback::onExecve() {
    if (this->result < 0) {
        this->result = RUNNING;
        return 1;
    } else {
        return 0;
    }
}

void TraceCallback::onMemoryLimitExceeded() {
    this->result = MEMORY_LIMIT_EXCEEDED;
}

void TraceCallback::onExit(pid_t pid) {
    this->timeConsumption = readTimeConsumption(pid);
    this->memoryConsumption = readMemoryConsumption(pid);
    this->result = 0;
}

void TraceCallback::onSIGCHLD(pid_t pid) {
    int status;
    while (waitpid(pid, &status, 0) < 0) {
        if (errno != EINTR) {
            this->result = INTERNAL_ERROR;
            return;
        }
    }
    switch (this->result) {
        case -1:
            // Before the first execve is invoked.
            this->result = INTERNAL_ERROR;
            break;
        case RUNNING:
            switch (WTERMSIG(status)) {
                case SIGXCPU:
                    this->result = TIME_LIMIT_EXCEEDED;
                    break;
                case SIGSEGV:
                    this->result = SEGMENTATION_FAULT;
                    break;
                case SIGXFSZ:
                    this->result = OUTPUT_LIMIT_EXCEEDED;
                    break;
                case SIGFPE:
                    this->result = FLOATING_POINT_ERROR;
                    break;
                case SIGKILL:
                    this->result = RUNTIME_ERROR;
                    break;
                default:
                    LOG(ERROR)<<"Unexpected signal "<<WTERMSIG(status);
                    this->result = INTERNAL_ERROR;
            }
            break;
    }
}

void TraceCallback::onError() {
    this->result = INTERNAL_ERROR;
}

void ExecutiveCallback::onExit(pid_t pid) {
    this->timeConsumption = readTimeConsumption(pid);
    this->memoryConsumption = readMemoryConsumption(pid);
}

static void sigchldHandler(int sig, siginfo_t* siginfo, void* context) {
    if (TraceCallback::getInstance()) {
        TraceCallback::getInstance()->onSIGCHLD(siginfo->si_pid);
    }
}

static void sigkmmonHandler(int sig, siginfo_t* siginfo, void* context) {
    if (!TraceCallback::getInstance()) {
        return;
    }
    pid_t pid = siginfo->si_pid;
    int syscall = siginfo->si_value.sival_int;
    if (syscall == SYS_exit || syscall == SYS_exit_group) {
        TraceCallback::getInstance()->onExit(pid);
        kmmon_continue(pid);
    } else if (syscall == SYS_brk) {
        TraceCallback::getInstance()->onMemoryLimitExceeded();
        kmmon_kill(pid);
    } else if (syscall == SYS_clone ||
               syscall == SYS_fork ||
               syscall == SYS_vfork) {
        TraceCallback::getInstance()->onClone();
    } else if (syscall == SYS_execve) {
        if (TraceCallback::getInstance()->onExecve()) {
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

void installHandlers() {
    struct sigaction act, oact;
    act.sa_sigaction = sigkmmonHandler;
    sigemptyset(&act.sa_mask);
    act.sa_flags = SA_SIGINFO;
    sigaction(KMMON_SIG, &act, &oact);
    act.sa_sigaction = sigchldHandler;
    sigaction(SIGCHLD, &act, &oact);
}
