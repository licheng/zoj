/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.form.ProfileForm;
import cn.edu.zju.acm.onlinejudge.form.ResetPasswordForm;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.util.PasswordManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * ResetPasswordAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ResetPasswordAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ResetPasswordAction() {
        // empty
    }

    /**
     * Edit Profile.
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
    	
    	ResetPasswordForm passwordForm = (ResetPasswordForm) form;
    	String code = passwordForm.getCode();
    	
    	UserPersistence userPersistence = PersistenceManager.getInstance().getUserPersistence();
    	UserProfile user = null;
    	if (code != null && code.trim().length() > 0) {
    		user = userPersistence.getUserProfileByCode(code);
    	}
    	 
    	if (user == null) {
    		ActionMessages messages = new ActionMessages();       
            messages.add("message", new ActionMessage("onlinejudge.resetPassword.invalidCode"));
            this.saveErrors(context.getRequest(), messages);
    		return handleSuccess(mapping, context, "message");
    		
    	}
    	
    	if (passwordForm.getPassword() == null) {
    		return handleSuccess(mapping, context, "failure");
    	} 
    	
    	user.setPassword(passwordForm.getPassword());
    	userPersistence.updateUserProfile(user, user.getId());
    	userPersistence.deleteConfirmCode(user.getId(), user.getId());
    	
    	ActionMessages messages = new ActionMessages();       
        messages.add("message", new ActionMessage("onlinejudge.resetPassword.success"));
        this.saveErrors(context.getRequest(), messages);
        
    	return handleSuccess(mapping, context, "message");
    	    	    	 
    }    
        
}
    