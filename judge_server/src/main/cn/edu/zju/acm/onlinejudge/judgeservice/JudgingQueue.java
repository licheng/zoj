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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

/**
 * This class is a queue of all submissions being judged. It is responsible for returning JudingView instances.
 * 
 * This class is like ConcurrentLinkedQueue and JudgingViewImpl behaves like its iterator. However, removal is only
 * allowed from the head of queue and all removals are not reflected in JudgingView.
 * 
 * All submissions are put in a single linked list. Pop operation only moves the head reference forward without changing
 * the internal linked list structure. Once a JudgingViewImpl instance is created, it copies the current head reference.
 * In this way, a JudgingViewImpl instance can always visit all nodes created subsequent to its construction by its
 * internal head reference.
 * 
 * Nodes can be garbage collected when no submission referenced by its prior nodes is still under judge and there is no
 * reachable(in other words, used by any living thread) JudgingViewImpl instances created prior to its construction. So
 * as long as we expect that the judge of a submission finishes quite quickly(say, in one minute) and the life of a
 * JudgingViewImpl instance is short(usually in seconds), this queue does not cost too much memory.
 * 
 * This implementation is thread safe and lock-free just like ConcurrentLinkedQueue.
 * 
 * The same submission may be push into this queue multiple times. When this happens, the linked list will contains
 * multiple nodes with the same submission reference. It is safe excepting using more memory. Usually it is not a
 * problem.
 * 
 * CAVEAT: Remember to invoke tryPop when the judge of a submission is done and set the judgeReply field of submissions
 * properly.
 * 
 * @author xuchuan
 * 
 */
class JudgingQueue {

    /**
     * The head of this queue.
     */
    private JudgingQueue.JudgingQueueNode head = new JudgingQueue.JudgingQueueNode();

    /**
     * The tail of this queue. Always points to an empty node.
     */
    private AtomicReference<JudgingQueue.JudgingQueueNode> tail =
            new AtomicReference<JudgingQueue.JudgingQueueNode>(this.head);

    /**
     * Adds a new submission.
     * 
     * @param submission
     *            the submission to add
     */
    public void push(Submission submission) {
        if (submission == null) {
            throw new NullPointerException("submission should not be null");
        }
        JudgingQueue.JudgingQueueNode node = new JudgingQueueNode();
        JudgingQueue.JudgingQueueNode last = this.tail.getAndSet(node);
        last.next = node;
        last.submission = submission;
    }

    /**
     * Pops all submissions that are judged.
     */
    public void tryPop() {
        JudgingQueue.JudgingQueueNode node = this.head;
        while (node.submission != null && node.submission.getJudgeReply().isCommittedReply()) {
            node = node.next;
        }
        // Not a problem when multiple threads trying to modify this value. The head will finally be moved forward.
        this.head = node;
    }

    public JudgingView getJudingView() {
        return new JudgingViewImpl(this.head);
    }

    private static class JudgingQueueNode {
        Submission submission = null;
        JudgingQueue.JudgingQueueNode next = null;
    }

    private static class JudgingViewImpl implements JudgingView {
        JudgingQueue.JudgingQueueNode head;

        public JudgingViewImpl(JudgingQueue.JudgingQueueNode head) {
            this.head = head;
        }

        /**
         * See JudgingView.getSubmissionMap
         */
        @Override
        public Map<Long, Submission> getSubmissionMap() {
            HashMap<Long, Submission> submissionMap = new HashMap<Long, Submission>();
            JudgingQueue.JudgingQueueNode p = this.head;
            while (p.submission != null) {
                Submission submission = p.submission;
                submissionMap.put(submission.getId(), submission);
                p = p.next;
            }
            return submissionMap;
        }
    }
}