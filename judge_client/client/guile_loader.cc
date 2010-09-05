#include <cstdio>
#include <cstdlib>
#include <sys/resource.h>
#include <libguile.h>
#include "util.h"

static void init_sandbox(const char* memory_limit_string) {
    int memory = ReadMemoryConsumption(getpid());
    int memory_limit = 0;
    sscanf(memory_limit_string, "%d", &memory_limit);
    memory_limit = (memory_limit + memory) * 1024;

    SetLimit(RLIMIT_DATA, memory_limit);
    SetLimit(RLIMIT_AS, memory_limit + 10 * 1024 * 1024);
}

static void scm_main(void* closure, int argc, char** argv) {
    const char file_name[] = "p.scm";
    if (argc < 1)
        exit(1);
    init_sandbox(argv[0]);
    scm_c_primitive_load(file_name);
}

int main(int argc, char* argv[]) {
    scm_boot_guile(argc, argv, scm_main, NULL);
}
