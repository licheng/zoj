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

package cn.edu.zju.acm.onlinejudge.action;

import java.util.List;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.form.ProblemForm;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * EditProblemAction.
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class EditProblemAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public EditProblemAction() {
    // empty
    }

    /**
     * EditProblemAction.
     * 
     * @param mapping
     *            action mapping
     * @param form
     *            action form
     * @param request
     *            http servlet request
     * @param response
     *            http servlet response
     * 
     * @return action forward instance
     * 
     * @throws Exception
     *             any errors happened
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
        // check contest
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("editProblem.do");

        ActionForward forward = this.checkProblemAdminPermission(mapping, context, isProblemset);
        if (forward != null) {
            return forward;
        }

        ProblemForm problemForm = (ProblemForm) form;
        if (problemForm == null || problemForm.getName() == null) {
            Problem problem = context.getProblem();
            problemForm.populate(problem, context.getContest());
            this.setReference("DescriptionRef", ReferenceType.DESCRIPTION, problem.getId(), context);
            this.setReference("InputRef", ReferenceType.INPUT, problem.getId(), context);
            this.setReference("OutputRef", ReferenceType.OUTPUT, problem.getId(), context);
            this.setReference("JudgeSolutionRef", ReferenceType.JUDGE_SOLUTION, problem.getId(), context);
            this.setReference("CheckerRef", ReferenceType.CHECKER, problem.getId(), context);
            this.setReference("CheckerSourceRef", ReferenceType.CHECKER_SOURCE, problem.getId(), context);
            return this.handleSuccess(mapping, context, "failure");
        }

        // check title and code
        ActionMessages errors = this.validate(problemForm, context);
        if (errors.size() > 0) {
            return this.handleFailure(mapping, context, errors);
        }

        ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();
        AbstractContest contest = context.getContest();

        Problem problem = problemForm.toProblem();
        if (problemForm.isUseContestDefault()) {
            problem.getLimit().setId(contest.getLimit().getId());
        }

        long userId = context.getUserSecurity().getId();
        // create problem
        problemPersistence.updateProblem(problem, userId);

        // cprete problem reference, i.e. text, input, output, checker, judge solution, checker source
        this.updateReference(ReferenceType.DESCRIPTION, problemForm.getDescription(), problem.getId(), userId);
        this.updateReference(ReferenceType.INPUT, problemForm.getInputData(), problem.getId(), userId);
        this.updateReference(ReferenceType.OUTPUT, problemForm.getOutputData(), problem.getId(), userId);
        this.updateReference(ReferenceType.CHECKER, problemForm.getChecker(), problem.getId(), userId);
        this.updateReference(ReferenceType.CHECKER_SOURCE, problemForm.getCheckerSource(), problem.getId(), userId);
        this.updateReference(ReferenceType.JUDGE_SOLUTION, problemForm.getJudgeSolution(), problem.getId(), userId);

        ContestManager.getInstance().refreshProblem(problem);

        return this.handleSuccess(mapping, context, "success", "?contestId=" + contest.getId());
    }

    private void setReference(String typeKey, ReferenceType type, long problemId, ContextAdapter context) throws Exception {
        ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
        List<Reference> references = referencePersistence.getProblemReferenceInfo(problemId, type);
        if (references.size() > 0) {
            context.setAttribute(typeKey, references.get(0));
        }
    }

    private void updateReference(ReferenceType type, FormFile formFile, long problemId, long user) throws Exception {

    	if (formFile == null || formFile.getFileName() == null || formFile.getFileName().trim().length() == 0) {
            return;
        }

        String name = formFile.getFileName();
        String contentType = null;
        int p = name.lastIndexOf('.');
        if (p != -1) {
            contentType = name.substring(p + 1);
        }

        byte[] data = formFile.getFileData();

        ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
        List<Reference> references = referencePersistence.getProblemReferenceInfo(problemId, type);
        if (references.size() == 0) {
            Reference ref = new Reference();
            ref.setContent(data);
            ref.setContentType(contentType);
            ref.setReferenceType(type);
            ref.setSize(data.length);
            referencePersistence.createProblemReference(problemId, ref, user);
        } else {
            Reference ref = references.get(0);
            ref.setContent(data);
            ref.setContentType(contentType);
            ref.setSize(data.length);
            referencePersistence.updateReference(ref, user);
        }
    }

    /**
     * Validates the form.
     * 
     * @param mapping
     *            the action mapping.
     * @param request
     *            the user request.
     * 
     * @return collection of validation errors.
     */
    public ActionErrors validate(ProblemForm form, ContextAdapter context) throws Exception {

        ActionErrors errors = new ActionErrors();

        this.checkInteger(form.getProblemId(), 0, Integer.MAX_VALUE, "id", errors);
        String name = form.getName();
        String code = form.getCode();
        if (name == null || name.trim().length() == 0) {
            errors.add("name", new ActionMessage("ProblemForm.name.required"));
        }

        if (code == null || code.trim().length() == 0) {
            errors.add("code", new ActionMessage("ProblemForm.code.required"));
        }
        // TODO check code

        if (!form.isUseContestDefault()) {
            this.checkInteger(form.getTimeLimit(), 0, 3600, "timeLimit", errors);
            this.checkInteger(form.getMemoryLimit(), 0, 1024 * 1024, "memoryLimit", errors);
            this.checkInteger(form.getOutputLimit(), 0, 100 * 1024, "outputLimit", errors);
            this.checkInteger(form.getSubmissionLimit(), 0, 10 * 1024, "submissionLimit", errors);
        }

        List<Problem> problems = ContestManager.getInstance().getContestProblems(context.getContest().getId());
        /*for (Object obj : problems) {
            Problem p = (Problem) obj;
            if (!form.getProblemId().equals("" + p.getId()) && p.getTitle().equals(name)) {
                errors.add("name", new ActionMessage("ProblemForm.name.used"));
                break;
            }
        }*/
        for (Object obj : problems) {
            Problem p = (Problem) obj;
            if (!form.getProblemId().equals("" + p.getId()) && p.getCode().equals(code)) {
                errors.add("code", new ActionMessage("ProblemForm.code.used"));
                break;
            }
        }
        return errors;
    }

    /**
     * 
     * @param value
     * @param min
     * @param max
     * @param name
     * @param errors
     */
    private void checkInteger(String value, int min, int max, String name, ActionErrors errors) {
        if (value == null || value.trim().length() == 0) {
            errors.add(name, new ActionMessage("ProblemForm." + name + ".required"));
            return;
        }
        try {
            int intValue = Integer.parseInt(value);
            if (intValue < min || intValue > max) {
                errors.add(name, new ActionMessage("ProblemForm." + name + ".outrange"));
            }
        } catch (NumberFormatException e) {
            errors.add(name, new ActionMessage("ProblemForm." + name + ".invalid"));
        }
    }

}
