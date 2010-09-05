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

#ifndef __TRACER_H__
#define __TRACER_H__

#include <stdio.h>
#include <stdlib.h>

#ifdef __i386
#define REG_SYSCALL orig_eax
#define REG_RET eax
#define REG_ARG0 ebx
#define REG_ARG1 ecx
#define REG_ARG4 edi
#else
#ifdef __x86_64
#define REG_SYSCALL orig_rax
#define REG_RET rax
#define REG_ARG0 rdi
#define REG_ARG1 rsi
#define REG_ARG4 r8
#endif
#endif

class Tracer {
  public:
    Tracer(pid_t pid)
        : pid_(pid), exited_(false), memory_limit_exceeded_(false), restricted_syscall_(false),
          status_(-1), first_execve_(true), before_syscall_(true), restricted_open_path_(true) {
    }

    bool HasExited() {
        return exited_;
    }

    bool IsMemoryLimitExceeded() {
        return memory_limit_exceeded_;
    }

    bool IsRestrictedSyscall() {
        return restricted_syscall_;
    }

    int GetStatus() {
        return status_;
    }

    void SetRestrictedOpenPath(bool value) {
        restricted_open_path_ = value;
    }

    void Trace();

  protected:
    virtual void OnExit();
    virtual bool HandleSyscall(struct user_regs_struct& regs);

    pid_t pid_;
    bool exited_;
    bool memory_limit_exceeded_;
    bool restricted_syscall_;
    int status_;
    bool first_execve_;
    bool before_syscall_;
    bool restricted_open_path_;
    unsigned long requested_brk_;
    char path_[FILENAME_MAX + 1];
};

#endif // __TRACER_H__
