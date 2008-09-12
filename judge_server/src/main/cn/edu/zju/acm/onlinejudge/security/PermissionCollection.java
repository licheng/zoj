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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;

/**
 * <p>
 * AbstractSecurity.
 * </p>
 * 
 * @version 2.0
 * @author ZOJDEV
 */
public class PermissionCollection {

    /**
     * <p>
     * ermissions.
     * </p>
     */
    private final Map<Long, PermissionLevel> permissions = new HashMap<Long, PermissionLevel>();

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public PermissionCollection() {
        // empty constructor
    }

    /**
     * <p>
     * Adds the permission for given context.
     * </p>
     * 
     * @return the added permission level.
     * @param context
     *            the context
     * @param action
     *            the permission to add
     * @throws NullPointerException
     *             if action is null
     */
    public PermissionLevel addPermission(long context, PermissionLevel action) {

        if (action == null) {
            throw new NullPointerException("action should not be null.");
        }

        Long contextLong = new Long(context);
        PermissionLevel oldAction = permissions.get(contextLong);
        if (action.compareTo(oldAction) > 0) {
            permissions.put(contextLong, action);
        }
        return oldAction;

    }

    /**
     * <p>
     * Removes the permission for given context.
     * </p>
     * 
     * @return the removed permission level
     * @param context
     *            the context
     */
    public PermissionLevel removePermission(long context) {
        return permissions.remove(new Long(context));
    }

    /**
     * <p>
     * Gets the permission for given context.
     * </p>
     * 
     * @return the permission for given context
     * @param context
     *            the context
     */
    public PermissionLevel getPermission(long context) {
        return permissions.get(new Long(context));
    }

    /**
     * <p>
     * A list of PermissionEntry instances.
     * </p>
     * 
     * @return a list of PermissionEntry instances.
     */
    public List<PermissionEntry> getPermissions() {
        List<PermissionEntry> entries = new ArrayList<PermissionEntry>();
        for (Iterator<Map.Entry<Long, PermissionLevel>> it = permissions.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Long, PermissionLevel> entry = it.next();
            long id = ((Long) entry.getKey()).longValue();
            PermissionLevel action = (PermissionLevel) entry.getValue();
            entries.add(new PermissionEntry(id, action));
        }
        return entries;
    }

    /**
     * <p>
     * Clears the permissions.
     * </p>
     */
    public void clearPermissions() {
        permissions.clear();
    }

    /**
     * <p>
     * Imports perssiions form given PermissionCollection instance.
     * </p>
     * 
     * @param permissions
     *            the permissions would be imported.
     * 
     * @throws NullPointerException
     *             if permissions is null
     */
    public void importPermissions(PermissionCollection permissions) {
        if (permissions == null) {
            throw new NullPointerException("permissions should not be null.");
        }

        for (Iterator<Map.Entry<Long, PermissionLevel>> iter = permissions.permissions.entrySet().iterator(); iter
                .hasNext();) {
            Map.Entry<Long, PermissionLevel> entry = iter.next();
            long id = ((Long) entry.getKey()).longValue();
            PermissionLevel action = (PermissionLevel) entry.getValue();
            addPermission(id, action);
        }
    }
}
