#!/bin/bash
set -e
function Link() {
  p=`which $1`
  while [ -L "$p" ]; do
      p=`readlink -f "$p"`
  done
  if [ $p != "" ]; then
      ln "$p" "bin/$1"
  fi
}

# Prepare /bin
mkdir -m 750 bin
Link unzip
Link rm
Link sh
Link gcc
Link g++
Link fpc

# Prepare /etc
mkdir -m 755 etc
sed -n -e '/^zoj:/p' -e '/^root:/p' /etc/passwd > etc/passwd
rm -f etc/group
for i in $(awk -F ':' '{print $4}' etc/passwd); do
    awk -F ':' '{if($3=='$i')print $0}' /etc/group >> etc/group
done

# Prepare /dev
mkdir -m 755 dev
mknod -m 666 dev/null c 1 3

# Prepare /proc
mkdir -m 755 proc
mount -t proc /proc proc
chroot .
