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

#ifndef __TEXT_CHECKER_H__
#define __TEXT_CHECKER_H__

#include "checker.h"

class TextChecker : public Checker {
  protected:
    // This function compares two text files and returns
    // 1. ACCEPTED
    //    If these two files are exactly the same
    // 2. PRESENTATION_ERROR
    //    If these two files are the same after normalization.
    //    The file is normalized by
    //      a) Removing all white spaces at the beginning or ending of lines
    //      b) Removing all blank lines
    //      c) Replacing all consecutive white space characters by a single space(0x20).
    //    See the C library function "isspace" for the definition of white space characters.
    // 3. WRONG_ANSWER
    //    Neither ACCEPTED nor PRESENTATION_ERROR
    // 4. -1
    //    Any error occurs
    int InternalCheck(int sock);
};

#endif // __TEXT_CHECKER_H__
