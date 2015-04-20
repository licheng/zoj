#!/bin/sh

export ROOT="/home/client/"

[ -e "$ROOT/proc/meminfo" ] || mount --bind /proc "$ROOT/proc/"
chroot $ROOT /zoj/start.sh 127.0.0.1:8301

