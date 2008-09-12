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

package cn.edu.zju.acm.onlinejudge.bean.request;

import java.util.Date;

public class LogCriteria {
	
    private Long userId;
    
    private String handle;

    private Date timeStart;

    private Date timeEnd;
    
    private String action;

    private String ip;

    public LogCriteria() {    	
    }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int hashCode() {
    	int hash = 0;
    	hash = cal(userId, hash);
    	hash = cal(handle, hash);
    	hash = cal(timeStart, hash);
    	hash = cal(timeEnd, hash);
    	hash = cal(action, hash);
    	hash = cal(ip, hash);
        
    	return hash;    	
    }
    private int cal(Object obj, int hash) {
    	hash = (hash >>> 3);
    	if (obj == null) {
    		return hash ^ 1234567891;
    	} else {
    		return hash ^ obj.hashCode();
    	}
    }
    public boolean equals(Object obj) {
    	if (!(obj instanceof LogCriteria)) {
    		return false;
    	}
    	LogCriteria that = (LogCriteria) obj;
    	if (!equals(this.userId, that.userId)) {
    		return false;
    	}
    	if (!equals(this.timeStart, that.timeStart)) {
    		return false;
    	}
    	if (!equals(this.timeEnd, that.timeEnd)) {
    		return false;
    	}
    	if (!equals(this.handle, that.handle)) {
    		return false;
    	}
    	if (!equals(this.action, that.action)) {
    		return false;
    	}
    	if (!equals(this.ip, that.ip)) {
    		return false;
    	}
    	return true;
    	
    }
    private boolean equals(Object o1, Object o2) {
    	if (o1 == null) {
    		return o2 == null;
    	} 
    	return o1.equals(o2);
    }
}
