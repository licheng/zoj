/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 */
package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.HashMap;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

class JudgingQueue {
    private JudgingQueue.JudgingQueueNode head = new JudgingQueueNode();
    private JudgingQueue.JudgingQueueNode tail = head;

    public void push(Submission submission) {
        if (submission == null) {
            throw new NullPointerException("submission should not be null");
        }
        JudgingQueue.JudgingQueueNode node = new JudgingQueueNode();
        synchronized (this) {
            this.tail.next = node;
            // Should be put after setting next, otherwise tryPop & getSubmissionMap will fail.
            this.tail.submission = submission;
            this.tail = node;
        }
    }

    public void tryPop() {
        while (this.head.submission != null && this.head.submission.getContent() == null) {
            this.head = this.head.next;
        }
    }

    public JudgingView getJudingView() {
        return new JudgingView() {
            JudgingQueue.JudgingQueueNode head = JudgingQueue.this.head;

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
        };
    }

    private static class JudgingQueueNode {
        Submission submission = null;
        JudgingQueue.JudgingQueueNode next = null;
    }
}