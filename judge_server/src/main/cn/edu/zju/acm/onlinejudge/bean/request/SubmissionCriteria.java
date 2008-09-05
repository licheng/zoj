/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean.request;

import java.util.Date;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;


/**
 * <p>SubmissionCriteria.</p>
 * 
 * @version 2.0
 * @author ZOJDEV
 */
public class SubmissionCriteria {
	
    /**
     * <p>Represents contestId.</p>
     */
    private Long contestId;

    /**
     * <p>Represents problemId.</p>
     */
    private Long problemId;

    /**
     * <p>Represents problemCode.</p>
     */
    private String problemCode;
    
    /**
     * 
     */
    private Long userId;
    
    /**
     * <p>Represents handle.</p>
     */
    private String handle;

    /**
     * <p>Represents idStart.</p>
     */
    private Long idStart;

    /**
     * <p>Represents idEnd.</p>
     */
    private Long idEnd;

    /**
     * <p>Represents timeStart.</p>
     */
    private Date timeStart;

    /**
     * <p>Represents timeEnd.</p>
     */
    private Date timeEnd;

    /**
     * <p>Represents judgeReplies.</p>
     */
    private List<JudgeReply> judgeReplies;

    /**
     * <p>Represents languages.</p>
     */
    private List<Language> languages;

    /**
     * SubmissionCriteria.
     */
    public SubmissionCriteria() {    	
    }
    
    /**
     * @param problemId The problemId to set.
     */
    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    /**
     * @return Returns the problemId.
     */
    public Long getProblemId() {
        return problemId;
    }

    /**
     * @param problemCode The problemCode to set.
     */
    public void setProblemCode(String problemCode) {
        this.problemCode = problemCode;
    }

    /**
     * @return Returns the problemCode.
     */
    public String getProblemCode() {
        return problemCode;
    }
    
    /**
     * @param contestId The contestId to set.
     */
    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    /**
     * @return Returns the contestId.
     */
    public Long getContestId() {
        return contestId;
    }

    /**
     * @param handle The handle to set.
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }

    /**
     * @return Returns the handle.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * @param idStart The idStart to set.
     */
    public void setIdStart(Long idStart) {
        this.idStart = idStart;
    }

    /**
     * @return Returns the idStart.
     */
    public Long getIdStart() {
        return idStart;
    }

    /**
     * @param idEnd The idEnd to set.
     */
    public void setIdEnd(Long idEnd) {
        this.idEnd = idEnd;
    }

    /**
     * @return Returns the idEnd.
     */
    public Long getIdEnd() {
        return idEnd;
    }

    /**
     * @param timeStart The timeStart to set.
     */
    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    /**
     * @return Returns the timeStart.
     */
    public Date getTimeStart() {
        return timeStart;
    }

    /**
     * @param timeEnd The timeEnd to set.
     */
    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    /**
     * @return Returns the timeEnd.
     */
    public Date getTimeEnd() {
        return timeEnd;
    }

    /**
     * @param judgeReplies The judgeReplies to set.
     */
    public void setJudgeReplies(List<JudgeReply> judgeReplies) {
        this.judgeReplies = judgeReplies;
    }

    /**
     * @return Returns the judgeReplies.
     */
    public List<JudgeReply> getJudgeReplies() {
        return judgeReplies;
    }

    /**
     * @param languages The languages to set.
     */
    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    /**
     * @return Returns the languages.
     */
    public List<Language> getLanguages() {
        return languages;
    }
    
    public int hashCode() {
    	int hash = 0;
    	hash = cal(contestId, hash);
    	hash = cal(problemId, hash);
    	hash = cal(handle, hash);
    	hash = cal(problemCode, hash);
    	hash = cal(idStart, hash);
    	hash = cal(idEnd, hash);
    	hash = cal(timeStart, hash);
    	hash = cal(timeEnd, hash);
    	hash = cal(judgeReplies, hash);
    	hash = cal(languages, hash);
    	hash = cal(userId, hash);
    	/*
    	if (judgeReplies != null) {
    		for (Iterator it = judgeReplies.iterator(); it.hasNext();) {
    			hash = cal(it.next(), hash);
    		}
    	}
    	if (judgeReplies != null) {
    		for (Iterator it = languages.iterator(); it.hasNext();) {
    			hash = cal(it.next(), hash);
    		}
    	}*/
        
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
    	if (!(obj instanceof SubmissionCriteria)) {
    		return false;
    	}
    	SubmissionCriteria that = (SubmissionCriteria) obj;
    	if (!equals(this.contestId, that.contestId)) {
    		return false;
    	}
    	if (!equals(this.problemId, that.problemId)) {
    		return false;
    	}
    	if (!equals(this.problemCode, that.problemCode)) {
    		return false;
    	}
    	if (!equals(this.idStart, that.idStart)) {
    		return false;
    	}
    	if (!equals(this.idEnd, that.idEnd)) {
    		return false;
    	}
    	if (!equals(this.timeStart, that.timeStart)) {
    		return false;
    	}
    	if (!equals(this.timeEnd, that.timeEnd)) {
    		return false;
    	}
    	if (!equals(this.judgeReplies, that.judgeReplies)) {
    		return false;
    	}
    	if (!equals(this.languages, that.languages)) {
    		return false;
    	}
    	if (!equals(this.userId, that.userId)) {
    		return false;
    	}
    	if (!equals(this.handle, that.handle)) {
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
