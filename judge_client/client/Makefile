CC=$(CXX)
CXXFLAGS=-I ../kernel_module -L ../kernel_module -Wall -Werror -Wno-sign-compare -ansi -O3 -MD
VPATH=../kernel_module
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
	rm -f $(ALL_OBJS) $(ALL_DEPS) judged *.jar $(UNITTESTS) *.so Sandbox.h *.class
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

judged: $(OBJS) -lkmmon
	$(CXX) $(CXXFLAGS) -o $@ $^

JavaSandbox.jar: Sandbox.java SandboxClassLoader.java SandboxSecurityManager.java ../../judge_server/src/main/cn/edu/zju/acm/onlinejudge/bean/enumeration/JudgeReply.java
	DIR=`mktemp -d` && \
	javac -sourcepath .:../../judge_server/src/main -d $$DIR ../../judge_server/src/main/cn/edu/zju/acm/onlinejudge/bean/enumeration/JudgeReply.java Sandbox.java SandboxClassLoader.java SandboxSecurityManager.java && \
	javah -classpath $$DIR -jni -force Sandbox && \
	CWD=`pwd` && \
	cd $$DIR && \
	jar cfe $$CWD/JavaSandbox.jar Sandbox * && \
	rm -rf $$DIR

Sandbox.h: JavaSandbox.jar

libsandbox.so: Sandbox.h Sandbox.cc args.o logging.o util.o runner.o strutil.o common_io.o global.o
	$(CXX) $(CXXFLAGS) -o libsandbox.so -shared -Wl,-soname,libsandbox.so -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux $^ -static -lc -lkmmon

$(UNITTESTS): %_unittest : %.o %_unittest.o global.o -lcppunit args.o common_io.o logging.o strutil.o
	$(CXX) $(CXXFLAGS) -o $@ $(filter-out libsandbox.so JavaSandbox.jar, $^)

text_checker_unittest: text_file_reader.o checker.o
special_checker_unittest: checker.o trace.o util.o -lkmmon
native_runner_unittest: runner.o trace.o util.o -lkmmon
java_runner_unittest: runner.o libsandbox.so JavaSandbox.jar trace.o util.o net_util.o -lkmmon
compiler_unittest: environment.o trace.o util.o -lkmmon
judge_unittest: compiler.o runner.o checker.o native_runner.o text_checker.o special_checker.o environment.o util.o trace.o text_file_reader.o -lkmmon
control_unittest: compiler.o environment.o trace.o util.o -lkmmon
util_unittest: -lkmmon
main_unittest: $(filter-out main.o main_stub.o,$(OBJS))

testdata:
	make -C testdata all

$(UNITTEST_RUNS): %.run : % testdata
	@echo -n "Running $<: "
	@./$<
	@echo "******************************************************************************"
	@echo


