package cn.edu.zju.acm.onlinejudge.util;

import java.util.ArrayList;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;

public class RankList {
    private List entries = new ArrayList();
    private List roles = new ArrayList();
    private RoleSecurity role = null;
    
    public RankList() {
        
    }
    /**
     * @return the entries
     */
    public List getEntries() {
        return entries;
    }
    /**
     * @param entries the entries to set
     */
    public void setEntries(List entries) {
        this.entries = entries;
    }
    /**
     * @return the roles
     */
    public List getRoles() {
        return roles;
    }
    /**
     * @param roles the roles to set
     */
    public void setRoles(List roles) {
        this.roles = roles;
    }
    /**
     * @return the role
     */
    public RoleSecurity getRole() {
        return role;
    }
    /**
     * @param role the role to set
     */
    public void setRole(RoleSecurity role) {
        this.role = role;
    }
    
    
}
