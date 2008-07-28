package cn.edu.zju.acm.onlinejudge.dao;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

class SubmissionDAOImpl extends AbstractDAO implements SubmissionDAO {

    public Submission getSubmission(long id) throws PersistenceException {
	return PersistenceManager.getInstance().getSubmissionPersistence().getSubmission(id);
    }

    public void save(Submission submission, long contestId) throws PersistenceException {
	PersistenceManager.getInstance().getSubmissionPersistence().createSubmission(submission, 1, contestId);
    }

    public void update(Submission submission, long contestId) throws PersistenceException {
	PersistenceManager.getInstance().getSubmissionPersistence().updateSubmission(submission, 1, contestId);
    }
}
