/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <p>
 * Contest Info Action.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ContestInfoAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ContestInfoAction() {
        // empty
    }

    /**
     * Show Contest Info.
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
    	
    	// check contest
    	boolean isProblemset = context.getRequest().getRequestURI().endsWith("problemsetInfo.do");
    	
    	ActionForward forward = checkContestViewPermission(mapping, context, isProblemset, false);
    	if (forward != null) {
    		return forward;
    	}    	    	
               
    	return handleSuccess(mapping, context, "success");    	    	    	
    }    

}
    