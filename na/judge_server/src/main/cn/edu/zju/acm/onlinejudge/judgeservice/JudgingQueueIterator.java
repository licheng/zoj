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

import cn.edu.zju.acm.onlinejudge.bean.Submission;

/**
 * JudgingQueueIterator can be used to traverse all submissions judged and being judged since it construction. Because
 * the state of a submission being judged is not reflected in the database, this interface is very useful when querying
 * submissions from database. Before querying database, acquires a JudgingQueueIterator instance. After database results
 * are returned, iterates by this iterator and updates the results.
 * 
 * This iterator is lock-free and never throws ConcurrentModificationException.
 * 
 * @author Xu, Chuan
 * 
 */
public interface JudgingQueueIterator {
    /**
     * Returns the next submission or null if reaches the end of queue.
     * 
     * @return the next submission or null if reaches the end of queue.
     */
    public Submission next();
}
