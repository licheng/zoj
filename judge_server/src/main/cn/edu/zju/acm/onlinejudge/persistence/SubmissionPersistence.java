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

package cn.edu.zju.acm.onlinejudge.persistence;

import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.QQ;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;
import cn.edu.zju.acm.onlinejudge.util.ProblemStatistics;
import cn.edu.zju.acm.onlinejudge.util.ProblemsetRankList;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.UserStatistics;

import java.util.List;

/**
 * <p>SubmissionPersistence interface defines the API used to manager the submission related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public interface SubmissionPersistence {

    /**
     * <p>Creates the specified submission in persistence layer.</p>
     *
     * @param submission the Submission instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void createSubmission(Submission submission, long user) throws PersistenceException;

    /**
     * <p>Updates the specified submission in persistence layer.</p>
     *
     * @param submission the Submission instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void updateSubmission(Submission submission, long user) throws PersistenceException;

    /**
     * <p>Deletes the specified submission in persistence layer.</p>
     *
     * @param id the id of the submission to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void deleteSubmission(long id, long user) throws PersistenceException;

    /**
     * <p>Gets the submission with given id in persistence layer.</p>
     *
     * @param id the id of the submission
     * @return the submission with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    Submission getSubmission(long id) throws PersistenceException;

    /**
     * <p>Searches all submissions according with the given criteria in persistence layer.</p>
     *
     * @return a list of submissions according with the given criteria
     * @param criteria the submission search criteria
     * @param lastId the last id
     * @param count the maximum number of submissions in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List<Submission> searchSubmissions(SubmissionCriteria criteria, long firstId, long lastId, int count) throws PersistenceException;
    
    List<Submission> searchSubmissions(SubmissionCriteria criteria, long firstId, long lastId, int count, boolean withContent) throws PersistenceException;

    /**
     * <p>Creates the specified judge reply in persistence layer.</p>
     *
     * @param reply the JudgeReply instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void createJudgeReply(JudgeReply reply, long user) throws PersistenceException;

    /**
     * <p>Updates the specified judge reply in persistence layer.</p>
     *
     * @param reply the JudgeReply instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void updateJudgeReply(JudgeReply reply, long user) throws PersistenceException;

    /**
     * <p>Deletes the specified judge reply in persistence layer.</p>
     *
     * @param id the id of the judge reply to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void deleteJudgeReply(long id, long user) throws PersistenceException;

    /**
     * <p>Gets the judge reply with given id in persistence layer.</p>
     *
     * @param id the id of the judge reply
     * @return the judge reply with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    JudgeReply getJudgeReply(long id) throws PersistenceException;

    /**
     * <p>Gets all judge replies in persistence layer.</p>
     *
     * @return a list of JudgeReply instances containing all judge replies in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List<JudgeReply> getAllJudgeReplies() throws PersistenceException;

    ContestStatistics getContestStatistics(List<Problem> problems) throws PersistenceException;
    
    ProblemStatistics getProblemStatistics(long problemId, String orderBy, int count) throws PersistenceException;
    
    List<RankListEntry> getRankList(List<Problem> problems, long contestStartDate) throws PersistenceException;
    
    List<RankListEntry> getRankList(List<Problem> problems, long contestStartDate, long roleId) throws PersistenceException;
    
    ProblemsetRankList getProblemsetRankList(long contestId, int offset, int count) throws PersistenceException;
    
    
    UserStatistics getUserStatistics(long contestId, long userId) throws PersistenceException;
    
    RankListEntry getRankListEntry(long contestId, long userId) throws PersistenceException;
    
    
    void changeQQStatus(long pid, long uid, String status) throws PersistenceException;
    List<QQ> searchQQs(long contestId) throws PersistenceException;

    String getSubmissionSource(long id) throws PersistenceException;
}


