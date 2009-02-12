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
    static const Environment* GetInstance();

    const string& root() const {
        return root_;
    }

    string GetWorkingDir() const;

    int ChangeToWorkingDir() const;

    void ClearWorkingDir() const;

    string GetProblemDir(int problem_id, int revision) const;

    string GetCompilationScript() const;

    string GetLogDir() const;

    string GetServerSockName() const;

    string GetClientSockName() const;

  private:
    Environment(const string& root) : root_(root) {
    }
    
    static Environment* instance_;

    string root_;

  friend class CompilerTest;
  friend class ControlMainTest;
  friend class ExecJudgeCommandTest;
  friend class ExecCompileCommandTest;
  friend class ExecTestCaseCommandTest;
  friend class CheckDataTest;
  friend class ExecDataCommandTest;
  friend class JudgeMainTest;
};

#endif // __ENVIRONMENT_H__
