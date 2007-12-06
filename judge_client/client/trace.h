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
            memoryConsumption(0) {
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

    private:
        static TraceCallback* instance;
};

class ExecutiveCallback: public TraceCallback {
    public:
        virtual void onExit(pid_t pid);

};

void installHandlers();

#endif
