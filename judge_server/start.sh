#!/bin/bash
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

supported_source_file_types=

if [ -f /usr/bin/gcc ]; then
    supported_source_file_types=$supported_source_file_types,c
fi

if [ -f /usr/bin/g++ ]; then
    supported_source_file_types=$supported_source_file_types,cc
fi

if [ -f /usr/bin/fpc ]; then
    supported_source_file_types=$supported_source_file_types,pas
fi

if [ -f /usr/bin/gcj ] ; then
    supported_source_file_types=$supported_source_file_types,java
fi

if [ -f /usr/bin/python ] ; then
    supported_source_file_types=$supported_source_file_types,py
fi

if [ ! -f /usr/bin/unzip ]; then
    echo '/usr/bin/unzip does not exist'
    exit 1
fi

if [ ! -f /usr/bin/strace ]; then
    echo '/usr/bin/strace does not exist'
    exit 1
fi

if [[ $0 = */* ]]; then
    cd ${0%/*}
fi

CreateDir prob 750
CreateDir script 750
CreateDir working 777
chmod +x script/*
CreateDir prob/0 750
CreateDir prob/0/0 750
if [ ! -e prob/0/current ]; then
    ln -s 0 prob/0/current
    chmod 750 prob/0/current
fi
if [ ! -f prob/0/current/input.0 ]; then
    echo -e "0 0\n1 2\n2 3" >prob/0/current/input.0
fi
if [ ! -f prob/0/current/output.0 ]; then
    echo -e "0\n3\n5" >prob/0/current/output.0
fi
chmod 750 prob/0/current/*
cmd="bin/judge --lang=$supported_source_file_types $*"
echo $cmd
eval $cmd
