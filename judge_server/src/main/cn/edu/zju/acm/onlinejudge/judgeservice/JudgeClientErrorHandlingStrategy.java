/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */

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

    public Action onJudgeError(JudgeClientJudgeThread judgeThread, Exception error) throws InterruptedException {
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
