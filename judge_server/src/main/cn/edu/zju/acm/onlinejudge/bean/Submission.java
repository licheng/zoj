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

import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;

import java.util.Date;

/**
 * <p>Submission bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class Submission {

	private long contestId = -1;
	private long contestOrder = -1;
	
    /**
     * <p>Represents id.</p>
     */
    private boolean finished = false;
    
    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents problemId.</p>
     */
    private long problemId = -1;

    /**
     * <p>Represents language.</p>
     */
    private Language language = null;

    /**
     * <p>Represents judgeReply.</p>
     */
    private JudgeReply judgeReply = null;

    /**
     * <p>Represents userProfileId.</p>
     */
    private long userProfileId = -1;

    /**
     * <p>Represents timeConsumption.</p>
     */
    private int timeConsumption = -1;

    /**
     * <p>Represents memoryConsumption.</p>
     */
    private int memoryConsumption = -1;

    /**
     * <p>Represents submitDate.</p>
     */
    private Date submitDate = null;

    /**
     * <p>Represents judgeDate.</p>
     */
    private Date judgeDate = null;
    
    /**
     * <p>Represents userName.</p>
     */
    private String userName = null;
    
     
    /**
     * <p>Represents problemCode.</p>
     */
    private String problemCode = null;

    /**
     * <p>Empty constructor.</p>
     */
    public Submission() {
    }

    /**
     * <p>Gets id.</p>
     *
     * @return id
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>Sets id.</p>
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>Gets problemId.</p>
     *
     * @return problemId
     */
    public long getProblemId() {
        return this.problemId;
    }

    /**
     * <p>Sets problemId.</p>
     *
     * @param problemId problemId
     */
    public void setProblemId(long problemId) {
        this.problemId = problemId;
    }

    /**
     * <p>Gets language.</p>
     *
     * @return language
     */
    public Language getLanguage() {
        return this.language;
    }

    /**
     * <p>Sets language.</p>
     *
     * @param language language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * <p>Gets judgeReply.</p>
     *
     * @return judgeReply
     */
    public JudgeReply getJudgeReply() {
        return this.judgeReply;
    }

    /**
     * <p>Sets judgeReply.</p>
     *
     * @param judgeReply judgeReply
     */
    public void setJudgeReply(JudgeReply judgeReply) {
        this.judgeReply = judgeReply;
    }

    /**
     * <p>Gets userProfileId.</p>
     *
     * @return userProfileId
     */
    public long getUserProfileId() {
        return this.userProfileId;
    }

    /**
     * <p>Sets userProfileId.</p>
     *
     * @param userProfileId userProfileId
     */
    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
    }

    /**
     * <p>Gets timeConsumption.</p>
     *
     * @return timeConsumption
     */
    public int getTimeConsumption() {
        return this.timeConsumption;
    }

    /**
     * <p>Sets timeConsumption.</p>
     *
     * @param timeConsumption timeConsumption
     */
    public void setTimeConsumption(int timeConsumption) {
        this.timeConsumption = timeConsumption;
    }

    /**
     * <p>Gets memoryConsumption.</p>
     *
     * @return memoryConsumption
     */
    public int getMemoryConsumption() {
        return this.memoryConsumption;
    }

    /**
     * <p>Sets memoryConsumption.</p>
     *
     * @param memoryConsumption memoryConsumption
     */         
    public void setMemoryConsumption(int memoryConsumption) {
        this.memoryConsumption = memoryConsumption;
    }

    /**
     * <p>Gets submitDate.</p>
     *
     * @return submitDate
     */
    public Date getSubmitDate() {
        return this.submitDate;
    }

    /**
     * <p>Sets submitDate.</p>
     *
     * @param submitDate submitDate
     */
    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    /**
     * <p>Gets judgeDate.</p>
     *
     * @return judgeDate
     */
    public Date getJudgeDate() {
        return this.judgeDate;
    }

    /**
     * <p>Sets judgeDate.</p>
     *
     * @param judgeDate judgeDate
     */
    public void setJudgeDate(Date judgeDate) {
        this.judgeDate = judgeDate;
    }
    
    /**
     * <p>Gets userName.</p>
     *
     * @return userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * <p>Sets userName.</p>
     *
     * @param userName userName
     */
    public void setUserName(String userName) {
        this.userName = userName;        
    }

    /**
     * <p>Gets problemCode.</p>
     *
     * @return problemCode
     */
    public String getProblemCode() {
        return this.problemCode;
    }

    /**
     * <p>Sets problemCode.</p>
     *
     * @param problemCode problemCode
     */
    public void setProblemCode(String problemCode) {
        this.problemCode = problemCode;
    }
    
    /**
     * <p>Represents content.</p>
     */
    private String content = null;

    /**
     * <p>Represents judgeComment.</p>
     */
    private String judgeComment = null;

    /**
     * <p>Gets content.</p>
     *
     * @return content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * <p>Sets content.</p>
     *
     * @param content content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * <p>Gets judgeComment.</p>
     *
     * @return judgeComment
     */
    public String getJudgeComment() {
        return this.judgeComment;
    }

    /**
     * <p>Sets judgeComment.</p>
     *
     * @param judgeComment judgeComment
     */
    public void setJudgeComment(String judgeComment) {
        this.judgeComment = judgeComment;
    }

    
    
    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

	public long getContestId() {
		return contestId;
	}

	public void setContestId(long contestId) {
		this.contestId = contestId;
	}

	public long getContestOrder() {
		return contestOrder;
	}

	public void setContestOrder(long contestOrder) {
		this.contestOrder = contestOrder;
	}

}
