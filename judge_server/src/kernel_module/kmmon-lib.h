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

#define EBX 0
#define EAX 1
#define ECX 2
#define EDX 3
#define ESI 4
#define EDI 5

#endif
