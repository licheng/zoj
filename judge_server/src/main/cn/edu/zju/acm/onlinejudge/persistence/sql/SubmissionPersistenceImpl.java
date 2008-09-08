/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import cn.edu.zju.acm.onlinejudge.bean.QQ;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;
import cn.edu.zju.acm.onlinejudge.util.ProblemStatistics;
import cn.edu.zju.acm.onlinejudge.util.ProblemsetRankList;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.UserStatistics;
									   

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;

/**
 * <p>SubmissionPersistenceImpl implements SubmissionPersistence interface.</p>
 * <p>SubmissionPersistence interface defines the API used to manager the submission related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class SubmissionPersistenceImpl implements SubmissionPersistence {
		    
	/**
	 * The statement to create a Submission.
	 */
	private static final String INSERT_SUBMISSION = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}," +
				" {12}, {13}, {14}, {15}, {16}, {17}) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)", 
							 new Object[] {DatabaseConstants.SUBMISSION_TABLE, 
				  						   DatabaseConstants.SUBMISSION_PROBLEM_ID,
				  						   DatabaseConstants.SUBMISSION_LANGUAGE_ID,
				  						   DatabaseConstants.SUBMISSION_JUDGE_REPLY_ID,
				  						   DatabaseConstants.SUBMISSION_USER_PROFILE_ID,
				  						   DatabaseConstants.SUBMISSION_CONTENT,
				  						   DatabaseConstants.SUBMISSION_TIME_CONSUMPTION,				  						   
				  						   DatabaseConstants.SUBMISSION_MEMORY_CONSUMPTION,
				  						   DatabaseConstants.SUBMISSION_SUBMISSION_DATE,				  						   
				  						   DatabaseConstants.SUBMISSION_JUDGE_DATE,
				  						   DatabaseConstants.SUBMISSION_JUDGE_COMMENT,
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,		
 				  						   "contest_id",
				  						   "contest_order",
				  						   DatabaseConstants.SUBMISSION_ACTIVE});

	/**
	 * The statement to update a Submission.
	 */
	private static final String UPDATE_SUBMISSION = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=?, {7}=?, {8}=?, "
				+ "{9}=?, {10}=?, {11}=?, {12}=? WHERE {13}=?", 
							 new Object[] {DatabaseConstants.SUBMISSION_TABLE,  
										   DatabaseConstants.SUBMISSION_PROBLEM_ID,
				  						   DatabaseConstants.SUBMISSION_LANGUAGE_ID,
				  						   DatabaseConstants.SUBMISSION_JUDGE_REPLY_ID,
				  						   DatabaseConstants.SUBMISSION_USER_PROFILE_ID,
				  						   DatabaseConstants.SUBMISSION_TIME_CONSUMPTION,				  						   
				  						   DatabaseConstants.SUBMISSION_MEMORY_CONSUMPTION,
				  						   DatabaseConstants.SUBMISSION_SUBMISSION_DATE,
				  						   DatabaseConstants.SUBMISSION_JUDGE_DATE,
				  						   DatabaseConstants.SUBMISSION_JUDGE_COMMENT,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.SUBMISSION_CONTENT,
				  						   DatabaseConstants.SUBMISSION_SUBMISSION_ID}); 
	
	/**
	 * The statement to update a Submission.
	 */
	private static final String UPDATE_SUBMISSION_WITHOUT_CONTENT = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=?, {7}=?, {8}=?, "
				+ "{9}=?, {10}=?, {11}=? WHERE {12}=?", 
							 new Object[] {DatabaseConstants.SUBMISSION_TABLE,  
										   DatabaseConstants.SUBMISSION_PROBLEM_ID,
				  						   DatabaseConstants.SUBMISSION_LANGUAGE_ID,
				  						   DatabaseConstants.SUBMISSION_JUDGE_REPLY_ID,
				  						   DatabaseConstants.SUBMISSION_USER_PROFILE_ID,
				  						   DatabaseConstants.SUBMISSION_TIME_CONSUMPTION,				  						   
				  						   DatabaseConstants.SUBMISSION_MEMORY_CONSUMPTION,
				  						   DatabaseConstants.SUBMISSION_SUBMISSION_DATE,
				  						   DatabaseConstants.SUBMISSION_JUDGE_DATE,
				  						   DatabaseConstants.SUBMISSION_JUDGE_COMMENT,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.SUBMISSION_SUBMISSION_ID}); 
	
	/**
	 * The statement to delete a submission.
	 */
	private static final String INACTIVE_SUBMISSION = 
		MessageFormat.format("UPDATE {0} SET {1}=0, {2}=?, {3}=? WHERE {4}=?", 
							 new Object[] {DatabaseConstants.SUBMISSION_TABLE, 
										   DatabaseConstants.SUBMISSION_ACTIVE,
										   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.SUBMISSION_SUBMISSION_ID}); 
	
	
	private static final String GET_SUBMISSION_PREFIX = 
		"SELECT s.submission_id,s.problem_id,s.language_id,s.judge_reply_id,s.user_profile_id,s.time_consumption," +
		"s.memory_consumption,s.submission_date,s.judge_date,s.judge_comment,s.contest_id,s.contest_order,u.handle,p.code";
	
	private static final String GET_SUBMISSION_WITH_CONTENT_PREFIX = GET_SUBMISSION_PREFIX + ",s.content";
	
	private static final String GET_SUBMISSION_FROM_PART =
		" FROM submission s FORCE_INDEX " +
		"LEFT JOIN user_profile u ON s.user_profile_id = u.user_profile_id " +
		"LEFT JOIN problem p ON s.problem_id = p.problem_id " +
		"WHERE s.active=1 AND u.active=1 AND p.active=1 ";
	
	private static final String GET_SUBMISSION = 
		GET_SUBMISSION_WITH_CONTENT_PREFIX + GET_SUBMISSION_FROM_PART + " AND s.submission_id=?";

	private static final String GET_SUBMISSIONS = 
		GET_SUBMISSION_PREFIX + GET_SUBMISSION_FROM_PART;
				 			 
	/**
	 * The query to get submissions.
	 */
	private static final String GET_SUBMISSIONS_WITH_CONTENT = 
		GET_SUBMISSION_WITH_CONTENT_PREFIX + GET_SUBMISSION_FROM_PART;
		
	/**
	 * The statement to create a judge_reply.
	 */
	private static final String INSERT_JUDGE_REPLY = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", 
							 new Object[] {DatabaseConstants.JUDGE_REPLY_TABLE, 
				  						   DatabaseConstants.JUDGE_REPLY_JUDGE_REPLY_ID,
				  						   DatabaseConstants.JUDGE_REPLY_NAME,
				  						   DatabaseConstants.JUDGE_REPLY_DESCRIPTION,
				  						   DatabaseConstants.JUDGE_REPLY_STYLE,
				  						   DatabaseConstants.JUDGE_REPLY_COMMITTED,				  						   
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE}); 
    
	/**
	 * The statement to update a judge_reply.
	 */
	private static final String UPDATE_JUDGE_REPLY = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=? WHERE {7}=?", 
							 new Object[] {DatabaseConstants.JUDGE_REPLY_TABLE, 
										   DatabaseConstants.JUDGE_REPLY_NAME,
										   DatabaseConstants.JUDGE_REPLY_DESCRIPTION,
										   DatabaseConstants.JUDGE_REPLY_STYLE,
				  						   DatabaseConstants.JUDGE_REPLY_COMMITTED,				  						   
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.JUDGE_REPLY_JUDGE_REPLY_ID}); 
	
	/**
	 * The statement to delete a judge_reply.
	 */
	private static final String DELETE_JUDGE_REPLY = 
		MessageFormat.format("DELETE FROM {0} WHERE {1}=?", 
				 new Object[] {DatabaseConstants.JUDGE_REPLY_TABLE, 
							   DatabaseConstants.JUDGE_REPLY_JUDGE_REPLY_ID});	
	/**
	 * The statement to delete a judge_reply.
	 */
	private static final String DELETE_SUBMISSION = 
		MessageFormat.format("DELETE FROM {0} WHERE {1}=?", 
				 new Object[] {DatabaseConstants.SUBMISSION_TABLE, 
							   DatabaseConstants.SUBMISSION_JUDGE_REPLY_ID});				
	
	/**
	 * The query to get a judge_reply.
	 */
	private static final String GET_JUDGE_REPLY = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4} FROM {5} WHERE {0}=?",
				 			 new Object[] {DatabaseConstants.JUDGE_REPLY_JUDGE_REPLY_ID, 
										   DatabaseConstants.JUDGE_REPLY_NAME,
										   DatabaseConstants.JUDGE_REPLY_DESCRIPTION,
										   DatabaseConstants.JUDGE_REPLY_STYLE,
				  						   DatabaseConstants.JUDGE_REPLY_COMMITTED,				  						   				  						   			
				   					       DatabaseConstants.JUDGE_REPLY_TABLE});
	
	/**
	 * The query to get all judge_replies.
	 */
	private static final String GET_ALL_JUDGE_REPLIES = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4} FROM {5}",
				 			 new Object[] {DatabaseConstants.JUDGE_REPLY_JUDGE_REPLY_ID, 
										   DatabaseConstants.JUDGE_REPLY_NAME,
										   DatabaseConstants.JUDGE_REPLY_DESCRIPTION,
										   DatabaseConstants.JUDGE_REPLY_STYLE,
				  						   DatabaseConstants.JUDGE_REPLY_COMMITTED,				  						   				  						   			
				   					       DatabaseConstants.JUDGE_REPLY_TABLE});
	
	
	/**
	 * The JudgeReplies cache.
	 */
	private static List<JudgeReply> allJudgeReplies = null;
	
    /**
     * <p>Creates the specified submission in persistence layer.</p>
     *
     * @param submission the Submission instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createSubmission(Submission submission, long user) throws PersistenceException {
    	checkSubmission(submission);
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();  
        	conn.setAutoCommit(false);
        	
        	String sql = "select max(contest_order) from submission where contest_id=" + submission.getContestId();
        	ps = conn.prepareStatement(sql);
        	rs = ps.executeQuery();
        	String maxOrder = null;
        	if (rs.next()) {
        		maxOrder = rs.getString(1);
        	}
        	long count = maxOrder == null ? 0 : Long.parseLong(maxOrder) + 1;
        	submission.setContestOrder(count);
        	
        	// create the submission
            ps = conn.prepareStatement(INSERT_SUBMISSION);            
            ps.setLong(1, submission.getProblemId());
            ps.setLong(2, submission.getLanguage().getId());
            ps.setLong(3, submission.getJudgeReply().getId());
            ps.setLong(4, submission.getUserProfileId());
            ps.setString(5, submission.getContent());
            ps.setString(10, submission.getJudgeComment());
            ps.setInt(6, submission.getTimeConsumption());
            ps.setInt(7, submission.getMemoryConsumption());
            ps.setTimestamp(8, Database.toTimestamp(submission.getSubmitDate()));
            ps.setTimestamp(9, Database.toTimestamp(submission.getJudgeDate()));            	
            ps.setLong(11, user);
            ps.setTimestamp(12, new Timestamp(new Date().getTime()));
            ps.setLong(13, user);
            ps.setTimestamp(14, new Timestamp(new Date().getTime()));
            ps.setLong(15, submission.getContestId());
            ps.setLong(16, submission.getContestOrder());
            ps.executeUpdate();  
            
            submission.setId(Database.getLastId(conn, ps, rs));
            conn.commit();
        } catch (PersistenceException pe) {
        	Database.rollback(conn);
        	throw pe;
		} catch (SQLException e) {
			Database.rollback(conn);
        	throw new PersistenceException("Failed to insert submission.", e);
		} finally {
			Database.dispose(conn, ps, null);
        }
    }

    /**
     * <p>Updates the specified submission in persistence layer.</p>
     *
     * @param submission the Submission instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateSubmission(Submission submission, long user) throws PersistenceException {
    	checkSubmission(submission);
    	
        Connection conn = null;
        PreparedStatement ps = null;
        try {
        	conn = Database.createConnection();    
        	conn.setAutoCommit(false);
        	// update the submission
            ps = conn.prepareStatement(submission.getContent() == null ? UPDATE_SUBMISSION_WITHOUT_CONTENT : UPDATE_SUBMISSION);            
            ps.setLong(1, submission.getProblemId());
            ps.setLong(2, submission.getLanguage().getId());
            ps.setLong(3, submission.getJudgeReply().getId());
            ps.setLong(4, submission.getUserProfileId());
            ps.setInt(5, submission.getTimeConsumption());
            ps.setInt(6, submission.getMemoryConsumption());
            ps.setTimestamp(7, Database.toTimestamp(submission.getSubmitDate()));
            ps.setTimestamp(8, Database.toTimestamp(submission.getJudgeDate()));            	
            ps.setString(9, submission.getJudgeComment());
            ps.setLong(10, user);
            ps.setTimestamp(11, new Timestamp(new Date().getTime()));
            if (submission.getContent() == null) {
            	ps.setLong(12, submission.getId());
            } else {
            	ps.setString(12, submission.getContent());
            	ps.setLong(13, submission.getId());
            }
            
            ps.executeUpdate();
            
            // TODO(ob): update the user statistics if no tiger?
            
            conn.commit();
        } catch (PersistenceException pe) {
        	Database.rollback(conn);
        	throw pe;
		} catch (SQLException e) {
			Database.rollback(conn);
        	throw new PersistenceException("Failed to update submission.", e);
		} finally {
			Database.dispose(conn, ps, null);
        }
    }

    /**
     * <p>Deletes the specified submission in persistence layer.</p>
     *
     * @param id the id of the submission to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteSubmission(long id, long user) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(INACTIVE_SUBMISSION);  
            ps.setLong(1, user);
            ps.setTimestamp(2, new Timestamp(new Date().getTime()));
            ps.setLong(3, id);
            
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such submission");
            }
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to delete submission.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        } 
    }

    /**
     * <p>Gets the submission with given id in persistence layer.</p>
     *
     * @param id the id of the submission
     * @return the submission with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public Submission getSubmission(long id) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	   
        try {
        	conn = Database.createConnection();
        	ps = conn.prepareStatement(GET_SUBMISSION.replace("FORCE_INDEX", ""));  
        	
        	ps.setLong(1, id);            
            rs = ps.executeQuery();
             
                        
            if (!rs.next()) {
            	return null;
            }
            
            Map<Long, Language> languageMap = new ContestPersistenceImpl().getLanguageMap();
            Map<Long, JudgeReply> judgeReplyMap = getJudgeReplyMap();
            
            Submission submission = populateSubmission(rs, true, languageMap, judgeReplyMap);
            return submission;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the submission with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }
    

    @Override
    public String getSubmissionSource(long id) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                   
        try {
            conn = Database.createConnection();
            ps = conn.prepareStatement("SELECT content FROM submission WHERE submission_id=?");   
            ps.setLong(1, id);
            rs = ps.executeQuery();
                        
            if (!rs.next()) {
                throw new PersistenceException("Submission id " + id + " not found");
            }
            String content = rs.getString("content");
            if (content == null) {
                return "";
            } else {
                return content;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the submission with id " + id, e);
        } finally {
            Database.dispose(conn, ps, rs);
        }
    }
    
    /**
     * Populates an ExtendedSubmission with given ResultSet.
     * 
     * @param rs
     * @return an ExtendedSubmission instance
     * @throws SQLException
     */
    private Submission populateSubmission(ResultSet rs, boolean withContent, 
    		Map<Long, Language> languageMap, Map<Long, JudgeReply> judgeReplyMap) throws SQLException {
        Submission submission = new Submission();
        
    	submission.setId(rs.getLong(DatabaseConstants.SUBMISSION_SUBMISSION_ID));
    	submission.setProblemId(rs.getLong(DatabaseConstants.SUBMISSION_PROBLEM_ID));
    	submission.setUserProfileId(rs.getLong(DatabaseConstants.SUBMISSION_USER_PROFILE_ID));
    	submission.setJudgeComment(rs.getString(DatabaseConstants.SUBMISSION_JUDGE_COMMENT));
    	submission.setJudgeDate(Database.getDate(rs,DatabaseConstants.SUBMISSION_JUDGE_DATE));
    	submission.setJudgeDate(Database.getDate(rs,DatabaseConstants.SUBMISSION_SUBMISSION_DATE));
    	submission.setSubmitDate(Database.getDate(rs,DatabaseConstants.SUBMISSION_SUBMISSION_DATE));
    	submission.setMemoryConsumption(rs.getInt(DatabaseConstants.SUBMISSION_MEMORY_CONSUMPTION));
    	submission.setTimeConsumption(rs.getInt(DatabaseConstants.SUBMISSION_TIME_CONSUMPTION));
        submission.setUserName(rs.getString(DatabaseConstants.USER_PROFILE_HANDLE));
    	submission.setProblemCode(rs.getString(DatabaseConstants.PROBLEM_CODE));
    	submission.setContestId(rs.getLong("contest_id"));
    	submission.setContestOrder(rs.getLong("contest_order"));
    	
    	if (withContent) {
    		submission.setContent(rs.getString("content"));
    	}
    	
        // set language
        long languageId = rs.getLong(DatabaseConstants.SUBMISSION_LANGUAGE_ID);            
        Language language = languageMap.get(languageId);
        submission.setLanguage(language);
        
        // set judge reply
        long judgeReplyId = rs.getLong(DatabaseConstants.SUBMISSION_JUDGE_REPLY_ID);
        JudgeReply judgeReply = judgeReplyMap.get(judgeReplyId);
        submission.setJudgeReply(judgeReply);                        	
    	
    	return submission;
    }
    
    /**
     * <p>Searches all submissions according with the given criteria in persistence layer.</p>
     *
     * @return a list of submissions according with the given criteria
     * @param criteria the submission search criteria
     * @param lastId the last id
     * @param count the maximum number of submissions in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<Submission> searchSubmissions(SubmissionCriteria criteria, long firstId, long lastId, int count) 
        throws PersistenceException {
    	return searchSubmissions(criteria, firstId, lastId, count, false);
    }
    /**
     * <p>Searches all submissions according with the given criteria in persistence layer.</p>
     *
     * @return a list of submissions according with the given criteria
     * @param criteria the submission search criteria
     * @param lastId the last id
     * @param count the maximum number of submissions in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<Submission> searchSubmissions(SubmissionCriteria criteria, long firstId, long lastId, int count, boolean withContent) 
        throws PersistenceException {
    	if (criteria == null) {
    		throw new NullPointerException("criteria is null");
    	}
    	if (lastId < 0) {
    		throw new IllegalArgumentException("offset is negative"); 
    	}
    	if (count < 0) {
    		throw new IllegalArgumentException("count is negative"); 
    	}
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            
        Map<Long, Language> languageMap = new ContestPersistenceImpl().getLanguageMap();
        Map<Long, JudgeReply> judgeReplyMap = getJudgeReplyMap();
        
        try {
        	conn = Database.createConnection();
        	if (criteria.getUserId() == null && criteria.getHandle() != null) {
        		String sql = "select user_profile_id from user_profile where handle=? AND active=1";
        		ps = conn.prepareStatement(sql);
        		ps.setString(1, criteria.getHandle());
        		rs = ps.executeQuery();
        		if (!rs.next()) {
        			return new ArrayList<Submission>();
        		}
        		long userId = rs.getLong(1);
        		criteria.setUserId(userId);
        	}
        	if (criteria.getProblemId() == null && criteria.getProblemCode() != null) {
        		String sql = "select problem_id from problem where code=? AND contest_id=? AND active=1";
        		ps = conn.prepareStatement(sql);
        		ps.setString(1, criteria.getProblemCode());
        		ps.setLong(2, criteria.getContestId());
        		rs = ps.executeQuery();
        		if (!rs.next()) {
        			return new ArrayList<Submission>();
        		}
        		long problemId = rs.getLong(1);
        		criteria.setProblemId(problemId);
        	}
        	ps = buildQuery(withContent ? GET_SUBMISSIONS_WITH_CONTENT : GET_SUBMISSIONS, criteria, firstId, lastId, count, conn, ps, rs);

        	if (ps == null) {
        		return new ArrayList<Submission>();
        	}
        	
        	rs = ps.executeQuery();
             
            List<Submission> submissions = new ArrayList<Submission>();
            while (rs.next()) {
                Submission submission = populateSubmission(rs, withContent, languageMap, judgeReplyMap);
            	submissions.add(submission);
            } 
                                         
            return submissions;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the submissions", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        } 

    }

    /**
     * Build search query.
     * 
     * @param criteria
     * @param lastId
     * @param count
     * @param conn
     * @param ps
     * @param rs
     * @return search query.
     * @throws SQLException 
     */
    private PreparedStatement buildQuery(String perfix, SubmissionCriteria criteria, long firstId, long lastId, int count,
    		Connection conn, PreparedStatement ps, ResultSet rs) throws SQLException {
    	
    	//String userIndex = "index_submission_user";
    	//String problemIndex = "index_submission_problem";
    	String userIndex = "index_submission_user_reply_contest";
    	String problemIndex = "index_submission_problem_reply";
    	
    	String judgeReplyIndex = "fk_submission_reply";
    	String defaultIndex = "index_submission_contest_order";
    	
    	Set<String> easyProblems = new HashSet<String>(
    			Arrays.asList(new String[] {"2060","1180","1067","1292","1295","1951","1025","2095","2105","1008","1005","1152","1240","2107",
    			"1037","1205","1113","1045","1489","1241","1101","1049","1057","1003","1151","1048","1002","1115","1001"}));
    	Set<JudgeReply> easyJudgeReply = new HashSet<JudgeReply>(Arrays.asList(new JudgeReply[] {
    			JudgeReply.ACCEPTED,
    			JudgeReply.WRONG_ANSWER,
    			JudgeReply.TIME_LIMIT_EXCEEDED,
    			JudgeReply.MEMORY_LIMIT_EXCEEDED,
    			JudgeReply.SEGMENTATION_FAULT,
    			JudgeReply.COMPILATION_ERROR,
    			JudgeReply.PRESENTATION_ERROR}));
    	
    	/*
    	 * INDEX optimization
    	 * If user id presents, use fk_submission_user
    	 * If problem id presents and submission number < 5000, use fk_submission_problem;
    	 * If judge_reply_id presents and none of id is 4,5,6,7,12,13 or 16, use fk_submission_reply when 
    	 * otherwise use index_submission_contest_order;
    	 */
    	String order = firstId == -1 ? "DESC" : "ASC";
    	if (criteria.getIdStart() != null && firstId < criteria.getIdStart() - 1) {
    		firstId = criteria.getIdStart() - 1;
    	}
    	
    	if (criteria.getIdEnd() != null && lastId > (criteria.getIdEnd() + 1)) {
    		lastId = criteria.getIdEnd() + 1;
    	}
    	
    	
    	StringBuffer query = new StringBuffer();
    	query.append(perfix);
    	query.append(" AND s.contest_id=" + criteria.getContestId());
    	query.append(" AND contest_order BETWEEN " +  (firstId+1) + " and " + (lastId-1));
    	
    	String index = null;
    	
    	if (criteria.getUserId() != null) {
    		query.append(" AND s.user_profile_id=" + criteria.getUserId());
    		index = userIndex;
    	}
    	
    	if (criteria.getProblemId() != null) {
    		query.append(" AND s.problem_id=" + criteria.getProblemId());
    		if (index == null && !easyProblems.contains(criteria.getProblemCode())) {
    			index = problemIndex;
    		}
    	}

    	String inCondition = null;
    	if (criteria.getJudgeReplies() != null) {
    		if (criteria.getJudgeReplies().size() == 0) {
    			return null;
    		}
    		List<Long> judgeRepliesIds = new ArrayList<Long>();
    		boolean easy = false;
    		for (JudgeReply judgeReply : criteria.getJudgeReplies()) {
    			judgeRepliesIds.add(judgeReply.getId());
    			if (easyJudgeReply.contains(judgeReply)) {
    				easy = true;
    			}
    		}
    		inCondition = " AND s.judge_reply_id IN " + Database.createNumberValues(judgeRepliesIds); 
    		query.append(inCondition);
    		if (index == null && !easy) {
    			if (criteria.getProblemId() != null) {
    				index = problemIndex;
    			} else {
    				index = judgeReplyIndex;
    			}
    		}
    	}
    	if (index == null && criteria.getJudgeReplies() != null && criteria.getProblemId() != null) {
    		String sql = "SELECT count(*) from submission s where problem_id=" + criteria.getProblemId() + inCondition;
    		ps = conn.prepareStatement(sql);
    		rs = ps.executeQuery();
    		rs.next();
    		long cnt = rs.getLong(1);
    		if (cnt < 10000) {
    			index = problemIndex;
    		}
    	}
    	
    	if (criteria.getLanguages() != null) {
    		if (criteria.getLanguages().size() == 0) {
    			return null;
    		}
    		List<Long> languageIds = new ArrayList<Long>();
    		for (Language language : criteria.getLanguages()) {
    			languageIds.add(language.getId());
    		}
    		query.append(" AND s.language_id IN " 
    				+ Database.createNumberValues(languageIds));
    	}
    	
    	query.append(" ORDER BY contest_order " + order);
    	query.append(" LIMIT " + count);
    	
    	if (index == null) {
    		index = defaultIndex;
    	}
    	
    	String queryString = query.toString().replace("FORCE_INDEX", "USE INDEX (" + index +")");
    	System.out.println(queryString);
    	
    	ps = conn.prepareStatement(queryString);
    	
		return ps;
	}   

    
    public ContestStatistics getContestStatistics(List problems) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;                        
        
        ContestStatistics statistics = new ContestStatistics(problems);
        if (problems.size() == 0) {
        	return statistics;
        }
        try {
        	conn = Database.createConnection();
        	List problemIds = new ArrayList();
        	for (Iterator it = problems.iterator(); it.hasNext();) {
        		problemIds.add(new Long(((Problem) it.next()).getId()));        		
        	}
        	String inProblemIds = Database.createNumberValues(problemIds);
        	String query = "SELECT problem_id, judge_reply_id, count(*) FROM submission " +
        			"WHERE problem_id IN " + inProblemIds + " GROUP BY problem_id, judge_reply_id";
        	/*
        	String query = "SELECT problem_id, judge_reply_id, count FROM problem_statistics " +
				"WHERE problem_id IN " + inProblemIds;
        	*/
        	
        	ps = conn.prepareStatement(query);
        	rs = ps.executeQuery();        	        	
        	
        	while (rs.next()) {
            	long problemId = rs.getLong(1);
            	long judgeReplyId = rs.getLong(2);
            	int value = rs.getInt(3);            	
            	statistics.setCount(problemId, judgeReplyId, value);
            } 
                                         
            return statistics;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the statistics", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }     	
    }
    
    public List getRankList(List problems, long contestStartDate) throws PersistenceException {
        return getRankList(problems, contestStartDate, -1);
    }
    
    public List getRankList(List problems, long contestStartDate, long roleId) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;                                
        if (problems.size() == 0) {
        	return new ArrayList();
        }
        try {
        	conn = Database.createConnection();
            
        	List problemIds = new ArrayList();
        	Map problemIndexes = new HashMap();
        	int index = 0;
        	for (Iterator it = problems.iterator(); it.hasNext();) {
        		Problem problem = (Problem) it.next();
        		problemIds.add(new Long(problem.getId()));  
        		problemIndexes.put(new Long(problem.getId()), new Integer(index));
        		index++;
        	}
            String userIdsCon = "";
            if (roleId >= 0) {
                // TODO performance issue!!
                List ids = new ArrayList();
                String userQuery = "SELECT user_profile_id FROM user_role WHERE role_id=?";
                ps = conn.prepareStatement(userQuery);
                ps.setLong(1, roleId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
                if (ids.size() == 0) {
                    return new ArrayList();
                }
                userIdsCon = " AND user_profile_id IN " + Database.createNumberValues(ids);                
            }                        
            
        	String inProblemIds = Database.createNumberValues(problemIds);
        	String query = "SELECT user_profile_id, problem_id, judge_reply_id, submission_date FROM submission " 
        			+ "WHERE problem_id IN " + inProblemIds + userIdsCon + " ORDER BY submission_date";
        	//System.out.println(query);
        	ps = conn.prepareStatement(query);
        	rs = ps.executeQuery();
        	
        	Map entries = new HashMap();      
        	
        	while (rs.next()) {
            	long userId = rs.getLong(1);
            	RankListEntry entry = (RankListEntry) entries.get(new Long(userId));
            	if (entry == null) {
            		entry = new RankListEntry(problems.size());
            		entries.put(new Long(userId), entry);
            		UserProfile profile = new UserProfile();
            		profile.setId(userId);
            		entry.setUserProfile(profile);
            	}            	
            	long problemId = rs.getLong(2);
            	long judgeReplyId = rs.getLong(3);
            	int time = (int) ((rs.getTimestamp(4).getTime() - contestStartDate) / 1000 / 60);
            	
            	entry.update(((Integer) problemIndexes.get(new Long(problemId))).intValue(), time, 
            			judgeReplyId == JudgeReply.ACCEPTED.getId());            	
            } 
            
            List entryList = new ArrayList(entries.values());
            Collections.sort(entryList);
                                         
            return entryList;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the rank list", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }     	
    }
    
    public RankListEntry getRankListEntry(long contestId, long userId) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        RankListEntry re=null;
        try {
        	conn = Database.createConnection();
            
        	String query = "SELECT ac_number, submission_number FROM user_stat " 
        			+ "WHERE contest_id=? AND user_id=?";
        	ps = conn.prepareStatement(query);
        	ps.setLong(1, contestId);
        	ps.setLong(2, userId);
        	rs = ps.executeQuery();
        	if (rs.next()) {
            	re=new RankListEntry(1);
            	re.setSolved(rs.getLong(1));
            	re.setSubmitted(rs.getLong(2));
            }
                                         
            return re;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the rank list", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }     	
    }
    
    public ProblemsetRankList getProblemsetRankList(long contestId, int offset, int count) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();
            String sql = "SELECT u.user_profile_id, u.handle, u.nickname, up.plan, ua.solved, ua.tiebreak " +
            		"FROM user_ac ua " +
            		"LEFT JOIN user_profile u ON ua.user_profile_id = u.user_profile_id " +
            		"LEFT JOIN user_preference up ON ua.user_profile_id = up.user_profile_id " +
            		"WHERE contest_id=? ORDER BY ua.solved DESC, ua.tiebreak ASC " +
            		"LIMIT " + offset + "," + count;
            
            ps = conn.prepareStatement(sql);
            ps.setLong(1, contestId);
            
            rs = ps.executeQuery();
            int index = 0;
            List<UserProfile> users = new ArrayList<UserProfile>();
            List<Integer> solved = new ArrayList<Integer>();
            List<Integer> total = new ArrayList<Integer>();
            
            while (rs.next()) {
                UserProfile user = new UserProfile();
                user.setId(rs.getLong(1));
                user.setHandle(rs.getString(2));
                user.setNickName(rs.getString(3));
                user.setDeclaration(rs.getString(4));
                users.add(user);
                solved.add(rs.getInt(5));
                total.add(rs.getInt(6));
            }
            
            int[] solvedArray = new int[solved.size()];
            int[] totalArray = new int[solved.size()];
            for (int i = 0; i < solvedArray.length; ++i) {
            	solvedArray[i] = solved.get(i);
            	totalArray[i] = total.get(i);
            }
            
            ProblemsetRankList r = new ProblemsetRankList(offset, count);
            r.setUsers(users.toArray(new UserProfile[0]));
            r.setSolved(solvedArray);
            r.setTotal(totalArray);
            return r;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the rank list", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }     	
    }
    
    public UserStatistics getUserStatistics(long contestId, long userId) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;                        
        
        try {
        	UserStatistics statistics = new UserStatistics(userId, contestId);
        	
        	
        	conn = Database.createConnection();
        	String sql = "SELECT DISTINCT p.problem_id, p.code, p.title " +
        			GET_SUBMISSION_FROM_PART + " AND s.user_profile_id=? AND s.judge_reply_id=? AND s.contest_id=?";
        	sql = sql.replace("FORCE_INDEX", "USE INDEX (index_submission_user_reply_contest)");
        	
        	System.out.println(sql);
        	ps = conn.prepareStatement(sql);
        	ps.setLong(1, userId);
        	ps.setLong(2, JudgeReply.ACCEPTED.getId());
        	ps.setLong(3, contestId);
        	rs = ps.executeQuery();
        	
        	List<Problem> solved = new ArrayList<Problem>();
        	while (rs.next()) {
        		Problem p = new Problem();
        		p.setContestId(contestId);
        		p.setId(rs.getLong("problem_id"));
        		p.setCode(rs.getString("code"));
        		p.setTitle(rs.getString("title"));
        		solved.add(p);
            } 
        	statistics.setSolved(new TreeSet<Problem>(solved));
        	
        	//sql = "SELECT judge_reply_id, count FROM user_statistics WHERE user_profile_id=? AND contest_id=?";
        	sql = "SELECT judge_reply_id, count(*) FROM submission WHERE contest_id=? AND user_profile_id=? GROUP BY judge_reply_id";
        	ps = conn.prepareStatement(sql);
        	ps.setLong(1, contestId);
        	ps.setLong(2, userId);
        	rs = ps.executeQuery();
        	
        	while (rs.next()) {
        		long jid = rs.getLong(1);
        		int count = rs.getInt(2);
        		statistics.setCount(jid, count);
            } 
        	
        	return statistics;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the user statistics", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }     	
    }
    
	/**
     * <p>Creates the specified judge reply in persistence layer.</p>
     *
     * @param judgeReply the JudgeReply instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createJudgeReply(JudgeReply judgeReply, long user) throws PersistenceException {
    	checkJudgeReply(judgeReply);
    	
    	synchronized (this.getClass()) {
    		allJudgeReplies = null;
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
            ps = conn.prepareStatement(INSERT_JUDGE_REPLY);
            ps.setLong(1, judgeReply.getId());
            ps.setString(2, judgeReply.getName());
            ps.setString(3, judgeReply.getDescription());
            ps.setString(4, judgeReply.getStyle());
            ps.setBoolean(5, judgeReply.isCommitted());
            
            ps.setLong(6, user);
            ps.setTimestamp(7, new Timestamp(new Date().getTime()));
            ps.setLong(8, user);
            ps.setTimestamp(9, new Timestamp(new Date().getTime()));
            ps.executeUpdate();                               
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create judgeReply.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Updates the specified judge reply in persistence layer.</p>
     *
     * @param judgeReply the JudgeReply instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateJudgeReply(JudgeReply judgeReply, long user) throws PersistenceException {
    	checkJudgeReply(judgeReply);
    	
    	synchronized (this.getClass()) {
    		allJudgeReplies = null;
    	}
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(UPDATE_JUDGE_REPLY);  
            ps.setString(1, judgeReply.getName());
            ps.setString(2, judgeReply.getDescription());
            ps.setString(3, judgeReply.getStyle());
            ps.setBoolean(4, judgeReply.isCommitted());            
            ps.setLong(5, user);
            ps.setTimestamp(6, new Timestamp(new Date().getTime()));
            ps.setLong(7, judgeReply.getId());
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such judgeReply");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to update judgeReply.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }      
    }

    /**
     * <p>Deletes the specified judge reply in persistence layer.</p>
     *
     * @param id the id of the judge reply to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteJudgeReply(long id, long user) throws PersistenceException {
    	synchronized (this.getClass()) {
    		allJudgeReplies = null;
    	}
    	Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
        	conn.setAutoCommit(false);
        	
        	ps = conn.prepareStatement(DELETE_SUBMISSION);
        	ps.setLong(1, id);
        	ps.executeUpdate();             
        	
            ps = conn.prepareStatement(DELETE_JUDGE_REPLY);  
            ps.setLong(1, id);
            ps.executeUpdate();
            
            conn.commit();        
        } catch (PersistenceException pe) {
        	Database.rollback(conn);
        	throw pe;
		} catch (SQLException e) {
			Database.rollback(conn);
        	throw new PersistenceException("Failed to delete judgeReply.", e);
		} finally {
			Database.dispose(conn, ps, null);
        }   
    }

    /**
     * <p>Gets the judge reply with given id in persistence layer.</p>
     *
     * @param id the id of the judge reply
     * @return the judge reply with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public JudgeReply getJudgeReply(long id) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
        	ps = conn.prepareStatement(GET_JUDGE_REPLY);   
        	ps.setLong(1, id);
            rs = ps.executeQuery();
                
            if (rs.next()) {
            	JudgeReply judgeReply = new JudgeReply(rs.getLong(DatabaseConstants.JUDGE_REPLY_JUDGE_REPLY_ID),
            									 rs.getString(DatabaseConstants.JUDGE_REPLY_NAME),
            									 rs.getString(DatabaseConstants.JUDGE_REPLY_DESCRIPTION),
            									 rs.getString(DatabaseConstants.JUDGE_REPLY_STYLE),
            									 rs.getBoolean(DatabaseConstants.JUDGE_REPLY_COMMITTED));
            	return judgeReply;            	
            } else {
            	return null;
            }
        	
             	            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get all judgeReplies", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Gets all judge replies in persistence layer.</p>
     *
     * @return a list of JudgeReply instances containing all judge replies in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<JudgeReply> getAllJudgeReplies() throws PersistenceException {
    	synchronized (this.getClass()) {
    		if (allJudgeReplies != null) {
    			return new ArrayList<JudgeReply>(allJudgeReplies);
    		}
    	}
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
        	ps = conn.prepareStatement(GET_ALL_JUDGE_REPLIES);   
            rs = ps.executeQuery();
                
            List<JudgeReply> judgeReplies = new ArrayList<JudgeReply>();
            
            while (rs.next()) {
            	JudgeReply judgeReply = new JudgeReply(rs.getLong(DatabaseConstants.JUDGE_REPLY_JUDGE_REPLY_ID),
            									 rs.getString(DatabaseConstants.JUDGE_REPLY_NAME),
            									 rs.getString(DatabaseConstants.JUDGE_REPLY_DESCRIPTION),
            									 rs.getString(DatabaseConstants.JUDGE_REPLY_STYLE),
            									 rs.getBoolean(DatabaseConstants.JUDGE_REPLY_COMMITTED));
            	judgeReplies.add(judgeReply);
            }
        	
            allJudgeReplies = new ArrayList<JudgeReply>(judgeReplies);
            return judgeReplies;     	            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get all judgeReplies", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }  		    	    	
    }    
    
    /**
     * Checks the submission.
     * @param submission
     * @throws NullPointerException if submission is null
     * @throws IllegalArgumentException if submission is invalid
     */
    private void checkSubmission(Submission submission) {
    	if (submission == null) {
    		throw new NullPointerException("submission is null.");
    	}
    	if (submission.getLanguage() == null) {
    		throw new IllegalArgumentException("language is null");
    	}
    	if (submission.getJudgeReply() == null) {
    		throw new IllegalArgumentException("judge reply is null");
    	}    	    	
    }
    
    /**
     * Gets a Language Map. Language id is the key and Language itself is the value.
     * @return a Language Map
     * @throws PersistenceException
     */
    Map<Long, JudgeReply> getJudgeReplyMap() throws PersistenceException {
    	List<JudgeReply> judgeReplies = getAllJudgeReplies(); 
        Map<Long, JudgeReply> judgeReplyMap = new HashMap<Long, JudgeReply>();
        for (JudgeReply reply : judgeReplies) {
        	judgeReplyMap.put(reply.getId(), reply);        	
        }
        return judgeReplyMap;    	
    }
    
    /**
     * Checks the JudgeReply.
     * @param reply
     * @throws NullPointerException if reply is null
     * @throws IllegalArgumentException if reply is invalid
     */
    private void checkJudgeReply(JudgeReply reply) {
    	if (reply == null) {
    		throw new NullPointerException("reply is null.");
    	}
    	if (reply.getName() == null) {
    		throw new IllegalArgumentException("name is null");
    	}    	    	
    }       
    
    public void changeQQStatus(long pid, long uid, String status) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                    
        String insert = "INSERT INTO submission_status (problem_id, user_profile_id, status) VALUES (?,?,?)";
        String update = "UPDATE submission_status SET status=? WHERE problem_id=? AND user_profile_id=?";
                       
        try {
            conn = Database.createConnection();
            ps = conn.prepareStatement(update);
            ps.setString(1, status);
            ps.setLong(2, pid);   
            ps.setLong(3, uid);
         
            int changes = ps.executeUpdate();
            if (changes == 0) {
                ps = conn.prepareStatement(insert);
                ps.setLong(1, pid);
                ps.setLong(2, uid);
                ps.setString(3, status);
                ps.executeUpdate();
            }
             
           
        } catch (SQLException e) {
            throw new PersistenceException("Failed to update the QQs", e);
        } finally {
            Database.dispose(conn, ps, rs);
        } 

        
    }
    
    public List<QQ> searchQQs(long contestId) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                    
        String query = "SELECT s.submission_id, s.submission_date, " +
                       "u.user_profile_id, u.handle, u.nickname, " +
                       "p.problem_id, p.code, p.color, ss.status " +
                       "FROM submission s " +
                       "LEFT JOIN user_profile u ON s.user_profile_id=u.user_profile_id " + 
                       "LEFT JOIN problem p ON s.problem_id=p.problem_id " +                       
                       "LEFT JOIN submission_status ss ON u.user_profile_id=ss.user_profile_id AND p.problem_id=ss.problem_id " +
                       "WHERE p.contest_id=? AND s.judge_reply_id=? AND p.active=1 AND (ss.status IS NULL OR ss.status<>?) " + 
                       "ORDER BY s.submission_date";
        try {
            conn = Database.createConnection();
            ps = conn.prepareStatement(query);
            
            ps.setLong(1, contestId);
            ps.setLong(2, JudgeReply.ACCEPTED.getId());
            ps.setString(3, QQ.QQ_FINISHED);
            rs = ps.executeQuery();
             
            List<QQ> qqs = new ArrayList<QQ>();
            while (rs.next()) {
                QQ qq = new QQ();
                qq.setCode(rs.getString("code"));
                qq.setColor(rs.getString("color"));
                qq.setNickName(rs.getString("nickname"));
                qq.setHandle(rs.getString("handle"));
                qq.setProblemId(rs.getLong("problem_id"));
                qq.setUserProfileId(rs.getLong("user_profile_id"));
                qq.setSubmissionId(rs.getLong("submission_id"));
                qq.setSubmissionDate(Database.getDate(rs, "submission_date"));
                
                qq.setStatus(rs.getString("status"));
                if (qq.getStatus() == null) {
                    qq.setStatus(QQ.QQ_NEW);
                }
                
                qqs.add(qq);
            } 
                                         
            return qqs;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the QQs", e);
        } finally {
            Database.dispose(conn, ps, rs);
        } 

    }

	public ProblemStatistics getProblemStatistics(long problemId, String orderBy, int count) throws PersistenceException {
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String ob = null;
        ProblemStatistics ret = null;
        
        if ("time".equals(orderBy)) {
        	ob = "s.time_consumption ASC,memory_consumption ASC,s.submission_date ASC";
        	ret = new ProblemStatistics(problemId, "time");
        } else if ("memory".equals(orderBy)) {
        	ob = "s.memory_consumption ASC,s.time_consumption ASC,submission_date ASC";
        	ret = new ProblemStatistics(problemId, "memory");
        } else {
        	ob = "s.submission_date ASC,s.time_consumption ASC,memory_consumption ASC";
        	ret = new ProblemStatistics(problemId, "date");
        }
          
        Map<Long, Language> languageMap = new ContestPersistenceImpl().getLanguageMap();
        Map<Long, JudgeReply> judgeReplyMap = getJudgeReplyMap();
        
        try {
            conn = Database.createConnection();
            //String sql = "SELECT judge_reply_id, count FROM problem_statistics WHERE problem_id=?";
            String sql = "SELECT judge_reply_id, count(*) FROM submission WHERE problem_id=? GROUP BY judge_reply_id";
            ps = conn.prepareStatement(sql);
            
            ps.setLong(1, problemId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
            	long jid = rs.getLong(1);
            	int c = rs.getInt(2);
                ret.setCount(jid, c);
            } 
            
            sql = GET_SUBMISSIONS + " AND s.problem_id=? AND s.judge_reply_id=? ORDER BY " + ob + " LIMIT " + count;
            sql = sql.replace("FORCE_INDEX", "USE INDEX (index_submission_problem_reply)");
            System.out.println(sql);
            ps = conn.prepareStatement(sql);
            ps.setLong(1, problemId);
            ps.setLong(2, JudgeReply.ACCEPTED.getId());
            rs = ps.executeQuery();
            
            List<Submission> submissions = new ArrayList<Submission>();
            while (rs.next()) {
                Submission submission = populateSubmission(rs, false, languageMap, judgeReplyMap);
            	submissions.add(submission);
            } 
            
            ret.setBestRuns(submissions);
                                         
            return ret;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the QQs", e);
        } finally {
            Database.dispose(conn, ps, rs);
        }
	} 
}


