/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.form.ProfileForm;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * Delete Contest Action.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class DeleteProblemAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public DeleteProblemAction() {
        // empty
    }

    /**
     * Register.
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request http servlet request
     * @param response http servlet response
     *
     * @return action forward instance
     *
     * @throws Exception any errors happened
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
        
        
        // check problem
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("deleteProblem.do");        
        ActionForward forward = checkProblemAdminPermission(mapping, context, isProblemset);
        if (forward != null) {
            return forward;
        }               
                
        Problem problem = context.getProblem();        
        ActionMessages messages = new ActionMessages();        
        ProblemPersistence problemPersistence = PersistenceManager.getInstance().getProblemPersistence();
        problemPersistence.deleteProblem(problem.getId(), context.getUserProfile().getId());
                
        messages.add("message", new ActionMessage("onlinejudge.deleteProblem.success"));
        
        this.saveErrors(context.getRequest(), messages);
        String back = isProblemset ? "showProblems" : "showContestProblems";
        context.setAttribute("back", back + ".do?contestId=" + context.getContest().getId());
        
        ContestManager.getInstance().refreshContest(context.getContest().getId());
        
        return handleSuccess(mapping, context, "success");
    }    

}
    