/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com> Xu, Chuan <xuchuan@gmail.com>
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

package cn.edu.zju.acm.onlinejudge.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.security.PermissionCollection;
import cn.edu.zju.acm.onlinejudge.security.PermissionEntry;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;

/**
 * <p>
 * AuthorizationPersistenceImpl implements AuthorizationPersistence interface
 * </p>
 * <p>
 * AuthorizationPersistence interface defines the API used to manager the authorization related affairs in persistence
 * layer.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 * @author Xu, Chuan
 */
public class AuthorizationPersistenceImpl implements AuthorizationPersistence {
    /**
     * <p>
     * Creates the specified role in the persistence layer.
     * </p>
     * 
     * @param role
     *            the RoleSecurity instance to be created
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void createRole(RoleSecurity role, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps =
                        conn.prepareStatement("INSERT INTO role"
                            + "(name, description, create_user, create_date, last_update_user, last_update_date) "
                            + "VALUES(?, ?, ?, NOW(), ?, NOW())");
                ps.setString(1, role.getName());
                ps.setString(2, role.getDescription());
                ps.setLong(3, user);
                ps.setLong(4, user);
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create role.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Updates the specified role in the persistence layer.
     * </p>
     * 
     * @param role
     *            the RoleSecurity instance to update
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void updateRole(RoleSecurity role, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            try {
                ps =
                        conn.prepareStatement("UPDATE role "
                            + "SET name=?, description=?, last_update_user=?, last_update_date=NOW() "
                            + "WHERE role_id=?");
                ps.setString(1, role.getName());
                ps.setString(2, role.getDescription());
                ps.setLong(3, user);
                ps.setLong(4, role.getId());
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            this.updateRolePermission("contest", role.getId(), role.getContestPermission(), conn);
            this.updateRolePermission("forum", role.getId(), role.getForumPermission(), conn);
            conn.commit();
        } catch (SQLException e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to update role.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    private void updateRolePermission(String tableName, long roleId, PermissionCollection permissions, Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE FROM " + tableName + "_permission WHERE role_id=?");
            ps.setLong(1, roleId);
            ps.executeUpdate();
        } finally {
            Database.dispose(ps);
        }
        for (PermissionEntry entry : permissions.getPermissions()) {
            try {
                ps =
                        conn.prepareStatement("INSERT INTO " + tableName + "_permission(role_id, " + tableName +
                            "_id, permission_level_id) " + "VALUES(?, ?, ?)");
                ps.setLong(1, roleId);
                ps.setLong(2, entry.getContext());
                ps.setLong(3, entry.getAction().getId());
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
        }
    }

    /**
     * <p>
     * Deletes the specified role in the persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the role to be deleted
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void deleteRole(long id, long user) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("DELETE FROM user_role WHERE role_id=?");
                ps.setLong(1, id);
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            try {
                ps = conn.prepareStatement("DELETE FROM contest_permission WHERE role_id=?");
                ps.setLong(1, id);
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            try {
                ps = conn.prepareStatement("DELETE FROM forum_permission WHERE role_id=?");
                ps.setLong(1, id);
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            try {
                ps = conn.prepareStatement("DELETE FROM role WHERE role_id=?");
                ps.setLong(1, id);
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
            conn.commit();
        } catch (SQLException e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to delete role.", e);
        } finally {
            Database.dispose(conn);
        }

    }

    /**
     * <p>
     * Gets a role with given id from persistence layer.
     * </p>
     * 
     * @return a role with given id from persistence layer
     * @param id
     *            the id of the role
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public RoleSecurity getRole(long id) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            RoleSecurity role;
            try {
                ps = conn.prepareStatement("SELECT role_id, name, description FROM role WHERE role_id=?");
                ps.setLong(1, id);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    return null;
                }
                role = new RoleSecurity(rs.getLong(1), rs.getString(2), rs.getString(3));
            } finally {
                Database.dispose(ps);
            }
            try {
                // select the contest permissions
                ps =
                        conn
                            .prepareStatement("SELECT role_id, contest_id, permission_level_id FROM contest_permission WHERE role_id=?");
                ps.setLong(1, id);
                rs = ps.executeQuery();
                while (rs.next()) {
                    role.getContestPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));
                }
            } finally {
                Database.dispose(ps);
            }
            try {
                // select the forum permissions
                ps =
                        conn
                            .prepareStatement("SELECT role_id, forum_id, permission_level_id FROM forum_permission WHERE role_id=?");
                ps.setLong(1, id);
                rs = ps.executeQuery();
                while (rs.next()) {
                    role.getForumPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));
                }
            } finally {
                Database.dispose(ps);
            }
            return role;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get the role with id " + id, e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Gets all roles of given contest from persistence layer.
     * </p>
     * 
     * @return a list of RoleSecurity instances
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<RoleSecurity> getContestRoles(long contestId) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                // select the roles;
                ps =
                        conn
                            .prepareStatement("SELECT role_id, name, description FROM role WHERE role_id IN (SELECT role_id FROM contest_permission WHERE contest_id=? AND permission_level_id>1)");
                ps.setLong(1, contestId);
                ResultSet rs = ps.executeQuery();

                List<RoleSecurity> roles = new ArrayList<RoleSecurity>();
                while (rs.next()) {
                    RoleSecurity role = new RoleSecurity(rs.getLong(1), rs.getString(2), rs.getString(3));
                    roles.add(role);
                }
                return roles;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get all roles", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Gets all roles from persistence layer.
     * </p>
     * 
     * @return a list of RoleSecurity instances
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<RoleSecurity> getAllRoles() throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            List<RoleSecurity> roles = new ArrayList<RoleSecurity>();
            Map<Long, RoleSecurity> roleIds = new HashMap<Long, RoleSecurity>();
            try {
                // select the roles;
                ps = conn.prepareStatement("SELECT role_id, name, description FROM role");
                rs = ps.executeQuery();
                while (rs.next()) {
                    RoleSecurity role = new RoleSecurity(rs.getLong(1), rs.getString(2), rs.getString(3));
                    roles.add(role);
                    roleIds.put(role.getId(), role);
                }
            } finally {
                Database.dispose(ps);
            }
            try {
                // select the contests permissions
                ps = conn.prepareStatement("SELECT role_id, contest_id, permission_level_id FROM contest_permission");
                rs = ps.executeQuery();
                while (rs.next()) {
                    RoleSecurity role = roleIds.get(rs.getLong(1));
                    role.getContestPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));
                }
            } finally {
                Database.dispose(ps);
            }
            try {
                // select the forum permissions
                ps = conn.prepareStatement("SELECT role_id, forum_id, permission_level_id FROM forum_permission");
                rs = ps.executeQuery();
                while (rs.next()) {
                    RoleSecurity role = roleIds.get(rs.getLong(1));
                    role.getForumPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));
                }
            } finally {
                Database.dispose(ps);
            }
            return roles;

        } catch (SQLException e) {
            throw new PersistenceException("Failed to get all roles", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Updates the given UserSecurity instance in persistence layer.
     * </p>
     * 
     * @param security
     *            the specified UserSecurity to update
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public void updateUserSecurity(UserSecurity security, long user) throws PersistenceException {

    }

    public void addUserRole(long userProfileId, long roleId) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("INSERT INTO user_role(user_profile_id, role_id) VALUES(?,?)");
                ps.setLong(1, userProfileId);
                ps.setLong(2, roleId);
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to add user role", e);
        } finally {
            Database.dispose(conn);
        }
    }

    public void deleteUserRole(long userProfileId, long roleId) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement("DELETE FROM user_role WHERE user_profile_id=? AND role_id=?");
                ps.setLong(1, userProfileId);
                ps.setLong(2, roleId);
                ps.executeUpdate();
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Failed to delete user role", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Gets a UserSecurity instance with the given user id from persistence layer.
     * </p>
     * 
     * @param userProfileId
     *            the id of user profile used to get the UserSecurity instance
     * @return the UserSecurity instance with the given user id
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public UserSecurity getUserSecurity(long userProfileId) throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            boolean superAdmin = false;
            try {
                ps = conn.prepareStatement("SELECT super_admin FROM user_profile where user_profile_id=?");
                ps.setLong(1, userProfileId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    superAdmin = rs.getBoolean("super_admin");
                } else {
                    return null;
                }
            } finally {
                Database.dispose(ps);
            }

            UserSecurity security = new UserSecurity(userProfileId, superAdmin);
            List<RoleSecurity> roles = new ArrayList<RoleSecurity>();
            Map<Long, RoleSecurity> roleIds = new HashMap<Long, RoleSecurity>();

            try {
                // select the roles;
                ps =
                        conn.prepareStatement("SELECT role_id, name, description FROM role " + "WHERE role_id IN "
                            + "(SELECT role_id from user_role WHERE user_profile_id = ?)");
                ps.setLong(1, userProfileId);
                rs = ps.executeQuery();

                while (rs.next()) {
                    RoleSecurity role = new RoleSecurity(rs.getLong(1), rs.getString(2), rs.getString(3));
                    roles.add(role);
                    roleIds.put(role.getId(), role);
                }
            } finally {
                Database.dispose(ps);
            }

            try {
                // select the contests permissions
                ps =
                        conn
                            .prepareStatement("SELECT role_id, contest_id, permission_level_id FROM contest_permission "
                                + "WHERE role_id IN " + "(SELECT role_id from user_role WHERE user_profile_id = ?)");
                ps.setLong(1, userProfileId);
                rs = ps.executeQuery();

                while (rs.next()) {
                    RoleSecurity role = roleIds.get(rs.getLong(1));
                    role.getContestPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));
                }
            } finally {
                Database.dispose(ps);
            }

            try {
                // select the forum permissions
                ps =
                        conn.prepareStatement("SELECT role_id, forum_id, permission_level_id FROM forum_permission "
                            + "WHERE role_id IN " + "(SELECT role_id from user_role WHERE user_profile_id = ?)");
                ps.setLong(1, userProfileId);
                rs = ps.executeQuery();
                while (rs.next()) {
                    RoleSecurity role = roleIds.get(rs.getLong(1));
                    role.getForumPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));
                }
            } finally {
                Database.dispose(ps);
            }

            for (RoleSecurity role : roles) {
                security.importRole(role);
            }

            return security;

        } catch (SQLException e) {
            throw new PersistenceException("Failed to get user security with id " + userProfileId, e);
        } finally {
            Database.dispose(conn);
        }
    }

    public Map<String, Boolean> addRoleUsers(List<String> users, long roleId) throws PersistenceException {
        return this.manageRoleUsers(users, roleId, false);
    }

    public Map<String, Boolean> removeRoleUsers(List<String> users, long roleId) throws PersistenceException {
        return this.manageRoleUsers(users, roleId, true);
    }

    private Map<String, Boolean> manageRoleUsers(List<String> users, long roleId, boolean remove) throws PersistenceException {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        if (users.size() == 0) {
            return result;
        }
        Connection conn = null;

        Map<Long, String> idMap = new HashMap<Long, String>();

        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps =
                        conn.prepareStatement("SELECT handle, user_profile_id FROM user_profile where handle IN " +
                            Database.createValues(users));
                rs = ps.executeQuery();
                while (rs.next()) {
                    String handle = rs.getString(1);
                    long id = rs.getLong(2);
                    idMap.put(id, handle);
                    result.put(handle, !remove);
                }
            } finally {
                Database.dispose(ps);
            }

            List<Long> existingIds = new ArrayList<Long>();
            if (idMap.size() > 0) {
                try {
                    ps =
                            conn.prepareStatement("SELECT user_profile_id FROM user_role " +
                                "where role_id = ? AND user_profile_id IN " +
                                Database.createNumberValues(idMap.keySet()));
                    ps.setLong(1, roleId);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        long id = rs.getLong(1);
                        result.put(idMap.get(id), remove);
                        existingIds.add(id);
                    }
                } finally {
                    Database.dispose(ps);
                }
            }

            if (remove) {
                if (existingIds.size() > 0) {
                    try {
                        ps =
                                conn.prepareStatement("DELETE FROM user_role " +
                                    "WHERE role_id=? AND user_profile_id IN " +
                                    Database.createNumberValues(existingIds));
                        ps.setLong(1, roleId);
                        ps.executeUpdate();
                    } finally {
                        Database.dispose(ps);
                    }
                }
            } else {
                for (Long id : idMap.keySet()) {
                    String handle = idMap.get(id);
                    if (result.get(handle)) {
                        try {
                            ps = conn.prepareStatement("REPLACE INTO user_role(role_id, user_profile_id) VALUES(?,?)");
                            ps.setLong(1, roleId);
                            ps.setLong(2, id);
                            ps.executeUpdate();
                        } finally {
                            Database.dispose(ps);
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to manage role users.", e);
        } finally {
            Database.dispose(conn);
        }
        return result;
    }

}
