package cn.edu.zju.acm.onlinejudge.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.Problem;

public class ContestStatistics {

	public static final JudgeReply[] JUDGE_REPLIES = new JudgeReply[] {
		JudgeReply.ACCEPTED, 
		JudgeReply.WRONG_ANSWER,
		JudgeReply.PRESENTATION_ERROR,
		JudgeReply.RUNTIME_ERROR,
		JudgeReply.FLOATING_POINT_ERROR,
        JudgeReply.SEGMENTATION_FAULT,
        JudgeReply.TIME_LIMIT_EXCEEDED,
        JudgeReply.MEMORY_LIMIT_EXCEEDED,
        JudgeReply.OUTPUT_LIMIT_EXCEEDED,
		JudgeReply.COMPILATION_ERROR
	};
	
    
	private final List problems;
	private final Map problemIdMap;
	
	private final int[][] statistics;
	private final int[] judgeReplyCount;
	private final int[] problemCount;
	private int total = 0;
	
	public ContestStatistics(List problems) {		
		if (problems == null) {
			throw new IllegalArgumentException("problems is null");
		}
		this.problems = problems;
		this.problemIdMap = new HashMap();
		int index = 0;
		for (Iterator it = problems.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (!(obj instanceof Problem)) {
				throw new IllegalArgumentException("problems should contain only Problem instances");
			}
			problemIdMap.put(new Long(((Problem) obj).getId()), new Integer(index));
			index++;
		}
		
		judgeReplyCount = new int[JUDGE_REPLIES.length];
		problemCount = new int[problems.size()];
		statistics = new int[problems.size()][JUDGE_REPLIES.length];
		
	}
	
	
	public int getTotal() {
		return total;
	}
	public List getProblems() {
		return problems;
	}
	
	
	public void setCount(int problemIndex, int judgeReplyIndex, int value) {
		if (!validateProblemIndex(problemIndex) || !validateJudgeReplyIndex(judgeReplyIndex)) {
			return;
		}
		int dif = value - statistics[problemIndex][judgeReplyIndex];
		total += dif;
		problemCount[problemIndex] += dif;
		judgeReplyCount[judgeReplyIndex] += dif;
		statistics[problemIndex][judgeReplyIndex] = value;
	}
	public void setCount(long problemId, long judgeReplyId, int value) {
		setCount(getProblemIndex(problemId), getJudgeReplyIndex(judgeReplyId), value);
	}
	
	
	public int getCount(int problemIndex, int judgeReplyIndex) {
		if (!validateProblemIndex(problemIndex) || !validateJudgeReplyIndex(judgeReplyIndex)) {
			return 0;
		}
		return statistics[problemIndex][judgeReplyIndex];
	}
	public int getCount(long problemId, long judgeReplyId) {
		return getCount(getProblemIndex(problemId), getJudgeReplyIndex(judgeReplyId));
	}
	public int getJudgeReplyCount(long judgeReplyId) {
		return getJudgeReplyCount(getJudgeReplyIndex(judgeReplyId));
	}
	
	public int getJudgeReplyCount(int judgeReplyIndex) {
		if (!validateJudgeReplyIndex(judgeReplyIndex)) {
			return 0;
		}
		return judgeReplyCount[judgeReplyIndex];
	}
	
	public int getProblemCount(long problemId) {
		return getProblemCount(getProblemIndex(problemId));
	}
	
	public int getProblemCount(int problemIndex) {
		if (!validateProblemIndex(problemIndex)) {
			return 0;
		}
		return problemCount[problemIndex];
	}
	
	private boolean validateProblemIndex(int problemIndex) {
		return problemIndex >= 0 && problemIndex < problems.size();
	}
	
	private boolean validateJudgeReplyIndex(int judgeReplyIndex) {
		return judgeReplyIndex >= 0 && judgeReplyIndex < JUDGE_REPLIES.length;
	}
	private int getProblemIndex(long problemId) {
		Integer index = (Integer) problemIdMap.get(new Long(problemId));
		if (index == null) {
			return -1;
		}
		return index.intValue();
	}
	
	
	private int getJudgeReplyIndex(long replyId) {
		for (int i = 0; i < JUDGE_REPLIES.length; ++i) {
			if  (JUDGE_REPLIES[i].getId() == replyId) {
				return i;
			}
		}
		return -1;
	}
	
}
