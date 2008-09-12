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

import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;

/**
 * <p>
 * This class represents the PermissionEntry.
 * </p>
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class PermissionEntry {

    /**
     * <p>
     * Represents context.
     * </p>
     */
    private final long context;

    /**
     * <p>
     * Represents PermissionLevel.
     * </p>
     */
    private final PermissionLevel action;

    /**
     * <p>
     * Constructor with context and action.
     * </p>
     * 
     * @param context
     *            the context
     * @param action
     *            the action
     */
    public PermissionEntry(long context, PermissionLevel action) {
        this.context = context;
        this.action = action;
    }

    /**
     * <p>
     * Gets context
     * </p>
     * 
     * @return context
     */
    public long getContext() {
        return this.context;
    }

    /**
     * <p>
     * Gets action.
     * </p>
     * 
     * @return action
     */
    public PermissionLevel getAction() {
        return this.action;
    }

}
