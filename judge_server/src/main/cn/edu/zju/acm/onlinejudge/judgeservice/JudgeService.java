package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;

public class JudgeService {
    private JudgeQueue judgeQueue = new JudgeQueue();

    private static JudgeService instance;

    static {
        try {
            instance = new JudgeService(Integer.parseInt(ConfigManager.getValue("queue_port")), ConfigManager
                    .getValues("client_ip"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private List<JudgeClient> clients = new ArrayList<JudgeClient>();

    private Logger logger;

    public JudgeService(int port, final String[] clientAddressList) {
        logger = Logger.getLogger(JudgeService.class.getName());
        for (String clientAddress : clientAddressList) {
            String[] info = clientAddress.split(":");
            JudgeClient client;
            try {
                client = new JudgeClient(info[0], Integer.parseInt(info[1]), this.judgeQueue, getClientNumber());
            } catch (NumberFormatException e) {
                logger.error("Invalid address " + clientAddress);
                continue;
            }
            synchronized (clients) {
                clients.add(client);
            }
            client.start();
        }
    }

    private int getClientNumber() {
        try {
            return Integer.parseInt(ConfigManager.getValue("client_max_job"));
        } catch (Exception e) {
            return 1;
        }
    }

    public void judge(Submission submission) throws Exception {
        judgeQueue.push(submission);
    }

    public static JudgeService getInstance() {
        return instance;
    }

    public List<JudgeClient> getClients() {
        synchronized (this.clients) {
            for (int i = clients.size() - 1; i >= 0; --i) {
                if (!clients.get(i).isAlive()) {
                    clients.remove(i);
                }
            }
            return new ArrayList<JudgeClient>(clients);
        }
    }

    public JudgingList getJudgingList() {
        return judgeQueue.getJudgingList();
    }

    public void terminate() {
        for (JudgeClient client : this.clients) {
            client.interrupt();
        }
    }
}
