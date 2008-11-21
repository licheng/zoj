#ifndef __ENVIRONMENT_H__
#define __ENVIRONMENT_H__

#include <string>

using namespace std;

class Environment {
  public:
    static Environment* instance() {
        return instance_;
    }

    const string& root() {
        return root_;
    }

    void set_root(const string& root) {
        root_ = root;
    }

    string GetWorkingDir();

    int ChangeToWorkingDir();

    void ClearWorkingDir();

    string GetProblemDir(int problem_id, int revision);

    string GetCompilationScript();

    string GetLogDir();

    string GetServerSockName();

    string GetClientSockName();

  private:
    Environment() {
    }
    
    static Environment* instance_;

    string root_;
};

#endif // __ENVIRONMENT_H__
