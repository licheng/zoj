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

#ifndef __COMMAND_READER_H
#define __COMMAND_READER_H

#include "util.h"
#include "logging.h"

class CommandReader {
    public:
        CommandReader(bool break_on_signal): break_on_signal_(break_on_signal) {
            set_sock(-1);
        }

        int ReadUint8() {
            return ReadUint((uint8_t)0);
        }

        int ReadUint16() {
            int ret = ReadUint((uint16_t)0);
            if (ret < 0) {
                return -1;
            }
            return ntohs(ret);
        }

        int ReadUint32() {
            int ret = ReadUint((uint32_t)0);
            if (ret < 0) {
                return -1;
            }
            return ntohl(ret);
        }

        void Rewind() {
            p_read_ = buf_;
        }

        void Clear() {
            p_read_ = p_write_ = buf_;
        }

        bool error() {
            return error_;
        }

        void set_sock(int sock) {
            sock_ = sock;
            if (sock < 0) {
                error_ = true;
            } else {
                error_ = false;
            }
            Clear();
        }

    private:
        int TryRead(int num);

        template<class T>
        int ReadUint(T t) {
            if (TryRead(sizeof(T)) < 0) {
                return -1;
            }
            int ret = *(T*)p_read_;
            p_read_ += sizeof(T);
            return ret;
        }

        int sock_;
        char buf_[32];
        char* p_read_;
        char* p_write_;
        bool error_;
        bool break_on_signal_;
};

#endif
