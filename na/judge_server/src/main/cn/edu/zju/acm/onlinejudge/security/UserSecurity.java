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

package cn.edu.zju.acm.onlinejudge.security;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;

/**
 * <p>
 * UserSecurity.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 */
public class UserSecurity extends AbstractSecurity {

    /**
     * <p>
     * Represents the role of super admin.
     * </p>
     */
    private static final String SUPER_ADMIN = "Super Admin";

    /**
     * <p>
     * User roles.
     * </p>
     */
    private List<RoleSecurity> roles = new ArrayList<RoleSecurity>();

    /**
     * <p>
     * Super admin flag.
     * </p>
     */
    private boolean superAdmin = false;

    /**
     * <p>
     * has Course flag.
     * </p>
     */
    private boolean hasCourse = false;

    /**
     * <p>
     * Constructor with id.
     * </p>
     * 
     * @param id
     *            the id.
     */
    public UserSecurity(long id) {
        super(id);
    }

    /**
     * <p>
     * Constructor with id and superAdmin flag.
     * </p>
     * 
     * @param id
     *            the id.
     * @param superAdmin
     *            the superAdmin flag
     */
    public UserSecurity(long id, boolean superAdmin) {
        super(id);
        this.superAdmin = superAdmin;
    }

    /**
     * <p>
     * The role list.
     * </p>
     * 
     * 
     * @return the role list
     */
    public List<RoleSecurity> getRoles() {
        return new ArrayList<RoleSecurity>(this.roles);
    }

    /**
     * <p>
     * Get the a String represents the roles of UserSecurity.
     * </p>
     * 
     * @return a String represents the roles of UserSecurity.
     */
    public String getRolesString() {
        if (this.superAdmin) {
            return UserSecurity.SUPER_ADMIN;
        }

        StringBuffer buffer = new StringBuffer();
        for (RoleSecurity roleSecurity : this.roles) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(roleSecurity.getName());
        }

        return buffer.toString();
    }

    /**
     * <p>
     * Imports the given role.
     * </p>
     * 
     * 
     * @param role
     *            role to import
     * 
     * @throws NullPointerException
     *             if role is null
     */
    public void importRole(RoleSecurity role) {
        if (role == null) {
            throw new NullPointerException("role should not be null.");
        }

        this.roles.add(role);
        this.getContestPermission().importPermissions(role.getContestPermission());
        this.getForumPermission().importPermissions(role.getForumPermission());
    }

    /**
     * <p>
     * Get whether the UserSecurity can view the contest.
     * </p>
     * 
     * @param contestId
     *            the contest id.
     * 
     * @return whether the UserSecurity can view the contest.
     */
    public boolean canViewContest(long contestId) {
        return this.superAdmin ||
            PermissionLevel.VIEW.compareTo(this.getContestPermission().getPermission(contestId)) <= 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can participate the contest.
     * </p>
     * 
     * @param contestId
     *            the contest id.
     * 
     * @return whether the UserSecurity can participate the contest.
     */
    public boolean canParticipateContest(long contestId) {
        return this.superAdmin ||
            PermissionLevel.PARTICIPATE.compareTo(this.getContestPermission().getPermission(contestId)) <= 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can view the forum.
     * </p>
     * 
     * @param forumId
     *            the forum id.
     * 
     * @return whether the UserSecurity can view the forum.
     */
    public boolean canViewSource(long contestId) {
        return this.superAdmin ||
            PermissionLevel.PARTICIPATECANVIEWSOURCE.compareTo(this.getContestPermission().getPermission(contestId)) <= 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can admin the contest.
     * </p>
     * 
     * @param contestId
     *            the contest id.
     * 
     * @return whether the UserSecurity can admin the contest.
     */
    public boolean canAdminContest(long contestId) {
        return this.superAdmin ||
            PermissionLevel.ADMIN.compareTo(this.getContestPermission().getPermission(contestId)) <= 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can view the forum.
     * </p>
     * 
     * @param forumId
     *            the forum id.
     * 
     * @return whether the UserSecurity can view the forum.
     */
    public boolean canViewForum(long forumId) {
        return this.superAdmin || PermissionLevel.VIEW.compareTo(this.getForumPermission().getPermission(forumId)) <= 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can participate the forum.
     * </p>
     * 
     * @param forumId
     *            the forum id.
     * 
     * @return whether the UserSecurity can participate the forum.
     */
    public boolean canParticipateForum(long forumId) {
        return this.superAdmin ||
            PermissionLevel.PARTICIPATE.compareTo(this.getForumPermission().getPermission(forumId)) <= 0;
    }

    /**
     * <p>
     * Get whether the UserSecurity can admin the forum.
     * </p>
     * 
     * @param forumId
     *            the forum id.
     * 
     * @return whether the UserSecurity can admin the forum.
     */
    public boolean canAdminForum(long forumId) {
        return this.superAdmin ||
            PermissionLevel.ADMIN.compareTo(this.getForumPermission().getPermission(forumId)) == 0;
    }

    /**
     * <p>
     * Returns whether the user is super amdin.
     * </p>
     * 
     * 
     * @return whether the user is super amdin.
     */
    public boolean isSuperAdmin() {
        return this.superAdmin;
    }

    /**
     * <p>
     * Sets user as a super admin or not.
     * </p>
     * 
     * @param superAdmin
     *            the superAdmin flag
     */
    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }
    
    public boolean isHasCourses() {
    	return this.hasCourse;
    }
    
    public void setHasCourses(boolean hasCourse) {
    	this.hasCourse = hasCourse;
    }
}
