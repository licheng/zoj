/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;



import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.form.ContestForm;
import cn.edu.zju.acm.onlinejudge.form.LimitForm;
import cn.edu.zju.acm.onlinejudge.form.RoleForm;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * Edit Limit Action.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class EditLimitAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public EditLimitAction() {
        // empty
    }

    /**
     * Edit Role.
     * <pre>
     * </pre>
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
        
        // check admin
        ActionForward forward = checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }
        
        LimitForm limitForm = (LimitForm) form;
        
        if (limitForm.getId() == null || limitForm.getId().trim().length() == 0) {
            
            Limit limit = PersistenceManager.getInstance().getContestPersistence().getDefaultLimit();
            limitForm.setId("1");
            limitForm.setTimeLimit("" + limit.getTimeLimit());
            limitForm.setMemoryLimit("" + limit.getMemoryLimit());
            limitForm.setSubmissionLimit("" + limit.getSubmissionLimit());
            limitForm.setOutputLimit("" + limit.getOutputLimit());
            
            return handleSuccess(mapping, context, "failure");
        }
        
        Limit limit = new Limit();
        limit.setId(1);
        limit.setTimeLimit(Integer.parseInt(limitForm.getTimeLimit()));
        limit.setMemoryLimit(Integer.parseInt(limitForm.getMemoryLimit()));
        limit.setOutputLimit(Integer.parseInt(limitForm.getOutputLimit()));
        limit.setSubmissionLimit(Integer.parseInt(limitForm.getSubmissionLimit()));
                
        PersistenceManager.getInstance().getContestPersistence().updateDefaultLimit(limit);
        
        ActionMessages messages = new ActionMessages();       
        messages.add("success", new ActionMessage("onlinejudge.DefaultLimit.success"));
        this.saveErrors(context.getRequest(), messages);
                
        return handleSuccess(mapping, context, "success");   	    	    	    	
    }    
        
}
    