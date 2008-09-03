package cn.edu.zju.acm.onlinejudge.judgeservice.submissiontest;

import java.util.HashSet;
import java.util.Set;

public abstract class CompoundTest implements Test {
    protected Set<Test> tests = new HashSet<Test>();

    public void add(Test test) {
        this.tests.add(test);
    }
}
