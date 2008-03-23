/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.form.ContestForm;
import cn.edu.zju.acm.onlinejudge.form.ProblemForm;
import cn.edu.zju.acm.onlinejudge.form.ProfileForm;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.ProblemManager;

/**
 * <p>
 * EditProblemAction.
 * </p>
 * 
 * 
 * @author ZOJDEV
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
     * @param mapping action mapping
     * @param form action form
     * @param request http sevelet request
     * @param response http sevelet response
     *
     * @return action forward instance
     *
     * @throws Exception any errors happened
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
        // check contest
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("editProblem.do");
                
        ActionForward forward = checkProblemAdminPermission(mapping, context, isProblemset);
        if (forward != null) {
            return forward;
        }

        ProblemForm problemForm = (ProblemForm) form;     
        if (problemForm == null || problemForm.getName() == null) {    
            Problem problem = context.getProblem();
            problemForm.populate(problem, context.getContest());
            setReference("DescriptionRef", ReferenceType.DESCRIPTION, problem.getId(), context);
            setReference("InputRef", ReferenceType.INPUT, problem.getId(), context);
            setReference("OutputRef", ReferenceType.OUTPUT, problem.getId(), context);
            setReference("JudgeSolutionRef", ReferenceType.JUDGE_SOLUTION, problem.getId(), context);
            setReference("CheckerRef", ReferenceType.CHECKER, problem.getId(), context);
            setReference("CheckerSourceRef", ReferenceType.CHECKER_SOURCE, problem.getId(), context);
            return handleSuccess(mapping, context, "failure");
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
        problemPersistence.updateProblem(problem, userId);       
        
        // cprete problem reference, i.e. text, input, output, checker, judge solution, checker source
        updateReference(ReferenceType.DESCRIPTION, problemForm.getDescription(), problem.getId(), userId);
        updateReference(ReferenceType.INPUT, problemForm.getInputData(), problem.getId(), userId);
        updateReference(ReferenceType.OUTPUT, problemForm.getOutputData(), problem.getId(), userId);
        updateReference(ReferenceType.CHECKER, problemForm.getChecker(), problem.getId(), userId);
        updateReference(ReferenceType.CHECKER_SOURCE, problemForm.getCheckerSource(), problem.getId(), userId);
        updateReference(ReferenceType.JUDGE_SOLUTION, problemForm.getJudgeSolution(), problem.getId(), userId);
        
        
        ContestManager.getInstance().refreshProblem(problem);

        return handleSuccess(mapping, context, "success", "?contestId=" + contest.getId());                     
    }    
    
    private void setReference(String typeKey, ReferenceType type, long problemId, ContextAdapter context) 
        throws Exception {
        ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
        List references = referencePersistence.getProblemReferenceInfo(problemId, type);
        if (references.size() > 0) {
            System.out.println(references.size());
            context.setAttribute(typeKey, references.get(0));
        }
    }
    
    private void updateReference(ReferenceType type, FormFile formFile, long problemId, long user) throws Exception {
        /*
        System.out.println(type.getDescription());
        if (formFile == null) {
            System.out.println("null");
        } else {
            System.out.println(formFile.getFileName());
            System.out.println(formFile.getFileSize());
        }
        */
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
        List references = referencePersistence.getProblemReferenceInfo(problemId, type);        
        if (references.size() == 0) {
            Reference ref = new Reference();
            ref.setContent(data);
            ref.setContentType(contentType);
            ref.setReferenceType(type);
            ref.setSize(data.length);
            referencePersistence.createProblemReference(problemId, ref, user);
        } else {
            Reference ref = (Reference) references.get(0);
            ref.setContent(data);
            ref.setContentType(contentType);
            ref.setSize(data.length);            
            referencePersistence.updateReference(ref, user);
        }
    }
    
    /**
     * Validates the form.
     *
     * @param mapping the action mapping.
     * @param request the user request.
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
         
         
         List problems = ContestManager.getInstance().getContestProblems(context.getContest().getId());
         for (Object obj : problems) {
             Problem p = (Problem) obj;
             if (!form.getProblemId().equals("" + p.getId()) && p.getTitle().equals(name)) {
                 errors.add("name", new ActionMessage("ProblemForm.name.used")); 
                 break;
             }
         }
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
    