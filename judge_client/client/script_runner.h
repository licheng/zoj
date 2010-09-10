/*
 * Copyright 2010 Li, Cheng <hanshuiys@gmail.com>
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

#ifndef __SCRIPT_RUNNER_H__
#define __SCRIPT_RUNNER_H__

#include "native_runner.h"
#include "script_initializer.h"

class TraceCallback;

class ScriptRunner : public NativeRunner {
  public:
    ScriptRunner(int sock, int time_limit, int memory_limit, int output_limit, int uid, int gid, int language_id)
        : NativeRunner(sock, time_limit, memory_limit, output_limit, uid, gid), commands(NULL) {
        initializer_ = ScriptInitializer::create(language_id);
    }

    ~ScriptRunner() {
        delete initializer_;
    }

    void SetCommands(const char** commands) { this->commands = commands; };
    void SetLoaderSyscallMagic(unsigned long id, int count);

  protected:
    virtual void InternalRun();
    virtual StartupInfo GetStartupInfo();
    virtual Tracer* CreateTracer(pid_t pid, Runner* runner);

    ScriptInitializer* initializer_;
    const char** commands;
    unsigned long loader_syscall_magic_id_;
    int loader_syscall_magic_left_;

  friend class ScriptRunnerTest;    
};

#endif // __SCRIPT_RUNNER_H__
