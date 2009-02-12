package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

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

public class JudgeClientInstanceUnitTest {
    private JudgeClientJudgeThread instance;

    private Submission submission;

    private SubmissionQueueReader queue;

    private static MockClient server;

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
        DAOFactory.getReferenceDAO().save(reference, 1);
        DAOFactory.getProblemDAO().update(problem);
    }

    @Before
    public void setUp() throws Exception {
        // server = new MockClient();
        queue = new SubmissionQueueReader();
        submission = new Submission();
        submission.setId(0);
        submission.setLanguage(LanguageManager.getLanguage(0));
        submission.setProblemId(0);
        submission.setContent("test");
        DAOFactory.getSubmissionDAO().update(submission);
        instance = new JudgeClientJudgeThread(queue, new InetSocketAddress(InetAddress.getByName("192.168.37.130"), 2191));
        instance.start();
    }

    @After
    public void tearDown() throws Exception {
        //server.stop();
        //instance.interrupt();
    }

    @Test
    public void testSendProblemAutomatically() throws Exception {
        Thread.sleep(10000);
    }

    @Test(timeout = 1000)
    public void testRejudge() throws Exception {
    }

    @Test
    public void testNormal() throws Exception {
    }

    @Test(timeout = 3000)
    public void testConnectionTimeOut() throws Exception {
    }

    @Test(timeout = 15000)
    public void testReadTimeOut() throws Exception {
    }
}
