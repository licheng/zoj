package cn.edu.zju.acm.onlinejudge.judgeserver;

import org.junit.Before;
import org.junit.Test;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

import static org.junit.Assert.*;

public class JudgeCacheUnitTest {
    private JudgeCache cache;

    private Submission[] submissions = new Submission[100];

    @Before
    public void setUp() {
	cache = new JudgeCache(10);
	for (int i = 0; i < submissions.length; i++) {
	    submissions[i] = new Submission();
	    submissions[i].setId(i);
	}
    }

    @Test
    public void testConstructor1() {
	cache = new JudgeCache(10);
	assertEquals(16, cache.getCapacity());
	cache = new JudgeCache(16);
	assertEquals(16, cache.getCapacity());
    }

    @Test
    public void testConstructor2() {
	for (int i = 0; i < 10; i++) {
	    cache.add(submissions[i]);
	}
	cache = new JudgeCache(100, cache);
	assertEquals(128, cache.getCapacity());
	for (int i = 0; i < 10; i++) {
	    assertNotNull(cache.get(i));
	}
    }

    @Test
    public void testAddAndGet() {
	for (int i = 0; i < cache.getCapacity(); i++) {
	    cache.add(submissions[i]);
	    assertNotNull(cache.get(i));
	}
	for (int i = cache.getCapacity(); i < submissions.length; i++) {
	    cache.add(submissions[i]);
	    for (int j = 0; j <= i - cache.getCapacity(); j++) {
		assertNull("" + j, cache.get(j));
	    }
	    for (int j = i - cache.getCapacity() + 1; j <= i; j++) {
		assertNotNull(cache.get(j));
	    }
	}
    }

    @Test
    public void testMultiThread() throws Exception {
	final Thread[] threads = new Thread[100];
	final int range = 50;
	cache = new JudgeCache(range);
	final boolean[] errors = new boolean[threads.length];
	for (int i = 0; i < threads.length; i++) {
	    final int k = i;
	    threads[i] = new Thread() {
		public void run() {
		    errors[k] = false;
		    try {
			while (!isInterrupted()) {
			    int border = 0;
			    Submission[] submissions = new Submission[range];
			    submissions[range - 1] = cache.get(k * range + range - 1);
			    for (int i = range - 2; i >= 0; i--) {
				submissions[i] = cache.get(k * range + i);
				if (submissions[i] == null) {
				    if (submissions[i + 1] != null) {
					if (i + range < border) {
					    System.out.println(k * range + i + " " + border + " " + (i + range));
					    errors[k] = true;
					}
					border = i + range;
				    }
				} else {
				    if (submissions[i + 1] == null) {
					if (i < border) {
					    errors[k] = true;
					    System.out.println(k * range + i + " " + border + " " + i);
					}
					border = i;
				    }
				}
			    }
			    Thread.yield();
			}
		    } catch (Exception e) {
		    }
		}
	    };
	    threads[i].start();
	}
	for (int i = 0; i < threads.length * range; i++) {
	    Submission submission = new Submission();
	    submission.setId(i);
	    cache.add(submission);
	    Thread.yield();
	}
	for (Thread thread : threads) {
	    thread.interrupt();
	}
	for (Thread thread : threads) {
	    thread.join();
	}
	for (boolean error : errors) {
	    assertFalse(error);
	}
    }
}
