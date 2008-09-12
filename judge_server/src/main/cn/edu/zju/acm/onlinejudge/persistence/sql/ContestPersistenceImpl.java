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

package cn.edu.zju.acm.onlinejudge.persistence.sql;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>ContestPersistenceImpl implements ContestPersistence interface</p>
 * <p>ContestPersistence interface defines the API used to manager the contest related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class ContestPersistenceImpl implements ContestPersistence {
	
	/**
	 * The default limit id.
	 */
	private static final long DEFAULT_LIMIT_ID = 1;
	
	
	
	/**
	 * The statement to get the contest limit id.
	 */
	private static final String GET_CONTEST_LIMIT_ID = 
		MessageFormat.format("SELECT {0} FROM {1} WHERE {2}=?", 
							 new Object[] {DatabaseConstants.CONTEST_LIMITS_ID, 				  						   
				  						   DatabaseConstants.CONTEST_TABLE,
				  						   DatabaseConstants.CONTEST_CONTEST_ID});

	
	   
	/**
	 * The statement to update problem limit id.
	 */
	private static final String UPDATE_PROBLEM_LIMIT = 
		MessageFormat.format("UPDATE {0} SET {1}=? WHERE {2}=? AND {3}=?", 
							 new Object[] {DatabaseConstants.PROBLEM_TABLE, 				  						   
				  						   DatabaseConstants.PROBLEM_LIMITS_ID,
				  						   DatabaseConstants.PROBLEM_LIMITS_ID,
				  						   DatabaseConstants.PROBLEM_CONTEST_ID});	
	
	
    /**
     * The statement to get the default limit.
     */
    private static final String SELECT_DEFAULT_LIMIT = 
        MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4} FROM {5} WHERE {0}=" + DEFAULT_LIMIT_ID , 
                             new Object[] {DatabaseConstants.LIMITS_LIMITS_ID,
                                           DatabaseConstants.LIMITS_TIME_LIMIT,
                                           DatabaseConstants.LIMITS_MEMORY_LIMIT,
                                           DatabaseConstants.LIMITS_OUTPUT_LIMIT,
                                           DatabaseConstants.LIMITS_SUBMISSION_LIMIT,
                                           DatabaseConstants.LIMITS_TABLE});
               
    
	/**
	 * The statement to create a Contest.
	 */
	private static final String INSERT_CONTEST = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13})"
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, ?)", 
							 new Object[] {DatabaseConstants.CONTEST_TABLE, 
				  						   DatabaseConstants.CONTEST_TITLE,
				  						   DatabaseConstants.CONTEST_DESCRIPTION,
				  						   DatabaseConstants.CONTEST_START_TIME,
				  						   DatabaseConstants.CONTEST_END_TIME,
				  						   DatabaseConstants.CONTEST_FORUM_ID,
				  						   DatabaseConstants.CONTEST_LIMITS_ID,
				  						   DatabaseConstants.CONTEST_PROBLEMSET,				  						   
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,				  						   
				  						   DatabaseConstants.CONTEST_ACTIVE,
                                           DatabaseConstants.CONTEST_CHECK_IP});
	
	/**
	 * The statement to update a Contest.
	 */
	private static final String UPDATE_CONTEST = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=?, {7}=?, {8}=?, "
				+ "{9}=?, {10}=? WHERE {11}=?", 
							 new Object[] {DatabaseConstants.CONTEST_TABLE,  
										   DatabaseConstants.CONTEST_TITLE,
										   DatabaseConstants.CONTEST_DESCRIPTION,
										   DatabaseConstants.CONTEST_START_TIME,
										   DatabaseConstants.CONTEST_END_TIME,
										   DatabaseConstants.CONTEST_FORUM_ID,
										   DatabaseConstants.CONTEST_LIMITS_ID,
										   DatabaseConstants.CONTEST_PROBLEMSET,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
                                           DatabaseConstants.CONTEST_CHECK_IP,
				  						   DatabaseConstants.CONTEST_CONTEST_ID}); 
	
	/**
	 * The statement to delete a contest.
	 */
	private static final String DELETE_CONTEST = 
		MessageFormat.format("UPDATE {0} SET {1}=0, {2}=?, {3}=? WHERE {4}=?", 
							 new Object[] {DatabaseConstants.CONTEST_TABLE, 
										   DatabaseConstants.CONTEST_ACTIVE,
										   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.CONTEST_CONTEST_ID}); 
	
	/**
	 * The query to get a contest.
	 */
	private static final String GET_CONTEST = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4}, {5}, {12}.{6}, {7}, {8}, {9}, {10}, {11}, {16} " 
				+ "FROM {12} LEFT JOIN {13} ON ({12}.{6}={13}.{14}) WHERE {15}=1",
				 			 new Object[] {DatabaseConstants.CONTEST_CONTEST_ID, 
										   DatabaseConstants.CONTEST_TITLE,
										   DatabaseConstants.CONTEST_DESCRIPTION,
										   DatabaseConstants.CONTEST_START_TIME,
										   DatabaseConstants.CONTEST_END_TIME,
										   DatabaseConstants.CONTEST_FORUM_ID,
										   DatabaseConstants.CONTEST_LIMITS_ID,										   
										   DatabaseConstants.CONTEST_PROBLEMSET,
										   DatabaseConstants.LIMITS_TIME_LIMIT,
										   DatabaseConstants.LIMITS_MEMORY_LIMIT,
										   DatabaseConstants.LIMITS_OUTPUT_LIMIT,
										   DatabaseConstants.LIMITS_SUBMISSION_LIMIT,
				   					       DatabaseConstants.CONTEST_TABLE,
				   					       DatabaseConstants.LIMITS_TABLE,
				   					       DatabaseConstants.LIMITS_LIMITS_ID,
				   					       DatabaseConstants.CONTEST_ACTIVE,
                                           DatabaseConstants.CONTEST_CHECK_IP});
	

	/**
	 * The statement to create a Limit.
	 */
	private static final String INSERT_LIMIT = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}) VALUES(?, ?, ?, ?)", 
							 new Object[] {DatabaseConstants.LIMITS_TABLE, 
				  						   DatabaseConstants.LIMITS_TIME_LIMIT,
				  						   DatabaseConstants.LIMITS_MEMORY_LIMIT,
				  						   DatabaseConstants.LIMITS_OUTPUT_LIMIT,
				  						   DatabaseConstants.LIMITS_SUBMISSION_LIMIT});
	
	/**
	 * The statement to update a Limit.
	 */
	private static final String UPDATE_LIMIT = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=? WHERE {5}=?", 
							 new Object[] {DatabaseConstants.LIMITS_TABLE, 
				   						   DatabaseConstants.LIMITS_TIME_LIMIT,
										   DatabaseConstants.LIMITS_MEMORY_LIMIT,
										   DatabaseConstants.LIMITS_OUTPUT_LIMIT,
										   DatabaseConstants.LIMITS_SUBMISSION_LIMIT,
										   DatabaseConstants.LIMITS_LIMITS_ID});
	


	/**
	 * The query to get a limit.
	 */
	private static final String GET_CONTEST_LANGUAGE = 
		MessageFormat.format("SELECT {0}, {1} FROM {2} WHERE {0} IN {3} ORDER BY {0}, {1}",
				 			 new Object[] {DatabaseConstants.CONTEST_LANGUAGE_CONTEST_ID, 
										   DatabaseConstants.CONTEST_LANGUAGE_LANGUAGE_ID,										   
										   DatabaseConstants.CONTEST_LANGUAGE_TABLE,
										   "{0}"});
	
	
	/**
	 * The statement to create a contest-language reference.
	 */
	private static final String INSERT_CONTEST_LANGUAGE = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}) VALUES(?, ?)", 
							 new Object[] {DatabaseConstants.CONTEST_LANGUAGE_TABLE,
										   DatabaseConstants.CONTEST_LANGUAGE_CONTEST_ID,
				  						   DatabaseConstants.CONTEST_LANGUAGE_LANGUAGE_ID});

	/**
	 * The statement to delete the contest-language references.
	 */
	private static final String DELETE_CONTEST_LANGUAGE = 
		MessageFormat.format("DELETE FROM {0} WHERE {1}=?", 
							 new Object[] {DatabaseConstants.CONTEST_LANGUAGE_TABLE,
										   DatabaseConstants.CONTEST_LANGUAGE_CONTEST_ID});

	
	/**
	 * The statement to create a language.
	 */
	private static final String INSERT_LANGUAGE = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", 
							 new Object[] {DatabaseConstants.LANGUAGE_TABLE, 
				  						   DatabaseConstants.LANGUAGE_LANGUAGE_ID,
				  						   DatabaseConstants.LANGUAGE_NAME,
				  						   DatabaseConstants.LANGUAGE_DESCRIPTION,
				  						   DatabaseConstants.LANGUAGE_OPTIONS,
				  						   DatabaseConstants.LANGUAGE_COMPILER,				  						   
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE}); 
    
	/**
	 * The statement to update a language.
	 */
	private static final String UPDATE_LANGUAGE = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=? WHERE {7}=?", 
							 new Object[] {DatabaseConstants.LANGUAGE_TABLE, 
										   DatabaseConstants.LANGUAGE_NAME,
										   DatabaseConstants.LANGUAGE_DESCRIPTION,
										   DatabaseConstants.LANGUAGE_OPTIONS,
										   DatabaseConstants.LANGUAGE_COMPILER,			
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.LANGUAGE_LANGUAGE_ID}); 

	/**
	 * The statement to delete a language.
	 */
	private static final String DELETE_LANGUAGE_CONTEST = 
		MessageFormat.format("DELETE FROM {0} WHERE {1}=?", 
							 new Object[] {DatabaseConstants.CONTEST_LANGUAGE_TABLE, 
										   DatabaseConstants.CONTEST_LANGUAGE_LANGUAGE_ID}); 
	
	/**
	 * The statement to delete a language.
	 */
	private static final String DELETE_LANGUAGE = 
		MessageFormat.format("DELETE FROM {0} WHERE {1}=?", 
				 new Object[] {DatabaseConstants.LANGUAGE_TABLE, 
							   DatabaseConstants.LANGUAGE_LANGUAGE_ID});	
	/**
	 * The statement to delete a language.
	 */
	private static final String DELETE_SUBMISSION = 
		MessageFormat.format("DELETE FROM {0} WHERE {1}=?", 
				 new Object[] {DatabaseConstants.SUBMISSION_TABLE, 
							   DatabaseConstants.SUBMISSION_LANGUAGE_ID});				
	/**
	 * The query to get all languages.
	 */
	private static final String GET_ALL_LANGUAGES = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4} FROM {5}",
				 			 new Object[] {DatabaseConstants.LANGUAGE_LANGUAGE_ID, 
										   DatabaseConstants.LANGUAGE_NAME,
										   DatabaseConstants.LANGUAGE_DESCRIPTION,
										   DatabaseConstants.LANGUAGE_OPTIONS,
										   DatabaseConstants.LANGUAGE_COMPILER,			
				   					       DatabaseConstants.LANGUAGE_TABLE});
	
	
	/**
	 * The languages cache.
	 */
	private static List<Language> allLanguages = null;
    
    
    /**
     * The defaultLimit = null;
     */
    private static Limit defaultLimit = null;
    
    /**
     * Gets the default limit.
     * @return the defalut limit.
     * @throws PersistenceException if failed to get the default limit
     */
    public Limit getDefaultLimit() throws PersistenceException {
        synchronized (this.getClass()) {
            if (defaultLimit != null) {
                return defaultLimit;
            }
            
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
                    
            try {
                conn = Database.createConnection();
                ps = conn.prepareStatement(SELECT_DEFAULT_LIMIT);   
                rs = ps.executeQuery();
                if (rs.next()) {
                    defaultLimit = new Limit();
                    defaultLimit.setId(rs.getLong(DatabaseConstants.LIMITS_LIMITS_ID));
                    defaultLimit.setMemoryLimit(rs.getInt(DatabaseConstants.LIMITS_MEMORY_LIMIT));
                    defaultLimit.setOutputLimit(rs.getInt(DatabaseConstants.LIMITS_OUTPUT_LIMIT));
                    defaultLimit.setSubmissionLimit(rs.getInt(DatabaseConstants.LIMITS_SUBMISSION_LIMIT));
                    defaultLimit.setTimeLimit(rs.getInt(DatabaseConstants.LIMITS_TIME_LIMIT));          
                }       
                
            } catch (SQLException e) {
                throw new PersistenceException("Failed to get the default limit", e);
            } finally {
                Database.dispose(conn, ps, rs);
            }   

            return defaultLimit;
        }
    }
    
    /**
     * Update the default limit.
     * @param limit the default limit.
     * @throws PersistenceException if failed to update the default limit
     */
    public void updateDefaultLimit(Limit limit) throws PersistenceException{
        synchronized (this.getClass()) {
            defaultLimit = null;
            
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
                    
            try {
                conn = Database.createConnection();
                ps = conn.prepareStatement(UPDATE_LIMIT);
                ps.setInt(1, limit.getTimeLimit());
                ps.setInt(2, limit.getMemoryLimit());
                ps.setInt(3, limit.getOutputLimit());
                ps.setInt(4, limit.getSubmissionLimit());
                ps.setLong(5, DEFAULT_LIMIT_ID);
                ps.executeUpdate();
                                                
            } catch (SQLException e) {
                throw new PersistenceException("Failed to update the default limit", e);
            } finally {
                Database.dispose(conn, ps, rs);
            }   
        }
    }    
	
    /**
     * <p>Creates the specified contest in persistence layer.</p>
     *
     * @param contest the AbstractContest instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createContest(AbstractContest contest, long user) throws PersistenceException {
    	
    	if (contest == null) {
    		throw new NullPointerException("contest is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();   
        	conn.setAutoCommit(false);
	
        	// create a new limit
            Limit limit = contest.getLimit();
            if (limit != null && limit.getId() != DEFAULT_LIMIT_ID) {
            	ps = conn.prepareStatement(INSERT_LIMIT);
            	ps.setInt(1, limit.getTimeLimit());
            	ps.setInt(2, limit.getMemoryLimit());
            	ps.setInt(3, limit.getOutputLimit());
            	ps.setInt(4, limit.getSubmissionLimit());
            	ps.executeUpdate();                        
            	limit.setId(Database.getLastId(conn, ps, rs));
            }
            
            // create the contest
            ps = conn.prepareStatement(INSERT_CONTEST); 
            
            ps.setString(1, contest.getTitle());
            ps.setString(2, contest.getDescription());
            
            if (contest.getStartTime() != null) {
                ps.setTimestamp(3, new Timestamp(contest.getStartTime().getTime()));
            } else {
                ps.setTimestamp(3, null);
            }
            if (contest.getEndTime() != null) {
                ps.setTimestamp(4, new Timestamp(contest.getEndTime().getTime()));
            } else {
                ps.setTimestamp(4, null);
            }
            
            ps.setLong(5, contest.getForumId());
            if (limit == null || limit.getId() == DEFAULT_LIMIT_ID) {
            	ps.setLong(6, DEFAULT_LIMIT_ID);
            } else {
            	ps.setLong(6, limit.getId());
            }
            ps.setBoolean(7, contest instanceof Problemset);
            ps.setLong(8, user);
            ps.setTimestamp(9, new Timestamp(new Date().getTime()));
            ps.setLong(10, user);
            ps.setTimestamp(11, new Timestamp(new Date().getTime()));
            ps.setBoolean(12, contest.isCheckIp());
            ps.executeUpdate();                                               
            contest.setId(Database.getLastId(conn, ps, rs));   
            
            // create languages
            if (contest.getLanguages() != null) { 
            	for (Iterator<Language> it = contest.getLanguages().iterator(); it.hasNext();) {
            		Language language = it.next();
            		ps = conn.prepareStatement(INSERT_CONTEST_LANGUAGE);            
            		ps.setLong(1, contest.getId());
            		ps.setLong(2, language.getId());
            		ps.executeUpdate();
            	}
            }

            conn.commit();
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create contest.", e);
		} finally {
			Database.rollback(conn);
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Updates the specified contest in persistence layer.</p>
     *
     * @param contest the AbstractContest instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateContest(AbstractContest contest, long user) throws PersistenceException {
    	if (contest == null) {
    		throw new NullPointerException("contest is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();   
        	conn.setAutoCommit(false);
	
           	long contestLimitId = DEFAULT_LIMIT_ID;
        	ps = conn.prepareStatement(GET_CONTEST_LIMIT_ID);
        	ps.setLong(1, contest.getId());
        	rs = ps.executeQuery();
        	if (rs.next()) {
        		contestLimitId = rs.getLong(1);
        	}
        	        	
        	// update the limit              
            Limit limit = contest.getLimit();
            if (limit.getId() != DEFAULT_LIMIT_ID) {
            	ps = conn.prepareStatement(INSERT_LIMIT);
                ps.setInt(1, limit.getTimeLimit());
                ps.setInt(2, limit.getMemoryLimit());
                ps.setInt(3, limit.getOutputLimit());
                ps.setInt(4, limit.getSubmissionLimit());
                ps.executeUpdate();                        
                limit.setId(Database.getLastId(conn, ps, rs));      
            	/*
            	if (contestLimitId == DEFAULT_LIMIT_ID) {
	            	ps = conn.prepareStatement(INSERT_LIMIT);
	                ps.setInt(1, limit.getTimeLimit());
	                ps.setInt(2, limit.getMemoryLimit());
	                ps.setInt(3, limit.getOutputLimit());
	                ps.setInt(4, limit.getSubmissionLimit());
	                ps.executeUpdate();                        
	                limit.setId(Database.getLastId(conn, ps, rs));                
	            } else {	            
		        	ps = conn.prepareStatement(UPDATE_LIMIT);            
		            ps.setInt(1, limit.getTimeLimit());
		            ps.setInt(2, limit.getMemoryLimit());
		            ps.setInt(3, limit.getOutputLimit());
		            ps.setInt(4, limit.getSubmissionLimit());
		            ps.setLong(5, limit.getId());
		            ps.executeUpdate();
	            }    
	            */                                           
            }	 
            if (contestLimitId != limit.getId()) {
            	ps = conn.prepareStatement(UPDATE_PROBLEM_LIMIT);
            	ps.setLong(1, limit.getId());
            	ps.setLong(2, contest.getId());
                ps.setLong(3, contestLimitId);
                ps.executeUpdate();
            }
            
            // update the contest
            ps = conn.prepareStatement(UPDATE_CONTEST);            
            ps.setString(1, contest.getTitle());
            ps.setString(2, contest.getDescription());
            
            if (contest.getStartTime() != null) {
                ps.setTimestamp(3, new Timestamp(contest.getStartTime().getTime()));
            } else {
                ps.setTimestamp(3, null);
            }
            if (contest.getEndTime() != null) {
                ps.setTimestamp(4, new Timestamp(contest.getEndTime().getTime()));
            } else {
                ps.setTimestamp(4, null);
            }
            
            
            ps.setLong(5, contest.getForumId());
            if (limit == null || limit.getId() == DEFAULT_LIMIT_ID) {
            	ps.setLong(6, DEFAULT_LIMIT_ID);
            } else {
            	ps.setLong(6, limit.getId());
            }
            ps.setBoolean(7, contest instanceof Problemset);
            ps.setLong(8, user);
            ps.setTimestamp(9, new Timestamp(new Date().getTime()));
            ps.setBoolean(10, contest.isCheckIp());
            ps.setLong(11, contest.getId());
            ps.executeUpdate();                                                              
            
            // delete languages            
            ps = conn.prepareStatement(DELETE_CONTEST_LANGUAGE);            
            ps.setLong(1, contest.getId());
            ps.executeUpdate();
            
            // insert languages
            if (contest.getLanguages() != null) {
	            for (Iterator<Language> it = contest.getLanguages().iterator(); it.hasNext();) {
	            	Language language = it.next();
	            	ps = conn.prepareStatement(INSERT_CONTEST_LANGUAGE);            
	                ps.setLong(1, contest.getId());
	                ps.setLong(2, language.getId());
	                ps.executeUpdate();
	            }
            }

            conn.commit();
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create contest.", e);
		} finally {
			Database.rollback(conn);
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Deletes the specified contest in persistence layer.</p>
     *
     * @param id the id of the contest to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteContest(long id, long user) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(DELETE_CONTEST);  
            ps.setLong(1, user);
            ps.setTimestamp(2, new Timestamp(new Date().getTime()));
            ps.setLong(3, id);
            
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such contest");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to delete contest.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        } 
    }

    /**
     * <p>Gets the contest with given id in persistence layer.</p>
     *
     * @param id the id of the contest
     * @return the contest with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public AbstractContest getContest(long id) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
        	String query = GET_CONTEST + " AND " + DatabaseConstants.CONTEST_CONTEST_ID + "=" + id;
        	ps = conn.prepareStatement(query);   
            
            rs = ps.executeQuery();
             
            AbstractContest contest = null;
                        
            if (rs.next()) {
            	contest = populateContest(rs);
            } else {
            	return null;
            }               
            
            populatesLanguages(conn, ps, rs, Arrays.asList(new AbstractContest[] {contest}));
            
            return contest;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the contest with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
        
    }
    
    /**
     * Populates a Limit with given ResultSet.
     * 
     * @param rs
     * @return a Limit instance
     * @throws SQLException
     * @throws PersistenceException 
     */
    private void populatesLanguages(Connection conn, PreparedStatement ps, ResultSet rs, List<AbstractContest> contests) 
    		throws SQLException, PersistenceException {
    	
    	if (contests.isEmpty()) {
    		return;
    	}
        Map<Long, Language> languageMap = getLanguageMap();
        
    	List<Long> contestIds = new ArrayList<Long>();
    	for (Iterator<AbstractContest> it = contests.iterator(); it.hasNext();) {
    		AbstractContest contest = it.next();
    		contestIds.add(new Long(contest.getId()));
    	}
    	
    	String query =  MessageFormat.format(GET_CONTEST_LANGUAGE, 
    			new Object[] {Database.createNumberValues(contestIds)});
    			    	
    	ps = conn.prepareStatement(query);       	
    	rs = ps.executeQuery();
        

        Map<Long, List<Language>> contestLanguageMap = new HashMap<Long, List<Language>>();
        while (rs.next()) {
        	Long contestId = new Long(rs.getLong(DatabaseConstants.CONTEST_LANGUAGE_CONTEST_ID));
        	Long languageId = new Long(rs.getLong(DatabaseConstants.CONTEST_LANGUAGE_LANGUAGE_ID));
        	
        	List<Language> contestLanguages = contestLanguageMap.get(contestId);
        	if (contestLanguages == null) {
        		contestLanguages = new ArrayList<Language>();
        		contestLanguageMap.put(contestId, contestLanguages);
        	}
        	contestLanguages.add(languageMap.get(languageId));        	
        }
        
    	for (Iterator<AbstractContest> it = contests.iterator(); it.hasNext();) {
    		AbstractContest contest = it.next();
    		List<Language> contestLanguages = contestLanguageMap.get(new Long(contest.getId()));
    		if (contestLanguages == null) {
    			contest.setLanguages(new ArrayList<Language>());
    		} else {
    			contest.setLanguages(contestLanguages);
    		}
    	}
            	
    }
    
    /**
     * Populates an AbstractContest with given ResultSet.
     * 
     * @param rs
     * @return an AbstractContest instance
     * @throws SQLException
     */
    private AbstractContest populateContest(ResultSet rs) throws SQLException {
    	AbstractContest contest = null;
    	if (rs.getBoolean(DatabaseConstants.CONTEST_PROBLEMSET)) {
    		contest = new Problemset();             		
    	} else {
    		contest = new Contest();
        }
        if (rs.getTimestamp(DatabaseConstants.CONTEST_START_TIME) != null) {
            contest.setStartTime(
                new Date(rs.getTimestamp(DatabaseConstants.CONTEST_START_TIME).getTime()));
        }
        if (rs.getTimestamp(DatabaseConstants.CONTEST_END_TIME) != null) {            
            contest.setEndTime(
    			new Date(rs.getTimestamp(DatabaseConstants.CONTEST_END_TIME).getTime()));            		
        }
    	contest.setId(rs.getLong(DatabaseConstants.CONTEST_CONTEST_ID));
    	contest.setTitle(rs.getString(DatabaseConstants.CONTEST_TITLE));            	
    	contest.setDescription(rs.getString(DatabaseConstants.CONTEST_DESCRIPTION));
    	contest.setForumId(rs.getLong(DatabaseConstants.CONTEST_FORUM_ID));
        contest.setCheckIp(rs.getBoolean(DatabaseConstants.CONTEST_CHECK_IP));
    	
    	Limit limit = new Limit();
    	limit.setId(rs.getLong(DatabaseConstants.LIMITS_LIMITS_ID));
    	limit.setTimeLimit(rs.getInt(DatabaseConstants.LIMITS_TIME_LIMIT));
    	limit.setMemoryLimit(rs.getInt(DatabaseConstants.LIMITS_MEMORY_LIMIT));
    	limit.setSubmissionLimit(rs.getInt(DatabaseConstants.LIMITS_SUBMISSION_LIMIT));
    	limit.setOutputLimit(rs.getInt(DatabaseConstants.LIMITS_OUTPUT_LIMIT));
    	
    	contest.setLimit(limit);
    	return contest;
    }
    
    /**
     * <p>Gets all contests in persistence layer.</p>
     *
     * @return a list of Contest instances containing all contests in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<AbstractContest> getAllContests() throws PersistenceException {
    	return getContests(false);
    }

    /**
     * <p>Gets all problem sets in persistence layer.</p>
     *
     * @return a list of Problemset instances containing all problem sets in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<AbstractContest> getAllProblemsets() throws PersistenceException {
    	return getContests(true);
    }
    
    /**
     * <p>Gets a list of contests with given type in persistence layer.</p>
     *
     * @param isProblemset
     * @return a list of Problemset instances containing all problem sets in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    private List<AbstractContest> getContests(boolean isProblemset) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
        	String query = GET_CONTEST + " AND " + 
        			DatabaseConstants.CONTEST_PROBLEMSET + "=" + (isProblemset ? 1 : 0) + " ORDER BY start_time DESC";

        	System.out.println(query);
        	ps = conn.prepareStatement(query);               
            rs = ps.executeQuery();
            
            List<AbstractContest> contests = new ArrayList<AbstractContest>();
            while (rs.next()) {
            	AbstractContest contest = populateContest(rs);
            	contests.add(contest);
            }
            
                        
            
            populatesLanguages(conn, ps, rs, contests);
            
            return contests;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the contests", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Creates the specified language in persistence layer.</p>
     *
     * @param language the Language instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createLanguage(Language language, long user) throws PersistenceException {
    	if (language == null) {
    		throw new NullPointerException("language is null.");
    	}
    	synchronized (this.getClass()) {
    		allLanguages = null;
    	}
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
            ps = conn.prepareStatement(INSERT_LANGUAGE);
            ps.setLong(1, language.getId());
            ps.setString(2, language.getName());
            ps.setString(3, language.getDescription());
            ps.setString(4, language.getOptions());
            ps.setString(5, language.getCompiler());
            
            ps.setLong(6, user);
            ps.setTimestamp(7, new Timestamp(new Date().getTime()));
            ps.setLong(8, user);
            ps.setTimestamp(9, new Timestamp(new Date().getTime()));
            ps.executeUpdate();                               
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create language.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    	
    }

    /**
     * <p>Updates the specified language in persistence layer.</p>
     *
     * @param language the Language instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateLanguage(Language language, long user) throws PersistenceException {
    	if (language == null) {
    		throw new NullPointerException("language is null.");
    	}
    	synchronized (this.getClass()) {
    		allLanguages = null;
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(UPDATE_LANGUAGE);  
            ps.setString(1, language.getName());
            ps.setString(2, language.getDescription());
            ps.setString(3, language.getOptions());
            ps.setString(4, language.getCompiler());            
            ps.setLong(5, user);
            ps.setTimestamp(6, new Timestamp(new Date().getTime()));
            ps.setLong(7, language.getId());
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such language");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to update language.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }      	
    }

    /**
     * <p>Deletes the specified language in persistence layer.</p>
     *
     * @param id the id of the language to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteLanguage(long id, long user) throws PersistenceException {
    	synchronized (this.getClass()) {
    		allLanguages = null;
    	}
    	Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
        	conn.setAutoCommit(false);
        	
        	ps = conn.prepareStatement(DELETE_SUBMISSION);
        	ps.setLong(1, id);
        	ps.executeUpdate();
        	
        	ps = conn.prepareStatement(DELETE_LANGUAGE_CONTEST);
        	ps.setLong(1, id);
        	ps.executeUpdate();        	
        	
            ps = conn.prepareStatement(DELETE_LANGUAGE);  
            ps.setLong(1, id);
            ps.executeUpdate();
            
            conn.commit();   


        	
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to delete language.", e);
		} finally {
			Database.rollback(conn);
        	Database.dispose(conn, ps, null);
        }   
    }

    /**
     * <p>Gets the language with given id.</p>
     * @param id the language id.
     * @return the language with given id or null.
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public Language getLanguage(long id) throws PersistenceException {
        List<Language> languages = getAllLanguages();
        for (Iterator<Language> it = languages.iterator(); it.hasNext();) {
            Language language = it.next();
            if (id == language.getId()) {
                return language;
            }
        }
        return null;
    }
    /**
     * <p>Gets all languages in persistence layer.</p>
     *
     * @return a list of Language instances containing all languages in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<Language> getAllLanguages() throws PersistenceException {
    	synchronized (this.getClass()) {
    		if (allLanguages != null) {
    			return new ArrayList<Language>(allLanguages);
    		}
    	
    	
	    	Connection conn = null;
	        PreparedStatement ps = null;
	        ResultSet rs = null;
	            	
	        try {
	        	conn = Database.createConnection();
	        	ps = conn.prepareStatement(GET_ALL_LANGUAGES);   
	            rs = ps.executeQuery();
	                
	            List<Language> languages = new ArrayList<Language>();
	            
	            while (rs.next()) {
	            	Language language = new Language(rs.getLong(DatabaseConstants.LANGUAGE_LANGUAGE_ID),
	            									 rs.getString(DatabaseConstants.LANGUAGE_NAME),
	            									 rs.getString(DatabaseConstants.LANGUAGE_DESCRIPTION),
	            									 rs.getString(DatabaseConstants.LANGUAGE_COMPILER),
	            									 rs.getString(DatabaseConstants.LANGUAGE_OPTIONS));
	            	languages.add(language);
	            }
	        	
	            allLanguages = new ArrayList<Language>(languages);
	            return languages;  	            
	        } catch (SQLException e) {
	        	throw new PersistenceException("Failed to get all languages", e);
			} finally {
	        	Database.dispose(conn, ps, rs);
	        }   
    	}
    }
    
    /**
     * Gets a Language Map. Language id is the key and Language itself is the value.
     * @return a Language Map
     * @throws PersistenceException
     */
    Map<Long, Language> getLanguageMap() throws PersistenceException {
    	List<Language> languages = getAllLanguages(); 
        Map<Long, Language> languageMap = new HashMap<Long, Language>();
        for (Language language : languages) {
        	languageMap.put(language.getId(), language);        	
        }
        return languageMap;
    	
    }

        
    public String getLastSubmitIP(long userId, long contestId) throws PersistenceException {
       
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                
        try {
            conn = Database.createConnection();
            ps = conn.prepareStatement("SELECT ip FROM user_contest_ip WHERE user_profile_id=? AND contest_id=?");
            ps.setLong(1, userId);
            ps.setLong(2, contestId);
            rs = ps.executeQuery();
                
            if (rs.next()) {
                return rs.getString(1);
            } else {
                return null;
            }                           
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get last submit ip", e);
        } finally {
            Database.dispose(conn, ps, rs);
        }   
        
    }

    
    public void setLastSubmitIP(long userId, long contestId, String ip) throws PersistenceException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                
        try {
            conn = Database.createConnection();            
            ps = conn.prepareStatement("UPDATE user_contest_ip SET ip=? WHERE user_profile_id=? AND contest_id=?");
            ps.setString(1, ip);
            ps.setLong(2, userId);
            ps.setLong(3, contestId);
            int ret = ps.executeUpdate();
            if (ret == 0) {
                ps = conn.prepareStatement("INSERT INTO user_contest_ip(user_profile_id, contest_id, ip) VALUES(?,?,?)");
                ps.setLong(1, userId);
                ps.setLong(2, contestId);
                ps.setString(3, ip);            
                ps.executeUpdate();
            }                   
        } catch (SQLException e) {
            throw new PersistenceException("Failed to set last submit ip", e);
        } finally {
            Database.dispose(conn, ps, rs);
        }   
        
    }

}


