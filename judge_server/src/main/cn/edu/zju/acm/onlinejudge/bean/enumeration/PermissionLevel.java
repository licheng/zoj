/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean.enumeration;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>This class represents the Permission Level. </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class PermissionLevel implements Comparable {

    /**
     * <p>Represents VIEW static instance.</p>
     */
    public static final PermissionLevel VIEW = new PermissionLevel(1, "View");

    /**
     * <p>Represents PARTICIPATE static instance.</p>
     */
    public static final PermissionLevel PARTICIPATE = new PermissionLevel(2, "Participate");

    /**
     * <p>Represents ADMIN static instance.</p>
     */
    public static final PermissionLevel PARTICIPATECANVIEWSOURCE = new PermissionLevel(3, "ParticipateCanViewSource");

    /**
     * <p>Represents ADMIN static instance.</p>
     */
    public static final PermissionLevel ADMIN = new PermissionLevel(4, "Admin");
    

    /**
     * <p>Represents the id of PermissionLevel.</p>
     */
    private long id;

    /**
     * <p>Represents the description of PermissionLevel.</p>
     */
    private String description;

    /**
     * <p>Create a new instance.</p>
     *
     * @param id the id of PermissionLevel.
     * @param description the description of PermissionLevel.
     *
     * @throws NullPointerException if the argument is null.
     */
    public PermissionLevel(long id, String description) {
        if (description == null) {
            throw new NullPointerException("description should not be null.");
        }
        this.id = id;
        this.description = description;
    }

    /**
     * <p>Get the id of the PermissionLevel.</p>
     *
     * @return the id of the PermissionLevel.
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>Get the description of the PermissionLevel.</p>
     *
     * @return the description of the PermissionLevel.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * <p>Get the list which consists of all permission levels.</p>
     *
     * @return the list which consists of all permission levels.
     */
    public static List getPermissionLevels() {
        List list = new ArrayList(4);
        list.add(VIEW);
        list.add(PARTICIPATE);
        list.add(PARTICIPATECANVIEWSOURCE);
        list.add(ADMIN);
        return list;
    }

    /**
     * <p>Get the permission level represented by id.</p>
     *
     * @return the permission level represented by id, or null if none is found.
     * @param id the id of permission level.
     */
    public static PermissionLevel findById(long id) {
        if (VIEW.id == id) {
            return VIEW;
        } else if (PARTICIPATE.id == id) {
            return PARTICIPATE;
        } else if (ADMIN.id == id) {
            return ADMIN;
        } else if (PARTICIPATECANVIEWSOURCE.id == id) {
            return PARTICIPATECANVIEWSOURCE;
        } else {
            return null;
        }
    }

    /**
     * <p>Get the permission level represented by name.</p>
     *
     * @return the permission level represented by name, or null if none matchs.
     *
     * @param name the name of permission level.
     */
    public static PermissionLevel findByName(String name) {
        if (VIEW.description.equals(name)) {
            return VIEW;
        } else if (PARTICIPATE.description.equals(name)) {
            return PARTICIPATE;
        } else if (ADMIN.description.equals(name)) {
            return ADMIN;
        } else if (PARTICIPATECANVIEWSOURCE.description.equals(name)) {
            return PARTICIPATECANVIEWSOURCE;
        } else {
            return null;
        }
    }

    /**
     * <p>Compares to the object.</p>
     *
     * @return true if and only if the ids of the two are the same.
     * @param object the object to compare.
     */
    public boolean equals(Object object) {
        return object instanceof PermissionLevel && this.id == ((PermissionLevel) object).id;
    }

    /**
     * <p>get the hashCode of this instance.</p>
     *
     * @return the hash code of this instance.
     */
    public int hashCode() {
        return new Long(this.id).hashCode();
    }
    
    /**
     * <p>Compares two permission level.</p>
     *
     * @param object the object to compare
     * @return a positive int if this level is greater than the given entry, 0 if equals to or a negative int
     * if is less than
     * @throws ClassCastException if the object is not type of PermissionLevel
     */
    public int compareTo(Object object) {
    	if (object == null) {
    		return 1;
    	}
    	if (object instanceof PermissionLevel) {
    		return new Long(id).compareTo(new Long(((PermissionLevel) object).id));
    	} else {
    		throw new ClassCastException("the object is not type of PermissionLevel");
    	}        
    }

}
