package cn.edu.zju.acm.onlinejudge.judgeserver;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
	JudgeCacheUnitTest.class,
	JudgeDaemonUnitTest.class,
	JudgeQueueUnitTest.class,
	JudgeServerProxyUnitTest.class,
	JudgeServerUnitTest.class })
public class TestSuite {
}
