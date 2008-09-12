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

package cn.edu.zju.acm.onlinejudge.bean;

import java.util.Date;

public class QQ {
    public static final String QQ_NEW = "New";
    public static final String QQ_DELIVERING = "Delivering";
    public static final String QQ_FINISHED = "Finished";
    
    private long submissionId = -1;
    private Date submissionDate = null;
    
    private long userProfileId = -1;
    private String handle = null;
    private String nickName = null;
    
    
    private long problemId = -1;
    private String code = null;
    private String color = null;
    private String status = null;
    public QQ() {
        
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getHandle() {
        return handle;
    }
    public void setHandle(String handle) {
        this.handle = handle;
    }
    public long getProblemId() {
        return problemId;
    }
    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public long getSubmissionId() {
        return submissionId;
    }
    public void setSubmissionId(long submissionId) {
        this.submissionId = submissionId;
    }
    public Date getSubmissionDate() {
        return submissionDate;
    }
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    public long getUserProfileId() {
        return userProfileId;
    }
    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
    }
    
    
}

