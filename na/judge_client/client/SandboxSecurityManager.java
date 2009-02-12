/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */

import java.security.Permission;
import java.security.SecurityPermission;
import java.util.PropertyPermission;
import java.io.FilePermission;

public class SandboxSecurityManager extends SecurityManager {

    public static Thread targetThread;

    @Override
    public void checkPermission(Permission perm) {
        this.internalCheckPermision(perm);
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        this.internalCheckPermision(perm);
    }

    private void internalCheckPermision(Permission perm) {
        if (Thread.currentThread() == targetThread) {
            if (perm instanceof SecurityPermission) {
                if (perm.getName().startsWith("getProperty")) {
                    return;
                }
            } else if (perm instanceof PropertyPermission) {
                if (perm.getActions().equals("read")) {
                    return;
                }
            } else if (perm instanceof FilePermission) {
                String name = perm.getName();
                if (name.length() > 1 && name.charAt(0) != '.' && name.charAt(1) != '/') {
                    return;
                }
            }
            throw new SecurityException(perm.toString());
        }
    }
}

