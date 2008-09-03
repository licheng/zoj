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
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;

public class JudgeService {
    private ServerSocket serverSocket;

    private static JudgeService instance;

    static {
        try {
            instance = new JudgeService(Integer.parseInt(ConfigManager.getValue("queue_port")), ConfigManager
                    .getValues("client_ip"), Integer.parseInt(ConfigManager.getValue("client_max_job")));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private List<JudgeClient> clients = new ArrayList<JudgeClient>();

    private Logger logger;

    private JudgingQueue judgingQueue = new JudgingQueue();

    private CandidateQueue candidateQueue = new CandidateQueue();

    private SubmissionFilter submissionFilter = new SubmissionFilter();

    public JudgeService(int port, final String[] clientHostNameList, final int defaultClientJobs) throws IOException {
        this.logger = Logger.getLogger(JudgeService.class);
        this.serverSocket = new ServerSocket(port);
        this.logger.info("Listening on port " + port);
        new Thread() {
            public void run() {
                Logger logger = Logger.getLogger(JudgeService.class.getName() + ".Thread");
                Set<String> clientHostNameSet = new HashSet<String>();
                for (String clientHostName : clientHostNameList) {
                    clientHostNameSet.add(clientHostName);
                }
                while (!serverSocket.isClosed()) {
                    try {
                        Socket socket = JudgeService.this.serverSocket.accept();
                        logger.info("Connection from " + socket.getInetAddress().getHostAddress() + ":" +
                                socket.getPort());
                        if (!socket.getInetAddress().isLoopbackAddress() &&
                                !clientHostNameSet.contains(socket.getInetAddress().getHostAddress())) {
                            logger.info("Refused");
                            socket.close();
                            continue;
                        }
                        JudgeClient client = new JudgeClient(JudgeService.this, socket, defaultClientJobs);
                        client.start();
                        synchronized (JudgeService.this.clients) {
                            JudgeService.this.clients.add(client);
                        }
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }


            }
        }.start();


    }





    public static JudgeService getInstance() {
        return JudgeService.instance;
    }

    public List<JudgeClient> getClients() {
        synchronized (this.clients) {
            return new ArrayList<JudgeClient>(this.clients);
        }
    }

    public JudgingList getJudgingList() {
        return this.judgingQueue.clone();
    }

    public JudgeQueue createJudgeQueue(SubmissionFilter serviceFilter, SubmissionFilter clientFilter,
            SubmissionFilter threadFilter) {
        return new JudgeQueueImpl(this.candidateQueue, serviceFilter, clientFilter, threadFilter);






    }

    public SubmissionFilter getSubmissionFilter() {
        return this.submissionFilter;
    }

    public void setSubmissionFilter(SubmissionFilter submissionFilter) {
        this.submissionFilter = submissionFilter;
    }

    public void judge(Submission submission, int priority) {
        submission.setJudgeReply(JudgeReply.QUEUING);
        this.candidateQueue.add(submission, priority);
    }

    void judgeStart(Submission submission) {
        this.judgingQueue.push(submission);
    }

    void judgeDone(Submission submission) {
        this.judgingQueue.tryPop();
    }
}
