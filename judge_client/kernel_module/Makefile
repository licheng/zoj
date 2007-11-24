obj-m += kmmon.o

default: all

all: kmmon.ko libkmmon.a

clean:
	@rm -f *.o *.a *.ko Module.symvers

kmmon.ko: kmmon.c kmmon.h syscall.h
	@make -C /lib/modules/$(shell uname -r)/build V=1 M=$(CURDIR) modules

kmmon-lib.o: kmmon-lib.c kmmon-lib.h kmmon.h
	@gcc -O2 -c kmmon-lib.c -o kmmon-lib.o

libkmmon.a: kmmon-lib.o
	@ar crv libkmmon.a kmmon-lib.o

