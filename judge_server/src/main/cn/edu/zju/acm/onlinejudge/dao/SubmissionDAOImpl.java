package cn.edu.zju.acm.onlinejudge.dao;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

class SubmissionDAOImpl extends AbstractDAO implements SubmissionDAO {
    private static SubmissionPersistence submissionPersistence = PersistenceManager.getInstance().getSubmissionPersistence();

    public Submission getSubmission(long id) throws PersistenceException {
        return submissionPersistence.getSubmission(id);
    }

    public void save(Submission submission, long contestId) throws PersistenceException {
        submission.setContestId(contestId);
        submissionPersistence.createSubmission(submission, 1);
    }

    public void update(Submission submission, long contestId) throws PersistenceException {
        submission.setContestId(contestId);
        submissionPersistence.updateSubmission(submission, 1);
    }

    @Override
    public String getSubmissionSource(long id) throws PersistenceException {
        return submissionPersistence.getSubmissionSource(id);
    }
}
