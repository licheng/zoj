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

package cn.edu.zju.acm.onlinejudge.bean.enumeration;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class represents the Permission Level.
 * </p>
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class PermissionLevel implements Comparable<PermissionLevel> {

    /**
     * <p>
     * Represents VIEW static instance.
     * </p>
     */
    public static final PermissionLevel VIEW = new PermissionLevel(1, "View");

    /**
     * <p>
     * Represents PARTICIPATE static instance.
     * </p>
     */
    public static final PermissionLevel PARTICIPATE = new PermissionLevel(2, "Participate");

    /**
     * <p>
     * Represents ADMIN static instance.
     * </p>
     */
    public static final PermissionLevel PARTICIPATECANVIEWSOURCE = new PermissionLevel(3, "ParticipateCanViewSource");

    /**
     * <p>
     * Represents ADMIN static instance.
     * </p>
     */
    public static final PermissionLevel ADMIN = new PermissionLevel(4, "Admin");

    /**
     * <p>
     * Represents the id of PermissionLevel.
     * </p>
     */
    private long id;

    /**
     * <p>
     * Represents the description of PermissionLevel.
     * </p>
     */
    private String description;

    /**
     * <p>
     * Create a new instance.
     * </p>
     * 
     * @param id
     *            the id of PermissionLevel.
     * @param description
     *            the description of PermissionLevel.
     * 
     * @throws NullPointerException
     *             if the argument is null.
     */
    public PermissionLevel(long id, String description) {
        if (description == null) {
            throw new NullPointerException("description should not be null.");
        }
        this.id = id;
        this.description = description;
    }

    /**
     * <p>
     * Get the id of the PermissionLevel.
     * </p>
     * 
     * @return the id of the PermissionLevel.
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>
     * Get the description of the PermissionLevel.
     * </p>
     * 
     * @return the description of the PermissionLevel.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * <p>
     * Get the list which consists of all permission levels.
     * </p>
     * 
     * @return the list which consists of all permission levels.
     */
    public static List<PermissionLevel> getPermissionLevels() {
        List<PermissionLevel> list = new ArrayList<PermissionLevel>(4);
        list.add(PermissionLevel.VIEW);
        list.add(PermissionLevel.PARTICIPATE);
        list.add(PermissionLevel.PARTICIPATECANVIEWSOURCE);
        list.add(PermissionLevel.ADMIN);
        return list;
    }

    /**
     * <p>
     * Get the permission level represented by id.
     * </p>
     * 
     * @return the permission level represented by id, or null if none is found.
     * @param id
     *            the id of permission level.
     */
    public static PermissionLevel findById(long id) {
        if (PermissionLevel.VIEW.id == id) {
            return PermissionLevel.VIEW;
        } else if (PermissionLevel.PARTICIPATE.id == id) {
            return PermissionLevel.PARTICIPATE;
        } else if (PermissionLevel.ADMIN.id == id) {
            return PermissionLevel.ADMIN;
        } else if (PermissionLevel.PARTICIPATECANVIEWSOURCE.id == id) {
            return PermissionLevel.PARTICIPATECANVIEWSOURCE;
        } else {
            return null;
        }
    }

    /**
     * <p>
     * Get the permission level represented by name.
     * </p>
     * 
     * @return the permission level represented by name, or null if none matchs.
     * 
     * @param name
     *            the name of permission level.
     */
    public static PermissionLevel findByName(String name) {
        if (PermissionLevel.VIEW.description.equals(name)) {
            return PermissionLevel.VIEW;
        } else if (PermissionLevel.PARTICIPATE.description.equals(name)) {
            return PermissionLevel.PARTICIPATE;
        } else if (PermissionLevel.ADMIN.description.equals(name)) {
            return PermissionLevel.ADMIN;
        } else if (PermissionLevel.PARTICIPATECANVIEWSOURCE.description.equals(name)) {
            return PermissionLevel.PARTICIPATECANVIEWSOURCE;
        } else {
            return null;
        }
    }

    /**
     * <p>
     * Compares to the object.
     * </p>
     * 
     * @return true if and only if the ids of the two are the same.
     * @param object
     *            the object to compare.
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof PermissionLevel && this.id == ((PermissionLevel) object).id;
    }

    /**
     * <p>
     * get the hashCode of this instance.
     * </p>
     * 
     * @return the hash code of this instance.
     */
    @Override
    public int hashCode() {
        return new Long(this.id).hashCode();
    }

    /**
     * <p>
     * Compares two permission level.
     * </p>
     * 
     * @param object
     *            the object to compare
     * @return a positive int if this level is greater than the given entry, 0 if equals to or a negative int if is less
     *         than
     * @throws ClassCastException
     *             if the object is not type of PermissionLevel
     */
    public int compareTo(PermissionLevel object) {
        if (object == null) {
            return 1;
        }
        return new Long(this.id).compareTo(new Long(object.id));
    }

}
