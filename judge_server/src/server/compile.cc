#include "compile.h"

#include <string>

#include <sys/wait.h>
#include <unistd.h>

#include "logging.h"
#include "params.h"
#include "trace.h"
#include "util.h"

namespace {

pid_t pid = 0;

class Monitor: public ProcessMonitor {
    public:
        virtual void onSIGCHLD(pid_t) { }
        virtual void terminate() {
            if (pid > 0) {
                kill(pid, SIGKILL);
            }
            ProcessMonitor::terminate();
        }
};

}

int compile(const std::string& sourceFilename, char buffer[], int* bufferSize) {
    int fdPipe[2];
    if (pipe(fdPipe) < 0) {
        LOG(SYSCALL_ERROR);
        return -1;
    }
    std::string command =
        JUDGE_ROOT + "/script/compile.sh '" + sourceFilename + "'";
    StartupInfo info;
    info.fdStderr = fdPipe[1];
    info.timeLimit = 30;
    pid = 0;
    Monitor monitor;
    pid = createShellProcess(command.c_str(), info);
    close(fdPipe[1]);
    if (pid < 0) {
        close(fdPipe[0]);
        return -1;
    }
    *bufferSize = readn(fdPipe[0], buffer, *bufferSize);
    close(fdPipe[0]);
    if (bufferSize < 0) {
        return -1;
    }
    int status;
    waitpid(pid, &status, 0);
    if (WIFSIGNALED(status)) {
        LOG(ERROR)<<"Compilation terminated by signal "<<WTERMSIG(status);
        return -1;
    }
    if (WEXITSTATUS(status)) {
        return 1;
    }
    return 0;
}

static char buffer[16385];

int doCompile(int fdSocket, const std::string& sourceFilename) {
    sendReply(fdSocket, COMPILING);
    int bufferSize;
    int result = compile(sourceFilename, buffer, &bufferSize);
    if (result) {
        if (result == -1) {
            sendReply(fdSocket, SERVER_ERROR);
            return -1;
        } else {
            sendReply(fdSocket, COMPILATION_ERROR);
            writen(fdSocket, buffer, bufferSize);
            return -1;
        }
    }
    return 0;
}
