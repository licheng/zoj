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

#include "tracer.h"

#include <string>

using namespace std;

#include <fcntl.h>
#include <signal.h>
#include <sys/ptrace.h>
#include <sys/syscall.h>
#include <sys/user.h>
#include <sys/wait.h>
#include <unistd.h>

#include "disabled_syscall.h"
#include "logging.h"
#include "protocol.h"
#include "strutil.h"

#ifdef __i386
#define REG_SYSCALL orig_eax
#define REG_RET eax
#define REG_ARG0 ebx
#define REG_ARG1 ecx
#else
#ifdef __x86_64
#define REG_SYSCALL orig_rax
#define REG_RET rax
#define REG_ARG0 rdi
#define REG_ARG1 rsi
#endif
#endif
namespace {

void sigalrm_handler(int) {
}

int IgnoreSIGALRM() {
    struct sigaction act;
    act.sa_handler = sigalrm_handler;
    sigemptyset(&act.sa_mask);
    act.sa_flags = 0;
    sigaction(SIGALRM, &act, NULL);
    return 0;
}

int ReadStringFromTracedProcess(pid_t pid, unsigned long address, char* buffer, int max_length) {
    for (int i = 0; i < max_length; i += sizeof(long)) {
        long data = ptrace(PTRACE_PEEKDATA, pid, address + i, 0);
        if (data == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to read address "<<address + i;
            return -1;
        }
        char* p = (char*) &data;
        for (int j = 0; j < sizeof(long); j++, p++) {
            if (*p && i + j < max_length) {
                if (isprint(*p)) {
                    buffer[i + j] = *p;
                } else {
                    LOG(ERROR)<<"Unrecoginized character 0x"<<hex<<(int)(*p);
                    return -1;
                }
            } else {
                buffer[i + j] = 0;
                return 0;
            }
        }
    }
    buffer[max_length] = 0;
    return 0;
}

bool AllowedToOpen(const string& path, int flags) {
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
        if (!(StringStartsWith(path, "/proc/") || StringEndsWith(path, ".so") || StringEndsWith(path, ".a") || path == "/dev/urandom")) {
            LOG(INFO)<<"Opening "<<path<<" with flags 0x"<<hex<<flags<<" is not allowed";
            return false;
        }
    }
    return true;
}

}

int __to_install_sigalrm_handler = IgnoreSIGALRM();

void Tracer::Trace() {
    while (waitpid(pid_, &status_, 0) > 0) {
        if (!WIFSTOPPED(status_)) {
            exited_ = true;
            break;
        }
        int sig = WSTOPSIG(status_);
        if (sig != SIGTRAP) {
            ptrace(PTRACE_SYSCALL, pid_, 0, sig);
            continue;
        }
        struct user_regs_struct regs;
        ptrace(PTRACE_GETREGS, pid_, 0, &regs);
        switch(regs.REG_SYSCALL) {
          case SYS_exit:
          case SYS_exit_group:
              DLOG<<"SYS_exit";
              OnExit();
              break;
          case SYS_execve:
            if (first_execve_) {
                DLOG<<"SYS_execve";
                first_execve_ = false;
                ptrace(PTRACE_SYSCALL, pid_, 0, 0);
                continue;
            }
            break;
          case SYS_brk:
            if (before_syscall_) {
                requested_brk_ = regs.REG_ARG0;
                before_syscall_ = false;
            } else {
                if (regs.REG_RET < requested_brk_) {
                    DLOG<<"brk request "<<requested_brk_<<" return "<<regs.REG_RET;
                    ptrace(PTRACE_KILL, pid_, 0, 0);
                    memory_limit_exceeded_ = true;
                    continue;
                }
                before_syscall_ = true;
            }
            break;
          case SYS_open:
            if (before_syscall_) {
                if (ReadStringFromTracedProcess(pid_, regs.REG_ARG0, path_, sizeof(path_)) < 0) {
                    break;
                }
                DLOG<<"SYS_open "<<path_<<" flag "<<hex<<regs.REG_ARG1;
                if (!AllowedToOpen(path_, regs.REG_ARG1)) {
                    break;
                }
                before_syscall_ = false;
            } else {
                before_syscall_ = true;
            }
            ptrace(PTRACE_SYSCALL, pid_, 0, 0);
            continue;
        }
        if (regs.REG_SYSCALL < sizeof(disabled_syscall) / sizeof(disabled_syscall[0]) &&
            disabled_syscall[regs.REG_SYSCALL]) {
            LOG(ERROR)<<"Restricted syscall "<<syscall_name[regs.REG_SYSCALL];
            ptrace(PTRACE_KILL, pid_, 0, 0);
            restricted_syscall_ = true;
        } else {
            ptrace(PTRACE_SYSCALL, pid_, 0, 0);
        }
    }
}

void Tracer::OnExit() {
}

