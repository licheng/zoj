/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either revision 3 of the License, or
 * (at your option) any later revision.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */
#include <dirent.h>
#include <sys/types.h>

#include "text_checker.h"
#include "special_checker.h"
#include "common_io.h"
#include "compiler.h"
#include "environment.h"
#include "global.h"
#include "java_runner.h"
#include "logging.h"
#include "protocol.h"
#include "native_runner.h"
#include "strutil.h"
#include "trace.h"
#include "util.h"

// Reads the file content from the given file descriptor and writes the file
// specified by output_filename. Creates the file if not exists.
//
// Return 0 if success, or -1 if any error occurs.
int SaveFile(int sock, const string& output_filename, size_t size) {
    int fd = open(output_filename.c_str(), O_RDWR | O_CREAT | O_TRUNC, 0640);
    if (fd == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create file "<<output_filename;
        return -1;
    }
    static char buffer[4096];
    while (size && !global::terminated) {
        int count = min(size, sizeof(buffer));
        count = Readn(sock, buffer, count);
        if (count <= 0) {
            LOG(ERROR)<<"Fail to read file";
            close(fd);
            return -1;
        }
        if (Writen(fd, buffer, count) == -1) {
            LOG(ERROR)<<"Fail to write to "<<output_filename;
            close(fd);
            return -1;
        }
        size -= count;
    }
    close(fd);
    if (size) {
        LOG(ERROR)<<"Terminated";
        return -1;
    }
    return 0;
}

int ExecJudgeCommand(int sock, int* problem_id, int* revision) {
    uint32_t submission_id;
    uint32_t _problem_id;
    uint32_t _revision;
    uint32_t checksum;
    if (ReadUint32(sock, &submission_id) == -1 ||
        ReadUint32(sock, &_problem_id) == -1 ||
        ReadUint32(sock, &_revision) == -1 ||
        ReadUint32(sock, &checksum)) {
        return -1;
    }
    *problem_id = _problem_id;
    *revision = _revision;
    LOG(INFO)<<StringPrintf("Submission:%u Problem:%u Revision:%u",
                            (unsigned int)submission_id,
                            (unsigned int)*problem_id,
                            (unsigned int)*revision);
    if (CheckSum(CMD_JUDGE) +
        CheckSum(submission_id) +
        CheckSum(_problem_id) +
        CheckSum(_revision) != checksum) {
        LOG(ERROR)<<"Invalid checksum "<<checksum;
        WriteUint32(sock, INVALID_INPUT);
        return -1;
    }
    string problem_dir = Environment::GetInstance()->GetProblemDir(*problem_id, *revision);
    if (access(problem_dir.c_str(), F_OK) == 0) {
        WriteUint32(sock, READY);
        return 0;
    } else if (errno != ENOENT) {
        LOG(SYSCALL_ERROR)<<"Fail to access "<<problem_dir;
        WriteUint32(sock, INTERNAL_ERROR);
        return -1;
    } else {
        LOG(ERROR)<<"No such problem";
        WriteUint32(sock, NO_SUCH_PROBLEM);
        return 1;
    }
}

int ExecCompileCommand(int sock, int* compiler_id) {
    uint32_t id;
    uint32_t source_file_size;
    uint32_t checksum;
    if (ReadUint32(sock, &id) == -1 ||
        ReadUint32(sock, &source_file_size) == -1 ||
        ReadUint32(sock, &checksum) == -1) {
        return -1;
    }
    LOG(INFO)<<StringPrintf("Compiler:%u", (unsigned int)id);
    if (CheckSum(CMD_COMPILE) +
        CheckSum(id) +
        CheckSum(source_file_size) != checksum) {
        LOG(ERROR)<<"Invalid checksum "<<checksum;
        WriteUint32(sock, INVALID_INPUT);
        return -1;
    }
    const Compiler* compiler = CompilerManager::GetInstance()->GetCompiler(id);
    if (compiler == NULL) {
        LOG(ERROR)<<"Invalid compiler "<<(int)id;
        WriteUint32(sock, INVALID_INPUT);
        return -1;
    }
    *compiler_id = id;
    LOG(INFO)<<"Compiler:"<<compiler->compiler_name();
    WriteUint32(sock, READY);
    const string& source_filename = compiler->source_filename();
    LOG(INFO)<<"Saving source file "<<source_filename<<". Length:"<<source_file_size;
    if (SaveFile(sock, source_filename.c_str(), source_file_size) == -1) {
        WriteUint32(sock, INTERNAL_ERROR);
        return -1;
    }

    switch (compiler->Compile(sock, source_filename)) {
        case -1:
            return -1;
        case 0:
            WriteUint32(sock, READY);
            break;
        default:
            *compiler_id = -1;
    }
    return 0;
}

