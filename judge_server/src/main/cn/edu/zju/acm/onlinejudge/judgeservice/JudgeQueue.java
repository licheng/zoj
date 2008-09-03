package cn.edu.zju.acm.onlinejudge.judgeservice;




import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public interface JudgeQueue {
    public Submission poll() throws InterruptedException, PersistenceException;





















































}
