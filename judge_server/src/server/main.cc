#include <stdio.h>
#include <stdlib.h>

#include <string>
#include <sstream>

#include <asm/param.h>
#include <errno.h>
#include <fcntl.h>
#include <netinet/in.h>
#include <sys/resource.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <sys/user.h>
#include <sys/wait.h>
#include <unistd.h>

#include "judge.h"
#include "judge_result.h"
#include "kmmon-lib.h"
#include "logging.h"
#include "params.h"
#include "run.h"
#include "save_problem.h"
#include "trace.h"
#include "util.h"

static int fdServerSocket = 0;

static pid_t* pids;

bool isSupportedSourceFileType(const std::string& sourceFileType) {
    return (int) LANG.find("," + sourceFileType + ",") >= 0;
}

#define WS " \t\r\n"

int dispatch(int fdSocket, const char* command_line) {
    char buffer[MAX_BUFFER_SIZE];
    strncpy(buffer, command_line, sizeof(buffer) - 1);
    buffer[sizeof(buffer) - 1] = 0;
    char *p = buffer;
    const char* command = strsep(&p, WS);
    if (strcmp(command, "saveprob") == 0) {
        const char* problem_name = strsep(&p, WS);
        const char* version = strsep(&p, WS);
        if (problem_name == NULL || version == NULL) {
            INPUT_FAIL(fdSocket, "Insufficient number of arguments: "<<command_line);
        }
        const std::string path =
            JUDGE_ROOT + "/prob/" + problem_name + "/" + version;
        if (execSaveprobCommand(fdSocket, problem_name, version) == -1) {
            system(("rm -rf " + path + " " + path + ".link").c_str());
            return -1;
        }
        return 0;
    } else if (strcmp(command, "judge") == 0) {
        const char* sourceFileType = strsep(&p, WS);
        const char* problem_name = strsep(&p, WS);
        const char* testcase = strsep(&p, WS);
        const char* version = strsep(&p, WS);
        const char* time_limit = strsep(&p, WS);
        const char* memory_limit = strsep(&p, WS);
        const char* output_limit = strsep(&p, WS);
        if (sourceFileType == NULL ||
            problem_name == NULL ||
            testcase == NULL ||
            version == NULL ||
            time_limit == NULL ||
            memory_limit == NULL ||
            output_limit == NULL) {
            INPUT_FAIL(fdSocket, "Insufficient number of arguments: "<<command_line);
        }
        if (!isSupportedSourceFileType(sourceFileType)) {
            INPUT_FAIL(fdSocket, "Unsupported source file type "<<sourceFileType);
        }
        int tl, ml, ol;
        if (sscanf(time_limit, "%d", &tl) < 1 || tl < 0) {
            INPUT_FAIL(fdSocket, "Invalid time limit: "<<time_limit);
        }
        if (tl > MAX_TIME_LIMIT) {
            INPUT_FAIL(fdSocket, "Time limit too large: "<<time_limit);
        }
        if (sscanf(memory_limit, "%d", &ml) < 1 || ml <= 0) {
            INPUT_FAIL(fdSocket, "Invalid memory limit: "<<memory_limit);
        }
        if (ml > MAX_MEMORY_LIMIT) {
            INPUT_FAIL(fdSocket, "Memory limit too large: "<<memory_limit);
        }
        if (sscanf(output_limit, "%d", &ol) < 1 || ol <= 0) {
            INPUT_FAIL(fdSocket, "Invalid output limit: "<<output_limit);
        }
        if (ol > MAX_OUTPUT_LIMIT) {
            INPUT_FAIL(fdSocket, "Output limit too large: "<<output_limit);
        }

        int ret = execJudgeCommand(fdSocket,
                                   sourceFileType,
                                   problem_name,
                                   testcase,
                                   version,
                                   tl,
                                   ml,
                                   ol);
        
        // clear all temporary files.
        system("rm -f *");
        return ret;
    } else {
        INPUT_FAIL(fdSocket, "Unrecognized command: "<<command);
        return -1;
    }
}

