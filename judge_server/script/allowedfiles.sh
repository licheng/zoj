#!/bin/bash
cmd=/tmp/test
case $1 in
    c)
        src="#include <stdio.h>\n#include <stdlib.h>\n#include <string.h>\n int main(){ char s[100];memset(s, 0, sizeof(s));printf(\"Hello world!\");}"
        ;;
    cc)
        src="#include <iostream>\n#include <cmath>\n#include <cstdlib>\n#include <ctime>\nusing namespace std; int main(){ char s[100];memset(s, 0, sizeof(s));cout<<\"Hello world!\";}"
        ;;
    pas)
        src="begin\nWriteln('Hello world');\nend."
        ;;
    java)
        src="public class test{public static void main(String[] args){System.out.println(\"Hello world\");}}"
        ;;
    py)
        cmd="/usr/bin/python /tmp/test.py"
        src="print 'Hello world'"
        ;;
    *) exit -1;;
esac
echo -e $src >/tmp/test.$1
${0%/*}/compile /tmp/test.$1 >/dev/null 2>/dev/null
/usr/bin/strace $cmd 2>&1 | grep ^open | sed -ne '/^open(/{s/.*"\([^"]*\)".*/\1/g p}'
rm -f /tmp/test /tmp/test.*
echo /proc/meminfo
