CC=$(CXX)
CXXFLAGS=-Wall -Werror -Wno-sign-compare -ansi -O3 -MD -fPIC

.PHONY: clean

libzunit.so: zunit.o
	$(CXX) -shared -Wl,-soname,libzunit.so -o libzunit.so zunit.o

zunit.o: zunit.h

clean:
	rm -f *.so *.o *.d

