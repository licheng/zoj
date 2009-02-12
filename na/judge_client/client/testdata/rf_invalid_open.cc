#include <unistd.h>
#include <stdio.h>
#include <fcntl.h>

int main() {
    fopen(NULL, "r");
    return 0;
}
