#!/bin/bash
if [[ $1 = */* ]]; then
    cd ${1%/*}
fi
src=${1##*/}
bin=${src%.*}
err=$bin.err
case ${src##*.} in
    c)
        /usr/bin/gcc -o $bin -ansi -fno-asm -O2 -Wall -lm --static -s -DONLINE_JUDGE $src 2>$err
        ;;
    cc)
        /usr/bin/g++ -o $bin -ansi -fno-asm -O2 -Wall -lm --static -s -DONLINE_JUDGE $src 2>$err
        ;;
    pas)
        /usr/bin/fpc -o$bin -Fe$err -Sd -dONLINE_JUDGE -O2 -Op2 $src >/dev/null 2>/dev/null
        if [ -f $bin ]; then
            exit 0
        fi
        exit 1
        ;;
    java)
        /usr/bin/gcj -o $bin -fno-asm -O2 -lm --static -s -DONLINE_JUDGE --main=$bin $src 2>$err
        ;;
    py)
        exit 0
        ;;
    *) exit -1;;
esac
