#!/bin/bash
set -e

function CreateDir() {
    if [ -e "$1" ]; then
        if ! [ -d "$1" ]; then
            echo "$1" exists but is not a directory
            exit 1
        fi
    else
        mkdir "$1"
    fi
    chmod $2 "$1"
}

if [ "$1" == "" ]; then
    echo "Usage: start.sh <server address>:<server port>"
    exit 1
fi

address=${1%:*}
port=${1##*:}

root=`readlink /proc/$$/fd/255`
root=${root%/*}
cd "$root"

if [ "`which unzip`" == "" ]; then
    echo "No unzip found!"
    exit 1
fi

if [ "`which gcc`" != "" ]; then
    supported_source_file_types=$supported_source_file_types,c
fi

if [ "`which g++`" != "" ]; then
    supported_source_file_types=$supported_source_file_types,cc
fi

if [ "`which fpc`" != "" ]; then
    supported_source_file_types=$supported_source_file_types,pas
fi

# Prepare /prob
CreateDir prob 750
CreateDir prob/0 750
CreateDir prob/0/0 750
if [ ! -f prob/0/0/1.in ]; then
    echo -e "0 0\n1 2\n2 3" >prob/0/0/1.in
fi
if [ ! -f prob/0/0/1.out ]; then
    echo -e "0\n3\n5" >prob/0/0/1.out
fi
chmod 750 prob/0/*

# Prepare /working
CreateDir working 750

# Prepare /log
CreateDir log 750

if [ "`lsmod | grep kmmon`" == "" ]; then
    insmod kmmon.ko
fi

ids=`cat /etc/passwd | grep zoj | awk -F ':' '{print "--uid=" $3, "--gid=" $4}'`
cmd="./judge_client --lang=\"$supported_source_file_types\" --daemonize --root=. --queue_address=\"$address\" --queue_port=$port $ids"
echo $cmd
eval $cmd
