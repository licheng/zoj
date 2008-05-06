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

#include "check.h"
#include "global.h"
#include "compile.h"
#include "logging.h"
#include "run.h"
#include "trace.h"
#include "util.h"

DEFINE_ARG(string, lang, "All programming languages supported by this client");

// Returns true if the specified file type is supported by the server
bool IsSupportedSourceFileType(const string& sourceFileType) {
    vector<string> supportedLanguages;
    SplitString(ARG_lang, ',', &supportedLanguages);
    return find(supportedLanguages.begin(),
                supportedLanguages.end(),
                sourceFileType) != supportedLanguages.end();
}

int ExecCompileCommand(int sock,
                       const string& root,
                       const string& working_root,
                       string* source_file_type) {
    uint32_t submission_id;
    uint8_t source_file_type_id;
    uint32_t source_file_length;
    uint16_t checksum;
    if (ReadUint32(sock, &submission_id) == -1 ||
        ReadUint8(sock, &source_file_type_id) == -1 ||
        ReadUint32(sock, &source_file_length) == -1 ||
        ReadUint16(sock, &checksum) == -1) {
        return -1;
    }
    LOG(INFO)<<StringPrintf("Compile Id:%u Type:%u",
                            (unsigned int)submission_id,
                            source_file_type_id);
    if (CheckSum(CMD_COMPILE) +
        CheckSum(submission_id) +
        CheckSum(source_file_type_id) +
        CheckSum(source_file_length) != checksum) {
        LOG(ERROR)<<"Invalid checksum "<<checksum;
        SendReply(sock, INVALID_INPUT);
        return -1;
    }
    const char* source_file_type_list[] =
            {"cc", "cpp", "pas", "c", "java", "cs"};
    const int max_source_file_type =
            sizeof(source_file_type_list) / sizeof(source_file_type_list[0]);
    if (source_file_type_id == 0 ||
        source_file_type_id > max_source_file_type) {
        LOG(ERROR)<<"Invalid source file type "<<(int)source_file_type_id;
        SendReply(sock, INVALID_INPUT);
        return -1;
    }
    *source_file_type = source_file_type_list[source_file_type_id];
    if (!IsSupportedSourceFileType(source_file_type->c_str())) {
        LOG(ERROR)<<"Unsupported source file type "<<source_file_type;
        SendReply(sock, INVALID_INPUT);
        return -1;
    }
    LOG(INFO)<<"Source file type: "<<source_file_type;
    SendReply(sock, READY);
    LOG(INFO)<<"Saving source file";
    string source_filename = "p." + *source_file_type;
    if (SaveFile(sock, source_filename.c_str(), source_file_length) == -1) {
        SendReply(sock, INTERNAL_ERROR);
        return -1;
    }

    if (DoCompile(sock, root, source_filename.c_str()) == -1) {
        return -1;
    }
    SendReply(sock, READY);
    return 0;
}

