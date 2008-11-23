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
    virtual ~Runner();

    // Runs the specified program. Returns 0 if execution succeeded, or -1 on error.
    virtual int Run(int sock, int time_limit, int memory_limit, int output_limit, int uid, int gid) = 0;

  protected:
    static int SendRunningMessage(int sock, uint32_t time_consumption, uint32_t memory_consumption);
};

#endif // __RUNNER_H__
