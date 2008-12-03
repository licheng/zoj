#define __NO_ZUNIT_MAIN__
#include "zunit.h"

#include <iostream>
#include <utility>

using namespace std;

namespace zunit {

Runner::Runner(const string& name) : name_(name) {
    RunnerRegistry::GetInstance()->AddRunner(this);
}

Runner::~Runner() {
}

RunnerRegistry* RunnerRegistry::instance_ = 0;

RunnerRegistry* RunnerRegistry::GetInstance() {
    if (instance_ == NULL) {
        instance_ = new RunnerRegistry();
    }
    return instance_;
}

RunnerRegistry::~RunnerRegistry() {
    for (int i = 0; i < runners_.size(); ++i) {
        delete runners_[i];
    }
}

void RunnerRegistry::AddRunner(Runner* runner) {
    runners_.push_back(runner);
}

int RunnerRegistry::RunAll() {
    vector<pair<int, Exception*> > failures;
    for (int i = 0; i < runners_.size(); ++i) {
        cout<<"=============== "<<runners_[i]->name()<<" ==============="<<endl;
        try {
            runners_[i]->Run();
            cout<<"\x1b[32mPass\x1b[0m"<<endl<<endl;
        } catch (Exception* e) {
            cout<<"\x1b[31mFailed\x1b[0m"<<endl<<endl;
            failures.push_back(make_pair(i, e));
        }
        cout<<endl;
    }

    cout<<runners_.size()<<" testcases. ";
    if (failures.size()) {
        cout<<"\x1b[31m"<<failures.size()<<" FAILED\x1b[0m"<<endl;
    } else {
        cout<<"\x1b[32mALL PASSED\x1b[0m"<<endl;
    }
    for (int i = 0; i < failures.size(); ++i) {
        int index = failures[i].first;
        cout<<endl<<index + 1<<") "<<runners_[index]->name();
        failures[i].second->Print(cout);
        delete failures[i].second;
    }

    return failures.size() != 0;
}

}
