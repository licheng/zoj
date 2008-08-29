package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class JudgeClient extends Thread {
    public static final int CONNECTION_TIMEOUT = 30000;

    public static final int READ_TIMEOUT = 60000;

    private JudgeQueue judgeQueue;

    private String host;

    private int port;

    private int maxJobs;

    private List<JudgeClientInstance> instances = new ArrayList<JudgeClientInstance>();

    private Logger logger;

    public JudgeClient(String host, int port, JudgeQueue judgeQueue, int maxJobs) {
        this.host = host;
        this.port = port;
        this.judgeQueue = judgeQueue;
        this.maxJobs = maxJobs;
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public void run() {
        while (!this.isInterrupted()) {
            synchronized (this) {
                this.logger.info("Set jobs to " + this.maxJobs);
                while (this.instances.size() < maxJobs) {
                    JudgeClientInstance instance = new JudgeClientInstance(this, this.host, this.port);
                    instance.start();
                    this.instances.add(instance);
                }
                while (this.instances.size() > maxJobs) {
                    this.instances.remove(this.instances.size() - 1).interrupt();
                }
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        for (JudgeClientInstance instance : this.instances) {
            instance.interrupt();
        }
    }

    public int getMaxJobs() {
        return this.maxJobs;
    }

    public void setMaxJobs(int maxJobs) {
        if (this.maxJobs != maxJobs) {
            this.maxJobs = maxJobs;
            synchronized (this) {
                this.notify();
            }
        }
    }

    public synchronized List<JudgeClientInstance> getInstances() {
        return new ArrayList<JudgeClientInstance>(this.instances);
    }

    public synchronized void removeInstance(JudgeClientInstance instance) {
        if (this.instances.remove(instance)) {
            this.notify();
        }
    }

    public JudgeQueue getJudgeQueue() {
        return this.judgeQueue;
    }
}
