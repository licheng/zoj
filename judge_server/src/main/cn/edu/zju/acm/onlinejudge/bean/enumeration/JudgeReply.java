/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean.enumeration;

/**
 * <p>
 * This class represents the Judge Reply.
 * </p>
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class JudgeReply {

    // public static final int OutputLimitExceeded = 8;
    // public final static JudgeReply OUT_OF_CONTEST_TIME = new JudgeReply(8,
    // "Out of Contest Time", "Out of Contest Time", null, false);
    // public final static JudgeReply RESTRICTED_FUNCTION = new JudgeReply(9,
    // "Restricted Function", "Restricted Function", null, false);
    // public final static JudgeReply OUTPUT_LIMIT_EXCEEDED = new JudgeReply(10,
    // "Output Limit Exceeded", "Output Limit Exceeded", null, false);

    // public static final int Success = 0;

    public final static JudgeReply QUEUING = new JudgeReply(0, "Queuing", "Queuing", null, false);

    public final static JudgeReply COMPILING = new JudgeReply(1, "Compiling", "Compiling", null, false);

    public final static JudgeReply RUNNING = new JudgeReply(2, "Running", "Running", null, false);

    public final static JudgeReply RUNTIME_ERROR = new JudgeReply(3, "Runtime Error", "Runtime Error", null, false);

    public final static JudgeReply WRONG_ANSWER = new JudgeReply(4, "Wrong Answer", "Wrong Answer", null, false);

    public final static JudgeReply ACCEPTED = new JudgeReply(5, "Accepted", "Accepted", null, false);

    public final static JudgeReply TIME_LIMIT_EXCEEDED =
	    new JudgeReply(6, "Time Limit Exceeded", "Time Limit Exceeded", null, false);

    public final static JudgeReply MEMORY_LIMIT_EXCEEDED =
	    new JudgeReply(7, "Memory Limit Exceeded", "Memory Limit Exceeded", null, false);

    public final static JudgeReply OUT_OF_CONTEST_TIME =
	    new JudgeReply(8, "Out of Contest Time", "Out of Contest Time", null, false);

    public final static JudgeReply RESTRICTED_FUNCTION =
	    new JudgeReply(9, "Restricted Function", "Restricted Function", null, false);

    public final static JudgeReply OUTPUT_LIMIT_EXCEEDED =
	    new JudgeReply(10, "Output Limit Exceeded", "Output Limit Exceeded", null, false);

    public final static JudgeReply NO_SUCH_PROBLEM =
	    new JudgeReply(11, "No such Problem", "No such Problem", null, false);

    public final static JudgeReply COMPILATION_ERROR =
	    new JudgeReply(12, "Compilation Error", "Compilation Error", null, false);

    public final static JudgeReply PRESENTATION_ERROR =
	    new JudgeReply(13, "Presentation Error", "Presentation Error", null, false);

    public final static JudgeReply JUDGE_INTERNAL_ERROR =
	    new JudgeReply(14, "Judge Internal Error", "Judge Internal Error", null, false);

    public final static JudgeReply FLOATING_POINT_ERROR =
	    new JudgeReply(15, "Floating Point Error", "Floating Point Error", null, false);

    public final static JudgeReply SEGMENTATION_FAULT =
	    new JudgeReply(16, "Segmentation Fault", "Segmentation Fault", null, false);

    public final static JudgeReply JUDGING = new JudgeReply(19, "Judging", "Judging", null, false);
    
    public final static JudgeReply SUBMISSION_LIMIT_EXCEEDED = new JudgeReply(20, "Submission Limit Exceeded", "Submission Limit Exceeded", null, false);

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
    private boolean committed;

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
     *                the id of JudgeReply.
     * @param name
     *                the name of JudgeReply.
     * @param description
     *                the description of JudgeReply.
     * @param committed
     *                whether it is committed.
     * @param style
     *                wthe style
     * 
     * @throws NullPointerException
     *                 if any argument is null.
     * @throws IllegalArgumentException
     *                 if the name is an empty string.
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
	this.committed = committed;
	this.style = style;
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
    public boolean isCommitted() {
	return this.committed;
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
     *                the object to compare.
     */
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
     *                the id of JudgeReply
     */
    public static JudgeReply findById(long id) {
	if (QUEUING.id == id) {
	    return QUEUING;
	} else if (COMPILING.id == id) {
	    return COMPILING;
	} else if (RUNNING.id == id) {
	    return RUNNING;
	} else if (RUNTIME_ERROR.id == id) {
	    return RUNTIME_ERROR;
	} else if (WRONG_ANSWER.id == id) {
	    return WRONG_ANSWER;
	} else if (ACCEPTED.id == id) {
	    return ACCEPTED;
	} else if (TIME_LIMIT_EXCEEDED.id == id) {
	    return TIME_LIMIT_EXCEEDED;
	} else if (MEMORY_LIMIT_EXCEEDED.id == id) {
	    return MEMORY_LIMIT_EXCEEDED;
	} else if (OUT_OF_CONTEST_TIME.id == id) {
	    return OUT_OF_CONTEST_TIME;
	} else if (RESTRICTED_FUNCTION.id == id) {
	    return RESTRICTED_FUNCTION;
	} else if (OUTPUT_LIMIT_EXCEEDED.id == id) {
	    return OUTPUT_LIMIT_EXCEEDED;
	} else if (NO_SUCH_PROBLEM.id == id) {
	    return NO_SUCH_PROBLEM;
	} else if (COMPILATION_ERROR.id == id) {
	    return COMPILATION_ERROR;
	} else if (PRESENTATION_ERROR.id == id) {
	    return PRESENTATION_ERROR;
	} else if (JUDGE_INTERNAL_ERROR.id == id) {
	    return JUDGE_INTERNAL_ERROR;
	} else if (SEGMENTATION_FAULT.id == id) {
	    return SEGMENTATION_FAULT;
	} else if (FLOATING_POINT_ERROR.id == id) {
	    return FLOATING_POINT_ERROR;
	} else if (SUBMISSION_LIMIT_EXCEEDED.id == id) {
        return SUBMISSION_LIMIT_EXCEEDED;
    } else {
	    return null;
	}
    }

    /**
     * <p>
     * Get the JudgeReply Type represented by name.
     * </p>
     * 
     * @return the JudgeReply Type represented by name, or null if none matchs.
     * 
     * @param name
     *                the name of JudgeReply.
     */
    public static JudgeReply findByName(String name) {
	if (QUEUING.description.equalsIgnoreCase(name)) {
	    return QUEUING;
	} else if (COMPILING.description.equalsIgnoreCase(name)) {
	    return COMPILING;
	} else if (RUNNING.description.equalsIgnoreCase(name)) {
	    return RUNNING;
	} else if (RUNTIME_ERROR.description.equalsIgnoreCase(name)) {
	    return RUNTIME_ERROR;
	} else if (WRONG_ANSWER.description.equalsIgnoreCase(name)) {
	    return WRONG_ANSWER;
	} else if (ACCEPTED.description.equalsIgnoreCase(name)) {
	    return ACCEPTED;
	} else if (TIME_LIMIT_EXCEEDED.description.equalsIgnoreCase(name)) {
	    return TIME_LIMIT_EXCEEDED;
	} else if (MEMORY_LIMIT_EXCEEDED.description.equalsIgnoreCase(name)) {
	    return MEMORY_LIMIT_EXCEEDED;
	} else if (OUT_OF_CONTEST_TIME.description.equalsIgnoreCase(name)) {
	    return OUT_OF_CONTEST_TIME;
	} else if (RESTRICTED_FUNCTION.description.equalsIgnoreCase(name)) {
	    return RESTRICTED_FUNCTION;
	} else if (OUTPUT_LIMIT_EXCEEDED.description.equalsIgnoreCase(name)) {
	    return OUTPUT_LIMIT_EXCEEDED;
	} else if (NO_SUCH_PROBLEM.description.equalsIgnoreCase(name)) {
	    return NO_SUCH_PROBLEM;
	} else if (COMPILATION_ERROR.description.equalsIgnoreCase(name)) {
	    return COMPILATION_ERROR;
	} else if (PRESENTATION_ERROR.description.equalsIgnoreCase(name)) {
	    return PRESENTATION_ERROR;
	} else if (FLOATING_POINT_ERROR.description.equalsIgnoreCase(name)) {
	    return FLOATING_POINT_ERROR;
	} else if (SEGMENTATION_FAULT.description.equalsIgnoreCase(name)) {
        return SEGMENTATION_FAULT;
    } else if (JUDGE_INTERNAL_ERROR.description.equalsIgnoreCase(name)) {
        return JUDGE_INTERNAL_ERROR;
    } else if (SUBMISSION_LIMIT_EXCEEDED.description.equalsIgnoreCase(name)) {
        return SUBMISSION_LIMIT_EXCEEDED;
    } else {
	    return null;
	}
    
    }

}
