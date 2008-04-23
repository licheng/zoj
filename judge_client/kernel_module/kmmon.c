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

#include "kmmon.h"
#include "syscall.h"

#include <asm/errno.h>
#include <asm/types.h>
#include <asm/uaccess.h>
#include <asm/unistd.h>
#include <linux/fcntl.h>
#include <linux/highmem.h>
#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/mm.h>
#include <linux/mman.h>
#include <linux/module.h>
#include <linux/ptrace.h>
#include <linux/rbtree.h>
#include <linux/sched.h>
#include <linux/signal.h>
#include <linux/slab.h>
#include <linux/stddef.h>
#include <linux/string.h>

static void ** orig_syscall_table;

asmlinkage long (*old_ni_syscall)(void);
asmlinkage int (*old_clone)(struct pt_regs);
asmlinkage int (*old_fork)(struct pt_regs);
asmlinkage int (*old_vfork)(struct pt_regs);
asmlinkage unsigned long (*old_brk)(unsigned long);
asmlinkage void* (*old_mmap2)(void *start, size_t length, int prot,
                              int flags, int fd, off_t pgoffset);
asmlinkage void* (*old_mmap)(void *start, size_t length, int prot,
                             int flags, int fd, off_t offset);

/* struct for idt entry */
struct idt{
    unsigned short off_low;
    unsigned short seg_selector;
    unsigned char reserved, flag;
    unsigned short off_high;
};

extern void new_int80(void);
extern void new_sysenter(void);


static struct idt* p_idt80;

int notify_tracer(int syscall) {
    struct task_struct* p = current;
    struct siginfo info;
    info.si_signo = KMMON_SIG;
    info.si_int = syscall;
    info.si_code = SI_QUEUE;
    info.si_pid = p->pid;
    info.si_uid = p->uid;
    for (;;) {
        struct task_struct* q = p->parent;
        if (q == NULL) {
            return syscall;
        }
        p = q;
        if (!(q->flags & KMMON_MASK)) {
            break;
        }
    }
    if (p->pid == 1) {
        return syscall;
    }
    current->exit_code = KMMON_SIG;
    rcu_read_lock();
    send_sig_info(KMMON_SIG, &info, p);
    rcu_read_unlock();
    while (current->exit_code == KMMON_SIG) {
        set_current_state(TASK_STOPPED);
        schedule();
    }
    if (current->exit_code) {
        send_sig(SIGKILL, current, 1);
        return -1;
    } else {
        return syscall;
    }
}

extern void new_int80(void);
extern void old_int80(void);
extern void new_sysenter(void);
extern void old_sysenter(void);

static unsigned long int80_resume_addr;
static unsigned long sysenter_resume_addr;

void asm_syscall(void) {
    __asm__ __volatile__ (
        ".globl new_int80\n"
        ".globl old_int80\n"
        ".globl new_sysenter\n"
        ".globl old_sysenter\n"
"new_int80:\n"
        "pushl %%ebx;" // save EBX
        "movl %%esp, %%ebx;"
        "andl %0, %%ebx;"
        "movl %c1(%%ebx), %%ebx;" // equals the "current" macro for i386
        "testl %2, %c3(%%ebx);" // test if KMMON_MASK is set in current->flags
        "jz end_new_int80;" // if not, jump to end
        "movl %4, %%ebx;" // load the address of syscall_filter_table
        "testl $3, 0(%%ebx, %%eax);" // check syscall_filter_table[syscall] 
        "jz end_new_int80;" // 0 means enabled
        "pushl %%ebp;"
        "pushl %%ecx;"
        "pushl %%edx;"
        "pushl %%esi;"
        "pushl %%edi;"
        "call notify_tracer;" // notify the tracer
        "popl %%edi;"
        "popl %%esi;"
        "popl %%edx;"
        "popl %%ecx;"
        "popl %%ebp;"
"end_new_int80:\n"
        "popl %%ebx;" // restore EBX
        "cmpl $-1, %%eax;"
        "jz int80_jump_back;" // if returns -1, skip the syscall
"old_int80:\n"
        "pushl %5;"
        "jmp *0xffffffff(, %%eax, 4);" // call *syscall_table(, %%eax, 4)
                                       // 0xffffffff is a place holder
                                       // It will be replaced in inline_hook()
"int80_jump_back:\n"
        "jmp *%5;"
"new_sysenter:\n"
        "pushl %%ebx;" // save EBX
        "movl %%esp, %%ebx;"
        "andl %0, %%ebx;"
        "movl %c1(%%ebx), %%ebx;" // equals the "current" macro for i386
        "testl %2, %c3(%%ebx);" // test if KMMON_MASK is set in current->flags
        "jz end_new_sysenter;" // if not, jump to end
        "movl %4, %%ebx;" // load the address of syscall_filter_table
        "testl $3, 0(%%ebx, %%eax);" // check syscall_filter_table[syscall] 
        "jz end_new_sysenter;" // 0 means enabled
        "pushl %%ebp;"
        "pushl %%ecx;"
        "pushl %%edx;"
        "pushl %%esi;"
        "pushl %%edi;"
        "call notify_tracer;" // notify the tracer
        "popl %%edi;"
        "popl %%esi;"
        "popl %%edx;"
        "popl %%ecx;"
        "popl %%ebp;"
"end_new_sysenter:\n"
        "popl %%ebx;" // restore EBX
        "cmpl $-1, %%eax;"
        "jz sysenter_jump_back;" // if returns -1, skip the syscall
"old_sysenter:\n"
        "pushl %6;"
        "jmp *0xffffffff(, %%eax, 4);" // call *syscall_table(, %%eax, 4)
                                       // 0xffffffff is a place holder
                                       // It will be replaced in inline_hook()
"sysenter_jump_back:\n"
        "jmp *%6;"
        :
        : "i"(-THREAD_SIZE),
          "i"(&((struct thread_info*)0)->task),
          "i"(KMMON_MASK),
          "i"(&((struct task_struct*)0)->flags),
          "i"(syscall_filter_table),
          "m"(int80_resume_addr),
          "m"(sysenter_resume_addr)
    );
}

