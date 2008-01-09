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
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.form.ContestForm;
import cn.edu.zju.acm.onlinejudge.form.ProfileForm;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * Register Action.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class CreateContestAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public CreateContestAction() {
        // empty
    }

    /**
     * Register.
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
        
        ActionForward forward = checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }
        
        ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
        ContestForm contestForm = (ContestForm) form;    	    	
    	if (contestForm == null || contestForm.getId() == null) {
        	return handleSuccess(mapping, context, "failure");
    	}    	 
        
        context.setAttribute("ContestForm", contestForm);
    	
        // create user profile
    	AbstractContest contest = contestForm.toContest();
        
        contestPersistence.createContest(contest, context.getUserProfile().getId());
        ContestManager.getInstance().refreshContest(contest.getId());
        
        ActionMessages messages = new ActionMessages();
        messages.add("message", new ActionMessage("onlinejudge.createContest.success"));
        this.saveErrors(context.getRequest(), messages);
        context.setAttribute("back", "manageContests.do");
        
    	return handleSuccess(mapping, context, "success");    	    	    	
    }    
        

}
    