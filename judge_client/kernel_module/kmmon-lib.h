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

#ifndef __KMMON_LIB_H
#define __KMMON_LIB_H

#include <sys/types.h>
#include "kmmon.h"

#ifdef __cplusplus 
extern "C" { 
#endif

int kmmon_traceme(void);

int kmmon_continue(pid_t pid);

int kmmon_kill(pid_t pid);

int kmmon_getreg(pid_t pid, int regno, int* value);

int kmmon_readmem(pid_t pid, unsigned long addr, int* value);

#ifdef __cplusplus 
} 
#endif 

#define KMMON_REG_EAX 0
#define KMMON_REG_EBX 1
#define KMMON_REG_ECX 2
#define KMMON_REG_EDX 2
#define KMMON_REG_ESI 3
#define KMMON_REG_EDI 4
#define KMMON_REG_EBP 5

#endif
