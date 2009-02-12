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

#ifndef __SPECIAL_CHECKER_H__
#define __SPECIAL_CHECKER_H__

#include <string>

using namespace std;

#include "checker.h"

class SpecialChecker : public Checker {
  public:
    SpecialChecker(const string& special_judge_filename) : special_judge_filename_(special_judge_filename) {
    }

  protected:
    int InternalCheck(int sock);

  private:
    string special_judge_filename_;

  friend class SpecialCheckerTest;
};

#endif // __SPECIAL_CHECKER_H__
