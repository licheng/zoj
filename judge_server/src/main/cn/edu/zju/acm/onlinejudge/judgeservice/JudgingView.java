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

package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

/**
 * JudgingView is a view of all submissions judged and being judged since the creation of this object. Because the state
 * of a submission being judged is not reflected in the database, this interface is very useful when querying
 * submissions from database. Before querying database, acquires a JudgingView instance. After database results are returned, invokes 
 * getSubmissionMap and updates the results.
 * 
 * Thread Safety: all subclasses of this interface should be thread safe.
 * 
 * CAVEAT: Keeping long-living strong references to instances of this interface may cause out-of-memory issues because
 * it keeps ALL submissions judged and being judged since the creation of this object
 * 
 * @author xuchuan
 * 
 */
public interface JudgingView {
    /**
     * Returns a map from submission id to submission instance which includes all submissions judged or being judged
     * since the creation of this object.
     * 
     * 
     * @return A map from submission id to submission instance which includes all submissions judged or being judged
     *         since the creation of this object.
     */
    public Map<Long, Submission> getSubmissionMap();
}
