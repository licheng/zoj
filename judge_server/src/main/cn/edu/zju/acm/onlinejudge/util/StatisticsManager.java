package cn.edu.zju.acm.onlinejudge.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.relation.Role;

import cn.edu.zju.acm.onlinejudge.judgeservice.JudgeService;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.util.cache.Cache;
import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;

public class StatisticsManager {

    private static final String TOTAL_NUMBER_KEY = "total_number";

    private final Cache contestStatisticsCache;
    
    private final Cache problemStatisticsCache;

    private final Cache ranklistCache;

    private final Cache solvedCache;

    private final Cache submissionCache;

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
	contestStatisticsCache = new Cache(10000, 20);
	problemStatisticsCache = new Cache(10000, 20);
	ranklistCache = new Cache(10000, 20);
	solvedCache = new Cache(10000, 50);
	submissionCache = new Cache(10000, 50);
    }

    /**
     * Gets the singleton instance.
     * 
     * @return the singleton instance.
     * @throws PersistenceCreationException
     */
    public static StatisticsManager getInstance() throws PersistenceCreationException {
	if (instance == null) {
	    synchronized (StatisticsManager.class) {
		if (instance == null) {
		    instance = new StatisticsManager();
		}
	    }
	}
	return instance;
    }

    public ContestStatistics getContestStatistics(long contestId) throws PersistenceException {
        return getContestStatistics(contestId, 0, Integer.MAX_VALUE);
    }
    
    public ContestStatistics getContestStatistics(long contestId, int offset, int count) throws PersistenceException {
    List key = new ArrayList();
    key.add(contestId);
    key.add(offset);
    key.add(count);
    	
	synchronized (contestStatisticsCache) {
	    ContestStatistics statistics = (ContestStatistics) contestStatisticsCache.get(key);
	    if (statistics == null) {
		List problmes = ContestManager.getInstance().getContestProblems(contestId, offset, count);
		statistics = PersistenceManager.getInstance().getSubmissionPersistence().getContestStatistics(problmes);
		contestStatisticsCache.put(key, statistics);
	    }
	    return statistics;
	}
    }
    
    public ProblemStatistics getProblemStatistics(long problemId) throws PersistenceCreationException, PersistenceException {
        List key = new ArrayList();
        key.add(problemId);
        	
    	synchronized (problemStatisticsCache) {
    		ProblemStatistics statistics = (ProblemStatistics) contestStatisticsCache.get(key);
    	    if (statistics == null) {
    		statistics = PersistenceManager.getInstance().getSubmissionPersistence().getProblemStatistics(problemId);
    		contestStatisticsCache.put(key, statistics);
    	    }
    	    return statistics;
    	}
        }

    public RankList getRankList(long contestId) throws PersistenceException {
        return getRankList(contestId, -1);

    }
    
    public RankList getProblemsetRankList(long contestId, long roleId,long begin,int order) throws PersistenceException {
        List key = new ArrayList();
        key.add(contestId);
        key.add(roleId);
        key.add(begin);
        key.add(order);
        
        synchronized (ranklistCache) {
    	    RankList ranklist = (RankList) ranklistCache.get(key);
    	    if (ranklist == null) {
            ranklist = new RankList();
    		        
            List roles = PersistenceManager.getInstance().getAuthorizationPersistence().getContestRoles(contestId);            
            ranklist.setRoles(roles);
            for (Object obj : roles) {
                RoleSecurity role = (RoleSecurity) obj;
                if (role.getId() == roleId) {
                    ranklist.setRole(role);
                                                
                    break;                    
                }
            }       
            if (roleId < 0 || ranklist.getRole() != null) {
                List entries =
                    PersistenceManager.getInstance().getSubmissionPersistence().getProblemsetRankList(
                    		contestId,begin,order, roleId);
        
                for (Iterator it = entries.iterator(); it.hasNext();) {
                    RankListEntry entry = (RankListEntry) it.next();
                    entry.setUserProfile(UserManager.getInstance().getUserProfile(entry.getUserProfile().getId()));
                }
                ranklist.setEntries(entries);
        
            }
    		ranklistCache.put(key, ranklist);
    	    }
    	    return ranklist;
    	}

    }
    
    public RankList getRankList(long contestId, long roleId) throws PersistenceException {
        List key = new ArrayList();
        key.add(contestId);
        key.add(roleId);
        
        synchronized (ranklistCache) {
    	    RankList ranklist = (RankList) ranklistCache.get(key);
    	    if (ranklist == null) {
            ranklist = new RankList();
    		        
            List roles = PersistenceManager.getInstance().getAuthorizationPersistence().getContestRoles(contestId);            
            ranklist.setRoles(roles);
            for (Object obj : roles) {
                RoleSecurity role = (RoleSecurity) obj;
                if (role.getId() == roleId) {
                    ranklist.setRole(role);
                                                
                    break;                    
                }
            }       
            if (roleId < 0 || ranklist.getRole() != null) {
                AbstractContest contest = ContestManager.getInstance().getContest(contestId);
                List problmes = ContestManager.getInstance().getContestProblems(contestId);
                
                List entries =
                    PersistenceManager.getInstance().getSubmissionPersistence().getRankList(problmes,
                        contest.getStartTime().getTime(), roleId);
        
                for (Iterator it = entries.iterator(); it.hasNext();) {
                    RankListEntry entry = (RankListEntry) it.next();
                    entry.setUserProfile(UserManager.getInstance().getUserProfile(entry.getUserProfile().getId()));
                }
                ranklist.setEntries(entries);
        
            }
    		ranklistCache.put(key, ranklist);
    	    }
    	    return ranklist;
    	}

    }

    public Set getSolvedProblems(long contestId, long userId) throws PersistenceException {
	ArrayList key = new ArrayList();
	key.add(new Long(contestId));
	key.add(new Long(userId));
	synchronized (solvedCache) {
	    Set solved = (Set) solvedCache.get(key);
	    if (solved == null) {
		List problmes = ContestManager.getInstance().getContestProblems(contestId);
		solved =
			PersistenceManager.getInstance().getSubmissionPersistence().getSolvedProblems(problmes, userId);
		solvedCache.put(key, solved);
	    }
	    return solved;
	}

    }

    public List getSubmissions(SubmissionCriteria criteria, int offset, int count) throws PersistenceException {
	ArrayList key = new ArrayList();
	key.add(criteria);
	key.add(new Integer(offset));
	key.add(new Integer(count));
	synchronized (submissionCache) {
	    List submissions = (List) submissionCache.get(key);
	    if (submissions == null) {
		submissions =
			PersistenceManager.getInstance().getSubmissionPersistence().searchSubmissions(criteria,
				offset,
				count);

		submissionCache.put(key, submissions);
	    }
	    JudgeService judgeService = JudgeService.getInstance();
	    for (int i = 0; i < submissions.size(); i++) {
		Submission submission = (Submission) submissions.get(i);
		Submission t = judgeService.getSubmission(submission.getId());
		if (t != null) {
		    submission.setTimeConsumption(t.getTimeConsumption());
		    submission.setMemoryConsumption(t.getMemoryConsumption());
		    submission.setJudgeComment(t.getJudgeComment());
		    submission.setJudgeReply(t.getJudgeReply());
		}
	    }
	    return submissions;
	}

    }

    public long getSubmissionsNumber(SubmissionCriteria criteria) throws PersistenceException {
	Object key = criteria;
	synchronized (submissionCache) {
	    Long number = (Long) submissionCache.get(key);
	    if (number == null) {
		number =
			new Long(PersistenceManager.getInstance().getSubmissionPersistence()
				.searchSubmissionNumber(criteria));
		submissionCache.put(key, number);
	    }
	    return number.longValue();
	}

    }

    public void refresh(long contestId) {
	Object key = new Long(contestId);
	synchronized (contestStatisticsCache) {
	    contestStatisticsCache.remove(key);
	}
	synchronized (ranklistCache) {
	    ranklistCache.remove(key);
	}
    }

}
