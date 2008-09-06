package cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public class ProblemTest {
    private int problemId;

    public ProblemTest(int problemId) {
        this.problemId = problemId;
    }

    public boolean test(Submission submission, int priority) {
        return this.problemId == submission.getProblemId();
    }

    @Override
    public int hashCode() {
        return this.problemId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProblemTest) {
            return this.problemId == ((ProblemTest) obj).problemId;
        }
        return false;
    }
}
