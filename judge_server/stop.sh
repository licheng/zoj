#!/bin/bash
if [[ $0 = */* ]]; then
    cd ${0%/*}
fi

kill -s SIGTERM `cat judge.pid`
