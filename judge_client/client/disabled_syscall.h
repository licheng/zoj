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

#ifndef __DISABLED_SYSCALL_H__
#define __DISABLED_SYSCALL_H__

#include <sys/syscall.h>

namespace {

bool disabled_syscall[512] = {0};
const char* syscall_name[512] = {0};

int init() {
#ifdef __NR_fork
    disabled_syscall[__NR_fork] = 1;
#endif
#ifdef __NR_open
    disabled_syscall[__NR_open] = 1;
#endif
#ifdef __NR_creat
    disabled_syscall[__NR_creat] = 1;
#endif
#ifdef __NR_link
    disabled_syscall[__NR_link] = 1;
#endif
#ifdef __NR_unlink
    disabled_syscall[__NR_unlink] = 1;
#endif
#ifdef __NR_execve
    disabled_syscall[__NR_execve] = 1;
#endif
#ifdef __NR_chdir
    disabled_syscall[__NR_chdir] = 1;
#endif
#ifdef __NR_mknod
    disabled_syscall[__NR_mknod] = 1;
#endif
#ifdef __NR_chmod
    disabled_syscall[__NR_chmod] = 1;
#endif
#ifdef __NR_break
    disabled_syscall[__NR_break] = 1;
#endif
#ifdef __NR_oldstat
    disabled_syscall[__NR_oldstat] = 1;
#endif
#ifdef __NR_mount
    disabled_syscall[__NR_mount] = 1;
#endif
#ifdef __NR_umount
    disabled_syscall[__NR_umount] = 1;
#endif
#ifdef __NR_ptrace
    disabled_syscall[__NR_ptrace] = 1;
#endif
#ifdef __NR_oldfstat
    disabled_syscall[__NR_oldfstat] = 1;
#endif
#ifdef __NR_pause
    disabled_syscall[__NR_pause] = 1;
#endif
#ifdef __NR_stty
    disabled_syscall[__NR_stty] = 1;
#endif
#ifdef __NR_gtty
    disabled_syscall[__NR_gtty] = 1;
#endif
#ifdef __NR_ftime
    disabled_syscall[__NR_ftime] = 1;
#endif
#ifdef __NR_sync
    disabled_syscall[__NR_sync] = 1;
#endif
#ifdef __NR_rename
    disabled_syscall[__NR_rename] = 1;
#endif
#ifdef __NR_mkdir
    disabled_syscall[__NR_mkdir] = 1;
#endif
#ifdef __NR_rmdir
    disabled_syscall[__NR_rmdir] = 1;
#endif
#ifdef __NR_dup
    disabled_syscall[__NR_dup] = 1;
#endif
#ifdef __NR_prof
    disabled_syscall[__NR_prof] = 1;
#endif
#ifdef __NR_signal
    disabled_syscall[__NR_signal] = 1;
#endif
#ifdef __NR_umount2
    disabled_syscall[__NR_umount2] = 1;
#endif
#ifdef __NR_lock
    disabled_syscall[__NR_lock] = 1;
#endif
#ifdef __NR_mpx
    disabled_syscall[__NR_mpx] = 1;
#endif
#ifdef __NR_ulimit
    disabled_syscall[__NR_ulimit] = 1;
#endif
#ifdef __NR_oldolduname
    disabled_syscall[__NR_oldolduname] = 1;
#endif
#ifdef __NR_chroot
    disabled_syscall[__NR_chroot] = 1;
#endif
#ifdef __NR_dup2
    disabled_syscall[__NR_dup2] = 1;
#endif
#ifdef __NR_setrlimit
    disabled_syscall[__NR_setrlimit] = 1;
#endif
#ifdef __NR_select
    disabled_syscall[__NR_select] = 1;
#endif
#ifdef __NR_symlink
    disabled_syscall[__NR_symlink] = 1;
#endif
#ifdef __NR_oldlstat
    disabled_syscall[__NR_oldlstat] = 1;
#endif
#ifdef __NR_swapon
    disabled_syscall[__NR_swapon] = 1;
#endif
#ifdef __NR_reboot
    disabled_syscall[__NR_reboot] = 1;
#endif
#ifdef __NR_readdir
    disabled_syscall[__NR_readdir] = 1;
#endif
#ifdef __NR_fchmod
    disabled_syscall[__NR_fchmod] = 1;
#endif
#ifdef __NR_profil
    disabled_syscall[__NR_profil] = 1;
#endif
#ifdef __NR_statfs
    disabled_syscall[__NR_statfs] = 1;
#endif
#ifdef __NR_fstatfs
    disabled_syscall[__NR_fstatfs] = 1;
#endif
#ifdef __NR_syslog
    disabled_syscall[__NR_syslog] = 1;
#endif
#ifdef __NR_idle
    disabled_syscall[__NR_idle] = 1;
#endif
#ifdef __NR_swapoff
    disabled_syscall[__NR_swapoff] = 1;
#endif
#ifdef __NR_ipc
    disabled_syscall[__NR_ipc] = 1;
#endif
#ifdef __NR_clone
    disabled_syscall[__NR_clone] = 1;
#endif
#ifdef __NR_create_module
    disabled_syscall[__NR_create_module] = 1;
#endif
#ifdef __NR_init_module
    disabled_syscall[__NR_init_module] = 1;
#endif
#ifdef __NR_delete_module
    disabled_syscall[__NR_delete_module] = 1;
#endif
#ifdef __NR_afs_syscall
    disabled_syscall[__NR_afs_syscall] = 1;
#endif
#ifdef __NR_getdents
    disabled_syscall[__NR_getdents] = 1;
#endif
#ifdef __NR_newselect
    disabled_syscall[__NR_newselect] = 1;
#endif
#ifdef __NR_nanosleep
    disabled_syscall[__NR_nanosleep] = 1;
#endif
#ifdef __NR_query_module
    disabled_syscall[__NR_query_module] = 1;
#endif
#ifdef __NR_pread64
    disabled_syscall[__NR_pread64] = 1;
#endif
#ifdef __NR_pwrite64
    disabled_syscall[__NR_pwrite64] = 1;
#endif
#ifdef __NR_chown
    disabled_syscall[__NR_chown] = 1;
#endif
#ifdef __NR_sigaltstack
    disabled_syscall[__NR_sigaltstack] = 1;
#endif
#ifdef __NR_sendfile
    disabled_syscall[__NR_sendfile] = 1;
#endif
#ifdef __NR_getpmsg
    disabled_syscall[__NR_getpmsg] = 1;
#endif
#ifdef __NR_putpmsg
    disabled_syscall[__NR_putpmsg] = 1;
#endif
#ifdef __NR_vfork
    disabled_syscall[__NR_vfork] = 1;
#endif
#ifdef __NR_lchown32
    disabled_syscall[__NR_lchown32] = 1;
#endif
#ifdef __NR_chown32
    disabled_syscall[__NR_chown32] = 1;
#endif
#ifdef __NR_madvise1
    disabled_syscall[__NR_madvise1] = 1;
#endif
#ifdef __NR_fcntl64
    disabled_syscall[__NR_fcntl64] = 1;
#endif
#ifdef __NR_unused
    disabled_syscall[__NR_unused] = 1;
#endif
#ifdef __NR_setxattr
    disabled_syscall[__NR_setxattr] = 1;
#endif
#ifdef __NR_lsetxattr
    disabled_syscall[__NR_lsetxattr] = 1;
#endif
#ifdef __NR_fsetxattr
    disabled_syscall[__NR_fsetxattr] = 1;
#endif
#ifdef __NR_getxattr
    disabled_syscall[__NR_getxattr] = 1;
#endif
#ifdef __NR_lgetxattr
    disabled_syscall[__NR_lgetxattr] = 1;
#endif
#ifdef __NR_fgetxattr
    disabled_syscall[__NR_fgetxattr] = 1;
#endif
#ifdef __NR_listxattr
    disabled_syscall[__NR_listxattr] = 1;
#endif
#ifdef __NR_llistxattr
    disabled_syscall[__NR_llistxattr] = 1;
#endif
#ifdef __NR_flistxattr
    disabled_syscall[__NR_flistxattr] = 1;
#endif
#ifdef __NR_removexattr
    disabled_syscall[__NR_removexattr] = 1;
#endif
#ifdef __NR_lremovexattr
    disabled_syscall[__NR_lremovexattr] = 1;
#endif
#ifdef __NR_fremovexattr
    disabled_syscall[__NR_fremovexattr] = 1;
#endif
#ifdef __NR_sendfile64
    disabled_syscall[__NR_sendfile64] = 1;
#endif
#ifdef __NR_futex
    disabled_syscall[__NR_futex] = 1;
#endif
#ifdef __NR_sched_setaffinity
    disabled_syscall[__NR_sched_setaffinity] = 1;
#endif
#ifdef __NR_sched_getaffinity
    disabled_syscall[__NR_sched_getaffinity] = 1;
#endif
#ifdef __NR_io_setup
    disabled_syscall[__NR_io_setup] = 1;
#endif
#ifdef __NR_io_destroy
    disabled_syscall[__NR_io_destroy] = 1;
#endif
#ifdef __NR_io_getevents
    disabled_syscall[__NR_io_getevents] = 1;
#endif
#ifdef __NR_io_submit
    disabled_syscall[__NR_io_submit] = 1;
#endif
#ifdef __NR_io_cancel
    disabled_syscall[__NR_io_cancel] = 1;
#endif
#ifdef __NR_fadvise64
    disabled_syscall[__NR_fadvise64] = 1;
#endif
#ifdef __NR_unused
    disabled_syscall[__NR_unused] = 1;
#endif
#ifdef __NR_lookup_dcookie
    disabled_syscall[__NR_lookup_dcookie] = 1;
#endif
#ifdef __NR_epoll_create
    disabled_syscall[__NR_epoll_create] = 1;
#endif
#ifdef __NR_epoll_ctl
    disabled_syscall[__NR_epoll_ctl] = 1;
#endif
#ifdef __NR_epoll_wait
    disabled_syscall[__NR_epoll_wait] = 1;
#endif
#ifdef __NR_remap_file_pages
    disabled_syscall[__NR_remap_file_pages] = 1;
#endif
#ifdef __NR_set_tid_address
    disabled_syscall[__NR_set_tid_address] = 1;
#endif
#ifdef __NR_timer_create
    disabled_syscall[__NR_timer_create] = 1;
#endif
#ifdef __NR_timer_settime
    disabled_syscall[__NR_timer_settime] = 1;
#endif
#ifdef __NR_timer_gettime
    disabled_syscall[__NR_timer_gettime] = 1;
#endif
#ifdef __NR_timer_getoverrun
    disabled_syscall[__NR_timer_getoverrun] = 1;
#endif
#ifdef __NR_timer_delete
    disabled_syscall[__NR_timer_delete] = 1;
#endif
#ifdef __NR_clock_settime
    disabled_syscall[__NR_clock_settime] = 1;
#endif
#ifdef __NR_clock_gettime
    disabled_syscall[__NR_clock_gettime] = 1;
#endif
#ifdef __NR_clock_getres
    disabled_syscall[__NR_clock_getres] = 1;
#endif
#ifdef __NR_clock_nanosleep
    disabled_syscall[__NR_clock_nanosleep] = 1;
#endif
#ifdef __NR_statfs64
    disabled_syscall[__NR_statfs64] = 1;
#endif
#ifdef __NR_fstatfs64
    disabled_syscall[__NR_fstatfs64] = 1;
#endif
#ifdef __NR_tgkill
    disabled_syscall[__NR_tgkill] = 1;
#endif
#ifdef __NR_utimes
    disabled_syscall[__NR_utimes] = 1;
#endif
#ifdef __NR_fadvise64_64
    disabled_syscall[__NR_fadvise64_64] = 1;
#endif
#ifdef __NR_vserver
    disabled_syscall[__NR_vserver] = 1;
#endif

#ifdef __NR_accept
    syscall_name[__NR_accept] = "__NR_accept";
#endif
#ifdef __NR_accept4
    syscall_name[__NR_accept4] = "__NR_accept4";
#endif
#ifdef __NR_access
    syscall_name[__NR_access] = "__NR_access";
#endif
#ifdef __NR_acct
    syscall_name[__NR_acct] = "__NR_acct";
#endif
#ifdef __NR_add_key
    syscall_name[__NR_add_key] = "__NR_add_key";
#endif
#ifdef __NR_adjtimex
    syscall_name[__NR_adjtimex] = "__NR_adjtimex";
#endif
#ifdef __NR_afs_syscall
    syscall_name[__NR_afs_syscall] = "__NR_afs_syscall";
#endif
#ifdef __NR_alarm
    syscall_name[__NR_alarm] = "__NR_alarm";
#endif
#ifdef __NR_arch_prctl
    syscall_name[__NR_arch_prctl] = "__NR_arch_prctl";
#endif
#ifdef __NR_bdflush
    syscall_name[__NR_bdflush] = "__NR_bdflush";
#endif
#ifdef __NR_bind
    syscall_name[__NR_bind] = "__NR_bind";
#endif
#ifdef __NR_break
    syscall_name[__NR_break] = "__NR_break";
#endif
#ifdef __NR_brk
    syscall_name[__NR_brk] = "__NR_brk";
#endif
#ifdef __NR_capget
    syscall_name[__NR_capget] = "__NR_capget";
#endif
#ifdef __NR_capset
    syscall_name[__NR_capset] = "__NR_capset";
#endif
#ifdef __NR_chdir
    syscall_name[__NR_chdir] = "__NR_chdir";
#endif
#ifdef __NR_chmod
    syscall_name[__NR_chmod] = "__NR_chmod";
#endif
#ifdef __NR_chown
    syscall_name[__NR_chown] = "__NR_chown";
#endif
#ifdef __NR_chown32
    syscall_name[__NR_chown32] = "__NR_chown32";
#endif
#ifdef __NR_chroot
    syscall_name[__NR_chroot] = "__NR_chroot";
#endif
#ifdef __NR_clock_getres
    syscall_name[__NR_clock_getres] = "__NR_clock_getres";
#endif
#ifdef __NR_clock_gettime
    syscall_name[__NR_clock_gettime] = "__NR_clock_gettime";
#endif
#ifdef __NR_clock_nanosleep
    syscall_name[__NR_clock_nanosleep] = "__NR_clock_nanosleep";
#endif
#ifdef __NR_clock_settime
    syscall_name[__NR_clock_settime] = "__NR_clock_settime";
#endif
#ifdef __NR_clone
    syscall_name[__NR_clone] = "__NR_clone";
#endif
#ifdef __NR_close
    syscall_name[__NR_close] = "__NR_close";
#endif
#ifdef __NR_connect
    syscall_name[__NR_connect] = "__NR_connect";
#endif
#ifdef __NR_creat
    syscall_name[__NR_creat] = "__NR_creat";
#endif
#ifdef __NR_create_module
    syscall_name[__NR_create_module] = "__NR_create_module";
#endif
#ifdef __NR_delete_module
    syscall_name[__NR_delete_module] = "__NR_delete_module";
#endif
#ifdef __NR_dup
    syscall_name[__NR_dup] = "__NR_dup";
#endif
#ifdef __NR_dup2
    syscall_name[__NR_dup2] = "__NR_dup2";
#endif
#ifdef __NR_dup3
    syscall_name[__NR_dup3] = "__NR_dup3";
#endif
#ifdef __NR_epoll_create
    syscall_name[__NR_epoll_create] = "__NR_epoll_create";
#endif
#ifdef __NR_epoll_create1
    syscall_name[__NR_epoll_create1] = "__NR_epoll_create1";
#endif
#ifdef __NR_epoll_ctl
    syscall_name[__NR_epoll_ctl] = "__NR_epoll_ctl";
#endif
#ifdef __NR_epoll_ctl_old
    syscall_name[__NR_epoll_ctl_old] = "__NR_epoll_ctl_old";
#endif
#ifdef __NR_epoll_pwait
    syscall_name[__NR_epoll_pwait] = "__NR_epoll_pwait";
#endif
#ifdef __NR_epoll_wait
    syscall_name[__NR_epoll_wait] = "__NR_epoll_wait";
#endif
#ifdef __NR_epoll_wait_old
    syscall_name[__NR_epoll_wait_old] = "__NR_epoll_wait_old";
#endif
#ifdef __NR_eventfd
    syscall_name[__NR_eventfd] = "__NR_eventfd";
#endif
#ifdef __NR_eventfd2
    syscall_name[__NR_eventfd2] = "__NR_eventfd2";
#endif
#ifdef __NR_execve
    syscall_name[__NR_execve] = "__NR_execve";
#endif
#ifdef __NR_exit
    syscall_name[__NR_exit] = "__NR_exit";
#endif
#ifdef __NR_exit_group
    syscall_name[__NR_exit_group] = "__NR_exit_group";
#endif
#ifdef __NR_faccessat
    syscall_name[__NR_faccessat] = "__NR_faccessat";
#endif
#ifdef __NR_fadvise64
    syscall_name[__NR_fadvise64] = "__NR_fadvise64";
#endif
#ifdef __NR_fadvise64_64
    syscall_name[__NR_fadvise64_64] = "__NR_fadvise64_64";
#endif
#ifdef __NR_fallocate
    syscall_name[__NR_fallocate] = "__NR_fallocate";
#endif
#ifdef __NR_fchdir
    syscall_name[__NR_fchdir] = "__NR_fchdir";
#endif
#ifdef __NR_fchmod
    syscall_name[__NR_fchmod] = "__NR_fchmod";
#endif
#ifdef __NR_fchmodat
    syscall_name[__NR_fchmodat] = "__NR_fchmodat";
#endif
#ifdef __NR_fchown
    syscall_name[__NR_fchown] = "__NR_fchown";
#endif
#ifdef __NR_fchown32
    syscall_name[__NR_fchown32] = "__NR_fchown32";
#endif
#ifdef __NR_fchownat
    syscall_name[__NR_fchownat] = "__NR_fchownat";
#endif
#ifdef __NR_fcntl
    syscall_name[__NR_fcntl] = "__NR_fcntl";
#endif
#ifdef __NR_fcntl64
    syscall_name[__NR_fcntl64] = "__NR_fcntl64";
#endif
#ifdef __NR_fdatasync
    syscall_name[__NR_fdatasync] = "__NR_fdatasync";
#endif
#ifdef __NR_fgetxattr
    syscall_name[__NR_fgetxattr] = "__NR_fgetxattr";
#endif
#ifdef __NR_flistxattr
    syscall_name[__NR_flistxattr] = "__NR_flistxattr";
#endif
#ifdef __NR_flock
    syscall_name[__NR_flock] = "__NR_flock";
#endif
#ifdef __NR_fork
    syscall_name[__NR_fork] = "__NR_fork";
#endif
#ifdef __NR_fremovexattr
    syscall_name[__NR_fremovexattr] = "__NR_fremovexattr";
#endif
#ifdef __NR_fsetxattr
    syscall_name[__NR_fsetxattr] = "__NR_fsetxattr";
#endif
#ifdef __NR_fstat
    syscall_name[__NR_fstat] = "__NR_fstat";
#endif
#ifdef __NR_fstat64
    syscall_name[__NR_fstat64] = "__NR_fstat64";
#endif
#ifdef __NR_fstatat64
    syscall_name[__NR_fstatat64] = "__NR_fstatat64";
#endif
#ifdef __NR_fstatfs
    syscall_name[__NR_fstatfs] = "__NR_fstatfs";
#endif
#ifdef __NR_fstatfs64
    syscall_name[__NR_fstatfs64] = "__NR_fstatfs64";
#endif
#ifdef __NR_fsync
    syscall_name[__NR_fsync] = "__NR_fsync";
#endif
#ifdef __NR_ftime
    syscall_name[__NR_ftime] = "__NR_ftime";
#endif
#ifdef __NR_ftruncate
    syscall_name[__NR_ftruncate] = "__NR_ftruncate";
#endif
#ifdef __NR_ftruncate64
    syscall_name[__NR_ftruncate64] = "__NR_ftruncate64";
#endif
#ifdef __NR_futex
    syscall_name[__NR_futex] = "__NR_futex";
#endif
#ifdef __NR_futimesat
    syscall_name[__NR_futimesat] = "__NR_futimesat";
#endif
#ifdef __NR_getcpu
    syscall_name[__NR_getcpu] = "__NR_getcpu";
#endif
#ifdef __NR_getcwd
    syscall_name[__NR_getcwd] = "__NR_getcwd";
#endif
#ifdef __NR_getdents
    syscall_name[__NR_getdents] = "__NR_getdents";
#endif
#ifdef __NR_getdents64
    syscall_name[__NR_getdents64] = "__NR_getdents64";
#endif
#ifdef __NR_getegid
    syscall_name[__NR_getegid] = "__NR_getegid";
#endif
#ifdef __NR_getegid32
    syscall_name[__NR_getegid32] = "__NR_getegid32";
#endif
#ifdef __NR_geteuid
    syscall_name[__NR_geteuid] = "__NR_geteuid";
#endif
#ifdef __NR_geteuid32
    syscall_name[__NR_geteuid32] = "__NR_geteuid32";
#endif
#ifdef __NR_getgid
    syscall_name[__NR_getgid] = "__NR_getgid";
#endif
#ifdef __NR_getgid32
    syscall_name[__NR_getgid32] = "__NR_getgid32";
#endif
#ifdef __NR_getgroups
    syscall_name[__NR_getgroups] = "__NR_getgroups";
#endif
#ifdef __NR_getgroups32
    syscall_name[__NR_getgroups32] = "__NR_getgroups32";
#endif
#ifdef __NR_getitimer
    syscall_name[__NR_getitimer] = "__NR_getitimer";
#endif
#ifdef __NR_get_kernel_syms
    syscall_name[__NR_get_kernel_syms] = "__NR_get_kernel_syms";
#endif
#ifdef __NR_get_mempolicy
    syscall_name[__NR_get_mempolicy] = "__NR_get_mempolicy";
#endif
#ifdef __NR_getpeername
    syscall_name[__NR_getpeername] = "__NR_getpeername";
#endif
#ifdef __NR_getpgid
    syscall_name[__NR_getpgid] = "__NR_getpgid";
#endif
#ifdef __NR_getpgrp
    syscall_name[__NR_getpgrp] = "__NR_getpgrp";
#endif
#ifdef __NR_getpid
    syscall_name[__NR_getpid] = "__NR_getpid";
#endif
#ifdef __NR_getpmsg
    syscall_name[__NR_getpmsg] = "__NR_getpmsg";
#endif
#ifdef __NR_getppid
    syscall_name[__NR_getppid] = "__NR_getppid";
#endif
#ifdef __NR_getpriority
    syscall_name[__NR_getpriority] = "__NR_getpriority";
#endif
#ifdef __NR_getresgid
    syscall_name[__NR_getresgid] = "__NR_getresgid";
#endif
#ifdef __NR_getresgid32
    syscall_name[__NR_getresgid32] = "__NR_getresgid32";
#endif
#ifdef __NR_getresuid
    syscall_name[__NR_getresuid] = "__NR_getresuid";
#endif
#ifdef __NR_getresuid32
    syscall_name[__NR_getresuid32] = "__NR_getresuid32";
#endif
#ifdef __NR_getrlimit
    syscall_name[__NR_getrlimit] = "__NR_getrlimit";
#endif
#ifdef __NR_get_robust_list
    syscall_name[__NR_get_robust_list] = "__NR_get_robust_list";
#endif
#ifdef __NR_getrusage
    syscall_name[__NR_getrusage] = "__NR_getrusage";
#endif
#ifdef __NR_getsid
    syscall_name[__NR_getsid] = "__NR_getsid";
#endif
#ifdef __NR_getsockname
    syscall_name[__NR_getsockname] = "__NR_getsockname";
#endif
#ifdef __NR_getsockopt
    syscall_name[__NR_getsockopt] = "__NR_getsockopt";
#endif
#ifdef __NR_get_thread_area
    syscall_name[__NR_get_thread_area] = "__NR_get_thread_area";
#endif
#ifdef __NR_gettid
    syscall_name[__NR_gettid] = "__NR_gettid";
#endif
#ifdef __NR_gettimeofday
    syscall_name[__NR_gettimeofday] = "__NR_gettimeofday";
#endif
#ifdef __NR_getuid
    syscall_name[__NR_getuid] = "__NR_getuid";
#endif
#ifdef __NR_getuid32
    syscall_name[__NR_getuid32] = "__NR_getuid32";
#endif
#ifdef __NR_getxattr
    syscall_name[__NR_getxattr] = "__NR_getxattr";
#endif
#ifdef __NR_gtty
    syscall_name[__NR_gtty] = "__NR_gtty";
#endif
#ifdef __NR_idle
    syscall_name[__NR_idle] = "__NR_idle";
#endif
#ifdef __NR_init_module
    syscall_name[__NR_init_module] = "__NR_init_module";
#endif
#ifdef __NR_inotify_add_watch
    syscall_name[__NR_inotify_add_watch] = "__NR_inotify_add_watch";
#endif
#ifdef __NR_inotify_init
    syscall_name[__NR_inotify_init] = "__NR_inotify_init";
#endif
#ifdef __NR_inotify_init1
    syscall_name[__NR_inotify_init1] = "__NR_inotify_init1";
#endif
#ifdef __NR_inotify_rm_watch
    syscall_name[__NR_inotify_rm_watch] = "__NR_inotify_rm_watch";
#endif
#ifdef __NR_io_cancel
    syscall_name[__NR_io_cancel] = "__NR_io_cancel";
#endif
#ifdef __NR_ioctl
    syscall_name[__NR_ioctl] = "__NR_ioctl";
#endif
#ifdef __NR_io_destroy
    syscall_name[__NR_io_destroy] = "__NR_io_destroy";
#endif
#ifdef __NR_io_getevents
    syscall_name[__NR_io_getevents] = "__NR_io_getevents";
#endif
#ifdef __NR_ioperm
    syscall_name[__NR_ioperm] = "__NR_ioperm";
#endif
#ifdef __NR_iopl
    syscall_name[__NR_iopl] = "__NR_iopl";
#endif
#ifdef __NR_ioprio_get
    syscall_name[__NR_ioprio_get] = "__NR_ioprio_get";
#endif
#ifdef __NR_ioprio_set
    syscall_name[__NR_ioprio_set] = "__NR_ioprio_set";
#endif
#ifdef __NR_io_setup
    syscall_name[__NR_io_setup] = "__NR_io_setup";
#endif
#ifdef __NR_io_submit
    syscall_name[__NR_io_submit] = "__NR_io_submit";
#endif
#ifdef __NR_ipc
    syscall_name[__NR_ipc] = "__NR_ipc";
#endif
#ifdef __NR_kexec_load
    syscall_name[__NR_kexec_load] = "__NR_kexec_load";
#endif
#ifdef __NR_keyctl
    syscall_name[__NR_keyctl] = "__NR_keyctl";
#endif
#ifdef __NR_kill
    syscall_name[__NR_kill] = "__NR_kill";
#endif
#ifdef __NR_lchown
    syscall_name[__NR_lchown] = "__NR_lchown";
#endif
#ifdef __NR_lchown32
    syscall_name[__NR_lchown32] = "__NR_lchown32";
#endif
#ifdef __NR_lgetxattr
    syscall_name[__NR_lgetxattr] = "__NR_lgetxattr";
#endif
#ifdef __NR_link
    syscall_name[__NR_link] = "__NR_link";
#endif
#ifdef __NR_linkat
    syscall_name[__NR_linkat] = "__NR_linkat";
#endif
#ifdef __NR_listen
    syscall_name[__NR_listen] = "__NR_listen";
#endif
#ifdef __NR_listxattr
    syscall_name[__NR_listxattr] = "__NR_listxattr";
#endif
#ifdef __NR_llistxattr
    syscall_name[__NR_llistxattr] = "__NR_llistxattr";
#endif
#ifdef __NR__llseek
    syscall_name[__NR__llseek] = "__NR__llseek";
#endif
#ifdef __NR_lock
    syscall_name[__NR_lock] = "__NR_lock";
#endif
#ifdef __NR_lookup_dcookie
    syscall_name[__NR_lookup_dcookie] = "__NR_lookup_dcookie";
#endif
#ifdef __NR_lremovexattr
    syscall_name[__NR_lremovexattr] = "__NR_lremovexattr";
#endif
#ifdef __NR_lseek
    syscall_name[__NR_lseek] = "__NR_lseek";
#endif
#ifdef __NR_lsetxattr
    syscall_name[__NR_lsetxattr] = "__NR_lsetxattr";
#endif
#ifdef __NR_lstat
    syscall_name[__NR_lstat] = "__NR_lstat";
#endif
#ifdef __NR_lstat64
    syscall_name[__NR_lstat64] = "__NR_lstat64";
#endif
#ifdef __NR_madvise
    syscall_name[__NR_madvise] = "__NR_madvise";
#endif
#ifdef __NR_madvise1
    syscall_name[__NR_madvise1] = "__NR_madvise1";
#endif
#ifdef __NR_mbind
    syscall_name[__NR_mbind] = "__NR_mbind";
#endif
#ifdef __NR_migrate_pages
    syscall_name[__NR_migrate_pages] = "__NR_migrate_pages";
#endif
#ifdef __NR_mincore
    syscall_name[__NR_mincore] = "__NR_mincore";
#endif
#ifdef __NR_mkdir
    syscall_name[__NR_mkdir] = "__NR_mkdir";
#endif
#ifdef __NR_mkdirat
    syscall_name[__NR_mkdirat] = "__NR_mkdirat";
#endif
#ifdef __NR_mknod
    syscall_name[__NR_mknod] = "__NR_mknod";
#endif
#ifdef __NR_mknodat
    syscall_name[__NR_mknodat] = "__NR_mknodat";
#endif
#ifdef __NR_mlock
    syscall_name[__NR_mlock] = "__NR_mlock";
#endif
#ifdef __NR_mlockall
    syscall_name[__NR_mlockall] = "__NR_mlockall";
#endif
#ifdef __NR_mmap
    syscall_name[__NR_mmap] = "__NR_mmap";
#endif
#ifdef __NR_mmap2
    syscall_name[__NR_mmap2] = "__NR_mmap2";
#endif
#ifdef __NR_modify_ldt
    syscall_name[__NR_modify_ldt] = "__NR_modify_ldt";
#endif
#ifdef __NR_mount
    syscall_name[__NR_mount] = "__NR_mount";
#endif
#ifdef __NR_move_pages
    syscall_name[__NR_move_pages] = "__NR_move_pages";
#endif
#ifdef __NR_mprotect
    syscall_name[__NR_mprotect] = "__NR_mprotect";
#endif
#ifdef __NR_mpx
    syscall_name[__NR_mpx] = "__NR_mpx";
#endif
#ifdef __NR_mq_getsetattr
    syscall_name[__NR_mq_getsetattr] = "__NR_mq_getsetattr";
#endif
#ifdef __NR_mq_notify
    syscall_name[__NR_mq_notify] = "__NR_mq_notify";
#endif
#ifdef __NR_mq_open
    syscall_name[__NR_mq_open] = "__NR_mq_open";
#endif
#ifdef __NR_mq_timedreceive
    syscall_name[__NR_mq_timedreceive] = "__NR_mq_timedreceive";
#endif
#ifdef __NR_mq_timedsend
    syscall_name[__NR_mq_timedsend] = "__NR_mq_timedsend";
#endif
#ifdef __NR_mq_unlink
    syscall_name[__NR_mq_unlink] = "__NR_mq_unlink";
#endif
#ifdef __NR_mremap
    syscall_name[__NR_mremap] = "__NR_mremap";
#endif
#ifdef __NR_msgctl
    syscall_name[__NR_msgctl] = "__NR_msgctl";
#endif
#ifdef __NR_msgget
    syscall_name[__NR_msgget] = "__NR_msgget";
#endif
#ifdef __NR_msgrcv
    syscall_name[__NR_msgrcv] = "__NR_msgrcv";
#endif
#ifdef __NR_msgsnd
    syscall_name[__NR_msgsnd] = "__NR_msgsnd";
#endif
#ifdef __NR_msync
    syscall_name[__NR_msync] = "__NR_msync";
#endif
#ifdef __NR_munlock
    syscall_name[__NR_munlock] = "__NR_munlock";
#endif
#ifdef __NR_munlockall
    syscall_name[__NR_munlockall] = "__NR_munlockall";
#endif
#ifdef __NR_munmap
    syscall_name[__NR_munmap] = "__NR_munmap";
#endif
#ifdef __NR_nanosleep
    syscall_name[__NR_nanosleep] = "__NR_nanosleep";
#endif
#ifdef __NR_newfstatat
    syscall_name[__NR_newfstatat] = "__NR_newfstatat";
#endif
#ifdef __NR__newselect
    syscall_name[__NR__newselect] = "__NR__newselect";
#endif
#ifdef __NR_nfsservctl
    syscall_name[__NR_nfsservctl] = "__NR_nfsservctl";
#endif
#ifdef __NR_nice
    syscall_name[__NR_nice] = "__NR_nice";
#endif
#ifdef __NR_oldfstat
    syscall_name[__NR_oldfstat] = "__NR_oldfstat";
#endif
#ifdef __NR_oldlstat
    syscall_name[__NR_oldlstat] = "__NR_oldlstat";
#endif
#ifdef __NR_oldolduname
    syscall_name[__NR_oldolduname] = "__NR_oldolduname";
#endif
#ifdef __NR_oldstat
    syscall_name[__NR_oldstat] = "__NR_oldstat";
#endif
#ifdef __NR_olduname
    syscall_name[__NR_olduname] = "__NR_olduname";
#endif
#ifdef __NR_open
    syscall_name[__NR_open] = "__NR_open";
#endif
#ifdef __NR_openat
    syscall_name[__NR_openat] = "__NR_openat";
#endif
#ifdef __NR_pause
    syscall_name[__NR_pause] = "__NR_pause";
#endif
#ifdef __NR_perf_counter_open
    syscall_name[__NR_perf_counter_open] = "__NR_perf_counter_open";
#endif
#ifdef __NR_personality
    syscall_name[__NR_personality] = "__NR_personality";
#endif
#ifdef __NR_pipe
    syscall_name[__NR_pipe] = "__NR_pipe";
#endif
#ifdef __NR_pipe2
    syscall_name[__NR_pipe2] = "__NR_pipe2";
#endif
#ifdef __NR_pivot_root
    syscall_name[__NR_pivot_root] = "__NR_pivot_root";
#endif
#ifdef __NR_poll
    syscall_name[__NR_poll] = "__NR_poll";
#endif
#ifdef __NR_ppoll
    syscall_name[__NR_ppoll] = "__NR_ppoll";
#endif
#ifdef __NR_prctl
    syscall_name[__NR_prctl] = "__NR_prctl";
#endif
#ifdef __NR_pread64
    syscall_name[__NR_pread64] = "__NR_pread64";
#endif
#ifdef __NR_preadv
    syscall_name[__NR_preadv] = "__NR_preadv";
#endif
#ifdef __NR_prof
    syscall_name[__NR_prof] = "__NR_prof";
#endif
#ifdef __NR_profil
    syscall_name[__NR_profil] = "__NR_profil";
#endif
#ifdef __NR_pselect6
    syscall_name[__NR_pselect6] = "__NR_pselect6";
#endif
#ifdef __NR_ptrace
    syscall_name[__NR_ptrace] = "__NR_ptrace";
#endif
#ifdef __NR_putpmsg
    syscall_name[__NR_putpmsg] = "__NR_putpmsg";
#endif
#ifdef __NR_pwrite64
    syscall_name[__NR_pwrite64] = "__NR_pwrite64";
#endif
#ifdef __NR_pwritev
    syscall_name[__NR_pwritev] = "__NR_pwritev";
#endif
#ifdef __NR_query_module
    syscall_name[__NR_query_module] = "__NR_query_module";
#endif
#ifdef __NR_quotactl
    syscall_name[__NR_quotactl] = "__NR_quotactl";
#endif
#ifdef __NR_read
    syscall_name[__NR_read] = "__NR_read";
#endif
#ifdef __NR_readahead
    syscall_name[__NR_readahead] = "__NR_readahead";
#endif
#ifdef __NR_readdir
    syscall_name[__NR_readdir] = "__NR_readdir";
#endif
#ifdef __NR_readlink
    syscall_name[__NR_readlink] = "__NR_readlink";
#endif
#ifdef __NR_readlinkat
    syscall_name[__NR_readlinkat] = "__NR_readlinkat";
#endif
#ifdef __NR_readv
    syscall_name[__NR_readv] = "__NR_readv";
#endif
#ifdef __NR_reboot
    syscall_name[__NR_reboot] = "__NR_reboot";
#endif
#ifdef __NR_recvfrom
    syscall_name[__NR_recvfrom] = "__NR_recvfrom";
#endif
#ifdef __NR_recvmsg
    syscall_name[__NR_recvmsg] = "__NR_recvmsg";
#endif
#ifdef __NR_remap_file_pages
    syscall_name[__NR_remap_file_pages] = "__NR_remap_file_pages";
#endif
#ifdef __NR_removexattr
    syscall_name[__NR_removexattr] = "__NR_removexattr";
#endif
#ifdef __NR_rename
    syscall_name[__NR_rename] = "__NR_rename";
#endif
#ifdef __NR_renameat
    syscall_name[__NR_renameat] = "__NR_renameat";
#endif
#ifdef __NR_request_key
    syscall_name[__NR_request_key] = "__NR_request_key";
#endif
#ifdef __NR_restart_syscall
    syscall_name[__NR_restart_syscall] = "__NR_restart_syscall";
#endif
#ifdef __NR_rmdir
    syscall_name[__NR_rmdir] = "__NR_rmdir";
#endif
#ifdef __NR_rt_sigaction
    syscall_name[__NR_rt_sigaction] = "__NR_rt_sigaction";
#endif
#ifdef __NR_rt_sigpending
    syscall_name[__NR_rt_sigpending] = "__NR_rt_sigpending";
#endif
#ifdef __NR_rt_sigprocmask
    syscall_name[__NR_rt_sigprocmask] = "__NR_rt_sigprocmask";
#endif
#ifdef __NR_rt_sigqueueinfo
    syscall_name[__NR_rt_sigqueueinfo] = "__NR_rt_sigqueueinfo";
#endif
#ifdef __NR_rt_sigreturn
    syscall_name[__NR_rt_sigreturn] = "__NR_rt_sigreturn";
#endif
#ifdef __NR_rt_sigsuspend
    syscall_name[__NR_rt_sigsuspend] = "__NR_rt_sigsuspend";
#endif
#ifdef __NR_rt_sigtimedwait
    syscall_name[__NR_rt_sigtimedwait] = "__NR_rt_sigtimedwait";
#endif
#ifdef __NR_rt_tgsigqueueinfo
    syscall_name[__NR_rt_tgsigqueueinfo] = "__NR_rt_tgsigqueueinfo";
#endif
#ifdef __NR_sched_getaffinity
    syscall_name[__NR_sched_getaffinity] = "__NR_sched_getaffinity";
#endif
#ifdef __NR_sched_getparam
    syscall_name[__NR_sched_getparam] = "__NR_sched_getparam";
#endif
#ifdef __NR_sched_get_priority_max
    syscall_name[__NR_sched_get_priority_max] = "__NR_sched_get_priority_max";
#endif
#ifdef __NR_sched_get_priority_min
    syscall_name[__NR_sched_get_priority_min] = "__NR_sched_get_priority_min";
#endif
#ifdef __NR_sched_getscheduler
    syscall_name[__NR_sched_getscheduler] = "__NR_sched_getscheduler";
#endif
#ifdef __NR_sched_rr_get_interval
    syscall_name[__NR_sched_rr_get_interval] = "__NR_sched_rr_get_interval";
#endif
#ifdef __NR_sched_setaffinity
    syscall_name[__NR_sched_setaffinity] = "__NR_sched_setaffinity";
#endif
#ifdef __NR_sched_setparam
    syscall_name[__NR_sched_setparam] = "__NR_sched_setparam";
#endif
#ifdef __NR_sched_setscheduler
    syscall_name[__NR_sched_setscheduler] = "__NR_sched_setscheduler";
#endif
#ifdef __NR_sched_yield
    syscall_name[__NR_sched_yield] = "__NR_sched_yield";
#endif
#ifdef __NR_security
    syscall_name[__NR_security] = "__NR_security";
#endif
#ifdef __NR_select
    syscall_name[__NR_select] = "__NR_select";
#endif
#ifdef __NR_semctl
    syscall_name[__NR_semctl] = "__NR_semctl";
#endif
#ifdef __NR_semget
    syscall_name[__NR_semget] = "__NR_semget";
#endif
#ifdef __NR_semop
    syscall_name[__NR_semop] = "__NR_semop";
#endif
#ifdef __NR_semtimedop
    syscall_name[__NR_semtimedop] = "__NR_semtimedop";
#endif
#ifdef __NR_sendfile
    syscall_name[__NR_sendfile] = "__NR_sendfile";
#endif
#ifdef __NR_sendfile64
    syscall_name[__NR_sendfile64] = "__NR_sendfile64";
#endif
#ifdef __NR_sendmsg
    syscall_name[__NR_sendmsg] = "__NR_sendmsg";
#endif
#ifdef __NR_sendto
    syscall_name[__NR_sendto] = "__NR_sendto";
#endif
#ifdef __NR_setdomainname
    syscall_name[__NR_setdomainname] = "__NR_setdomainname";
#endif
#ifdef __NR_setfsgid
    syscall_name[__NR_setfsgid] = "__NR_setfsgid";
#endif
#ifdef __NR_setfsgid32
    syscall_name[__NR_setfsgid32] = "__NR_setfsgid32";
#endif
#ifdef __NR_setfsuid
    syscall_name[__NR_setfsuid] = "__NR_setfsuid";
#endif
#ifdef __NR_setfsuid32
    syscall_name[__NR_setfsuid32] = "__NR_setfsuid32";
#endif
#ifdef __NR_setgid
    syscall_name[__NR_setgid] = "__NR_setgid";
#endif
#ifdef __NR_setgid32
    syscall_name[__NR_setgid32] = "__NR_setgid32";
#endif
#ifdef __NR_setgroups
    syscall_name[__NR_setgroups] = "__NR_setgroups";
#endif
#ifdef __NR_setgroups32
    syscall_name[__NR_setgroups32] = "__NR_setgroups32";
#endif
#ifdef __NR_sethostname
    syscall_name[__NR_sethostname] = "__NR_sethostname";
#endif
#ifdef __NR_setitimer
    syscall_name[__NR_setitimer] = "__NR_setitimer";
#endif
#ifdef __NR_set_mempolicy
    syscall_name[__NR_set_mempolicy] = "__NR_set_mempolicy";
#endif
#ifdef __NR_setpgid
    syscall_name[__NR_setpgid] = "__NR_setpgid";
#endif
#ifdef __NR_setpriority
    syscall_name[__NR_setpriority] = "__NR_setpriority";
#endif
#ifdef __NR_setregid
    syscall_name[__NR_setregid] = "__NR_setregid";
#endif
#ifdef __NR_setregid32
    syscall_name[__NR_setregid32] = "__NR_setregid32";
#endif
#ifdef __NR_setresgid
    syscall_name[__NR_setresgid] = "__NR_setresgid";
#endif
#ifdef __NR_setresgid32
    syscall_name[__NR_setresgid32] = "__NR_setresgid32";
#endif
#ifdef __NR_setresuid
    syscall_name[__NR_setresuid] = "__NR_setresuid";
#endif
#ifdef __NR_setresuid32
    syscall_name[__NR_setresuid32] = "__NR_setresuid32";
#endif
#ifdef __NR_setreuid
    syscall_name[__NR_setreuid] = "__NR_setreuid";
#endif
#ifdef __NR_setreuid32
    syscall_name[__NR_setreuid32] = "__NR_setreuid32";
#endif
#ifdef __NR_setrlimit
    syscall_name[__NR_setrlimit] = "__NR_setrlimit";
#endif
#ifdef __NR_set_robust_list
    syscall_name[__NR_set_robust_list] = "__NR_set_robust_list";
#endif
#ifdef __NR_setsid
    syscall_name[__NR_setsid] = "__NR_setsid";
#endif
#ifdef __NR_setsockopt
    syscall_name[__NR_setsockopt] = "__NR_setsockopt";
#endif
#ifdef __NR_set_thread_area
    syscall_name[__NR_set_thread_area] = "__NR_set_thread_area";
#endif
#ifdef __NR_set_tid_address
    syscall_name[__NR_set_tid_address] = "__NR_set_tid_address";
#endif
#ifdef __NR_settimeofday
    syscall_name[__NR_settimeofday] = "__NR_settimeofday";
#endif
#ifdef __NR_setuid
    syscall_name[__NR_setuid] = "__NR_setuid";
#endif
#ifdef __NR_setuid32
    syscall_name[__NR_setuid32] = "__NR_setuid32";
#endif
#ifdef __NR_setxattr
    syscall_name[__NR_setxattr] = "__NR_setxattr";
#endif
#ifdef __NR_sgetmask
    syscall_name[__NR_sgetmask] = "__NR_sgetmask";
#endif
#ifdef __NR_shmat
    syscall_name[__NR_shmat] = "__NR_shmat";
#endif
#ifdef __NR_shmctl
    syscall_name[__NR_shmctl] = "__NR_shmctl";
#endif
#ifdef __NR_shmdt
    syscall_name[__NR_shmdt] = "__NR_shmdt";
#endif
#ifdef __NR_shmget
    syscall_name[__NR_shmget] = "__NR_shmget";
#endif
#ifdef __NR_shutdown
    syscall_name[__NR_shutdown] = "__NR_shutdown";
#endif
#ifdef __NR_sigaction
    syscall_name[__NR_sigaction] = "__NR_sigaction";
#endif
#ifdef __NR_sigaltstack
    syscall_name[__NR_sigaltstack] = "__NR_sigaltstack";
#endif
#ifdef __NR_signal
    syscall_name[__NR_signal] = "__NR_signal";
#endif
#ifdef __NR_signalfd
    syscall_name[__NR_signalfd] = "__NR_signalfd";
#endif
#ifdef __NR_signalfd4
    syscall_name[__NR_signalfd4] = "__NR_signalfd4";
#endif
#ifdef __NR_sigpending
    syscall_name[__NR_sigpending] = "__NR_sigpending";
#endif
#ifdef __NR_sigprocmask
    syscall_name[__NR_sigprocmask] = "__NR_sigprocmask";
#endif
#ifdef __NR_sigreturn
    syscall_name[__NR_sigreturn] = "__NR_sigreturn";
#endif
#ifdef __NR_sigsuspend
    syscall_name[__NR_sigsuspend] = "__NR_sigsuspend";
#endif
#ifdef __NR_socket
    syscall_name[__NR_socket] = "__NR_socket";
#endif
#ifdef __NR_socketcall
    syscall_name[__NR_socketcall] = "__NR_socketcall";
#endif
#ifdef __NR_socketpair
    syscall_name[__NR_socketpair] = "__NR_socketpair";
#endif
#ifdef __NR_splice
    syscall_name[__NR_splice] = "__NR_splice";
#endif
#ifdef __NR_ssetmask
    syscall_name[__NR_ssetmask] = "__NR_ssetmask";
#endif
#ifdef __NR_stat
    syscall_name[__NR_stat] = "__NR_stat";
#endif
#ifdef __NR_stat64
    syscall_name[__NR_stat64] = "__NR_stat64";
#endif
#ifdef __NR_statfs
    syscall_name[__NR_statfs] = "__NR_statfs";
#endif
#ifdef __NR_statfs64
    syscall_name[__NR_statfs64] = "__NR_statfs64";
#endif
#ifdef __NR_stime
    syscall_name[__NR_stime] = "__NR_stime";
#endif
#ifdef __NR_stty
    syscall_name[__NR_stty] = "__NR_stty";
#endif
#ifdef __NR_swapoff
    syscall_name[__NR_swapoff] = "__NR_swapoff";
#endif
#ifdef __NR_swapon
    syscall_name[__NR_swapon] = "__NR_swapon";
#endif
#ifdef __NR_symlink
    syscall_name[__NR_symlink] = "__NR_symlink";
#endif
#ifdef __NR_symlinkat
    syscall_name[__NR_symlinkat] = "__NR_symlinkat";
#endif
#ifdef __NR_sync
    syscall_name[__NR_sync] = "__NR_sync";
#endif
#ifdef __NR_sync_file_range
    syscall_name[__NR_sync_file_range] = "__NR_sync_file_range";
#endif
#ifdef __NR__sysctl
    syscall_name[__NR__sysctl] = "__NR__sysctl";
#endif
#ifdef __NR_sysfs
    syscall_name[__NR_sysfs] = "__NR_sysfs";
#endif
#ifdef __NR_sysinfo
    syscall_name[__NR_sysinfo] = "__NR_sysinfo";
#endif
#ifdef __NR_syslog
    syscall_name[__NR_syslog] = "__NR_syslog";
#endif
#ifdef __NR_tee
    syscall_name[__NR_tee] = "__NR_tee";
#endif
#ifdef __NR_tgkill
    syscall_name[__NR_tgkill] = "__NR_tgkill";
#endif
#ifdef __NR_time
    syscall_name[__NR_time] = "__NR_time";
#endif
#ifdef __NR_timer_create
    syscall_name[__NR_timer_create] = "__NR_timer_create";
#endif
#ifdef __NR_timer_delete
    syscall_name[__NR_timer_delete] = "__NR_timer_delete";
#endif
#ifdef __NR_timerfd_create
    syscall_name[__NR_timerfd_create] = "__NR_timerfd_create";
#endif
#ifdef __NR_timerfd_gettime
    syscall_name[__NR_timerfd_gettime] = "__NR_timerfd_gettime";
#endif
#ifdef __NR_timerfd_settime
    syscall_name[__NR_timerfd_settime] = "__NR_timerfd_settime";
#endif
#ifdef __NR_timer_getoverrun
    syscall_name[__NR_timer_getoverrun] = "__NR_timer_getoverrun";
#endif
#ifdef __NR_timer_gettime
    syscall_name[__NR_timer_gettime] = "__NR_timer_gettime";
#endif
#ifdef __NR_timer_settime
    syscall_name[__NR_timer_settime] = "__NR_timer_settime";
#endif
#ifdef __NR_times
    syscall_name[__NR_times] = "__NR_times";
#endif
#ifdef __NR_tkill
    syscall_name[__NR_tkill] = "__NR_tkill";
#endif
#ifdef __NR_truncate
    syscall_name[__NR_truncate] = "__NR_truncate";
#endif
#ifdef __NR_truncate64
    syscall_name[__NR_truncate64] = "__NR_truncate64";
#endif
#ifdef __NR_tuxcall
    syscall_name[__NR_tuxcall] = "__NR_tuxcall";
#endif
#ifdef __NR_ugetrlimit
    syscall_name[__NR_ugetrlimit] = "__NR_ugetrlimit";
#endif
#ifdef __NR_ulimit
    syscall_name[__NR_ulimit] = "__NR_ulimit";
#endif
#ifdef __NR_umask
    syscall_name[__NR_umask] = "__NR_umask";
#endif
#ifdef __NR_umount
    syscall_name[__NR_umount] = "__NR_umount";
#endif
#ifdef __NR_umount2
    syscall_name[__NR_umount2] = "__NR_umount2";
#endif
#ifdef __NR_uname
    syscall_name[__NR_uname] = "__NR_uname";
#endif
#ifdef __NR_unlink
    syscall_name[__NR_unlink] = "__NR_unlink";
#endif
#ifdef __NR_unlinkat
    syscall_name[__NR_unlinkat] = "__NR_unlinkat";
#endif
#ifdef __NR_unshare
    syscall_name[__NR_unshare] = "__NR_unshare";
#endif
#ifdef __NR_uselib
    syscall_name[__NR_uselib] = "__NR_uselib";
#endif
#ifdef __NR_ustat
    syscall_name[__NR_ustat] = "__NR_ustat";
#endif
#ifdef __NR_utime
    syscall_name[__NR_utime] = "__NR_utime";
#endif
#ifdef __NR_utimensat
    syscall_name[__NR_utimensat] = "__NR_utimensat";
#endif
#ifdef __NR_utimes
    syscall_name[__NR_utimes] = "__NR_utimes";
#endif
#ifdef __NR_vfork
    syscall_name[__NR_vfork] = "__NR_vfork";
#endif
#ifdef __NR_vhangup
    syscall_name[__NR_vhangup] = "__NR_vhangup";
#endif
#ifdef __NR_vm86
    syscall_name[__NR_vm86] = "__NR_vm86";
#endif
#ifdef __NR_vm86old
    syscall_name[__NR_vm86old] = "__NR_vm86old";
#endif
#ifdef __NR_vmsplice
    syscall_name[__NR_vmsplice] = "__NR_vmsplice";
#endif
#ifdef __NR_vserver
    syscall_name[__NR_vserver] = "__NR_vserver";
#endif
#ifdef __NR_wait4
    syscall_name[__NR_wait4] = "__NR_wait4";
#endif
#ifdef __NR_waitid
    syscall_name[__NR_waitid] = "__NR_waitid";
#endif
#ifdef __NR_waitpid
    syscall_name[__NR_waitpid] = "__NR_waitpid";
#endif
#ifdef __NR_write
    syscall_name[__NR_write] = "__NR_write";
#endif
#ifdef __NR_writev
    syscall_name[__NR_writev] = "__NR_writev";
#endif
    return 0;
}

int t = init();

}

#endif // __DISABLED_SYSCALL_H__
