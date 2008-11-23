CC=$(CXX)
CXXFLAGS=-I ../kernel_module -L ../kernel_module -Wall -Werror -Wno-sign-compare -ansi -O3 -MD
VPATH=../kernel_module
ALL_SOURCES:=$(wildcard *.cc)
UNITTEST_SOURCES:=$(wildcard *_unittest.cc)
SOURCES=$(filter-out $(UNITTEST_SOURCES),$(ALL_SOURCES))
ALL_OBJS:=$(patsubst %.cc,%.o,$(ALL_SOURCES))
OBJS:=$(patsubst %.cc,%.o,$(SOURCES))
UNITTESTS:=$(patsubst %.cc,%,$(UNITTEST_SOURCES))
UNITTEST_RUNS:=$(patsubst %.cc,%.run,$(UNITTEST_SOURCES))

.PHONY: all clean rebuild $(UNITTEST_RUNS) test testdata

all: judged

clean:
	rm -f $(ALL_OBJS) $(ALL_DEPS) judged $(UNITTESTS)
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

$(UNITTESTS): %_unittest : %.o %_unittest.o global.o -lcppunit args.o common_io.o logging.o strutil.o
	$(CXX) $(CXXFLAGS) -o $@ $^

text_checker_unittest: text_file_reader.o checker.o
special_checker_unittest: checker.o trace.o util.o -lkmmon
native_runner_unittest: runner.o trace.o util.o -lkmmon
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


