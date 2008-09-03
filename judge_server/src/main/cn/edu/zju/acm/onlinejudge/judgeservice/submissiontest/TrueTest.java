package cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public class TrueTest implements Test {
    @Override
    public boolean test(Submission submission, int priority) {
        return true;
    }
}
