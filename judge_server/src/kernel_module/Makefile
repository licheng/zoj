obj-m += kmmon.o

all : kmmon.ko libkmmon.a

clean :
	rm *.o *.a *.ko

kmmon.ko: kmmon.c kmmon.h syscall.h
	make -C /lib/modules/$(shell uname -r)/build V=1 M=$(PWD) modules

kmmon-lib.o: kmmon-lib.c kmmon-lib.h kmmon.h
	gcc -O2 -c kmmon-lib.c -o kmmon-lib.o

libkmmon.a: kmmon-lib.o
	ar crv libkmmon.a kmmon-lib.o

