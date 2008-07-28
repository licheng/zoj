package cn.edu.zju.acm.onlinejudge.util;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;

public class ProblemStatistics {
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
	private final long[] statistics;
	private long total = 0;
	
	public ProblemStatistics(long problemId) {		
		statistics = new long[JUDGE_REPLIES.length];
	}
	
	public long getTotal() {
		return total;
	}
	
	
	public void setCount(int judgeReplyIndex, long value) {
		if(judgeReplyIndex==-1)
		{
			return;
		}
		long dif = value - statistics[judgeReplyIndex];
		total += dif;
		statistics[judgeReplyIndex] = value;
	}
	public void setCount(long judgeReplyId, long value) {
		setCount(getJudgeReplyIndex(judgeReplyId), value);
	}
	
	
	public long getCount(int judgeReplyIndex) {
		return statistics[judgeReplyIndex];
	}
	public long getCount(long judgeReplyId) {
		return getCount(getJudgeReplyIndex(judgeReplyId));
	}
	public int getJudgeReplyCount(long judgeReplyId) {
		return getJudgeReplyCount(getJudgeReplyIndex(judgeReplyId));
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
