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

#ifndef __MAIN_H
#define __MAIN_H

#include <string>

using namespace std;

#define MAX_TIME_LIMIT 300
#define MAX_MEMORY_LIMIT (1024 * 1024)
#define MAX_OUTPUT_LIMIT (16 * 1024)
#define MAX_DATA_FILE_SIZE (16 * 1024)
#define HEADER_SIZE 9
#define TESTCASE_MSG_SIZE 9

// Returns true if the specified file type is supported by the server
bool isSupportedSourceFileType(const string& sourceFileType);

int readHeader(int fdSocket,
               string* sourceFileType,
               unsigned int* problemId,
               unsigned int* version);

int readTestcase(int fdSocket,
                 unsigned int* testcase,
                 unsigned int* timeLimit,
                 unsigned int* memoryLimit,
                 unsigned int* outputLimit);

// Reads the file content from the given file descriptor and writes the file
// specified by outputFilename. Creates the file if not exists.
//
// Return 0 if success, or -1 if any error occurs.
int saveFile(int fdSocket, const string& outputFilename, size_t size);

int readSourceFilename(int fdSocket, string* sourceFilename);

int saveSourceFile(int fdSocket, const string& sourceFileName);

bool isValidDataStructure(const string& dir);

int saveData(int fdSocket, unsigned int problemId, unsigned int version);

// Deal with a single judge request
void process(int fdSocket);

int execMain(int argc, char* argv[]);

#endif

