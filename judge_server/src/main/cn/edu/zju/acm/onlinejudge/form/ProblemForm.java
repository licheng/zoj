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

package cn.edu.zju.acm.onlinejudge.form;

import java.io.Serializable;
import java.text.ParseException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

/**
 * <p>
 * ContestForm.
 * </p>
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ProblemForm extends ActionForm implements Serializable {

    /**
     * The contest id.
     */
    private String contestId = null;

    /**
     * The id.
     */
    private String problemId = null;

    /**
     * The name.
     */
    private String name = null;

    /**
     * The author.
     */
    private String author = null;

    /**
     * The code.
     */
    private String code = null;

    /**
     * The source.
     */
    private String source = null;

    /**
     * The contest.
     */
    private String contest = null;

    /**
     * The timeLimit.
     */
    private String timeLimit = null;

    /**
     * The MemoryLimit.
     */
    private String memoryLimit = null;

    /**
     * The outputLimit.
     */
    private String outputLimit = null;

    /**
     * The submissionLimit.
     */
    private String submissionLimit = null;

    /**
     * The useContestDefault.
     */
    private boolean useContestDefault = false;

    /**
     * The specialJudge.
     */
    private boolean specialJudge = false;

    private String color = null;

    private FormFile description = null;
    private FormFile inputData = null;
    private FormFile outputData = null;
    private FormFile judgeSolution = null;
    private FormFile checker = null;
    private FormFile checkerSource = null;

    /**
     * Empty constructor.
     */
    public ProblemForm() {
    // Empty constructor
    }

    /**
     * Sets the contest id.
     * 
     * @prama contestId the contestId to set.
     */
    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    /**
     * Gets the contest id.
     * 
     * @return the contest id.
     */
    public String getContestId() {
        return this.contestId;
    }

    /**
     * Sets the id.
     * 
     * @prama id the id to set.
     */
    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    /**
     * Gets the id.
     * 
     * @return the id.
     */
    public String getProblemId() {
        return this.problemId;
    }

    /**
     * Sets the name.
     * 
     * @prama name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name.
     * 
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the author.
     * 
     * @prama author the author to set.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the author.
     * 
     * @return the author.
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Sets the source.
     * 
     * @prama source the source to set.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the source.
     * 
     * @return the source.
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Sets the contest.
     * 
     * @prama contest the contest to set.
     */
    public void setContest(String contest) {
        this.contest = contest;
    }

    /**
     * Gets the contest.
     * 
     * @return the contest.
     */
    public String getContest() {
        return this.contest;
    }

    /**
     * Sets the code.
     * 
     * @prama code the code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the code.
     * 
     * @return the code.
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Sets the timeLimit.
     * 
     * @prama timeLimit the timeLimit to set.
     */
    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * Gets the timeLimit.
     * 
     * @return the timeLimit.
     */
    public String getTimeLimit() {
        return this.timeLimit;
    }

    /**
     * Sets the MemoryLimit.
     * 
     * @prama memoryLimit the MemoryLimit to set.
     */
    public void setMemoryLimit(String memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    /**
     * Gets the MemoryLimit.
     * 
     * @return the MemoryLimit.
     */
    public String getMemoryLimit() {
        return this.memoryLimit;
    }

    /**
     * Sets the outputLimit.
     * 
     * @prama outputLimit the outputLimit to set.
     */
    public void setOutputLimit(String outputLimit) {
        this.outputLimit = outputLimit;
    }

    /**
     * Gets the outputLimit.
     * 
     * @return the outputLimit.
     */
    public String getOutputLimit() {
        return this.outputLimit;
    }

    /**
     * Sets the submissionLimit.
     * 
     * @prama submissionLimit the submissionLimit to set.
     */
    public void setSubmissionLimit(String submissionLimit) {
        this.submissionLimit = submissionLimit;
    }

    /**
     * Gets the submissionLimit.
     * 
     * @return the submissionLimit.
     */
    public String getSubmissionLimit() {
        return this.submissionLimit;
    }

    /**
     * Sets the useContestDefault.
     * 
     * @prama useContestDefault the useContestDefault to set.
     */
    public void setUseContestDefault(boolean useContestDefault) {
        this.useContestDefault = useContestDefault;
    }

    /**
     * Gets the useContestDefault.
     * 
     * @return the useContestDefault.
     */
    public boolean isUseContestDefault() {
        return this.useContestDefault;
    }

    /**
     * Sets the specialJudge.
     * 
     * @prama specialJudge the specialJudge to set.
     */
    public void setSpecialJudge(boolean specialJudge) {
        this.specialJudge = specialJudge;
    }

    /**
     * Gets the specialJudge.
     * 
     * @return the specialJudge.
     */
    public boolean isSpecialJudge() {
        return this.specialJudge;
    }

    public FormFile getDescription() {
        return this.description;
    }

    public FormFile getInputData() {
        return this.inputData;
    }

    public FormFile getOutputData() {
        return this.outputData;
    }

    public FormFile getJudgeSolution() {
        return this.judgeSolution;
    }

    public FormFile getChecker() {
        return this.checker;
    }

    public FormFile getCheckerSource() {
        return this.checkerSource;
    }

    public void setDescription(FormFile description) {
        this.description = description;
    }

    public void setInputData(FormFile inputData) {
        this.inputData = inputData;
    }

    public void setOutputData(FormFile outputData) {
        this.outputData = outputData;
    }

    public void setJudgeSolution(FormFile judgeSolution) {
        this.judgeSolution = judgeSolution;
    }

    public void setChecker(FormFile checker) {
        this.checker = checker;
    }

    public void setCheckerSource(FormFile checkerSource) {
        this.checkerSource = checkerSource;
    }

    public void populate(Problem problem, AbstractContest contest) {
        this.contestId = String.valueOf(problem.getContestId());
        this.problemId = String.valueOf(problem.getId());
        this.name = problem.getTitle();
        this.code = problem.getCode();
        this.author = problem.getAuthor();
        this.source = problem.getSource();
        this.contest = problem.getContest();
        this.specialJudge = problem.isChecker();
        Limit limit = problem.getLimit();
        this.useContestDefault = limit.getId() == contest.getLimit().getId();
        this.timeLimit = String.valueOf(limit.getTimeLimit());
        this.memoryLimit = String.valueOf(limit.getMemoryLimit());
        this.submissionLimit = String.valueOf(limit.getSubmissionLimit());
        this.outputLimit = String.valueOf(limit.getOutputLimit());
        this.color = problem.getColor();

    }

    public Problem toProblem() throws ParseException, NumberFormatException, PersistenceException {
        Problem problem = new Problem();

        if (this.problemId != null) {
            problem.setId(Long.parseLong(this.problemId));
        }
        if (this.contestId != null) {
            problem.setContestId(Long.parseLong(this.contestId));
        }

        problem.setTitle(this.name);
        problem.setCode(this.code);
        problem.setAuthor(this.author);
        problem.setSource(this.source);
        problem.setContest(this.contest);

        Limit limit = new Limit();
        if (!this.useContestDefault) {
            limit.setTimeLimit(Integer.parseInt(this.timeLimit));
            limit.setMemoryLimit(Integer.parseInt(this.memoryLimit));
            limit.setSubmissionLimit(Integer.parseInt(this.submissionLimit));
            limit.setOutputLimit(Integer.parseInt(this.outputLimit));
        }
        problem.setLimit(limit);
        problem.setChecker(this.specialJudge);

        problem.setColor(this.color);
        return problem;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
