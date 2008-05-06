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

#ifndef __LOG_SERVER_H
#define __LOG_SERVER_H

#include <string>
#include <vector>

using namespace std;

class LogClient;

class LogServer {
    public:
        ~LogServer();

        static LogServer* Create(const string& root);

        void Start();

    private:
        LogServer(const string& root, int server_sock)
                : root_(root), server_sock_(server_sock) { }
        string root_;
        int server_sock_;
        vector<LogClient*> clients_;
};

#endif
