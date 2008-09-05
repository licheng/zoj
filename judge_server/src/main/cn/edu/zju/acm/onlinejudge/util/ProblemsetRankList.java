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
		return offset;
	}

	public int getCount() {
		return count;
	}

	public UserProfile[] getUsers() {
		return users;
	}

	public void setUsers(UserProfile[] users) {
		this.users = users;
	}

	public int[] getSolved() {
		return solved;
	}

	public void setSolved(int[] solved) {
		this.solved = solved;
	}

	public int[] getTotal() {
		return total;
	}

	public void setTotal(int[] total) {
		this.total = total;
	}

}
