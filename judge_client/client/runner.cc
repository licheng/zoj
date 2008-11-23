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

#include "runner.h"

#include "common_io.h"
#include "logging.h"
#include "protocol.h"

Runner::~Runner() {
}

int Runner::SendRunningMessage(int sock, uint32_t time_consumption, uint32_t memory_consumption) {
    if (WriteUint32(sock, RUNNING) == -1 ||
        WriteUint32(sock, time_consumption) == -1 ||
        WriteUint32(sock, memory_consumption) == -1) {
        LOG(ERROR)<<"Fail to send running message";
        return -1;
    }
    return 0;
}
