/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

/**
 * 
 */
package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.judgeservice.submissionfilter.SubmissionFilter;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

class SubmissionQueue {
    private Candidate head = new Candidate();
    private Candidate tail = head;
    private Map<SubmissionFilter, SubmissionQueueReaderImpl> readerMap =
            new HashMap<SubmissionFilter, SubmissionQueueReaderImpl>();

    public synchronized void push(Submission submission, int priority) {
        Candidate candidate = new Candidate();
        candidate.prev = this.tail;
        this.tail.submissionId = submission.getId();
        this.tail.submission = submission;
        this.tail.priority = priority;
        this.tail.next = candidate;
        this.tail = candidate;
        this.notifyAll();
    }

    public SubmissionQueueReader getReader(SubmissionFilter submissionFilter) {
        synchronized (this.readerMap) {
            SubmissionQueueReaderImpl reader = this.readerMap.get(submissionFilter);
            if (reader == null) {
                reader = this.new SubmissionQueueReaderImpl(submissionFilter);
                this.readerMap.put(submissionFilter, reader);
            }
            ++reader.referenceCount;
            return reader;
        }
    }

    private class Candidate {
        Candidate prev = null;
        Candidate next = null;
        long submissionId = -1;

        Submission submission = null;

        boolean claimed = false;
        int priority;

        public synchronized Submission getSubmission() throws PersistenceException {
            return this.submission;
        }

        public boolean tryClaim() {
            if (this.claimed) {
                return false;
            }
            synchronized (this) {
                if (this.claimed) {
                    return false;
                }
                this.claimed = true;
            }
            synchronized (SubmissionQueue.this) {
                if (this.prev != null) {
                    this.prev.next = this.next;
                }
                this.next.prev = this.prev;
                if (SubmissionQueue.this.head == this) {
                    SubmissionQueue.this.head = this.next;
                }
                // IMPORTANT: Set prev to null to make garbage collection possible. Don't set next to null so that any
                // candidate reference can travel to the main queue by following this link. This is important to support
                // multi-threaded queue readers.
                this.prev = null;
            }
            return true;
        }
    }

    private class SubmissionQueueReaderImpl implements SubmissionQueueReader {
        private SubmissionFilter submissionFilter;
        int referenceCount = 0;
        private Candidate head = SubmissionQueue.this.head;
        private List<LinkedList<Candidate>> candidatesLists = new ArrayList<LinkedList<Candidate>>();
        private int size = 0;

        private SubmissionQueueReaderImpl(SubmissionFilter submissionFilter) {
            this.submissionFilter = submissionFilter;
            for (int i = Priority.MIN; i <= Priority.MAX; ++i) {
                candidatesLists.add(new LinkedList<Candidate>());
            }
        }

        @Override
        public synchronized Submission poll(JudgeClientJudgeThread judgeThread) throws InterruptedException,
                PersistenceException {
            for (;;) {
                while (this.head.next != null) {
                    if (!this.head.claimed) {
                        this.add(this.head);
                    }
                    this.head = this.head.next;
                }
                if (this.size == 0) {
                    synchronized (SubmissionQueue.this) {
                        while (this.head.next != null) {
                            if (!this.head.claimed) {
                                this.add(this.head);
                            }
                            this.head = this.head.next;
                        }
                        if (this.size == 0) {
                            SubmissionQueue.this.wait();
                            continue;
                        }
                    }
                }
                for (int i = this.candidatesLists.size() - 1; i >= 0; --i) {
                    LinkedList<Candidate> candidatesList = this.candidatesLists.get(i);
                    while (candidatesList.size() > 0) {
                        Candidate candidate = candidatesList.removeFirst();
                        --this.size;
                        if (candidate.tryClaim()) {
                            return candidate.getSubmission();
                        }
                    }
                }
            }
        }

        @Override
        public void close() {
            synchronized (SubmissionQueue.this.readerMap) {
                if (--this.referenceCount == 0) {
                    SubmissionQueue.this.readerMap.remove(this.submissionFilter);
                }
            }
        }

        private void add(Candidate candidate) throws PersistenceException {
            Submission submission = candidate.getSubmission();
            int priority = candidate.priority;
            priority += this.submissionFilter.filter(submission, priority);
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
    }
}