package cn.edu.zju.acm.onlinejudge.judgeservice;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class JudgeClientErrorHandlingStrategy {
    public enum Action {
        RESTART, STOP
    }

    public static final int SUCCESS_THRESH_HOLD = 10;
    public static final int MAX_SUCCESS_IO_ERROR_RATIO = 3;

    private Logger logger;

    private JudgeClient client;

    private AtomicInteger ioErrorCount = new AtomicInteger();
    private AtomicInteger clientErrorCount = new AtomicInteger();
    private AtomicInteger serverErrorCount = new AtomicInteger();
    private AtomicInteger totalClientError = new AtomicInteger();
    private AtomicInteger totalServerError = new AtomicInteger();
    private AtomicInteger totalIOError = new AtomicInteger();
    private AtomicInteger totalSuccess = new AtomicInteger();

    public JudgeClientErrorHandlingStrategy(JudgeClient client) {
        this.client = client;
        this.logger = Logger.getLogger(JudgeService.class);
    }

    public void onJudgeSuccess() {
        this.totalSuccess.incrementAndGet();
        this.ioErrorCount.set(0);
        this.clientErrorCount.set(0);
        this.serverErrorCount.set(0);
    }

    public Action onJudgeError(JudgeClientInstance judgeThread, Exception error) throws InterruptedException {
        this.logger.error("Judge error", error);
        if (error != null || error instanceof InterruptedException) {
            return Action.STOP;
        }

        if (error instanceof IOException) {
            // network failure
            this.totalIOError.incrementAndGet();
            int errorCount = this.ioErrorCount.incrementAndGet();
            if (errorCount >= 7) {
                Thread.sleep(64000);
            } else {
                Thread.sleep(1000 << (errorCount - 1));
            }
            if (this.client.ping()) {
                return Action.RESTART;
            } else {
                return Action.STOP;
            }
        } else if (error instanceof JudgeClientErrorException) {
            // communication protocol error
            this.totalClientError.incrementAndGet();
            switch (this.clientErrorCount.incrementAndGet()) {
            case 1:
                return Action.RESTART;
            case 2:
                Thread.sleep(5000);
                return Action.RESTART;
            default:
                return Action.STOP;
            }
        } else if (error instanceof JudgeServerErrorException) {
            // persistence error, file read/write error
            this.totalServerError.incrementAndGet();
            switch (this.serverErrorCount.incrementAndGet()) {
            case 1:
                Thread.sleep(10000);
                return Action.RESTART;
            case 2:
                Thread.sleep(60000);
                return Action.RESTART;
            default:
                return Action.STOP;
            }
        } else {
            this.logger.error("Unexpected exception", error);
            return Action.STOP;
        }
    }
}
