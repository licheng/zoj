#ifndef __TRACE_H
#define __TRACE_H

#include <string>
#include <vector>

#include "judge_result.h"

class ProcessMonitor {
    public:
        ProcessMonitor(): result(-1), timeConsumption(0), memoryConsumption(0) {
            ProcessMonitor::monitor = this;
        }

        virtual ~ProcessMonitor() {
            ProcessMonitor::monitor = NULL;
        }

        virtual int onOpen(const std::string& filename, int flags) {
            return 1;
        }

        virtual int onClone() {
            return 0;
        }

        virtual int onExecve();

        virtual void onMemoryLimitExceeded();

        virtual void onExit(pid_t pid);

        virtual void onSIGCHLD(pid_t pid);

        virtual void onError();

        virtual void terminate();

        int getResult() const {
            return this->result;
        }

        void setResult(int result) {
            this->result = result;
        }

        double getTimeConsumption() const {
            return this->timeConsumption;
        }

        int getMemoryConsumption() const {
            return this->memoryConsumption;
        }

        int hasExited() const {
            return result >= 0 && result != RUNNING;
        }

        static ProcessMonitor* getMonitor() {
            return ProcessMonitor::monitor;
        }

    protected:
        int result;
        double timeConsumption;
        int memoryConsumption;
    private:
        static ProcessMonitor* monitor;
};

void installHandlers();

#endif
