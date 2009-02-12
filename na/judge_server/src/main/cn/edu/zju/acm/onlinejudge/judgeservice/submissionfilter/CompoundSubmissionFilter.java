/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

package cn.edu.zju.acm.onlinejudge.judgeservice.submissionfilter;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public class CompoundSubmissionFilter implements SubmissionFilter {
    private List<SubmissionFilter> submissionFilterList = new ArrayList<SubmissionFilter>();

    public void add(SubmissionFilter submissionFilter) {
        this.submissionFilterList.add(submissionFilter);
    }

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
