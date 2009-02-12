#!/bin/bash
if [[ $2 = */* ]]; then
    cd ${2%/*}
fi
LANG=en_US
src=${2##*/}
bin=${src%.*}
main=main.${src##*.}
if ! [[ -f $main ]]; then
    main=
fi
case $1 in
    gcc)
        gcc -o $bin -ansi -fno-asm -O2 -Wall --static -s -DONLINE_JUDGE $src $main -lm >/dev/null
        ;;
    g++)
        g++ -o $bin -ansi -fno-asm -O2 -Wall -lm --static -s -DONLINE_JUDGE $src $main >/dev/null
        ;;
    fpc)
        if ! [[ $main = "" ]]; then
            temp=`mktemp`
            cat $src $main > $temp
            mv $temp $src
        fi
        fpc -o$bin -Fe"/proc/self/fd/2" -Sd -dONLINE_JUDGE -O2 -Op2 $src >/dev/null
        if [[ -f $bin ]]; then
            exit 0
        fi
        exit 1
        ;;
    javac)
        root=`ps -o cmd $$ | tail -n 1`
        root=${root#* }
        root=${root%/script/*}
        java_home=`which javac`
        while [ -L "$java_home" ]; do
            java_home=`readlink $java_home`
        done
        java_home=${java_home%/javac}
        if ! $java_home/java -cp $root CustomJavaCompiler $src >/dev/null ; then
            exit 1
        fi
        if ! [[ -f Main.class ]]; then
            echo "No public class Main found" 1>&2
            exit 1
        fi
        exit 0
        ;;
    *) exit -1;;
esac
