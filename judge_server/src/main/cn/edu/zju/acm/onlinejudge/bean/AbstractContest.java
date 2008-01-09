/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * <p>AbstractContest bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public abstract class AbstractContest {

    
    
    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents title.</p>
     */
    private String title = null;

    /**
     * <p>Represents description.</p>
     */
    private String description = null;

    /**
     * <p>Represents limit.</p>
     */
    private Limit limit = null;

    /**
     * <p>Represents forumId.</p>
     */
    private long forumId = -1;

    /**
     * <p>Represents languages.</p>
     */
    private List languages = null;

    /**
     * <p>Represents startTime.</p>
     */
    private Date startTime = null;

    /**
     * <p>Represents endTime.</p>
     */
    private Date endTime = null;

    private boolean checkIp = false;
    
    /**
     * <p>Abstract constructor.</p>
     */
    protected AbstractContest() {
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
     * <p>Gets title.</p>
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }


    
    /**
     * <p>Sets title.</p>
     *
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Gets description.</p>
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * <p>Sets description.</p>
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Gets limit.</p>
     *
     * @return limit
     */
    public Limit getLimit() {
        return this.limit;
    }

    /**
     * <p>Sets limit.</p>
     *
     * @param limit limit
     */
    public void setLimit(Limit limit) {
        this.limit = limit;
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
     * <p>Gets languages.</p>
     *
     * @return languages
     */
    public List getLanguages() {
        return this.languages;
    }

    /**
     * <p>Sets languages.</p>
     *
     * @param languages languages
     */
    public void setLanguages(List languages) {
        this.languages = languages;
    }
    
    /**
     * <p>Gets startTime.</p>
     *
     * @return startTime
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * <p>Sets startTime.</p>
     *
     * @param startTime startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * <p>Gets endTime.</p>
     *
     * @return endTime
     */
    public Date getEndTime() {
        return this.endTime;
    }

    /**
     * <p>Sets endTime.</p>
     *
     * @param endTime endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * <p>Gets the hours the contest lasts.</p>
     *
     * @return the hours the contest lasts
     */
    public int getHours() {
        return (int) ((this.endTime.getTime() - this.startTime.getTime()) / 1000 / 60 / 60);
    }

    /**
     * <p>Gets the minutes the contest lasts.</p>
     *
     * @return the minutes the contest lasts
     */
    public int getMinutes() {
        return (int) (((this.endTime.getTime() - this.startTime.getTime()) / 1000 / 60) % 60);
    }

    /**
     * <p>Gets the seconds the contest lasts.</p>
     *
     * @return the seconds the contest lasts
     */
    public int getSeconds() {
        return (int) (((this.endTime.getTime() - this.startTime.getTime()) / 1000) % 60);
    }    
    public long getLength() {
    	return endTime.getTime() - startTime.getTime();
    }

    public boolean isCheckIp() {
        return checkIp;
    }

    public void setCheckIp(boolean checkIp) {
        this.checkIp = checkIp;
    }
    

}
