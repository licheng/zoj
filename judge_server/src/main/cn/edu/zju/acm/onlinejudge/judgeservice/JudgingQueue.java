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

import java.util.concurrent.atomic.AtomicReference;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

/**
 * This class is a queue of all submissions being judged.
 * 
 * All submissions are put in a single linked list. Removal operations only move the head reference forward without
 * changing the internal linked list structure. Once a JudgingQueueIterator instance is created, it copies the current
 * head reference. In this way, a JudgingQueueIterator instance can always traverse all nodes created subsequent to its
 * construction.
 * 
 * We expect this queue to be short. A submission is pushed into this queue when its judge starts and removed when judge
 * ends. This is usually less than 1 minute. And we do not expect many judge threads using this queue.
 * 
 * Nodes can be garbage collected when it is not reachable from the head and by any JudgingQueueIterator. So as long as
 * we expect that the life of a JudgingQueueIterator stays short, like in seconds, this queue does not cost too much
 * memory.
 * 
 * This implementation is thread safe and lock-free just like ConcurrentLinkedQueue.
 * 
 * The same submission may be push into this queue multiple times. When this happens, the linked list will contains
 * multiple nodes with the same submission reference. It is safe excepting using a bit more memory. Usually this is not
 * a problem.
 * 
 * 
 * @author Xu, Chuan
 * @version 1.0
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
     * Remove the submission from this queue. It is done by marking the corresponding node and removed and move forward
     * the head if necessary.
     * 
     * NOTE: It is done by a scan of this queue. This queue is expected to be short so that this operation can be
     * finished very quickly. We do have ways to implement this in constant time like using a hashmap or just add a
     * field in Submission class and update the to mark it as removed. The former solution is a waste when the queue
     * keeps short which is expected. The latter one hurts readability a lot. Some alternative designs similar to the
     * latter solution exists, but they all have defects. Let me know if anyone has a good idea about the design.
     */
    public void remove(Submission submission) {
        // Find the submission and mark it as removed.
        JudgingQueue.JudgingQueueNode node = this.head;
        while (node.submission != null) {
            if (node.submission == submission) {
                node.removed = true;
                break;
            }
            node = node.next;
        }

        // Find the last not-removed node.
        node = this.head;
        while (node.submission != null && node.removed) {
            node = node.next;
        }

        // Not a problem when multiple threads trying to modify this value. The head will finally be moved forward.
        this.head = node;
    }

    public JudgingQueueIterator iterator() {
        return new JudgingQueueIteratorImpl(this.head);
    }

    private static class JudgingQueueNode {
        Submission submission = null;
        JudgingQueue.JudgingQueueNode next = null;
        boolean removed = false;
    }

    private static class JudgingQueueIteratorImpl implements JudgingQueueIterator {
        JudgingQueue.JudgingQueueNode head;

        public JudgingQueueIteratorImpl(JudgingQueue.JudgingQueueNode head) {
            this.head = head;
        }

        /**
         * See JudgingQueueIterator.next
         */
        @Override
        public Submission next() {
            Submission ret = this.head.submission;
            if (ret != null) {
                this.head = this.head.next;
            }
            return ret;
        }
    }
}