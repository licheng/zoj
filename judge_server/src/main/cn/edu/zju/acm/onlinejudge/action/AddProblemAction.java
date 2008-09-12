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
 * AddProblemAction.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class AddProblemAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public AddProblemAction() {
        // empty
    }

    /**
     * AddProblemAction.
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
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
        // check contest
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("addProblem.do");

        ActionForward forward = checkContestAdminPermission(mapping, context, isProblemset, false);
        if (forward != null) {
            return forward;
        }

        ProblemForm problemForm = (ProblemForm) form;
        if (problemForm == null || problemForm.getProblemId() == null) {
            return handleSuccess(mapping, context);
        }

        // check title and code
        ActionMessages errors = validate(problemForm, context);
        if (errors.size() > 0) {
            return handleFailure(mapping, context, errors);
        }

        ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();
        AbstractContest contest = context.getContest();

        Problem problem = problemForm.toProblem();
        if (problemForm.isUseContestDefault()) {
            problem.getLimit().setId(contest.getLimit().getId());
        }

        long userId = context.getUserSecurity().getId();
        // create problem
        problemPersistence.createProblem(problem, userId);

        // cprete problem reference, i.e. text, input, output, checker, judge solution, checker source
        createReference(ReferenceType.DESCRIPTION, problemForm.getDescription(), problem.getId(), userId);
        createReference(ReferenceType.INPUT, problemForm.getInputData(), problem.getId(), userId);
        createReference(ReferenceType.OUTPUT, problemForm.getOutputData(), problem.getId(), userId);
        createReference(ReferenceType.CHECKER, problemForm.getChecker(), problem.getId(), userId);
        createReference(ReferenceType.CHECKER_SOURCE, problemForm.getCheckerSource(), problem.getId(), userId);
        createReference(ReferenceType.JUDGE_SOLUTION, problemForm.getJudgeSolution(), problem.getId(), userId);

        ContestManager.getInstance().refreshContest(problem.getContestId());

        return handleSuccess(mapping, context, "success", "?contestId=" + contest.getId());
    }

    private void createReference(ReferenceType type, FormFile formFile, long problemId, long user) throws Exception {
        if (formFile == null) {
            return;
        }

        ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
        byte[] data = formFile.getFileData();
        if (data.length == 0) {
            return;
        }
        Reference ref = new Reference();
        ref.setContent(data);
        ref.setReferenceType(type);
        ref.setSize(data.length);

        referencePersistence.createProblemReference(problemId, ref, user);
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

        checkInteger(form.getProblemId(), 0, Integer.MAX_VALUE, "id", errors);
        String name = form.getName();
        String code = form.getCode();
        if ((name == null) || (name.trim().length() == 0)) {
            errors.add("name", new ActionMessage("ProblemForm.name.required"));
        }

        if ((code == null) || (code.trim().length() == 0)) {
            errors.add("code", new ActionMessage("ProblemForm.code.required"));
        }
        // TODO check code

        if (!form.isUseContestDefault()) {
            checkInteger(form.getTimeLimit(), 0, 3600, "timeLimit", errors);
            checkInteger(form.getMemoryLimit(), 0, 1024 * 1024, "memoryLimit", errors);
            checkInteger(form.getOutputLimit(), 0, 100 * 1024, "outputLimit", errors);
            checkInteger(form.getSubmissionLimit(), 0, 10 * 1024, "submissionLimit", errors);
        }

        List<Problem> problems = ContestManager.getInstance().getContestProblems(context.getContest().getId());
        for (Object problem : problems) {
            if (((Problem) problem).getTitle().equals(name)) {
                errors.add("name", new ActionMessage("ProblemForm.name.used"));
                break;
            }
        }
        for (Object problem : problems) {
            if (((Problem) problem).getCode().equals(code)) {
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
        if ((value == null) || (value.trim().length() == 0)) {
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
