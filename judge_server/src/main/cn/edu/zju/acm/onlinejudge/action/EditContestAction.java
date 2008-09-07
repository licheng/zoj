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
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.form.ContestForm;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * Edit Contest Action.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class EditContestAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public EditContestAction() {
        // empty
    }

    /**
     * Edit Contest.
     * <pre>
     * </pre>
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
        
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("editProblemset.do");
        
        ActionForward forward = checkContestAdminPermission(mapping, context, isProblemset, false);
        if (forward != null) {
            return forward;
        }
        
        ContestForm contestForm = (ContestForm) form;
               
        if (contestForm.getId() == null) {
            AbstractContest contest = context.getContest();            
            contestForm.populate(contest);            
            return handleSuccess(mapping, context);
        } else {                
            ContestPersistence persistence = PersistenceManager.getInstance().getContestPersistence();
            AbstractContest contest = contestForm.toContest();
            persistence.updateContest(contest, context.getUserSecurity().getId());
            ContestManager.getInstance().refreshContest(contest.getId());
            
            ActionMessages messages = new ActionMessages();       
            messages.add("message", new ActionMessage("onlinejudge.editContest.success"));
            this.saveErrors(context.getRequest(), messages);
            context.setAttribute("back", 
                    (isProblemset ? "problemsetInfo.do" : "contestInfo.do") + "?contestId=" + contest.getId());   
            
            return handleSuccess(mapping, context, "success");
        }                          	    	    	    
    }            
}
    