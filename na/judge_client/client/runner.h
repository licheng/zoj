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

#ifndef __RUNNER_H__
#define __RUNNER_H__

#include <string>

using namespace std;

class Runner {
  public:
    Runner(int sock, int time_limit, int memory_limit, int output_limit, int uid, int gid)
        : sock_(sock),
          time_limit_(time_limit),
          memory_limit_(memory_limit),
          output_limit_(output_limit),
          uid_(uid),
          gid_(gid),
          pid_(-1),
          result_(-1),
          time_consumption_(0),
          memory_consumption_(0) {
    }

    virtual ~Runner();

    // Runs the specified program. Returns 0 if execution succeeded, or -1 on error.
    int Run();


  protected:
    virtual void InternalRun() = 0;

    int SendRunningMessage();

    int sock_;
    int time_limit_;
    int memory_limit_;
    int output_limit_;
    int uid_;
    int gid_;
    pid_t pid_;
    int result_;
    int time_consumption_;
    int memory_consumption_;
};

#endif // __RUNNER_H__
