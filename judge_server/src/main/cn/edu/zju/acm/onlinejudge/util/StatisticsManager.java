package cn.edu.zju.acm.onlinejudge.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.relation.Role;

import cn.edu.zju.acm.onlinejudge.judgeservice.JudgeService;
import cn.edu.zju.acm.onlinejudge.judgeservice.JudgingList;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.util.cache.Cache;
import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;

public class StatisticsManager {

    private final Cache contestStatisticsCache;
    
    private final Cache problemStatisticsCache;

    private final Cache ranklistCache;
    
    private final Cache problemsetRanklistCache;
    
    

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
		problemsetRanklistCache = new Cache(30000, 20);
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
    List<Object> key = new ArrayList<Object>();
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
    
    public ProblemStatistics getProblemStatistics(long problemId, String orderBy, int count) throws PersistenceCreationException, PersistenceException {
        List<Object> key = new ArrayList<Object>();
        key.add(problemId);
        key.add(orderBy);
        key.add(count);
        	
    	synchronized (problemStatisticsCache) {
    		ProblemStatistics statistics = (ProblemStatistics) contestStatisticsCache.get(key);
    	    if (statistics == null) {
    		statistics = PersistenceManager.getInstance().getSubmissionPersistence().getProblemStatistics(problemId, orderBy, count);
    		contestStatisticsCache.put(key, statistics);
    	    }
    	    return statistics;
    	}
        }

    public RankList getRankList(long contestId) throws PersistenceException {
        return getRankList(contestId, -1);

    }
    
    public ProblemsetRankList getProblemsetRankList(long contestId, int offset, int count) throws PersistenceException {
        List<Object> key = new ArrayList<Object>();
        key.add(contestId);
        key.add(offset);
        key.add(count);
        
        synchronized (problemsetRanklistCache) {
        	ProblemsetRankList ranklist = (ProblemsetRankList) problemsetRanklistCache.get(key);
    	    if (ranklist == null) {
    	    	ranklist = PersistenceManager.getInstance().getSubmissionPersistence().getProblemsetRankList(
                    		contestId, offset, count);
                problemsetRanklistCache.put(key, ranklist);
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
    

    public UserStatistics getUserStatistics(long contestId, long userId) throws PersistenceException {
		ArrayList<Object> key = new ArrayList<Object>();
		key.add(new Long(contestId));
		key.add(new Long(userId));
		synchronized (solvedCache) {
			UserStatistics ret = (UserStatistics) solvedCache.get(key);
		    if (ret == null) {
				ret = PersistenceManager.getInstance().getSubmissionPersistence().getUserStatistics(contestId, userId);
				solvedCache.put(key, ret);
		    }
		    return ret;
		}
    }

    public List getSubmissions(SubmissionCriteria criteria, long firstId, long lastId, int count) throws PersistenceException {
        List<Object> key = new ArrayList<Object>();
        key.add(criteria);
		key.add(new Long(firstId));
		key.add(new Long(lastId));
		key.add(new Integer(count));
        JudgingList judgingList;
        List submissions;
        synchronized (submissionCache) {
            Object[] pair = (Object[]) submissionCache.get(key);
            if (pair != null) {
                judgingList = (JudgingList) pair[0];
                submissions = (List) pair[1];
            } else {
                judgingList = JudgeService.getInstance().getJudgingList();
                submissions = PersistenceManager.getInstance().getSubmissionPersistence().searchSubmissions(criteria,
                		firstId, lastId, count);
                pair = new Object[] {judgingList, submissions};
                submissionCache.put(key, pair);
            }
        }
        Map<Long, Submission> submissionMap = judgingList.getSubmissionMap();
        for (int i = 0; i < submissions.size(); i++) {
            Submission submission = (Submission) submissions.get(i);
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
		synchronized (contestStatisticsCache) {
		    contestStatisticsCache.remove(key);
		}
		synchronized (ranklistCache) {
		    ranklistCache.remove(key);
		}
    }

}
