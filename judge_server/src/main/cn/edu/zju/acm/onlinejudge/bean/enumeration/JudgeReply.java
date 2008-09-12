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

package cn.edu.zju.acm.onlinejudge.bean.enumeration;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class represents the Judge Reply.
 * </p>
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class JudgeReply {

    private static List<JudgeReply> replies = new ArrayList<JudgeReply>();

    public final static JudgeReply QUEUING = new JudgeReply(0, "Queuing", "Queuing", null, false);

    public final static JudgeReply COMPILING = new JudgeReply(1, "Compiling", "Compiling", null, false);

    public final static JudgeReply RUNNING = new JudgeReply(2, "Running", "Running", null, false);

    public final static JudgeReply RUNTIME_ERROR = new JudgeReply(3, "Runtime Error", "Runtime Error", null, true);

    public final static JudgeReply WRONG_ANSWER = new JudgeReply(4, "Wrong Answer", "Wrong Answer", null, true);

    public final static JudgeReply ACCEPTED = new JudgeReply(5, "Accepted", "Accepted", null, true);

    public final static JudgeReply TIME_LIMIT_EXCEEDED =
            new JudgeReply(6, "Time Limit Exceeded", "Time Limit Exceeded", null, true);

    public final static JudgeReply MEMORY_LIMIT_EXCEEDED =
            new JudgeReply(7, "Memory Limit Exceeded", "Memory Limit Exceeded", null, true);

    public final static JudgeReply OUT_OF_CONTEST_TIME =
            new JudgeReply(8, "Out of Contest Time", "Out of Contest Time", null, true);

    public final static JudgeReply RESTRICTED_FUNCTION =
            new JudgeReply(9, "Restricted Function", "Restricted Function", null, true);

    public final static JudgeReply OUTPUT_LIMIT_EXCEEDED =
            new JudgeReply(10, "Output Limit Exceeded", "Output Limit Exceeded", null, true);

    public final static JudgeReply COMPILATION_ERROR =
            new JudgeReply(12, "Compilation Error", "Compilation Error", null, true);

    public final static JudgeReply PRESENTATION_ERROR =
            new JudgeReply(13, "Presentation Error", "Presentation Error", null, true);

    public final static JudgeReply JUDGE_INTERNAL_ERROR =
            new JudgeReply(14, "Judge Internal Error", "Judge Internal Error", null, true);

    public final static JudgeReply FLOATING_POINT_ERROR =
            new JudgeReply(15, "Floating Point Error", "Floating Point Error", null, true);

    public final static JudgeReply SEGMENTATION_FAULT =
            new JudgeReply(16, "Segmentation Fault", "Segmentation Fault", null, true);

    public final static JudgeReply JUDGING = new JudgeReply(19, "Judging", "Judging", null, false);

    public final static JudgeReply SUBMISSION_LIMIT_EXCEEDED =
            new JudgeReply(20, "Submission Limit Exceeded", "Submission Limit Exceeded", null, true);
    public final static JudgeReply READY = new JudgeReply(100, "Ready", "Ready", null, false);
    public final static JudgeReply NO_SUCH_PROBLEM =
            new JudgeReply(101, "No Such Problem", "No Such Problem", null, false);
    public final static JudgeReply INVALID_INPUT = new JudgeReply(102, "Invalid Input", "Invalid Input", null, false);
    /**
     * <p>
     * Represents the id of JudgeReply.
     * </p>
     */
    private long id;

    /**
     * <p>
     * Represents the name of JudgeReply.
     * </p>
     */
    private String name;

    /**
     * <p>
     * Represents the description of JudgeReply.
     * </p>
     */
    private String description;

    /**
     * <p>
     * Represents whether it is committed.
     * </p>
     */
    private boolean committedReply;

    /**
     * <p>
     * Represents the style of JudgeReply.
     * </p>
     */
    private String style;

    /**
     * <p>
     * Create a new instance of JudgeReply.
     * </p>
     * 
     * @param id
     *            the id of JudgeReply.
     * @param name
     *            the name of JudgeReply.
     * @param description
     *            the description of JudgeReply.
     * @param committed
     *            whether it is committed.
     * @param style
     *            wthe style
     * 
     * @throws NullPointerException
     *             if any argument is null.
     * @throws IllegalArgumentException
     *             if the name is an empty string.
     */
    public JudgeReply(long id, String name, String description, String style, boolean committed) {
        if (name == null) {
            throw new NullPointerException("name should not be null.");
        }
        if (description == null) {
            throw new NullPointerException("description should not be null.");
        }
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("name should not be empty string.");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.committedReply = committed;
        this.style = style;
        JudgeReply.replies.add(this);
    }

    /**
     * <p>
     * Get the id of the JudgeReply.
     * </p>
     * 
     * @return id the id of the JudgeReply.
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>
     * Get the name of the JudgeReply.
     * </p>
     * 
     * @return id the name of the JudgeReply.
     */
    public String getName() {
        return this.name;
    }

    /**
     * <p>
     * Get the description of the JudgeReply.
     * </p>
     * 
     * @return id the description of the JudgeReply.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * <p>
     * whether the JudgeReply is committed.
     * </p>
     * 
     * @return true if the JudgeReply is committed.
     */
    public boolean isCommittedReply() {
        return this.committedReply;
    }

    /**
     * <p>
     * Get the style of the JudgeReply.
     * </p>
     * 
     * @return id the style of the JudgeReply.
     */
    public String getStyle() {
        return this.style;
    }

    /**
     * <p>
     * Return the name.
     * 
     * @return the name
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * <p>
     * Compares to the object.
     * </p>
     * 
     * @return true if and only if the ids of the two are the same.
     * 
     * @param object
     *            the object to compare.
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof JudgeReply && this.id == ((JudgeReply) object).id;
    }

    /**
     * <p>
     * get the hashCode of this instance.
     * </p>
     * 
     * @return the hash code of this instance.
     */
    @Override
    public int hashCode() {
        return new Long(this.id).hashCode();
    }

    /**
     * <p>
     * Get the JudgeReply Type represented by id.
     * </p>
     * 
     * @return the JudgeReply Type represented by id, or null if none is found.
     * 
     * @param id
     *            the id of JudgeReply
     */
    public static JudgeReply findById(long id) {
        for (JudgeReply reply : JudgeReply.replies) {
            if (reply.getId() == id) {
                return reply;
            }
        }
        return null;
    }

    /**
     * <p>
     * Get the JudgeReply Type represented by name.
     * </p>
     * 
     * @return the JudgeReply Type represented by name, or null if none matchs.
     * 
     * @param name
     *            the name of JudgeReply.
     */
    public static JudgeReply findByName(String name) {
        for (JudgeReply reply : JudgeReply.replies) {
            if (reply.getName().equals(name)) {
                return reply;
            }
        }
        return null;

    }

    public static List<JudgeReply> getAllJudgeReplies() {
        List<JudgeReply> judgeReplies = new ArrayList<JudgeReply>();
        for (JudgeReply reply : JudgeReply.replies) {
            if (reply.isCommittedReply()) {
                judgeReplies.add(reply);
            }
        }
        return judgeReplies;
    }

}
