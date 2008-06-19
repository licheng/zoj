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

#include "command_reader.h"

#include <errno.h>

#include "global.h"
#include "logging.h"

int CommandReader::TryRead(int num) {
    if (sock_ < 0) {
        return -1;
    }
    for (;;) {
        int gap = p_read_ + num - p_write_;
        if (gap <= 0) {
            return 0;
        }
        if (global::terminated) {
            return -1;
        }
        int count = read(sock_, p_write_, gap);
        if (count < 0) {
            if (errno != EINTR) {
                LOG(SYSCALL_ERROR)<<"Fail to read";
                error_ = true;
            } else if (!break_on_signal_) {
                continue;
            }
            return -1;
        }
        if (count == 0) {
            LOG(ERROR)<<"EOF";
            error_ = true;
            return -1;
        }
        p_write_ += count;
        if (break_on_signal_) {
            return count < gap ? -1 : 0;
        }
    }
}
