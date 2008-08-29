/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.util.Date;

import javax.servlet.http.Cookie;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.UserPreference;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.form.LoginForm;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.PasswordManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * Login Action.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class LoginAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public LoginAction() {
        // empty
    }

    /**
     * Login.
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
    		
    	LoginForm loginForm = (LoginForm) form;    	
    	String forwardPath = context.getRequest().getParameter("forward");

    	if (loginForm.getHandle() == null) {
    		context.setAttribute("forward", forwardPath);
    		return handleSuccess(mapping, context, "failure");
    	}    	    	
    	
    	ActionMessages errors = authenticate(loginForm, context);
    	if (errors.size() > 0) {
    		context.setAttribute("forward", forwardPath);
    		return handleFailure(mapping, context, errors);
    	}    	     	
    	
    	if (loginForm.isRememberMe()) {
    		 Cookie ch = new Cookie("oj_handle", loginForm.getHandle());
    		 ch.setMaxAge(3600 * 24 * 30);
    		 ch.setPath("/");
    		 context.getResponse().addCookie(ch);
    		 
    		 Cookie cp = new Cookie("oj_password", loginForm.getPassword());
    		 cp.setMaxAge(3600 * 24 * 30);
    		 cp.setPath("/");
    		 context.getResponse().addCookie(cp);
    	} 
    	
    	
    	
    	// record login info;
    	PersistenceManager.getInstance().getUserPersistence().updateLastLoginInfo(
    			context.getUserProfile().getId(), new Date(), context.getRequest().getRemoteHost());
    	
    	if (forwardPath != null) {    		    		
    		return handleSuccess(new ActionForward(forwardPath, true), context, forwardPath);
    	}
    	return handleSuccess(mapping, context, "success");    	    	    	
    }    
    
    /**
     * Authenticate.
     * 
     * @param form
     * @return
     * @throws Exception
     */
    private ActionMessages authenticate(LoginForm form, ContextAdapter context) throws PersistenceException {
        context.getRequest().getSession().invalidate();
    	ActionMessages errors = new ActionMessages();
    	UserPersistence userPersistence = PersistenceManager.getInstance().getUserPersistence();
    	UserProfile profile = userPersistence.login(form.getHandle(), form.getPassword());
    	
    	// no such user
    	if (profile == null) {
    		errors.add("password", new ActionMessage("LoginForm.password.invalid"));
    		return errors;
    	}
    	
    	// deactivated
    	if (!profile.isActive()) {
    		errors.add("password", new ActionMessage("LoginForm.password.deactivated"));
    		return errors;
    	}
    	
    	AuthorizationPersistence authorizationPersistence 
    		= PersistenceManager.getInstance().getAuthorizationPersistence();
    	
    	// get UserSecurity
    	UserSecurity security = authorizationPersistence.getUserSecurity(profile.getId());
    	
    	// get UserPreference
    	UserPreference perference = userPersistence.getUserPreference(profile.getId());
    	
    	context.setUserProfile(profile);
    	context.setUserSecurity(security);
    	context.setUserPreference(perference);
    	    	
    	return errors;

    }    


}
    