int ExecTestCaseCommand(int sock, int problem_id, int revision, int compiler, int uid, int gid) {
    uint32_t testcase;
    uint32_t time_limit;
    uint32_t memory_limit;
    uint32_t output_limit;
    uint32_t checksum;
    if (ReadUint32(sock, &testcase) == -1 ||
        ReadUint32(sock, &time_limit) == -1 ||
        ReadUint32(sock, &memory_limit) == -1 ||
        ReadUint32(sock, &output_limit) == -1 ||
        ReadUint32(sock, &checksum)) {
        return -1;
    }
    LOG(INFO)<<StringPrintf("Testcase:%u TL:%u ML:%u OL:%u",
                            (unsigned int)testcase,
                            (unsigned int)time_limit,
                            (unsigned int)memory_limit,
                            (unsigned int)output_limit);
    if (CheckSum(CMD_TESTCASE) +
        CheckSum(testcase) + 
        CheckSum(time_limit) + 
        CheckSum(memory_limit) + 
        CheckSum(output_limit) != checksum) {
        LOG(ERROR)<<"Invalid checksum "<<checksum;
        WriteUint32(sock, INVALID_INPUT);
        return -1;
    }
    string problem_dir = Environment::GetInstance()->GetProblemDir(problem_id, revision);
    if (access(problem_dir.c_str(), F_OK) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to access "<<problem_dir;
        if (errno != ENOENT) {
            WriteUint32(sock, INTERNAL_ERROR);
        } else {
            WriteUint32(sock, INVALID_INPUT);
        }
        return -1;
    }
    string input_filename = StringPrintf("%s/%u.in", problem_dir.c_str(), (unsigned int)testcase);
    string output_filename = StringPrintf("%s/%u.out", problem_dir.c_str(), (unsigned int)testcase);
    string special_judge_filename = problem_dir + "/judge";
    if (access(input_filename.c_str(), F_OK) == -1) {
        LOG(ERROR)<<"Invalid test case "<<testcase;
        WriteUint32(sock, INVALID_INPUT);
        return -1;
    }
    if (unlink("input") < 0) {
        if (errno != ENOENT) {
            LOG(SYSCALL_ERROR)<<"Fail to remove symlink input";
            WriteUint32(sock, INTERNAL_ERROR);
            return -1;
        }
    }
    if (unlink("output") < 0) {
        if (errno != ENOENT) {
            LOG(SYSCALL_ERROR)<<"Fail to remove symlink output";
            WriteUint32(sock, INTERNAL_ERROR);
            return -1;
        }
    }
    if (symlink(input_filename.c_str(), "input") == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create symlink to "<<input_filename;
        WriteUint32(sock, INTERNAL_ERROR);
        return -1;
    }
    int result;
    if (compiler == 4) {
        JavaRunner runner;
        result = runner.Run(sock, time_limit, memory_limit, output_limit, uid, gid);
    } else {
        NativeRunner runner;
        result = runner.Run(sock, time_limit, memory_limit, output_limit, uid, gid);
    }
    if (result) {
        return result == -1 ? -1 : 0;
    }
    if (symlink(output_filename.c_str(), "output") == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create symlink to "<<output_filename;
        WriteUint32(sock, INTERNAL_ERROR);
        return -1;
    }
    if (access(special_judge_filename.c_str(), F_OK) == 0) {
        SpecialChecker checker(special_judge_filename);
        return checker.Check(sock);
    } else {
        TextChecker checker;
        return checker.Check(sock);
    }
}

