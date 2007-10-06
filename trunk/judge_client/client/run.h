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

#ifndef __RUN_H
#define __RUN_H

#include <string>

// Runs the specified program. See the Communication Protocol for the
// description of possible meessages written to fdSocket.
// Returns 0 if compilation succeeded, or -1 on error.
int doRun(int fdSocket,
          const std::string& programName,
          const std::string& sourceFileType,
          const std::string& stdinFilename,
          const std::string& stdoutFilename,
          int timeLiimt,
          int memoryLimit,
          int outputLimit);

#endif
