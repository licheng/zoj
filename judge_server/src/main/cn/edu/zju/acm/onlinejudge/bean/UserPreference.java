/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;

/**
 * <p>UserPreference bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class UserPreference {

    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents plan.</p>
     */
    private String plan = null;

    /**
     * <p>Represents problemPaging.</p>
     */
    private int problemPaging = -1;

    /**
     * <p>Represents submissionPaging.</p>
     */
    private int submissionPaging = -1;

    /**
     * <p>Represents statusPaging.</p>
     */
    private int statusPaging = -1;

    /**
     * <p>Represents threadPaging.</p>
     */
    private int threadPaging = -1;

    /**
     * <p>Represents postPaging.</p>
     */
    private int postPaging = -1;

    /**
     * <p>Represents userPaging.</p>
     */
    private int userPaging = -1;

    /**
     * <p>Empty constructor.</p>
     */
    public UserPreference() {
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
     * <p>Gets plan.</p>
     *
     * @return plan
     */
    public String getPlan() {
        return this.plan;
    }

    /**
     * <p>Sets plan.</p>
     *
     * @param plan plan
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * <p>Gets problemPaging.</p>
     *
     * @return problemPaging
     */
    public int getProblemPaging() {
        return this.problemPaging;
    }

    /**
     * <p>Sets problemPaging.</p>
     *
     * @param problemPaging problemPaging
     */
    public void setProblemPaging(int problemPaging) {
        this.problemPaging = problemPaging;
    }

    /**
     * <p>Gets submissionPaging.</p>
     *
     * @return submissionPaging
     */
    public int getSubmissionPaging() {
        return this.submissionPaging;
    }

    /**
     * <p>Sets submissionPaging.</p>
     *
     * @param submissionPaging submissionPaging
     */
    public void setSubmissionPaging(int submissionPaging) {
        this.submissionPaging = submissionPaging;
    }

    /**
     * <p>Gets statusPaging.</p>
     *
     * @return statusPaging
     */
    public int getStatusPaging() {
        return this.statusPaging;
    }

    /**
     * <p>Sets statusPaging.</p>
     *
     * @param statusPaging statusPaging
     */
    public void setStatusPaging(int statusPaging) {
        this.statusPaging = statusPaging;
    }

    /**
     * <p>Gets threadPaging.</p>
     *
     * @return threadPaging
     */
    public int getThreadPaging() {
        return this.threadPaging;
    }

    /**
     * <p>Sets threadPaging.</p>
     *
     * @param threadPaging threadPaging
     */
    public void setThreadPaging(int threadPaging) {
        this.threadPaging = threadPaging;
    }

    /**
     * <p>Gets postPaging.</p>
     *
     * @return postPaging
     */
    public int getPostPaging() {
        return this.postPaging;
    }

    /**
     * <p>Sets postPaging.</p>
     *
     * @param postPaging postPaging
     */
    public void setPostPaging(int postPaging) {
        this.postPaging = postPaging;
    }

    /**
     * <p>Gets userPaging.</p>
     *
     * @return userPaging
     */
    public int getUserPaging() {
        return this.userPaging;
    }

    /**
     * <p>Sets userPaging.</p>
     *
     * @param userPaging userPaging
     */
    public void setUserPaging(int userPaging) {
        this.userPaging = userPaging;
    }

}
