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

package cn.edu.zju.acm.onlinejudge.form;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;
import cn.edu.zju.acm.onlinejudge.security.PermissionEntry;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * RoleForm.
 * </p>
 * 
 * @author Zhang, Zheng
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
     * @param mapping
     *            the action mapping.
     * @param request
     *            the user request.
     * 
     * @return collection of validation errors.
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (this.id == null || this.id.length() == 0) {
            return null;
        }
        ActionErrors errors = new ActionErrors();
        if (this.name == null || this.name.trim().length() == 0) {
            errors.add("name", new ActionMessage("RoleForm.name.required"));
        }
        return errors;
    }

    public RoleSecurity toRole() {
        RoleSecurity role = new RoleSecurity(Utility.parseLong(this.id), this.name, this.description);
        if (this.selectedContestIds != null) {
            for (int i = 0; i < this.selectedContestIds.length; ++i) {
                role.getContestPermission()
                    .addPermission(Utility.parseLong(this.selectedContestIds[i]),
                                   PermissionLevel.findById(Utility.parseLong(this.contestPermissions[i])));
            }
        }
        if (this.selectedForumIds != null) {
            for (int i = 0; i < this.selectedForumIds.length; ++i) {
                role.getForumPermission()
                    .addPermission(Utility.parseLong(this.selectedForumIds[i]),
                                   PermissionLevel.findById(Utility.parseLong(this.forumPermissions[i])));
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
            this.selectedContestIds[i] = "" + entry.getContext();
            this.contestPermissions[i] = "" + entry.getAction().getId();
            i++;
        }

        entries = role.getForumPermission().getPermissions();
        this.forumPermissions = new String[entries.size()];
        this.selectedForumIds = new String[entries.size()];
        i = 0;
        for (PermissionEntry entry : entries) {
            this.selectedForumIds[i] = "" + entry.getContext();
            this.forumPermissions[i] = "" + entry.getAction().getId();
            i++;
        }
    }

    public String[] getContestPermissions() {
        return this.contestPermissions;
    }

    public void setContestPermissions(String[] contestPermissions) {
        this.contestPermissions = contestPermissions;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getForumPermissions() {
        return this.forumPermissions;
    }

    public void setForumPermissions(String[] forumPermissions) {
        this.forumPermissions = forumPermissions;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getSelectedContestIds() {
        return this.selectedContestIds;
    }

    public void setSelectedContestIds(String[] selectedContestIds) {
        this.selectedContestIds = selectedContestIds;
    }

    public String[] getSelectedForumIds() {
        return this.selectedForumIds;
    }

    public void setSelectedForumIds(String[] selectedForumIds) {
        this.selectedForumIds = selectedForumIds;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
