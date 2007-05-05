#include "save_problem.h"

#include <dirent.h>
#include <unistd.h>

#include "compile.h"
#include "util.h"

bool isSupportedSourceFileType(const std::string& sourceFileType);

int testAndCreateProblemDirectory(int fdSocket,
                                  const std::string& problem_name,
                                  const std::string& version) {
    std::string path = "../../prob/" + problem_name;
    if (mkdir(path.c_str(), 0750) == -1 && errno != EEXIST) {
        LOG(SYSCALL_ERROR)<<"Fail to create dir "<<path;
        SERVER_FAIL(fdSocket);
    }
    path += "/" + version;
    if (mkdir(path.c_str(), 0750) == -1) {
        if (errno == EEXIST) {
            sendReply(fdSocket, PROBLEM_EXIST);
            return -1;
        } else {
            LOG(SYSCALL_ERROR)<<"Fail to create dir "<<path;
            SERVER_FAIL(fdSocket);
        }
    }
    return 0;
}
static char buffer[16385];

int validateProblemFiles(int fdSocket, const std::string& path) {
    DIR* dir = opendir(path.c_str());
    if (dir == NULL) {
        LOG(SYSCALL_ERROR)<<"Fail to open dir "<<path;
        SERVER_FAIL(fdSocket);
    }

    int testcaseFlags[100] = {0};
    int judge_flag = 0;
    for (;;) {
        struct dirent* entry = readdir(dir);
        if (entry == NULL) {
            closedir(dir);
            break;
        }
        std::string filename = entry->d_name;
        if (filename.find("judge.") == 0) {
            if (judge_flag) {
                closedir(dir);
                INPUT_FAIL(fdSocket, "Duplicated judge "<<filename);
            }
            judge_flag = 1;
            std::string sourceFileType =
                filename.substr(filename.find('.') + 1);
            if (!isSupportedSourceFileType(sourceFileType)) {
                closedir(dir);
                INPUT_FAIL(fdSocket, "Unsupported judge source file type "
                           <<sourceFileType);
            }
            int bufferSize = sizeof(buffer) - 1;
            int result = compile(path + "/" + filename, buffer, &bufferSize);
            if (result == -1) {
                closedir(dir);
                SERVER_FAIL(fdSocket);
            } else if (result > 0) {
                writen(fdSocket, buffer, bufferSize);
                closedir(dir);
                return -1;
            }
        } else if (filename.find("input.") == 0 ||
                   filename.find("output.") == 0) {
            std::string extension = filename.substr(filename.find('.') + 1);
            int testcase;
            if (sscanf(extension.c_str(), "%d", &testcase) < 1 ||
                testcase < 0 ||
                testcase >= 100) {
                closedir(dir);
                INPUT_FAIL(fdSocket, "Invalid file entry "<<filename);
            }
            testcaseFlags[testcase] |= (filename[0] == 'o' ? 2 : 1);
        } else if (filename != "." &&
                   filename != ".." &&
                   filename != "data.zip") {
            closedir(dir);
            INPUT_FAIL(fdSocket, "Invalid file entry "<<filename);
        }
    }
    const int maxTestcases = sizeof(testcaseFlags) / sizeof(testcaseFlags[0]);
    for (int i = 0; i < maxTestcases; i++) {
        if (i && testcaseFlags[i] && !testcaseFlags[i - 1]) {
            INPUT_FAIL(fdSocket, "Testcases should be consecutive numbers "
                                 "starting with 0");
        }
        if (testcaseFlags[i] == 2) {
            INPUT_FAIL(fdSocket, "Missing file input."<<i);
        } else if (testcaseFlags[i] == 1 && !judge_flag) {
            INPUT_FAIL(fdSocket, "Missing either judge file or output."<<i);
        }
    }
    return 0;
}

int execSaveprobCommand(int fdSocket,
                        const std::string& problem_name,
                        const std::string& version) {
    if (testAndCreateProblemDirectory(fdSocket, problem_name, version) < 0) {
        return 0;
    }
    
    if (sendReply(fdSocket, READY) == -1) {
        return -1;
    }
    
    std::string path = "../../prob/" + problem_name + "/" + version;
    std::string zip = path + "/data.zip";
    if (saveFile(fdSocket, zip) == -1) {
        SERVER_FAIL(fdSocket);
    }

    int status = system(("/usr/bin/unzip -d '" + path + "' '" + zip +
                         "' >/dev/null 2>/dev/null").c_str());
    if (WEXITSTATUS(status) != 0) {
        LOG(ERROR)<<"Fail to unzip "<<zip<<". Returned status: "<<status;
        SERVER_FAIL(fdSocket);
    }

    if (validateProblemFiles(fdSocket, path) < 0) {
        return -1;
    }
    
    if (symlink(version.c_str(), (path + ".link").c_str()) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create symlink for "<<path;
        SERVER_FAIL(fdSocket);
    }
    if (rename((path + ".link").c_str(),
               ("../../prob/" + problem_name + "/current").c_str()) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to rename "<<path;
        SERVER_FAIL(fdSocket);
    }
    if (symlink("input.0", (path + "/input").c_str()) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create symlink for "<<path<<"/input.0";
        SERVER_FAIL(fdSocket);
    }
    if (symlink("output.0", (path + "/output").c_str()) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create symlink for "<<path<<"/output.0";
        SERVER_FAIL(fdSocket);
    }
    sendReply(fdSocket, READY);
    return 0;
}


