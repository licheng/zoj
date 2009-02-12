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

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;
import cn.edu.zju.acm.onlinejudge.util.EmailService;
import cn.edu.zju.acm.onlinejudge.util.Features;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.RandomStringGenerator;

/**
 * <p>
 * Forgot Action.
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ForgotPasswordAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ForgotPasswordAction() {
    // empty
    }

    /**
     * Login.
     * 
     * <pre>
     * </pre>
     * 
     * @param mapping
     *            action mapping
     * @param form
     *            action form
     * @param request
     *            http servlet request
     * @param response
     *            http servlet response
     * 
     * @return action forward instance
     * 
     * @throws Exception
     *             any errors happened
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
        if (!Features.forgotPassword()) {
            context.getResponse().sendError(404);
            return null;
        }
        String handle = context.getRequest().getParameter("handle");
        String email = context.getRequest().getParameter("email");
        if ((handle == null || handle.trim().length() == 0) && (email == null || email.trim().length() == 0)) {
        	return this.handleSuccess(mapping, context, "success");
        }
        UserPersistence userPersistence = PersistenceManager.getInstance().getUserPersistence();
        UserProfile user = null;
        
        
        if (handle != null && handle.trim().length() > 0) {
        	user = userPersistence.getUserProfileByHandle(handle);
        }
        if (user == null) {
	        if (email != null && email.trim().length() > 0) {
	            user = userPersistence.getUserProfileByEmail(email.trim());
	        }
        }
        
        if (user != null) {
        	forgotPassword(user, context);
        	context.setAttribute("ok", Boolean.TRUE);
        } else {
        	context.setAttribute("ok", Boolean.FALSE);
        }
        context.setAttribute("email", email);
        context.setAttribute("handle", handle);
        return this.handleSuccess(mapping, context, "success");

        
    }

    public void forgotPassword(UserProfile user, ContextAdapter context) throws Exception {
    	UserPersistence userPersistence = PersistenceManager.getInstance().getUserPersistence();
        String code = RandomStringGenerator.generate();
        userPersistence.createConfirmCode(user.getId(), code, user.getId());

        String url =
                ConfigManager.getValue("home_url") + context.getRequest().getContextPath() + "/resetPassword.do?code=" +
                    code;
        EmailService.sendPasswordEmail(user, url);
    }

}
