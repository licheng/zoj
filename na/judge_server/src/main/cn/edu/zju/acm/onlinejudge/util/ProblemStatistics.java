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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;

public class ProblemStatistics {
    Map<Long, Integer> counts = new HashMap<Long, Integer>();
    private final long problemId;
    private final String orderBy;
    private int total = 0;
    private List<Submission> bestRuns = null;

    public ProblemStatistics(long problemId, String orderBy) {
        this.problemId = problemId;
        this.orderBy = orderBy;
    }

    public long getProblemId() {
        return this.problemId;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public int getTotal() {
        return this.total;
    }

    public int getCount(JudgeReply judgeReply) {
        return this.getCount(judgeReply.getId());
    }

    public double getPercentage(JudgeReply judgeReply) {
        return this.getPercentage(judgeReply.getId());
    }

    public double getPercentage(long judgeReplyId) {
        return this.total == 0 ? 0 : 1.0 * this.getCount(judgeReplyId) / this.total;
    }

    public int getPercentageInt(JudgeReply judgeReply) {
        return this.getPercentageInt(judgeReply.getId());
    }

    public int getPercentageInt(long judgeReplyId) {
        return this.total == 0 ? 0 : this.getCount(judgeReplyId) * 100 / this.total;
    }

    public int getCount(long judgeReplyId) {
        return this.counts.containsKey(judgeReplyId) ? this.counts.get(judgeReplyId) : 0;
    }

    public void setCount(JudgeReply judgeReply, int value) {
        this.setCount(judgeReply.getId(), value);
    }

    public void setCount(long judgeReplyId, int value) {
        this.total -= this.getCount(judgeReplyId);
        this.counts.put(judgeReplyId, value);
        this.total += value;
    }

    public List<Submission> getBestRuns() {
        return this.bestRuns;
    }

    public void setBestRuns(List<Submission> bestRuns) {
        this.bestRuns = bestRuns;
    }

}
