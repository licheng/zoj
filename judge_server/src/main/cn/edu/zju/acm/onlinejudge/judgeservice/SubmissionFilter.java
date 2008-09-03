package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest.Test;

public class SubmissionFilter {
    public static final int DENY = -100000;
    private List<Test> testList = new ArrayList<Test>();
    private List<Integer> priorityList = new ArrayList<Integer>();

    public void add(Test test, int priority) {
        this.testList.add(test);
        this.priorityList.add(priority);
    }
    
    public void addFirst(Test test, int priority) {
        this.testList.add(0, test);
        this.priorityList.add(0, priority);
    }

    public int filter(Submission submission, int priority) {
        int ret = 0;
        Logger logger = Logger.getLogger(this.getClass());
        for (int i = 0; i < this.testList.size(); ++i) {
            if (this.testList.get(i).test(submission, priority)) {
                ret += this.priorityList.get(i);
            }
        }
        return ret;
    }
}
