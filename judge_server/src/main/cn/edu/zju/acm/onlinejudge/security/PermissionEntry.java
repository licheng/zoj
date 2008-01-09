package cn.edu.zju.acm.onlinejudge.security;
/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;


/**
 * <p>This class represents the PermissionEntry. </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class PermissionEntry {
	
    /**
     * <p>Represents context.</p>     
     */
    private final long context;

    /**
     * <p>Represents PermissionLevel.</p> 
     */
    private final PermissionLevel action;

    /**
     * <p>Constructor with context and action.</p>
     *
     * @param context the context
     * @param action the action
     */
    public PermissionEntry(long context, PermissionLevel action) {
    	this.context = context;
    	this.action = action;
    }

    /**
     * <p>Gets context</p>
     * 
     * @return context
     */
    public long getContext() {
        return context;
    }

    /**
     * <p>Gets action.</p>
     * 
     * @return action
     */
    public PermissionLevel getAction() {
        return action;
    }

}
