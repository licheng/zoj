package cn.edu.zju.acm.onlinejudge.util;

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;

public class RankListEntry implements Comparable {

	private UserProfile user;
	private final int[] acceptTime;
	private final int[] submitNumber;
	private int penalty = 0;
	private int solved = 0;
	public RankListEntry(int problemNumber) {		
		acceptTime = new int[problemNumber];
		submitNumber = new int[problemNumber];
		for (int i = 0; i < problemNumber; ++i) {
			acceptTime[i] = -1;
		}
	}
	public int getSolved() {
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
	public int compareTo(Object obj) {
		if (!(obj instanceof RankListEntry)) {
			throw new ClassCastException("obj is not type of RankListEntry");
		}
		RankListEntry entry = (RankListEntry) obj;
		if (entry.solved == this.solved) {
			return this.penalty - entry.penalty;
		} else {
			return entry.solved - this.solved;
		}
	}
	
}
