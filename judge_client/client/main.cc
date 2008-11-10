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
#include <sys/un.h>
#include <sys/wait.h>
#include <unistd.h>

#include "global.h"
#include "logging.h"
#include "args.h"
#include "util.h"

DEFINE_ARG(string, root, "The root directory of the client");

DEFINE_ARG(int, uid, "The uid for executing the program to be judged");

DEFINE_ARG(int, gid, "The uid for executing the program to be judged");

DEFINE_OPTIONAL_ARG(bool, daemonize, true, "If true, the program will be started as a daemon process");

DEFINE_ARG(string, queue_address, "The ip address of the queue service to which this client connects");

DEFINE_ARG(int, queue_port, "The port of the queue service to which this client connects");

DEFINE_OPTIONAL_ARG(bool, logtostderr, true, "If true, all logs are written to stderr as well");

DEFINE_OPTIONAL_ARG(int, max_judge_process, 50, "The maximal number of judge processes");

void Daemonize(int lock_fd) {
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

    ftruncate(lock_fd, 0);
    char buffer[20];
    sprintf(buffer, "%ld", (long)getpid());
    write(lock_fd, buffer, strlen(buffer) + 1);
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

int ControlMain(const string& root, const string& queue_address, int queue_port, int uid, int gid);

int CreateServerSocket(int* port) {
    int sock = socket(PF_INET, SOCK_STREAM, 6);
    if (sock == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create socket";
        return -1;
    }
    int optionValue = 1;
    if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optionValue, sizeof(optionValue)) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to set socket option";
        close(sock);
        return -1;
    }
    sockaddr_in address;
    memset(&address, 0, sizeof(address));
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = htonl(INADDR_ANY); 
    address.sin_port = 0;
    if (bind(sock, (struct sockaddr*)&address, sizeof(address)) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to bind";
        close(sock);
        return -1;
    }
    if (listen(sock, 32) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to listen";
        close(sock);
        return -1;
    }
    socklen_t len = sizeof(address);
    if (getsockname(sock, (struct sockaddr*)&address, &len) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to get socket port";
        close(sock);
        return -1;
    }
    *port = ntohs(address.sin_port);
    LOG(INFO)<<"Listening on port "<<*port;
    return sock;
}

int CreateUnixDomainServerSocket() {
    int server_sock = socket(AF_UNIX, SOCK_STREAM, 0);
    if (server_sock < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to create socket";
        return -1;
    }
    struct sockaddr_un un;
    memset(&un, 0, sizeof(un));
    un.sun_family = AF_UNIX;
    string sock_name = ARG_root + "/working/log.sock";
    unlink(sock_name.c_str());
    strcpy(un.sun_path, sock_name.c_str());
    if (bind(server_sock, (struct sockaddr*)&un, offsetof(struct sockaddr_un, sun_path) + sock_name.size()) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to bind";
        return -1;
    }
    if (listen(server_sock, MAX_JOBS + 2) < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to listen";
        return -1;
    }
    return server_sock;
}


class JudgeProcess {
  public:
    JudgeProcess(int sock) : sock_(sock), pbuf_(buf_) { }
    ~JudgeProcess() {
        close(sock_);
    }

    int sock() { return sock_; }

    int ReadLines(vector<string>* lines) {
        int count = read(sock_, pbuf_, sizeof(buf_) - (pbuf_ - buf_));
        if (count < 0) {
            if (errno != EINTR) {
                LOG(SYSCALL_ERROR)<<"Fail to read log";
                return -1;
            }
        } else if (count == 0) {
            return -1;
        } else {
            char* start = buf_;
            for (char* p = pbuf_; p - pbuf_ < count; ++p) {
                if (*p == '\n') {
                    lines->push_back(string(start, p - start));
                    start = p + 1;
                }
            }
            pbuf_ += count;
            count = start - buf_;
            if (count) {
                memmove(buf_, start, count);
                pbuf_ -= count;
            }
        }
        return 0;
    }

  private:
    int sock_;
    char buf_[8192];
    char* pbuf_;
};

void SIGTERMHandler(int sig) {
    global::terminated = 1;
}

void SIGPIPEHandler(int sig) {
    global::socket_closed = 1;
}

void SIGCHLDHandler(int sig) {
    while (waitpid(-1, NULL, WNOHANG) > 0);
}

void SIGUSR1Handler(int sig) {
    Log::Close();
}

int ControlMain(const string& root, const string& queue_address, int queueu_port, int port);
int JudgeMain(const string& root, int sock, int uid, int gid);

