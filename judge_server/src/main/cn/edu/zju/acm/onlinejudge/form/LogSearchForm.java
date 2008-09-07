/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.form;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessage;

import cn.edu.zju.acm.onlinejudge.bean.request.LogCriteria;
import cn.edu.zju.acm.onlinejudge.util.Utility;


public class LogSearchForm extends ActionForm implements Serializable {
	
    private String userId;

    private String handle;

    private String timeStart;

    private String timeEnd;
    
    private String action;

    private String ip;
    
    private String page;
    
    private String type;
    
    private String orderBy;

    public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LogSearchForm() {    	
    }
 
    public ActionErrors check() {    	
       
        ActionErrors errors = new ActionErrors();
        
        checkLong(errors, userId, "idStart", "LogSearchForm.userId.invalid");
        checkTime(errors, timeStart, "timeStart", "LogSearchForm.timeStart.invalid");
        checkTime(errors, timeEnd, "timeEnd", "LogSearchForm.timeEnd.invalid");
                        
        return errors;
    }
    
    private void checkLong(ActionErrors errors, String value, String key, String message) {
    	if (value != null && value.trim().length() > 0) {    		 
    		try {
    			long l = Long.parseLong(value);
    			if (l < 0) {
    				errors.add(key, new ActionMessage(message));
    			}
    		} catch (Exception e) {
    			errors.add(key, new ActionMessage(message));
    		}
    	}
    }
    
    private void checkTime(ActionErrors errors, String value, String key, String message) {
    	if (value != null && value.trim().length() > 0) {
	    	if (!Utility.validateTimestamp(value)) {
	            errors.add(key, new ActionMessage(message));
	        }
    	}
    }
    
    
    public Map<String, String> toParameterMap() {
    	Map<String, String> p = new HashMap<String, String>();
    	int pageNumber = Utility.parseInt(page);
    	if (pageNumber > 1) {
    		p.put("page", "" + pageNumber);
    	}
    	long uid = Utility.parseLong(userId);
    	if (uid > 1) {
    		p.put("userId", "" + uid);
    	}
    	if (action != null && action.trim().length() > 0) {
    		p.put("action", action);
    	}
    	if (handle != null && handle.trim().length() > 0) {
    		p.put("handle", handle);
    	}
    	if (ip != null && ip.trim().length() > 0) {
    		p.put("ip", ip);
    	}
    	if (type != null && type.trim().length() > 0) {
    		p.put("type", type);
    	}
    	if (orderBy != null && orderBy.trim().length() > 0) {
    		p.put("orderBy", orderBy);
    	}
    	
    	if (timeStart != null && timeStart.trim().length() > 0) {
    		p.put("timeStart", timeStart);
    	}
    	if (timeEnd != null && timeEnd.trim().length() > 0) {
    		p.put("timeEnd", timeEnd);
    	}

    	return p;
    }
    public LogCriteria toLogCriteria() throws ParseException, NumberFormatException {
    	
    	LogCriteria criteria = new LogCriteria();
    	if (userId != null && userId.trim().length() > 0) {
    		criteria.setUserId(Long.valueOf(userId.trim()));
    	}
    	if (handle != null && handle.trim().length() > 0) {
    		criteria.setHandle(handle);
    	}
    	if (timeStart != null && timeStart.trim().length() > 0) {
    		criteria.setTimeStart(Utility.parseTimestamp(timeStart));
    	}
    	if (timeEnd != null && timeEnd.trim().length() > 0) {
    		criteria.setTimeEnd(Utility.parseTimestamp(timeEnd));
    	}
    	if (action != null && action.trim().length() > 0) {
    		criteria.setAction(action);
    	}
    	if (ip != null && ip.trim().length() > 0) {
    		criteria.setIp(ip);
    	}

    	return criteria;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHandle() {
		return handle;
	}


	public void setHandle(String handle) {
		this.handle = handle;
	}


	public String getTimeStart() {
		return timeStart;
	}


	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}


	public String getTimeEnd() {
		return timeEnd;
	}


	public void setTimeEnd(String timeEnd) {
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

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
    
    
}
