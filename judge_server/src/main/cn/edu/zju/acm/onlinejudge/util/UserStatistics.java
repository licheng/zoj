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
import java.util.Map;
import java.util.Set;

import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;

public class UserStatistics {
	Map<Long, Integer> counts = new HashMap<Long, Integer>();
	private final long userId;
	private final long contestId;
	private int total = 0;
	private Set<Problem> solved = null;
	
	
	public UserStatistics(long userId, long contestId) {
		this.userId = userId;
		this.contestId = contestId;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public long getContestId() {
		return contestId;
	}
	
	public int getTotal() {
		return total;
	}
	
	public int getCount(JudgeReply judgeReply) {
		return getCount(judgeReply.getId());
	}
	
	public double getPercentage(JudgeReply judgeReply) {
		return getPercentage(judgeReply.getId());
	}
	
	public double getPercentage(long judgeReplyId) {
		return total == 0 ? 0 : 1.0 * getCount(judgeReplyId) / total;
	}
	
	public int getPercentageInt(JudgeReply judgeReply) {
		return getPercentageInt(judgeReply.getId());
	}
	
	public int getPercentageInt(long judgeReplyId) {
		return total == 0 ? 0 : getCount(judgeReplyId) * 100 / total;
	}
	
	public int getCount(long judgeReplyId) {
		return counts.containsKey(judgeReplyId) ? counts.get(judgeReplyId) : 0;
	}
	
	public void setCount(JudgeReply judgeReply, int value) {
		setCount(judgeReply.getId(), value);
	}
	
	public void setCount(long judgeReplyId, int value) {
		total -= getCount(judgeReplyId);
		counts.put(judgeReplyId, value);
		total += value;
	}

	public boolean isSolved(Problem p) {
		if (solved == null) {
			return false;
		}
		return solved.contains(p);
	}
	public boolean isSolved(long id) {
		Problem p = new Problem();
		p.setId(id);
		return isSolved(p);
	}
	
	public Set<Problem> getSolved() {
		return solved;
	}

	public void setSolved(Set<Problem> s) {
		this.solved = s;
	}
		
}
