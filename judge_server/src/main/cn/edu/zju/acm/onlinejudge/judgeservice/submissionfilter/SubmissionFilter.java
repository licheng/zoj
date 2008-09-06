package cn.edu.zju.acm.onlinejudge.judgeservice.submissionfilter;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public interface SubmissionFilter {
    public int filter(Submission submission, int priority);
}
