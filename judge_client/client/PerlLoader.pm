use strict;
use BSD::Resource;

sub get_memory_usage() {
    my %mem;
    open FILE, '</proc/self/status' or die $!;
    while (my $line = <FILE>) {
        next if $line !~ /^(Vm[^:]*):\s*(\d*) /;
        $mem{$1} = int($2);
    }
    close FILE;
    $mem{'VmSize'} = $mem{'VmPeak'} if exists($mem{'VmPeak'});
    return $mem{'VmSize'} - $mem{'VmExe'} - $mem{'VmLib'} - $mem{'VmStk'};
}

my $memory_limit = (int($ENV{'MEMORY_LIMIT'}) + get_memory_usage()) * 1024;

$SIG{FPE} = 'DEFAULT';
$SIG{__DIE__} = sub {
    # send SIGFPE to self
    kill 8, $$ if $_[0] =~ '^Illegal division by zero';
};

setrlimit(RLIMIT_DATA, $memory_limit, $memory_limit + 1);
setrlimit(RLIMIT_AS, $memory_limit + 10 * 1024 * 1024, $memory_limit + 10 * 1024 * 1024 + 1);

