/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.form;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;
import cn.edu.zju.acm.onlinejudge.security.PermissionEntry;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.util.Utility;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * RoleForm.
 * </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class RoleForm extends ActionForm implements Serializable {

    /**
     * The id.
     */
    private String id = null;
    
    /**
     * The name.
     */
    private String name = null;

    /**
     * The description.
     */
    private String description = null;

    /**
     * The selectedContestIds.
     */
    private String[] selectedContestIds = null;
    
    /**
     * The contestPermissions.
     */
    private String[] contestPermissions = null;
    
    
    /**
     * The selectedForumIds.
     */
    private String[] selectedForumIds = null;
    
    /**
     * The forumPermissions.
     */
    private String[] forumPermissions = null;
    
    /**
     * Empty constructor.
     */
    public RoleForm() {
        // Empty constructor
    }


	 /**
     * Validates the form.
     *
     * @param mapping the action mapping.
     * @param request the user request.
     *
     * @return collection of validation errors.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (id == null || id.length() == 0) {
            return null;            
        }
        ActionErrors errors = new ActionErrors();        
        if (name == null || name.trim().length() == 0) {
        	errors.add("name", new ActionMessage("RoleForm.name.required"));
        }             
        return errors;
    }

    public RoleSecurity toRole() {
        RoleSecurity role = new RoleSecurity(Utility.parseLong(id), name, description);
        if (selectedContestIds != null) {
            for (int i = 0; i < this.selectedContestIds.length; ++i) {
                role.getContestPermission().addPermission(
                        Utility.parseLong(selectedContestIds[i]),
                        PermissionLevel.findById(Utility.parseLong(contestPermissions[i])));
            }
        }
        if (selectedForumIds != null) {
            for (int i = 0; i < this.selectedForumIds.length; ++i) {
                role.getForumPermission().addPermission(
                        Utility.parseLong(selectedForumIds[i]),
                        PermissionLevel.findById(Utility.parseLong(forumPermissions[i])));
            }
        }
        return role;
    }
    
    public void populate(RoleSecurity role) {
        this.name = role.getName();
        this.id = "" + role.getId();
        this.description = role.getDescription();
        
        List<PermissionEntry> entries = role.getContestPermission().getPermissions();        
        this.contestPermissions = new String[entries.size()];
        this.selectedContestIds = new String[entries.size()];
        int i = 0;
        for (PermissionEntry entry : entries) {
            selectedContestIds[i] = "" + entry.getContext();
            contestPermissions[i] = "" + entry.getAction().getId();
            i++;
        }
        
        entries = role.getForumPermission().getPermissions();        
        this.forumPermissions = new String[entries.size()];
        this.selectedForumIds = new String[entries.size()];
        i = 0;
        for (PermissionEntry entry : entries) {
            selectedForumIds[i] = "" + entry.getContext();
            forumPermissions[i] = "" + entry.getAction().getId();
            i++;
        }
    }

    public String[] getContestPermissions() {
        return contestPermissions;
    }


    public void setContestPermissions(String[] contestPermissions) {
        this.contestPermissions = contestPermissions;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String[] getForumPermissions() {
        return forumPermissions;
    }


    public void setForumPermissions(String[] forumPermissions) {
        this.forumPermissions = forumPermissions;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String[] getSelectedContestIds() {
        return selectedContestIds;
    }


    public void setSelectedContestIds(String[] selectedContestIds) {
        this.selectedContestIds = selectedContestIds;
    }


    public String[] getSelectedForumIds() {
        return selectedForumIds;
    }


    public void setSelectedForumIds(String[] selectedForumIds) {
        this.selectedForumIds = selectedForumIds;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

}
