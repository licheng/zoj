/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either revision 3 of the License, or
 * (at your option) any later revision.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */

#include "net_util.h"

#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/un.h>

#include "logging.h"

int CreateServerSocket(int* port) {
    int sock = socket(PF_INET, SOCK_STREAM, 6);
    if (sock == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to create socket";
        return -1;
    }
    int optionValue = 1;
    if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optionValue, sizeof(optionValue)) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to set socket option";
        close(sock);
        return -1;
    }
    sockaddr_in address;
    memset(&address, 0, sizeof(address));
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = htonl(INADDR_ANY); 
    address.sin_port = 0;
    if (bind(sock, (struct sockaddr*)&address, sizeof(address)) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to bind";
        close(sock);
        return -1;
    }
    if (listen(sock, 32) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to listen";
        close(sock);
        return -1;
    }
    socklen_t len = sizeof(address);
    if (getsockname(sock, (struct sockaddr*)&address, &len) == -1) {
        LOG(SYSCALL_ERROR)<<"Fail to get socket port";
        close(sock);
        return -1;
    }
    *port = ntohs(address.sin_port);
    return sock;
}

