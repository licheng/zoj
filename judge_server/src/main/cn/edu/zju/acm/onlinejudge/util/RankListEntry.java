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

public class RankListEntry implements Comparable<RankListEntry> {

    private UserProfile user;
    private final int[] acceptTime;
    private final int[] submitNumber;
    private int penalty = 0;
    private long solved = 0;
    private long submitted = 0;

    public RankListEntry(int problemNumber) {
        this.acceptTime = new int[problemNumber];
        this.submitNumber = new int[problemNumber];
        for (int i = 0; i < problemNumber; ++i) {
            this.acceptTime[i] = -1;
        }
    }

    public void setSolved(long solved) {
        this.solved = solved;
    }

    public long getSolved() {
        return this.solved;
    }

    public int getPenalty() {
        return this.penalty;
    }

    public int getAcceptTime(int index) {
        return this.acceptTime[index];
    }

    public int getSubmitNumber(int index) {
        return this.submitNumber[index];
    }

    public void update(int index, int time, boolean accepted) {
        if (this.acceptTime[index] >= 0) {
            return;
        }
        if (accepted) {
            this.acceptTime[index] = time;
            this.penalty += time + this.submitNumber[index] * 20;
            this.solved++;
        }
        this.submitNumber[index]++;
    }

    public UserProfile getUserProfile() {
        return this.user;
    }

    public void setUserProfile(UserProfile user) {
        this.user = user;
    }

    public double getACRatio() {
        if (this.submitNumber.length == 0) {
            return 0;
        } else {
            return (double) this.solved / (double) this.submitted;
        }
    }

    public void setSubmitted(long submitted) {
        this.submitted = submitted;
    }

    public long getSubmitted() {
        return this.submitted;
    }

    public int compareTo(RankListEntry obj) {
        RankListEntry entry = obj;
        if (entry.solved == this.solved) {
            return this.penalty - entry.penalty;
        } else {
            return (int) (entry.solved - this.solved);
        }
    }
}
