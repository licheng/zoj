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
