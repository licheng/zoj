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

import java.util.Map;
import java.util.TreeMap;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.form.RoleForm;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * Edit Role Action.
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class EditRoleAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public EditRoleAction() {
    // empty
    }

    /**
     * Edit Role.
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

        // check admin
        ActionForward forward = this.checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }

        RoleForm roleForm = (RoleForm) form;
        AuthorizationPersistence authorizationPersistence =
                PersistenceManager.getInstance().getAuthorizationPersistence();

        if (roleForm.getId() == null || roleForm.getId().trim().length() == 0) {
            long roleId = Utility.parseLong(context.getRequest().getParameter("roleId"));
            RoleSecurity role = authorizationPersistence.getRole(roleId);
            if (role == null) {
                return this.handleSuccess(mapping, context, "success");
            }

            // add contest names
            Map<Long, String> contestNames = new TreeMap<Long, String>();
            for (AbstractContest contest : ContestManager.getInstance().getAllContests()) {
                contestNames.put(contest.getId(), contest.getTitle());
            }
            for (AbstractContest contest : ContestManager.getInstance().getAllProblemsets()) {
                contestNames.put(contest.getId(), contest.getTitle());
            }
            for (AbstractContest contest : ContestManager.getInstance().getAllCourses()) {
                contestNames.put(contest.getId(), contest.getTitle());
            }
            context.setAttribute("ContestNames", contestNames);

            // TODO add forums
            Map<Long, String> forumNames = new TreeMap<Long, String>();
            forumNames.put(1L, "ZOJ Forum");
            context.setAttribute("ForumNames", forumNames);

            roleForm.populate(role);
            return this.handleSuccess(mapping, context, "failure");
        }

        RoleSecurity role = roleForm.toRole();
        authorizationPersistence.updateRole(role, context.getUserProfile().getId());

        if (role.getId() == 1) {
            ContextAdapter.resetDefaultUserSecurity();
        }
        return this.handleSuccess(mapping, context, "success");
    }

}
