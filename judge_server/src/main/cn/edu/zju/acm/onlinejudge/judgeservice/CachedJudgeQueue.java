package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

public class CachedJudgeQueue extends JudgeQueue {
    private LinkedList<Submission> cache = new LinkedList<Submission>();
    
    private Map<Long, Submission> cacheIndex = new HashMap<Long, Submission>();
    
    private int cacheSize;
    
    public CachedJudgeQueue(int cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    @Override
    public synchronized Submission poll() throws Exception {
        Submission submission = super.poll();
        if (cache.size() == this.cacheSize) {
            cacheIndex.remove(cache.poll().getId());
        }
        cache.offer(submission);
        cacheIndex.put(submission.getId(), submission);
        return submission;
    }

    @Override
    public synchronized boolean push(Submission submission) {
        if (cacheIndex.get(submission.getId()) == null) {
            return super.push(submission);
        }
        return false;
    }
    
    public synchronized boolean pushIgnoreCache(Submission submission) {
        return super.push(submission);
    }
    
    public Submission getSubmissionInCache(long id) {
        return cacheIndex.get(id);
    }
}
