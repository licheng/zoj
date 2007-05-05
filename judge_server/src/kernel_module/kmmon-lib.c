#include <sys/syscall.h>
#include "kmmon.h"
#include "kmmon-lib.h"

static inline int kmmon(int request, unsigned long pid, unsigned long addr, unsigned long data) {
    return syscall(__NR_kmmon, request, pid, addr, data);
}

int kmmon_traceme(void) {
    return kmmon(KMMON_TRACEME, 0, 0, 0);
}

int kmmon_continue(pid_t pid) {
    return kmmon(KMMON_CONTINUE, pid, 0, 0);
}

int kmmon_kill(pid_t pid) {
    return kmmon(KMMON_KILL, pid, 0, 0);
}

int kmmon_getreg(pid_t pid, int regno, int* value) {
    return kmmon(KMMON_GETREG, pid, regno, (unsigned long)value);
}

int kmmon_readmem(pid_t pid, unsigned long addr, int* value) {
    return kmmon(KMMON_READMEM, pid, addr, (unsigned long)value);
}
