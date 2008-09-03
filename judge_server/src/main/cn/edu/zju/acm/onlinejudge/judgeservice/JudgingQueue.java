/**
 * 
 */
package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.HashMap;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

class JudgingQueue implements JudgingList, Cloneable {
    private JudgingQueue.JudgingQueueNode head = new JudgingQueueNode(null);
    private JudgingQueue.JudgingQueueNode tail = head;

    public synchronized void push(Submission submission) {
        JudgingQueue.JudgingQueueNode node = new JudgingQueueNode(submission);
        this.tail.setNext(node);
        this.tail = node;
    }

    public synchronized void tryPop() {
        while (this.head != tail &&
                (this.head.getSubmission() == null || this.head.getSubmission().getContent() == null)) {
            this.head = this.head.next;
        }
    }

    @Override
    public Map<Long, Submission> getSubmissionMap() {
        HashMap<Long, Submission> submissionMap = new HashMap<Long, Submission>();
        JudgingQueue.JudgingQueueNode p = head;
        while (p != null) {
            Submission submission = p.getSubmission();
            if (submission != null) {
                submissionMap.put(submission.getId(), submission);
            }
            p = p.getNext();
        }
        return submissionMap;
    }

    @Override
    public synchronized JudgingList clone() {
        try {
            return (JudgingList) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    private static class JudgingQueueNode {
        private Submission submission;
        private JudgingQueue.JudgingQueueNode next = null;

        public JudgingQueueNode(Submission submission) {
            this.submission = submission;
        }

        public Submission getSubmission() {
            return submission;
        }

        public JudgingQueue.JudgingQueueNode getNext() {
            return next;
        }

        public void setNext(JudgingQueue.JudgingQueueNode next) {
            this.next = next;
        }
    }
}