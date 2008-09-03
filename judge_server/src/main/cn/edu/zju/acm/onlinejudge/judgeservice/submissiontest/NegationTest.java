package cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public class NegationTest implements Test {

    private Test test;

    public NegationTest(Test test) {
        this.test = test;
    }

    @Override
    public boolean test(Submission submission, int priority) {
        return !this.test.test(submission, priority);
    }

}
