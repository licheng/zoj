/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com> Chen, Zhengguang <cerrorism@gmail.com> Xu, Chuan <xuchuan@gmail.com>
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.QQ;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.ProblemStatistics;
import cn.edu.zju.acm.onlinejudge.util.ProblemsetRankList;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.UserStatistics;

/**
 * <p>
 * SubmissionPersistenceImpl implements SubmissionPersistence interface.
 * </p>
 * <p>
 * SubmissionPersistence interface defines the API used to manager the submission related affairs in persistence layer.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 * @author Xu, Chuan
 * @author Chen, Zhengguang
 */
public class SubmissionPersistenceImpl implements SubmissionPersistence {

    /**
     * The statement to create a Submission.
     */
    private static final String INSERT_SUBMISSION =
            MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11},"
                + " {12}, {13}, {14}, {15}, {16}, {17}) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)",
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
                                               DatabaseConstants.CREATE_USER, DatabaseConstants.CREATE_DATE,
                                               DatabaseConstants.LAST_UPDATE_USER, DatabaseConstants.LAST_UPDATE_DATE,
                                               "contest_id", "contest_order", DatabaseConstants.SUBMISSION_ACTIVE});

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
                + "{9}=?, {10}=?, {11}=? WHERE {12}=?", new Object[] {DatabaseConstants.SUBMISSION_TABLE,
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
                                 new Object[] {DatabaseConstants.SUBMISSION_TABLE, DatabaseConstants.SUBMISSION_ACTIVE,
                                               DatabaseConstants.LAST_UPDATE_USER, DatabaseConstants.LAST_UPDATE_DATE,
                                               DatabaseConstants.SUBMISSION_SUBMISSION_ID});

    private static final String GET_SUBMISSION_PREFIX =
            "SELECT s.submission_id,s.problem_id,s.language_id,s.judge_reply_id,s.user_profile_id,s.time_consumption,"
                + "s.memory_consumption,s.submission_date,s.judge_date,s.judge_comment,s.contest_id,s.contest_order,u.handle,u.nickname,p.code";

    private static final String GET_SUBMISSION_WITH_CONTENT_PREFIX =
            SubmissionPersistenceImpl.GET_SUBMISSION_PREFIX + ",s.content";

    private static final String GET_SUBMISSION_FROM_PART =
            " FROM submission s FORCE_INDEX " + "LEFT JOIN user_profile u ON s.user_profile_id = u.user_profile_id "
                + "LEFT JOIN problem p ON s.problem_id = p.problem_id "
                + "WHERE s.active=1 AND u.active=1 AND p.active=1 ";

    private static final String GET_SUBMISSION =
            SubmissionPersistenceImpl.GET_SUBMISSION_WITH_CONTENT_PREFIX +
                SubmissionPersistenceImpl.GET_SUBMISSION_FROM_PART + " AND s.submission_id=?";

    private static final String GET_SUBMISSIONS =
            SubmissionPersistenceImpl.GET_SUBMISSION_PREFIX + SubmissionPersistenceImpl.GET_SUBMISSION_FROM_PART;

    /**
     * The query to get submissions.
     */
    private static final String GET_SUBMISSIONS_WITH_CONTENT =
            SubmissionPersistenceImpl.GET_SUBMISSION_WITH_CONTENT_PREFIX +
                SubmissionPersistenceImpl.GET_SUBMISSION_FROM_PART;

    /**
     * <p>
     * Creates the specified submission in persistence layer.
     * </p>
     * 
     * @param submission
     *            the Submission instance to create
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void createSubmission(Submission submission, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            String maxOrder = null;
            try {
                ps =
                        conn.prepareStatement("select max(contest_order) from submission where contest_id=" +
                            submission.getContestId());
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    maxOrder = rs.getString(1);
                }
            } finally {
                Database.dispose(ps);
            }
            long count = maxOrder == null ? 0 : Long.parseLong(maxOrder) + 1;
            submission.setContestOrder(count);
            try {
                // create the submission
                ps = conn.prepareStatement(SubmissionPersistenceImpl.INSERT_SUBMISSION);
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
            } finally {
                Database.dispose(ps);
            }
            submission.setId(Database.getLastId(conn));
            conn.commit();
        } catch (Exception e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to insert submission.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Updates the specified submission in persistence layer.
     * </p>
     * 
     * @param submission
     *            the Submission instance to update
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void updateSubmission(Submission submission, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            // update the submission
            PreparedStatement ps = null;
            try {
                ps =
                        conn
                            .prepareStatement(submission.getContent() == null ? SubmissionPersistenceImpl.UPDATE_SUBMISSION_WITHOUT_CONTENT
                                                                             : SubmissionPersistenceImpl.UPDATE_SUBMISSION);
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
            } finally {
                Database.dispose(ps);
            }
            // TODO(ob): update the user statistics if no tiger?
            conn.commit();
        } catch (Exception e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to update submission.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Deletes the specified submission in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the submission to delete
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void deleteSubmission(long id, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(SubmissionPersistenceImpl.INACTIVE_SUBMISSION);
                ps.setLong(1, user);
                ps.setTimestamp(2, new Timestamp(new Date().getTime()));
                ps.setLong(3, id);
                if (ps.executeUpdate() == 0) {
                    throw new PersistenceException("no such submission");
                }
            } finally {
                Database.dispose(ps);
            }
        } catch (Exception e) {
            throw new PersistenceException("Failed to delete submission.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Gets the submission with given id in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the submission
     * @return the submission with given id in persistence layer
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public Submission getSubmission(long id) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(SubmissionPersistenceImpl.GET_SUBMISSION.replace("FORCE_INDEX", ""));
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                Map<Long, Language> languageMap =
                        PersistenceManager.getInstance().getLanguagePersistence().getLanguageMap();
                Submission submission = this.populateSubmission(rs, true, languageMap);
                return submission;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the submission with id " + id, e);
        } finally {
            Database.dispose(conn);
        }
    }

    public String getSubmissionSource(long id) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("SELECT content FROM submission WHERE submission_id=?");
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new PersistenceException("Submission id " + id + " not found");
                }
                String content = rs.getString("content");
                if (content == null) {
                    return "";
                } else {
                    return content;
                }
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the submission with id " + id, e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * Populates an ExtendedSubmission with given ResultSet.
     * 
     * @param rs
     * @return an ExtendedSubmission instance
     * @throws SQLException
     */
    private Submission populateSubmission(ResultSet rs, boolean withContent, Map<Long, Language> languageMap) throws SQLException {
        Submission submission = new Submission();
        submission.setId(rs.getLong(DatabaseConstants.SUBMISSION_SUBMISSION_ID));
        submission.setProblemId(rs.getLong(DatabaseConstants.SUBMISSION_PROBLEM_ID));
        submission.setUserProfileId(rs.getLong(DatabaseConstants.SUBMISSION_USER_PROFILE_ID));
        submission.setJudgeComment(rs.getString(DatabaseConstants.SUBMISSION_JUDGE_COMMENT));
        submission.setJudgeDate(Database.getDate(rs, DatabaseConstants.SUBMISSION_JUDGE_DATE));
        submission.setSubmitDate(Database.getDate(rs, DatabaseConstants.SUBMISSION_SUBMISSION_DATE));
        submission.setMemoryConsumption(rs.getInt(DatabaseConstants.SUBMISSION_MEMORY_CONSUMPTION));
        submission.setTimeConsumption(rs.getInt(DatabaseConstants.SUBMISSION_TIME_CONSUMPTION));
        submission.setUserName(rs.getString(DatabaseConstants.USER_PROFILE_NICKNAME));
	 if(submission.getUserName().equals("")) {
             submission.setUserName(rs.getString(DatabaseConstants.USER_PROFILE_HANDLE));
        }
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
        JudgeReply judgeReply = JudgeReply.findById(judgeReplyId);
        submission.setJudgeReply(judgeReply);

        return submission;
    }

    /**
     * <p>
     * Searches all submissions according with the given criteria in persistence layer.
     * </p>
     * 
     * @return a list of submissions according with the given criteria
     * @param criteria
     *            the submission search criteria
     * @param lastId
     *            the last id
     * @param count
     *            the maximum number of submissions in returned list
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<Submission> searchSubmissions(SubmissionCriteria criteria, long firstId, long lastId, int count) throws PersistenceException {
        return this.searchSubmissions(criteria, firstId, lastId, count, false);
    }

    /**
     * <p>
     * Searches all submissions according with the given criteria in persistence layer.
     * </p>
     * 
     * @return a list of submissions according with the given criteria
     * @param criteria
     *            the submission search criteria
     * @param lastId
     *            the last id
     * @param count
     *            the maximum number of submissions in returned list
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<Submission> searchSubmissions(SubmissionCriteria criteria, long firstId, long lastId, int count,
                                              boolean withContent) throws PersistenceException {
        if (lastId < 0) {
            throw new IllegalArgumentException("offset is negative");
        }
        if (count < 0) {
            throw new IllegalArgumentException("count is negative");
        }
        Connection conn = null;
        Map<Long, Language> languageMap = PersistenceManager.getInstance().getLanguagePersistence().getLanguageMap();
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            if (criteria.getUserId() == null && criteria.getHandle() != null) {
                try {
                    ps = conn.prepareStatement("select user_profile_id from user_profile where handle=? AND active=1");
                    ps.setString(1, criteria.getHandle());
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        return new ArrayList<Submission>();
                    }
                    long userId = rs.getLong(1);
                    criteria.setUserId(userId);
                } finally {
                    Database.dispose(ps);
                }
            }
            if (criteria.getProblemId() == null && criteria.getProblemCode() != null) {
                try {
                    ps =
                            conn
                                .prepareStatement("select problem_id from problem where code=? AND contest_id=? AND active=1");
                    ps.setString(1, criteria.getProblemCode());
                    ps.setLong(2, criteria.getContestId());
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        return new ArrayList<Submission>();
                    }
                    long problemId = rs.getLong(1);
                    criteria.setProblemId(problemId);
                } finally {
                    Database.dispose(ps);
                }
            }
            try {
                ps =
                        this.buildQuery(withContent ? SubmissionPersistenceImpl.GET_SUBMISSIONS_WITH_CONTENT
                                                   : SubmissionPersistenceImpl.GET_SUBMISSIONS, criteria, firstId,
                                        lastId, count, conn);
                if (ps == null) {
                    return new ArrayList<Submission>();
                }
                ResultSet rs = ps.executeQuery();
                List<Submission> submissions = new ArrayList<Submission>();
                while (rs.next()) {
                    Submission submission = this.populateSubmission(rs, withContent, languageMap);
                    submissions.add(submission);
                }
                return submissions;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the submissions", e);
        } finally {
            Database.dispose(conn);
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
    private PreparedStatement buildQuery(String perfix, SubmissionCriteria criteria, long firstId, long lastId,
                                         int count, Connection conn) throws SQLException {

        // String userIndex = "index_submission_user";
        // String problemIndex = "index_submission_problem";
        String userIndex = "index_submission_user_reply_contest";
        String problemIndex = "index_submission_problem_reply";

        String judgeReplyIndex = "fk_submission_reply";
        String defaultIndex = "index_submission_contest_order";

        Set<String> easyProblems =
                new HashSet<String>(Arrays.asList(new String[] {"2060", "1180", "1067", "1292", "1295", "1951", "1025",
                                                                "2095", "2105", "1008", "1005", "1152", "1240", "2107",
                                                                "1037", "1205", "1113", "1045", "1489", "1241", "1101",
                                                                "1049", "1057", "1003", "1151", "1048", "1002", "1115",
                                                                "1001"}));
        Set<JudgeReply> easyJudgeReply =
                new HashSet<JudgeReply>(Arrays.asList(new JudgeReply[] {JudgeReply.ACCEPTED, JudgeReply.WRONG_ANSWER,
                                                                        JudgeReply.TIME_LIMIT_EXCEEDED,
                                                                        JudgeReply.MEMORY_LIMIT_EXCEEDED,
                                                                        JudgeReply.SEGMENTATION_FAULT,
                                                                        JudgeReply.COMPILATION_ERROR,
                                                                        JudgeReply.PRESENTATION_ERROR}));

        /*
         * INDEX optimization If user id presents, use fk_submission_user If problem id presents and submission number <
         * 5000, use fk_submission_problem; If judge_reply_id presents and none of id is 4,5,6,7,12,13 or 16, use
         * fk_submission_reply when otherwise use index_submission_contest_order;
         */
        String order = firstId == -1 ? "DESC" : "ASC";
        if (criteria.getIdStart() != null && firstId < criteria.getIdStart() - 1) {
            firstId = criteria.getIdStart() - 1;
        }

        if (criteria.getIdEnd() != null && lastId > criteria.getIdEnd() + 1) {
            lastId = criteria.getIdEnd() + 1;
        }

        StringBuilder query = new StringBuilder();
        query.append(perfix);
        query.append(" AND s.contest_id=" + criteria.getContestId());
        query.append(" AND contest_order BETWEEN " + (firstId + 1) + " and " + (lastId - 1));

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
        PreparedStatement ps = null;
        if (index == null && criteria.getJudgeReplies() != null && criteria.getProblemId() != null) {
            try {
                ps =
                        conn.prepareStatement("SELECT count(*) from submission s where problem_id=" +
                            criteria.getProblemId() + inCondition);
                ResultSet rs = ps.executeQuery();
                rs.next();
                long cnt = rs.getLong(1);
                if (cnt < 10000) {
                    index = problemIndex;
                }
            } finally {
                Database.dispose(ps);
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
            query.append(" AND s.language_id IN " + Database.createNumberValues(languageIds));
        }
        query.append(" ORDER BY contest_order " + order);
        query.append(" LIMIT " + count);
        if (index == null) {
            index = defaultIndex;
        }
        String queryString = query.toString().replace("FORCE_INDEX", "USE INDEX (" + index + ")");
        return conn.prepareStatement(queryString);
    }

    public ContestStatistics getContestStatistics(List<Problem> problems) throws PersistenceException {
        Connection conn = null;
        ContestStatistics statistics = new ContestStatistics(problems);
        if (problems.size() == 0) {
            return statistics;
        }
        try {
            conn = Database.createConnection();
            List<Long> problemIds = new ArrayList<Long>();
            for (Problem problem : problems) {
                problemIds.add(new Long(((Problem) problem).getId()));
            }
            String inProblemIds = Database.createNumberValues(problemIds);
            String query =
                    "SELECT problem_id, judge_reply_id, count(*) FROM submission " + "WHERE problem_id IN " +
                        inProblemIds + " GROUP BY problem_id, judge_reply_id";
            /*
             * String query = "SELECT problem_id, judge_reply_id, count FROM problem_statistics " +
             * "WHERE problem_id IN " + inProblemIds;
             */
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    long problemId = rs.getLong(1);
                    long judgeReplyId = rs.getLong(2);
                    int value = rs.getInt(3);
                    statistics.setCount(problemId, judgeReplyId, value);
                }
                return statistics;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the statistics", e);
        } finally {
            Database.dispose(conn);
        }
    }

    public List<RankListEntry> getRankList(List<Problem> problems, long contestStartDate) throws PersistenceException {
        return this.getRankList(problems, contestStartDate, -1);
    }

    public List<RankListEntry> getRankList(List<Problem> problems, long contestStartDate, long roleId) throws PersistenceException {
        Connection conn = null;
        if (problems.size() == 0) {
            return new ArrayList<RankListEntry>();
        }
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            List<Long> problemIds = new ArrayList<Long>();
            Map<Long, Integer> problemIndexes = new HashMap<Long, Integer>();
            int index = 0;
            for (Problem problem2 : problems) {
                Problem problem = (Problem) problem2;
                problemIds.add(new Long(problem.getId()));
                problemIndexes.put(new Long(problem.getId()), new Integer(index));
                index++;
            }
            String userIdsCon = "";
            if (roleId >= 0) {
                // TODO performance issue!!
                List<Long> ids = new ArrayList<Long>();
                try {
                    ps = conn.prepareStatement("SELECT user_profile_id FROM user_role WHERE role_id=?");
                    ps.setLong(1, roleId);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        ids.add(rs.getLong(1));
                    }
                    if (ids.size() == 0) {
                        return new ArrayList<RankListEntry>();
                    }
                } finally {
                    Database.dispose(ps);
                }
                userIdsCon = " AND user_profile_id IN " + Database.createNumberValues(ids);
            }

            String inProblemIds = Database.createNumberValues(problemIds);
            Map<Long, RankListEntry> entries = new HashMap<Long, RankListEntry>();
            try {
                ps =
                        conn
                            .prepareStatement("SELECT user_profile_id, problem_id, judge_reply_id, submission_date FROM submission " +
                                "WHERE problem_id IN " + inProblemIds + userIdsCon + " ORDER BY submission_date");
                ResultSet rs = ps.executeQuery();
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
            } finally {
                Database.dispose(ps);
            }
            List<RankListEntry> entryList = new ArrayList<RankListEntry>(entries.values());
            Collections.sort(entryList);
            return entryList;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the rank list", e);
        } finally {
            Database.dispose(conn);
        }
    }

    public RankListEntry getRankListEntry(long contestId, long userId) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps =
                        conn.prepareStatement("SELECT ac_number, submission_number FROM user_stat "
                            + "WHERE contest_id=? AND user_id=?");
                ps.setLong(1, contestId);
                ps.setLong(2, userId);
                ResultSet rs = ps.executeQuery();
                RankListEntry re = null;
                if (rs.next()) {
                    re = new RankListEntry(1);
                    re.setSolved(rs.getLong(1));
                    re.setSubmitted(rs.getLong(2));
                }
                return re;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the rank list", e);
        } finally {
            Database.dispose(conn);
        }
    }

    public ProblemsetRankList getProblemsetRankList(long contestId, int offset, int count, String sort) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            String sql=null;
            if(sort.equalsIgnoreCase("submit")){
				sql =
                    "SELECT u.user_profile_id, u.handle, u.nickname, up.plan, ua.solved, ua.tiebreak " +
                        "FROM user_ac ua " + "LEFT JOIN user_profile u ON ua.user_profile_id = u.user_profile_id " +
                        "LEFT JOIN user_preference up ON ua.user_profile_id = up.user_profile_id " +
                        "WHERE contest_id=? ORDER BY ua.tiebreak DESC, ua.solved DESC " + "LIMIT " + offset + "," +
                        count;
            } else {
				sql =
                    "SELECT u.user_profile_id, u.handle, u.nickname, up.plan, ua.solved, ua.tiebreak " +
                        "FROM user_ac ua " + "LEFT JOIN user_profile u ON ua.user_profile_id = u.user_profile_id " +
                        "LEFT JOIN user_preference up ON ua.user_profile_id = up.user_profile_id " +
                        "WHERE contest_id=? ORDER BY ua.solved DESC, ua.tiebreak ASC " + "LIMIT " + offset + "," +
                        count;
            }
            List<UserProfile> users = new ArrayList<UserProfile>();
            List<Integer> solved = new ArrayList<Integer>();
            List<Integer> total = new ArrayList<Integer>();
            try {
                ps = conn.prepareStatement(sql);
                ps.setLong(1, contestId);
                ResultSet rs = ps.executeQuery();
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
            } finally {
                Database.dispose(ps);
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
            Database.dispose(conn);
        }
    }

    public UserStatistics getUserStatistics(long contestId, long userId) throws PersistenceException {
        Connection conn = null;
        try {
            UserStatistics statistics = new UserStatistics(userId, contestId);
            conn = Database.createConnection();
            PreparedStatement ps = null;
            String sql =
                    "SELECT DISTINCT p.problem_id, p.code, p.title " +
                        SubmissionPersistenceImpl.GET_SUBMISSION_FROM_PART +
                        " AND s.user_profile_id=? AND s.judge_reply_id=? AND s.contest_id=?";
            sql = sql.replace("FORCE_INDEX", "USE INDEX (index_submission_user_reply_contest)");
            List<Problem> solved = new ArrayList<Problem>();
            try {
                ps = conn.prepareStatement(sql);
                ps.setLong(1, userId);
                ps.setLong(2, JudgeReply.ACCEPTED.getId());
                ps.setLong(3, contestId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Problem p = new Problem();
                    p.setContestId(contestId);
                    p.setId(rs.getLong("problem_id"));
                    p.setCode(rs.getString("code"));
                    p.setTitle(rs.getString("title"));
                    solved.add(p);
                }
            } finally {
                Database.dispose(ps);
            }
            statistics.setSolved(new TreeSet<Problem>(solved));
            try {
                ps =
                        conn
                            .prepareStatement("SELECT judge_reply_id, count(*) FROM submission WHERE contest_id=? AND user_profile_id=? GROUP BY judge_reply_id");
                ps.setLong(1, contestId);
                ps.setLong(2, userId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    long jid = rs.getLong(1);
                    int count = rs.getInt(2);
                    statistics.setCount(jid, count);
                }
                return statistics;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the user statistics", e);
        } finally {
            Database.dispose(conn);
        }
    }

    public void changeQQStatus(long pid, long uid, String status) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps =
                        conn
                            .prepareStatement("UPDATE submission_status SET status=? WHERE problem_id=? AND user_profile_id=?");
                ps.setString(1, status);
                ps.setLong(2, pid);
                ps.setLong(3, uid);
                int changes = ps.executeUpdate();
                if (changes == 0) {
                    ps =
                            conn
                                .prepareStatement("INSERT INTO submission_status (problem_id, user_profile_id, status) VALUES (?,?,?)");
                    ps.setLong(1, pid);
                    ps.setLong(2, uid);
                    ps.setString(3, status);
                    ps.executeUpdate();
                }
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to update the QQs", e);
        } finally {
            Database.dispose(conn);
        }

    }

    public List<QQ> searchQQs(long contestId) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps =
                        conn
                            .prepareStatement("SELECT s.submission_id, s.submission_date, "
                                + "u.user_profile_id, u.handle, u.nickname, "
                                + "p.problem_id, p.code, p.color, ss.status "
                                + "FROM submission s "
                                + "LEFT JOIN user_profile u ON s.user_profile_id=u.user_profile_id "
                                + "LEFT JOIN problem p ON s.problem_id=p.problem_id "
                                + "LEFT JOIN submission_status ss ON u.user_profile_id=ss.user_profile_id AND p.problem_id=ss.problem_id "
                                + "WHERE p.contest_id=? AND s.judge_reply_id=? AND p.active=1 AND (ss.status IS NULL OR ss.status<>?) "
                                + "ORDER BY s.submission_date");
                ps.setLong(1, contestId);
                ps.setLong(2, JudgeReply.ACCEPTED.getId());
                ps.setString(3, QQ.QQ_FINISHED);
                ResultSet rs = ps.executeQuery();
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
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the QQs", e);
        } finally {
            Database.dispose(conn);
        }
    }

    public ProblemStatistics getProblemStatistics(long problemId, String orderBy, int count) throws PersistenceException {
        Connection conn = null;
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

        Map<Long, Language> languageMap = PersistenceManager.getInstance().getLanguagePersistence().getLanguageMap();
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps =
                        conn
                            .prepareStatement("SELECT judge_reply_id, count(*) FROM submission WHERE problem_id=? GROUP BY judge_reply_id");

                ps.setLong(1, problemId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    long jid = rs.getLong(1);
                    int c = rs.getInt(2);
                    ret.setCount(jid, c);
                }
            } finally {
                Database.dispose(ps);
            }
            String sql =
                    SubmissionPersistenceImpl.GET_SUBMISSIONS + " AND s.problem_id=? AND s.judge_reply_id=? ORDER BY " +
                        ob + " LIMIT " + count;
            sql = sql.replace("FORCE_INDEX", "USE INDEX (index_submission_problem_reply)");
            try {
                ps = conn.prepareStatement(sql);
                ps.setLong(1, problemId);
                ps.setLong(2, JudgeReply.ACCEPTED.getId());
                ResultSet rs = ps.executeQuery();
                List<Submission> submissions = new ArrayList<Submission>();
                while (rs.next()) {
                    Submission submission = this.populateSubmission(rs, false, languageMap);
                    submissions.add(submission);
                }
                ret.setBestRuns(submissions);
                return ret;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the QQs", e);
        } finally {
            Database.dispose(conn);
        }
    }

    public List<Submission> getQueueingSubmissions(long maxSubmissionId, int count) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            if (maxSubmissionId < 0) {
                ps = conn.prepareStatement("SELECT MAX(submission_id) FROM submission;");
                try {
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    maxSubmissionId = rs.getLong(1);
                } finally {
                    Database.dispose(ps);
                }
            }
            StringBuilder query =
                    new StringBuilder(SubmissionPersistenceImpl.GET_SUBMISSIONS_WITH_CONTENT.replace("FORCE_INDEX", ""));
            query.append(" AND s.judge_reply_id=");
            query.append(JudgeReply.QUEUING.getId());
            query.append(" AND s.submission_id<=");
            query.append(maxSubmissionId);
            query.append(" ORDER BY s.submission_id DESC LIMIT ");
            query.append(count);
            System.out.println(query.toString());
            ps = conn.prepareStatement(query.toString());
            try {
                ResultSet rs = ps.executeQuery();
                List<Submission> submissions = new ArrayList<Submission>();
                Map<Long, Language> languageMap =
                        PersistenceManager.getInstance().getLanguagePersistence().getLanguageMap();
                while (rs.next()) {
                    Submission submission = this.populateSubmission(rs, true, languageMap);
                    submissions.add(submission);
                }
                return submissions;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get queueing submissions", e);
        } finally {
            Database.dispose(conn);
        }
    }
}
