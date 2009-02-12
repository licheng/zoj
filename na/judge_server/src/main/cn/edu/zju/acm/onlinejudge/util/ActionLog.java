package cn.edu.zju.acm.onlinejudge.util;

import java.util.Date;

public class ActionLog {
	private String action;
	private Date timeStart;
	private Date timeEnd;
	private long count;
	private long maxAccessTime;
	private long minAccessTime;
	private long avgAccessTime;
	
	public ActionLog() {
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}

	public Date getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getMaxAccessTime() {
		return maxAccessTime;
	}

	public void setMaxAccessTime(long maxAccessTime) {
		this.maxAccessTime = maxAccessTime;
	}

	public long getMinAccessTime() {
		return minAccessTime;
	}

	public void setMinAccessTime(long minAccessTime) {
		this.minAccessTime = minAccessTime;
	}

	public long getAvgAccessTime() {
		return avgAccessTime;
	}

	public void setAvgAccessTime(long avgAccessTime) {
		this.avgAccessTime = avgAccessTime;
	}
	
	
}
