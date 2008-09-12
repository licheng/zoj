/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

package cn.edu.zju.acm.onlinejudge.util;

import java.util.Random;

public class RandomStringGenerator {

    private static Random random = new Random(System.currentTimeMillis());

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int k = RandomStringGenerator.random.nextInt(62);
            if (k < 10) {
                sb.append((char) ('0' + k));
            } else if (k < 36) {
                sb.append((char) ('A' + k - 10));
            } else {
                sb.append((char) ('a' + k - 36));
            }
        }

        return sb.toString();
    }

    public static String generate() {
        return RandomStringGenerator.generate(32);
    }
}
