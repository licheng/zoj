/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * ShowProblemAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ShowProblemAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowProblemAction() {
        // empty
    }

    /**
     * ShowProblemAction.
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
    	boolean isProblemset = context.getRequest().getRequestURI().endsWith("showProblem.do");
    	
    	ActionForward forward = checkProblemViewPermission(mapping, context, isProblemset);
    	if (forward != null) {
    		return forward;
    	}    	    	
    	
    	
    	Problem problem = context.getProblem();
    	byte[] text = ContestManager.getInstance().getDescription(problem.getId());
    	    	
        //StringBuffer sb = new StringBuffer();        
        context.setAttribute("text", text);

        
        return handleSuccess(mapping, context, "success");
                  	    	   
    }         

}
    