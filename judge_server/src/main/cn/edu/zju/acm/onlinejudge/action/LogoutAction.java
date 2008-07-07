/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import javax.servlet.http.Cookie;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <p>
 * Login Action.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class LogoutAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public LogoutAction() {
        // empty
    }

    /**
     * Log out.
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
      
    	
    	Cookie ch = new Cookie("oj_handle", "");
		ch.setMaxAge(0);
		ch.setPath("/");
		context.getResponse().addCookie(ch);
		 
		Cookie cp = new Cookie("oj_password", "");
		cp.setMaxAge(0);
		cp.setPath("/");
		context.getResponse().addCookie(cp);
		 
    	context.getRequest().getSession().invalidate();      
      
    	return handleSuccess(mapping, context, "success");    	    	    	 		    
    }    
    
}
    