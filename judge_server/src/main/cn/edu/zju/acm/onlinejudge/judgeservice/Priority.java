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

package cn.edu.zju.acm.onlinejudge.judgeservice;

public class Priority {
    public static final int MIN = 1;
    public static final int LOW = 4;
    public static final int NORMAL = 5;
    public static final int HIGH = 6;
    public static final int MAX = 9;
    public static final int DENY = -100000;
    
    public static boolean isValidPriority(int priority) {
        return priority >= Priority.MIN && priority <= Priority.MAX;
    }
}
