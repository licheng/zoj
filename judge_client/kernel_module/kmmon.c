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
#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/mm.h>
#include <linux/module.h>
#include <linux/preempt.h>
#include <linux/rbtree.h>
#include <linux/sched.h>
#include <linux/signal.h>
#include <linux/slab.h>
#include <linux/string.h>

/* struct for idtr */
struct{
    unsigned short limit;
    unsigned int base;
} __attribute__ ((packed)) idtr;

/* struct for idt entry */
struct idt{
    unsigned short off_low;
    unsigned short seg_selector;
    unsigned char reserved, flag;
    unsigned short off_high;
};

static struct idt *p_idt80;
static unsigned long orig_syscall;

static void ** orign_sys_call_table;

asmlinkage long (*old_ni_syscall)(void);
asmlinkage int (*old_clone)(struct pt_regs);
asmlinkage int (*old_fork)(struct pt_regs);
asmlinkage int (*old_vfork)(struct pt_regs);
asmlinkage unsigned long (*old_brk)(unsigned long);

asmlinkage void suicide(void) {
    send_sig(SIGKILL, current, 1);
}

asmlinkage int notify_tracer(int syscall) {
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
            return 0;
        }
        p = q;
        if (!(q->flags & KMMON_MASK)) {
            break;
        }
    }
    if (p->pid == 1) {
        return 0;
    }
    current->exit_code = KMMON_SIG;
    rcu_read_lock();
    send_sig_info(KMMON_SIG, &info, p);
    rcu_read_unlock();
    while (current->exit_code == KMMON_SIG) {
        current->state = TASK_STOPPED;
        schedule();
        printk("wake up\n");
    }
    return current->exit_code;
}

extern void new_int80(void);

void asm_stuff(void) {
    __asm__ __volatile__ (
        ".globl new_int80\n"
        ".align 4,0x90\n"
"new_int80:\n"
        "pushl %%ebx;" // save EBX
        "movl %%esp, %%ebx;"
        "andl %0, %%ebx;" // get the address of struct thread_info
        "movl %c1(%%ebx), %%ebx;" // get the address of struct task
        "andl %2, %c3(%%ebx);" // test if KMMON_MASK is set in task->flags
        "jz normal;" // if not, jump to normal syscall
        "movl %4, %%ebx;" // load the address of syscall_filter_table
        "movl 0(%%ebx, %%eax), %%ebx;" // load the status of current syscall in
                                       // syscall_filter_table
        "andl $3, %%ebx;" 
        "jz normal;" // 0 means enabled
        "jnp disable;" // Not 0, only can be 1 or 3. If the parity flag is not
                       // set, it should have odd number of 1s in the result,
                       // which is 1. Jump to disable.
        "pushl %%ecx;"
        "pushl %%edx;"
        "pushl %%esi;"
        "pushl %%edi;"
        "pushl %%eax;"
        "call notify_tracer;" // notify the tracer
        "andl $-1, %%eax;" // test if the return valud is 0
        "popl %%eax;"
        "popl %%edi;"
        "popl %%esi;"
        "popl %%edx;"
        "popl %%ecx;"
        "jz normal;" // 0 means not killed, jump to normal syscall.
"disable:\n"
        "call suicide;" // kill the process
        "movl $0xffff, %%eax;" // set to an invalid syscall
"normal:\n"
        "popl %%ebx;" // restore EBX
        "jmp *%5;" // jump to original int80
        :
        : "i"(-THREAD_SIZE),
          "i"(&((struct thread_info*)0)->task),
          "i"(KMMON_MASK),
          "i"(&((struct task_struct*)0)->flags),
          "i"(syscall_filter_table),
          "m"(orig_syscall)
    );
}

