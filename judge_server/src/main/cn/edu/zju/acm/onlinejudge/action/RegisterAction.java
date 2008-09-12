/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

package cn.edu.zju.acm.onlinejudge.action;


import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.UserPreference;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.form.ProfileForm;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.Features;
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
public class RegisterAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public RegisterAction() {
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
    	if (!Features.register()) {
    		context.getResponse().sendError(404);
            return null;
    	}
    	UserPersistence userPersistence = PersistenceManager.getInstance().getUserPersistence();
    	
    	ProfileForm profileForm = (ProfileForm) form;    	    	
    	if (profileForm.getHandle() == null) {    		    		
    		return handleSuccess(mapping, context, "failure");
    	}    	    	
        context.getRequest().getSession().invalidate();
    	ActionMessages errors = this.validate(userPersistence, profileForm);
    	
    	if (errors.size() > 0) {
    		return handleFailure(mapping, context, errors);   
    	}
    	// create user profile
    	UserProfile profile = profileForm.toUserProfile();
    	userPersistence.createUserProfile(profile, 0); 
    	
    	// create user perference
    	UserPreference perference = new UserPreference();
    	perference.setId(profile.getId());
    	perference.setPlan("");
    	perference.setPostPaging(20); // TODO...
    	perference.setProblemPaging(100);
    	perference.setStatusPaging(20);
    	perference.setThreadPaging(20);
    	perference.setUserPaging(20);
    	userPersistence.createUserPreference(perference, 0);
        
        AuthorizationPersistence authorizationPersistence 
            = PersistenceManager.getInstance().getAuthorizationPersistence();       
        authorizationPersistence.addUserRole(profile.getId(), 2);
    	        
        context.getRequest().setAttribute("Countries", 
                PersistenceManager.getInstance().getUserPersistence().getAllCountries());
    	
    	// get UserSecurity
		UserSecurity security = authorizationPersistence.getUserSecurity(profile.getId());        
        		    	
		context.setUserProfile(profile);
		context.setUserSecurity(security);
		context.setUserPreference(perference);
	
    	ActionMessages messages = new ActionMessages();       
        messages.add("message", new ActionMessage("onlinejudge.register.success"));
        this.saveErrors(context.getRequest(), messages);
        context.setAttribute("back", "");         
        
    	return handleSuccess(mapping, context, "success");    	    	    	
    }    
        

    /**
     * Further validation.
     * 
     * @param userPersistence
     * @param form
     * @return
     * @throws PersistenceException
     */
    private ActionMessages validate(UserPersistence userPersistence, ProfileForm form) throws PersistenceException {
    	ActionMessages errors = new ActionMessages();
    	
    	if (userPersistence.getUserProfileByHandle(form.getHandle()) != null) {
    		errors.add("handle", new ActionMessage("ProfileForm.handle.used"));
    	}
    	
    	if (userPersistence.getUserProfileByEmail(form.getEmail()) != null) {
    		errors.add("email", new ActionMessage("ProfileForm.email.used"));
    	}
    	
    	return errors;
    }
}
    