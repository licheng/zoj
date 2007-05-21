/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ Judge Server.
 *
 * ZOJ Judge Server is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZOJ Judge Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ Judge Server; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

#ifndef __TRACE_H
#define __TRACE_H

#include <string>
#include <vector>

#include "judge_result.h"

class TraceCallback {
    public:
        TraceCallback():
            result(-1),
            timeConsumption(0),
            memoryConsumption(0),
            pid(0) {
            TraceCallback::instance = this;
        }

        virtual ~TraceCallback() {
            TraceCallback::instance = NULL;
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

        void setPid(pid_t pid) {
            this->pid = pid;
        }

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

        static TraceCallback* getInstance() {
            return TraceCallback::instance;
        }

    protected:
        int result;
        double timeConsumption;
        int memoryConsumption;
        pid_t pid;

    private:
        static TraceCallback* instance;
};

class ExecutiveCallback: public TraceCallback {
    public:
        virtual void onExit(pid_t pid);

};

void installHandlers();

#endif
