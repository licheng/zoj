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
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;

/**
 * <p>
 * SubmissionCriteria.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 */
public class SubmissionCriteria {

    /**
     * <p>
     * Represents contestId.
     * </p>
     */
    private Long contestId;

    /**
     * <p>
     * Represents problemId.
     * </p>
     */
    private Long problemId;

    /**
     * <p>
     * Represents problemCode.
     * </p>
     */
    private String problemCode;

    /**
     * 
     */
    private Long userId;

    /**
     * <p>
     * Represents handle.
     * </p>
     */
    private String handle;

    /**
     * <p>
     * Represents idStart.
     * </p>
     */
    private Long idStart;

    /**
     * <p>
     * Represents idEnd.
     * </p>
     */
    private Long idEnd;

    /**
     * <p>
     * Represents timeStart.
     * </p>
     */
    private Date timeStart;

    /**
     * <p>
     * Represents timeEnd.
     * </p>
     */
    private Date timeEnd;

    /**
     * <p>
     * Represents judgeReplies.
     * </p>
     */
    private List<JudgeReply> judgeReplies;

    /**
     * <p>
     * Represents languages.
     * </p>
     */
    private List<Language> languages;

    /**
     * SubmissionCriteria.
     */
    public SubmissionCriteria() {}

    /**
     * @param problemId
     *            The problemId to set.
     */
    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    /**
     * @return Returns the problemId.
     */
    public Long getProblemId() {
        return this.problemId;
    }

    /**
     * @param problemCode
     *            The problemCode to set.
     */
    public void setProblemCode(String problemCode) {
        this.problemCode = problemCode;
    }

    /**
     * @return Returns the problemCode.
     */
    public String getProblemCode() {
        return this.problemCode;
    }

    /**
     * @param contestId
     *            The contestId to set.
     */
    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    /**
     * @return Returns the contestId.
     */
    public Long getContestId() {
        return this.contestId;
    }

    /**
     * @param handle
     *            The handle to set.
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }

    /**
     * @return Returns the handle.
     */
    public String getHandle() {
        return this.handle;
    }

    /**
     * @param idStart
     *            The idStart to set.
     */
    public void setIdStart(Long idStart) {
        this.idStart = idStart;
    }

    /**
     * @return Returns the idStart.
     */
    public Long getIdStart() {
        return this.idStart;
    }

    /**
     * @param idEnd
     *            The idEnd to set.
     */
    public void setIdEnd(Long idEnd) {
        this.idEnd = idEnd;
    }

    /**
     * @return Returns the idEnd.
     */
    public Long getIdEnd() {
        return this.idEnd;
    }

    /**
     * @param timeStart
     *            The timeStart to set.
     */
    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    /**
     * @return Returns the timeStart.
     */
    public Date getTimeStart() {
        return this.timeStart;
    }

    /**
     * @param timeEnd
     *            The timeEnd to set.
     */
    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    /**
     * @return Returns the timeEnd.
     */
    public Date getTimeEnd() {
        return this.timeEnd;
    }

    /**
     * @param judgeReplies
     *            The judgeReplies to set.
     */
    public void setJudgeReplies(List<JudgeReply> judgeReplies) {
        this.judgeReplies = judgeReplies;
    }

    /**
     * @return Returns the judgeReplies.
     */
    public List<JudgeReply> getJudgeReplies() {
        return this.judgeReplies;
    }

    /**
     * @param languages
     *            The languages to set.
     */
    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    /**
     * @return Returns the languages.
     */
    public List<Language> getLanguages() {
        return this.languages;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = this.cal(this.contestId, hash);
        hash = this.cal(this.problemId, hash);
        hash = this.cal(this.handle, hash);
        hash = this.cal(this.problemCode, hash);
        hash = this.cal(this.idStart, hash);
        hash = this.cal(this.idEnd, hash);
        hash = this.cal(this.timeStart, hash);
        hash = this.cal(this.timeEnd, hash);
        hash = this.cal(this.judgeReplies, hash);
        hash = this.cal(this.languages, hash);
        hash = this.cal(this.userId, hash);

        return hash;
    }

    private int cal(Object obj, int hash) {
        hash = hash >>> 3;
        if (obj == null) {
            return hash ^ 1234567891;
        } else {
            return hash ^ obj.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubmissionCriteria)) {
            return false;
        }
        SubmissionCriteria that = (SubmissionCriteria) obj;
        if (!this.equals(this.contestId, that.contestId)) {
            return false;
        }
        if (!this.equals(this.problemId, that.problemId)) {
            return false;
        }
        if (!this.equals(this.problemCode, that.problemCode)) {
            return false;
        }
        if (!this.equals(this.idStart, that.idStart)) {
            return false;
        }
        if (!this.equals(this.idEnd, that.idEnd)) {
            return false;
        }
        if (!this.equals(this.timeStart, that.timeStart)) {
            return false;
        }
        if (!this.equals(this.timeEnd, that.timeEnd)) {
            return false;
        }
        if (!this.equals(this.judgeReplies, that.judgeReplies)) {
            return false;
        }
        if (!this.equals(this.languages, that.languages)) {
            return false;
        }
        if (!this.equals(this.userId, that.userId)) {
            return false;
        }
        if (!this.equals(this.handle, that.handle)) {
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
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
