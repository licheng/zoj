/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com> Xu, Chuan <xuchuan@gmail.com>
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;

/**
 * <p>
 * ProblemPersistenceImpl implements ProblemPersistence interface
 * </p>
 * <p>
 * ProblemPersistence interface defines the API used to manager the problem related affairs in persistence layer.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 * @author Xu, Chuan
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
            MessageFormat
                         .format("SELECT {0} FROM {1} WHERE {2}=?", new Object[] {DatabaseConstants.CONTEST_LIMITS_ID,
                                                                                  DatabaseConstants.CONTEST_TABLE,
                                                                                  DatabaseConstants.CONTEST_CONTEST_ID});

    /**
     * The statement to create a Problem.
     */
    private static final String INSERT_PROBLEM =
            MessageFormat.format(
                                 "INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13}, {14}, {15}, {16})"
                                     + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, ?, ?)",
                                 new Object[] {DatabaseConstants.PROBLEM_TABLE, DatabaseConstants.PROBLEM_CONTEST_ID,
                                               DatabaseConstants.PROBLEM_TITLE, DatabaseConstants.PROBLEM_CODE,
                                               DatabaseConstants.PROBLEM_LIMITS_ID, DatabaseConstants.PROBLEM_AUTHOR,
                                               DatabaseConstants.PROBLEM_SOURCE, DatabaseConstants.PROBLEM_CONTEST,
                                               DatabaseConstants.PROBLEM_CHECKER, DatabaseConstants.PROBLEM_REVISION,
                                               DatabaseConstants.CREATE_USER, DatabaseConstants.CREATE_DATE,
                                               DatabaseConstants.LAST_UPDATE_USER, DatabaseConstants.LAST_UPDATE_DATE,
                                               DatabaseConstants.CONTEST_ACTIVE, DatabaseConstants.PROBLEM_COLOR,
                                               DatabaseConstants.PROBLEM_SCORE});

    /**
     * The statement to update a Problem.
     */
    private static final String UPDATE_PROBLEM =
            MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=?, {7}=?, {8}=?, "
                + "{9}={9}+1, {10}=?, {11}=?, {12}={12}+1, {13}=?, {15}=? WHERE {14}=?",
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
                                               DatabaseConstants.PROBLEM_PROBLEM_ID,
                                               DatabaseConstants.PROBLEM_SCORE});

    /**
     * The statement to delete a problem.
     */
    private static final String DELETE_PROBLEM =
            MessageFormat.format("UPDATE {0} SET {1}=CONCAT({2}, {1}), {3}=CONCAT({2}, {3}), "
                + "{4}=0, {5}=?, {6}=? WHERE {2}=?", new Object[] {DatabaseConstants.PROBLEM_TABLE,
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
            MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {12}, {13} "
                + "FROM {10} WHERE {11}=1 AND {0}=?", new Object[] {DatabaseConstants.PROBLEM_PROBLEM_ID,
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
                                                                    DatabaseConstants.PROBLEM_SCORE});
    /*
     * The query to search problems.
     */
    private static final String SEARCH_PROBLEMS =
            MessageFormat.format(
                                 "SELECT {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, p.{9}, {10}, {11}, {12}, {13}, {17} "
                                     + "FROM {14} p LEFT JOIN {15} l ON p.{9} = l.{9} WHERE {16}=1 ",
                                 new Object[] {DatabaseConstants.PROBLEM_PROBLEM_ID,
                                               DatabaseConstants.PROBLEM_CONTEST_ID, DatabaseConstants.PROBLEM_TITLE,
                                               DatabaseConstants.PROBLEM_CODE, DatabaseConstants.PROBLEM_AUTHOR,
                                               DatabaseConstants.PROBLEM_SOURCE, DatabaseConstants.PROBLEM_CONTEST,
                                               DatabaseConstants.PROBLEM_CHECKER, DatabaseConstants.PROBLEM_REVISION,
                                               DatabaseConstants.PROBLEM_LIMITS_ID,
                                               DatabaseConstants.LIMITS_TIME_LIMIT,
                                               DatabaseConstants.LIMITS_MEMORY_LIMIT,
                                               DatabaseConstants.LIMITS_OUTPUT_LIMIT,
                                               DatabaseConstants.LIMITS_SUBMISSION_LIMIT,
                                               DatabaseConstants.PROBLEM_TABLE, DatabaseConstants.LIMITS_TABLE,
                                               DatabaseConstants.PROBLEM_ACTIVE, DatabaseConstants.PROBLEM_COLOR});

    /**
     * The statement to create a Limit.
     */
    // TODO(xuchuan): move all these into a LimitPersistence class
    private static final String INSERT_LIMIT =
            MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}) VALUES(?, ?, ?, ?)",
                                 new Object[] {DatabaseConstants.LIMITS_TABLE, DatabaseConstants.LIMITS_TIME_LIMIT,
                                               DatabaseConstants.LIMITS_MEMORY_LIMIT,
                                               DatabaseConstants.LIMITS_OUTPUT_LIMIT,
                                               DatabaseConstants.LIMITS_SUBMISSION_LIMIT});

    /**
     * The query to get a limit.
     */
    private static final String GET_LIMIT =
            MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4} FROM {5} WHERE {0}=?",
                                 new Object[] {DatabaseConstants.LIMITS_LIMITS_ID, DatabaseConstants.LIMITS_TIME_LIMIT,
                                               DatabaseConstants.LIMITS_MEMORY_LIMIT,
                                               DatabaseConstants.LIMITS_OUTPUT_LIMIT,
                                               DatabaseConstants.LIMITS_SUBMISSION_LIMIT,
                                               DatabaseConstants.LIMITS_TABLE});

    /**
     * <p>
     * Creates the specified problem in persistence layer.
     * </p>
     * 
     * @param problem
     *            the Problem instance to create
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void createProblem(Problem problem, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            Limit limit;
            try {
                long contestLimitId = ProblemPersistenceImpl.DEFAULT_LIMIT_ID;
                ps = conn.prepareStatement(ProblemPersistenceImpl.GET_CONTEST_LIMIT_ID);
                ps.setLong(1, problem.getContestId());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    contestLimitId = rs.getLong(1);
                }
                limit = problem.getLimit();
                if (limit == null) {
                    limit = new Limit();
                    limit.setId(contestLimitId);
                    problem.setLimit(limit);
                }
                if (limit.getId() != contestLimitId) {
                    ps = conn.prepareStatement(ProblemPersistenceImpl.INSERT_LIMIT);
                    ps.setInt(1, limit.getTimeLimit());
                    ps.setInt(2, limit.getMemoryLimit());
                    ps.setInt(3, limit.getOutputLimit());
                    ps.setInt(4, limit.getSubmissionLimit());
                    ps.executeUpdate();
                    limit.setId(Database.getLastId(conn));
                }
            } finally {
                Database.dispose(ps);
            }
            try {
                // create the problem
                ps = conn.prepareStatement(ProblemPersistenceImpl.INSERT_PROBLEM);
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
                ps.setInt(15, problem.getScore());
                ps.executeUpdate();
                problem.setId(Database.getLastId(conn));
            } finally {
                Database.dispose(ps);
            }
            conn.commit();
        } catch (Exception e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to create problem.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Updates the specified problem in persistence layer.
     * </p>
     * 
     * @param problem
     *            the Problem instance to update
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void updateProblem(Problem problem, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            long contestLimitId = ProblemPersistenceImpl.DEFAULT_LIMIT_ID;
            try {
                ps = conn.prepareStatement(ProblemPersistenceImpl.GET_CONTEST_LIMIT_ID);
                ps.setLong(1, problem.getContestId());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    contestLimitId = rs.getLong(1);
                }
            } finally {
                Database.dispose(ps);
            }
            // update a new limit
            Limit limit = problem.getLimit();
            if (limit.getId() != contestLimitId) {
                try {
                    ps = conn.prepareStatement(ProblemPersistenceImpl.INSERT_LIMIT);
                    ps.setInt(1, limit.getTimeLimit());
                    ps.setInt(2, limit.getMemoryLimit());
                    ps.setInt(3, limit.getOutputLimit());
                    ps.setInt(4, limit.getSubmissionLimit());
                    ps.executeUpdate();
                    limit.setId(Database.getLastId(conn));
                } finally {
                    Database.dispose(ps);
                }
            }
            try {
                // update the problem
                ps = conn.prepareStatement(ProblemPersistenceImpl.UPDATE_PROBLEM);
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
                ps.setInt(12, problem.getScore());
                ps.setLong(13, problem.getId());
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            conn.commit();
        } catch (Exception e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to create problem.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Deletes the specified problem in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the problem to delete
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void deleteProblem(long id, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(ProblemPersistenceImpl.DELETE_PROBLEM);
                ps.setLong(1, user);
                ps.setTimestamp(2, new Timestamp(new Date().getTime()));
                ps.setLong(3, id);
                if (ps.executeUpdate() == 0) {
                    throw new PersistenceException("no such problem");
                }
            } finally {
                Database.dispose(ps);
            }
        } catch (PersistenceException pe) {
            throw pe;
        } catch (Exception e) {
            throw new PersistenceException("Failed to delete contest.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Gets the problem with given id in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the problem
     * @return the problem with given id in persistence layer
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public Problem getProblem(long id) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(ProblemPersistenceImpl.GET_PROBLEM);
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                Problem problem = this.populateProblem(rs);
                long limitId = rs.getLong(DatabaseConstants.PROBLEM_LIMITS_ID);
                ps = conn.prepareStatement(ProblemPersistenceImpl.GET_LIMIT);
                ps.setLong(1, limitId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    Limit limit = this.populateLimit(rs);
                    problem.setLimit(limit);
                }
                return problem;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the contest with id " + id, e);
        } finally {
            Database.dispose(conn);
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
        problem.setScore(rs.getInt(DatabaseConstants.PROBLEM_SCORE));
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

    public List<Problem> searchProblems(ProblemCriteria criteria) throws PersistenceException {
        return this.searchProblems(criteria, 0, Integer.MAX_VALUE);
    }

    /**
     * <p>
     * Searches all problems according with the given criteria in persistence layer.
     * </p>
     * 
     * @return a list of problems according with the given criteria
     * @param criteria
     *            the problem search criteria
     * @param offset
     *            the offset of the start position to search
     * @param count
     *            the maximum number of problems in returned list
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<Problem> searchProblems(ProblemCriteria criteria, int offset, int count) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(this.buildSearchQuery(criteria, offset, count));
                ResultSet rs = ps.executeQuery();
                List<Problem> problems = new ArrayList<Problem>();
                while (rs.next()) {
                    Problem problem = this.populateProblem(rs);
                    Limit limit = this.populateLimit(rs);
                    problem.setLimit(limit);
                    problems.add(problem);
                }
                return problems;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to search the problems", e);
        } finally {
            Database.dispose(conn);
        }
    }

    public int getProblemsCount(long contestId) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("select count(*) from problem where contest_id=? and active=1");
                ps.setLong(1, contestId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to search the problems count", e);
        } finally {
            Database.dispose(conn);
        }
    }

    private String buildSearchQuery(ProblemCriteria criteria, int offset, int count) {
        StringBuffer sb = new StringBuffer();
        sb.append(ProblemPersistenceImpl.SEARCH_PROBLEMS);
        if (criteria.getContestId() != null) {
            sb.append(" AND " + DatabaseConstants.PROBLEM_CONTEST_ID + "=" + criteria.getContestId());
        }
	if (criteria.getCode() != null) {
            sb.append(" AND " + DatabaseConstants.PROBLEM_CODE + "='" + criteria.getCode() +"'");
        }
        sb.append(" ORDER BY " + DatabaseConstants.PROBLEM_CODE);
        sb.append(" LIMIT " + offset + "," + count);
        System.out.println(sb.toString());
        return sb.toString();
    }
}
