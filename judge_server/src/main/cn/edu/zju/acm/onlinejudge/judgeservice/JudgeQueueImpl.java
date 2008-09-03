/**
 * 
 */
package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

class JudgeQueueImpl implements JudgeQueue {
    private SubmissionFilter serviceFilter;
    private SubmissionFilter clientFilter;
    private SubmissionFilter threadFilter;
    private CandidateQueue candidateQueue;
    private Candidate head;
    private List<LinkedList<Candidate>> candidatesLists = new ArrayList<LinkedList<Candidate>>();
    private int size = 0;

    public JudgeQueueImpl(CandidateQueue candidateQueue, SubmissionFilter serviceFilter,
            SubmissionFilter clientFilter, SubmissionFilter threadFilter) {
        this.candidateQueue = candidateQueue;
        this.head = this.candidateQueue.getHead();
        this.serviceFilter = serviceFilter;
        this.clientFilter = clientFilter;
        this.threadFilter = threadFilter;
        for (int i = Priority.MIN; i <= Priority.MAX; ++i) {
            candidatesLists.add(new LinkedList<Candidate>());
        }
    }

    public void add(Candidate candidate) throws PersistenceException {
        Submission submission = candidate.getSubmission();
        int priority = candidate.getPriority();
        int p = 0;
        if (this.threadFilter != null) {
            p = this.threadFilter.filter(submission, priority);
        }
        if (p == 0 && this.clientFilter != null) {
            p = this.clientFilter.filter(submission, priority);
        }
        if (p == 0 && this.serviceFilter != null) {
            p = this.serviceFilter.filter(submission, priority);
        }
        priority += p;
        priority -= Priority.MIN;
        if (priority < 0) {
            return;
        }
        if (priority >= this.candidatesLists.size()) {
            priority = this.candidatesLists.size() - 1;
        }
        this.candidatesLists.get(priority).add(candidate);
        ++this.size;
    }

    public Submission poll() throws InterruptedException, PersistenceException {
        for (;;) {
            while (head.getNext() != null) {
                if (!head.isClaimed()) {
                    this.add(head);
                }
                head = head.getNext();
            }
            synchronized (this.candidateQueue) {
                while (head.getNext() != null) {
                    if (!head.isClaimed()) {
                        this.add(head);
                    }
                    head = head.getNext();
                }
                if (this.size == 0) {
                    this.candidateQueue.wait();
                    continue;
                }
            }
            for (int i = this.candidatesLists.size() - 1; i >= 0; --i) {
                LinkedList<Candidate> candidatesList = this.candidatesLists.get(i);
                while (candidatesList.size() > 0) {
                    --this.size;
                    Candidate candidate = candidatesList.removeFirst();
                    if (candidate.tryClaim()) {
                        return candidate.getSubmission();
                    }
                }
            }
        }
    }
}