#!/bin/sh

export ROOT="/the_chroot/"

[ -e "$ROOT/proc/meminfo" ] || mount --bind /proc "$ROOT/proc/"
chroot $ROOT /zoj/start.sh 127.0.0.1:8301

