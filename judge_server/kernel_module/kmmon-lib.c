/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ Judge Server.
 *
 * ZOJ Judge Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZOJ Judge Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ Judge Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