int main(int argc, const char* argv[]) {
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

    InstallSignalHandler(SIGCHLD, SIGCHLDHandler);

    InstallSignalHandler(SIGUSR1, SIGUSR1Handler);

    int port;
    int server_sock = CreateServerSocket(&port);
    if (server_sock < 0) {
        return 1;
    }

    int log_server_sock = CreateUnixDomainServerSocket();
    if (log_server_sock < 0) {
        return 1;
    }

    int fd = -1;
    if (ARG_daemonize) {
        fd = Lock();
        if (fd < 0) {
            return 1;
        }
        Daemonize(fd);
    }

    if (ARG_daemonize || !ARG_logtostderr) {
        Log::SetLogToStderr(false);
    }

    pid_t pid = fork();
    if (pid < 0) {
        LOG(SYSCALL_ERROR)<<"Fail to fork";
        return 1;
    } else if (pid == 0) {
        if (fd >= 0) {
            close(fd);
        }
        close(server_sock);
        close(log_server_sock);
        Log::SetLogFile(new UnixDomainSocketLogFile(ARG_root));
        exit(ControlMain(ARG_root, ARG_queue_address, ARG_queue_port, port));
    }

    Log::SetLogFile(new DiskLogFile(ARG_root + "/log/"));
    vector<JudgeProcess*> children;
    while (!global::terminated) {
        int nfds = max(log_server_sock, server_sock);
        fd_set read_fdset;
        fd_set except_fdset;
        FD_ZERO(&read_fdset);
        FD_ZERO(&except_fdset);
        FD_SET(server_sock, &read_fdset);
        FD_SET(log_server_sock, &read_fdset);
        for (int i = 0; i < children.size(); ++i) {
            FD_SET(children[i]->sock(), &read_fdset);
            FD_SET(children[i]->sock(), &except_fdset);
            if (children[i]->sock() > nfds) {
                nfds = children[i]->sock();
            }
        }
        int ready = select(nfds + 1, &read_fdset, NULL, &except_fdset, NULL);
        if (ready == -1) {
            if (errno != EINTR) {
                LOG(SYSCALL_ERROR)<<"Fail to select";
                sleep(10);
            }
            continue;
        }
        for (int i = 0; i < children.size(); ++i) {
            if (FD_ISSET(children[i]->sock(), &read_fdset)) {
                vector<string> lines;    
                if (children[i]->ReadLines(&lines) < 0) {
                    delete children[i];
                    children.erase(children.begin() + i);
                    --i;
                }
                for (int j = 0; j < lines.size(); ++j) {
                    LOG(RAW)<<lines[j];
                }
            } else if (FD_ISSET(children[i]->sock(), &except_fdset)) {
                delete children[i];
                children.erase(children.begin() + i);
                --i;
            }
        }
        if (FD_ISSET(log_server_sock, &read_fdset)) {
            socklen_t len;
            struct sockaddr_un un;
            int client_sock = accept(log_server_sock, (struct sockaddr*)&un, &len);
            if (client_sock < 0) {
                LOG(SYSCALL_ERROR)<<"Fail to accept";
            } else {
                children.push_back(new JudgeProcess(client_sock));
            }
        }
        if (FD_ISSET(server_sock, &read_fdset)) {
            sockaddr_in addr;
            socklen_t len = sizeof(sockaddr_in);
            int sock = accept(server_sock, (struct sockaddr*)&addr, &len);
            if (sock == -1) {
                if (errno != EINTR) {
                    LOG(SYSCALL_ERROR)<<"Fail to accept";
                }
                continue;
            }
            if (children.size() >= ARG_max_judge_process) {
                LOG(ERROR)<<"Max judge process exceeded";
                close(sock);
                continue;
            }
            string address = inet_ntoa(addr.sin_addr);
            LOG(INFO)<<"Received connection from "<<address<<":"<<addr.sin_port;
            if (ARG_queue_address != address) {
                LOG(INFO)<<"Refused";
            } else {
                pid_t pid = fork();
                if (pid < 0) {
                    LOG(SYSCALL_ERROR)<<"Fail to fork";
                } else if (pid == 0) {
                    if (fd >= 0) {
                        close(fd);
                    }
                    close(server_sock);
                    close(log_server_sock);
                    for (int i = 0; i < children.size(); ++i) {
                        delete children[i];
                    }
                    Log::SetLogFile(new UnixDomainSocketLogFile(ARG_root));
                    exit(JudgeMain(ARG_root, sock, ARG_uid, ARG_gid));
                }
            }
            close(sock);
        }

    }
    return 0;
}
