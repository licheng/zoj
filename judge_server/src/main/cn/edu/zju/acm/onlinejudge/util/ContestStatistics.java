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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;

public class ContestStatistics {

    public static final JudgeReply[] JUDGE_REPLIES =
            new JudgeReply[] {JudgeReply.ACCEPTED, JudgeReply.WRONG_ANSWER, JudgeReply.PRESENTATION_ERROR,
                              JudgeReply.RUNTIME_ERROR, JudgeReply.FLOATING_POINT_ERROR, JudgeReply.SEGMENTATION_FAULT,
                              JudgeReply.TIME_LIMIT_EXCEEDED, JudgeReply.MEMORY_LIMIT_EXCEEDED,
                              JudgeReply.OUTPUT_LIMIT_EXCEEDED, JudgeReply.COMPILATION_ERROR};

    private final List<Problem> problems;
    private final Map<Long, Integer> problemIdMap;

    private final int[][] statistics;
    private final int[] judgeReplyCount;
    private final int[] problemCount;
    private int total = 0;

    public ContestStatistics(List<Problem> problems) {
        if (problems == null) {
            throw new IllegalArgumentException("problems is null");
        }
        this.problems = problems;
        this.problemIdMap = new HashMap<Long, Integer>();
        int index = 0;
        for (Iterator<Problem> it = problems.iterator(); it.hasNext();) {
            Object obj = it.next();
            if (!(obj instanceof Problem)) {
                throw new IllegalArgumentException("problems should contain only Problem instances");
            }
            this.problemIdMap.put(new Long(((Problem) obj).getId()), new Integer(index));
            index++;
        }

        this.judgeReplyCount = new int[ContestStatistics.JUDGE_REPLIES.length];
        this.problemCount = new int[problems.size()];
        this.statistics = new int[problems.size()][ContestStatistics.JUDGE_REPLIES.length];

    }

    public int getTotal() {
        return this.total;
    }

    public List<Problem> getProblems() {
        return this.problems;
    }

    public void setCount(int problemIndex, int judgeReplyIndex, int value) {
        if (!this.validateProblemIndex(problemIndex) || !this.validateJudgeReplyIndex(judgeReplyIndex)) {
            return;
        }
        int dif = value - this.statistics[problemIndex][judgeReplyIndex];
        this.total += dif;
        this.problemCount[problemIndex] += dif;
        this.judgeReplyCount[judgeReplyIndex] += dif;
        this.statistics[problemIndex][judgeReplyIndex] = value;
    }

    public void setCount(long problemId, long judgeReplyId, int value) {
        this.setCount(this.getProblemIndex(problemId), this.getJudgeReplyIndex(judgeReplyId), value);
    }

    public int getCount(int problemIndex, int judgeReplyIndex) {
        if (!this.validateProblemIndex(problemIndex) || !this.validateJudgeReplyIndex(judgeReplyIndex)) {
            return 0;
        }
        return this.statistics[problemIndex][judgeReplyIndex];
    }

    public int getCount(long problemId, long judgeReplyId) {
        return this.getCount(this.getProblemIndex(problemId), this.getJudgeReplyIndex(judgeReplyId));
    }

    public int getJudgeReplyCount(long judgeReplyId) {
        return this.getJudgeReplyCount(this.getJudgeReplyIndex(judgeReplyId));
    }

    public int getJudgeReplyCount(int judgeReplyIndex) {
        if (!this.validateJudgeReplyIndex(judgeReplyIndex)) {
            return 0;
        }
        return this.judgeReplyCount[judgeReplyIndex];
    }

    public int getProblemCount(long problemId) {
        return this.getProblemCount(this.getProblemIndex(problemId));
    }

    public int getProblemCount(int problemIndex) {
        if (!this.validateProblemIndex(problemIndex)) {
            return 0;
        }
        return this.problemCount[problemIndex];
    }

    private boolean validateProblemIndex(int problemIndex) {
        return problemIndex >= 0 && problemIndex < this.problems.size();
    }

    private boolean validateJudgeReplyIndex(int judgeReplyIndex) {
        return judgeReplyIndex >= 0 && judgeReplyIndex < ContestStatistics.JUDGE_REPLIES.length;
    }

    private int getProblemIndex(long problemId) {
        Integer index = this.problemIdMap.get(new Long(problemId));
        if (index == null) {
            return -1;
        }
        return index.intValue();
    }

    private int getJudgeReplyIndex(long replyId) {
        for (int i = 0; i < ContestStatistics.JUDGE_REPLIES.length; ++i) {
            if (ContestStatistics.JUDGE_REPLIES[i].getId() == replyId) {
                return i;
            }
        }
        return -1;
    }

}
