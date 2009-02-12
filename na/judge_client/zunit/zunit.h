#ifndef __ZUNIT_H__
#define __ZUNIT_H__

#include <ostream>
#include <sstream>
#include <string>
#include <vector>

using namespace std;

namespace zunit {

class Exception {
  public:
    Exception(const string& message,
              const string& expected,
              const string& actual,
              int line_number) {
        ostringstream os;
        os<<" (line "<<line_number<<"): "<<message<<endl;
        os<<"    Expected: "<<expected<<endl;
        os<<"    Actual:   "<<actual<<endl;
        message_ = os.str();
    }

    Exception(const string& message, int line_number) {
        ostringstream os;
        os<<" (line "<<line_number<<"): "<<message<<endl;
        message_ = os.str();
    }

    void Print(ostream& os) {
        os<<message_;
    }

  private:
    string message_;
};

template<class T>
string ToString(const T& value) {
    ostringstream os;
    os<<value;
    return os.str();
}

template<class T>
void AssertEquals(const string& message,
                  const T& expected,
                  const T& actual,
                  int line_number) {
    if (!(expected == actual)) {
        throw new Exception(message,
                            ToString(expected),
                            ToString(actual),
                            line_number);
    }
}

template<class T>
void AssertEquals(const T& expected,
                  const T& actual,
                  int line_number) {
    if (!(expected == actual)) {
        throw new Exception("Assertion Failed",
                            ToString(expected),
                            ToString(actual),
                            line_number);
    }
}

static inline void Assert(const string& message, int condition, int line_number) {
    if (!condition) {
        throw new Exception(message, "True", "False", line_number);
    }
}

static inline void Assert(int condition, int line_number) {
    if (!condition) {
        throw new Exception("Assertion Failed", "True", "False", line_number);
    }
}

static inline void Fail(int line_number) {
    throw new Exception("Failure", line_number);
}

static inline void Fail(const string& message, int line_number) {
    throw new Exception(message, line_number);
}

class Runner {
  public:
    Runner(const string& name);
    virtual ~Runner();

    const string& name() {
        return name_;
    }

    virtual void Run() = 0;

  private:
    string name_;
};
template<class T>
class TestCaseRunner : public Runner {
  public:
    TestCaseRunner(const string& name) : Runner(name) {
    }

    virtual void Run() {
        T test_case;
        try {
            test_case.SetUp();
            test_case.__Test__();
            test_case.TearDown();
        } catch (Exception* e) {
            test_case.TearDown();
            throw e;
        }
    }
};

class RunnerRegistry {
  public:
    ~RunnerRegistry();

    static RunnerRegistry* GetInstance();

    void AddRunner(Runner* runner);

    int RunAll();

  private:
    RunnerRegistry() {
    }
    static RunnerRegistry* instance_;
    vector<Runner*> runners_;
};

}

class TestFixture {
  public:
    void SetUp() {
    }

    void TearDown() {
    }
};

#define TEST_F(name, method) \
    class __ ## name ## _ ## method : public name {\
      public:\
        __ ## name ## _ ## method() { } \
        ~__ ## name ## _ ## method() { } \
      private:\
        void __Test__();\
      friend class zunit::TestCaseRunner<__ ## name ## _ ## method>;\
    };\
    zunit::TestCaseRunner<__ ## name ## _ ## method> \
        __ ## name ## _ ## method ## _runner(#name "::" #method);\
    void __ ## name ## _ ## method::__Test__()

#define ASSERT_EQUAL(expected, actual) \
        zunit::AssertEquals("ASSERT_EQUAL(" #expected ", " #actual ")", (expected), (actual), __LINE__)
#define ASSERT(condition)  zunit::Assert(condition, __LINE__)
#define FAIL(message) zunit::Fail(message, __LINE__)

#ifndef __NO_ZUNIT_MAIN__
int main() {
    return zunit::RunnerRegistry::GetInstance()->RunAll();
}
#endif // __NO_ZUNIT_MAIN__

#endif // __ZUNIT_H__
