package cn.edu.zju.acm.onlinejudge.judgeserver;

public class NoSuchProblemException extends Exception {
    public NoSuchProblemException() {
	super("Invalid judge reply");
    }

    public NoSuchProblemException(String message) {
	super(message);
    }
}
