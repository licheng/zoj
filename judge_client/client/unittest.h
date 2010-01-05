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

#ifndef __UNITTEST_H__
#define __UNITTEST_H__

#include "zunit.h"

#include <cstdio>
#include <iostream>
#include <stdlib.h>

using namespace std;

#include <unistd.h>

static inline string GetWorkingDir() {
    char path[FILENAME_MAX + 1];
    if (getcwd(path, sizeof(path)) == NULL) {
        cout<<"Fail to get the current working dir";
        exit(1);
    }
    return path;
}

const string CURRENT_WORKING_DIR = GetWorkingDir();
const string TESTDIR = CURRENT_WORKING_DIR + "/testdata";

#endif // __UNITTEST_H__
