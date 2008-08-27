package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.dao.SubmissionDAO;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public class MockSubmissionDAO extends MockDAO implements SubmissionDAO {

    private AtomicLong id = new AtomicLong();

    private static Map<Long, Submission> submissionMap = new Hashtable<Long, Submission>();

    public Submission getSubmission(long id) throws PersistenceException {
        return cloneSubmission(submissionMap.get(id));
    }

    public void save(Submission submission) throws PersistenceException {
        submission.setId(id.getAndIncrement());
        update(submission);
    }

    public void update(Submission submission) throws PersistenceException {
        submissionMap.put(submission.getId(), cloneSubmission(submission));
    }

    private Submission cloneSubmission(Submission submission) {
        Submission ret = new Submission();
        ret.setJudgeComment(submission.getJudgeComment());
        ret.setContent(submission.getContent());
        ret.setId(submission.getId());
        ret.setLanguage(submission.getLanguage());
        ret.setMemoryConsumption(submission.getMemoryConsumption());
        ret.setProblemId(submission.getProblemId());
        ret.setJudgeReply(submission.getJudgeReply());
        ret.setTimeConsumption(submission.getTimeConsumption());
        return ret;
    }
}
