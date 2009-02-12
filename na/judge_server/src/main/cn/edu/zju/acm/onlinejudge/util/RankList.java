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

package cn.edu.zju.acm.onlinejudge.util;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;

public class RankList {
    private List<RankListEntry> entries = new ArrayList<RankListEntry>();
    private List<RoleSecurity> roles = new ArrayList<RoleSecurity>();
    private RoleSecurity role = null;

    /**
     * @return the entries
     */
    public List<RankListEntry> getEntries() {
        return this.entries;
    }

    /**
     * @param entries
     *            the entries to set
     */
    public void setEntries(List<RankListEntry> entries) {
        this.entries = entries;
    }

    /**
     * @return the roles
     */
    public List<RoleSecurity> getRoles() {
        return this.roles;
    }

    /**
     * @param roles
     *            the roles to set
     */
    public void setRoles(List<RoleSecurity> roles) {
        this.roles = roles;
    }

    /**
     * @return the role
     */
    public RoleSecurity getRole() {
        return this.role;
    }

    /**
     * @param role
     *            the role to set
     */
    public void setRole(RoleSecurity role) {
        this.role = role;
    }

}
