package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

public class JudgeServerProxyUnitTest {
    private JudgeServerProxy proxy;

    private Submission submission;

    private static MockServer server;

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
	DAOFactory.getProblemDAO().update(problem);
	problem.setId(1);
	problem.setChecker(true);
	DAOFactory.getProblemDAO().update(problem);
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
	reference = new Reference();
	reference.setReferenceType(ReferenceType.CHECKER_SOURCE);
	reference.setContent("test".getBytes("ASCII"));
	reference.setContentType("cc");
	DAOFactory.getReferenceDAO().save(reference, 1);
    }

    @Before
    public void setUp() throws Exception {
	server = new MockServer();
	proxy = new JudgeServerProxy(InetAddress.getLocalHost(), server.getPort());
	submission = new Submission();
	submission.setId(0);
	submission.setLanguage(LanguageManager.getLanguage(0));
	submission.setProblemId(0);
	submission.setContent("test");
    }

    @After
    public void tearDown() throws Exception {
	server.stop();
    }

    @Test(expected = NoSuchProblemException.class)
    public void testJudgeNoSuchProblem() throws Exception {
	server.addReply(MockServer.NO_SUCH_PROBLEM);
	proxy.judge(submission);
    }

    @Test(expected = JudgeServerErrorException.class)
    public void testJudgeSaveSourceServerError() throws Exception {
	server.addReply(MockServer.SAVE_SERVER_ERROR);
	proxy.judge(submission);
    }

    @Test(expected = JudgeServerErrorException.class)
    public void testJudgeCompilationInternalError() throws Exception {
	server.addReply(MockServer.COMPILATION_SERVER_ERROR);
	proxy.judge(submission);
    }

    @Test
    public void testJudgeCompilationError() throws Exception {
	server.addReply(MockServer.COMPILATION_ERROR);
	proxy.judge(submission);
	assertEquals(JudgeReply.COMPILATION_ERROR, submission.getJudgeReply());
	assertEquals("compilation error\n", submission.getJudgeComment());
    }

    @Test(expected = JudgeServerErrorException.class)
    public void testJudgeRunningInternalError() throws Exception {
	server.addReply(MockServer.RUNNING_SERVER_ERROR);
	proxy.judge(submission);
    }

    @Test
    public void testJudgeRuntimeError() throws Exception {
	server.addReply(MockServer.RUNTIME_ERROR);
	proxy.judge(submission);
	assertEquals(JudgeReply.RUNTIME_ERROR, submission.getJudgeReply());
	assertEquals(1100, submission.getTimeConsumption());
	assertEquals(2, submission.getMemoryConsumption());
    }

    @Test(expected = JudgeServerErrorException.class)
    public void testJudgeJudgeInternalError() throws Exception {
	server.addReply(MockServer.JUDGE_INTERNAL_ERROR);
	proxy.judge(submission);
    }

    @Test
    public void testJudgeAccepted() throws Exception {
	server.addReply(MockServer.ACCEPTED);
	proxy.judge(submission);
	assertEquals(JudgeReply.ACCEPTED, submission.getJudgeReply());
	assertEquals(1100, submission.getTimeConsumption());
	assertEquals(2, submission.getMemoryConsumption());
	assertEquals(new String[] { "judge", "0", "cc", "0", "*", "0", "1", "1024", "1" }, server.getCommand());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendProblemInternalError() throws Exception {
	server.addReply(MockServer.SAVE_INTERNAL_ERROR);
	proxy.sendProblem(DAOFactory.getProblemDAO().getProblem(0));
    }

    @Test
    public void testSendProblemDuplicateProblem() throws Exception {
	server.addReply(new String[] { "" + JudgeServerProxy.DUPLICATE_PROBLEM });
	proxy.sendProblem(DAOFactory.getProblemDAO().getProblem(0));
	assertEquals(new String[] { "saveprob", "0", "0" }, server.getCommand());
	assertEquals(0, server.getFile().length);
    }

    @Test(expected = JudgeServerErrorException.class)
    public void testSendProblemSaveProblemInternalError() throws Exception {
	server.addReply(MockServer.SAVE_SERVER_ERROR);
	proxy.sendProblem(DAOFactory.getProblemDAO().getProblem(0));
    }

    @Test
    public void testSendProblem0() throws Exception {
	server.addReply(MockServer.SAVE_SUCCESS);
	proxy.sendProblem(DAOFactory.getProblemDAO().getProblem(0));
	Map<String, String> fileMap = new HashMap<String, String>();
	ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(server.getFile()));
	int numberOfEntries = 0;
	for (;;) {
	    ZipEntry entry = zip.getNextEntry();
	    if (entry == null) {
		break;
	    }
	    numberOfEntries++;
	    byte[] buf = new byte[1024];
	    byte[] content = new byte[zip.read(buf, 0, buf.length)];
	    System.arraycopy(buf, 0, content, 0, content.length);
	    fileMap.put(entry.getName(), new String(content, "ASCII"));
	}
	assertEquals(2, numberOfEntries);
	assertEquals("0 0\n1 2\n2 3\n", fileMap.get("input.0"));
	assertEquals("0\n3\n5\n", fileMap.get("output.0"));
    }

    @Test
    public void testSendProblem1() throws Exception {
	server.addReply(MockServer.SAVE_SUCCESS);
	proxy.sendProblem(DAOFactory.getProblemDAO().getProblem(1));
	Map<String, String> fileMap = new HashMap<String, String>();
	ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(server.getFile()));
	int numberOfEntries = 0;
	for (;;) {
	    ZipEntry entry = zip.getNextEntry();
	    if (entry == null) {
		break;
	    }
	    numberOfEntries++;
	    byte[] buf = new byte[1024];
	    byte[] content = new byte[zip.read(buf, 0, buf.length)];
	    System.arraycopy(buf, 0, content, 0, content.length);
	    fileMap.put(entry.getName(), new String(content, "ASCII"));
	}
	assertEquals(3, numberOfEntries);
	assertEquals("0 0\n1 2\n2 3\n", fileMap.get("input.0"));
	assertEquals("0\n3\n5\n", fileMap.get("output.0"));
	assertEquals("test", fileMap.get("judge.cc"));
    }

    @Test(expected = JudgeServerErrorException.class)
    public void testConnectionTimeOut() throws Exception {
	proxy = new JudgeServerProxy(InetAddress.getLocalHost(), server.getPort() + 1);
	proxy.judge(submission);
    }

    @Test(expected = JudgeServerErrorException.class)
    public void testReadTimeOut() throws Exception {
	server.addReply(new String[0]);
	proxy.judge(submission);
    }
}
