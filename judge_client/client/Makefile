CC=$(CXX)
CXXFLAGS=-I ../zunit -Wall -Werror -Wno-sign-compare -ansi -O3 -MD
ALL_SOURCES:=$(filter-out Sandbox.cc, $(wildcard *.cc))
UNITTEST_SOURCES:=$(wildcard *_unittest.cc)
SOURCES=$(filter-out $(UNITTEST_SOURCES),$(ALL_SOURCES))
ALL_OBJS:=$(patsubst %.cc,%.o,$(ALL_SOURCES))
OBJS:=$(patsubst %.cc,%.o,$(SOURCES))
UNITTESTS:=$(patsubst %.cc,%,$(UNITTEST_SOURCES))
UNITTEST_RUNS:=$(patsubst %.cc,%.run,$(UNITTEST_SOURCES))

.PHONY: all clean rebuild $(UNITTEST_RUNS) test testdata

all: judged JavaSandbox.jar Sandbox.h libsandbox.so

clean:
	rm -f *.o *.d judged *.jar $(UNITTESTS) *.so Sandbox.h *.class
	@make -C testdata clean

rebuild: clean all

test: $(UNITTEST_RUNS)

ALL_DEPS:=$(patsubst %.cc,%.d,$(ALL_SOURCES))
MISSING_DEPS:=$(filter-out $(wildcard $(ALL_DEPS)),$(ALL_DEPS))
ifneq ($(MISSING_DEPS),)
$(MISSING_DEPS) :
	@rm -f $(patsubst %.d,%.o,$@)
endif

-include $(ALL_DEPS)

judged: $(OBJS)
	$(CXX) $(CXXFLAGS) -o $@ $^

JavaSandbox.jar: Sandbox.java SandboxClassLoader.java SandboxSecurityManager.java ../../judge_server/src/main/cn/edu/zju/acm/onlinejudge/bean/enumeration/JudgeReply.java
	DIR=`mktemp -d` && \
	javac -sourcepath .:../../judge_server/src/main -d $$DIR ../../judge_server/src/main/cn/edu/zju/acm/onlinejudge/bean/enumeration/JudgeReply.java Sandbox.java SandboxClassLoader.java SandboxSecurityManager.java && \
	javah -classpath $$DIR -jni -force Sandbox && \
	CWD=`pwd` && \
	cd $$DIR && \
	jar cfe $$CWD/JavaSandbox.jar Sandbox * && \
	rm -rf $$DIR

CustomJavaCompiler.class: CustomJavaCompiler.java
	javac CustomJavaCompiler.java

Sandbox.h: JavaSandbox.jar

libsandbox.so: Sandbox.h Sandbox.cc args.o logging.o util.o runner.o strutil.o common_io.o global.o
	JAVA_HOME=`which javac`; \
	while [ -L "$$JAVA_HOME" ]; do JAVA_HOME=`readlink $$JAVA_HOME`; done; \
	JAVA_HOME=$${JAVA_HOME%/bin/javac}; \
	$(CXX) $(CXXFLAGS) -o libsandbox.so -shared -Wl,-soname,libsandbox.so -I$$JAVA_HOME/include -I$$JAVA_HOME/include/linux $^ -fPIC -lc

$(UNITTESTS): %_unittest : %.o %_unittest.o global.o args.o common_io.o logging.o strutil.o
	$(CXX) $(CXXFLAGS) -L ../zunit -lzunit -DDEBUG -o $@ $(filter-out libsandbox.so JavaSandbox.jar CustomJavaCompiler.class, $^)

text_checker_unittest: text_file_reader.o checker.o
special_checker_unittest: checker.o util.o tracer.o
native_runner_unittest: runner.o util.o tracer.o
java_runner_unittest: runner.o libsandbox.so JavaSandbox.jar tracer.o util.o net_util.o
compiler_unittest: environment.o util.o CustomJavaCompiler.class
judge_unittest: compiler.o runner.o checker.o java_runner.o native_runner.o text_checker.o special_checker.o environment.o util.o tracer.o text_file_reader.o libsandbox.so JavaSandbox.jar net_util.o
control_unittest: compiler.o environment.o tracer.o util.o
util_unittest:
main_unittest: $(filter-out main.o main_stub.o,$(OBJS))

testdata:
	make -C testdata all

$(UNITTEST_RUNS): %.run : % testdata
	@echo -n "Running $<: "
	@env LD_LIBRARY_PATH="$$LD_LIBRARY_PATH:../zunit" ./$<
	@echo "******************************************************************************"
	@echo


