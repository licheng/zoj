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

#include "checker.h"

#include "common_io.h"
#include "logging.h"
#include "protocol.h"

Checker::~Checker() {
}

int Checker::Check(int sock) {
    LOG(INFO)<<"Judging";
    WriteUint32(sock, JUDGING);
    int result = InternalCheck(sock);
    switch(result) {
        case ACCEPTED:
            LOG(INFO)<<"Accepted";
            break;
        case WRONG_ANSWER:
            LOG(INFO)<<"Wrong Answer";
            break;
        case PRESENTATION_ERROR:
            LOG(INFO)<<"Presentation Error";
            break;
        default:
            if (result != -1) {
                LOG(ERROR)<<"Invalid result "<<result;
            }
            WriteUint32(sock, INTERNAL_ERROR);
            return -1;
    }
    WriteUint32(sock, result);
    return 0;
}

