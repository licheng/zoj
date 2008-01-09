package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.xuchuan.util.ReflectionUtil;

import static org.junit.Assert.*;

public class JudgeServerUnitTest {
    private Submission[] submissions;

    private static MockServer mockServer;

    private JudgeQueue queue;

    private JudgeServer judgeServer;

    @BeforeClass
    public static void init() throws Exception {
	ReflectionUtil.setFieldValue(DAOFactory.class, "languageDAO", new MockLanguageDAO());
	ReflectionUtil.setFieldValue(DAOFactory.class, "problemDAO", new MockProblemDAO());
	ReflectionUtil.setFieldValue(DAOFactory.class, "submissionDAO", new MockSubmissionDAO());
	ReflectionUtil.setFieldValue(DAOFactory.class, "referenceDAO", new MockReferenceDAO());
	Problem problem = new Problem();
	problem.setId(0);
	problem.setRevision(0);
	Limit limit = new Limit();
	limit.setTimeLimit(1);
	limit.setMemoryLimit(1024);
	limit.setOutputLimit(1);
	problem.setLimit(limit);
	Reference reference = new Reference();
	reference.setReferenceType(ReferenceType.INPUT);
	reference.setContent("0 0\n1 2\n2 3\n".getBytes("ASCII"));
	DAOFactory.getReferenceDAO().save(reference, 0);
	DAOFactory.getReferenceDAO().save(reference, 1);
	reference = new Reference();
	reference.setReferenceType(ReferenceType.OUTPUT);
	reference.setContent("0\n3\n5\n".getBytes("ASCII"));
	DAOFactory.getReferenceDAO().save(reference, 0);
	DAOFactory.getProblemDAO().update(problem);
    }

    @Before
    public void setUp() throws Exception {
	mockServer = new MockServer(true);
	queue = new JudgeQueue();
	judgeServer = new JudgeServer(queue, InetAddress.getLocalHost(), mockServer.getPort(), 3);
	judgeServer.start();
	submissions = new Submission[10];
	for (int i = 0; i < submissions.length; i++) {
	    submissions[i] = new Submission();
	    submissions[i].setId(i);
	    submissions[i].setLanguage(LanguageManager.getLanguage(0));
	    submissions[i].setProblemId(0);
	    submissions[i].setContent("test");
	    DAOFactory.getSubmissionDAO().update(submissions[i]);
	}
    }

    @After
    public void tearDown() throws Exception {
	judgeServer.stop();
	mockServer.stop();
    }

    @Test
    public void testNormal() throws Exception {
	for (int i = 0; i < submissions.length + 1; i++) {
	    mockServer.addReply(MockServer.ACCEPTED);
	}
	for (int i = 0; i < submissions.length; i++) {
	    queue.push(submissions[i]);
	}
	for (int i = 0; i < submissions.length; i++) {
	    for (;;) {
		Submission submission = DAOFactory.getSubmissionDAO().getSubmission(i);
		if (submission.getJudgeReply() == JudgeReply.ACCEPTED) {
		    break;
		}
		Thread.sleep(10);
	    }
	}
	assertEquals(0, mockServer.getReplies().size());
    }

    @Test
    public void testWaitForServer() throws Exception {
	mockServer.addReply(MockServer.SAVE_SERVER_ERROR);
	for (int i = 0; i < submissions.length + 1; i++) {
	    mockServer.addReply(MockServer.ACCEPTED);
	}
	for (int i = 0; i < submissions.length; i++) {
	    queue.push(submissions[i]);
	}
	for (int i = 0; i < submissions.length; i++) {
	    for (;;) {
		Submission submission = DAOFactory.getSubmissionDAO().getSubmission(i);
		if (submission.getJudgeReply() == JudgeReply.ACCEPTED) {
		    break;
		}
		Thread.sleep(10);
	    }
	}
	assertEquals(0, mockServer.getReplies().size());
    }

    @Test
    public void testRestartServer() throws Exception {
	mockServer.addReply(MockServer.ACCEPTED);
	mockServer.addReply(MockServer.COMPILATION_SERVER_ERROR);
	for (int i = 0; i < submissions.length - 1; i++) {
	    mockServer.addReply(MockServer.ACCEPTED);
	}
	mockServer.addReply(MockServer.COMPILATION_SERVER_ERROR);
	mockServer.addReply(MockServer.ACCEPTED);
	mockServer.addReply(MockServer.ACCEPTED);
	for (int i = 0; i < submissions.length; i++) {
	    queue.push(submissions[i]);
	}
	for (int i = 0; i < submissions.length; i++) {
	    for (;;) {
		Submission submission = DAOFactory.getSubmissionDAO().getSubmission(i);
		if (submission.getJudgeReply() == JudgeReply.ACCEPTED) {
		    break;
		}
		Thread.sleep(10);
	    }
	}
	assertEquals(0, mockServer.getReplies().size());
    }
}
