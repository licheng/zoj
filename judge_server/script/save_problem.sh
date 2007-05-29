#!/bin/bash
set -u
set -e
PROB="$1"
VER="$2"
ZIP="/prob/$PROB_$VER.zip"
if ! [ -e "/prob/$PROB" ]; then
    mkdir -m 750 "/prob/$PROB"
fi
PATH="/prob/$PROB/$VER"
mkdir -m 755 "$PATH"
unzip -d "$PATH" "$ZIP"
for i in "$PATH/judge.*"; do
    compile "$i"
    break
done
ln -s "input.0" "$PATH/input"
ln -s "output.0" "$PATH/output"
LINK="/prob/$PROB/current"
if ! [-e $LINK ]; then
    ln -s "$VER" "$LINK"
fi
