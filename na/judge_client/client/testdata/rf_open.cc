#include <unistd.h>
#include <stdio.h>
#include <fcntl.h>

int main() {
    fopen("/tmp/1", "r");
    return 0;
}
