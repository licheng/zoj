package cn.edu.zju.acm.onlinejudge.dao;

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

class ProblemDAOImpl extends AbstractDAO implements ProblemDAO {
    public Problem getProblem(long problemId) throws PersistenceException {
	if (problemId == 0) {
	    Problem problem = new Problem();
	    problem.setId(0);
	    problem.setRevision(0);
	    Limit limit = new Limit();
	    limit.setTimeLimit(1);
	    limit.setMemoryLimit(32 * 1024);
	    limit.setOutputLimit(1);
	    problem.setLimit(limit);
	    return problem;
	}
	return PersistenceManager.getInstance().getProblemPersistence().getProblem(problemId);
    }

    public void update(Problem problem) throws PersistenceException {
	PersistenceManager.getInstance().getProblemPersistence().updateProblem(problem, 1);
    }
}
