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

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;

public class ProblemsetRankList {

    private int offset = -1;
    private int count = -1;
    private UserProfile[] users;
    private int[] solved;
    private int[] total;

    public ProblemsetRankList(int offset, int count) {
        this.offset = offset;
        this.count = count;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getCount() {
        return this.count;
    }

    public UserProfile[] getUsers() {
        return this.users;
    }

    public void setUsers(UserProfile[] users) {
        this.users = users;
    }

    public int[] getSolved() {
        return this.solved;
    }

    public void setSolved(int[] solved) {
        this.solved = solved;
    }

    public int[] getTotal() {
        return this.total;
    }

    public void setTotal(int[] total) {
        this.total = total;
    }

}
