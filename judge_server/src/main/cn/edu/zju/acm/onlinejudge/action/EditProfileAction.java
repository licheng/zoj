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
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.util.PasswordManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * EditProfile Action.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class EditProfileAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public EditProfileAction() {
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
    	if (!isLogin(context)) {   
    		return handleSuccess(mapping, context, "login");    		    		
    	}
    	UserPersistence userPersistence = PersistenceManager.getInstance().getUserPersistence();
    	ProfileForm profileForm = (ProfileForm) form;    
    	UserProfile profile = context.getUserProfile();
    	if (profileForm.getHandle() == null) {  
    		profileForm.populate(profile);
    		context.setAttribute("ProfileForm", profileForm);
    		return handleSuccess(mapping, context, "failure");
    	}  
    	
    	if (userPersistence.login(profileForm.getHandle(), profileForm.getPassword()) == null) {
    		return handleFailure(mapping, context, "password", "ProfileForm.password.invalid");  
    	}
    	
    	
    	UserProfile newProfile = profileForm.toUserProfile();
    	newProfile.setId(profile.getId());
    	newProfile.setRegDate(profile.getRegDate());
    	if (newProfile.getPassword() == null || newProfile.getPassword().length() == 0) {
    		newProfile.setPassword(profile.getPassword());
    	}
    	
    	if (!profile.getHandle().equals(newProfile.getHandle())) {
    		return handleFailure(mapping, context, "handle", "ProfileForm.handle.changed");
    	}
    	
    	if (!profile.getEmail().equals(newProfile.getEmail())) {
    		UserProfile temp = userPersistence.getUserProfileByEmail(newProfile.getEmail());
    		if (temp != null && temp.getId() != profile.getId()) {
    			return handleFailure(mapping, context, "email", "ProfileForm.email.used");
    		}
    	}
    	    	    	    	    	    	    	    	
    	userPersistence.updateUserProfile(newProfile, profile.getId());
    	
    	context.setUserProfile(newProfile);
        context.getRequest().setAttribute("Countries", 
                PersistenceManager.getInstance().getUserPersistence().getAllCountries());
    	
    	
    	ActionMessages messages = new ActionMessages();       
        messages.add("message", new ActionMessage("onlinejudge.editProfile.success"));
        this.saveErrors(context.getRequest(), messages);
        context.setAttribute("back", "");  
        
    	return handleSuccess(mapping, context, "success");    	 
    }    
        


}
    