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
        return this.page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LogSearchForm() {}

    public ActionErrors check() {

        ActionErrors errors = new ActionErrors();

        this.checkLong(errors, this.userId, "idStart", "LogSearchForm.userId.invalid");
        this.checkTime(errors, this.timeStart, "timeStart", "LogSearchForm.timeStart.invalid");
        this.checkTime(errors, this.timeEnd, "timeEnd", "LogSearchForm.timeEnd.invalid");

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
        int pageNumber = Utility.parseInt(this.page);
        if (pageNumber > 1) {
            p.put("page", "" + pageNumber);
        }
        long uid = Utility.parseLong(this.userId);
        if (uid > 1) {
            p.put("userId", "" + uid);
        }
        if (this.action != null && this.action.trim().length() > 0) {
            p.put("action", this.action);
        }
        if (this.handle != null && this.handle.trim().length() > 0) {
            p.put("handle", this.handle);
        }
        if (this.ip != null && this.ip.trim().length() > 0) {
            p.put("ip", this.ip);
        }
        if (this.type != null && this.type.trim().length() > 0) {
            p.put("type", this.type);
        }
        if (this.orderBy != null && this.orderBy.trim().length() > 0) {
            p.put("orderBy", this.orderBy);
        }

        if (this.timeStart != null && this.timeStart.trim().length() > 0) {
            p.put("timeStart", this.timeStart);
        }
        if (this.timeEnd != null && this.timeEnd.trim().length() > 0) {
            p.put("timeEnd", this.timeEnd);
        }

        return p;
    }

    public LogCriteria toLogCriteria() throws ParseException, NumberFormatException {

        LogCriteria criteria = new LogCriteria();
        if (this.userId != null && this.userId.trim().length() > 0) {
            criteria.setUserId(Long.valueOf(this.userId.trim()));
        }
        if (this.handle != null && this.handle.trim().length() > 0) {
            criteria.setHandle(this.handle);
        }
        if (this.timeStart != null && this.timeStart.trim().length() > 0) {
            criteria.setTimeStart(Utility.parseTimestamp(this.timeStart));
        }
        if (this.timeEnd != null && this.timeEnd.trim().length() > 0) {
            criteria.setTimeEnd(Utility.parseTimestamp(this.timeEnd));
        }
        if (this.action != null && this.action.trim().length() > 0) {
            criteria.setAction(this.action);
        }
        if (this.ip != null && this.ip.trim().length() > 0) {
            criteria.setIp(this.ip);
        }

        return criteria;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHandle() {
        return this.handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getTimeStart() {
        return this.timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return this.timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

}
