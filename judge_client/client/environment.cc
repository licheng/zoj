#include "environment.h"

#include <sys/stat.h>
#include "unistd.h"

#include "logging.h"
#include "strutil.h"

Environment* Environment::instance_ = new Environment();

string Environment::GetWorkingDir() {
    return StringPrintf("%s/working/%u", root_.c_str(), getpid());
}

int Environment::ChangeToWorkingDir() {
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

void Environment::ClearWorkingDir() {
    system(StringPrintf("rm -f %s/*", this->GetWorkingDir().c_str()).c_str());
}

string Environment::GetProblemDir(int problem_id, int revision) {
    return StringPrintf("%s/prob/%u/%u", root_.c_str(), problem_id, revision);
}

string Environment::GetCompilationScript() {
    return root_ + "/script/compile.sh";
}

string Environment::GetLogDir() {
    return root_ + "/log";
}

string Environment::GetServerSockName() {
    return StringPrintf("%s/working/server_log.sock", root_.c_str());
}

string Environment::GetClientSockName() {
    return StringPrintf("%s/client_log.sock", this->GetWorkingDir().c_str());
}
