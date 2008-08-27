package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.dao.ProblemDAO;

public class MockProblemDAO extends MockDAO implements ProblemDAO {
    private AtomicLong id = new AtomicLong();

    private Map<Long, Problem> problemMap = new Hashtable<Long, Problem>();

    public Problem getProblem(long problemId) {
        return cloneProblem(problemMap.get(problemId));
    }

    public void save(Problem problem) {
        problem.setId(id.getAndIncrement());
        update(problem);
    }

    public void update(Problem problem) {
        problemMap.put(problem.getId(), cloneProblem(problem));
    }

    private Problem cloneProblem(Problem problem) {
        Problem ret = new Problem();
        ret.setId(problem.getId());
        ret.setRevision(problem.getRevision());
        ret.setLimit(cloneLimit(problem.getLimit()));
        ret.setChecker(problem.isChecker());
        return ret;
    }

    private Limit cloneLimit(Limit limit) {
        Limit ret = new Limit();
        ret.setId(limit.getId());
        ret.setMemoryLimit(limit.getMemoryLimit());
        ret.setOutputLimit(limit.getOutputLimit());
        ret.setTimeLimit(limit.getTimeLimit());
        return ret;
    }
}