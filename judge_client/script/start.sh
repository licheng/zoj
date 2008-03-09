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

if [[ $0 = */* ]]; then
    cd ${0%/*}
fi

if [ `which unzip` == "" ]; then
    echo "No unzip found!"
    exit 1
fi

if [ `which gcc` != "" ]; then
    supported_source_file_types=$supported_source_file_types,c
fi

if [ `which g++` != "" ]; then
    supported_source_file_types=$supported_source_file_types,cc
fi

if [ `which fpc` != "" ]; then
    supported_source_file_types=$supported_source_file_types,pas
fi

# Prepare /script
CreateDir script 750
chmod +x script/*

# Prepare /prob
CreateDir prob 750
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

# Prepare /working
CreateDir working 750

# Prepare /log
CreateDir log 750

cmd="./judge_client --lang=$supported_source_file_types $*"
echo $cmd
eval $cmd
