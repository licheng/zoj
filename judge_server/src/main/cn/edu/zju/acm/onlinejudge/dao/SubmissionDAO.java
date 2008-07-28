package cn.edu.zju.acm.onlinejudge.dao;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public interface SubmissionDAO extends GenericDAO {

    Submission getSubmission(long id) throws PersistenceException;

    void save(Submission submission, long contestId) throws PersistenceException;

    void update(Submission submission, long contestId) throws PersistenceException;

}