asmlinkage unsigned long kmmon(int request, unsigned long pid, unsigned long addr, unsigned long data) {
    struct task_struct* p;
    int ret = 0;
    rcu_read_lock();
    switch (request) {
        case KMMON_TRACEME:
            current->flags |= KMMON_MASK;
            break;
        case KMMON_CONTINUE:
        case KMMON_KILL:
        case KMMON_READMEM:
        case KMMON_GETREG:
            p = find_task_by_pid(pid);
            if (!p || !(p->flags & KMMON_MASK)) {
                printk(KERN_ERR "Invalid pid: %ld, %d\n", pid, p->flags);
                ret = -1;
                break;
            }
            if (request == KMMON_READMEM) {
                struct page* page;
                struct mm_struct* mm;
                struct vm_area_struct* vma;
                int offset, len, tmp;
                mm = get_task_mm(p);
                if (mm == NULL) {
                    printk(KERN_ERR "Fail to get mm: %ld\n", pid);
                    ret = -1;
                    break;
                }
                if (get_user_pages(p, mm, addr, 1, 0, 1, &page, &vma) <= 0) {
                    printk(KERN_ERR "Fail to get user pages: %ld, %lx\n", pid, addr);
                    ret = -1;
                    break;
                }
                offset = addr & (PAGE_SIZE - 1);
                len = sizeof(data) > PAGE_SIZE - offset ? PAGE_SIZE - offset
                                                        : sizeof(data);
                copy_from_user_page(vma, page, addr, &tmp,
                                    kmap(page) + offset, sizeof(tmp));
                kunmap(page);
                put_page(page);
                put_user(tmp, (unsigned long*)data);
                mmput(mm);
            } else if (request == KMMON_GETREG) {
                unsigned long reg_table[] = {EAX, EBX, ECX, EDX, ESI, EDI, EBP};
                put_user(*(int*)((char*)p->thread.esp0 -
                                 sizeof(struct pt_regs) +
                                 reg_table[addr]), 
                         (unsigned long*)data);
            } else {
                p->exit_code = request == KMMON_KILL;
                wake_up_process(p);
            }
            break;
        default:
            ret = -1;
            break;
    }
    rcu_read_unlock();
    return ret;
}

#define DEFINE_CLONE(func) \
asmlinkage int kmmon_ ## func (struct pt_regs regs) {\
    int ret = old_ ## func (regs);\
    if (!ret && (current->parent->flags & KMMON_MASK)) {\
        current->flags |= KMMON_MASK;\
    }\
    return ret;\
}

DEFINE_CLONE(clone)

DEFINE_CLONE(fork);

DEFINE_CLONE(vfork);

__always_inline int mmap_allowed(int flags, size_t length) {
    if ((current->flags & KMMON_MASK) && (flags & MAP_ANONYMOUS)) {
        int allow = 1;
        unsigned long* mem_limit;
        rcu_read_lock();
        mem_limit = &current->signal->rlim[RLIMIT_DATA].rlim_cur;
        if (*mem_limit < RLIM_INFINITY) {
            struct mm_struct* mm = current->mm;
            if (mm->brk - mm->start_data + length > *mem_limit) {
                allow = 0;
            } else {
                *mem_limit -= length;
            }
        }
        rcu_read_unlock();
        if (!allow) {
            notify_tracer(45);
            send_sig(SIGKILL, current, 1);
            return 0;
        }
    }
    return 1;
}

asmlinkage void* kmmon_mmap(void *start, size_t length, int prot,
                            int flags, int fd, off_t offset) {
    if (!mmap_allowed(flags, length)) {
        return (void*)-1;
    }
    return old_mmap(start, length, prot, flags, fd, offset);
}

