/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;

import java.util.List;
import java.util.Set;

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
     * <p>Returns the number of all submissions according with the given criteria in persistence layer.</p>
     *
     * @return the number of all submissions according with the given criteria
     * @param criteria the submission search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of submissions in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    long searchSubmissionNumber(SubmissionCriteria criteria) throws PersistenceException;

    /**
     * <p>Searchs all submissions according with the given criteria in persistence layer.</p>
     *
     * @return a list of submissions according with the given criteria
     * @param criteria the submission search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of submissions in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List searchSubmissions(SubmissionCriteria criteria, int offset, int count) throws PersistenceException;

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
    List getAllJudgeReplies() throws PersistenceException;

    ContestStatistics getContestStatistics(List problems) throws PersistenceException;
    
    List getRankList(List problems, long contestStartDate) throws PersistenceException;
    
    List getRankList(List problems, long contestStartDate, long roleId) throws PersistenceException;
    
    
    Set getSolvedProblems(List problems, long userProfileId) throws PersistenceException;
    
    
    void changeQQStatus(long pid, long uid, String status) throws PersistenceException;
    List searchQQs(long contestId) throws PersistenceException;
}


