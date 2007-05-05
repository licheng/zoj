#include "run.h"

#include <map>
#include <string>
#include <vector>

#include <errno.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include "logging.h"
#include "params.h"
#include "trace.h"
#include "util.h"

static std::map<std::string, std::vector<std::string> > allowedFilesMap;

int initAllowedFilesMap(const std::string& supportedSourceFileTypes) {
    int i = 0;
    for (;;) {
        int j = supportedSourceFileTypes.find(',', i + 1);
        if (j < 0) {
            return 0;
        }
        std::string sourceFileType = supportedSourceFileTypes.substr(i, j - i);
        std::vector<std::string>& allowedFiles = allowedFilesMap[sourceFileType];
        FILE* fp = popen(("script/allowedfiles.sh " + sourceFileType).c_str(),
                         "r");
        if (fp == NULL) {
            LOG(SYSCALL_ERROR)<<"Fail to run allowedfiles.sh";
            return -1;
        }
        char buffer[PATH_MAX + 1];
        while (fgets(buffer, sizeof(buffer), fp)) {
            int length = strlen(buffer);
            while (length && isspace(buffer[length - 1])) {
                length--;
            }
            buffer[length] = 0;
            allowedFiles.push_back(buffer);
        }
        pclose(fp);
        i = j + 1;
    }
}

int trace(int fdSocket,
          pid_t pid,
          int timeLimit,
          int memoryLimit,
          const ProcessMonitor& monitor) {
    int result = -1;
    double timeConsumption = 0;
    int memoryConsumption = 0;
    do {
        double ts;
        int ms;
        if (monitor.getResult() == JUDGING) {
            ts = monitor.getTimeConsumption();
            ms = monitor.getMemoryConsumption();
        } else {
            ts = readTimeConsumption(pid);
            ms = readMemoryConsumption(pid);
        }
        if (ts > timeConsumption) {
            timeConsumption = ts;
        }
        if (ms > memoryConsumption) {
            memoryConsumption = ms;
        }
        if (timeConsumption > timeLimit) {
            result = TIME_LIMIT_EXCEEDED;
        }
        if (result == TIME_LIMIT_EXCEEDED) {
            timeConsumption = timeLimit + 0.01;
        }
        if (memoryConsumption > memoryLimit) {
            result = MEMORY_LIMIT_EXCEEDED;
        }
        if (result == MEMORY_LIMIT_EXCEEDED) {
            memoryConsumption = memoryLimit + 1;
        }
        if (result < 0 &&
            monitor.getResult() &&
            monitor.getResult() != RUNNING) {
            result = monitor.getResult();
        }
        char buffer[128];
        snprintf(buffer, sizeof(buffer), "%.3lf %d\n",
                 timeConsumption, memoryConsumption);
        if (writen(fdSocket, buffer, strlen(buffer)) < 0) {
            if (!monitor.hasExited()) {
                kill(pid, SIGKILL);
            }
            result = SERVER_ERROR;
        }
        struct timespec request, remain;
        request.tv_sec = 1;
        request.tv_nsec = 0;
        while (result < 0 &&
               !monitor.hasExited() &&
               nanosleep(&request, &remain) < 0) {
            if (errno != EINTR) {
                LOG(SYSCALL_ERROR);
                kill(pid, SIGKILL);
                result = SERVER_ERROR;
                break;
            }
            request = remain;
        }
    } while (result < 0);
    return result;
}

static pid_t pid;

class ExecutiveFileMonitor: public ProcessMonitor {
    public:
        ExecutiveFileMonitor(const std::vector<std::string>& allowedFiles)
            : allowedFiles(allowedFiles) {
            pid = 0;
        }

        virtual int onOpen(const std::string& pathname, int flags) {
            if (!isFlagsReadOnly(flags)) {
                LOG(ERROR)<<"Not allowed to open file "<<pathname<<" for writing";
                return 0;
            }
            for (int i = 0; i < (int)this->allowedFiles.size(); i++) {
                if (this->allowedFiles[i] == pathname) {
                    LOG(ERROR)<<"Not allowed to open file "<<pathname;
                    return 1;
                }
            }
            return 0;
        }

        virtual void onExit(pid_t pid) {
            this->timeConsumption = readTimeConsumption(pid);
            this->memoryConsumption = readMemoryConsumption(pid);
        }

        virtual void terminate() {
            if (pid > 0) {
                kill(pid, SIGKILL);
            }
            ProcessMonitor::terminate();
        }

    private:
        const std::vector<std::string>& allowedFiles;
};

int runExe(int fdSocket,
            const std::string& exeFilename,
            const std::string& stdinFilename,
            const std::string& stdoutFilename,
            int timeLimit,
            int memoryLimit,
            int outputLimit,
            const std::vector<std::string>& allowedFiles) {
    const char* commands[] = {exeFilename.c_str(), exeFilename.c_str(), NULL};
    StartupInfo info;
    info.stdinFilename = stdinFilename.c_str();
    info.stdoutFilename = stdoutFilename.c_str();
    info.uid = JOB_UID;
    info.gid = JOB_GID;
    info.timeLimit = timeLimit;
    info.memoryLimit = memoryLimit;
    info.outputLimit = outputLimit;
    info.procLimit = 1;
    info.fileLimit = 5;
    info.trace = 1;
    pid = createProcess(commands, info);
    if (pid == -1) {
        return SERVER_ERROR;
    }
    ExecutiveFileMonitor monitor(allowedFiles);
    int result = trace(fdSocket, pid, timeLimit, memoryLimit, monitor);
    if (writen(fdSocket, "-1 -1\n", 6) < 0) {
        return -1;
    }
    return result;
}

inline int isNativeExe(const std::string& sourceFileType) {
    return sourceFileType == "cc" ||
           sourceFileType == "c" ||
           sourceFileType == "pas";
}

int doRun(int fdSocket,
          const std::string& programName,
          const std::string& sourceFileType,
          const std::string& stdinFilename,
          const std::string& stdoutFilename,
          int timeLimit,
          int memoryLimit,
          int outputLimit) {
    sendReply(fdSocket, RUNNING);
    int result;
    if (isNativeExe(sourceFileType)) {
        result = runExe(fdSocket,
                        programName,
                        stdinFilename,
                        stdoutFilename,
                        timeLimit,
                        memoryLimit,
                        outputLimit,
                        allowedFilesMap[sourceFileType]);
    } else {
        return -1;
    }
    if (result) {
        sendReply(fdSocket, result);
        return -1;
    }
    return 0;
}
