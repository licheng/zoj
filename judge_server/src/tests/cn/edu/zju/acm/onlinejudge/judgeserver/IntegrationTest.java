package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.xuchuan.util.ReflectionUtil;

public class IntegrationTest {
    private static class TestCase {
	public String content;

	public long result;

	public String extension;

	public TestCase(String content, long result, String extension) {
	    this.content = content;
	    this.result = result;
	    this.extension = extension;
	}
    }

    private JudgeService service;

    private TestCase[] testcases =
	    new TestCase[] {
		    new TestCase("#include <iostream>\nusing namespace std;int main(){int a, b;while(cin>>a>>b){cout<<a+b<<endl;}}",
			    JudgeReply.ACCEPTED.getId(),
			    "cc"),
		    new TestCase("#include <iostream>\nusing namespace std;int main(){int a, b;while(cin>>a>>b){cout<<a+b+1<<endl;}}",
			    JudgeReply.WRONG_ANSWER.getId(),
			    "cc"),
		    new TestCase("invalid", JudgeReply.COMPILATION_ERROR.getId(), "cc"),
		    new TestCase("#include <iostream>\nusing namespace std;int main(){int a, b;while(cin>>a>>b){cout<<a+b<<endl<<endl;}}",
			    JudgeReply.PRESENTATION_ERROR.getId(),
			    "cc"),
		    new TestCase("#include <iostream>\nusing namespace std;int main(){while(1);return 0;}",
			    JudgeReply.TIME_LIMIT_EXCEEDED.getId(),
			    "cc"),
		    new TestCase("#include <iostream>\nusing namespace std;int main(){while(1)new int[1024];return 0;}",
			    JudgeReply.MEMORY_LIMIT_EXCEEDED.getId(),
			    "cc"),
		    new TestCase("#include <iostream>\nusing namespace std;int main(){while(1)cout<<\"Hello world!\";return 0;}",
			    JudgeReply.OUTPUT_LIMIT_EXCEEDED.getId(),
			    "cc"),
		    new TestCase("#include <string.h>\nint a[1];int main(){memset(a, 0, 10000000);return 0;}",
			    JudgeReply.SEGMENTATION_FAULT.getId(),
			    "cc"),
		    new TestCase("int main(){int a=0,b=1/a;return b;}", JudgeReply.FLOATING_POINT_ERROR.getId(), "cc"),
		    new TestCase("#include <unistd.h>\nint main(){fork();return 0;}",
			    JudgeReply.RUNTIME_ERROR.getId(),
			    "cc") };

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
	service = new JudgeService(128);
	service.addJudgeServer(InetAddress.getByName("192.168.1.128"), 8725, 3);
    }

    @After
    public void TearDown() throws Exception {
	service.removeJudgeServer(InetAddress.getByName("192.168.1.128"), 8725);
    }

    @Test
    public void test() throws Exception {
	for (int i = 0; i < testcases.length; i++) {
	    Submission submission = new Submission();
	    submission.setContent(testcases[i].content);
	    submission.setLanguage(LanguageManager.getLanguageByExtension(testcases[i].extension));
	    submission.setProblemId(0);
	    submission.setJudgeReply(JudgeReply.QUEUING);
	    DAOFactory.getSubmissionDAO().save(submission);
	    service.judge(submission);
	}
	for (int i = 0; i < testcases.length; i++) {
	    for (;;) {
		Submission submission = DAOFactory.getSubmissionDAO().getSubmission(i);
		if (submission.getJudgeReply() != JudgeReply.QUEUING) {
		    assertEquals((long) testcases[i].result, submission.getJudgeReply().getId());
		    break;
		}
		Thread.sleep(1000);
	    }
	}
    }
}
