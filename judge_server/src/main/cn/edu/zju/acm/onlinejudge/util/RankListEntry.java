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
	private long submitted=0;
	public RankListEntry(int problemNumber) {		
		acceptTime = new int[problemNumber];
		submitNumber = new int[problemNumber];
		for (int i = 0; i < problemNumber; ++i) {
			acceptTime[i] = -1;
		}
	}
	public void  setSolved(long solved){
		this.solved=solved;
	}
	public long getSolved() {
		return solved;
	}
	public int getPenalty() {
		return penalty;
	}
	public int getAcceptTime(int index) {
		return acceptTime[index];
	}
	public int getSubmitNumber(int index) {
		return submitNumber[index];
	}
	public void update(int index, int time, boolean accepted) {
		if (acceptTime[index] >= 0) {
			return;
		}		
		if (accepted) {
			acceptTime[index] = time;
			penalty += time + submitNumber[index] * 20;
			solved++;
		}
		submitNumber[index]++;		
	}
	public UserProfile getUserProfile() {
		return user;
	}
	public void setUserProfile(UserProfile user) {
		this.user = user;
	}
	
	public double getACRatio(){
		if(submitNumber.length==0) {
			return 0;
		}
		else {
			return (double)solved/(double)submitted;
		}
	}
	
	public void  setSubmitted(long submitted){
		this.submitted=submitted;
	}
	
	public long getSubmitted(){
		return submitted;
	}
	
	@Override
	public int compareTo(RankListEntry obj) {
		RankListEntry entry = (RankListEntry) obj;
		if (entry.solved == this.solved) {
			return this.penalty - entry.penalty;
		} else {
			return (int)(entry.solved - this.solved);
		}
	}
}
