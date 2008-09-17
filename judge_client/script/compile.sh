#!/bin/bash
if [[ $2 = */* ]]; then
    cd ${2%/*}
fi
LANG=en_US
src=${2##*/}
bin=${src%.*}
case $1 in
    gcc)
        gcc -o $bin -ansi -fno-asm -O2 -Wall -lm --static -s -DONLINE_JUDGE $src >/dev/null
        ;;
    g++)
        g++ -o $bin -ansi -fno-asm -O2 -Wall -lm --static -s -DONLINE_JUDGE $src >/dev/null
        ;;
    fpc)
        fpc -o$bin -Fe"/proc/self/fd/2" -Sd -dONLINE_JUDGE -O2 -Op2 $src >/dev/null
        if [ -f $bin ]; then
            exit 0
        fi
        exit 1
        ;;
    *) exit -1;;
esac
