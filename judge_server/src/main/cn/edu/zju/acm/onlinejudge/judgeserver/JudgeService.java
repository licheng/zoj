package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;

public class JudgeService {
    private Map<String, JudgeServer> serverMap = new HashMap<String, JudgeServer>();

    private JudgeQueue queue;

    private int maxSubmissionsInCache = 0;

    private int cacheCapacity;

    private JudgeCache cache;

    private static JudgeService instance;
    static {
	instance = new JudgeService(128);
	try {
	    for (String value : ConfigManager.getValues("judge_server")) {
		String[] s = value.split(":");
		instance.addJudgeServer(InetAddress.getByName(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
	    }
	} catch (Exception e) {
	    throw new ExceptionInInitializerError(e);
	}
    }

    public JudgeService(int cacheCapacity) {
	queue = new JudgeQueue() {

	    @Override
	    public synchronized Submission poll() throws Exception {
		Submission submission = super.poll();
		cache.add(submission);
		return submission;
	    }

	    @Override
	    public synchronized boolean push(Submission submission) {
		if (cache.get(submission.getId()) == null) {
		    return super.push(submission);
		}
		return false;
	    }
	};
	if (cacheCapacity < 128) {
	    cacheCapacity = 128;
	}
	this.cacheCapacity = cacheCapacity;
	cache = new JudgeCache(cacheCapacity);
	maxSubmissionsInCache = 32;
    }

    public void addJudgeServer(InetAddress address, int port, int maxJobs) throws InterruptedException {
	JudgeServer server = new JudgeServer(queue, address, port, maxJobs);
	synchronized (serverMap) {
	    serverMap.put(address + ":" + port, server);
	    maxSubmissionsInCache += maxJobs;
	    if (cacheCapacity < maxSubmissionsInCache) {
		synchronized (queue) {
		    cache = new JudgeCache(cacheCapacity * 2, cache);
		}
	    }
	    server.start();
	}

    }

    public void removeJudgeServer(InetAddress address, int port) throws InterruptedException {
	synchronized (serverMap) {
	    JudgeServer server = serverMap.remove(address + ":" + port);
	    if (server != null) {
		maxSubmissionsInCache -= server.getMaxJobs();
		server.stop();
	    }
	}
    }

    public boolean judge(Submission submission) throws Exception {
	return queue.push(submission);
    }

    public Submission getSubmission(long id) {
	return cache.get(id);
    }

    public static JudgeService getInstance() {
	return instance;
    }
}
