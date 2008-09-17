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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.judgeservice.submissionfilter.SubmissionFilter;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

public class JudgeService extends Thread {
    private static JudgeService instance;

    static {
        try {
            JudgeService.instance =
                    new JudgeService(Integer.parseInt(ConfigManager.getValue("queue_port")),
                                     ConfigManager.getValues("client_ip"),
                                     Integer.parseInt(ConfigManager.getValue("client_max_job")));
            JudgeService.instance.start();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ServerSocket serverSocket;

    private List<JudgeClient> judgeClientList = new ArrayList<JudgeClient>();

    private Logger logger = Logger.getLogger(JudgeService.class);

    private JudgingQueue judgingQueue = new JudgingQueue();

    private SubmissionQueue submissionQueue = new SubmissionQueue();

    private SubmissionFilter submissionFilter = null;

    private Set<String> clientHostAddressSet = new HashSet<String>();

    private int defaultNumberOfJudgeThreads;

    private Set<Long> queuingSubmissionIdSet = new HashSet<Long>();

    private Thread rejudgeThread;

    public static JudgeService getInstance() {
        return JudgeService.instance;
    }

    private JudgeService(int port, String[] clientHostAddressList, int defaultNumberOfJudgeThreads) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.logger.info("Listening on port " + port);
        if (clientHostAddressList != null) {
            for (String clientHostAddress : clientHostAddressList) {
                this.clientHostAddressSet.add(clientHostAddress);
            }
        }
        this.defaultNumberOfJudgeThreads = defaultNumberOfJudgeThreads;
        this.rejudgeThread = new Thread() {
            public void run() {
                SubmissionPersistence submissionPersistence =
                        PersistenceManager.getInstance().getSubmissionPersistence();
                try {
                    long last = 0;
                    for (int i = 0;; ++i) {
                        List<Submission> submissions = submissionPersistence.getQueueingSubmissions(last - 1, 100);
                        if (submissions.size() == 0) {
                            break;
                        }
                        synchronized (JudgeService.this.queuingSubmissionIdSet) {
                            for (int j = submissions.size() - 1; j >= 0; --j) {
                                Submission submission = submissions.get(j);
                                JudgeService.this.queuingSubmissionIdSet.add(submission.getId());
                                if (i == 0) {
                                    JudgeService.instance.judge(submission, Priority.NORMAL);
                                } else {
                                    JudgeService.instance.judge(submission, Priority.LOW);
                                }
                            }
                            JudgeService.this.queuingSubmissionIdSet.wait();
                        }
                        last = submissions.get(0).getId();
                    }
                } catch (PersistenceException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                }
                JudgeService.this.rejudgeThread = null;
                JudgeService.this.queuingSubmissionIdSet = null;
            }
        };
        rejudgeThread.start();
    }

    @Override
    public void run() {
        while (!this.serverSocket.isClosed()) {
            try {
                Socket socket = this.serverSocket.accept();
                this.logger
                           .info("Connection from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                if (!socket.getInetAddress().isLoopbackAddress() &&
                    !this.clientHostAddressSet.contains(socket.getInetAddress().getHostAddress())) {
                    this.logger.info("Refused");
                    socket.close();
                    continue;
                }
                JudgeClient client = new JudgeClient(this, socket, this.defaultNumberOfJudgeThreads);
                client.start();
                synchronized (this.judgeClientList) {
                    this.judgeClientList.add(client);
                }
            } catch (IOException e) {
                this.logger.error(e);
            }
        }

    }

    public List<JudgeClient> getJudgeClientList() {
        synchronized (this.judgeClientList) {
            return new ArrayList<JudgeClient>(this.judgeClientList);
        }
    }

    public JudgingQueueIterator getJudgingQueueIterator() {
        return this.judgingQueue.iterator();
    }

    public SubmissionFilter getSubmissionFilter() {
        return this.submissionFilter;
    }

    public void judge(Submission submission, int priority) {
        submission.setJudgeReply(JudgeReply.QUEUING);
        this.submissionQueue.push(submission, priority);
    }

    void judgeStart(Submission submission) {
        this.judgingQueue.push(submission);
    }

    void judgeDone(Submission submission) {
        this.judgingQueue.remove(submission);
        if (this.queuingSubmissionIdSet != null) {
            try {
                synchronized (this.queuingSubmissionIdSet) {
                    this.queuingSubmissionIdSet.remove(submission.getId());
                    if (this.queuingSubmissionIdSet.size() == 0) {
                        this.queuingSubmissionIdSet.notify();
                    }
                }
            } catch (NullPointerException e) {
                // In case queuingSubmissionIdSet is null
            }
        }
    }

    public SubmissionQueue getSubmissionQueue() {
        return this.submissionQueue;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        synchronized (this.judgeClientList) {
            for (JudgeClient client : this.judgeClientList) {
                client.interrupt();
            }
        }
        try {
            this.rejudgeThread.interrupt();
        } catch (NullPointerException e) {
            // In case rejudgeThread is null
        }
    }
}
