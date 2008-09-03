package cn.edu.zju.acm.onlinejudge.judgeservice;

public class Priority {
    public static final int MIN = 1;
    public static final int LOW = 4;
    public static final int NORMAL = 5;
    public static final int HIGH = 6;
    public static final int MAX = 9;
    public static final int DENY = -100000;
    
    public static boolean isValidPriority(int priority) {
        return priority >= Priority.MIN && priority <= Priority.MAX;
    }
}
