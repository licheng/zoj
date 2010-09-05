#!/usr/bin/env python

import sys, os, resource, signal, runpy

sys.path = ['.'] + sys.path

def get_memory_status() :
    args = { 'VmSize' : 0, 'VmExe' : 0, 'VmLib': 0, 'VmStk' : 0 }
    fin = open('/proc/self/status', 'r')
    for i in fin :
        if not i.startswith('Vm') : continue
        arg = [ j.strip() for j in i.split(':') ]
        args[arg[0]] = int(arg[1].partition(' ')[0])
    fin.close()
    return args

memory_limit = int(sys.argv[1])
module_name = sys.argv[2].partition('.')[0]

memory_status = get_memory_status()
if 'VmPeak' in memory_status :
    memory_status['VmSize'] = memory_status['VmPeak']

memory_usage = memory_status['VmSize'] - memory_status['VmExe'] - memory_status['VmLib'] - memory_status['VmStk']
memory_limit = memory_limit + memory_usage

# do not catch SIGXFSZ, otherwise the judge will not know whether an
# OLE has been generated
signal.signal(signal.SIGXFSZ, signal.SIG_DFL)

# tight memory usage
resource.setrlimit(resource.RLIMIT_DATA, (memory_limit * 1024, memory_limit * 1024 + 1 * 1024) )
resource.setrlimit(resource.RLIMIT_AS, (memory_limit * 1024 + 10 * 1024 * 1024, memory_limit * 1024 + 11 * 1024 * 1024) )

# after the above two calls, the judge will know preparation has finished and
# all following syscall should be traced

try :
    runpy.run_module(module_name, run_name = '__main__')
except MemoryError :
    os.kill(os.getpid(), signal.SIGKILL)
except ZeroDivisionError :
    os.kill(os.getpid(), signal.SIGFPE)

