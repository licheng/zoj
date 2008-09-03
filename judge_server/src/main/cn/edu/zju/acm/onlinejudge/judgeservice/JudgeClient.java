package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.util.Utility;

public class JudgeClient extends Thread {
    public static final int CONNECTION_TIMEOUT = 30000;

    public static final int READ_TIMEOUT = 60000;

    public static final int HEART_BEAT_INTERVAL = 30000;

    private String host;

    private List<Language> supportedLanguages = new ArrayList<Language>();

    private int port;

    private List<JudgeClientInstance> judgeThreads = new ArrayList<JudgeClientInstance>();

    private JudgeClientErrorHandlingStrategy errorHandlingStrategy = new JudgeClientErrorHandlingStrategy(this);

    private Logger logger = Logger.getLogger(JudgeClient.class);



    private int defaultNumberOfJudgeThreads;

    private DataInputStream in;

    private DataOutputStream out;

    private Socket socket;

    private boolean initialized;

    private JudgeService service;

    private SubmissionFilter submissionFilter = null;

    private int[] pingCounter = new int[] { 0 };

    private Object pingBarrier = new Object();

    public JudgeClient(JudgeService service, Socket socket, int defaultNumberOfJudgeThreads) throws IOException {
        this.service = service;
        this.host = socket.getInetAddress().getHostAddress();
        this.socket = socket;
        socket.setKeepAlive(true);
        socket.setSoTimeout(JudgeClient.READ_TIMEOUT);
        this.defaultNumberOfJudgeThreads = defaultNumberOfJudgeThreads;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.initialized = false;
    }

    public List<JudgeClientInstance> getJudgeThreads() {
        synchronized (this.judgeThreads) {
            return new ArrayList<JudgeClientInstance>(this.judgeThreads);
        }
    }

    public void run() {
        try {
            this.logger.info("Start to get information");
            try {
                this.sendInfoCommand();
                this.port = this.in.readInt();
                this.logger.info("port=" + this.port);
                this.supportedLanguages.clear();
                int n = this.in.readInt();
                if (n < 0 || n > LanguageManager.getNumberOfLanguages()) {
                    logger.error("Invalid number of supported languages:" + n);
                    return;
                }
                for (int i = 0; i < n; ++i) {
                    int id = this.in.readInt();
                    Language language = LanguageManager.getLanguage(id);
                    if (language == null) {
                        logger.error("Invalid language id:" + id);
                        return;
                    }
                    this.supportedLanguages.add(language);
                    this.logger.info("Supported language:" + language.getName());
                }
                this.initialized = true;
                for (int i = 0; i < this.defaultNumberOfJudgeThreads; ++i) {
                    this.addJudgeThread();
                }
            } catch (IOException e) {
                this.logger.error("Fail to get information", e);
                return;
            }
            for (;;) {
                synchronized (this.pingCounter) {
                    if (this.pingCounter[0] == 0) {
                        this.pingCounter.wait(JudgeClient.HEART_BEAT_INTERVAL);
                    }
                }
                try {
                    if (this.sendPingCommand() != JudgeReply.READY.getId()) {
                        break;
                    }
                    synchronized (this.pingBarrier) {
                        this.pingCounter[0] = 0;
                        this.pingBarrier.notifyAll();
                    }
                } catch (IOException e) {
                    this.logger.error("Send ping command failure", e);
                    break;
                }
            }
        } catch (InterruptedException e) {
            this.logger.info("Interrupted");
        } finally {
            synchronized (this.pingBarrier) {
                this.pingCounter[0] = 0;
                Utility.closeSocket(this.socket);
                this.pingBarrier.notifyAll();
            }
            for (JudgeClientInstance judgeThread : this.judgeThreads) {
                judgeThread.interrupt();
            }
        }
    }

    public void interrupt() {
        super.interrupt();
        synchronized (this.judgeThreads) {
            for (JudgeClientInstance judgeThread : this.judgeThreads) {
                judgeThread.interrupt();
            }
            this.judgeThreads.clear();
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public List<Language> getSupportedLanguages() {
        return this.supportedLanguages;
    }

    public SubmissionFilter getSubmissionFilter() {
        return this.submissionFilter;
    }

    public void setSubmissionFilter(SubmissionFilter submissionFilter) {       
        this.submissionFilter = submissionFilter;
    }

    public JudgeClientErrorHandlingStrategy getErrorHandlingStrategy() {
        return this.errorHandlingStrategy;
    }

    public JudgeClientInstance addJudgeThread() {
        synchronized (this.judgeThreads) {
            JudgeClientInstance judgeThread = new JudgeClientInstance(this.service, this);
            judgeThread.start();
            this.judgeThreads.add(judgeThread);
            return judgeThread;
        }
    }

    public void removeJudgeThread(int index) {
        synchronized (this.judgeThreads) {
            this.judgeThreads.remove(index).interrupt();
        }
    }

    public void removeDeadThreads() {
        synchronized (this.judgeThreads) {
            for (int i = this.judgeThreads.size() - 1; i >= 0; --i) {
                if (!this.judgeThreads.get(i).isAlive()) {
                    this.judgeThreads.remove(i);
                }
            }
        }
    }

    boolean ping() throws InterruptedException {
        synchronized (this.pingBarrier) {
            if (this.socket.isClosed()) {
                return false;
            } else {
                synchronized (this.pingCounter) {
                    ++this.pingCounter[0];
                    this.pingCounter.notify();
                }
                this.pingBarrier.wait();
                return !this.socket.isClosed();
            }
        }

    }

    private void sendInfoCommand() throws IOException {
        this.out.write(JudgeClientCommandsFactory.createInfoCommand());
    }

    private int sendPingCommand() throws IOException {
        this.out.write(JudgeClientCommandsFactory.createPingCommand());
        return this.in.readInt();
    }
}
