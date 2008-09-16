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

package cn.edu.zju.acm.onlinejudge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.judgeservice.JudgeService;
import cn.edu.zju.acm.onlinejudge.judgeservice.JudgingQueueIterator;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.util.cache.Cache;

public class StatisticsManager {

    private final Cache<ContestStatistics> contestStatisticsCache;

    private final Cache<ProblemStatistics> problemStatisticsCache;

    private final Cache<RankList> ranklistCache;

    private final Cache<ProblemsetRankList> problemsetRanklistCache;

    private final Cache<UserStatistics> solvedCache;

    private final Cache<Object[]> submissionCache;

    /**
     * StatisticsManager.
     */
    private static StatisticsManager instance = null;

    /**
     * <p>
     * Constructor of StatisticsManager class.
     * </p>
     * 
     * @throws PersistenceCreationException
     * 
     */
    private StatisticsManager() throws PersistenceCreationException {
        // TODO
        this.contestStatisticsCache = new Cache<ContestStatistics>(20000, 20);
        this.problemStatisticsCache = new Cache<ProblemStatistics>(20000, 50);
        this.ranklistCache = new Cache<RankList>(10000, 20);
        this.problemsetRanklistCache = new Cache<ProblemsetRankList>(30000, 20);
        this.solvedCache = new Cache<UserStatistics>(10000, 50);
        this.submissionCache = new Cache<Object[]>(10000, 50);
    }

    /**
     * Gets the singleton instance.
     * 
     * @return the singleton instance.
     * @throws PersistenceCreationException
     */
    public static StatisticsManager getInstance() throws PersistenceCreationException {
        if (StatisticsManager.instance == null) {
            synchronized (StatisticsManager.class) {
                if (StatisticsManager.instance == null) {
                    StatisticsManager.instance = new StatisticsManager();
                }
            }
        }
        return StatisticsManager.instance;
    }

    public ContestStatistics getContestStatistics(long contestId) throws PersistenceException {
        return this.getContestStatistics(contestId, 0, Integer.MAX_VALUE);
    }

    public ContestStatistics getContestStatistics(long contestId, int offset, int count) throws PersistenceException {
        List<Object> key = new ArrayList<Object>();
        key.add(contestId);
        key.add(offset);
        key.add(count);

        synchronized (this.contestStatisticsCache) {
            ContestStatistics statistics = this.contestStatisticsCache.get(key);
            if (statistics == null) {
                List<Problem> problems = ContestManager.getInstance().getContestProblems(contestId, offset, count);
                statistics = PersistenceManager.getInstance().getSubmissionPersistence().getContestStatistics(problems);
                this.contestStatisticsCache.put(key, statistics);
            }
            return statistics;
        }
    }

    public ProblemStatistics getProblemStatistics(long problemId, String orderBy, int count) throws PersistenceCreationException,
            PersistenceException {
        List<Object> key = new ArrayList<Object>();
        key.add(problemId);
        key.add(orderBy);
        key.add(count);

        synchronized (this.problemStatisticsCache) {
            ProblemStatistics statistics = this.problemStatisticsCache.get(key);
            if (statistics == null) {
                statistics =
                        PersistenceManager.getInstance().getSubmissionPersistence()
                                          .getProblemStatistics(problemId, orderBy, count);
                this.problemStatisticsCache.put(key, statistics);
            }
            return statistics;
        }
    }

    public RankList getRankList(long contestId) throws PersistenceException {
        return this.getRankList(contestId, -1);

    }

    public ProblemsetRankList getProblemsetRankList(long contestId, int offset, int count) throws PersistenceException {
        List<Object> key = new ArrayList<Object>();
        key.add(contestId);
        key.add(offset);
        key.add(count);

        synchronized (this.problemsetRanklistCache) {
            ProblemsetRankList ranklist = this.problemsetRanklistCache.get(key);
            if (ranklist == null) {
                ranklist =
                        PersistenceManager.getInstance().getSubmissionPersistence()
                                          .getProblemsetRankList(contestId, offset, count);
                this.problemsetRanklistCache.put(key, ranklist);
            }
            return ranklist;
        }

    }

