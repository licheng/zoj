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
 * Limit bean.
 * </p>
 * 
 * @author Zhang, Zheng
 * 
 * @version 2.0
 */
public class Limit {

    /**
     * The default limit id.
     */
    public static final long DEFAULT_LIMIT_ID = 1;

    /**
     * <p>
     * Represents id.
     * </p>
     */
    private long id = -1;

    /**
     * <p>
     * Represents timeLimit.
     * </p>
     */
    private int timeLimit = -1;

    /**
     * <p>
     * Represents memoryLimit.
     * </p>
     */
    private int memoryLimit = -1;

    /**
     * <p>
     * Represents outputLimit.
     * </p>
     */
    private int outputLimit = -1;

    /**
     * <p>
     * Represents submissionLimit.
     * </p>
     */
    private int submissionLimit = -1;

    /**
     * <p>
     * Empty constructor.
     * </p>
     */
    public Limit() {}

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
     * Gets timeLimit.
     * </p>
     * 
     * @return timeLimit
     */
    public int getTimeLimit() {
        return this.timeLimit;
    }

    /**
     * <p>
     * Sets timeLimit.
     * </p>
     * 
     * @param timeLimit
     *            timeLimit
     */
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * <p>
     * Gets memoryLimit.
     * </p>
     * 
     * @return memoryLimit
     */
    public int getMemoryLimit() {
        return this.memoryLimit;
    }

    /**
     * <p>
     * Sets memoryLimit.
     * </p>
     * 
     * @param memoryLimit
     *            memoryLimit
     */
    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    /**
     * <p>
     * Gets outputLimit.
     * </p>
     * 
     * @return outputLimit
     */
    public int getOutputLimit() {
        return this.outputLimit;
    }

    /**
     * <p>
     * Sets outputLimit.
     * </p>
     * 
     * @param outputLimit
     *            outputLimit
     */
    public void setOutputLimit(int outputLimit) {
        this.outputLimit = outputLimit;
    }

    /**
     * <p>
     * Gets submissionLimit.
     * </p>
     * 
     * @return submissionLimit
     */
    public int getSubmissionLimit() {
        return this.submissionLimit;
    }

    /**
     * <p>
     * Sets submissionLimit.
     * </p>
     * 
     * @param submissionLimit
     *            submissionLimit
     */
    public void setSubmissionLimit(int submissionLimit) {
        this.submissionLimit = submissionLimit;
    }

}
