CC_SOURCES:=$(filter-out ce.cc ce_long_error.cc,$(wildcard *.cc))
CC_BINARIES:=$(patsubst %.cc,%,$(CC_SOURCES))
JAVA_SOURCES:=$(filter-out P.java $(wildcard ce*.java),$(wildcard *.java))
JAVA_CLASSES:=$(patsubst %.java,%.class,$(JAVA_SOURCES))

.PHONY: all clean

all: $(CC_BINARIES) $(JAVA_CLASSES)

clean: 
	rm -f $(CC_BINARIES) $(JAVA_CLASSES)

$(CC_BINARIES): % : %.cc
	g++ -o $@ -ansi -fno-asm -O2 -Wall -lm --static -s -DONLINE_JUDGE $^

$(JAVA_CLASSES): %.class : %.java
	cp -f $^ P.java
	javac P.java
	rm -f P.java
	mv P.class $@

ac_multiple_classes.class: ac_multiple_classes.java
	cp -f ac_multiple_classes.java P.java
	javac P.java
	rm -f P.java
	mv P.class ac_multiple_classes.class
	mv 'P$$1.class' 'ac_multiple_classes$$1.class'

tle_multiple_classes_static_initializer.class: tle_multiple_classes_static_initializer.java
	cp -f tle_multiple_classes_static_initializer.java P.java
	javac P.java
	rm -f P.java
	mv P.class tle_multiple_classes_static_initializer.class
	mv 'T.class' 'tle_multiple_classes_static_initializer_T.class'
