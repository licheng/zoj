/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */

#ifndef __TRACE_H__
#define __TRACE_H__

#include <string>
#include <vector>

using namespace std;

#include "global.h"

class TraceCallback {
    public:
        TraceCallback(): result_(-1), time_consumption_(0), memory_consumption_(0), exited_(false) {
            TraceCallback::instance_ = this;
        }

        virtual ~TraceCallback() {
            TraceCallback::instance_ = NULL;
        }

        virtual bool OnClone() {
            return false;
        }

        virtual bool OnExecve();

        virtual bool OnOpen(const string& path, int flags);

        virtual void OnMemoryLimitExceeded();

        virtual void OnExit(pid_t pid);

        virtual void OnSIGCHLD(pid_t pid);

        virtual void OnError();

        void ProcessResult(int status);

        int GetResult() const {
            return result_;
        }

        void SetResult(int result) {
            result_ = result;
        }

        int GetTimeConsumption() const {
            return time_consumption_;
        }

        int GetMemoryConsumption() const {
            return memory_consumption_;
        }

        bool HasExited() const {
            return exited_;
        }

        static TraceCallback* GetInstance() {
            return TraceCallback::instance_;
        }

    protected:
        int result_;
        int time_consumption_;
        int memory_consumption_;
        bool exited_;

    private:
        static TraceCallback* instance_;
};

void InstallHandlers();
void UninstallHandlers();

#endif // __TRACE_H__
