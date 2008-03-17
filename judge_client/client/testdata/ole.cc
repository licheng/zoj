#include <iostream>
#include <string>

using namespace std;

int main() {
    string s(10000, 'a');
    for (;;) {
        cout<<s<<endl;
    }
    return 0;
}
