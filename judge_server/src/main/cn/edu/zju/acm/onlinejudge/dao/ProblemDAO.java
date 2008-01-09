package cn.edu.zju.acm.onlinejudge.dao;

import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public interface ProblemDAO extends GenericDAO {

    Problem getProblem(long problemId) throws PersistenceException;

    void update(Problem problem) throws PersistenceException;

}