int ExecJudgeCommand(int sock,
                     const string& root,
                     const string& source_file_type,
                     int uid,
                     int gid) {
    uint32_t problem_id;
    uint32_t revision;
    uint8_t testcase;
    uint16_t time_limit;
    uint32_t memory_limit;
    uint16_t output_limit;
    uint16_t checksum;
    if (ReadUint32(sock, &problem_id) == -1 ||
        ReadUint32(sock, &revision) == -1 ||
        ReadUint8(sock, &testcase) == -1 ||
        ReadUint16(sock, &time_limit) == -1 ||
        ReadUint32(sock, &memory_limit) == -1 ||
        ReadUint16(sock, &output_limit) == -1 ||
        ReadUint16(sock, &checksum)) {
        return -1;
    }
    LOG(INFO)<<StringPrintf("P:%u R:%u T:%u TL:%u ML:%u OL:%u",
                            (unsigned int)problem_id,
                            (unsigned int)revision,
                            (unsigned int)testcase,
                            (unsigned int)time_limit,
                            (unsigned int)memory_limit,
                            (unsigned int)output_limit);
    if (CheckSum(CMD_JUDGE) +
        CheckSum(problem_id) + 
        CheckSum(revision) +
        CheckSum(testcase) + 
        CheckSum(time_limit) + 
        CheckSum(memory_limit) + 
        CheckSum(output_limit) != checksum) {
        LOG(ERROR)<<"Invalid checksum "<<checksum;
        SendReply(sock, INVALID_INPUT);
        return -1;
    }
    string problem_dir = StringPrintf("%s/prob/%u/%u", root.c_str(), problem_id, revision);
    if (access(problem_dir.c_str(), F_OK) == 0) {
        SendReply(sock, READY);
    } else if (errno != ENOENT) {
        LOG(SYSCALL_ERROR)<<"Fail to access "<<problem_dir;
        SendReply(sock, INTERNAL_ERROR);
        return -1;
    } else {
        LOG(ERROR)<<"No such problem";
        SendReply(sock, NO_SUCH_PROBLEM);
        return 1;
    }

    string input_filename = StringPrintf("%s/%u.in",
                                         problem_dir.c_str(),
                                         (unsigned int)testcase);
    string output_filename = StringPrintf("%s/%u.out",
                                          problem_dir.c_str(),
                                          (unsigned int)testcase);
    string special_judge_filename = problem_dir + "/judge";
    if (access(input_filename.c_str(), F_OK) == -1) {
        LOG(ERROR)<<"Invalid test case "<<testcase;
        SendReply(sock, INVALID_INPUT);
        return -1;
    }
    if (symlink(input_filename.c_str(), "input") == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create symlink to "<<input_filename;
        SendReply(sock, INTERNAL_ERROR);
        return -1;
    }
    int result = DoRun(sock,
                       source_file_type,
                       time_limit,
                       memory_limit,
                       output_limit,
                       uid,
                       gid);
    if (result == -1) {
        return -1;
    }
    if (symlink(output_filename.c_str(), "output") == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create symlink to "<<output_filename;
        SendReply(sock, INTERNAL_ERROR);
        return -1;
    }
    if (DoCheck(sock, uid, special_judge_filename) == -1) {
        return -1;
    }
    return 0;
}

int CheckData(int sock, const string& root, const string& data_dir) {
    DIR* dir = opendir(data_dir.c_str());
    if (dir == NULL) {
        LOG(SYSCALL_ERROR)<<"Can not open dir "<<data_dir;
        SendReply(sock, INTERNAL_ERROR);
        return -1;
    }
    int ret = 0;
    vector<int> in, out;
    string judge;
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
        lstat(StringPrintf("%s/%s", data_dir.c_str(), entry->d_name).c_str(),
              &status);
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
            if (!IsSupportedSourceFileType(entry->d_name + 6)) {
                LOG(ERROR)<<"Unsupported file type "<<entry->d_name + 6;
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
            SendReply(sock, INVALID_INPUT);
            return -1;
        }
        sort(in.begin(), in.end());
        sort(out.begin(), out.end());
        if (judge.empty()) {
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
                LOG(ERROR)<<"No "<<out[in.size()]<<".in found for "
                          <<out[in.size()]<<".out";
                ret = -1;
            }
        } else if (DoCompile(sock, root, data_dir + "/" + judge) == -1) {
            return -1;
        }
    }
    if (ret < 0) {
        SendReply(sock, INVALID_INPUT);
    }
    return ret;
}

