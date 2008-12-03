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

#include <signal.h>
#include <unistd.h>

#include "common_io.h"
#include "logging.h"
#include "protocol.h"

Runner::~Runner() {
    killpg(pid_, SIGTERM);
}

int Runner::SendRunningMessage() {
    if (WriteUint32(sock_, RUNNING) == -1 ||
        WriteUint32(sock_, time_consumption_) == -1 ||
        WriteUint32(sock_, memory_consumption_) == -1) {
        LOG(ERROR)<<"Fail to send running message";
        return -1;
    }
    return 0;
}

int Runner::Run() {
    LOG(INFO)<<"Running";
    InternalRun();
    switch (result_) {
      case TIME_LIMIT_EXCEEDED:
        LOG(INFO)<<"Time limit exceeded";
        break;
      case OUTPUT_LIMIT_EXCEEDED:
        LOG(INFO)<<"Output limit exceeded";
        break;
      case MEMORY_LIMIT_EXCEEDED:
        LOG(INFO)<<"Memory limit exceeded";
        break;
      case RUNTIME_ERROR:
        LOG(INFO)<<"Runtime error";
        break;
      case FLOATING_POINT_ERROR:
        LOG(INFO)<<"Floating point error";
        break;
      case SEGMENTATION_FAULT:
        LOG(INFO)<<"Segmentation fault";
        break;
    }
    if (result_ == 0) {
        return 0;
    }
    WriteUint32(sock_, result_);
    if (result_ == INTERNAL_ERROR) {
        return -1;
    } else {
        return 1;
    }
}
