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

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.form.ResetPasswordForm;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * ResetPasswordAction
 * </p>
 * 
 * 
 * @author Zhang, Zheng
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
            return this.handleSuccess(mapping, context, "message");

        }

        if (passwordForm.getPassword() == null) {
            return this.handleSuccess(mapping, context, "failure");
        }

        user.setPassword(passwordForm.getPassword());
        userPersistence.updateUserProfile(user, user.getId());
        userPersistence.deleteConfirmCode(user.getId(), user.getId());

        ActionMessages messages = new ActionMessages();
        messages.add("message", new ActionMessage("onlinejudge.resetPassword.success"));
        this.saveErrors(context.getRequest(), messages);

        return this.handleSuccess(mapping, context, "message");

    }

}
