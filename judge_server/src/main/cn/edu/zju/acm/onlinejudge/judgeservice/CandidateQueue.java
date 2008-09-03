/**
 * 
 */
package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.lang.ref.WeakReference;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.SubmissionDAO;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

class CandidateQueue {
    private CandidateImpl head = new CandidateImpl();
    private CandidateImpl tail = head;
    private static SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();

    public Candidate getHead() {
        return this.head;
    }

    public synchronized void add(Submission submission, int priority) {
        CandidateImpl candidate = new CandidateImpl();
        candidate.setPrev(this.tail);
        this.tail.fill(submission, priority);
        this.tail.setNext(candidate);
        this.tail = candidate;
        this.notifyAll();
    }

    private class CandidateImpl implements Candidate {
        private CandidateImpl prev = null;
        private CandidateImpl next = null;
        private long submissionId = -1;

        private WeakReference<Submission> submissionReference = null;

        private boolean claimed = false;
        private int priority;

        public synchronized Submission getSubmission() throws PersistenceException {
            if (this.submissionId < 0) {
                return null;
            }
            Submission submission = this.submissionReference.get();
            if (submission == null) {
                submission = CandidateQueue.submissionDAO.getSubmission(submissionId);
            }
            return submission;
        }

        public void setPrev(CandidateImpl prev) {
            this.prev = prev;
        }

        public void setNext(CandidateImpl next) {
            this.next = next;
        }

        public void fill(Submission submission, int priority) {
            this.submissionId = submission.getId();
            this.submissionReference = new WeakReference<Submission>(submission);
            this.priority = priority;
        }

        @Override
        public int getPriority() {
            return this.priority;
        }

        @Override
        public boolean isClaimed() {
            return this.claimed;
        }

        @Override
        public Candidate getNext() {
            return this.next;
        }

        @Override
        public synchronized boolean tryClaim() {
            if (this.claimed) {
                return false;
            }
            this.claimed = true;
            synchronized (CandidateQueue.this) {
                if (this.prev != null) {
                    this.prev.next = this.next;
                }
                this.next.prev = this.prev;
                if (CandidateQueue.this.head == this) {
                    CandidateQueue.this.head = this.next;
                }
            }
            return true;
        }

    }
}