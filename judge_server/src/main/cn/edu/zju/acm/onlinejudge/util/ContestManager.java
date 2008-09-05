package cn.edu.zju.acm.onlinejudge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.judgeservice.JudgeService;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.util.cache.Cache;
import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;

public class ContestManager {

	private final Cache contestProblemsCache;
	
	private final Cache contestCache;
	
	private final Cache problemCache;
	
	private final Cache submissionCache;
		
	private final Cache descriptionCache;
	
	private final Cache contestsCache;
    
    private final Cache problemCountCache;
	
	/**
	 * ContestManager.
	 */
	private static ContestManager instance = null; 

	
    /**
     * <p>Constructor of ContestManager class.</p>
     * @throws PersistenceCreationException 
     *
     */
    private ContestManager() throws PersistenceCreationException {
    	contestProblemsCache = new Cache(60000, 50);  
    	contestCache = new Cache(60000, 30); 
    	problemCache = new Cache(60000, 50); 
    	submissionCache = new Cache(10000, 50);
    	descriptionCache = new Cache(60000, 50);
    	contestsCache = new Cache(60000, 20);    
        problemCountCache = new Cache(60000, 20);
    }
    
    /**
     * Gets the singleton instance.
     * @return the singleton instance.
     * @throws PersistenceCreationException 
     */
    public static ContestManager getInstance() throws PersistenceCreationException {
    	if (instance == null) {
    		synchronized (ContestManager.class) {
    			if (instance == null) {
    				instance = new ContestManager();
    			}
    		}
    	}
    	return instance;
    }
    
    public List<AbstractContest> getAllContests() throws PersistenceException {
    	return getContests(false);
    }
    
    public List<AbstractContest> getAllProblemsets() throws PersistenceException {
    	return getContests(true);
    }
    
    public List<AbstractContest> getContests(boolean isProblemset) throws PersistenceException {
    	Object key = new Boolean(isProblemset);
    	synchronized (contestsCache) {
    		List<AbstractContest> contests = (List<AbstractContest>) contestsCache.get(key);
    		if (contests == null) {
    			ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
    			if (isProblemset) {
                    contests = contestPersistence.getAllProblemsets();
                } else {
                    contests = contestPersistence.getAllContests();
                }
    			contestsCache.put(key, contests);
    		}
    		return contests;
    	}
    }
    
    public int getProblemsCount(long contestId) throws PersistenceException {
        Object key = new Long(contestId);
        synchronized (problemCountCache) {
            Integer count = (Integer) problemCountCache.get(key);
            if (count == null) {
                ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();
                int ret = problemPersistence.getProblemsCount(contestId);
                problemCountCache.put(key, ret);
                return ret;
            }
            return count.intValue();
        }
    }
    
    
    public AbstractContest getContest(long contestId) throws PersistenceException {
    	Object key = new Long(contestId);
    	synchronized (contestCache) {
    		AbstractContest contest = (AbstractContest) contestCache.get(key);
    		if (contest == null) {
    			ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
    			contest = contestPersistence.getContest(contestId);
    			contestCache.put(key, contest);
    		}
    		return contest;
    	}
    }
    
    public List<Problem> getContestProblems(long contestId, int offset, int count) throws PersistenceException {
        ProblemCriteria criteria = new ProblemCriteria();
        criteria.setContestId(new Long(contestId));    
        return searchProblems(criteria, offset, count);                
    }
    
    public List<Problem> getContestProblems(long contestId) throws PersistenceException {
    	ProblemCriteria criteria = new ProblemCriteria();
        criteria.setContestId(new Long(contestId));    
        return searchProblems(criteria, 0, Integer.MAX_VALUE);                
    }
    
    public List<Problem> searchProblems(ProblemCriteria criteria, int offset, int count) throws PersistenceException {
    	List<Object> key = new ArrayList<Object>();
    	key.add(criteria);
    	key.add(new Integer(offset));
    	key.add(new Integer(count));
    	synchronized (contestProblemsCache) {
	    	List<Problem> problems = (List<Problem>) contestProblemsCache.get(key);
	    	if (problems == null) {    		
	    		ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();
	    		problems = problemPersistence.searchProblems(criteria, offset, count);
	    		contestProblemsCache.put(key, problems);
	    	}
	    	return problems;
    	}
    }
    
    public Problem getProblem(long problemId) throws PersistenceException {
    	Object key = new Long(problemId);
    	synchronized (problemCache) {
    		Problem problem = (Problem) problemCache.get(key);
    		if (problem == null) {
    			ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();
    			problem = problemPersistence.getProblem(problemId);
    			problemCache.put(key, problem);
    		}
    		return problem;
    	}
    }
    
    public Submission getSubmission(long submissionId) throws PersistenceException {
    	Object key = new Long(submissionId);
    	synchronized (submissionCache) {
    		Submission submission = (Submission) submissionCache.get(key);
    		if (submission == null) {
    			SubmissionPersistence submissionPersistence 
    				= PersistenceManager.getInstance().getSubmissionPersistence();
    			submission = submissionPersistence.getSubmission(submissionId);
    			submissionCache.put(key, submission);
    		}
    		return submission;
    	}
    }
    
    public byte[] getDescription(long problemId) throws PersistenceException {
    	Object key = new Long(problemId);
    	synchronized (descriptionCache) {
    		byte[] text = (byte[]) descriptionCache.get(key);
    		if (text == null) {
    			ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
    			
    	        List ref = referencePersistence.getProblemReferences(problemId, ReferenceType.DESCRIPTION);
    	        if (ref.size() > 0) {
    	        	text = ((Reference) ref.get(0)).getContent();
    	        } else {
    	        	text = new byte[0];
    	        }
    	            			
    			descriptionCache.put(key, text);
    		}
    		return text;
    	}
    }
    
    
    public void refreshContest(long contestId) {
        
    	synchronized (contestProblemsCache) {
            for (Object key : contestProblemsCache.getKeys()) {
                ProblemCriteria criteria = (ProblemCriteria) ((List) key).iterator().next();
                if (criteria.getContestId() == contestId) {
                    contestProblemsCache.remove(key);
                }
            }
    	}   
        
        contestCache.remove(contestId);
        problemCountCache.remove(contestId);
        contestsCache.remove(true);                
        contestsCache.remove(false);
    }
    
    public void refreshProblem(Problem problem) {
        refreshContest(problem.getContestId());
        synchronized (problemCache) {
            problemCache.remove(new Long(problem.getId()));
        } 
        synchronized (descriptionCache) {
            descriptionCache.remove(new Long(problem.getId()));
        } 
        
    }
    
    
    
}
