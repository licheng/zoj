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

#include <cstdio>
#include <cstring>
#include <string>

using namespace std;

#include <fcntl.h>
#include <netinet/in.h>
#include <signal.h>
#include <sys/resource.h>
#include <sys/socket.h>
#include <sys/ptrace.h>
#include <sys/times.h>
#include <sys/wait.h>
#include <unistd.h>
#include <stdarg.h>

#include "common_io.h"
#include "logging.h"
#include "strutil.h"
#include "util.h"

int SetLimit(int resource, unsigned int limit) {
    struct rlimit t;
    t.rlim_max = limit + 1;
    t.rlim_cur = limit;
    if (setrlimit(resource, &t) == -1) {
        LOG(SYSCALL_ERROR);
        return -1;
    }
    return 0;
}

int ReadTimeConsumption(pid_t pid) {
    char buffer[64];
    sprintf(buffer, "/proc/%d/stat", pid);
    FILE* fp = fopen(buffer, "r");
    if (fp == NULL) {
        return -1;
    }
    int utime, stime;
    while (fgetc(fp) != ')');
    fgetc(fp);
    if (fscanf(fp, "%*c %*d %*d %*d %*d %*d %*u %*u %*u %*u %*u %d %d", &utime, &stime) < 2) {
        fclose(fp);
        return -1;
    }
    fclose(fp);
    static int clktck = 0;
    if (clktck == 0) {
        clktck = sysconf(_SC_CLK_TCK);
    }
    return int((utime + stime + 0.0) / clktck * 1000);
}

int ReadMemoryConsumption(pid_t pid) {
    char buffer[64];
    sprintf(buffer, "/proc/%d/status", pid);
    FILE* fp = fopen(buffer, "r");
    if (fp == NULL) {
        return -1;
    }
    int vmPeak = 0, vmSize = 0, vmExe = 0, vmLib = 0, vmStack = 0;
    while (fgets(buffer, 32, fp)) {
        if (!strncmp(buffer, "VmPeak:", 7)) {
            sscanf(buffer + 7, "%d", &vmPeak);
        } else if (!strncmp(buffer, "VmSize:", 7)) {
            sscanf(buffer + 7, "%d", &vmSize);
        } else if (!strncmp(buffer, "VmExe:", 6)) {
            sscanf(buffer + 6, "%d", &vmExe);
        } else if (!strncmp(buffer, "VmLib:", 6)) {
            sscanf(buffer + 6, "%d", &vmLib);
        } else if (!strncmp(buffer, "VmStk:", 6)) {
            sscanf(buffer + 6, "%d", &vmStack);
        }
    }
    fclose(fp);
    if (vmPeak) {
        vmSize = vmPeak;
    }
    return vmSize - vmExe - vmLib - vmStack;
}

