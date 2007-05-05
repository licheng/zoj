#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/syscall.h>
#include <pthread.h>
#include <errno.h>
#include "kmmon-lib.h"
char *p = "test";
void sighand(int sig, siginfo_t* siginfo, void* context) {
    pid_t pid = siginfo->si_pid;
    int syscall = 0;
    kmmon_getreg(pid, EAX, &syscall);
    if (syscall == SYS_open) {
        int t1;
        char buf[100] = {0};
        kmmon_getreg(pid, EBX, &t1);
        kmmon_readmem(pid, t1, buf);
    }
    printf("%d %d\n", pid, syscall);
    kmmon_continue(pid);
}

void* fn(void* arg) {
}

char stack[16384];

int main() {
    struct sigaction act, oact;
    int i, pid, status;
    act.sa_sigaction = sighand;
    sigemptyset(&act.sa_mask);
    act.sa_flags = SA_SIGINFO;
    sigaction(KMMON_SIG, &act, &oact);
    pid = fork();
    if (pid == 0) {
        pthread_t ntid;
        void* tret;
        kmmon_traceme();
        fork();
        if (pthread_create(&ntid, NULL, fn, NULL)) {
            printf("%s\n", strerror(errno));
        }
        pthread_join(ntid, &tret);
        printf("%d\n", getpid());
        return 0;
    }
    while ((i=wait(&status)) >= 0 || errno == EINTR) {
        printf("%d %d %d\n", i, WIFSIGNALED(status), WIFEXITED(status));
    }
    printf("%s\n", strerror(errno));
    return 0;
}
