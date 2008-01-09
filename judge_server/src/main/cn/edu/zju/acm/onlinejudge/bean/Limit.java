/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;

/**
 * <p>Limit bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class Limit {

    /**
     * The default limit id.
     */
    public static final long DEFAULT_LIMIT_ID = 1;
    
    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents timeLimit.</p>
     */
    private int timeLimit = -1;

    /**
     * <p>Represents memoryLimit.</p>
     */
    private int memoryLimit = -1;

    /**
     * <p>Represents outputLimit.</p>
     */
    private int outputLimit = -1;

    /**
     * <p>Represents submissionLimit.</p>
     */
    private int submissionLimit = -1;

    /**
     * <p>Empty constructor.</p>
     */
    public Limit() {
    }

    /**
     * <p>Gets id.</p>
     *
     * @return id
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>Sets id.</p>
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>Gets timeLimit.</p>
     *
     * @return timeLimit
     */
    public int getTimeLimit() {
        return this.timeLimit;
    }

    /**
     * <p>Sets timeLimit.</p>
     *
     * @param timeLimit timeLimit
     */
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * <p>Gets memoryLimit.</p>
     *
     * @return memoryLimit
     */
    public int getMemoryLimit() {
        return this.memoryLimit;
    }

    /**
     * <p>Sets memoryLimit.</p>
     *
     * @param memoryLimit memoryLimit
     */
    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    /**
     * <p>Gets outputLimit.</p>
     *
     * @return outputLimit
     */
    public int getOutputLimit() {
        return this.outputLimit;
    }

    /**
     * <p>Sets outputLimit.</p>
     *
     * @param outputLimit outputLimit
     */
    public void setOutputLimit(int outputLimit) {
        this.outputLimit = outputLimit;
    }

    /**
     * <p>Gets submissionLimit.</p>
     *
     * @return submissionLimit
     */
    public int getSubmissionLimit() {
        return this.submissionLimit;
    }

    /**
     * <p>Sets submissionLimit.</p>
     *
     * @param submissionLimit submissionLimit
     */
    public void setSubmissionLimit(int submissionLimit) {
        this.submissionLimit = submissionLimit;
    }

}
