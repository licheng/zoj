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

#include "native_runner.h"

#include <string>

#include <signal.h>
#include <sys/ptrace.h>
#include <sys/syscall.h>
#include <sys/user.h>
#include <sys/wait.h>
#include <unistd.h>

#include "common_io.h"
#include "disabled_syscall.h"
#include "logging.h"
#include "protocol.h"
#include "tracer.h"
#include "util.h"

void NativeRunner::UpdateStatus() {
    int ts = ReadTimeConsumption(pid_);
    int ms = ReadMemoryConsumption(pid_);
    if (ts > time_consumption_) {
        time_consumption_ = ts;
    }
    if (ms > memory_consumption_) {
        memory_consumption_ = ms;
    }
    if (time_consumption_ > time_limit_ * 1000) {
        result_ = TIME_LIMIT_EXCEEDED;
    }
    if (result_ == TIME_LIMIT_EXCEEDED && time_consumption_ <= time_limit_ * 1000) {
        time_consumption_ = time_limit_ * 1000 + 1;
    }
    if (memory_consumption_ > memory_limit_) {
        result_ = MEMORY_LIMIT_EXCEEDED;
    }
    if (result_ == MEMORY_LIMIT_EXCEEDED && memory_consumption_ <= memory_limit_) {
        memory_consumption_ = memory_limit_ + 1;
    }
    DLOG<<time_consumption_<<' '<<memory_consumption_;
    if (SendRunningMessage() == -1) {
        result_ = INTERNAL_ERROR;
    }
}

namespace {

class NativeTracer : public Tracer {
  public:
    NativeTracer(pid_t pid, NativeRunner* runner) : Tracer(pid), runner_(runner) {
    }

  protected:
    virtual void OnExit() {
        runner_->UpdateStatus();
    }

  private:
    NativeRunner* runner_;
};

}

void NativeRunner::InternalRun() {
    const char* commands[] = {"p", "p", NULL};
    StartupInfo info;
    info.stdin_filename = "input";
    info.stdout_filename = "p.out";
    info.uid = uid_;
    info.gid = gid_;
    info.time_limit = time_limit_;
    info.memory_limit = memory_limit_;
    info.vm_limit = memory_limit_ + 10 * 1024;
    info.output_limit = output_limit_;
    info.stack_limit = 8192; // Always set stack limit to 8M
    info.proc_limit = 1;
    info.file_limit = 5;
    info.trace = 1;
    pid_ = CreateProcess(commands, info);
    if (pid_ == -1) {
        LOG(ERROR)<<"Fail to execute the program";
        result_ = INTERNAL_ERROR;
        return;
    }
    NativeTracer tracer(pid_, this);
    for (;;) {
        alarm(1);
        tracer.Trace();
        if (tracer.HasExited()) {
            break;
        }
        UpdateStatus();
        if (result_ >= 0) {
            kill(pid_, SIGKILL);
        }
    }
    if (result_ < 0) {
        if (tracer.IsMemoryLimitExceeded()) {
            result_ = MEMORY_LIMIT_EXCEEDED;
        } else if (tracer.IsRestrictedSyscall()) {
            result_ = RUNTIME_ERROR;
        } else {
            int status = tracer.GetStatus();
            if (WIFEXITED(status)) {
                result_ = 0;
            } else {
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
                    case SIGILL:
                        result_ = RUNTIME_ERROR;
                        break;
                    case SIGKILL:
                        result_ = MEMORY_LIMIT_EXCEEDED;
                        break;
                    default:
                        LOG(ERROR)<<"Unexpected signal "<<WTERMSIG(status);
                        result_ = INTERNAL_ERROR;
                }
            }
        }
    }
    UpdateStatus();
}

