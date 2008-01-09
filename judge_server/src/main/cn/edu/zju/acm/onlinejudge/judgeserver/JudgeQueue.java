package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.util.LinkedList;
import java.util.ListIterator;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

class JudgeQueue {
    private LinkedList<JudgeQueueNode> nodeList = new LinkedList<JudgeQueueNode>();

    private LinkedList<JudgeQueueNode> rejudgeList = new LinkedList<JudgeQueueNode>();

    public synchronized Submission poll() throws Exception {
	for (;;) {
	    while (isEmpty()) {
		wait();
	    }
	    Submission submission;
	    if (rejudgeList.size() == 0) {
		submission = nodeList.poll().getSubmission();
	    } else {
		submission = rejudgeList.poll().getSubmission();
	    }
	    return submission;
	}
    }

    public synchronized void rejudge(Submission submission) {
	JudgeQueueNode node = new JudgeQueueNode(submission);
	rejudgeList.add(node);
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
	if (empty) {
	    notifyAll();
	}
	return true;
    }

    public boolean isEmpty() {
	return nodeList.size() == 0 && rejudgeList.size() == 0;
    }
}