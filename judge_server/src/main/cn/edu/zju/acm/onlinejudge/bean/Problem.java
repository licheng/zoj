/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;

/**
 * <p>Problem bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class Problem {

    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents contestId.</p>
     */
    private long contestId = -1;

    /**
     * <p>Represents title.</p>
     */
    private String title = null;

    /**
     * <p>Represents author.</p>
     */
    private String color = null;
    
    /**
     * <p>Represents author.</p>
     */
    private String author = null;
    
    /**
     * <p>Represents contest.</p>
     */
    private String contest = null;
    
    /**
     * <p>Represents source.</p>
     */
    private String source = null;

    /**
     * <p>Represents checker.</p>
     */
    private boolean checker = false;

    /**
     * <p>Represents limit.</p>
     */
    private Limit limit = null;

    /**
     * <p>Represents code.</p>
     */
    private String code = null;

    /**
     * <p>Represents revision.</p>
     */
    private int revision = -1;

    /**
     * <p>Empty constructor.</p>
     */
    public Problem() {
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
     * <p>Gets contestId.</p>
     *
     * @return contestId
     */
    public long getContestId() {
        return this.contestId;
    }

    /**
     * <p>Sets contestId.</p>
     *
     * @param contestId contestId
     */
    public void setContestId(long contestId) {
        this.contestId = contestId;
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

    /**
     * <p>Gets author.</p>
     *
     * @return author
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * <p>Sets author.</p>
     *
     * @param author author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * <p>Gets checker.</p>
     *
     * @return checker
     */
    public boolean isChecker() {
        return this.checker;
    }

    /**
     * <p>Sets checker.</p>
     *
     * @param checker checker
     */
    public void setChecker(boolean checker) {
        this.checker = checker;
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
     * <p>Gets code.</p>
     *
     * @return code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * <p>Sets code.</p>
     *
     * @param code code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * <p>Gets revision.</p>
     *
     * @return revision
     */
    public int getRevision() {
        return this.revision;
    }

    /**
     * <p>Sets revision.</p>
     *
     * @param revision revision
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }
    
    /**
     * <p>Gets source.</p>
     *
     * @return source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * <p>Sets source.</p>
     *
     * @param source source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * <p>Gets contest.</p>
     *
     * @return contest
     */
    public String getContest() {
        return this.contest;
    }

    /**
     * <p>Sets contest.</p>
     *
     * @param contest contest
     */
    public void setContest(String contest) {
        this.contest = contest;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
