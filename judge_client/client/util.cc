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

#include <string>

#include <fcntl.h>
#include <netinet/in.h>
#include <signal.h>
#include <sys/resource.h>
#include <sys/socket.h>
#include <sys/times.h>
#include <sys/wait.h>
#include <unistd.h>
#include <stdarg.h>

#include "kmmon-lib.h"
#include "logging.h"
#include "trace.h"
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

int ReadTimeConsumption(int pid) {
    char buffer[64];
    sprintf(buffer, "/proc/%d/stat", pid);
    FILE* fp = fopen(buffer, "r");
    if (fp == NULL) {
        return -1;
    }
    int utime, stime;
    while (fgetc(fp) != ')');
    fgetc(fp);
    fscanf(fp, "%*c %*d %*d %*d %*d %*d %*u %*u %*u %*u %*u %d %d", &utime, &stime);
    fclose(fp);
    static int clktck = 0;
    if (clktck == 0) {
        clktck = sysconf(_SC_CLK_TCK);
    }
    return int((utime + stime + 0.0) / clktck * 1000);
}

int ReadMemoryConsumption(int pid) {
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
        if (kmmon_traceme() == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to trace";
            raise(SIGKILL);
        }
    }
    if (execv(commands[0], (char**)(commands + 1)) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to execute command '"<<commands[0]<<"'";
        raise(SIGKILL);
    }
    return -1;
}

int CreateShellProcess(const char* command, const StartupInfo& process_info) {
    const char* commands[] = {"/bin/sh", "sh", "-c", command, NULL};
    return CreateProcess(commands, process_info);
}

ssize_t Readn(int fd, void* buffer, size_t count) {
	char* p = (char*)buffer;
	while (count > 0 && !global::terminated) {
		ssize_t num = read(fd, p, count);
		if (num == -1) {
			if (errno == EINTR) {
				// interrupted by a signals, read again
				continue;
			}
			LOG(SYSCALL_ERROR)<<"Fail to read from file";
			return -1;
		}
		if (num == 0) {
			// EOF
			break;
		}
		p += num;
		count -= num;
	}
    if (global::terminated) {
        LOG(INFO)<<"Terminated";
        if (count > 0) {
            return -1;
        }
    }
	return p - (char*)buffer;
}

int Writen(int fd, const void* buffer, size_t count) {
	const char*p = (const char*)buffer;
	while (count > 0) {
		int num = write(fd, p, count);
		if (num == -1) {
			if (errno == EINTR) {
				// interrupted by a signals, write again
				continue;
			}
			LOG(SYSCALL_ERROR)<<"Fail to write to file";
			return -1;
		}
		p += num;
		count -= num;
	}
	return 0;
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

void SplitString(const string& str, char separator, vector<string>* output) {
    int k = 0;
    for (int i = 0; i < str.size(); ++i) {
        if (str[i] == separator) {
            if (i > k) {
                output->push_back(str.substr(k, i - k));
            }
            k = i + 1;
        }
    }
    if (k < str.size()) {
        output->push_back(str.substr(k, str.size() - k));
    }
}

string StringPrintf(const char* format, ...) {
    va_list args;
    char buffer[1024];
    va_start(args, format);
    vsnprintf(buffer, sizeof(buffer), format, args);
    va_end(args);
    return buffer;
}

int LockFile(int fd, int cmd) {
    struct flock lock;
    lock.l_type = F_WRLCK;
    lock.l_start = 0;
    lock.l_whence = SEEK_SET;
    lock.l_len = 0;
    return fcntl(fd, cmd, &lock);
}

string GetLocalTimeAsString(const char* format) {
    time_t t = time(NULL);
    struct tm tm;
    localtime_r(&t, &tm);
    char buf[1024];
    strftime(buf, sizeof(buf), format, &tm);
    return buf;
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

int ChangeToWorkingDir(const string& root, string* working_root) {
    string working_dir = StringPrintf("%s/working/%u", root.c_str(), getpid());
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
    if (working_root) {
        *working_root = working_dir;
    }
    return 0;
}
