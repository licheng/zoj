#!/bin/bash
if [[ $1 = */* ]]; then
    cd ${1%/*}
fi
src=${1##*/}
bin=${src%.*}
err=$bin.err
case ${src##*.} in
    c)
        gcc -o $bin -ansi -fno-asm -O2 -Wall -lm --static -s -DONLINE_JUDGE $src >/dev/null
        ;;
    cc)
        g++ -o $bin -ansi -fno-asm -O2 -Wall -lm --static -s -DONLINE_JUDGE $src >/dev/null
        ;;
    pas)
        fpc -o$bin -Fe"/proc/self/fd/2" -Sd -dONLINE_JUDGE -O2 -Op2 $src >/dev/null
        if [ -f $bin ]; then
            exit 0
        fi
        exit 1
        ;;
    *) exit -1;;
esac
