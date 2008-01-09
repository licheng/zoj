/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;

/**
 * <p>Thread bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class Thread {

    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents forumId.</p>
     */
    private long forumId = -1;

    /**
     * <p>Represents userProfileId.</p>
     */
    private long userProfileId = -1;

    /**
     * <p>Represents title.</p>
     */
    private String title = null;

    /**
     * <p>Empty constructor.</p>
     */
    public Thread() {
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
     * <p>Gets forumId.</p>
     *
     * @return forumId
     */
    public long getForumId() {
        return this.forumId;
    }

    /**
     * <p>Sets forumId.</p>
     *
     * @param forumId forumId
     */
    public void setForumId(long forumId) {
        this.forumId = forumId;
    }

    /**
     * <p>Gets userProfileId.</p>
     *
     * @return userProfileId
     */
    public long getUserProfileId() {
        return this.userProfileId;
    }

    /**
     * <p>Sets userProfileId.</p>
     *
     * @param userProfileId userProfileId
     */
    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
    }

    /**
     * <p>Gets title.</p>
     *
     * @return title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * <p>Sets title.</p>
     *
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
