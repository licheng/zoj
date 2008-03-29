package cn.edu.zju.acm.onlinejudge.judgeserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class JudgeClient extends Thread {
    public static final int CONNECTION_TIMEOUT = 30000;

    public static final int READ_TIMEOUT = 60000;

    private JudgeQueue queue;

    private Socket socket;

    private int port;

    private int maxJobs;

    private List<JudgeClientInstance> activeInstances = new ArrayList<JudgeClientInstance>();

    private List<JudgeClientInstance> inactiveInstances = new ArrayList<JudgeClientInstance>();

    private Logger logger;

    public JudgeClient(JudgeQueue queue, Socket socket, int maxJobs) {
        this.queue = queue;
        this.socket = socket;
        this.maxJobs = maxJobs;
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            for (;;) {
                this.port = in.readInt();
                logger.info("Judge client port is " + this.port);
                try {
                    this.adjustInstances();
                } catch (IOException e) {
                    if (activeInstances.size() + inactiveInstances.size() == 0) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (JudgeClientInstance instance : activeInstances) {
            instance.terminate();
        }
        for (JudgeClientInstance instance : inactiveInstances) {
            instance.terminate();
        }
        try {
            if (!this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMaxJobs() {
        return this.maxJobs;
    }

    public void setMaxJobs(int maxJobs) {
        this.maxJobs = maxJobs;
        try {
            this.adjustInstances();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<JudgeClientInstance> getInstances() {
        return new ArrayList<JudgeClientInstance>(this.activeInstances);
    }

    private synchronized void adjustInstances() throws IOException {
        for (int i = activeInstances.size() - 1; i >= 0; --i) {
            if (!activeInstances.get(i).isAlive()) {
                activeInstances.remove(i);
            }
        }
        for (int i = inactiveInstances.size() - 1; i >= 0; --i) {
            if (!inactiveInstances.get(i).isAlive()) {
                activeInstances.remove(i);
            }
        }
        while (activeInstances.size() < maxJobs) {
            if (inactiveInstances.size() > 0) {
                JudgeClientInstance instance = inactiveInstances.remove(inactiveInstances.size() - 1);
                instance.wakeup();
                activeInstances.add(instance);
            } else {
                JudgeClientInstance instance = new JudgeClientInstance(this.queue, new InetSocketAddress(this.socket
                        .getInetAddress(), this.port));
                instance.start();
                activeInstances.add(instance);
            }
        }
        while (activeInstances.size() > maxJobs) {
            JudgeClientInstance instance = activeInstances.remove(activeInstances.size() - 1);
            instance.rest();
            inactiveInstances.add(instance);
        }
    }

    public void terminate() {
        try {
            if (!this.socket.isClosed()) {
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (JudgeClientInstance instance : this.activeInstances) {
            instance.terminate();
        }
        for (JudgeClientInstance instance : this.inactiveInstances) {
            instance.terminate();
        }
    }
}
