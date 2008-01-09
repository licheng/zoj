package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.io.IOException;
import java.net.InetAddress;

import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.ProblemDAO;
import cn.edu.zju.acm.onlinejudge.dao.SubmissionDAO;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public class JudgeDaemon extends Thread {

    private Object monitor;

    private JudgeQueue queue;

    private InetAddress address;

    private int port;

    private SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();

    private ProblemDAO problemDAO = DAOFactory.getProblemDAO();

    public JudgeDaemon(Object monitor, JudgeQueue queue, InetAddress address, int port) {
	this.monitor = monitor;
	this.queue = queue;
	this.address = address;
	this.port = port;
    }

    public void run() {
	JudgeServerProxy proxy = new JudgeServerProxy(address, port);
	try {
	    while (!this.isInterrupted()) {
		Submission submission;
		try {
		    submission = queue.poll();
		} catch (InterruptedException e) {
		    throw e;
		} catch (Exception e) {
		    continue;
		}
		try {
		    try {
			try {
			    proxy.judge(submission);
			} catch (NoSuchProblemException e) {
			    Problem problem = problemDAO.getProblem(submission.getProblemId());
			    try {
				proxy.sendProblem(problem);
				proxy.judge(submission);
			    } catch (IOException e1) {
				submission.setJudgeReply(JudgeReply.JUDGE_INTERNAL_ERROR);
				
			    } catch (NoSuchProblemException e1) {
				submission.setJudgeReply(JudgeReply.JUDGE_INTERNAL_ERROR);
			    }
			}
		    } catch (PersistenceException e) {
			submission.setJudgeReply(JudgeReply.JUDGE_INTERNAL_ERROR);
		    }
		    submissionDAO.beginTransaction();
		    submissionDAO.update(submission);
		    submissionDAO.commitTransaction();
		    submission.setContent(null);
		} catch (JudgeServerErrorException e) {
		    synchronized (monitor) {
			if (!this.isInterrupted()) {
			    monitor.notify();
			}
		    }
		    queue.rejudge(submission);
		    break;
		} catch (PersistenceException e) {
		    submission.setJudgeReply(JudgeReply.JUDGE_INTERNAL_ERROR);
		}
	    }
	} catch (InterruptedException e) {
	} finally {
	    submissionDAO.closeSession();
	}
    }
}