int SaveData(int sock, const string& root,
             unsigned int problem_id, unsigned int revision) {
    string problem_dir = StringPrintf("%s/prob/%u", root.c_str(), problem_id);
    string revisionDir = StringPrintf("%s/%u", problem_dir.c_str(), revision);
    string tempDir = StringPrintf("%s.%u.%s",
                                  revisionDir.c_str(),
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
                    SendReply(sock, INTERNAL_ERROR);
                    return -1;
                }
            }
            LOG(INFO)<<"Creating temporary directory "<<tempDir;
            if (mkdir(tempDir.c_str(), 0750) == -1) {
                LOG(SYSCALL_ERROR)<<"Fail to create dir "<<tempDir;
                SendReply(sock, INTERNAL_ERROR);
                return -1;
            }
        } else if (errno != EEXIST) {
            LOG(SYSCALL_ERROR)<<"Fail to create dir "<<tempDir;
            SendReply(sock, INTERNAL_ERROR);
            return -1;
        }
    }
    LOG(INFO)<<"Saving data file";
    unsigned long size;
    if (Readn(sock, &size, 4) < 4) {
        LOG(ERROR)<<"Fail to read file size";
        SendReply(sock, INVALID_INPUT);
        return -1;
    }
    size = ntohl(size);
    LOG(INFO)<<"File size: "<<size;
    if (SaveFile(sock, tempDir + "/data.zip", size) == -1) {
        SendReply(sock, INTERNAL_ERROR);
        return -1;
    }
    LOG(INFO)<<"Unzipping data file";
    string command = StringPrintf("unzip '%s/data.zip' -d '%s'",
                                  tempDir.c_str(), tempDir.c_str());
    int result = system(command.c_str());
    if (result) {
        LOG(ERROR)<<"Fail to unzip data file. Command: "<<command;
        SendReply(sock, INVALID_INPUT);
        return -1;
    }
    LOG(INFO)<<"Checking data";
    if (CheckData(sock, root, tempDir) == -1) {
        return -1;
    }
    if (rename(tempDir.c_str(), revisionDir.c_str()) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to rename "<<tempDir<<" to "<<revisionDir;
        SendReply(sock, INTERNAL_ERROR);
        system(StringPrintf("rm -rf '%s'", tempDir.c_str()).c_str());
        return -1;
    }
    SendReply(sock, READY);
    return 0;
}

int JudgeMain(const string& root, const string& queue_address, int queue_port,
              int uid, int gid) {
    string working_root = StringPrintf("%s/working/%u", root.c_str(), getpid());
    int sock = ConnectTo(queue_address, queue_port);
    if (sock < 0) {
        return 1;
    }
    if (mkdir(working_root.c_str(), 0777) < 0) {
        if (errno != EEXIST) {
            LOG(SYSCALL_ERROR)<<"Fail to create dir "<<working_root;
            SendReply(sock, INTERNAL_ERROR);
            return 1;
        }
    }
    if (chdir(working_root.c_str()) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to change working dir to "<<working_root;
        SendReply(sock, INTERNAL_ERROR);
        return 1;
    }

    // installs handlers for tracing.
    InstallHandlers();

    int ret = 0;
    string source_file_type;
    string source_filename;
    string binary_filename;
    string program_output_filename;

    // Loops until SIGTERM or SIGPIPE is received.
    while (!global::terminated) {
        uint8_t command;
        if (Readn(sock, &command, sizeof(command)) == -1) {
            ret = -1;
            break;
        }
        if (command == CMD_TYPE) {
            SendReply(sock, TYPE_JUDGE);
        } else if (command == CMD_COMPILE) {
            source_filename.clear();
            ExecCompileCommand(sock,
                               root,
                               working_root,
                               &source_file_type);
        } else if (command == CMD_JUDGE) {
            if (source_filename.empty()) {
                SendReply(sock, INVALID_INPUT);
            } else {
                ExecJudgeCommand(sock, root,
                                 source_file_type,
                                 uid,
                                 gid);
            }
        } else if (command == CMD_DATA) {
            uint32_t problem_id;
            uint32_t revision;
            if (Readn(sock, &problem_id, sizeof(problem_id)) != -1 &&
                Readn(sock, &revision, sizeof(revision)) != -1) {
                SaveData(sock, root, problem_id, revision);
            }
        } else {
            LOG(ERROR)<<"Invalid command "<<command;
            SendReply(sock, INVALID_INPUT);
        }
        // clear all temporary files.
        system(StringPrintf("rm -f %s/*", working_root.c_str()).c_str());
    }
    close(sock);
    system(StringPrintf("rm -rf %s", working_root.c_str()).c_str());
    return ret;
}
