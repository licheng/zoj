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

#include <string>

#include <errno.h>
#include <unistd.h>

#include "global.h"
#include "logging.h"
#include "log_server.h"
#include "args.h"
#include "util.h"

DEFINE_ARG(string, root, "The root directory of the client");

DEFINE_ARG(int, uid, "The uid for executing the program to be judged");

DEFINE_ARG(int, gid, "The uid for executing the program to be judged");

DEFINE_OPTIONAL_ARG(bool, daemonize, true, "If true, the program will be "
                                           "started as a daemon process");

DEFINE_ARG(string, queue_address, "The ip address of the queue service to which"
                                  "this client connects");

DEFINE_ARG(int, queue_port, "The port of the queue service to which this client"
                            "connects");

// If true, all logs are written to stderr as well
DEFINE_OPTIONAL_ARG(bool, logtostderr, true, "If true, all logs are written to stderr as well");

void Daemonize() {
    umask(0);
    int pid = fork();
    if (pid < 0) {
        LOG(SYSCALL_ERROR);
        exit(1);
    } else if (pid > 0) {
        exit(0);
    }

    // start a new session
    setsid();

    // ignore SIGHUP
    if (InstallSignalHandler(SIGHUP, SIG_IGN) == SIG_ERR) {
        LOG(SYSCALL_ERROR)<<"Fail to ignore SIGHUP";
        exit(1);
    }

    // attach file descriptor 0, 1, 2 to /dev/null
    int fd = open("/dev/null", O_RDWR);
    dup2(fd, 0);
    dup2(fd, 1);
    dup2(fd, 2);
    
    // close all other file descriptors
    for (int i = 3; i < 100; i++) {
        close(i);
    }
}

int Lock() {
    int fd = open((ARG_root + "/judge.pid").c_str(), O_RDWR | O_CREAT, 0640);
    if (fd < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to open judge.pid";
        return -1;
    }
    if (LockFile(fd, F_SETLK) == -1) {
        if (errno == EACCES || errno == EAGAIN) {
            LOG(ERROR)<<"Another instance of judge_client exists";
            close(fd);
            return -1;
        } else {
            LOG(SYSCALL_ERROR)<<"Fail to lock judge.pid";
            return -1;
        }
    }
    ftruncate(fd, 0);
    char buffer[20];
    sprintf(buffer, "%ld", (long)getpid());
    write(fd, buffer, strlen(buffer) + 1);
    return fd;
}

void SIGTERMHandler(int sig) {
    global::terminated = 1;
}

void SIGPIPEHandler(int sig) {
    global::socket_closed = 1;
}

int ControlMain(const string& root, const string& queue_address, int queue_port,
                int uid, int gid);

int main(int argc, char* argv[]) {
    if (ParseArguments(argc, argv) < 0) {
        return 1;
    }

    if (chdir(ARG_root.c_str()) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to change working dir to "<<ARG_root<<endl;
        return 1;
    }

    char path[PATH_MAX + 1];
    if (getcwd(path, sizeof(path)) == NULL) {
        LOG(SYSCALL_ERROR)<<"Fail to get the current working dir";
        return 1;
    }

    ARG_root = path;

    InstallSignalHandler(SIGTERM, SIGTERMHandler);

    // prevents SIGPIPE to terminate the processes.
    InstallSignalHandler(SIGPIPE, SIGPIPEHandler);

    LogServer* log_server = LogServer::Create(ARG_root);

    if (log_server == NULL) {
        return 1;
    }

    int fd = -1;
    if (ARG_daemonize) {
        fd = Lock();
        if (fd < 0) {
            return 1;
        }
        Daemonize();
    }

    if (ChangeToWorkingDir(ARG_root, NULL) < 0) {
        return 1;
    }

    if (ARG_daemonize || !ARG_logtostderr) {
        Log::SetLogToStderr(false);
    }


    pid_t pid = fork();
    if (pid < 0) {
        return 1;
    }
    if (pid == 0) {
        log_server->Start();
    } else {
        if (fd >= 0) {
            close(fd);
        }
        delete log_server;
        Log::SetLogFile(new UnixDomainSocketLogFile(ARG_root));
        return ControlMain(ARG_root, ARG_queue_address, ARG_queue_port,
                           ARG_uid, ARG_gid);
    }
    return 0;
}
