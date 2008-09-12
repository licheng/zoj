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
import cn.edu.zju.acm.onlinejudge.form.ProfileForm;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.util.Features;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * EditProfile Action.
 * </p>
 * 
 * 
 * @author Zhang, Zheng
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
        if (!Features.editProfile()) {
            context.getResponse().sendError(404);
            return null;
        }

        if (!this.isLogin(context)) {
            return this.handleSuccess(mapping, context, "login");
        }
        UserPersistence userPersistence = PersistenceManager.getInstance().getUserPersistence();
        ProfileForm profileForm = (ProfileForm) form;
        UserProfile profile = context.getUserProfile();
        if (profileForm.getHandle() == null) {
            profileForm.populate(profile);
            context.setAttribute("ProfileForm", profileForm);
            return this.handleSuccess(mapping, context, "failure");
        }

        if (userPersistence.login(profileForm.getHandle(), profileForm.getPassword()) == null) {
            return this.handleFailure(mapping, context, "password", "ProfileForm.password.invalid");
        }

        UserProfile newProfile = profileForm.toUserProfile();
        newProfile.setId(profile.getId());
        newProfile.setRegDate(profile.getRegDate());
        System.out.println("cerror: " + newProfile.getPassword());

        if (!profile.getHandle().equals(newProfile.getHandle())) {
            return this.handleFailure(mapping, context, "handle", "ProfileForm.handle.changed");
        }

        if (!profile.getEmail().equals(newProfile.getEmail())) {
            UserProfile temp = userPersistence.getUserProfileByEmail(newProfile.getEmail());
            if (temp != null && temp.getId() != profile.getId()) {
                return this.handleFailure(mapping, context, "email", "ProfileForm.email.used");
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

        return this.handleSuccess(mapping, context, "success");
    }

}