int CreateProcess(const char* commands[], const StartupInfo& process_info) {
    const char* filename[] = {process_info.stdin_filename,
                              process_info.stdout_filename,
                              process_info.stderr_filename};
    int pid = fork();
    if (pid < 0) {
        LOG(SYSCALL_ERROR);
        return -1;
    } if (pid > 0) {
        return pid;
    }
    int mode[] = {O_RDONLY, O_RDWR | O_CREAT | O_TRUNC, O_RDWR};
    int fd[] = {process_info.fd_stdin,
                process_info.fd_stdout,
                process_info.fd_stderr};
    for (int i = 0; i < 3; ++i) {
        if (filename[i]) {
            if (fd[i]) {
                close(fd[i]);
            }
            fd[i] = open(filename[i], mode[i], 0777);
        }
        if (fd[i] == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to open "<<filename[i];
            raise(SIGKILL);
        }
        if (fd[i]) {
            if (dup2(fd[i], i) == -1) {
                LOG(SYSCALL_ERROR)<<"Fail to dup "<<fd[i]<<" to "<<i;
                raise(SIGKILL);
            }
            close(fd[i]);
        }
    }
    Log::Close();
    for (int i = 3; i < 100; i++) {
        close(i);
    }
    if (process_info.time_limit) {
        if (SetLimit(RLIMIT_CPU, process_info.time_limit) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set cpu limit to "<<process_info.time_limit<<'s';
            raise(SIGKILL);
        }
    }
    if (process_info.memory_limit) {
        if (SetLimit(RLIMIT_DATA, process_info.memory_limit * 1024) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set memory limit to "<<process_info.memory_limit<<'k';
            raise(SIGKILL);
        }
    }
    if (process_info.vm_limit) {
        if (SetLimit(RLIMIT_AS, process_info.vm_limit * 1024) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set memory limit to "<<process_info.memory_limit<<'k';
            raise(SIGKILL);
        }
    }
    if (process_info.output_limit) {
        if (SetLimit(RLIMIT_FSIZE, process_info.output_limit * 1024) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set output limit to "<<process_info.output_limit<<'k';
            raise(SIGKILL);
        }
    }
    if (process_info.stack_limit) {
        if (SetLimit(RLIMIT_STACK, process_info.stack_limit * 1024) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set stack limit to "<<process_info.file_limit<<'k';
            raise(SIGKILL);
        }
    }
    if (process_info.file_limit) {
        if (SetLimit(RLIMIT_NOFILE, process_info.file_limit) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set file limit to "<<process_info.file_limit;
            raise(SIGKILL);
        }
    }
    if (process_info.working_dir) {
        if (chdir(process_info.working_dir) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to change working directory to "<<process_info.working_dir;
            raise(SIGKILL);
        }
    }
    if (process_info.gid) {
        if (setgid(process_info.gid) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set gid to "<<process_info.gid;
            raise(SIGKILL);
        }
    }
    if (process_info.uid) {
        if (setuid(process_info.uid) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set uid to "<<process_info.uid;
            raise(SIGKILL);
        }
    }
    if (process_info.proc_limit) {
        if (SetLimit(RLIMIT_NPROC, process_info.proc_limit) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set process limit to "<<process_info.proc_limit;
            raise(SIGKILL);
        }
    }
    if (process_info.trace) {
        if (ptrace(PTRACE_TRACEME, 0, 0, 0) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to trace";
            raise(SIGKILL);
        }
    }
    execv(commands[0], (char**)(commands + 1));
    LOG(SYSCALL_ERROR)<<"Fail to execute command '"<<commands[0]<<"'";
    raise(SIGKILL);
    return -1;
}

int CreateShellProcess(const char* command, const StartupInfo& process_info) {
    const char* commands[] = {"/bin/sh", "sh", "-c", command, NULL};
    return CreateProcess(commands, process_info);
}

sighandler_t InstallSignalHandler(int signal, sighandler_t handler) {
    return InstallSignalHandler(signal, handler, 0);
}

sighandler_t InstallSignalHandler(int signal, sighandler_t handler, int flags) {
    sigset_t mask;
    sigemptyset(&mask);
    return InstallSignalHandler(signal, handler, flags, mask);
}

sighandler_t InstallSignalHandler(int signal, sighandler_t handler, int flags, sigset_t mask) {
    struct sigaction act, oact;
    act.sa_handler = handler;
    act.sa_mask = mask;
    act.sa_flags = flags;
    if (sigaction(signal, &act, &oact) < 0) {
        return SIG_ERR;
    }
    return oact.sa_handler;
}

int ConnectTo(const string& address, int port, int timeout) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create socket";
        return -1;
    }
    struct sockaddr_in servaddr;
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(port);
    if (inet_pton(AF_INET, address.c_str(), &servaddr.sin_addr) <= 0) {
        LOG(SYSCALL_ERROR)<<"Invalid address "<<address;
        close(sock);
        return -1;
    }
    struct timeval tv;
    tv.tv_sec = timeout / 1000;
    tv.tv_usec = timeout % 1000 * 1000;
    if (setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to set receive timeout";
        close(sock);
        return -1;
    }
    if (setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &tv, sizeof(tv)) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to set send timeout";
        close(sock);
        return -1;
    }
    LOG(INFO)<<"Connecting to "<<address<<":"<<port;
    if (connect(sock, (const sockaddr*)&servaddr, sizeof(servaddr)) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to connect to "<<address<<":"<<port;
        close(sock);
        return -1;
    }
    LOG(INFO)<<"Connected";
    return sock;
}
