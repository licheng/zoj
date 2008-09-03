/**
 * 
 */
package cn.edu.zju.acm.onlinejudge.judgeservice;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

interface Candidate {
    public Submission getSubmission() throws PersistenceException;

    public int getPriority();

    public boolean isClaimed();

    public boolean tryClaim();

    public Candidate getNext();
}