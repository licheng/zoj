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

import static org.hamcrest.core.IsNull.*;
import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public class JudgingQueueUnitTest {
    private JudgingQueue queue;
    private Submission[] submissions;

    @Before
    public void setUp() {
        this.queue = new JudgingQueue();
        this.submissions = new Submission[10];
        for (int i = 0; i < this.submissions.length; ++i) {
            this.submissions[i] = new Submission();
            this.submissions[i].setId(i);
        }
    }

    @Test
    public void testEmptyQueue() {
        JudgingQueueIterator iter = this.queue.iterator();
        assertThat(iter.next(), nullValue());
    }

    @Test
    public void testPush() {
        this.queue.push(this.submissions[0]);
        JudgingQueueIterator iter = this.queue.iterator();
        assertThat(iter.next(), is(this.submissions[0]));
        assertThat(iter.next(), nullValue());
    }

    @Test
    public void testIteratorBeforePush() {
        JudgingQueueIterator iter = this.queue.iterator();
        this.queue.push(this.submissions[0]);
        assertThat(iter.next(), is(this.submissions[0]));
        assertThat(iter.next(), nullValue());
    }

    @Test
    public void testMultiplePush() {
        this.queue.push(this.submissions[0]);
        this.queue.push(this.submissions[1]);
        JudgingQueueIterator iter = this.queue.iterator();
        assertThat(iter.next(), is(this.submissions[0]));
        this.queue.push(this.submissions[2]);
        assertThat(iter.next(), is(this.submissions[1]));
        assertThat(iter.next(), is(this.submissions[2]));
        this.queue.push(this.submissions[3]);
        assertThat(iter.next(), is(this.submissions[3]));
        assertThat(iter.next(), nullValue());
    }

    @Test
    public void testRemoveBeforeIterator() {
        this.queue.push(this.submissions[0]);
        this.queue.remove(this.submissions[0]);
        JudgingQueueIterator iter = this.queue.iterator();
        assertThat(iter.next(), nullValue());
    }

    @Test
    public void testRemoveAfterIterator() {
        this.queue.push(this.submissions[0]);
        JudgingQueueIterator iter = this.queue.iterator();
        this.queue.remove(this.submissions[0]);
        assertThat(iter.next(), is(this.submissions[0]));
        assertThat(iter.next(), nullValue());
    }

    @Test
    public void testRemoveNotExisting() {
        this.queue.push(this.submissions[0]);
        this.queue.remove(this.submissions[1]);
        JudgingQueueIterator iter = this.queue.iterator();
        assertThat(iter.next(), is(this.submissions[0]));
        assertThat(iter.next(), nullValue());
    }

    @Test
    public void testMultipleIterator() {
        this.queue.push(this.submissions[0]);
        this.queue.push(this.submissions[1]);

        JudgingQueueIterator iter = this.queue.iterator();

        assertThat(iter.next(), is(this.submissions[0]));

        this.queue.push(this.submissions[2]);

        assertThat(iter.next(), is(this.submissions[1]));
        assertThat(iter.next(), is(this.submissions[2]));
        assertThat(iter.next(), nullValue());

        iter = this.queue.iterator();

        assertThat(iter.next(), is(this.submissions[0]));
        assertThat(iter.next(), is(this.submissions[1]));
        assertThat(iter.next(), is(this.submissions[2]));
        assertThat(iter.next(), nullValue());
    }

    @Test
    public void testMix() {
        this.queue.push(this.submissions[0]);
        this.queue.push(this.submissions[1]);

        JudgingQueueIterator iter1 = this.queue.iterator();

        assertThat(iter1.next(), is(this.submissions[0]));

        this.queue.remove(this.submissions[1]);

        JudgingQueueIterator iter2 = this.queue.iterator();

        this.queue.push(this.submissions[2]);

        assertThat(iter1.next(), is(this.submissions[1]));
        assertThat(iter2.next(), is(this.submissions[0]));
        assertThat(iter2.next(), is(this.submissions[1]));

        this.queue.remove(this.submissions[0]);

        JudgingQueueIterator iter3 = this.queue.iterator();

        assertThat(iter1.next(), is(this.submissions[2]));
        assertThat(iter2.next(), is(this.submissions[2]));
        assertThat(iter3.next(), is(this.submissions[2]));

        this.queue.push(this.submissions[3]);

        assertThat(iter1.next(), is(this.submissions[3]));
        assertThat(iter2.next(), is(this.submissions[3]));
        assertThat(iter3.next(), is(this.submissions[3]));
        assertThat(iter1.next(), nullValue());
        assertThat(iter2.next(), nullValue());
        assertThat(iter3.next(), nullValue());
    }

    /**
     * This test works in this way: We first save a global iterator, then create a set of judge threads which add and
     * remove submissions and another set of check threads which get iterators from the queue. We finally assert that
     * every submission sequence seen by a check thread is a sub-sequence of that returned by the global iterator saved
     * before. In order to be efficient, we do not save all submission sequence seen by check threads. Instead, we save the
     * hash code of submission ids, the first submission id and total number of submissions in the sequence.
     */
    @Test
    public void testMultipleThreads() {
        JudgingQueueIterator allIter = queue.iterator();
        final Thread[] judge = new Thread[50];
        final int maxIdPerJudgeThread = 1000;
        for (int i = 0; i < judge.length; ++i) {
            final int id = i;
            judge[i] = new Thread() {
                public void run() {
                    for (int i = 0; i < maxIdPerJudgeThread; ++i) {
                        Submission submission = new Submission();
                        submission.setId(id * maxIdPerJudgeThread + i);
                        queue.push(submission);
                        Thread.yield();
                        queue.remove(submission);
                    }
                }
            };
            judge[i].start();
        }
        Thread[] check = new Thread[100];
        final long[] start = new long[check.length];
        final int[] len = new int[check.length];
        final long[] hash = new long[check.length];
        for (int i = 0; i < check.length; ++i) {
            final int id = i;
            check[i] = new Thread() {
                public void run() {
                    hash[id] = len[id] = 0;
                    JudgingQueueIterator iter = queue.iterator();
                    for (int i = 0; i < 100; ++i) {
                        Submission submission = iter.next();
                        if (submission != null) {
                            start[id] = hash[id] = submission.getId();
                            break;
                        }
                        Thread.yield();
                    }
                    if (len[id] > 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {}
                        for (Submission submission = iter.next(); submission != null; submission = iter.next()) {
                            ++len[id];
                            hash[id] = hash[id] * 31 + submission.getId();
                        }
                    }
                }
            };
            check[i].start();
        }
        for (int i = 0; i < judge.length; ++i) {
            try {
                judge[i].join();
            } catch (InterruptedException e) {}
        }
        for (int i = 0; i < check.length; ++i) {
            try {
                check[i].join();
            } catch (InterruptedException e) {}
        }
        List<Long> h = new ArrayList<Long>();
        List<Long> base = new ArrayList<Long>();
        Map<Long, Integer> m = new HashMap<Long, Integer>();
        long[] last = new long[judge.length];
        for (int i = 0; i < last.length; ++i) {
            last[i] = -1;
        }
        h.add(0L);
        base.add(1L);
        for (Submission submission = allIter.next(); submission != null; submission = allIter.next()) {
            long id = submission.getId();
            m.put(id, h.size());
            h.add(h.get(h.size() - 1) * 31 + id);
            base.add(base.get(base.size() - 1) * 31);
            int a = (int) (id / maxIdPerJudgeThread);
            long b = id % maxIdPerJudgeThread;
            if (last[a] < 0) {
                last[a] = b;
            } else {
                assertThat(b, is(last[a] + 1));
                last[a] = b;
            }
        }
        for (int i = 0; i < check.length; ++i) {
            if (len[i] > 0) {
                int s = m.get(start[i]);
                assertThat(hash[i], is(h.get(s + len[i] - 1) - h.get(s - 1) * base.get(len[i])));
            }
        }
    }
}
