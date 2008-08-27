package cn.edu.zju.acm.onlinejudge.judgeservice;

public class JudgeServerErrorException extends Exception {

    public JudgeServerErrorException() {
	super();
    }
    
    public JudgeServerErrorException(Exception e) {
	super(e);
    }
}
