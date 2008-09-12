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
 * This class represents the RoleSecurity.
 * </p>
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class RoleSecurity extends AbstractSecurity {
    /**
     * <p>
     * Represents role name.
     * </p>
     */
    private final String name;

    /**
     * <p>
     * Represents role description.
     * </p>
     */
    private final String description;

    /**
     * <p>
     * Constructor with id and name.
     * </p>
     * 
     * @param id
     *            the id
     * @param name
     *            the name
     * @param description
     *            the description
     * @throws NullPointerException
     *             if name or description is null
     * @throws IllegalArgumentException
     *             if name is empty;
     */
    public RoleSecurity(long id, String name, String description) {
        super(id);
        if (name == null) {
            throw new NullPointerException("name is null.");
        }
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("name is empty.");
        }
        if (description == null) {
            throw new NullPointerException("description is null.");
        }
        this.description = description;
        this.name = name;
    }

    /**
     * <p>
     * Gets the name.
     * </p>
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * <p>
     * Gets the description.
     * </p>
     * 
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }
}
