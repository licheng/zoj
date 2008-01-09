package cn.edu.zju.acm.onlinejudge.dao;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

class SubmissionDAOImpl extends AbstractDAO implements SubmissionDAO {

    public Submission getSubmission(long id) throws PersistenceException {
	return PersistenceManager.getInstance().getSubmissionPersistence().getSubmission(id);
    }

    public void save(Submission submission) throws PersistenceException {
	PersistenceManager.getInstance().getSubmissionPersistence().createSubmission(submission, 1);
    }

    public void update(Submission submission) throws PersistenceException {
	PersistenceManager.getInstance().getSubmissionPersistence().updateSubmission(submission, 1);
    }
}
