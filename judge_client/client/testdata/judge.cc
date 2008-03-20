#include <iostream>
#include <fstream>
#include <string>
#include <unistd.h>
#include <fcntl.h>
#include <sstream>

using namespace std;

int main(int argc, char* argv[]) {
    int a, b;
    int result = 0;
    ifstream fin(argv[1]);
    while (fin>>a>>b) {
        string s;
        for (;;) {
            getline(cin, s);
            if (cin.fail()) {
                return 1;
            }
            if (s.find_first_not_of(" \t") != string::npos) {
                break;
            }
            result = 2;
        }
        if (s.find(' ') != string::npos) {
            result = 2;
        }
        istringstream is(s);
        int c;
        is>>c;
        if (is.fail() || c != a + b) {
            return 1;
        }
    }
    for (;;) {
        string s;
        getline(cin, s);
        if (cin.fail()) {
            break;
        }
        if (s.find_first_not_of(" \t") != string::npos) {
            return 1;
        }
    }
    return result;
}
