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

ProcessMonitor* ProcessMonitor::monitor;

int ProcessMonitor::onExecve() {
    if (this->result < 0) {
        this->result = RUNNING;
        return 1;
    } else {
        return 0;
    }
}

void ProcessMonitor::onMemoryLimitExceeded() {
    this->result = MEMORY_LIMIT_EXCEEDED;
}

void ProcessMonitor::onExit(pid_t pid) {
    this->timeConsumption = readTimeConsumption(pid);
    this->memoryConsumption = readMemoryConsumption(pid);
    this->result = 0;
}

void ProcessMonitor::onSIGCHLD(pid_t pid) {
    int status;
    while (waitpid(pid, &status, 0) < 0) {
        if (errno != EINTR) {
            this->result = SERVER_ERROR;
            return;
        }
    }
    switch (this->result) {
        case -1:
            // Before the first execve is invoked.
            this->result = SERVER_ERROR;
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
                    this->result = SERVER_ERROR;
            }
            break;
    }
}

void ProcessMonitor::onError() {
    this->result = SERVER_ERROR;
}

void ProcessMonitor::terminate() {
    system(("rm -rf working/" + toString(getpid())).c_str());
    kill(getpid(), SIGKILL);
}

int readStringFromTracedProcess(pid_t pid,
                                int address,
                                char* buffer,
                                int maxLength) {
    for (int i = 0; i < maxLength; i += 4) {
        int data;
        if (kmmon_readmem(pid, address + i, &data) < 0) {
            return -1;
        }
        char* p = (char*) &data;
        for (int j = 0; j < 4; j++, p++) {
            if (*p && i + j < maxLength) {
                buffer[i + j] = *p;
            } else {
                buffer[i + j] = 0;
                return 0;
            }
        }
    }
    buffer[maxLength] = 0;
    return 0;
}

static void sigchldHandler(int sig, siginfo_t* siginfo, void* context) {
    if (ProcessMonitor::getMonitor()) {
        ProcessMonitor::getMonitor()->onSIGCHLD(siginfo->si_pid);
    }
}

static void sigkmmonHandler(int sig, siginfo_t* siginfo, void* context) {
    if (!ProcessMonitor::getMonitor()) {
        return;
    }
    static pid_t ppid = getppid();
    pid_t pid = siginfo->si_pid;
    if (pid == ppid) {
        ProcessMonitor::getMonitor()->terminate();
        return;
    }
    int syscall = siginfo->si_value.sival_int;
    if (syscall == SYS_exit || syscall == SYS_exit_group) {
        ProcessMonitor::getMonitor()->onExit(pid);
        kmmon_continue(pid);
    } else if (syscall == SYS_brk) {
        ProcessMonitor::getMonitor()->onMemoryLimitExceeded();
        kmmon_kill(pid);
    } else if (syscall == SYS_clone ||
               syscall == SYS_fork ||
               syscall == SYS_vfork) {
        ProcessMonitor::getMonitor()->onClone();
    } else if (syscall == SYS_open) {
        char buffer[PATH_MAX + 1];
        int address, flags;
        if (kmmon_getreg(pid, EBX, &address) < 0 ||
            kmmon_getreg(pid, ECX, &flags) < 0) {
            ProcessMonitor::getMonitor()->onError();
            kmmon_kill(pid);
            return;
        }
        if (readStringFromTracedProcess(pid,
                                        address,
                                        buffer,
                                        sizeof(buffer)) < 0) {
            ProcessMonitor::getMonitor()->onError();
            kmmon_kill(pid);
        } else if (ProcessMonitor::getMonitor()->onOpen(buffer, flags)) {
            kmmon_continue(pid);
        } else {
            kmmon_kill(pid);
        }
    } else if (syscall == SYS_execve) {
        if (ProcessMonitor::getMonitor()->onExecve()) {
            kmmon_continue(pid);
        } else {
            kmmon_kill(pid);
        }
    } else {
        LOG(ERROR)<<"Unexpected syscall "<<syscall;
        ProcessMonitor::getMonitor()->onError();
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
