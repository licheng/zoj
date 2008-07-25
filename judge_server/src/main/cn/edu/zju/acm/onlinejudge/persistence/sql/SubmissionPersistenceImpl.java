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
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
									   

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
				" {12}, {13}, {14}, {15}) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)", 
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
				  						   DatabaseConstants.SUBMISSION_ACTIVE});
	
	/**
	 * The statement to create a Submission.
	 */
	private static final String INSERT_SUBMISSION2 = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}," +
				" {12}, {13}, {14}, {15}) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)", 
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
				  						   DatabaseConstants.SUBMISSION_CONTENT,
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
	
	/**
	 * The query to get a submission.
	 */
	private static final String GET_SUBMISSION = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10} " 
				+ "FROM {11} WHERE {12}=1 AND {0}=?",
				 			 new Object[] {DatabaseConstants.SUBMISSION_SUBMISSION_ID, 
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
										   DatabaseConstants.SUBMISSION_TABLE,
				   					       DatabaseConstants.SUBMISSION_ACTIVE});
	
	/**
	 * The query to get submissions.
	 */
	private static final String GET_SUBMISSIONS = 
		MessageFormat.format("SELECT {0}, s.{1}, {2}, {3}, s.{4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12} " 
				+ "FROM {13} s LEFT JOIN {14} u ON s.{4} = u.{4} LEFT JOIN {15} p ON s.{1} = p.{1} "
				+ "WHERE s.{16}=1 AND u.{17}=1 AND p.{18}=1 ",
				 			 new Object[] {DatabaseConstants.SUBMISSION_SUBMISSION_ID, 
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
										   DatabaseConstants.USER_PROFILE_HANDLE,
										   DatabaseConstants.PROBLEM_CODE,
										   DatabaseConstants.SUBMISSION_TABLE,
										   DatabaseConstants.USER_PROFILE_TABLE,
										   DatabaseConstants.PROBLEM_TABLE,
										   DatabaseConstants.SUBMISSION_ACTIVE,
										   DatabaseConstants.USER_PROFILE_ACTIVE,
										   DatabaseConstants.PROBLEM_ACTIVE});
	
	/**
	 * The query to get submission number.
	 */
	private static final String GET_SUBMISSION_NUMBER = 
		MessageFormat.format("SELECT COUNT(*) FROM {0} s LEFT JOIN {1} u ON s.{2} = u.{2} LEFT JOIN {3} p ON s.{4} = p.{4} WHERE s.{5}=1 AND u.{6}=1 AND p.{7}=1 ",
				 			 new Object[] {DatabaseConstants.SUBMISSION_TABLE,
										   DatabaseConstants.USER_PROFILE_TABLE,
										   DatabaseConstants.SUBMISSION_USER_PROFILE_ID,
										   DatabaseConstants.PROBLEM_TABLE,
										   DatabaseConstants.SUBMISSION_PROBLEM_ID,
				   					       DatabaseConstants.SUBMISSION_ACTIVE,
				   					       DatabaseConstants.USER_PROFILE_ACTIVE,
										   DatabaseConstants.PROBLEM_ACTIVE});
	
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
	private static List allJudgeReplies = null;
	
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
            ps.executeUpdate();                                               
            submission.setId(Database.getLastId(conn, ps, rs));
                        
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create submission.", e);
		} finally {			
        	Database.dispose(conn, ps, rs);
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
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();           	
					   
            // create the submission
            ps = conn.prepareStatement(UPDATE_SUBMISSION);            
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
            ps.setLong(13, submission.getId());
            ps.executeUpdate();                                               
               
                        
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create submission.", e);
		} finally {			
        	Database.dispose(conn, ps, rs);
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
        	ps = conn.prepareStatement(GET_SUBMISSION);   
        	ps.setLong(1, id);            
            rs = ps.executeQuery();
             
            Submission submission = null;
                        
            if (rs.next()) {
            	submission = populateSubmissionWithoutDetail(rs);
            } else {
            	return null;
            }    
            
            // set language
            long languageId = rs.getLong(DatabaseConstants.SUBMISSION_LANGUAGE_ID);            
            Language language = 
            	(Language) new ContestPersistenceImpl().getLanguageMap().get(new Long(languageId));
            submission.setLanguage(language);
            
            // set judge reply
            long judgeReplyId = rs.getLong(DatabaseConstants.SUBMISSION_JUDGE_REPLY_ID);
            JudgeReply judgeReply = 
            	(JudgeReply) getJudgeReplyMap().get(new Long(judgeReplyId));
            submission.setJudgeReply(judgeReply);                        	
            
            return submission;
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
    private Submission populateSubmission(ResultSet rs) throws SQLException {
        Submission submission = new Submission();
        
    	submission.setId(rs.getLong(DatabaseConstants.SUBMISSION_SUBMISSION_ID));
    	submission.setProblemId(rs.getLong(DatabaseConstants.SUBMISSION_PROBLEM_ID));
    	submission.setUserProfileId(rs.getLong(DatabaseConstants.SUBMISSION_USER_PROFILE_ID));
    	submission.setContent(rs.getString(DatabaseConstants.SUBMISSION_CONTENT));
    	submission.setJudgeComment(rs.getString(DatabaseConstants.SUBMISSION_JUDGE_COMMENT));
    	submission.setJudgeDate(Database.getDate(rs,DatabaseConstants.SUBMISSION_JUDGE_DATE));
    	submission.setSubmitDate(Database.getDate(rs,DatabaseConstants.SUBMISSION_SUBMISSION_DATE));
    	submission.setMemoryConsumption(rs.getInt(DatabaseConstants.SUBMISSION_MEMORY_CONSUMPTION));
    	submission.setTimeConsumption(rs.getInt(DatabaseConstants.SUBMISSION_TIME_CONSUMPTION));
        submission.setUserName(rs.getString(DatabaseConstants.USER_PROFILE_HANDLE));
    	submission.setProblemCode(rs.getString(DatabaseConstants.PROBLEM_CODE));
    	return submission;
    }
    
    /**
     * Populates an ExtendedSubmission with given ResultSet.
     * 
     * @param rs
     * @return an ExtendedSubmission instance
     * @throws SQLException
     */
    private Submission populateSubmissionWithoutDetail(ResultSet rs) throws SQLException {
        Submission submission = new Submission();
        
        submission.setId(rs.getLong(DatabaseConstants.SUBMISSION_SUBMISSION_ID));
        submission.setProblemId(rs.getLong(DatabaseConstants.SUBMISSION_PROBLEM_ID));
        submission.setUserProfileId(rs.getLong(DatabaseConstants.SUBMISSION_USER_PROFILE_ID));
        submission.setContent(rs.getString(DatabaseConstants.SUBMISSION_CONTENT));
        submission.setJudgeComment(rs.getString(DatabaseConstants.SUBMISSION_JUDGE_COMMENT));
        submission.setJudgeDate(Database.getDate(rs,DatabaseConstants.SUBMISSION_JUDGE_DATE));
        submission.setSubmitDate(Database.getDate(rs,DatabaseConstants.SUBMISSION_SUBMISSION_DATE));
        submission.setMemoryConsumption(rs.getInt(DatabaseConstants.SUBMISSION_MEMORY_CONSUMPTION));
        submission.setTimeConsumption(rs.getInt(DatabaseConstants.SUBMISSION_TIME_CONSUMPTION));
        return submission;
    }

    /**
     * <p>Returns the number of all submissions according with the given criteria in persistence layer.</p>
     *
     * @return the number of all submissions according with the given criteria
     * @param criteria the submission search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of submissions in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public long searchSubmissionNumber(SubmissionCriteria criteria) throws PersistenceException {
    	if (criteria == null) {
    		throw new NullPointerException("criteria is null");
    	}
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                    
        try {
        	conn = Database.createConnection();
        	ps = buildQuery(GET_SUBMISSION_NUMBER, criteria, 0, Integer.MAX_VALUE, conn, ps, rs);        	
        	if (ps == null) {
        		return 0;
        	}
        	rs = ps.executeQuery();
        	rs.next();
        	return rs.getLong(1);
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the submissions number", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        } 

    }
    

    /**
     * <p>Searchs all submissions according with the given criteria in persistence layer.</p>
     *
     * @return a list of submissions according with the given criteria
     * @param criteria the submission search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of submissions in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List searchSubmissions(SubmissionCriteria criteria, int offset, int count) throws PersistenceException {
    	if (criteria == null) {
    		throw new NullPointerException("criteria is null");
    	}
    	if (offset < 0) {
    		throw new IllegalArgumentException("offset is negative"); 
    	}
    	if (count < 0) {
    		throw new IllegalArgumentException("count is negative"); 
    	}
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            
        Map languageMap = new ContestPersistenceImpl().getLanguageMap();
        Map judgeReplyMap = getJudgeReplyMap();
        
        try {
        	conn = Database.createConnection();
        	ps = buildQuery(GET_SUBMISSIONS, criteria, offset, count, conn, ps, rs);
        	
        	if (ps == null) {
        		return new ArrayList();
        	}
        	rs = ps.executeQuery();
             
            List submissions = new ArrayList();
            while (rs.next()) {
                Submission submission = populateSubmission(rs);
            	long languageId = rs.getLong(DatabaseConstants.SUBMISSION_LANGUAGE_ID);
            	long judgeReplyId = rs.getLong(DatabaseConstants.SUBMISSION_JUDGE_REPLY_ID);
            	submission.setLanguage((Language) languageMap.get(new Long(languageId)));
            	submission.setJudgeReply((JudgeReply) judgeReplyMap.get(new Long(judgeReplyId)));
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
     * @param offset
     * @param count
     * @param conn
     * @param ps
     * @param rs
     * @return search query.
     * @throws SQLException 
     */
    private PreparedStatement buildQuery(String perfix, SubmissionCriteria criteria, int offset, int count,
    		Connection conn, PreparedStatement ps, ResultSet rs) throws SQLException {
    	
    	StringBuffer query = new StringBuffer();
    	query.append(perfix);
    	
    	if (criteria.getLanguages() != null) {
    		if (criteria.getLanguages().size() == 0) {
    			return null;
    		}
    		List languageIds = new ArrayList();
    		for (Iterator it = criteria.getLanguages().iterator(); it.hasNext();) {
    			Language language = (Language) it.next();
    			languageIds.add(new Long(language.getId()));
    		}
    		query.append(" AND s." + DatabaseConstants.SUBMISSION_LANGUAGE_ID + " IN " 
    				+ Database.createNumberValues(languageIds));
    	}
    	
    	if (criteria.getJudgeReplies() != null) {
    		if (criteria.getJudgeReplies().size() == 0) {
    			return null;
    		}
    		List judgeRepliesIds = new ArrayList();
    		for (Iterator it = criteria.getJudgeReplies().iterator(); it.hasNext();) {
    			JudgeReply judgeReply = (JudgeReply) it.next();
    			judgeRepliesIds.add(new Long(judgeReply.getId()));
    		}
    		query.append(" AND s." + DatabaseConstants.SUBMISSION_JUDGE_REPLY_ID + " IN " 
    				+ Database.createNumberValues(judgeRepliesIds));
    	}
    	
    	if (criteria.getContestId() != null) {
    		query.append(" AND p." + DatabaseConstants.PROBLEM_CONTEST_ID + "=" + criteria.getContestId());
    	}
    	
    	if (criteria.getHandle() != null && criteria.getHandle().trim().length() > 0) {
    		query.append(" AND u." + DatabaseConstants.USER_PROFILE_HANDLE + "='" + criteria.getHandle() + "'");		
    	}
    	
    	if (criteria.getProblemId() != null) {
    		query.append(" AND s." + DatabaseConstants.SUBMISSION_PROBLEM_ID + "=" + criteria.getProblemId());
    	}
    	
    	if (criteria.getProblemCode() != null) {
    		query.append(" AND p." + DatabaseConstants.PROBLEM_CODE + "='" + criteria.getProblemCode() + "'");
    	}
    	
    	if (criteria.getIdStart() != null) {
    		query.append(" AND s." + DatabaseConstants.SUBMISSION_SUBMISSION_ID + ">=" + criteria.getIdStart());
    	}
    	
    	if (criteria.getIdEnd() != null) {
    		query.append(" AND s." + DatabaseConstants.SUBMISSION_SUBMISSION_ID + "<=" + criteria.getIdEnd());
    	}
    	
    	if (criteria.getTimeStart() != null) {
    		query.append(" AND s." + DatabaseConstants.SUBMISSION_SUBMISSION_DATE + ">=?");
    	}    	    	    	
    	if (criteria.getTimeEnd() != null) {
    		query.append(" AND s." + DatabaseConstants.SUBMISSION_SUBMISSION_DATE + "<=?");
    	}

    	query.append(" ORDER BY " + DatabaseConstants.SUBMISSION_SUBMISSION_ID + " DESC");
    	query.append(" LIMIT " + offset + "," + count);
    	System.out.println(query);
    	ps = conn.prepareStatement(query.toString());
    	
    	int index = 1;
    	if (criteria.getTimeStart() != null) {
    		ps.setTimestamp(index, Database.toTimestamp(criteria.getTimeStart()));
    		index++;
    	}    	    	    	
    	if (criteria.getTimeEnd() != null) {
    		ps.setTimestamp(index, Database.toTimestamp(criteria.getTimeEnd()));    		
    	}
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
        	ps = conn.prepareStatement(query);
        	rs = ps.executeQuery();        	        	
        	
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
    
    public List getProblemsetRankList(long contestId,  long begin, long order, long roleId) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();
        	int index = 0;
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
            
            String orderby="";
            if(order==0)
            {
            	orderby=" ORDER BY ac_number DESC, submission_number INSC";
            }
            else
            {
            	orderby=" ORDER BY submission_number DESC";
            }
            
            String limit=" LIMIT "+begin+", 25";
            
        	String query = "SELECT user_profile_id, ac_number, submission_number FROM user_stat " 
        			+ "WHERE contest_id= " + contestId + userIdsCon + orderby+limit;
        	ps = conn.prepareStatement(query);
        	rs = ps.executeQuery();
        	
        	Map entries = new HashMap();      
        	
        	while (rs.next()) {
            	long userId = rs.getLong(1);
            	RankListEntry entry = (RankListEntry) entries.get(new Long(userId));
            	if (entry == null) {
            		entry = new RankListEntry(10);
            		entries.put(new Long(userId), entry);
            		UserProfile profile = new UserProfile();
            		profile.setId(userId);
            		entry.setUserProfile(profile);
            	}
            	entry.setSolved(rs.getLong(2));
            	entry.setSubmitted(rs.getLong(3));
            } 
            
            List entryList = new ArrayList(entries.values());
                                         
            return entryList;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the rank list", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }     	
    }
    
    public Set getSolvedProblems(List problems, long userProfileId) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;                        
        
        try {
        	conn = Database.createConnection();
        	List problemIds = new ArrayList();
        	int index = 0;
        	for (Iterator it = problems.iterator(); it.hasNext();) {
        		Problem problem = (Problem) it.next();
        		problemIds.add(new Long(problem.getId()));  
        		index++;
        	}
        	String inProblemIds = Database.createNumberValues(problemIds);
        	String query = "SELECT DISTINCT problem_id FROM submission " 
        			+ "WHERE problem_id IN " + inProblemIds + " AND judge_reply_id=5 AND user_profile_id=" + userProfileId;
        	System.out.println(query);
        	ps = conn.prepareStatement(query);
        	
        	rs = ps.executeQuery();
        	Set solved = new HashSet();
            while (rs.next()) {
            	solved.add(new Long(rs.getLong(1)));            	
            } 
            
                                         
            return solved;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the solved problems", e);
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
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to delete judgeReply.", e);
		} finally {
			Database.rollback(conn);
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
    public List getAllJudgeReplies() throws PersistenceException {
    	synchronized (this.getClass()) {
    		if (allJudgeReplies != null) {
    			return new ArrayList(allJudgeReplies);
    		}
    	}
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
        	ps = conn.prepareStatement(GET_ALL_JUDGE_REPLIES);   
            rs = ps.executeQuery();
                
            List judgeReplies = new ArrayList();
            
            while (rs.next()) {
            	JudgeReply judgeReply = new JudgeReply(rs.getLong(DatabaseConstants.JUDGE_REPLY_JUDGE_REPLY_ID),
            									 rs.getString(DatabaseConstants.JUDGE_REPLY_NAME),
            									 rs.getString(DatabaseConstants.JUDGE_REPLY_DESCRIPTION),
            									 rs.getString(DatabaseConstants.JUDGE_REPLY_STYLE),
            									 rs.getBoolean(DatabaseConstants.JUDGE_REPLY_COMMITTED));
            	judgeReplies.add(judgeReply);
            }
        	
            allJudgeReplies = new ArrayList(judgeReplies);
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
    Map getJudgeReplyMap() throws PersistenceException {
    	List judgeReplies = getAllJudgeReplies(); 
        Map judgeReplyMap = new HashMap();
        for (Iterator it = judgeReplies.iterator(); it.hasNext();) {
        	JudgeReply reply = (JudgeReply) it.next();
        	judgeReplyMap.put(new Long(reply.getId()), reply);        	
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
    
    public List searchQQs(long contestId) throws PersistenceException {
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
             
            List qqs = new ArrayList();
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
    
  
}


