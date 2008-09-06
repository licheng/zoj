package cn.edu.zju.acm.onlinejudge.judgeservice.submissionfilter;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest.Test;

public class SimpleSubmissionFilter implements SubmissionFilter {
    public static final int DENY = -100000;
    private Test test;
    private int delta;

    public SimpleSubmissionFilter(Test test, int delta) {
        this.test = test;
        this.delta = delta;
    }

    @Override
    public int filter(Submission submission, int priority) {
        if (this.test.test(submission, priority)) {
            return this.delta;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return this.test.hashCode() + this.delta;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleSubmissionFilter) {
            SimpleSubmissionFilter submissionFilter = (SimpleSubmissionFilter) obj;
            return this.delta == submissionFilter.delta && this.test.equals(submissionFilter.test);
        }
        return false;
    }
}