asmlinkage unsigned long kmmon(int request, unsigned long pid, unsigned long addr, unsigned long data) {
    struct task_struct* p;
    switch (request) {
        case KMMON_TRACEME:
            current->flags |= KMMON_MASK;
            break;
        case KMMON_CONTINUE:
        case KMMON_KILL:
        case KMMON_READMEM:
        case KMMON_GETREG:
            rcu_read_lock();
            p = find_task_by_pid(pid);
            if (!p || !(p->flags & KMMON_MASK) || !(p->state & TASK_STOPPED)) {
                printk(KERN_ERR "Invalid pid: %ld\n", pid);
                return -1;
            }
            if (request == KMMON_READMEM) {
                struct page *page;
                struct vm_area_struct *vma;
                int offset, len, tmp;
                if (p->mm == NULL) {
                    printk(KERN_ERR "Fail to get mm: %ld\n", pid);
                    return -1;
                }
                if (get_user_pages(p, p->mm, addr, 1, 0, 1, &page, &vma) <= 0) {
                    printk(KERN_ERR "Fail to get user pages: %ld, %lx\n", pid, addr);
                    return -1;
                }
                if (PageHighMem(page)) {
                    printk(KERN_ERR "High mem page: %ld, %lx\n", pid, addr);
                    return -1;
                }
                offset = addr & (PAGE_SIZE - 1);
                len = sizeof(data) > PAGE_SIZE - offset ? PAGE_SIZE - offset : sizeof(data);
                memcpy(&tmp, page_address(page) + offset, len);
                put_user(tmp, (unsigned long*)data);
            } else if (request == KMMON_GETREG) {
                put_user(*((int*)p->thread.esp0 - 6 - addr), (unsigned long*)data);
            } else {
                p->exit_code = request == KMMON_KILL;
                wake_up_process(p);
            }
            rcu_read_unlock();
            break;
        default:
            return -1;
    }
    return 0;
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

asmlinkage unsigned long kmmon_brk(unsigned long brk) {
    unsigned long ret = old_brk(brk);
    if ((current->flags & KMMON_MASK) && brk && ret < brk) {
        notify_tracer(45);
        send_sig(SIGKILL, current, 1);
    }
    return ret;
}

int init(void) {
    char *p;
    unsigned new_syscall;
    orign_sys_call_table = 0;
    __asm__ ("sidt %0":"=m"(idtr));
    p_idt80 = (struct idt*)(idtr.base + sizeof(struct idt) * 0x80);
    orig_syscall = (p_idt80->off_high << 16) | p_idt80->off_low;
    for (p = (char*)orig_syscall; p < (char*)orig_syscall + 1024; p++) {
        if (*(p + 0) == '\xff' && *(p + 1) == '\x14' && *(p + 2) == '\x85') {
            orign_sys_call_table = (void**)*(unsigned long*)(p + 3);
            break;
        }
    }
    if (!orign_sys_call_table) {
        printk(KERN_ERR "Fail to find sys_call_table\n");
        return -1;
    }
    old_ni_syscall  = orign_sys_call_table[__NR_kmmon];
    old_clone = orign_sys_call_table[__NR_clone];
    old_fork = orign_sys_call_table[__NR_fork];
    old_vfork = orign_sys_call_table[__NR_vfork];
    old_brk = orign_sys_call_table[__NR_brk];
    orign_sys_call_table[__NR_kmmon] = kmmon;
    orign_sys_call_table[__NR_clone] = kmmon_clone;
    orign_sys_call_table[__NR_fork] = kmmon_fork;
    orign_sys_call_table[__NR_vfork] = kmmon_vfork;
    orign_sys_call_table[__NR_brk] = kmmon_brk;
    new_syscall = (unsigned long)&new_int80;
    p_idt80->off_low = (unsigned short)(new_syscall & 0x0000ffff);
    p_idt80->off_high = (unsigned short)((new_syscall>>16) & 0x0000ffff);
    return 0;
}

void cleanup(void) {
    orign_sys_call_table[__NR_kmmon] = old_ni_syscall;
    orign_sys_call_table[__NR_clone] = old_clone;
    orign_sys_call_table[__NR_fork] = old_fork;
    orign_sys_call_table[__NR_vfork] = old_vfork;
    orign_sys_call_table[__NR_brk] = old_brk;
    p_idt80->off_low = (unsigned short)(orig_syscall & 0x0000ffff);
    p_idt80->off_high = (unsigned short)((orig_syscall>>16) & 0x0000ffff);
    return;
}

module_init(init);
module_exit(cleanup);

MODULE_LICENSE("GPL v2");
