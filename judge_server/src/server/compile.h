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

#ifndef __COMPILE_H
#define __COMPILE_H

#include <string>

// Compiles the specified source file and stores the error message into buffer
// if compilation fails. If the length of the error message is greater than
// bufferSize bytes, it is truncated. bufferSize will be updated to reflect the
// actual length of the error message.
// Returns 0 if compilation succeeded, or -1 on error.
int compile(const std::string& sourceFilename, char buffer[], int* bufferSize);

// Compiles the specified source file. See the Communication Protocol for the
// description of possible meessages written to fd.
// Returns 0 if compilation succeeded, or -1 on error.
int doCompile(int fd, const std::string& sourceFilename);

#endif
