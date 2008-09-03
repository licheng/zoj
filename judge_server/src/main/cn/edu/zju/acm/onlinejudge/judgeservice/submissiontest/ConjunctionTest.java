package cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public class ConjunctionTest extends CompoundTest {
    @Override
    public boolean test(Submission submission, int priority) {
        for (Test test : this.tests) {
            if (!test.test(submission, priority)) {
                return false;
            }
        }
        return true;
    }

}
