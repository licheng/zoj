/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;



import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class ManageRoleUsersAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ManageRoleUsersAction() {
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
        long roleId = Utility.parseLong(context.getRequest().getParameter("roleId"));
        RoleSecurity role = null;
        AuthorizationPersistence authorizationPersistence 
            = PersistenceManager.getInstance().getAuthorizationPersistence();
        if (roleId >= 0) {
            role = authorizationPersistence.getRole(roleId);
        }
        if (role == null) {            
            return handleSuccess(mapping, context, "failure");
        }
        context.setAttribute("importMessage", "");
        context.setAttribute("role", role);
        String users = context.getRequest().getParameter("users");
        if (users == null || users.trim().length() == 0) {
            return handleSuccess(mapping, context, "success");
        }
        List<String> userList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new StringReader(users));
        for (;;) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            if (line.trim().length() > 0) { 
                userList.add(line.trim());
            }
        }

        String operation = context.getRequest().getParameter("operation");        
        if ("remove".equalsIgnoreCase(operation)) {
            // TODO NOT SAFE HERE, Sql injection is possible.
            Map<String, Boolean> result = authorizationPersistence.removeRoleUsers(userList, roleId);            
            String message = generateResult(userList, result, true);
            context.setAttribute("importMessage", message);
        } else if ("add".equalsIgnoreCase(operation)) {
            // TODO NOT SAFE HERE, Sql injection is possible.
            Map<String, Boolean> result = authorizationPersistence.addRoleUsers(userList, roleId);            
            String message = generateResult(userList, result, false);
            context.setAttribute("importMessage", message);
        } 
        
        return handleSuccess(mapping, context, "success");
                    	    	    
    }    
        
    private String generateResult(List<String>userList, Map<String, Boolean> results, boolean remove) {
        List<String> nonexistingUsers = new ArrayList<String>();
        List<String> notUpdatedUsers = new ArrayList<String>();
        for (String user : userList) {
            Boolean result = results.get(user);
            if (result == null) {
                nonexistingUsers.add(user);                
            } else if (!result) {
                notUpdatedUsers.add(user);
            } 
            
        }
        StringBuilder sb = new StringBuilder();
        if (nonexistingUsers.size() != 0) {
            sb.append("<font color='red'>Invalid Users: " + nonexistingUsers.size() + "</font><br>\n");
        }
        if (notUpdatedUsers.size() != 0) {
            sb.append("<font color='green'>Not Updated Users: " + notUpdatedUsers.size() + "</font><br>\n");
        }
        sb.append("<font color='green'>Updated Users: " 
                + (userList.size() - notUpdatedUsers.size() - nonexistingUsers.size())
                + "</font><br>\n");
        
        if (nonexistingUsers.size() != 0) {
            sb.append("<br>\n");
            sb.append("<font color='red'><b>Following handles are invalid:</b></font><br>\n");
            for (String user : nonexistingUsers) {
                sb.append(user +"<br>\n");
            }
            
        }
        
        if (notUpdatedUsers.size() != 0) {
            sb.append("<br>\n");
            if (remove) {
                sb.append("<font color='green'><b>Following users don't belong to this role:</b></font><br>\n");
            } else {
                sb.append("<font color='green'><b>Following users already belong to this role:</b></font><br>\n");
            }
            for (String user : notUpdatedUsers) {
                sb.append(user +"<br>\n");
            }
        }
               
        return sb.toString();
    }
}
    