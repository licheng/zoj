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

#ifndef __PARAMS_H
#define __PARAMS_H

#include <string>
#include <vector>

using namespace std;

// The root directory which contains problems, scripts and working directory of
// the client
extern string JUDGE_ROOT;

// The uid for executing the program to be judged
extern int JOB_UID;

// The gid for executing the program to be judged
extern int JOB_GID;

extern pair<string, int> QUEUE_ADDRESS;

// All languages supported by this client
extern vector<string> LANG;

// Extracts parameter values from the passed-in arguments.
int parseArguments(int argc, char* argv[]);

#endif
