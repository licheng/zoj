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
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.util.cache.Cache;

public class ContestManager {

    private final Cache<List<Problem>> contestProblemsCache;

    private final Cache<AbstractContest> contestCache;

    private final Cache<Problem> problemCache;

    private final Cache<Submission> submissionCache;

    private final Cache<byte[]> descriptionCache;

    private final Cache<List<AbstractContest>> contestsCache;

    private final Cache<Integer> problemCountCache;

    /**
     * ContestManager.
     */
    private static ContestManager instance = null;

    /**
     * <p>
     * Constructor of ContestManager class.
     * </p>
     * 
     * @throws PersistenceCreationException
     * 
     */
    private ContestManager() throws PersistenceCreationException {
        this.contestProblemsCache = new Cache<List<Problem>>(60000, 50);
        this.contestCache = new Cache<AbstractContest>(60000, 30);
        this.problemCache = new Cache<Problem>(60000, 50);
        this.submissionCache = new Cache<Submission>(10000, 50);
        this.descriptionCache = new Cache<byte[]>(60000, 50);
        this.contestsCache = new Cache<List<AbstractContest>>(60000, 20);
        this.problemCountCache = new Cache<Integer>(60000, 20);
    }

    /**
     * Gets the singleton instance.
     * 
     * @return the singleton instance.
     * @throws PersistenceCreationException
     */
    public static ContestManager getInstance() throws PersistenceCreationException {
        if (ContestManager.instance == null) {
            synchronized (ContestManager.class) {
                if (ContestManager.instance == null) {
                    ContestManager.instance = new ContestManager();
                }
            }
        }
        return ContestManager.instance;
    }

    public List<AbstractContest> getAllContests() throws PersistenceException {
        return this.getContests(0);
    }

    public List<AbstractContest> getAllProblemsets() throws PersistenceException {
        return this.getContests(1);
    }

    public List<AbstractContest> getAllCourses() throws PersistenceException {
        return this.getContests(2);
    }

    public List<AbstractContest> getContests(int contestType) throws PersistenceException {
        Object key = new Integer(contestType);
        synchronized (this.contestsCache) {
            List<AbstractContest> contests = this.contestsCache.get(key);
            if (contests == null) {
                ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
                if (contestType==1) {
                    contests = contestPersistence.getAllProblemsets();
                } else if (contestType==0) {
                    contests = contestPersistence.getAllContests();
                } else {
                    contests = contestPersistence.getAllCourses();
                }
                this.contestsCache.put(key, contests);
            }
            return contests;
        }
    }

    public int getProblemsCount(long contestId) throws PersistenceException {
        Object key = new Long(contestId);
        synchronized (this.problemCountCache) {
            Integer count = this.problemCountCache.get(key);
            if (count == null) {
                ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();
                int ret = problemPersistence.getProblemsCount(contestId);
                this.problemCountCache.put(key, ret);
                return ret;
            }
            return count.intValue();
        }
    }

    public AbstractContest getContest(long contestId) throws PersistenceException {
        Object key = new Long(contestId);
        synchronized (this.contestCache) {
            AbstractContest contest = this.contestCache.get(key);
            if (contest == null) {
                ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
                contest = contestPersistence.getContest(contestId);
                this.contestCache.put(key, contest);
            }
            return contest;
        }
    }

    public List<Problem> getContestProblems(long contestId, int offset, int count) throws PersistenceException {
        ProblemCriteria criteria = new ProblemCriteria();
        criteria.setContestId(new Long(contestId));
        return this.searchProblems(criteria, offset, count);
    }

    public List<Problem> getContestProblems(long contestId) throws PersistenceException {
        ProblemCriteria criteria = new ProblemCriteria();
        criteria.setContestId(new Long(contestId));
        return this.searchProblems(criteria, 0, Integer.MAX_VALUE);
    }

    public List<Problem> searchProblems(ProblemCriteria criteria, int offset, int count) throws PersistenceException {
        List<Object> key = new ArrayList<Object>();
        key.add(criteria);
        key.add(new Integer(offset));
        key.add(new Integer(count));
        synchronized (this.contestProblemsCache) {
            List<Problem> problems = this.contestProblemsCache.get(key);
            if (problems == null) {
                ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();
                problems = problemPersistence.searchProblems(criteria, offset, count);
                this.contestProblemsCache.put(key, problems);
            }
            return problems;
        }
    }

    public Problem getProblem(long problemId) throws PersistenceException {
        Object key = new Long(problemId);
        synchronized (this.problemCache) {
            Problem problem = this.problemCache.get(key);
            if (problem == null) {
                ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();
                problem = problemPersistence.getProblem(problemId);
                this.problemCache.put(key, problem);
            }
            return problem;
        }
    }

    public Submission getSubmission(long submissionId) throws PersistenceException {
        Object key = new Long(submissionId);
        synchronized (this.submissionCache) {
            Submission submission = this.submissionCache.get(key);
            if (submission == null) {
                SubmissionPersistence submissionPersistence =
                        PersistenceManager.getInstance().getSubmissionPersistence();
                submission = submissionPersistence.getSubmission(submissionId);
                this.submissionCache.put(key, submission);
            }
            return submission;
        }
    }

    public byte[] getDescription(long problemId) throws PersistenceException {
        Object key = new Long(problemId);
        synchronized (this.descriptionCache) {
            byte[] text = this.descriptionCache.get(key);
            if (text == null) {
                ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();

                List<Reference> ref = referencePersistence.getProblemReferences(problemId, ReferenceType.DESCRIPTION);
                if (ref.size() > 0) {
                    text = ref.get(0).getContent();
                } else {
                    text = new byte[0];
                }

                this.descriptionCache.put(key, text);
            }
            return text;
        }
    }

    public void refreshContest(long contestId) {

        synchronized (this.contestProblemsCache) {
            for (Object key : this.contestProblemsCache.getKeys()) {
                ProblemCriteria criteria = ((List<ProblemCriteria>) key).iterator().next();
                if (criteria.getContestId() == contestId) {
                    this.contestProblemsCache.remove(key);
                }
            }
        }

        this.contestCache.remove(contestId);
        this.problemCountCache.remove(contestId);
        this.contestsCache.remove(true);
        this.contestsCache.remove(false);
    }

    public void refreshProblem(Problem problem) {
        this.refreshContest(problem.getContestId());
        synchronized (this.problemCache) {
            this.problemCache.remove(new Long(problem.getId()));
        }
        synchronized (this.descriptionCache) {
            this.descriptionCache.remove(new Long(problem.getId()));
        }

    }

}
