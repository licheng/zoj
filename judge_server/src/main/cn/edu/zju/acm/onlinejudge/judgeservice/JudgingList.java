package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public interface JudgingList {
    public Map<Long, Submission> getSubmissionMap();
}
