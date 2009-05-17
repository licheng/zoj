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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Course;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * ContestPersistenceImpl implements ContestPersistence interface
 * </p>
 * <p>
 * ContestPersistence interface defines the API used to manager the contest related affairs in persistence layer.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 * @author Xu, Chuan
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
            MessageFormat
                         .format("SELECT {0} FROM {1} WHERE {2}=?", new Object[] {DatabaseConstants.CONTEST_LIMITS_ID,
                                                                                  DatabaseConstants.CONTEST_TABLE,
                                                                                  DatabaseConstants.CONTEST_CONTEST_ID});

    /**
     * The statement to update problem limit id.
     */
    private static final String UPDATE_PROBLEM_LIMIT =
            MessageFormat.format("UPDATE {0} SET {1}=? WHERE {2}=? AND {3}=?",
                                 new Object[] {DatabaseConstants.PROBLEM_TABLE, DatabaseConstants.PROBLEM_LIMITS_ID,
                                               DatabaseConstants.PROBLEM_LIMITS_ID,
                                               DatabaseConstants.PROBLEM_CONTEST_ID});

    /**
     * The statement to get the default limit.
     */
    private static final String SELECT_DEFAULT_LIMIT =
            MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4} FROM {5} WHERE {0}=" +
                ContestPersistenceImpl.DEFAULT_LIMIT_ID, new Object[] {DatabaseConstants.LIMITS_LIMITS_ID,
                                                                       DatabaseConstants.LIMITS_TIME_LIMIT,
                                                                       DatabaseConstants.LIMITS_MEMORY_LIMIT,
                                                                       DatabaseConstants.LIMITS_OUTPUT_LIMIT,
                                                                       DatabaseConstants.LIMITS_SUBMISSION_LIMIT,
                                                                       DatabaseConstants.LIMITS_TABLE});

    /**
     * The statement to create a Contest.
     */
    private static final String INSERT_CONTEST =
            MessageFormat.format(
                                 "INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, {13})"
                                     + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, ?)",
                                 new Object[] {DatabaseConstants.CONTEST_TABLE, DatabaseConstants.CONTEST_TITLE,
                                               DatabaseConstants.CONTEST_DESCRIPTION,
                                               DatabaseConstants.CONTEST_START_TIME,
                                               DatabaseConstants.CONTEST_END_TIME, DatabaseConstants.CONTEST_FORUM_ID,
                                               DatabaseConstants.CONTEST_LIMITS_ID,
                                               DatabaseConstants.CONTEST_PROBLEMSET, DatabaseConstants.CREATE_USER,
                                               DatabaseConstants.CREATE_DATE, DatabaseConstants.LAST_UPDATE_USER,
                                               DatabaseConstants.LAST_UPDATE_DATE, DatabaseConstants.CONTEST_ACTIVE,
                                               DatabaseConstants.CONTEST_CHECK_IP});

    /**
     * The statement to update a Contest.
     */
    private static final String UPDATE_CONTEST =
            MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=?, {7}=?, {8}=?, "
                + "{9}=?, {10}=? WHERE {11}=?", new Object[] {DatabaseConstants.CONTEST_TABLE,
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
                                 new Object[] {DatabaseConstants.CONTEST_TABLE, DatabaseConstants.CONTEST_ACTIVE,
                                               DatabaseConstants.LAST_UPDATE_USER, DatabaseConstants.LAST_UPDATE_DATE,
                                               DatabaseConstants.CONTEST_CONTEST_ID});

    /**
     * The query to get a contest.
     */
    private static final String GET_CONTEST =
            MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4}, {5}, {12}.{6}, {7}, {8}, {9}, {10}, {11}, {16} "
                + "FROM {12} LEFT JOIN {13} ON ({12}.{6}={13}.{14}) WHERE {15}=1",
                                 new Object[] {DatabaseConstants.CONTEST_CONTEST_ID, DatabaseConstants.CONTEST_TITLE,
                                               DatabaseConstants.CONTEST_DESCRIPTION,
                                               DatabaseConstants.CONTEST_START_TIME,
                                               DatabaseConstants.CONTEST_END_TIME, DatabaseConstants.CONTEST_FORUM_ID,
                                               DatabaseConstants.CONTEST_LIMITS_ID,
                                               DatabaseConstants.CONTEST_PROBLEMSET,
                                               DatabaseConstants.LIMITS_TIME_LIMIT,
                                               DatabaseConstants.LIMITS_MEMORY_LIMIT,
                                               DatabaseConstants.LIMITS_OUTPUT_LIMIT,
                                               DatabaseConstants.LIMITS_SUBMISSION_LIMIT,
                                               DatabaseConstants.CONTEST_TABLE, DatabaseConstants.LIMITS_TABLE,
                                               DatabaseConstants.LIMITS_LIMITS_ID, DatabaseConstants.CONTEST_ACTIVE,
                                               DatabaseConstants.CONTEST_CHECK_IP});

    /**
     * The statement to create a Limit.
     */
    private static final String INSERT_LIMIT =
            MessageFormat.format("INSERT IGNORE INTO {0} ({1}, {2}, {3}, {4}) VALUES(?, ?, ?, ?)",
                                 new Object[] {DatabaseConstants.LIMITS_TABLE, DatabaseConstants.LIMITS_TIME_LIMIT,
                                               DatabaseConstants.LIMITS_MEMORY_LIMIT,
                                               DatabaseConstants.LIMITS_OUTPUT_LIMIT,
                                               DatabaseConstants.LIMITS_SUBMISSION_LIMIT});

    /**
     * The statement to update a Limit.
     */
    private static final String UPDATE_LIMIT =
            MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=? WHERE {5}=?",
                                 new Object[] {DatabaseConstants.LIMITS_TABLE, DatabaseConstants.LIMITS_TIME_LIMIT,
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
                                               DatabaseConstants.CONTEST_LANGUAGE_TABLE, "{0}"});

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
     * The defaultLimit = null;
     */
    private static Limit defaultLimit = null;

    static {
        try {
            ContestPersistenceImpl.loadDefaultLimit();
        } catch (PersistenceException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Gets the default limit.
     * 
     * @return the default limit.
     * @throws PersistenceException
     *             if failed to get the default limit
     */
    public Limit getDefaultLimit() {
        return ContestPersistenceImpl.defaultLimit;
    }

    /**
     * Update the default limit.
     * 
     * @param limit
     *            the default limit.
     * @throws PersistenceException
     *             if failed to update the default limit
     */
    public void updateDefaultLimit(Limit limit) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            synchronized (ContestPersistenceImpl.class) {
                try {
                    ps = conn.prepareStatement(ContestPersistenceImpl.UPDATE_LIMIT);
                    ps.setInt(1, limit.getTimeLimit());
                    ps.setInt(2, limit.getMemoryLimit());
                    ps.setInt(3, limit.getOutputLimit());
                    ps.setInt(4, limit.getSubmissionLimit());
                    ps.setLong(5, ContestPersistenceImpl.DEFAULT_LIMIT_ID);
                    ps.executeUpdate();
                } finally {
                    Database.dispose(ps);
                }
                ContestPersistenceImpl.defaultLimit = limit;
            }
        } catch (Exception e) {
            throw new PersistenceException("Failed to update the default limit", e);
        } finally {
            Database.dispose(conn);
        }

    }

    /**
     * <p>
     * Creates the specified contest in persistence layer.
     * </p>
     * 
     * @param contest
     *            the AbstractContest instance to create
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void createContest(AbstractContest contest, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            Limit limit = contest.getLimit();
            try {
                // create a new limit
                if (limit != null && limit.getId() != ContestPersistenceImpl.DEFAULT_LIMIT_ID) {
                    ps = conn.prepareStatement(ContestPersistenceImpl.INSERT_LIMIT);
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
                // create the contest
                ps = conn.prepareStatement(ContestPersistenceImpl.INSERT_CONTEST);
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
                if (limit == null || limit.getId() == ContestPersistenceImpl.DEFAULT_LIMIT_ID) {
                    ps.setLong(6, ContestPersistenceImpl.DEFAULT_LIMIT_ID);
                } else {
                    ps.setLong(6, limit.getId());
                }
                int contesttype=0;
                if(contest instanceof Problemset) {
                	contesttype=1;
                }
                if(contest instanceof Course) {
                	contesttype=2;
                }
                ps.setInt(7, contesttype);
                ps.setLong(8, user);
                ps.setTimestamp(9, new Timestamp(new Date().getTime()));
                ps.setLong(10, user);
                ps.setTimestamp(11, new Timestamp(new Date().getTime()));
                ps.setBoolean(12, contest.isCheckIp());
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            contest.setId(Database.getLastId(conn));

            // create languages
            if (contest.getLanguages() != null) {
                for (Language language : contest.getLanguages()) {
                    try {
                        ps = conn.prepareStatement(ContestPersistenceImpl.INSERT_CONTEST_LANGUAGE);
                        ps.setLong(1, contest.getId());
                        ps.setLong(2, language.getId());
                        ps.executeUpdate();
                    } finally {
                        Database.dispose(ps);
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to create contest.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Updates the specified contest in persistence layer.
     * </p>
     * 
     * @param contest
     *            the AbstractContest instance to update
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void updateContest(AbstractContest contest, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            long contestLimitId = ContestPersistenceImpl.DEFAULT_LIMIT_ID;
            try {
                ps = conn.prepareStatement(ContestPersistenceImpl.GET_CONTEST_LIMIT_ID);
                ps.setLong(1, contest.getId());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    contestLimitId = rs.getLong(1);
                }
            } finally {
                Database.dispose(ps);
            }

            // update the limit
            Limit limit = contest.getLimit();
            if (limit.getId() != ContestPersistenceImpl.DEFAULT_LIMIT_ID) {
                try {
                    ps = conn.prepareStatement(ContestPersistenceImpl.INSERT_LIMIT);
                    ps.setInt(1, limit.getTimeLimit());
                    ps.setInt(2, limit.getMemoryLimit());
                    ps.setInt(3, limit.getOutputLimit());
                    ps.setInt(4, limit.getSubmissionLimit());
                    ps.executeUpdate();
                } finally {
                    Database.dispose(ps);
                }
                limit.setId(Database.getLastId(conn));
            }
            if (contestLimitId != limit.getId()) {
                // TODO(xuchuan) I don't understand what's that.
                try {
                    ps = conn.prepareStatement(ContestPersistenceImpl.UPDATE_PROBLEM_LIMIT);
                    ps.setLong(1, limit.getId());
                    ps.setLong(2, contest.getId());
                    ps.setLong(3, contestLimitId);
                    ps.executeUpdate();
                } finally {
                    Database.dispose(ps);
                }
            }

            try {
                // update the contest
                ps = conn.prepareStatement(ContestPersistenceImpl.UPDATE_CONTEST);
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
                if (limit == null || limit.getId() == ContestPersistenceImpl.DEFAULT_LIMIT_ID) {
                    ps.setLong(6, ContestPersistenceImpl.DEFAULT_LIMIT_ID);
                } else {
                    ps.setLong(6, limit.getId());
                }
                int contesttype=0;
                if(contest instanceof Course) {
                	contesttype=2;
                } else if (contest instanceof Problemset) {
                	contesttype=1;
                }
                ps.setInt(7, contesttype);
                ps.setLong(8, user);
                ps.setTimestamp(9, new Timestamp(new Date().getTime()));
                ps.setBoolean(10, contest.isCheckIp());
                ps.setLong(11, contest.getId());
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }

            try {
                // delete languages
                ps = conn.prepareStatement(ContestPersistenceImpl.DELETE_CONTEST_LANGUAGE);
                ps.setLong(1, contest.getId());
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }

            // insert languages
            if (contest.getLanguages() != null) {
                for (Language language : contest.getLanguages()) {
                    try {
                        ps = conn.prepareStatement(ContestPersistenceImpl.INSERT_CONTEST_LANGUAGE);
                        ps.setLong(1, contest.getId());
                        ps.setLong(2, language.getId());
                        ps.executeUpdate();
                    } finally {
                        Database.dispose(ps);
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to create contest.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Deletes the specified contest in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the contest to delete
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void deleteContest(long id, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(ContestPersistenceImpl.DELETE_CONTEST);
                ps.setLong(1, user);
                ps.setTimestamp(2, new Timestamp(new Date().getTime()));
                ps.setLong(3, id);
                if (ps.executeUpdate() == 0) {
                    throw new PersistenceException("no such contest");
                }
            } finally {
                Database.dispose(ps);
            }
        } catch (PersistenceException e) {
            throw e;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to delete contest.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Gets the contest with given id in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the contest
     * @return the contest with given id in persistence layer
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public AbstractContest getContest(long id) throws PersistenceException {
    	Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            AbstractContest contest;
            try {
                ps =
                        conn.prepareStatement(ContestPersistenceImpl.GET_CONTEST + " AND " +
                            DatabaseConstants.CONTEST_CONTEST_ID + "=" + id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    contest = this.populateContest(rs);
                } else {
                    return null;
                }
            } finally {
                Database.dispose(ps);
            }
            List<AbstractContest> contests = new ArrayList<AbstractContest>();
            contests.add(contest);
            this.populatesLanguages(conn, contests);
            return contest;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the contest with id " + id, e);
        } finally {
            Database.dispose(conn);
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
    private void populatesLanguages(Connection conn, List<AbstractContest> contests) throws SQLException,
            PersistenceException {
        if (contests.size() == 0) {
            return;
        }
        Map<Long, Language> languageMap = PersistenceManager.getInstance().getLanguagePersistence().getLanguageMap();
        PreparedStatement ps = null;
        try {
            Map<Long, AbstractContest> contestMap = new HashMap<Long, AbstractContest>();
            List<Long> contestIds = new ArrayList<Long>();
            for (AbstractContest contest : contests) {
                contestIds.add(contest.getId());
                contestMap.put(contest.getId(), contest);
                contest.setLanguages(new ArrayList<Language>());
            }
            ps =
                    conn.prepareStatement(MessageFormat.format(ContestPersistenceImpl.GET_CONTEST_LANGUAGE,
                                                               new Object[] {Database.createNumberValues(contestIds)}));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Long contestId = new Long(rs.getLong(DatabaseConstants.CONTEST_LANGUAGE_CONTEST_ID));
                Long languageId = new Long(rs.getLong(DatabaseConstants.CONTEST_LANGUAGE_LANGUAGE_ID));
                contestMap.get(contestId).getLanguages().add(languageMap.get(languageId));
            }
        } finally {
            Database.dispose(ps);
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
        int contestType=rs.getInt(DatabaseConstants.CONTEST_PROBLEMSET);
        if (contestType==1) {
            contest = new Problemset();
        } else if (contestType==0) {
            contest = new Contest();
        } else {
            contest = new Course();
        }
        if (rs.getTimestamp(DatabaseConstants.CONTEST_START_TIME) != null) {
            contest.setStartTime(new Date(rs.getTimestamp(DatabaseConstants.CONTEST_START_TIME).getTime()));
        }
        if (rs.getTimestamp(DatabaseConstants.CONTEST_END_TIME) != null) {
            contest.setEndTime(new Date(rs.getTimestamp(DatabaseConstants.CONTEST_END_TIME).getTime()));
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
     * <p>
     * Gets all contests in persistence layer.
     * </p>
     * 
     * @return a list of Contest instances containing all contests in persistence layer
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<AbstractContest> getAllContests() throws PersistenceException {
        return this.getContests(0);
    }

    /**
     * <p>
     * Gets all problem sets in persistence layer.
     * </p>
     * 
     * @return a list of ProblemSet instances containing all problem sets in persistence layer
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<AbstractContest> getAllProblemsets() throws PersistenceException {
        return this.getContests(1);
    }

    /**
     * <p>
     * Gets a list of contests with given type in persistence layer.
     * </p>
     * 
     * @param isProblemset
     * @return a list of ProblemSet instances containing all problem sets in persistence layer
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    private List<AbstractContest> getContests(int contestType) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            List<AbstractContest> contests = new ArrayList<AbstractContest>();
            try {
                ps =
                        conn.prepareStatement(ContestPersistenceImpl.GET_CONTEST + " AND " +
                            DatabaseConstants.CONTEST_PROBLEMSET + "=" + contestType +
                            " ORDER BY start_time DESC");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    AbstractContest contest = this.populateContest(rs);
                    contests.add(contest);
                }
            } finally {
                Database.dispose(ps);
            }
            this.populatesLanguages(conn, contests);
            return contests;
        } catch (Exception e) {
            throw new PersistenceException("Failed to get the contests", e);
        } finally {
            Database.dispose(conn);
        }
    }

    public String getLastSubmitIP(long userId, long contestId) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("SELECT ip FROM user_contest_ip WHERE user_profile_id=? AND contest_id=?");
                ps.setLong(1, userId);
                ps.setLong(2, contestId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    return null;
                }
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get last submit ip", e);
        } finally {
            Database.dispose(conn);
        }

    }

    public void setLastSubmitIP(long userId, long contestId, String ip) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            int ret;
            try {
                ps = conn.prepareStatement("UPDATE user_contest_ip SET ip=? WHERE user_profile_id=? AND contest_id=?");
                ps.setString(1, ip);
                ps.setLong(2, userId);
                ps.setLong(3, contestId);
                ret = ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            if (ret == 0) {
                try {
                    ps =
                            conn
                                .prepareStatement("INSERT INTO user_contest_ip(user_profile_id, contest_id, ip) VALUES(?,?,?)");
                    ps.setLong(1, userId);
                    ps.setLong(2, contestId);
                    ps.setString(3, ip);
                    ps.executeUpdate();
                } finally {
                    Database.dispose(ps);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to set last submit ip", e);
        } finally {
            Database.dispose(conn);
        }
    }

    private static void loadDefaultLimit() throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(ContestPersistenceImpl.SELECT_DEFAULT_LIMIT);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ContestPersistenceImpl.defaultLimit = new Limit();
                    ContestPersistenceImpl.defaultLimit.setId(rs.getLong(DatabaseConstants.LIMITS_LIMITS_ID));
                    ContestPersistenceImpl.defaultLimit
                                                       .setMemoryLimit(rs.getInt(DatabaseConstants.LIMITS_MEMORY_LIMIT));
                    ContestPersistenceImpl.defaultLimit
                                                       .setOutputLimit(rs.getInt(DatabaseConstants.LIMITS_OUTPUT_LIMIT));
                    ContestPersistenceImpl.defaultLimit
                                                       .setSubmissionLimit(rs
                                                                             .getInt(DatabaseConstants.LIMITS_SUBMISSION_LIMIT));
                    ContestPersistenceImpl.defaultLimit.setTimeLimit(rs.getInt(DatabaseConstants.LIMITS_TIME_LIMIT));
                }
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the default limit", e);
        } finally {
            Database.dispose(conn);
        }
    }

	public List<AbstractContest> getAllCourses() throws PersistenceException {
		return this.getContests(2);
	}
}