    public RankList getRankList(long contestId, long roleId) throws PersistenceException {
        List<Long> key = new ArrayList<Long>();
        key.add(contestId);
        key.add(roleId);

        synchronized (this.ranklistCache) {
            RankList ranklist = this.ranklistCache.get(key);
            if (ranklist == null) {
                ranklist = new RankList();

                List<RoleSecurity> roles =
                        PersistenceManager.getInstance().getAuthorizationPersistence().getContestRoles(contestId);
                ranklist.setRoles(roles);
                for (RoleSecurity role : roles) {
                    if (role.getId() == roleId) {
                        ranklist.setRole(role);
                        break;
                    }
                }
                if (roleId < 0 || ranklist.getRole() != null) {
                    AbstractContest contest = ContestManager.getInstance().getContest(contestId);
                    List<Problem> problems = ContestManager.getInstance().getContestProblems(contestId);

                    List<RankListEntry> entries =
                            PersistenceManager.getInstance().getSubmissionPersistence()
                                              .getRankList(problems, contest.getStartTime().getTime(), roleId);

                    for (RankListEntry entry : entries) {
                        entry.setUserProfile(UserManager.getInstance().getUserProfile(entry.getUserProfile().getId()));
                    }
                    ranklist.setEntries(entries);

                }
                this.ranklistCache.put(key, ranklist);
            }
            return ranklist;
        }

    }

    public UserStatistics getUserStatistics(long contestId, long userId) throws PersistenceException {
        ArrayList<Object> key = new ArrayList<Object>();
        key.add(new Long(contestId));
        key.add(new Long(userId));
        synchronized (this.solvedCache) {
            UserStatistics ret = this.solvedCache.get(key);
            if (ret == null) {
                ret = PersistenceManager.getInstance().getSubmissionPersistence().getUserStatistics(contestId, userId);
                this.solvedCache.put(key, ret);
            }
            return ret;
        }
    }

    public List<Submission> getSubmissions(SubmissionCriteria criteria, long firstId, long lastId, int count) throws PersistenceException {
        List<Object> key = new ArrayList<Object>();
        key.add(criteria);
        key.add(new Long(firstId));
        key.add(new Long(lastId));
        key.add(new Integer(count));
        JudgingQueueIterator iter;
        List<Submission> submissions;
        synchronized (this.submissionCache) {
            Object[] pair = this.submissionCache.get(key);
            if (pair != null) {
                iter = (JudgingQueueIterator) pair[0];
                submissions = (List<Submission>) pair[1];
            } else {
                iter = JudgeService.getInstance().getJudgingView();
                submissions =
                        PersistenceManager.getInstance().getSubmissionPersistence().searchSubmissions(criteria,
                                                                                                      firstId, lastId,
                                                                                                      count);
                pair = new Object[] {iter, submissions};
                this.submissionCache.put(key, pair);
            }
        }
        Map<Long, Submission> submissionMap = new HashMap<Long, Submission>();
        for (;;) {
            Submission submission = iter.next();
            if (submission == null) {
                break;
            }
            submissionMap.put(submission.getId(), submission);
        }
        for (int i = 0; i < submissions.size(); i++) {
            Submission submission = submissions.get(i);
            Submission t = submissionMap.get(submission.getId());
            if (t != null) {
                submission.setTimeConsumption(t.getTimeConsumption());
                submission.setMemoryConsumption(t.getMemoryConsumption());
                submission.setJudgeComment(t.getJudgeComment());
                submission.setJudgeReply(t.getJudgeReply());
            }
        }
        return submissions;
    }

    public void refresh(long contestId) {
        Object key = new Long(contestId);
        synchronized (this.contestStatisticsCache) {
            this.contestStatisticsCache.remove(key);
        }
        synchronized (this.ranklistCache) {
            this.ranklistCache.remove(key);
        }
    }

}
