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

int setLimit(int resource, unsigned int limit) {
    struct rlimit t;
    t.rlim_max = limit + 1;
    t.rlim_cur = limit;
    if (setrlimit(resource, &t) == -1) {
        LOG(SYSCALL_ERROR);
        return -1;
    }
    return 0;
}

int readTimeConsumption(int pid) {
    char buffer[64];
    sprintf(buffer, "/proc/%d/stat", pid);
    FILE* fp = fopen(buffer, "r");
    if (fp == NULL) {
        return -1;
    }
    int utime, stime;
    while (fgetc(fp) != ')');
    fgetc(fp);
    fscanf(fp,
           "%*c "
           "%*d %*d %*d %*d %*d "
           "%*u %*u %*u %*u %*u "
           "%d %d",
           &utime, &stime);
    fclose(fp);
    static int clktck = 0;
    if (clktck == 0) {
        clktck = sysconf(_SC_CLK_TCK);
    }
    return int((utime + stime + 0.0) / clktck * 1000);
}

int readMemoryConsumption(int pid) {
    char buffer[64];
    sprintf(buffer, "/proc/%d/status", pid);
    FILE* fp = fopen(buffer, "r");
    if (fp == NULL) {
        return -1;
    }
    int vmPeak = 0, vmSize = 0, vmExe = 0, vmLib = 0;
    while (fgets(buffer, 32, fp)) {
        if (!strncmp(buffer, "VmPeak:", 7)) {
            sscanf(buffer + 7, "%d", &vmPeak);
        } else if (!strncmp(buffer, "VmSize:", 7)) {
            sscanf(buffer + 7, "%d", &vmSize);
        } else if (!strncmp(buffer, "VmExe:", 6)) {
            sscanf(buffer + 6, "%d", &vmExe);
        } else if (!strncmp(buffer, "VmLib:", 6)) {
            sscanf(buffer + 6, "%d", &vmLib);
        }
    }
    if (vmPeak) {
        vmSize = vmPeak;
    }
    return vmSize - vmExe - vmLib;
}

int createProcess(const char* commands[], const StartupInfo& processInfo) {
    const char* filename[] = {processInfo.stdinFilename,
                              processInfo.stdoutFilename,
                              processInfo.stderrFilename};
    int mode[] = {O_RDONLY, O_RDWR | O_CREAT | O_TRUNC, O_RDWR};
    int fd[] = {processInfo.fdStdin,
                processInfo.fdStdout,
                processInfo.fdStderr};
    for (int i = 0; i < 3; i++) {
        if (filename[i]) {
            fd[i] = open(filename[i], mode[i], 0777);
            if (fd[i] == -1) {
                LOG(SYSCALL_ERROR)<<"Fail to open "<<filename[i];
                for (int j = 0; j < i; j++) {
                    if (filename[j]) {
                        close(fd[j]);
                    }
                }
                return -1;
            }
        }
    }
    int pid = fork();
    if (pid < 0) {
        LOG(SYSCALL_ERROR);
        return -1;
    } if (pid > 0) {
        return pid;
    }
    for (int i = 0; i < 3; i++) {
        if (fd[i]) {
            if (dup2(fd[i], i) == -1) {
                LOG(SYSCALL_ERROR)<<"Fail to dup "<<fd[i]<<" to "<<i;
                raise(SIGKILL);
            }
            close(fd[i]);
        }
    }
    for (int i = 3; i < 100; i++) {
        close(i);
    }
    if (processInfo.timeLimit) {
        if (setLimit(RLIMIT_CPU, processInfo.timeLimit) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set cpu limit to "
                              <<processInfo.timeLimit<<'s';
            raise(SIGKILL);
        }
    }
    if (processInfo.memoryLimit) {
        if (setLimit(RLIMIT_DATA, processInfo.memoryLimit * 1024) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set memory limit to "
                              <<processInfo.memoryLimit<<'k';
            raise(SIGKILL);
        }
    }
    if (processInfo.outputLimit) {
        if (setLimit(RLIMIT_FSIZE, processInfo.outputLimit * 1024) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set output limit to "
                              <<processInfo.outputLimit<<'k';
            raise(SIGKILL);
        }
    }
    if (processInfo.fileLimit) {
        if (setLimit(RLIMIT_NOFILE, processInfo.fileLimit) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set file limit to "
                              <<processInfo.fileLimit;
            raise(SIGKILL);
        }
    }
    if (processInfo.workingDirectory) {
        if (chdir(processInfo.workingDirectory) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to change working directory to "
                              <<processInfo.workingDirectory;
            raise(SIGKILL);
        }
    }
    if (processInfo.gid) {
        if (setgid(processInfo.gid) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set gid to "<<processInfo.gid;
            raise(SIGKILL);
        }
    }
    if (processInfo.uid) {
        if (setuid(processInfo.uid) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set uid to "<<processInfo.uid;
            raise(SIGKILL);
        }
    }
    if (processInfo.procLimit) {
        if (setLimit(RLIMIT_NPROC, processInfo.procLimit) == -1) {
            LOG(SYSCALL_ERROR)<<"Fail to set process limit to "
                              <<processInfo.procLimit;
            raise(SIGKILL);
        }
    }
    if (processInfo.trace) {
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

int createShellProcess(const char* command, const StartupInfo& processInfo) {
    const char* commands[] = {"/bin/sh", "sh", "-c", command, NULL};
    return createProcess(commands, processInfo);
}

ssize_t readn(int fd, void* buffer, size_t count) {
	char* p = (char*)buffer;
	while (count > 0) {
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
	return p - (char*)buffer;
}

int writen(int fd, const void* buffer, size_t count) {
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

sighandler_t installSignalHandler(int signal, sighandler_t handler) {
    return installSignalHandler(signal, handler, 0);
}

sighandler_t installSignalHandler(int signal, sighandler_t handler, int flags) {
    sigset_t mask;
    sigemptyset(&mask);
    return installSignalHandler(signal, handler, flags, mask);
}

sighandler_t installSignalHandler(
        int signal, sighandler_t handler, int flags, sigset_t mask) {
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

int lockFile(int fd, int cmd) {
    struct flock lock;
    lock.l_type = F_WRLCK;
    lock.l_start = 0;
    lock.l_whence = SEEK_SET;
    lock.l_len = 0;
    return fcntl(fd, cmd, &lock);
}

string getLocalTimeAsString(const char* format) {
    time_t t = time(NULL);
    struct tm tm;
    localtime_r(&t, &tm);
    char buf[1024];
    strftime(buf, sizeof(buf), format, &tm);
    return buf;
}

