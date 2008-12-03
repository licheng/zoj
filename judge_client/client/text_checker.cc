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

#include "text_checker.h"

#include "protocol.h"
#include "text_file_reader.h"

int TextChecker::InternalCheck(int sock) {
    int ret = ACCEPTED;
    TextFileReader f1("output"), f2("p.out");
    if (f1.Fail() || f2.Fail()) {
        return -1;
    }
    for (;;) {
        // Find the first non-space character at the beginning of line.
        // Blank lines are skipped.
        int c1 = f1.Read();
        int c2 = f2.Read();
        while (isspace(c1) || isspace(c2)) {
            if (c1 != c2) {
                ret = PRESENTATION_ERROR;
            }
            if (isspace(c1)) {
                c1 = f1.Read();
            }
            if (isspace(c2)) {
                c2 = f2.Read();
            }
        }
        // Compare the current line.
        for (;;) {
            // Read until 2 files return a space or 0 together.
            while ((!isspace(c1) && c1) || (!isspace(c2) && c2)) {
                if (c1 < 0 || c2 < 0) {
                    return -1;
                }
                if (c1 != c2) {
                    // Consecutive non-space characters should be all exactly the same
                    return WRONG_ANSWER;
                }
                c1 = f1.Read();
                c2 = f2.Read();
            }
            // Find the next non-space character or \n.
            while ((isspace(c1) && c1 != '\n') || (isspace(c2) && c2 != '\n')) {
                if (c1 != c2) {
                    ret = PRESENTATION_ERROR;
                }
                if (isspace(c1) && c1 != '\n') {
                    c1 = f1.Read();
                }
                if (isspace(c2) && c2 != '\n') {
                    c2 = f2.Read();
                }
            }
            if (c1 < 0 || c2 < 0) {
                return -1;
            }
            if (!c1 && !c2) {
                return ret;
            }
            if ((c1 == '\n' || !c1) && (c2 == '\n' || !c2)) {
                break;
            }
        }
    }
}
