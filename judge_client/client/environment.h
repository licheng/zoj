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