int CheckData(int sock, const string& data_dir) {
    DIR* dir = opendir(data_dir.c_str());
    if (dir == NULL) {
        LOG(SYSCALL_ERROR)<<"Can not open dir "<<data_dir;
        WriteUint32(sock, INTERNAL_ERROR);
        return -1;
    }
    int ret = 0;
    vector<int> in, out;
    string judge;
    const Compiler* judge_compiler = NULL;
    for (;;) {
        struct dirent* entry = readdir(dir);
        if (entry == NULL) {
            break;
        }
        if (strcmp(entry->d_name, ".") == 0 ||
            strcmp(entry->d_name, "..") == 0 ||
            strcmp(entry->d_name, "data.zip") == 0) {
            continue;
        }
        struct stat status;
        lstat(StringPrintf("%s/%s", data_dir.c_str(), entry->d_name).c_str(), &status);
        if (!S_ISREG(status.st_mode)) {
            LOG(ERROR)<<"Invalid file "<<entry->d_name;
            ret = -1;
            break;
        }
        int index;
        if (StringEndsWith(entry->d_name, ".in")) {
            if (sscanf(entry->d_name, "%d.in", &index) != 1) {
                LOG(ERROR)<<"Invalid filename "<<entry->d_name;
                ret = -1;
                break;
            }
            in.push_back(index);
        } else if (StringEndsWith(entry->d_name, ".out")) {
            if (sscanf(entry->d_name, "%d.out", &index) != 1) {
                LOG(ERROR)<<"Invalid filename "<<entry->d_name;
                ret = -1;
                break;
            }
            out.push_back(index);
        } else if (StringStartsWith(entry->d_name, "judge.")) {
            string source_file_extension = entry->d_name + 6;
            judge_compiler = CompilerManager::GetInstance()->GetCompilerByExtension(source_file_extension);
            if (judge_compiler == NULL) {
                LOG(ERROR)<<"Unsupported judge source file type "<<source_file_extension;
                ret = -1;
                break;
            }
            judge = entry->d_name;
        } else {
            LOG(ERROR)<<"Invalid filename "<<entry->d_name;
            ret = -1;
            break;
        }
    }
    closedir(dir);
    if (ret == 0) {
        if (in.empty()) {
            LOG(ERROR)<<"Empty directory "<<data_dir;
            WriteUint32(sock, INVALID_INPUT);
            return -1;
        }
        sort(in.begin(), in.end());
        sort(out.begin(), out.end());
        if (judge_compiler == NULL) {
            for (int i = 0; i < in.size(); ++i) {
                if (i >= out.size() || in[i] < out[i]) {
                    LOG(ERROR)<<"No "<<in[i]<<".out found for "<<in[i]<<".in";
                    ret = -1;
                    break;
                } else if (in[i] > out[i]) {
                    LOG(ERROR)<<"No "<<out[i]<<".in found for "<<out[i]<<".out";
                    ret = -1;
                    break;
                }
            }
            if (out.size() > in.size()) {
                LOG(ERROR)<<"No "<<out[in.size()]<<".in found for "<<out[in.size()]<<".out";
                ret = -1;
            }
        } else if (judge_compiler->Compile(sock, data_dir + "/" + judge) == -1) {
            return -1;
        }
    }
    if (ret < 0) {
        WriteUint32(sock, INVALID_INPUT);
    }
    return ret;
}

int ExecDataCommand(int sock, unsigned int problem_id, unsigned int revision) {
    uint32_t size;
    uint32_t checksum;
    if (ReadUint32(sock, &size) == -1 ||
        ReadUint32(sock, &checksum) == -1) {
        return -1;
    }
    if (CheckSum(CMD_DATA) + CheckSum(size) != checksum) {
        LOG(ERROR)<<"Invalid checksum "<<checksum;
        WriteUint32(sock, INVALID_INPUT);
        return -1;
    }

    if (size > MAX_DATA_FILE_SIZE) {
        LOG(ERROR)<<"File size too large: "<<size;
        WriteUint32(sock, INVALID_INPUT);
        return -1;
    }

    string revision_dir = Environment::GetInstance()->GetProblemDir(problem_id, revision);
    string problem_dir = revision_dir.substr(0, revision_dir.rfind('/'));
    string tempDir = StringPrintf("%s.%u.%s",
                                  revision_dir.c_str(),
                                  getpid(),
                                  GetLocalTimeAsString("%Y%m%d%H%M%S").c_str());
    LOG(INFO)<<"Creating temporary directory "<<tempDir;
    if (mkdir(tempDir.c_str(), 0750) == -1) {
        if (errno == ENOENT) {
            LOG(INFO)<<"Up level directory missing";
            LOG(INFO)<<"Creating problem directory "<<problem_dir;
            if (mkdir(problem_dir.c_str(), 0750) == -1) {
                if (errno != EEXIST) {
                    LOG(SYSCALL_ERROR)<<"Fail to create dir "<<problem_dir;
                    WriteUint32(sock, INTERNAL_ERROR);
                    return -1;
                }
            }
            LOG(INFO)<<"Creating temporary directory "<<tempDir;
            if (mkdir(tempDir.c_str(), 0750) == -1) {
                LOG(SYSCALL_ERROR)<<"Fail to create dir "<<tempDir;
                WriteUint32(sock, INTERNAL_ERROR);
                return -1;
            }
        } else if (errno != EEXIST) {
            LOG(SYSCALL_ERROR)<<"Fail to create dir "<<tempDir;
            WriteUint32(sock, INTERNAL_ERROR);
            return -1;
        }
    }
    WriteUint32(sock, READY);
    LOG(INFO)<<"Saving data file. Size: "<<size;
    if (SaveFile(sock, tempDir + "/data.zip", size) == -1) {
        WriteUint32(sock, INTERNAL_ERROR);
        return -1;
    }
    LOG(INFO)<<"Unzipping data file";
    string command = StringPrintf("unzip '%s/data.zip' -d '%s'", tempDir.c_str(), tempDir.c_str());
    int result = system(command.c_str());
    if (result) {
        LOG(ERROR)<<"Fail to unzip data file. Command: "<<command;
        WriteUint32(sock, INVALID_INPUT);
        return -1;
    }
    LOG(INFO)<<"Checking data";
    if (CheckData(sock, tempDir) == -1) {
        return -1;
    }
    if (rename(tempDir.c_str(), revision_dir.c_str()) == -1) {
        system(StringPrintf("rm -rf '%s'", tempDir.c_str()).c_str());
        LOG(SYSCALL_ERROR)<<"Fail to rename "<<tempDir<<" to "<<revision_dir;
        if (errno != ENOTEMPTY && errno != EEXIST) {
            WriteUint32(sock, INTERNAL_ERROR);
            return -1;
        }
    }
    WriteUint32(sock, READY);
    return 0;
}

