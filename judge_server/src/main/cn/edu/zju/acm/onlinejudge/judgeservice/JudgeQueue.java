package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.SubmissionDAO;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

class JudgeQueue {
    private LinkedList<JudgeQueueNode> nodeList = new LinkedList<JudgeQueueNode>();

    private LinkedList<Submission> rejudgeList = new LinkedList<Submission>();

    private JudgingQueue judgingQueue = new JudgingQueue();

    public synchronized Submission poll() throws InterruptedException, PersistenceException {
        for (;;) {
            while (isEmpty()) {
                wait();
            }
            judgingQueue.tryPop();
            if (rejudgeList.size() == 0) {
                Submission submission = nodeList.poll().getSubmission();
                judgingQueue.push(submission);
                return submission;
            } else {
                return rejudgeList.poll();
            }
        }
    }

    public synchronized void rejudge(Submission submission) {
        submission.setJudgeReply(JudgeReply.QUEUING);
        rejudgeList.offer(submission);
        notifyAll();
    }

    public synchronized void push(Submission submission) {
        boolean empty = isEmpty();
        nodeList.offer(new JudgeQueueNode(submission));
        submission.setJudgeReply(JudgeReply.QUEUING);
        if (empty) {
            notifyAll();
        }
    }

    public boolean isEmpty() {
        return nodeList.size() == 0 && rejudgeList.size() == 0;
    }

    public JudgingList getJudgingList() {
        return this.judgingQueue.clone();
    }

    private static class JudgeQueueNode {
        private long submissionId;

        private WeakReference<Submission> submissionReference;

        private static SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();

        public JudgeQueueNode(Submission submission) {
            submissionId = submission.getId();
            submissionReference = new WeakReference<Submission>(submission);
        }

        public Submission getSubmission() throws PersistenceException {
            Submission submission = submissionReference.get();
            if (submission == null) {
                submission = submissionDAO.getSubmission(submissionId);
            }
            return submission;
        }

        public long getSubmissionId() {
            return submissionId;
        }
    }

    private static class JudgingQueueNode {
        private Submission submission;
        private JudgingQueueNode next = null;

        public JudgingQueueNode(Submission submission) {
            this.submission = submission;
        }

        public Submission getSubmission() {
            return submission;
        }

        public JudgingQueueNode getNext() {
            return next;
        }

        public void setNext(JudgingQueueNode next) {
            this.next = next;
        }
    }

    private static class JudgingQueue implements JudgingList, Cloneable {
        private JudgingQueueNode head = new JudgingQueueNode(null);
        private JudgingQueueNode tail = head;

        public void push(Submission submission) {
            JudgingQueueNode node = new JudgingQueueNode(submission);
            this.tail.setNext(node);
            this.tail = node;
        }

        public void tryPop() {
            while (this.head != tail
                    && (this.head.getSubmission() == null || this.head.getSubmission().getContent() == null)) {
                this.head = this.head.next;
            }
        }

        public Map<Long, Submission> getSubmissionMap() {
            HashMap<Long, Submission> submissionMap = new HashMap<Long, Submission>();
            JudgingQueueNode p = head;
            while (p != null) {
                Submission submission = p.getSubmission();
                if (submission != null) {
                    submissionMap.put(submission.getId(), submission);
                }
                p = p.getNext();
            }
            return submissionMap;
        }

        public JudgingList clone() {
            try {
                return (JudgingList) super.clone();
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }
}