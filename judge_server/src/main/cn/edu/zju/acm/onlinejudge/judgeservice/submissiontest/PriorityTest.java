package cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public class PriorityTest {
    private int priority;

    public PriorityTest(int priority) {
        this.priority = priority;
    }

    public boolean test(Submission submission, int priority) {
        return this.priority == priority;
    }
}
