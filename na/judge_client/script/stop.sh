#!/bin/bash
root=`readlink /proc/$$/fd/255`
root=${root%/*}
pkill -TERM -P `cat $root/judge.pid` judged 
kill -TERM `cat $root/judge.pid`
