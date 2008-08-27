package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.LinkedList;
import java.util.ListIterator;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;

class JudgeQueue {
    private LinkedList<JudgeQueueNode> nodeList = new LinkedList<JudgeQueueNode>();

    private LinkedList<Submission> rejudgeList = new LinkedList<Submission>();

    public synchronized Submission poll() throws Exception {
        for (;;) {
            while (isEmpty()) {
                wait();
            }
            if (rejudgeList.size() == 0) {
                return nodeList.poll().getSubmission();
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

    private boolean insert(Submission submission) {
        for (ListIterator<JudgeQueueNode> it = nodeList.listIterator(nodeList.size()); it.hasPrevious();) {
            JudgeQueueNode node = it.previous();
            if (node.getSubmissionId() == submission.getId()) {
                return false;
            }
            if (node.getSubmissionId() < submission.getId()) {
                it.next();
                it.add(new JudgeQueueNode(submission));
                return true;
            }
        }
        nodeList.addFirst(new JudgeQueueNode(submission));
        return true;
    }

    public synchronized boolean push(Submission submission) {
        boolean empty = isEmpty();
        if (!insert(submission)) {
            return false;
        }
        submission.setJudgeReply(JudgeReply.QUEUING);
        if (empty) {
            notifyAll();
        }
        return true;
    }

    public boolean isEmpty() {
        return nodeList.size() == 0 && rejudgeList.size() == 0;
    }
}