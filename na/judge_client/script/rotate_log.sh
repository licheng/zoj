#!/bin/bash
set -euv
root=`readlink /proc/$$/fd/255`
root=${root%/*}
cd "$root"/log
target_dir=backup/`date +%Y-%m`
target_name=`date +%d.log`
mkdir -p $target_dir
mv judge.log $target_name
if kill -USR1 `cat ../judge.pid`; then echo; fi
mv $target_name $target_dir/
gzip $target_dir/$target_name &
