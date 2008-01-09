package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.lang.ref.WeakReference;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.SubmissionDAO;

public class JudgeQueueNode {
    private long submissionId;

    private WeakReference<Submission> submissionReference;

    private static SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();

    public JudgeQueueNode(Submission submission) {
	submissionId = submission.getId();
	submissionReference = new WeakReference<Submission>(submission);
    }

    public Submission getSubmission() throws Exception {
	Submission submission = submissionReference.get();
	if (submission == null) {
	    submission = submissionDAO.getSubmission(submissionId);
	}
	return submission;
    }

    public long getSubmissionId() {
	return submissionId;
    }
}
