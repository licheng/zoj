/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>ProblemPersistenceImpl implements ProblemPersistence interface</p>
 * <p>ProblemPersistence interface defines the API used to manager the problem related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class ProblemPersistenceImpl implements ProblemPersistence {

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
	 * The statement to create a Problem.
	 */
	private static final String INSERT_PROBLEM = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13}, {14}, {15})"
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, ?)", 
							 new Object[] {DatabaseConstants.PROBLEM_TABLE, 				  						   
				  						   DatabaseConstants.PROBLEM_CONTEST_ID,
				  						   DatabaseConstants.PROBLEM_TITLE,
				  						   DatabaseConstants.PROBLEM_CODE,
				  						   DatabaseConstants.PROBLEM_LIMITS_ID,
				  						   DatabaseConstants.PROBLEM_AUTHOR,
				  						   DatabaseConstants.PROBLEM_SOURCE,				  						   
				  						   DatabaseConstants.PROBLEM_CONTEST,
				  						   DatabaseConstants.PROBLEM_CHECKER,
				  						   DatabaseConstants.PROBLEM_REVISION,
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,				  						   
				  						   DatabaseConstants.CONTEST_ACTIVE,
				  						   DatabaseConstants.PROBLEM_COLOR});
	
	/**
	 * The statement to update a Problem.
	 */
	private static final String UPDATE_PROBLEM = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=?, {7}=?, {8}=?, "
				+ "{9}={9}+1, {10}=?, {11}=?, {12}={12}+1, {13}=? WHERE {14}=?", 
							 new Object[] {DatabaseConstants.PROBLEM_TABLE,  
										   DatabaseConstants.PROBLEM_CONTEST_ID,
										   DatabaseConstants.PROBLEM_TITLE,
										   DatabaseConstants.PROBLEM_CODE,
										   DatabaseConstants.PROBLEM_LIMITS_ID,
										   DatabaseConstants.PROBLEM_AUTHOR,
										   DatabaseConstants.PROBLEM_SOURCE,				  						   
										   DatabaseConstants.PROBLEM_CONTEST,
										   DatabaseConstants.PROBLEM_CHECKER,
										   DatabaseConstants.PROBLEM_REVISION,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.PROBLEM_REVISION,
				  						   DatabaseConstants.PROBLEM_COLOR,
				  						   DatabaseConstants.PROBLEM_PROBLEM_ID}); 
	
	/**
	 * The statement to delete a problem.
	 */
	private static final String DELETE_PROBLEM = 
		MessageFormat.format("UPDATE {0} SET {1}=CONCAT({2}, {1}), {3}=CONCAT({2}, {3}), " +
                "{4}=0, {5}=?, {6}=? WHERE {2}=?", 
							 new Object[] {DatabaseConstants.PROBLEM_TABLE, 
                                           DatabaseConstants.PROBLEM_TITLE,
                                           DatabaseConstants.PROBLEM_PROBLEM_ID,
                                           DatabaseConstants.PROBLEM_CODE,
										   DatabaseConstants.PROBLEM_ACTIVE,
										   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE}); 

     
	/**
	 * The query to get a problem.
	 */
	private static final String GET_PROBLEM = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {12} " 
				+ "FROM {10} WHERE {11}=1 AND {0}=?",
				 			 new Object[] {DatabaseConstants.PROBLEM_PROBLEM_ID, 
										   DatabaseConstants.PROBLEM_CONTEST_ID,
										   DatabaseConstants.PROBLEM_TITLE,
										   DatabaseConstants.PROBLEM_CODE,
										   DatabaseConstants.PROBLEM_LIMITS_ID,
										   DatabaseConstants.PROBLEM_AUTHOR,
										   DatabaseConstants.PROBLEM_SOURCE,				  						   
										   DatabaseConstants.PROBLEM_CONTEST,
										   DatabaseConstants.PROBLEM_CHECKER,
										   DatabaseConstants.PROBLEM_REVISION,
										   DatabaseConstants.PROBLEM_TABLE,
										   DatabaseConstants.PROBLEM_ACTIVE,
                                           DatabaseConstants.PROBLEM_COLOR});
	/*
	 * The query to search problems.
	 */
	private static final String SEARCH_PROBLEMS = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, p.{9}, {10}, {11}, {12}, {13}, {17} " 
				+ "FROM {14} p LEFT JOIN {15} l ON p.{9} = l.{9} WHERE {16}=1 ",
				 			 new Object[] {DatabaseConstants.PROBLEM_PROBLEM_ID, 
										   DatabaseConstants.PROBLEM_CONTEST_ID,
										   DatabaseConstants.PROBLEM_TITLE,
										   DatabaseConstants.PROBLEM_CODE,
										   DatabaseConstants.PROBLEM_AUTHOR,
										   DatabaseConstants.PROBLEM_SOURCE,				  						   
										   DatabaseConstants.PROBLEM_CONTEST,
										   DatabaseConstants.PROBLEM_CHECKER,
										   DatabaseConstants.PROBLEM_REVISION,
										   DatabaseConstants.PROBLEM_LIMITS_ID,
										   DatabaseConstants.LIMITS_TIME_LIMIT,
										   DatabaseConstants.LIMITS_MEMORY_LIMIT,
										   DatabaseConstants.LIMITS_OUTPUT_LIMIT,
										   DatabaseConstants.LIMITS_SUBMISSION_LIMIT,										   
										   DatabaseConstants.PROBLEM_TABLE,
										   DatabaseConstants.LIMITS_TABLE,
										   DatabaseConstants.PROBLEM_ACTIVE,
                                           DatabaseConstants.PROBLEM_COLOR});
				   
	
	
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
	 * The query to get a limit.
	 */
	private static final String GET_LIMIT = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4} FROM {5} WHERE {0}=?",
				 			 new Object[] {DatabaseConstants.LIMITS_LIMITS_ID, 
										   DatabaseConstants.LIMITS_TIME_LIMIT,
										   DatabaseConstants.LIMITS_MEMORY_LIMIT,
										   DatabaseConstants.LIMITS_OUTPUT_LIMIT,
										   DatabaseConstants.LIMITS_SUBMISSION_LIMIT,
										   DatabaseConstants.LIMITS_TABLE});
	
	
	
    /**
     * <p>Creates the specified problem in persistence layer.</p>
     *
     * @param problem the Problem instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createProblem(Problem problem, long user) throws PersistenceException {
    	if (problem == null) {
    		throw new NullPointerException("problem is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();   
        	conn.setAutoCommit(false);
	
        	long contestLimitId = DEFAULT_LIMIT_ID;
        	ps = conn.prepareStatement(GET_CONTEST_LIMIT_ID);
        	ps.setLong(1, problem.getContestId());
        	rs = ps.executeQuery();
        	if (rs.next()) {
        		contestLimitId = rs.getLong(1);
        	}
        	Limit limit = problem.getLimit();    
        	if (limit == null) {
        		limit = new Limit();
        		limit.setId(contestLimitId);
        		problem.setLimit(limit);
        	}
            if (limit.getId() != contestLimitId) {
            	ps = conn.prepareStatement(INSERT_LIMIT);
            	ps.setInt(1, limit.getTimeLimit());
            	ps.setInt(2, limit.getMemoryLimit());
            	ps.setInt(3, limit.getOutputLimit());
            	ps.setInt(4, limit.getSubmissionLimit());
            	ps.executeUpdate();                        
            	limit.setId(Database.getLastId(conn, ps, rs));            	
            }      
            
            // create the problem
            ps = conn.prepareStatement(INSERT_PROBLEM);            
            ps.setLong(1, problem.getContestId());
            ps.setString(2, problem.getTitle());
            ps.setString(3, problem.getCode());
            ps.setLong(4, limit.getId());
            
            ps.setString(5, problem.getAuthor());
            ps.setString(6, problem.getSource());
            ps.setString(7, problem.getContest());
            ps.setBoolean(8, problem.isChecker());
            ps.setInt(9, 0);
            
            ps.setLong(10, user);
            ps.setTimestamp(11, new Timestamp(new Date().getTime()));
            ps.setLong(12, user);
            ps.setTimestamp(13, new Timestamp(new Date().getTime()));
            ps.setString(14, problem.getColor());
            
            ps.executeUpdate();                                               
            problem.setId(Database.getLastId(conn, ps, rs));  
            
            conn.commit();
        } catch (SQLException e) {
        	Database.rollback(conn);
        	throw new PersistenceException("Failed to create problem.", e);
		} finally {
			Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Updates the specified problem in persistence layer.</p>
     *
     * @param problem the Problem instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateProblem(Problem problem, long user) throws PersistenceException{
    	if (problem == null) {
    		throw new NullPointerException("problem is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();   
        	conn.setAutoCommit(false);
        	
        	long contestLimitId = DEFAULT_LIMIT_ID;
        	ps = conn.prepareStatement(GET_CONTEST_LIMIT_ID);
        	ps.setLong(1, problem.getContestId());
        	rs = ps.executeQuery();
        	if (rs.next()) {
        		contestLimitId = rs.getLong(1);
        	}
        	
        	// update a new limit             
            Limit limit = problem.getLimit();
            if (limit.getId() != contestLimitId) {
                ps = conn.prepareStatement(INSERT_LIMIT);
                ps.setInt(1, limit.getTimeLimit());
                ps.setInt(2, limit.getMemoryLimit());
                ps.setInt(3, limit.getOutputLimit());
                ps.setInt(4, limit.getSubmissionLimit());
                ps.executeUpdate();                        
                limit.setId(Database.getLastId(conn, ps, rs));
                /*
            	ps = conn.prepareStatement(UPDATE_LIMIT);
            	ps.setInt(1, limit.getTimeLimit());
            	ps.setInt(2, limit.getMemoryLimit());
            	ps.setInt(3, limit.getOutputLimit());
            	ps.setInt(4, limit.getSubmissionLimit());
            	ps.setLong(5, limit.getId());
            	ps.executeUpdate();    
            	*/                                	
            }
            
            // update the problem
            ps = conn.prepareStatement(UPDATE_PROBLEM);            
            ps.setLong(1, problem.getContestId());
            ps.setString(2, problem.getTitle());
            ps.setString(3, problem.getCode());
            ps.setLong(4, limit.getId());            
            ps.setString(5, problem.getAuthor());
            ps.setString(6, problem.getSource());
            ps.setString(7, problem.getContest());
            ps.setBoolean(8, problem.isChecker());
            
            ps.setLong(9, user);
            ps.setTimestamp(10, new Timestamp(new Date().getTime()));
            ps.setString(11, problem.getColor());
            ps.setLong(12, problem.getId());
            ps.executeUpdate();                                                              
            
            conn.commit();
        } catch (SQLException e) {
        	Database.rollback(conn);        	
        	throw new PersistenceException("Failed to create problem.", e);
		} finally {
			Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Deletes the specified problem in persistence layer.</p>
     *
     * @param id the id of the problem to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteProblem(long id, long user) throws PersistenceException{
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(DELETE_PROBLEM);  
            ps.setLong(1, user);
            ps.setTimestamp(2, new Timestamp(new Date().getTime()));
            ps.setLong(3, id);
            
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such problem");
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
     * <p>Gets the problem with given id in persistence layer.</p>
     *
     * @param id the id of the problem
     * @return the problem with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public Problem getProblem(long id) throws PersistenceException{
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();        	
        	ps = conn.prepareStatement(GET_PROBLEM);   
            ps.setLong(1, id);
            rs = ps.executeQuery();
                                     
            if (!rs.next()) {
            	return null;
            }
            Problem problem = populateProblem(rs);
            long limitId = rs.getLong(DatabaseConstants.PROBLEM_LIMITS_ID);
            ps = conn.prepareStatement(GET_LIMIT);   
            ps.setLong(1, limitId);
            rs = ps.executeQuery();            
            if (rs.next()) {
            	Limit limit = populateLimit(rs);
            	problem.setLimit(limit);            	
            } 
            
            return problem;
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the contest with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * Populates a Problem with given ResultSet.
     * 
     * @param rs
     * @return an Problem instance
     * @throws SQLException
     */
    private Problem populateProblem(ResultSet rs) throws SQLException {
    	Problem problem = new Problem();

    	problem.setId(rs.getLong(DatabaseConstants.PROBLEM_PROBLEM_ID));
    	problem.setTitle(rs.getString(DatabaseConstants.PROBLEM_TITLE));  
    	problem.setContestId(rs.getLong(DatabaseConstants.PROBLEM_CONTEST_ID));
    	problem.setCode(rs.getString(DatabaseConstants.PROBLEM_CODE));
    	problem.setAuthor(rs.getString(DatabaseConstants.PROBLEM_AUTHOR));
    	problem.setSource(rs.getString(DatabaseConstants.PROBLEM_SOURCE));
    	problem.setContest(rs.getString(DatabaseConstants.PROBLEM_CONTEST));
    	problem.setChecker(rs.getBoolean(DatabaseConstants.PROBLEM_CHECKER));
    	problem.setRevision(rs.getInt(DatabaseConstants.PROBLEM_REVISION));
        problem.setColor(rs.getString(DatabaseConstants.PROBLEM_COLOR));
    	
    	return problem;
    }
    
    /**
     * Populates a Limit with given ResultSet.
     * 
     * @param rs
     * @return an Limit instance
     * @throws SQLException
     */
    private Limit populateLimit(ResultSet rs) throws SQLException {
    	
    	Limit limit = new Limit();
    	limit.setId(rs.getLong(DatabaseConstants.LIMITS_LIMITS_ID));
    	limit.setTimeLimit(rs.getInt(DatabaseConstants.LIMITS_TIME_LIMIT));
    	limit.setMemoryLimit(rs.getInt(DatabaseConstants.LIMITS_MEMORY_LIMIT));
    	limit.setSubmissionLimit(rs.getInt(DatabaseConstants.LIMITS_SUBMISSION_LIMIT));
    	limit.setOutputLimit(rs.getInt(DatabaseConstants.LIMITS_OUTPUT_LIMIT));
    	    	
    	return limit;
    }
    
    
    public List<Problem> searchProblems(ProblemCriteria criteria) throws PersistenceException{
    	return searchProblems(criteria, 0, Integer.MAX_VALUE);
    }
    
    
    /**
     * <p>Searches all problems according with the given criteria in persistence layer.</p>
     *
     * @return a list of problems according with the given criteria
     * @param criteria the problem search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of problems in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<Problem> searchProblems(ProblemCriteria criteria, int offset, int count) throws PersistenceException{
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
        	conn = Database.createConnection();  
        	String query = buildSearchQuery(criteria, offset, count);
        	ps = conn.prepareStatement(query);   
            rs = ps.executeQuery();
            
            List<Problem> problems = new ArrayList<Problem>();
            while (rs.next()) {
            	
	            Problem problem = populateProblem(rs);
	            Limit limit = populateLimit(rs);
	            problem.setLimit(limit);	            
	            problems.add(problem);
            }   
            return problems;
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to search the problems", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }
    
    
    public int getProblemsCount(long contestId) throws PersistenceException{
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = Database.createConnection();  
            String query = "select count(*) from problem where contest_id=?";            
            ps = conn.prepareStatement(query);   
            ps.setLong(1, contestId);
            rs = ps.executeQuery();
                   
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
            
        } catch (SQLException e) {
            throw new PersistenceException("Failed to search the problems count", e);
        } finally {
            Database.dispose(conn, ps, rs);
        }   
    }
    
    private String buildSearchQuery(ProblemCriteria criteria, int offset, int count) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(SEARCH_PROBLEMS);
    	if (criteria.getContestId() != null) {
    		sb.append(" AND " + DatabaseConstants.PROBLEM_CONTEST_ID + "=" + criteria.getContestId());
    	}
                
    	sb.append(" ORDER BY " + DatabaseConstants.PROBLEM_CODE);
        sb.append(" LIMIT " + offset + "," + count);
    	return sb.toString();
    	
    }
    
    

}


