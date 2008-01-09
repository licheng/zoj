package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.dao.DAOFactory;
import cn.edu.zju.acm.onlinejudge.dao.SubmissionDAO;

class JudgeServer {
    private class JudgeMonitor extends Thread {
	private int[] sleepIntervals = new int[] { 1, 1, 1, 1, 1, 5, 5, 5, 5, 10, 10, 10, 30, 30 };

	private List<JudgeDaemon> daemons = new ArrayList<JudgeDaemon>();

	public void run() {
	    Submission submission = new Submission();
	    submission.setId(Long.MAX_VALUE);
	    submission.setLanguage(LanguageManager.getLanguageByExtension("cc"));
	    submission
		    .setContent("#include<iostream>\nusing namespace std;int main(){int a,b;while(cin>>a>>b)cout<<a+b<<endl;return 0;}");
	    submission.setProblemId(0);
	    JudgeServerProxy proxy = new JudgeServerProxy(address, port);
	    try {
		synchronized (this) {
		    for (;;) {
			for (int i = 0;; i++) {
			    try {
				proxy.judge(submission);
				break;
			    } catch (Exception e) {
				e.printStackTrace();
			    }
			    if (i < sleepIntervals.length) {
				Thread.sleep(sleepIntervals[i] * 1000);
			    } else {
				Thread.sleep(60000);
			    }
			}
			status = JudgeServer.SERVER_NORMAL;
			synchronized (daemons) {
			    daemons.clear();
			    for (int i = 0; i < maxJobs; i++) {
				daemons.add(new JudgeDaemon(this, queue, address, port));
			    }
			    for (JudgeDaemon daemon : daemons) {
				daemon.start();
			    }
			}
			wait();
			synchronized (daemons) {
			    for (JudgeDaemon daemon : daemons) {
				daemon.interrupt();
			    }
			}
			status = JudgeServer.SERVER_ERROR;
		    }
		}
	    } catch (InterruptedException e) {
		for (JudgeDaemon daemon : daemons) {
		    daemon.interrupt();
		}
		status = JudgeServer.SERVER_STOP;
	    } finally {
		submissionDAO.closeSession();
	    }
	}

	public void update() {
	    synchronized (daemons) {
		while (daemons.size() < maxJobs) {
		    JudgeDaemon daemon = new JudgeDaemon(this, queue, address, port);
		    daemon.start();
		    daemons.add(daemon);
		}
		while (daemons.size() > maxJobs) {
		    daemons.remove(daemons.size() - 1).interrupt();
		}
	    }
	}
    }

    public static int SERVER_NORMAL = 0;

    public static int SERVER_ERROR = 1;

    public static int SERVER_STOP = 1;

    private SubmissionDAO submissionDAO = DAOFactory.getSubmissionDAO();

    private JudgeMonitor monitor;

    private InetAddress address;

    private int port;

    private JudgeQueue queue;

    int maxJobs;

    int status;

    public JudgeServer(JudgeQueue queue, InetAddress address, int port, int maxJobs) {
	this.queue = queue;
	this.address = address;
	this.port = port;
	this.maxJobs = maxJobs;
    }

    public synchronized void start() {
	if (monitor == null) {
	    monitor = new JudgeMonitor();
	    monitor.start();
	}
    }

    public synchronized void stop() {
	monitor.interrupt();
	monitor = null;
    }

    public InetAddress getAddress() {
	return address;
    }

    public int getMaxJobs() {
	return maxJobs;
    }

    public void setMaxJobs(int maxJobs) {
	if (maxJobs != this.maxJobs) {
	    this.maxJobs = maxJobs;
	    monitor.update();
	}
    }

    public int getPort() {
	return port;
    }

    public int getStatus() {
	return status;
    }
}