package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;

public class JudgeService {
    private CachedJudgeQueue queue;

    private ServerSocket serverSocket;

    private static JudgeService instance;
    
    static { try { instance = new JudgeService(128,
     Integer.parseInt(ConfigManager.getValue("queue_port"))); } catch
     (Exception e) { throw new ExceptionInInitializerError(e); } }
    

    private List<JudgeClient> clients = new ArrayList<JudgeClient>();

    private Logger logger;

    public JudgeService(int cacheCapacity, int port) throws IOException {
        logger = Logger.getLogger(JudgeService.class.getName());
        if (cacheCapacity < 128) {
            cacheCapacity = 128;
        }
        queue = new CachedJudgeQueue(cacheCapacity);
        this.serverSocket = new ServerSocket(port);
        logger.info("Listening on port " + port);
        new Thread() {
            public void run() {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket socket = serverSocket.accept();
                        logger.info("Connection from " + socket.getInetAddress().getCanonicalHostName() + ":"
                                + socket.getPort());
                        JudgeClient client = new JudgeClient(queue, socket, getClientNumber());
                        synchronized (clients) {
                            for (int i = clients.size() - 1; i >= 0; --i) {
                                if (!clients.get(i).isAlive()) {
                                    clients.remove(i);
                                }
                            }
                            client.start();
                            clients.add(client);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    private int getClientNumber() {
    	try {
    		return Integer.parseInt(ConfigManager.getValue("client_max_job"));
    	} catch (Exception e) {
    		return 1;
    	}
    }

    public boolean judge(Submission submission) throws Exception {
        return queue.push(submission);
    }

    public void rejudge(Submission submission) throws Exception {
        queue.pushIgnoreCache(submission);
    }

    public Submission getSubmission(long id) {
        return queue.getSubmissionInCache(id);
    }

    public static JudgeService getInstance() {
        return instance;
    }

    public List<JudgeClient> getClients() {
        synchronized (clients) {
            for (int i = clients.size() - 1; i >= 0; --i) {
                if (!clients.get(i).isAlive()) {
                    clients.remove(i);
                }
            }
            return new ArrayList<JudgeClient>(clients);
        }
    }

    public void terminate() {
        try {
            this.serverSocket.close();
            for (JudgeClient client : this.clients) {
                client.terminate();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
