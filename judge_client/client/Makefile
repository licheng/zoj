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

$(UNITTESTS): %_unittest : %.o %_unittest.o global.o -lcppunit
	$(CXX) $(CXXFLAGS) -o $@ $^

util_unittest: args.o util.o trace.o logging.o -lkmmon
command_reader_unittest: args.o util.o trace.o logging.o -lkmmon
compile_unittest: args.o util.o trace.o logging.o -lkmmon
run_unittest: args.o util.o trace.o logging.o -lkmmon
check_unittest: args.o util.o trace.o logging.o -lkmmon
judge_unittest: args.o util.o trace.o logging.o compile.o run.o check.o -lkmmon
control_unittest: args.o util.o trace.o logging.o compile.o run.o check.o -lkmmon
main_unittest: $(filter-out main.o main_stub.o,$(OBJS)) -lkmmon

testdata:
	make -C testdata all

$(UNITTEST_RUNS): %.run : % testdata
	@echo -n "Running $<: "
	@-./$<
	@echo "******************************************************************************"
	@echo


