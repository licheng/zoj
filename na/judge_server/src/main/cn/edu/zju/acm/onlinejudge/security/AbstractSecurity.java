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

/**
 * <p>
 * AbstractSecurity.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 */
public abstract class AbstractSecurity {
    /**
     * <p>
     * Security id.
     * </p>
     */
    private long id;

    /**
     * <p>
     * The contest permissions.
     * </p>
     */
    private final PermissionCollection contestPermission = new PermissionCollection();

    /**
     * <p>
     * The forum permissions.
     * </p>
     */
    private final PermissionCollection forumPermission = new PermissionCollection();

    /**
     * <p>
     * Constructor with id.
     * </p>
     * 
     * 
     * @param id
     *            the id.
     */
    protected AbstractSecurity(long id) {
        this.id = id;
    }

    /**
     * <p>
     * Gets the id.
     * </p>
     * 
     * @return the id
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>
     * Gets the contest permissions.
     * </p>
     * 
     * @return the contest permissions
     */
    public PermissionCollection getContestPermission() {
        return this.contestPermission;
    }

    /**
     * <p>
     * Gets the forum permissions.
     * </p>
     * 
     * @return the forum permissions
     */
    public PermissionCollection getForumPermission() {
        return this.forumPermission;
    }
}