int JudgeMain(int sock, int uid, int gid) {
    if (Environment::GetInstance()->ChangeToWorkingDir() == -1) {
        WriteUint32(sock, INTERNAL_ERROR);
        close(sock);
        return 1;
    }

    // installs handlers for tracing.
    InstallHandlers();

    int ret = 0;
    int problem_id = -1;
    int revision = -1;
    int compiler = 0;
    bool data_ready = false;
    // Loops until SIGTERM or SIGPIPE is received, sighandler will set global::terminated to false
    while (!global::terminated) {
        uint32_t command;
        if (ReadUint32(sock, &command) == -1) {
            LOG(ERROR)<<"Invalid command.";
            WriteUint32(sock, INVALID_INPUT);
            ret = 1;
            break;
        }
        if (command == CMD_PING) {
            WriteUint32(sock, READY);
        } else if (command == CMD_JUDGE) {
            int result = ExecJudgeCommand(sock, &problem_id, &revision);
            if (result == -1) {
                ret = 1;
                break;
            }
            if (result == 0) {
                data_ready = true;
            } else {
                data_ready = false;
            }
            compiler = -1;
            Environment::GetInstance()->ClearWorkingDir();
        } else if (command == CMD_DATA) {
            if (problem_id < 0) {
                LOG(ERROR)<<"No problem specified.";
                WriteUint32(sock, INVALID_INPUT);
                ret = 1;
                break;
            }
            if (data_ready) {
                LOG(ERROR)<<"Data synchronization is not required.";
                WriteUint32(sock, INVALID_INPUT);
                ret = 1;
                break;
            }
            if (ExecDataCommand(sock, problem_id, revision) == -1) {
                ret = 1;
                break;
            }
            data_ready = true;
        } else if (command == CMD_COMPILE) {
            if (ExecCompileCommand(sock, &compiler) == -1) {
                ret = 1;
                break;
            }
        } else if (command == CMD_TESTCASE) {
            if (problem_id < 0) {
                LOG(ERROR)<<"No problem specified.";
                WriteUint32(sock, INVALID_INPUT);
                ret = 1;
                break;
            }
            if (data_ready == 0) {
                LOG(ERROR)<<"Data is not ready";
                WriteUint32(sock, INVALID_INPUT);
                ret = 1;
                break;
            }
            if (compiler < 0) {
                LOG(ERROR)<<"Program is not compiled";
                WriteUint32(sock, INVALID_INPUT);
                ret = 1;
                break;
            }
            if (ExecTestCaseCommand(sock, problem_id, revision, compiler, uid, gid) == -1) {
                ret = 1;
                break;
            }
        } else {
            LOG(ERROR)<<"Invalid command "<<(int)command;
            WriteUint32(sock, INVALID_INPUT);
            ret = 1;
            break;
        }
    }
    close(sock);
    Environment::GetInstance()->ClearWorkingDir();
    return ret;
}
