/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

package cn.edu.zju.acm.onlinejudge.bean;

/**
 * <p>
 * Problem bean.
 * </p>
 * 
 * @author Zhang, Zheng
 * 
 * @version 2.0
 */
public class Problem implements Comparable<Problem> {

    /**
     * <p>
     * Represents id.
     * </p>
     */
    private long id = -1;

    /**
     * <p>
     * Represents contestId.
     * </p>
     */
    private long contestId = -1;

    /**
     * <p>
     * Represents title.
     * </p>
     */
    private String title = null;

    /**
     * <p>
     * Represents author.
     * </p>
     */
    private String color = null;

    /**
     * <p>
     * Represents author.
     * </p>
     */
    private String author = null;

    /**
     * <p>
     * Represents contest.
     * </p>
     */
    private String contest = null;

    /**
     * <p>
     * Represents source.
     * </p>
     */
    private String source = null;

    /**
     * <p>
     * Represents checker.
     * </p>
     */
    private boolean checker = false;

    /**
     * <p>
     * Represents limit.
     * </p>
     */
    private Limit limit = null;

    /**
     * <p>
     * Represents code.
     * </p>
     */
    private String code = null;

    /**
     * <p>
     * Represents revision.
     * </p>
     */
    private int revision = -1;

    /**
     * <p>
     * Represents score.
     * </p>
     */
    private int score = 0;

    
    private int total= -1;

    private int ac= -1;

    private double ratio= 0;

    /**
     * <p>
     * Empty constructor.
     * </p>
     */
    public Problem() {}

    /**
     * <p>
     * Gets id.
     * </p>
     * 
     * @return id
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>
     * Sets id.
     * </p>
     * 
     * @param id
     *            id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>
     * Gets contestId.
     * </p>
     * 
     * @return contestId
     */
    public long getContestId() {
        return this.contestId;
    }

    /**
     * <p>
     * Sets contestId.
     * </p>
     * 
     * @param contestId
     *            contestId
     */
    public void setContestId(long contestId) {
        this.contestId = contestId;
    }

    /**
     * <p>
     * Gets title.
     * </p>
     * 
     * @return title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * <p>
     * Sets title.
     * </p>
     * 
     * @param title
     *            title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>
     * Gets author.
     * </p>
     * 
     * @return author
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * <p>
     * Sets author.
     * </p>
     * 
     * @param author
     *            author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * <p>
     * Gets checker.
     * </p>
     * 
     * @return checker
     */
    public boolean isChecker() {
        return this.checker;
    }

    /**
     * <p>
     * Sets checker.
     * </p>
     * 
     * @param checker
     *            checker
     */
    public void setChecker(boolean checker) {
        this.checker = checker;
    }

    /**
     * <p>
     * Gets limit.
     * </p>
     * 
     * @return limit
     */
    public Limit getLimit() {
        return this.limit;
    }

    /**
     * <p>
     * Sets limit.
     * </p>
     * 
     * @param limit
     *            limit
     */
    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    /**
     * <p>
     * Gets code.
     * </p>
     * 
     * @return code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * <p>
     * Sets code.
     * </p>
     * 
     * @param code
     *            code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * <p>
     * Gets revision.
     * </p>
     * 
     * @return revision
     */
    public int getRevision() {
        return this.revision;
    }

    /**
     * <p>
     * Sets revision.
     * </p>
     * 
     * @param revision
     *            revision
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }

    /**
     * <p>
     * Gets source.
     * </p>
     * 
     * @return source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * <p>
     * Sets source.
     * </p>
     * 
     * @param source
     *            source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * <p>
     * Gets contest.
     * </p>
     * 
     * @return contest
     */
    public String getContest() {
        return this.contest;
    }

    /**
     * <p>
     * Sets contest.
     * </p>
     * 
     * @param contest
     *            contest
     */
    public void setContest(String contest) {
        this.contest = contest;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAC() {
        return this.ac;
    }

    public void setAC(int ac) {
        this.ac = ac;
    }

    public double getRatio() {
        return this.ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
    
    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object obj) {
        Problem that = (Problem) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) this.id;
    }

    public int compareTo(Problem obj) {
        Problem that = obj;

        return this.id == that.id ? 0 : this.id > that.id ? 1 : -1;
    }
}
