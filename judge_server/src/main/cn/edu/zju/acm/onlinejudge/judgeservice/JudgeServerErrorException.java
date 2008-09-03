package cn.edu.zju.acm.onlinejudge.judgeservice;

import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

public class JudgeServerErrorException extends Exception {

    public JudgeServerErrorException(String message) {
        super(message);
    }

    public JudgeServerErrorException(String message, Exception e) {
        super(message, e);
    }

    public JudgeServerErrorException(PersistenceException e) {
        super(e);
    }
}
