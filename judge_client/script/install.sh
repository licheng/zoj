#!/bin/bash
set -eu

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

read -p "Input the directory to install ZOJ judge client [/zoj]:" dir
if [ "$dir" == "" ]; then
    dir="/zoj"
fi
CreateDir "$dir" 755
cp script/start.sh "$dir"
cp script/stop.sh "$dir"
cp script/rotate_log.sh "$dir"
CreateDir "$dir/script" 755
cp script/compile.sh "$dir"/script
cp client/judged "$dir"
cp kernel_module/kmmon.ko "$dir"
chmod +x "$dir"/*.sh
chmod +x "$dir"/script/compile.sh
if [[ "`cat /etc/group | grep '^zoj:'`" == "" ]]; then
    groupadd zoj
fi
if [[ "`cat /etc/passwd | grep '^zoj:'`" == "" ]]; then
    useradd -g zoj zoj
fi
read -p "Create a symlink to rotate_log.sh in /etc/cron.daily? [y/n]" choice
if [ "$choice" == "y" ] || [ "$choice" == "" ]; then
    ln -f -s "$dir"/rotate_log.sh /etc/cron.daily/rotate_zoj_log
else
    echo "Remember to put rotate_log.sh in your cron jobs otherwise the log file will be too large"
fi
