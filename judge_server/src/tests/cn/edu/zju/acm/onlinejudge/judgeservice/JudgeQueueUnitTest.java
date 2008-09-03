package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.SubmissionDAO;
import cn.xuchuan.util.ReflectionUtil;

import static org.junit.Assert.*;

public class JudgeQueueUnitTest {
    private JudgeQueue queue;

    private Submission[] submissions = new Submission[10];

    private static SubmissionDAO submissionDAO = new MockSubmissionDAO();

    @BeforeClass
    public static void init() throws Exception {
        ReflectionUtil.setFieldValue(DAOFactory.class, "submissionDAO", new MockSubmissionDAO());
    }

    @Before
    public void setUp() {
        queue = new JudgeQueue();
        for (int i = 0; i < submissions.length; i++) {
            submissions[i] = new Submission();
            submissions[i].setId(i);
        }
    }

    @Test(timeout = 1000)
    public void testPollBlock() throws Exception {
        Thread t = new Thread() {
            public void run() {
                setName("fail");
                try {
                    queue.removeFirst();
                } catch (InterruptedException e) {
                    setName("pass");
                    return;
                } catch (Exception e) {
                }
            }
        };
        t.start();
        Thread.sleep(100);
        assertTrue(t.isAlive());
        t.interrupt();
        t.join();
        assertEquals("pass", t.getName());
    }

    @Test(timeout = 1000)
    public void testPushAndPoll() throws Exception {
        queue.add(submissions[0]);
        queue.add(submissions[1]);
        queue.add(submissions[2]);
        assertEquals((long) 0, queue.removeFirst().getId());
        assertEquals((long) 1, queue.removeFirst().getId());
        assertEquals((long) 2, queue.removeFirst().getId());
    }

    @Test(timeout = 1000)
    public void testRejudge() throws Exception {
        queue.add(submissions[0]);
        queue.add(submissions[1]);
        queue.add(submissions[2]);
        Submission submission = queue.removeFirst();
        queue.addFirst(queue.removeFirst());
        queue.addFirst(submission);
        assertEquals((long) 1, queue.removeFirst().getId());
        assertEquals((long) 0, queue.removeFirst().getId());
        assertEquals((long) 2, queue.removeFirst().getId());
    }

    @Test(timeout = 1000)
    public void testMix() throws Exception {
        queue.add(submissions[0]);
        queue.add(submissions[1]);
        assertEquals((long) 0, queue.removeFirst().getId());
        assertEquals((long) 1, queue.removeFirst().getId());
        queue.addFirst(submissions[1]);
        queue.add(submissions[2]);
        queue.add(submissions[3]);
        queue.add(submissions[4]);
        queue.add(submissions[5]);
        assertEquals((long) 1, queue.removeFirst().getId());
        assertEquals((long) 2, queue.removeFirst().getId());
        assertEquals((long) 3, queue.removeFirst().getId());
        queue.addFirst(submissions[2]);
        queue.addFirst(submissions[1]);
        assertEquals((long) 2, queue.removeFirst().getId());
        assertEquals((long) 1, queue.removeFirst().getId());
        assertEquals((long) 4, queue.removeFirst().getId());
        assertEquals((long) 5, queue.removeFirst().getId());
    }

    @Test
    public void testRestoreFromDAO() throws Exception {
        for (int i = 0; i < 1000; i++) {
            Submission submission = new Submission();
            submission.setContent("" + i);
            submissionDAO.save(submission);
            char[] content = new char[1024 * 128];
            Arrays.fill(content, (char) i);
            submission.setContent(new String(content));
            queue.add(submission);
        }
        for (int i = 0; i < 1000; i++) {
            Submission submission = queue.removeFirst();
            assertEquals((long) i, submission.getId());
        }
    }

    @Test(timeout = 5000)
    public void testMultiThread() throws Exception {
        final Thread[] consumer = new Thread[100];
        final Thread[] provider = new Thread[1000];
        final int submissionPerProvider = 100;
        final int total = provider.length * submissionPerProvider;
        final int[][] count = new int[consumer.length][total];
        submissions = new Submission[total];
        for (int i = 0; i < total; i++) {
            submissions[i] = new Submission();
            submissions[i].setId(i);
            submissions[i].setJudgeReply(JudgeReply.QUEUING);
        }
        for (int i = 0; i < consumer.length; i++) {
            final int k = i;
            final int[] cnt = count[k];
            for (int j = 0; j < cnt.length; j++) {
                cnt[j] = 0;
            }
            consumer[i] = new Thread() {
                public void run() {
                    try {
                        for (int i = 0;; i++) {
                            Submission submission = queue.removeFirst();
                            int id = (int) submission.getId();
                            // System.out.println("poll " + k + " " + id);
                            cnt[id]++;
                            assertEquals(submissions[id], submission);
                            if (id % consumer.length == k) {
                                queue.addFirst(submission);
                            } else {
                                submission.setJudgeReply(JudgeReply.ACCEPTED);
                            }
                            Thread.yield();
                        }
                    } catch (InterruptedException e) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        final int[] idGenerator = new int[] { 0 };
        for (int i = 0; i < provider.length; i++) {
            provider[i] = new Thread() {
                public void run() {
                    for (int i = 0; i < submissionPerProvider; i++) {
                        synchronized (JudgeQueueUnitTest.this) {
                            int id = idGenerator[0]++;
                            submissions[id].setJudgeReply(JudgeReply.QUEUING);
                            queue.add(submissions[id]);
                        }
                        Thread.yield();
                    }
                }
            };
        }
        for (int i = 0; i < consumer.length; i++) {
            consumer[i].start();
        }
        for (int i = 0; i < provider.length; i++) {
            provider[i].start();
        }
        for (int i = 0; i < provider.length; i++) {
            provider[i].join();
        }
        for (int i = 0; i < submissions.length; i++) {
            while (submissions[i].getJudgeReply() != JudgeReply.ACCEPTED) {
                // System.out.println("Wait " + i);
                Thread.sleep(100);
            }
        }
        for (int i = 0; i < consumer.length; i++) {
            consumer[i].interrupt();
            consumer[i].join();
        }
        for (int i = 0; i < total; i++) {
            int sum = 0;
            int max = 0;
            for (int j = 0; j < consumer.length; j++) {
                sum += count[j][i];
                if (count[j][i] > max) {
                    max = count[j][i];
                }
            }
            if (sum == 0) {
                fail("" + i);
            }
            if (sum > 1 && max + 1 != sum) {
                fail("" + i + " " + max + " " + sum);
            }
        }
    }
}