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

import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * Delete Contest Action.
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class DeleteRoleAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public DeleteRoleAction() {
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
                     
        // check admin
        ActionForward forward = checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }
        
        long roleId = Utility.parseLong(context.getRequest().getParameter("roleId"));
        AuthorizationPersistence ap = PersistenceManager.getInstance().getAuthorizationPersistence();
        ap.deleteRole(roleId, context.getUserProfile().getId());
        
        return handleSuccess(mapping, context, "success");
    }    

}
    