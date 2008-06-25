/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;


/**
 * <p>UserSecurity.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class UserSecurity extends AbstractSecurity {
	
    /**
     * <p>
     * Represents the role of super admin.
     * </p>
     */
    private static final String SUPER_ADMIN = "Super Admin";
    
    /**
     * <p>User roles.</p>
     */
    private List roles = new ArrayList();

    /**
     * <p>Super admin flag.</p>
     */
    private boolean superAdmin = false;

    /**
     * <p>Constructor with id.</p>
     *
     * @param id the id.
     */
    public UserSecurity(long id) {
        super(id);
    }

    /**
     * <p>Constructor with id and superAdmin flag.</p>
     *
     * @param id the id.
     * @param superAdmin the superAdmin flag
     */
    public UserSecurity(long id, boolean superAdmin) {
        super(id);
        this.superAdmin = superAdmin;
    }

    /**
     * <p>The role list.</p>
     *
     *
     * @return the role list
     */
    public List getRoles() {
        return new ArrayList(roles);
    }

    /**
     * <p>
     * Get the a String represents the roles of UserSecurity.
     * </p>
     *
     * @return a String represents the roles of UserSecurity.
     */
    public String getRolesString() {
        if (superAdmin) {
            return SUPER_ADMIN;
        }
        
        StringBuffer buffer = new StringBuffer();
        for (Iterator it = roles.iterator(); it.hasNext();) {
            if (buffer.length() > 0) {
            	buffer.append(", ");
            }
            buffer.append(((RoleSecurity) it.next()).getName());
        }

        return buffer.toString();
    }

    /**
     * <p>Imports the given role.</p>
     *
     *
     * @param role role to import
     *
     * @throws NullPointerException if role is null
     */
    public void importRole(RoleSecurity role) {
        if (role == null) {
            throw new NullPointerException("role should not be null.");
        }

        roles.add(role);
        getContestPermission().importPermissions(role.getContestPermission());
        getForumPermission().importPermissions(role.getForumPermission());
    }

    /**
     * <p>
     * Get whether the UserSecurity can view the contest.
     * </p>
     *
     * @param contestId the contest id.
     *
     * @return whether the UserSecurity can view the contest.
     */
    public boolean canViewContest(long contestId) {
    	return superAdmin || PermissionLevel.VIEW.compareTo(getContestPermission().getPermission(contestId)) <= 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can participate the contest.
     * </p>
     *
     * @param contestId the contest id.
     *
     * @return whether the UserSecurity can participate the contest.
     */
    public boolean canParticipateContest(long contestId) {
    	return superAdmin || 
    		PermissionLevel.PARTICIPATE.compareTo(getContestPermission().getPermission(contestId)) <= 0;        
    }

    /**
     * <p>
     * Get whether the UserSecurity can admin the contest.
     * </p>
     *
     * @param contestId the contest id.
     *
     * @return whether the UserSecurity can admin the contest.
     */
    public boolean canAdminContest(long contestId) {
    	return superAdmin || PermissionLevel.ADMIN.compareTo(getContestPermission().getPermission(contestId)) == 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can view the forum.
     * </p>
     *
     * @param forumId the forum id.
     *
     * @return whether the UserSecurity can view the forum.
     */
    public boolean canViewForum(long forumId) {
    	return superAdmin || PermissionLevel.VIEW.compareTo(getForumPermission().getPermission(forumId)) <= 0;
    }
    
    /**
     * <p>
     * Get whether the UserSecurity can view the forum.
     * </p>
     *
     * @param forumId the forum id.
     *
     * @return whether the UserSecurity can view the forum.
     */
    public boolean canViewSource(long contestId) {
    	return superAdmin || PermissionLevel.ADMIN.compareTo(getContestPermission().getPermission(contestId)) <= 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can participate the forum.
     * </p>
     *
     * @param forumId the forum id.
     *
     * @return whether the UserSecurity can participate the forum.
     */
    public boolean canParticipateForum(long forumId) {
    	return superAdmin || 
    		PermissionLevel.PARTICIPATE.compareTo(getForumPermission().getPermission(forumId)) <= 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can admin the forum.
     * </p>
     *
     * @param forumId the forum id.
     *
     * @return whether the UserSecurity can admin the forum.
     */
    public boolean canAdminForum(long forumId) {
        return superAdmin || PermissionLevel.ADMIN.compareTo(getForumPermission().getPermission(forumId)) == 0;
    }
    


    
    /**
     * <p>Returns whether the user is super amdin.</p>
     *
     *
     * @return  whether the user is super amdin.
     */
    public boolean isSuperAdmin() {
        return superAdmin;
    }

    /**
     * <p>Sets user as a super admin or not.</p>
     *
     * @param superAdmin the superAdmin flag
     */
    public void setSuperAdmin(boolean superAdmin) {        
    	this.superAdmin = superAdmin;
    }
}
