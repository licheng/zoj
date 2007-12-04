#include <stdio.h>
#include <stdlib.h>
#include <string>

#include <errno.h>
#include <fcntl.h>
#include <sched.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>

#include "check.h"
#include "compile.h"
#include "judge.h"
#include "judge_result.h"
#include "kmmon-lib.h"
#include "logging.h"
#include "params.h"
#include "run.h"
#include "util.h"

// The root directory which contains problems, scripts and working directory of
// the client
DEFINE_ARG(string, root, "");

// The uid for executing the program to be judged
DEFINE_ARG(int, uid, "");

// The gid for executing the program to be judged
DEFINE_ARG(int, gid, "");

// Executes the "judge" command.
int execJudgeCommand(int fdSocket,
                     const string& sourceFileType,
                     const string& problemName,
                     const string& testcase,
                     const string& version,
                     int timeLimit,
                     int memoryLimit,
                     int outputLimit) {
    string programName = "P" + problemName;
    char buffer[PATH_MAX + 1];
    string probDir = ARG_root + "/prob/";
    int count = readlink((probDir + problemName + "/current").c_str(),
                         buffer,
                         sizeof(buffer));
    if (count == -1) {
		if (errno == ENOENT) {
			sendReply(fdSocket, NO_SUCH_PROBLEM);
			return 0;
		}
        LOG(SYSCALL_ERROR);
        sendReply(fdSocket, SERVER_ERROR);
        return -1;
    }
    buffer[count] = 0;
    if (version != buffer) {
        sendReply(fdSocket, NO_SUCH_PROBLEM);
        return 0;
    }
    sendReply(fdSocket, READY);
    
    // save the file
    if (saveFile(fdSocket, programName + "." + sourceFileType) == -1) {
        sendReply(fdSocket, SERVER_ERROR);
        return -1;
    }

    string sourceFilename = programName + "." + sourceFileType;
    string exeFilename = programName;
    string problemPath = probDir + problemName + "/" + version;
    string specialJudgeFilename = problemPath + "/judge";
    if (doCompile(fdSocket, sourceFilename) == -1) {
        return -1;
    }
    for (int i = 0;; i++) {
        if (testcase != "*" && testcase != "?") {
            sscanf(testcase.c_str(), "%d", &i);
        }
        sprintf(buffer, "%d", i);
        string inputFilename = problemPath + "/input." + buffer;
        string outputFilename = problemPath + "/output." + buffer;
        string programOutputFilename = programName + ".out." + buffer;
        if (access(inputFilename.c_str(), F_OK) != 0) {
			if (i == 0) {
                sendReply(fdSocket, INTERNAL_ERROR);
                sprintf(buffer, "No such test case %s", testcase.c_str());
                writen(fdSocket, buffer, sizeof(buffer));
				return -1;
			}
            break;
        }
        int result = doRun(fdSocket,
                           programName,
                           sourceFileType,
                           inputFilename,
                           programOutputFilename,
                           timeLimit,
                           memoryLimit,
                           outputLimit);
        if (result == -1) {
            return -1;
        }
        if (result == 0) {
            result = doCheck(fdSocket,
                             inputFilename,
                             outputFilename,
                             programOutputFilename,
                             specialJudgeFilename);
        }
        if (testcase != "*" && testcase != "?" ||
            result != ACCEPTED && testcase == "?") {
            break;
        }
    }
    return 0;
}
