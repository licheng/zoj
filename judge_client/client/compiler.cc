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

#include "compiler.h"

#include <string>
#include <vector>

#include <arpa/inet.h>
#include <sys/wait.h>

#include "args.h"
#include "environment.h"
#include "common_io.h"
#include "logging.h"
#include "protocol.h"
#include "strutil.h"
#include "trace.h"
#include "util.h"

DEFINE_ARG(string, compiler, "All compilers supported by this client");
DEFINE_OPTIONAL_ARG(int, compilation_time_limit, 10, "The time limit of compilers in seconds");
DEFINE_OPTIONAL_ARG(int, compilation_output_limit, 4096, "The output limit of compilers in kb");
DEFINE_OPTIONAL_ARG(int, max_output_file_number, 16, "The maximum number of files that the compiler can create");

namespace {

struct CompilerInfo {
    int id;
    const char* compiler;
    const char* source_filename;
} COMPILER_LIST[] = {
    {1, "gcc", "p.c"},
    {2, "g++", "p.cc"},
    {3, "fpc", "p.pas"},
    {4, "javac", "P.java"}
};

}

CompilerManager* CompilerManager::instance_ = NULL;

const CompilerManager* CompilerManager::GetInstance() {
    if (instance_ == NULL) {
        instance_ = new CompilerManager(ARG_compiler);
    }
    return instance_;
}

CompilerManager::CompilerManager(const string& supported_compilers) {
    vector<string> t;
    SplitString(supported_compilers, ',', &t);
    for (int i = 0; i < sizeof(COMPILER_LIST) / sizeof(COMPILER_LIST[0]); ++i) {
        for (int j = 0; j < t.size(); ++j) {
            if (COMPILER_LIST[i].compiler == t[j]) {
                compiler_map_[COMPILER_LIST[i].id] = new Compiler(COMPILER_LIST[i].id,
                                                                  COMPILER_LIST[i].compiler,
                                                                  COMPILER_LIST[i].source_filename);
                break;
            }
        }
    }
}

vector<const Compiler*> CompilerManager::GetAllSupportedCompilers() const {
    vector<const Compiler*> ret;
    for (map<int, const Compiler*>::const_iterator it = compiler_map_.begin(); it != compiler_map_.end(); ++it) {
        ret.push_back(it->second);
    }
    return ret;
}

const Compiler* CompilerManager::GetCompiler(int compiler) const {
    map<int, const Compiler*>::const_iterator it = compiler_map_.find(compiler);
    if (it == compiler_map_.end()) {
        return NULL;
    } else {
        return it->second;
    }
}

const Compiler* CompilerManager::GetCompilerByExtension(const string& extension) const {
    for (map<int, const Compiler*>::const_iterator it = compiler_map_.begin(); it != compiler_map_.end(); ++it) {
        if (StringEndsWith(it->second->source_filename(), "." + extension)) {
            return it->second;
        }
    }
    return NULL;
}

namespace {

class CompilerTraceCallback : public TraceCallback {
  public:
    CompilerTraceCallback() : open_create_count_(0) {
    }

    bool TooManyFiles() {
        return open_create_count_ > ARG_max_output_file_number;
    }

    virtual bool OnExecve() {
        return true;
    }

    virtual bool OnClone() {
        return true;
    }

    virtual bool OnOpen(const string& path, int flags) {
        return (flags & O_CREAT) != O_CREAT || ++open_create_count_ <= ARG_max_output_file_number;
    }

    virtual bool OnOtherSyscall(int syscall) {
        return true;
    }

  private:
    int open_create_count_;
};

}

int Compiler::Compile(int sock, const string& source_file_name) const {
    LOG(INFO)<<"Compiling";
    WriteUint32(sock, COMPILING);
    string command = StringPrintf("%s '%s' '%s'",
                                  Environment::GetInstance()->GetCompilationScript().c_str(),
                                  this->compiler_name_.c_str(),
                                  source_file_name.c_str());
    LOG(INFO)<<"Command: "<<command;
    int fd_pipe[2];
    if (pipe(fd_pipe) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create pipe";
        WriteUint32(sock, INTERNAL_ERROR);
        return -1;
    }
    StartupInfo info;
    info.fd_stderr = fd_pipe[1];
    info.time_limit = ARG_compilation_time_limit;
    info.output_limit = ARG_compilation_output_limit;
    info.trace = 1;
    CompilerTraceCallback callback;
    pid_t pid = CreateShellProcess(command.c_str(), info);
    close(fd_pipe[1]);
    if (pid < 0) {
        LOG(INFO)<<"Compilation failed";
        close(fd_pipe[0]);
        WriteUint32(sock, INTERNAL_ERROR);
        return -1;
    }
    static signed char error_message[4096];
    int count = Readn(fd_pipe[0], error_message, sizeof(error_message));
    close(fd_pipe[0]);
    if (count < 0) {
        LOG(ERROR)<<"Fail to read error messages";
        WriteUint32(sock, INTERNAL_ERROR);
        return -1;
    }
    int status = 0;
    if (count == sizeof(error_message)) {
        kill(pid, SIGKILL);
    }
    while (waitpid(pid, &status, 0) < 0) {
        if (errno != EINTR) {
            LOG(SYSCALL_ERROR);
            return INTERNAL_ERROR;
        }
    }
    if (count == sizeof(error_message)) {
        status = 1;
    } else {
        if (WIFSIGNALED(status)) {
            LOG(ERROR)<<"Compilation terminated by signal "<<WTERMSIG(status);
            WriteUint32(sock, INTERNAL_ERROR);
            return -1;
        }
        status = WEXITSTATUS(status);
    }
    if (status) {
        if (status >= 126) {
            LOG(INFO)<<"Running compile.sh failed";
            WriteUint32(sock, INTERNAL_ERROR);
            return -1;
        } else {
            LOG(INFO)<<"Compilation error";
            WriteUint32(sock, COMPILATION_ERROR);
            if (callback.TooManyFiles()) {
                strcpy((char*)error_message, "The compiler generates too many files");
                count = strlen((char*)error_message);
            }
            uint32_t len = htonl(count);
            Writen(sock, &len, sizeof(len));
            for (int i = 0; i < count; ++i) {
                if (error_message[i] <= 0) {
                    error_message[i] = '?';
                }
            }
            Writen(sock, error_message, count);
            return 1;
        }
    }
    LOG(INFO)<<"Compilation done";
    return 0;
}
