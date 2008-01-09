package cn.edu.zju.acm.onlinejudge.judgeserver;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

class JudgeCache {
    private static class Node {
	Submission submission = null;

	Node linkNext = null;

	Node hashPrev = null;

	Node hashNext = null;

	public Node(Submission submission) {
	    this.submission = submission;
	}
    }

    private Node linkHead = null;

    private Node linkTail = new Node(null);

    private Node[] table = null;

    private int capacity = 0;

    private int remainingCapacity = 0;

    public JudgeCache(int capacity) {
	for (int i = 2;; i *= 2) {
	    if (i >= capacity) {
		capacity = i;
		break;
	    }
	}
	this.remainingCapacity = this.capacity = capacity;
	table = new Node[capacity * 2];
    }
    
    public JudgeCache(int capacity, JudgeCache cache) {
	this(capacity);
	synchronized(cache) {
	    for (Node p = cache.linkHead; p != null; p = p.linkNext) {
		add(p.submission);
	    }
	}
    }

    private int hash(long id) {
	return (int) (id % table.length);
    }

    public Submission get(long id) {
	for (Node p = table[hash(id)]; p != null; p = p.hashNext) {
	    if (p.submission.getId() == id) {
		return p.submission;
	    }
	}
	return null;
    }

    public synchronized void add(Submission submission) {
	Node node;
	if (remainingCapacity == 0) {
	    node = linkHead;
	    linkHead = linkHead.linkNext;
	    if (node.hashPrev == null) {
		table[hash(node.submission.getId())] = node.hashNext;
	    } else {
		node.hashPrev.hashNext = node.hashNext;
	    }
	    node.submission = submission;
	    node.linkNext = node.hashPrev = node.hashNext = null;
	} else {
	    remainingCapacity--;
	    node = new Node(submission);
	    if (linkHead == null) {
		linkHead = node;
	    }
	}
	int index = hash(submission.getId());
	if (table[index] != null) {
	    node.hashNext = table[index];
	    table[index].hashPrev = node;
	}
	table[index] = node;
	linkTail.linkNext = node;
	linkTail = node;
    }

    public int getCapacity() {
	return capacity;
    }
}