int alreadyRunning() {
    int fd = open("judge.pid", O_RDWR | O_CREAT, 0640);
    if (fd < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to open judge.pid";
        exit(1);
    }
    if (lockFile(fd, F_SETLK) == -1) {
        if (errno == EACCES || errno == EAGAIN) {
            close(fd);
            return 1;
        } else {
            LOG(SYSCALL_ERROR)<<"Fail to lock judge.pid";
            exit(1);
        }
    }
    ftruncate(fd, 0);
    char buffer[20];
    sprintf(buffer, "%ld", (long)getpid());
    write(fd, buffer, strlen(buffer) + 1);
    return 0;
}

void childMain() {
    installSignalHandler(SIGPIPE, SIG_IGN);
    installSignalHandler(SIGTERM, SIG_DFL);
    installHandlers();
    while (1) {
        sockaddr_in address;
        socklen_t addressLength = sizeof(sockaddr_in);
        int fdClientSocket = accept(
                fdServerSocket, (struct sockaddr*)&address, &addressLength);
        if (fdClientSocket == -1) {
            LOG(SYSCALL_ERROR);
            continue;
        }
        char buffer[65];
        int num = readn(fdClientSocket, buffer, sizeof(buffer) - 1);
        if (num == 0) {
            sendReply(fdClientSocket, INTERNAL_ERROR);
            writen(fdClientSocket, "No input", 8);
        } else if (num > 0) {
            while (num > 0 && buffer[num - 1] == ' ') {
                num--;
            }
            buffer[num] = 0;
            LOG(INFO)<<buffer;
            dispatch(fdClientSocket, buffer);
        }
        close(fdClientSocket);
    }
}

pid_t createChild() {
    pid_t pid = fork();
    if (pid < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create child";
        return -1;
    } else if (pid > 0) {
        return pid;
    } else {
        char buffer[MAX_BUFFER_SIZE];
        sprintf(buffer, "working/%d", getpid());
        if (mkdir(buffer, 0777) < 0) {
            LOG(SYSCALL_ERROR)<<"Fail to create dir "<<buffer;
            exit(1);
        }
        if (chdir(buffer) < 0) {
            LOG(SYSCALL_ERROR)<<"Fail to change working dir to "<<buffer;
            exit(1);
        }
        childMain(); // never returns
        exit(0);
    }
}

static void sigchldHandler(int signum) {
    int status;
    pid_t pid = wait(&status);
    if (WIFSIGNALED(status)) {
        LOG(ERROR)<<"Error occurs! Signal number "<<WTERMSIG(status);
    }
    char buffer[MAX_BUFFER_SIZE];
    sprintf(buffer, "rm -rf working/%d", pid);
    system(buffer);
}

static void sigtermHandler(int signum) {
    LOG(INFO)<<"Terminate";
    for (int i = 0; i < MAX_JOBS; i++) {
        kill(pids[i], KMMON_SIG);
    }
    exit(0);
}

int main(int argc, char* argv[]) {
    if (parseArguments(argc, argv) < 0) {
        return 1;
    }
    daemonize();
    if (alreadyRunning()) {
        return 1;
    }
    if (initAllowedFilesMap(LANG) == -1) {
        return 1;
    }
    sigset_t mask;
    sigemptyset(&mask);
    sigaddset(&mask, SIGTERM);
    sigaddset(&mask, SIGCHLD);
    installSignalHandler(SIGCHLD, sigchldHandler, 0, mask);
    installSignalHandler(SIGTERM, sigtermHandler, 0, mask);
    fdServerSocket = createServerSocket(SERVER_PORT);
    if (fdServerSocket == -1) {
        return 1;
    }
    //childMain();
    pids = (pid_t*) malloc(sizeof(pid_t) * MAX_JOBS);
    memset(pids, 0, sizeof(pid_t) * MAX_JOBS);
    for (int i = 0; i < MAX_JOBS; i++) {
        pids[i] = createChild();
    }
    while (1) {
        pause();
    }
}
