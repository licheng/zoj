/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;



import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.form.ContestForm;
import cn.edu.zju.acm.onlinejudge.form.RoleForm;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
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
 * @author ZOJDEV
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
        
        // check admin
        ActionForward forward = checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }
        
        RoleForm roleForm = (RoleForm) form;
        AuthorizationPersistence authorizationPersistence 
            = PersistenceManager.getInstance().getAuthorizationPersistence();
    
        if (roleForm.getId() == null || roleForm.getId().trim().length() == 0) {
            long roleId = Utility.parseLong(context.getRequest().getParameter("roleId"));            
            RoleSecurity role = authorizationPersistence.getRole(roleId);
            if (role == null) {
                return handleSuccess(mapping, context, "success");
            }
            
            // add contest names
            Map<Long, String> contestNames = new TreeMap<Long, String>();
            for (AbstractContest contest : ContestManager.getInstance().getAllContests()) {
                contestNames.put(contest.getId(), contest.getTitle());
            }
            for (AbstractContest contest : ContestManager.getInstance().getAllProblemsets()) {
                contestNames.put(contest.getId(), contest.getTitle());
            }
            context.setAttribute("ContestNames", contestNames);
            
            // TODO add forums
            Map<Long, String> forumNames = new TreeMap<Long, String>();
            forumNames.put(1L, "ZOJ Forum");
            context.setAttribute("ForumNames", forumNames);
                                    
            roleForm.populate(role);
            return handleSuccess(mapping, context, "failure");
        }
        
        RoleSecurity role = roleForm.toRole();        
        authorizationPersistence.updateRole(role, context.getUserProfile().getId());
        
        if (role.getId() == 1) {
            ContextAdapter.resetDefaultUserSecurity();
        }
        return handleSuccess(mapping, context, "success");   	    	    	    	
    }    
        
}
    