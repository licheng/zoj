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
import cn.edu.zju.acm.onlinejudge.form.ProfileForm;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
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
public class DeleteContestAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public DeleteContestAction() {
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
        
        // check contest
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("deleteProblemset.do");
                
        ActionForward forward = checkContestAdminPermission(mapping, context, isProblemset, false);
        if (forward != null) {
            return forward;
        }
        
        
        AbstractContest contest = context.getContest();
        ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
        contestPersistence.deleteContest(contest.getId(), context.getUserSecurity().getId());
        
        ContestManager.getInstance().refreshContest(contest.getId());
        
        ActionMessages messages = new ActionMessages();
        if (isProblemset) {                                        
            messages.add("message", new ActionMessage("onlinejudge.deleteProblemset.success"));            
        } else {
            messages.add("message", new ActionMessage("onlinejudge.deleteContest.success"));            
        }
        this.saveErrors(context.getRequest(), messages);
        context.setAttribute("back", isProblemset ? "showProblemsets.do" : "showContests.do");           
        return handleSuccess(mapping, context, "success");
    }    

}
    