int main() {
    char b;
    int* p = (int*)(&b + 1);
    *p = 0;
    return 0;
}