asmlinkage void* kmmon_mmap2(void *start, size_t length, int prot,
                             int flags, int fd, off_t pgoffset) {
    if (!mmap_allowed(flags, length)) {
        return (void*)-1;
    }
    return old_mmap2(start, length, prot, flags, fd, pgoffset);
}

asmlinkage unsigned long kmmon_brk(unsigned long brk) {
    unsigned long ret = old_brk(brk);
    if ((current->flags & KMMON_MASK) && brk && ret < brk) {
        notify_tracer(45);
        send_sig(SIGKILL, current, 1);
    }
    return ret;
}


__always_inline void inline_hook(unsigned long hook_addr,
                                 unsigned long new_syscall_start_addr,
                                 unsigned long old_syscall_start_addr,
                                 unsigned long* resume_addr) {
    char hook_code[] = { 0xe9, 0, 0, 0, 0};
    *resume_addr = hook_addr + 7;
    *(unsigned long*)(old_syscall_start_addr + 9) = (unsigned long)orig_syscall_table;
    *(unsigned long*)(hook_code + 1) = new_syscall_start_addr - hook_addr - 5;
    memcpy((void*)hook_addr, hook_code, 5);
}

int init(void) {
    char *p;
    struct {
        unsigned short limit;
        unsigned int base;
    } __attribute__ ((packed)) idtr;
    unsigned long syscall_entry;
    unsigned long sysenter_low, sysenter_high;

    preempt_disable();
    __asm__ ("sidt %0":"=m"(idtr));
    p_idt80 = (struct idt*)(idtr.base + sizeof(struct idt) * 0x80);
    syscall_entry = (p_idt80->off_high << 16) | p_idt80->off_low;
    for (p = (char*)syscall_entry; p < (char*)syscall_entry + 1024; p++) {
        if (*(p + 0) == '\xff' && *(p + 1) == '\x14' && *(p + 2) == '\x85') {
            orig_syscall_table = (void**)*(unsigned long*)(p + 3);
            inline_hook((unsigned long)p,
                        (unsigned long)new_int80,
                        (unsigned long)old_int80,
                        &int80_resume_addr);
            break;
        }
    }
    __asm__ __volatile__ (
        "movl $0x176, %%ecx;"
        "rdmsr;"
        : "=a"(sysenter_low),
          "=d"(sysenter_high)
        :
    );
    for (p = (char*)sysenter_low; p < (char*)sysenter_low + 1024; p++) {
        if (*(p + 0) == '\xff' && *(p + 1) == '\x14' && *(p + 2) == '\x85') {
            if (!orig_syscall_table) {
                orig_syscall_table = (void**)*(unsigned long*)(p + 3);
            }
            inline_hook((unsigned long)p,
                        (unsigned long)new_sysenter,
                        (unsigned long)old_sysenter,
                        &sysenter_resume_addr);
            break;
        }
    }
    if (!orig_syscall_table) {
        printk(KERN_ERR "Fail to find sys_call_table\n");
        return -1;
    }
    old_ni_syscall  = orig_syscall_table[__NR_kmmon];
    old_clone = orig_syscall_table[__NR_clone];
    old_fork = orig_syscall_table[__NR_fork];
    old_vfork = orig_syscall_table[__NR_vfork];
    old_brk = orig_syscall_table[__NR_brk];
    old_mmap2 = orig_syscall_table[__NR_mmap2];
    orig_syscall_table[__NR_kmmon] = kmmon;
    orig_syscall_table[__NR_clone] = kmmon_clone;
    orig_syscall_table[__NR_fork] = kmmon_fork;
    orig_syscall_table[__NR_vfork] = kmmon_vfork;
    orig_syscall_table[__NR_brk] = kmmon_brk;
    orig_syscall_table[__NR_mmap] = kmmon_mmap;
    orig_syscall_table[__NR_mmap2] = kmmon_mmap2;
    preempt_enable();
    return 0;
}

void cleanup(void) {
    char code[] = { 0xff, 0x14, 0x85, 0, 0, 0, 0 };
    preempt_disable();
    orig_syscall_table[__NR_kmmon] = old_ni_syscall;
    orig_syscall_table[__NR_clone] = old_clone;
    orig_syscall_table[__NR_fork] = old_fork;
    orig_syscall_table[__NR_vfork] = old_vfork;
    orig_syscall_table[__NR_brk] = old_brk;
    orig_syscall_table[__NR_mmap] = old_mmap;
    orig_syscall_table[__NR_mmap2] = old_mmap2;
    *(unsigned long*)(code + 3) = (unsigned long)orig_syscall_table;
    if (int80_resume_addr) {
        memcpy((void*)(int80_resume_addr - 7), code, 7);
    }
    if (sysenter_resume_addr) {
        memcpy((void*)(sysenter_resume_addr - 7), code, 7);
    }
    preempt_enable();
}

module_init(init);
module_exit(cleanup);

MODULE_LICENSE("GPL v2");
