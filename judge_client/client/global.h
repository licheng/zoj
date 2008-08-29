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

#ifndef __GLOBAL_H
#define __GLOBAL_H

#include <string>

using namespace std;

#define CMD_PING 100
#define CMD_JUDGE 1
#define CMD_DATA 2
#define CMD_COMPILE 3
#define CMD_TESTCASE 4
#define CMD_REMOVE_PROBLEM 5
#define CMD_INFO 6

#define TYPE_CONTROL 1
#define TYPE_DATA 2
#define TYPE_JUDGE 3

#define COMPILING 1
#define RUNNING 2
#define RUNTIME_ERROR 3
#define WRONG_ANSWER 4
#define ACCEPTED 5
#define TIME_LIMIT_EXCEEDED 6
#define MEMORY_LIMIT_EXCEEDED 7
#define OUTPUT_LIMIT_EXCEEDED 10
#define COMPILATION_ERROR 12
#define PRESENTATION_ERROR 13
#define INTERNAL_ERROR 14
#define FLOATING_POINT_ERROR 15
#define SEGMENTATION_FAULT 16
#define JUDGING 19
#define READY 100
#define NO_SUCH_PROBLEM 101
#define INVALID_INPUT 102

#define MAX_JOBS 100
#define MAX_LOG_FILE_SIZE 262144 // 256KB
#define MAX_DATA_FILE_SIZE 16 * 1024 * 1024 // 16MB

#define COMPILER_GPP 0
#define COMPILER_FREE_PASCAL 2
#define COMPILER_GCC 3
#define COMPILER_JAVAC 4

struct CompilerInfo {
    int id;
    const char* compiler;
    const char* source_file_type;
};

namespace global {
    extern int terminated;
    extern int socket_closed;
    extern CompilerInfo COMPILER_LIST[4];
}

#endif
