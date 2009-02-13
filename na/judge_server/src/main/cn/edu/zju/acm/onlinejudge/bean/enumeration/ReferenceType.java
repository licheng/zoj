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
 * This class represents the Reference Type.
 * </p>
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ReferenceType {

    /**
     * <p>
     * Represents DESCRIPTION static instance.
     * </p>
     */
    public static final ReferenceType DESCRIPTION = new ReferenceType(1, "Description");

    /**
     * <p>
     * Represents INPUT static instance.
     * </p>
     */
    public static final ReferenceType INPUT = new ReferenceType(2, "Input");

    /**
     * <p>
     * Represents OUTPUT static instance.
     * </p>
     */
    public static final ReferenceType OUTPUT = new ReferenceType(3, "Output");

    /**
     * <p>
     * Represents AUXILIARY static instance.
     * </p>
     */
    public static final ReferenceType AUXILIARY = new ReferenceType(4, "Auxiliary");

    /**
     * <p>
     * Represents CHECKER static instance.
     * </p>
     */
    public static final ReferenceType HEADER = new ReferenceType(5, "Header");

    /**
     * <p>
     * Represents CHECKER_SOURCE static instance.
     * </p>
     */
    public static final ReferenceType CHECKER_SOURCE = new ReferenceType(6, "Checker Source");

    /**
     * <p>
     * Represents JUDGE_SOLUTION static instance.
     * </p>
     */
    public static final ReferenceType JUDGE_SOLUTION = new ReferenceType(7, "Judge Solution");

    /**
     * <p>
     * Represents DOWNLOAD static instance.
     * </p>
     */
    public static final ReferenceType DOWNLOAD = new ReferenceType(8, "Download");

    /**
     * <p>
     * Represents MISC static instance.
     * </p>
     */
    public static final ReferenceType MISC = new ReferenceType(9, "Misc");
    
    /**
     * <p>
     * Represents MISC static instance.
     * </p>
     */
    public static final ReferenceType TAName = new ReferenceType(10, "TA Name");
    
    /**
     * <p>
     * Represents MISC static instance.
     * </p>
     */
    public static final ReferenceType TAPhone = new ReferenceType(11, "TA Phone");
    
    /**
     * <p>
     * Represents MISC static instance.
     * </p>
     */
    public static final ReferenceType TAEmail = new ReferenceType(12, "TA Email");

    /**
     * <p>
     * Represents the id of ReferenceType.
     * </p>
     */
    private long id;

    /**
     * <p>
     * Represents the description of ReferenceType.
     * </p>
     */
    private String description;

    /**
     * <p>
     * Create a new instance.
     * </p>
     * 
     * @param id
     *            the id of ReferenceType.
     * @param description
     *            the description of ReferenceType.
     * 
     * @throws NullPointerException
     *             if the argument is null.
     */
    public ReferenceType(long id, String description) {
        if (description == null) {
            throw new NullPointerException("description should not be null.");
        }
        this.id = id;
        this.description = description;
    }

    /**
     * <p>
     * Get the id of this instance.
     * </p>
     * 
     * @return the id of this instance.
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>
     * Get the description of this instance.
     * </p>
     * 
     * @return the description of this instance.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * <p>
     * Get the list which consists of all Reference Types.
     * </p>
     * 
     * @return the list which consists of all Reference Types.
     */
    public List<ReferenceType> getReferenceTypes() {
        List<ReferenceType> list = new ArrayList<ReferenceType>(9);
        list.add(ReferenceType.DESCRIPTION);
        list.add(ReferenceType.INPUT);
        list.add(ReferenceType.OUTPUT);
        list.add(ReferenceType.AUXILIARY);
        list.add(ReferenceType.HEADER);
        list.add(ReferenceType.CHECKER_SOURCE);
        list.add(ReferenceType.JUDGE_SOLUTION);
        list.add(ReferenceType.DOWNLOAD);
        list.add(ReferenceType.MISC);
        list.add(ReferenceType.TAName);
        list.add(ReferenceType.TAPhone);
        list.add(ReferenceType.TAEmail);
        return list;
    }

    /**
     * <p>
     * Get the Reference Type represented by id.
     * </p>
     * 
     * @return the Reference Type represented by id, or null if none is found.
     * 
     * @param id
     *            the id of Reference Type
     */
    public static ReferenceType findById(long id) {
        if (ReferenceType.DESCRIPTION.id == id) {
            return ReferenceType.DESCRIPTION;
        } else if (ReferenceType.INPUT.id == id) {
            return ReferenceType.INPUT;
        } else if (ReferenceType.OUTPUT.id == id) {
            return ReferenceType.OUTPUT;
        } else if (ReferenceType.AUXILIARY.id == id) {
            return ReferenceType.AUXILIARY;
        } else if (ReferenceType.HEADER.id == id) {
            return ReferenceType.HEADER;
        } else if (ReferenceType.CHECKER_SOURCE.id == id) {
            return ReferenceType.CHECKER_SOURCE;
        } else if (ReferenceType.JUDGE_SOLUTION.id == id) {
            return ReferenceType.JUDGE_SOLUTION;
        } else if (ReferenceType.DOWNLOAD.id == id) {
            return ReferenceType.DOWNLOAD;
        } else if (ReferenceType.MISC.id == id) {
            return ReferenceType.MISC;
        } else if (ReferenceType.TAName.id == id) {
            return ReferenceType.TAName;
        } else if (ReferenceType.TAPhone.id == id) {
            return ReferenceType.TAPhone;
        } else if (ReferenceType.TAEmail.id == id) {
            return ReferenceType.TAEmail;
        } else {
            return null;
        }
    }

    /**
     * <p>
     * Get the Reference Type represented by name.
     * </p>
     * 
     * @return the Reference Type represented by name, or null if none matchs.
     * 
     * @param name
     *            the name of Reference Type.
     */
    public static ReferenceType findByName(String name) {
        if (ReferenceType.DESCRIPTION.description.equalsIgnoreCase(name)) {
            return ReferenceType.DESCRIPTION;
        } else if (ReferenceType.INPUT.description.equalsIgnoreCase(name)) {
            return ReferenceType.INPUT;
        } else if (ReferenceType.OUTPUT.description.equalsIgnoreCase(name)) {
            return ReferenceType.OUTPUT;
        } else if (ReferenceType.AUXILIARY.description.equalsIgnoreCase(name)) {
            return ReferenceType.AUXILIARY;
        } else if (ReferenceType.HEADER.description.equalsIgnoreCase(name)) {
            return ReferenceType.HEADER;
        } else if (ReferenceType.CHECKER_SOURCE.description.equalsIgnoreCase(name)) {
            return ReferenceType.CHECKER_SOURCE;
        } else if (ReferenceType.JUDGE_SOLUTION.description.equalsIgnoreCase(name)) {
            return ReferenceType.JUDGE_SOLUTION;
        } else if (ReferenceType.DOWNLOAD.description.equalsIgnoreCase(name)) {
            return ReferenceType.DOWNLOAD;
        } else if (ReferenceType.MISC.description.equalsIgnoreCase(name)) {
            return ReferenceType.MISC;
        } else if (ReferenceType.TAName.description.equalsIgnoreCase(name)) {
            return ReferenceType.TAName;
        } else if (ReferenceType.TAPhone.description.equalsIgnoreCase(name)) {
            return ReferenceType.TAPhone;
        } else if (ReferenceType.TAEmail.description.equalsIgnoreCase(name)) {
            return ReferenceType.TAEmail;
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
     * 
     * @param object
     *            the object to compare.
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof ReferenceType && this.id == ((ReferenceType) object).id;
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

}
