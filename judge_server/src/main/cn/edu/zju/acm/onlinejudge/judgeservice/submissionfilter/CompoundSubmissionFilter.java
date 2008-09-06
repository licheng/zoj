package cn.edu.zju.acm.onlinejudge.judgeservice.submissionfilter;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public class CompoundSubmissionFilter implements SubmissionFilter {
    private List<SubmissionFilter> submissionFilterList = new ArrayList<SubmissionFilter>();

    public void add(SubmissionFilter submissionFilter) {
        this.submissionFilterList.add(submissionFilter);
    }

    @Override
    public int filter(Submission submission, int priority) {
        for (SubmissionFilter submissionFilter : this.submissionFilterList) {
            if (submissionFilter != null) {
                int delta = submissionFilter.filter(submission, priority);
                if (delta != 0) {
                    return delta;
                }
            }
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return this.submissionFilterList.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CompoundSubmissionFilter) {
            CompoundSubmissionFilter compoundSubmissionFilter = (CompoundSubmissionFilter) obj;
            return this.submissionFilterList.equals(compoundSubmissionFilter.submissionFilterList);
        }
        return false;
    }
}
