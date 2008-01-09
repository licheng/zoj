/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;


/**
 * <p>
 * Contest bean.
 * </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class Contest {

    /**
     * The id.
     */
    private String id = null;

    /**
     * The name.
     */
    private String name = null;

    /**
     * The Description.
     */
    private String description = null;

    /**
     * The startTime.
     */
    private String startTime = null;

    /**
     * The contestLength.
     */
    private String contestLength = null;

    /**
     * The forumId.
     */
    private String forumId = null;

    /**
     * The timeLimit.
     */
    private String timeLimit = null;

    /**
     * The MemoryLimit.
     */
    private String memoryLimit = null;

    /**
     * The outputLimit.
     */
    private String outputLimit = null;

    /**
     * The submissionLimit.
     */
    private String submissionLimit = null;

    /**
     * The useGlobalDefault.
     */
    private String useGlobalDefault = null;

    /**
     * The languageIds.
     */
    private String languageIds = null;

    /**
     * Empty constructor.
     */
    public Contest() {
        // Empty constructor
    }

    /**
     * Sets the id.
     *
     * @prama id the id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the id.
     *
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the name.
     *
     * @prama name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Description.
     *
     * @prama description the Description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Description.
     *
     * @return the Description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the startTime.
     *
     * @prama startTime the startTime to set.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the startTime.
     *
     * @return the startTime.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the contestLength.
     *
     * @prama contestLength the contestLength to set.
     */
    public void setContestLength(String contestLength) {
        this.contestLength = contestLength;
    }

    /**
     * Gets the contestLength.
     *
     * @return the contestLength.
     */
    public String getContestLength() {
        return contestLength;
    }

    /**
     * Sets the forumId.
     *
     * @prama forumId the forumId to set.
     */
    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    /**
     * Gets the forumId.
     *
     * @return the forumId.
     */
    public String getForumId() {
        return forumId;
    }

    /**
     * Sets the timeLimit.
     *
     * @prama timeLimit the timeLimit to set.
     */
    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * Gets the timeLimit.
     *
     * @return the timeLimit.
     */
    public String getTimeLimit() {
        return timeLimit;
    }

    /**
     * Sets the MemoryLimit.
     *
     * @prama memoryLimit the MemoryLimit to set.
     */
    public void setMemoryLimit(String memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    /**
     * Gets the MemoryLimit.
     *
     * @return the MemoryLimit.
     */
    public String getMemoryLimit() {
        return memoryLimit;
    }

    /**
     * Sets the outputLimit.
     *
     * @prama outputLimit the outputLimit to set.
     */
    public void setOutputLimit(String outputLimit) {
        this.outputLimit = outputLimit;
    }

    /**
     * Gets the outputLimit.
     *
     * @return the outputLimit.
     */
    public String getOutputLimit() {
        return outputLimit;
    }

    /**
     * Sets the submissionLimit.
     *
     * @prama submissionLimit the submissionLimit to set.
     */
    public void setSubmissionLimit(String submissionLimit) {
        this.submissionLimit = submissionLimit;
    }

    /**
     * Gets the submissionLimit.
     *
     * @return the submissionLimit.
     */
    public String getSubmissionLimit() {
        return submissionLimit;
    }

    /**
     * Sets the useGlobalDefault.
     *
     * @prama useGlobalDefault the useGlobalDefault to set.
     */
    public void setUseGlobalDefault(String useGlobalDefault) {
        this.useGlobalDefault = useGlobalDefault;
    }

    /**
     * Gets the useGlobalDefault.
     *
     * @return the useGlobalDefault.
     */
    public String getUseGlobalDefault() {
        return useGlobalDefault;
    }

    /**
     * Sets the languageIds.
     *
     * @prama languageIds the languageIds to set.
     */
    public void setLanguageIds(String languageIds) {
        this.languageIds = languageIds;
    }

    /**
     * Gets the languageIds.
     *
     * @return the languageIds.
     */
    public String getLanguageIds() {
        return languageIds;
    }

}
