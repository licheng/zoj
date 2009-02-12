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

#include "environment.h"

#include <stdlib.h>
#include <sys/stat.h>
#include <unistd.h>

#include "args.h"
#include "logging.h"
#include "strutil.h"

DEFINE_ARG(string, root, "The root directory of the client");

Environment* Environment::instance_ = NULL;

const Environment* Environment::GetInstance() {
    if (instance_ == NULL) {
        instance_ = new Environment(ARG_root);
    }
    return instance_;
}

string Environment::GetWorkingDir() const {
    return StringPrintf("%s/working/%u", root_.c_str(), getpid());
}

int Environment::ChangeToWorkingDir() const {
    const string& working_dir = this->GetWorkingDir();
    if (mkdir(working_dir.c_str(), 0777) < 0) {
        if (errno != EEXIST) {
            LOG(SYSCALL_ERROR)<<"Fail to create dir "<<working_dir;
            return -1;
        }
    }
    if (chdir(working_dir.c_str()) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to change working dir to "<<working_dir;
        return 1;
    }
    return 0;
}

void Environment::ClearWorkingDir() const {
    if (system(StringPrintf("rm -f %s/*", this->GetWorkingDir().c_str()).c_str())) {
    }
}

string Environment::GetProblemDir(int problem_id, int revision) const {
    return StringPrintf("%s/prob/%u/%u", root_.c_str(), problem_id, revision);
}

string Environment::GetCompilationScript() const {
    return root_ + "/script/compile.sh";
}

string Environment::GetLogDir() const {
    return root_ + "/log";
}

string Environment::GetServerSockName() const {
    return StringPrintf("%s/working/server_log.sock", root_.c_str());
}

string Environment::GetClientSockName() const {
    return StringPrintf("%s/client_log.sock", this->GetWorkingDir().c_str());
}
