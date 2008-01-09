/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.security;

/**
 * <p>AbstractSecurity.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public abstract class AbstractSecurity {
    /**
     * <p>Security id.</p>
     */
    private long id;

    /**
     * <p>The contest permissions.</p>
     */
    private final PermissionCollection contestPermission = new PermissionCollection();

    /**
     * <p>The forum permissions.</p>
     */
    private final PermissionCollection forumPermission = new PermissionCollection();

    /**
     * <p>Constructor with id.</p>
     *
     * 
     * @param id the id.
     */
    protected AbstractSecurity(long id) {
    	this.id = id; 
    }

    /**
     * <p>Gets the id.</p>
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * <p>Gets the contest permissions.</p>
     * 
     * @return the contest permissions
     */
    public PermissionCollection getContestPermission() {
    	return contestPermission;
    }

    /**
     * <p>Gets the forum permissions.</p>
     * 
     * @return the forum permissions
     */
    public PermissionCollection getForumPermission() {
    	return forumPermission;
    }
}
