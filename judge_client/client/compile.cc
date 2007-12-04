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

#include "compile.h"

#include <string>

#include "params.h"
#include "trace.h"
#include "util.h"

// The root directory which contains problems, scripts and working directory of
// the client
DECLARE_ARG(string, root);

int doCompile(int fdSocket, const string& sourceFilename) {
    sendReply(fdSocket, COMPILING);
    string command =
        ARG_root + "/script/compile.sh '" + sourceFilename + "'";
    class Callback: public TraceCallback {
        public:
            // Nothing special should be done when the compiling process
            // terminates.
            virtual void onSIGCHLD(pid_t) { }
    } callback;
    static char errorMessage[16384];
    int maxErrorMessageLength = sizeof(errorMessage);
    int result = runShellCommand(command.c_str(),
                                 errorMessage,
                                 &maxErrorMessageLength,
                                 30);
    if (result) {
        if (result == -1) {
            sendReply(fdSocket, SERVER_ERROR);
            return -1;
        } else {
            sendReply(fdSocket, COMPILATION_ERROR);
            writen(fdSocket, errorMessage, maxErrorMessageLength);
            return -1;
        }
    }
    return 0;
}
