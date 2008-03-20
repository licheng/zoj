CC_SOURCES:=$(filter-out ce.cc,$(wildcard *.cc))
CC_BINARIES:=$(patsubst %.cc,%,$(CC_SOURCES))

.PHONY: all clean

all: $(CC_BINARIES)

clean: 
	rm -f $(CC_BINARIES)

$(CC_BINARIES): % : %.cc
	g++ -o $@ -ansi -fno-asm -O2 -Wall -lm --static -s -DONLINE_JUDGE $^
