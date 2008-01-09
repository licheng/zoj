/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.security.PermissionCollection;
import cn.edu.zju.acm.onlinejudge.security.PermissionEntry;
import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>AuthorizationPersistenceImpl implements AuthorizationPersistence interface</p>
 * <p>AuthorizationPersistence interface defines the API used to manager the authorization
 * related affairs in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class AuthorizationPersistenceImpl implements AuthorizationPersistence {
	
	
	/**
	 * The statment to create a role.
	 */
	private static final String INSERT_ROLE = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}}) VALUES(?, ?, ?, ?, ?, ?)", 
							 new Object[] {DatabaseConstants.ROLE_TABLE, 
				  						   DatabaseConstants.ROLE_NAME,
				  						   DatabaseConstants.ROLE_DESCRIPTION,
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE});				  						   
				  						    
	/**
	 * The statment to update a role.
	 */
	private static final String UPDATE_ROLE = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=? WHERE {5}=?", 
							 new Object[] {DatabaseConstants.ROLE_TABLE, 
				  						   DatabaseConstants.ROLE_NAME,
				  						   DatabaseConstants.ROLE_DESCRIPTION,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.ROLE_ROLE_ID}); 
	
	/**
	 * The statment to delete a role.
	 */
	private static final String DELETE_ROLE = 
		MessageFormat.format("DELETE FROM {0} WHERE {1}=?", 
							 new Object[] {DatabaseConstants.ROLE_TABLE, 										   
										   DatabaseConstants.ROLE_ROLE_ID}); 
			
	/**
	 * The query to get all roles.
	 */
	private static final String GET_ALL_ROLES = 
		MessageFormat.format("SELECT {0}, {1}, {2} FROM {3}",
				 			 new Object[] {DatabaseConstants.ROLE_ROLE_ID, 
				   					       DatabaseConstants.ROLE_NAME,
				   					       DatabaseConstants.ROLE_DESCRIPTION,
				   					       DatabaseConstants.ROLE_TABLE});

	/**
	 * The query to get a role.
	 */
	private static final String GET_ROLE = 
		MessageFormat.format("SELECT {0}, {1}, {2} FROM {3} WHERE {0}=?",
				 			 new Object[] {DatabaseConstants.ROLE_ROLE_ID, 
				   					       DatabaseConstants.ROLE_NAME,
				   					       DatabaseConstants.ROLE_DESCRIPTION,
				   					       DatabaseConstants.ROLE_TABLE});

    /**
     * <p>Creates the specified role in the persistence layer.</p>
     *
     * @param role the RoleSecurity instance to be created
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createRole(RoleSecurity role, long user) throws PersistenceException {
    	
    	if (role == null) {
    		throw new NullPointerException("role is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
            ps = conn.prepareStatement("INSERT INTO role" +
                    "(name, description, create_user, create_date, last_update_user, last_update_date) " +
                    "VALUES(?, ?, ?, NOW(), ?, NOW())");  
            ps.setString(1, role.getName());
            ps.setString(2, role.getDescription());
            ps.setLong(3, user);
            ps.setLong(4, user);
            ps.executeUpdate();                                  
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create role.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Updates the specified role in the persistence layer.</p>
     *
     * @param role the RoleSecurity instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateRole(RoleSecurity role, long user) throws PersistenceException {
    	
    	if (role == null) {
    		throw new NullPointerException("role is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
            conn.setAutoCommit(false);
            
            ps = conn.prepareStatement("UPDATE role " +
                    "SET name=?, description=?, last_update_user=?, last_update_date=NOW() " +
                    "WHERE role_id=?");  
                     
            ps.setString(1, role.getName());
            ps.setString(2, role.getDescription());
            ps.setLong(3, user);
            ps.setLong(4, role.getId());
            ps.executeUpdate();
            
            updateRolePermission("contest", role.getId(), role.getContestPermission(), conn, ps);
            updateRolePermission("forum", role.getId(), role.getForumPermission(), conn, ps);
                        
            conn.commit();
        } catch (SQLException e) {
            Database.rollback(conn);
        	throw new PersistenceException("Failed to update role.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }      	
    	 
    }
    
    private void updateRolePermission(
            String tableName, long roleId, PermissionCollection permissions, Connection conn, PreparedStatement ps) 
        throws SQLException {
        ps = conn.prepareStatement("DELETE FROM " + tableName + "_permission WHERE role_id=?"); 
        ps.setLong(1, roleId);
        ps.executeUpdate();
        
        for (PermissionEntry entry : permissions.getPermissions()) {
            ps = conn.prepareStatement("INSERT INTO " +
                    tableName + "_permission(role_id, " + tableName + "_id, permission_level_id) " +
                    "VALUES(?, ?, ?)");
            ps.setLong(1, roleId);
            ps.setLong(2, entry.getContext());
            ps.setLong(3, entry.getAction().getId());
            ps.executeUpdate();
        }
        
    }

    /**
     * <p>Deletes the specified role in the persistence layer.</p>
     *
     * @param id the id of the role to be deleted
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteRole(long id, long user) throws PersistenceException {
    	
    	Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
        	conn.setAutoCommit(false);
            ps = conn.prepareStatement("DELETE FROM user_role WHERE role_id=?");  
            ps.setLong(1, id);
            ps.executeUpdate();
            
            ps = conn.prepareStatement("DELETE FROM contest_permission WHERE role_id=?");  
            ps.setLong(1, id);
            ps.executeUpdate();
            
            ps = conn.prepareStatement("DELETE FROM forum_permission WHERE role_id=?");  
            ps.setLong(1, id);
            ps.executeUpdate();
            
            ps = conn.prepareStatement("DELETE FROM role WHERE role_id=?");  
            ps.setLong(1, id);
            ps.executeUpdate();      
            
            conn.commit();            
        }catch (SQLException e) {
            Database.rollback(conn);
        	throw new PersistenceException("Failed to delete role.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }   
    	 
    }

    /**
     * <p>Gets a role with given id from persistence layer.</p>
     *
     * @return a role with given id from persistence layer
     * @param id the id of the role
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public RoleSecurity getRole(long id) throws PersistenceException {
        Connection conn = null;
    	PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(
                "SELECT role_id, name, description FROM role WHERE role_id=?");          
            ps.setLong(1, id);
            rs = ps.executeQuery();
                                    
            if (!rs.next()) {
                return null;
            }
            RoleSecurity role = new RoleSecurity(rs.getLong(1), rs.getString(2), rs.getString(3));
             
            // select the contest permissions
            ps = conn.prepareStatement(
                "SELECT role_id, contest_id, permission_level_id FROM contest_permission WHERE role_id=?");
            ps.setLong(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                role.getContestPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));
            }
            
            // select the forum permissions
            ps = conn.prepareStatement(
                "SELECT role_id, forum_id, permission_level_id FROM forum_permission WHERE role_id=?");
            ps.setLong(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                role.getForumPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));
            }
            
            return role;
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the role with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    	
    }
    
    /**
     * <p>Gets all roles of given contestfrom persistence layer.</p>
     *
     * @return a list of RoleSecurity instances
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<RoleSecurity> getContestRoles(long contestId) throws PersistenceException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                
        try {
            conn = Database.createConnection();
            
            // select the roles;
            ps = conn.prepareStatement(
                    "SELECT role_id, name, description FROM role WHERE role_id IN (SELECT role_id FROM contest_permission WHERE contest_id=? AND permission_level_id>1)");
            ps.setLong(1, contestId);
            rs = ps.executeQuery();
            
            List<RoleSecurity> roles = new ArrayList<RoleSecurity>();
            while (rs.next()) {
                RoleSecurity role = new RoleSecurity(rs.getLong(1), rs.getString(2), rs.getString(3));                                  
                roles.add(role);
            }     
            
            return roles; 
            
        } catch (SQLException e) {
            throw new PersistenceException("Failed to get all roles", e);
        } finally {
            Database.dispose(conn, ps, rs);
        }                   
    }


    
    /**
     * <p>Gets all roles from persistence layer.</p>
     *
     * @return a list of RoleSecurity instances
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<RoleSecurity> getAllRoles() throws PersistenceException {
    	
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            
            // select the roles;
            ps = conn.prepareStatement(
                    "SELECT role_id, name, description FROM role");            
            rs = ps.executeQuery();
            
            List<RoleSecurity> roles = new ArrayList<RoleSecurity>();
            Map<Long, RoleSecurity> roleIds = new HashMap<Long, RoleSecurity>();
            while (rs.next()) {
            	RoleSecurity role = new RoleSecurity(rs.getLong(1), rs.getString(2), rs.getString(3));                      			
            	roles.add(role);
                roleIds.put(role.getId(), role);
            }     
            
            // select the contests permissions
            ps = conn.prepareStatement(
                "SELECT role_id, contest_id, permission_level_id FROM contest_permission");            
            rs = ps.executeQuery();
                  
            while (rs.next()) {
                RoleSecurity role = roleIds.get(rs.getLong(1));
                role.getContestPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));                
            }       
            
            // select the forum permissions
            ps = conn.prepareStatement(
                "SELECT role_id, forum_id, permission_level_id FROM forum_permission");            
            rs = ps.executeQuery();
                  
            while (rs.next()) {
                RoleSecurity role = roleIds.get(rs.getLong(1));
                role.getForumPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));                
            }   
            return roles; 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get all roles", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }       	    	
    }

    /**
     * <p>Updates the given UserSecurity instance in persistence layer.</p>
     *
     * @param security the specified UserSecurity to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateUserSecurity(UserSecurity security, long user) throws PersistenceException {
    	
    }

    
    public void addUserRole(long userProfileId, long roleId) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;                        
                
        try {
            conn = Database.createConnection();
            ps = conn.prepareStatement("INSERT INTO user_role(user_profile_id, role_id) VALUES(?,?)");            
            ps.setLong(1, userProfileId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Failed to add user role", e);
        } finally {
            Database.dispose(conn, ps, rs);
        }       
    }
    
    public void deleteUserRole(long userProfileId, long roleId) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;                        
                
        try {
            conn = Database.createConnection();
            ps = conn.prepareStatement("DELETE FROM user_role WHERE user_profile_id=? AND role_id=?");            
            ps.setLong(1, userProfileId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Failed to delete user role", e);
        } finally {
            Database.dispose(conn, ps, rs);
        }       
    }
    
    /**
     * <p>Gets a UserSecurity instance with the given user id from persistence layer.</p>
     *
     * @param userProfileId the id of user profile used to get the UserSecurity instance
     * @return the UserSecurity instance with the given user id
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public UserSecurity getUserSecurity(long userProfileId) throws PersistenceException {
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;            	        
        
        String GET_SUPER_ADMIN = "SELECT super_admin FROM user_profile where user_profile_id=?";
        
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_SUPER_ADMIN);   
            ps.setLong(1, userProfileId);
            rs = ps.executeQuery();
            boolean superAdmin = false;         
            if (rs.next()) { 
                superAdmin = rs.getBoolean("super_admin");                
            } else {
            	return null;
            } 
            
            UserSecurity security = new UserSecurity(userProfileId, superAdmin);                        
            
            // select the roles;
            ps = conn.prepareStatement(
                    "SELECT role_id, name, description FROM role " +
                    "WHERE role_id IN " +
                    "(SELECT role_id from user_role WHERE user_profile_id = ?)");
            ps.setLong(1, userProfileId);
            rs = ps.executeQuery();
            
            List<RoleSecurity> roles = new ArrayList<RoleSecurity>();
            Map<Long, RoleSecurity> roleIds = new HashMap<Long, RoleSecurity>();
            while (rs.next()) {
                RoleSecurity role = new RoleSecurity(rs.getLong(1), rs.getString(2), rs.getString(3));                                  
                roles.add(role);
                roleIds.put(role.getId(), role);
            }     
            
            // select the contests permissions
            ps = conn.prepareStatement(
                "SELECT role_id, contest_id, permission_level_id FROM contest_permission " +
                "WHERE role_id IN " +
                "(SELECT role_id from user_role WHERE user_profile_id = ?)");    
            ps.setLong(1, userProfileId);
            rs = ps.executeQuery();
                  
            while (rs.next()) {
                RoleSecurity role = roleIds.get(rs.getLong(1));
                role.getContestPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));                
            }       
            
            // select the forum permissions
            ps = conn.prepareStatement(
                "SELECT role_id, forum_id, permission_level_id FROM forum_permission " +
                "WHERE role_id IN " +
                "(SELECT role_id from user_role WHERE user_profile_id = ?)"); 
            ps.setLong(1, userProfileId);
            rs = ps.executeQuery();
                  
            while (rs.next()) {
                RoleSecurity role = roleIds.get(rs.getLong(1));
                role.getForumPermission().addPermission(rs.getLong(2), PermissionLevel.findById(rs.getLong(3)));                
            }   
            
            for (RoleSecurity role : roles) {
                security.importRole(role);
            }
                
            return security;
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get user security with id " + userProfileId, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   		    	     	    	
    }

    public Map<String, Boolean> addRoleUsers(List<String> users, long roleId) throws PersistenceException {
        return manageRoleUsers(users, roleId, false);        
    }
    public Map<String, Boolean> removeRoleUsers(List<String> users, long roleId) throws PersistenceException {
        return manageRoleUsers(users, roleId, true);        
    }
    
    private Map<String, Boolean> manageRoleUsers(List<String> users, long roleId, boolean remove) throws PersistenceException {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        if (users.size() == 0) {
            return result;
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;                        
                
        Map<Long, String> idMap = new HashMap<Long, String>();
                
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            String GET_USER_ID = "SELECT handle, user_profile_id FROM user_profile where handle IN " + 
                Database.createValues(users);        
             
            ps = conn.prepareStatement(GET_USER_ID);   
            rs = ps.executeQuery();
            while (rs.next()) { 
                String handle = rs.getString(1);
                long id = rs.getLong(2);
                idMap.put(id, handle);
                result.put(handle, !remove);
            } 
            List<Long> existingIds = new ArrayList<Long>();
            if (idMap.size() > 0) {
                String GET_ROLE_USER_ID = "SELECT user_profile_id FROM user_role " +
                        "where role_id = ? AND user_profile_id IN " + Database.createNumberValues(idMap.keySet());
                ps = conn.prepareStatement(GET_ROLE_USER_ID); 
                ps.setLong(1, roleId);
                rs = ps.executeQuery();
                
                while (rs.next()) { 
                    long id = rs.getLong(1);
                    result.put(idMap.get(id), remove);
                    existingIds.add(id);
                } 
            }
            
            if (remove) {
                if (existingIds.size() >0) {
                    String DELETE_ROLE_USER = "DELETE FROM user_role " +
                            "WHERE role_id=? AND user_profile_id IN " + Database.createNumberValues(existingIds);
                    ps = conn.prepareStatement(DELETE_ROLE_USER); 
                    ps.setLong(1, roleId);
                    ps.executeUpdate();
                }
            } else {
                String ADD_ROLE_USER = "INSERT INTO user_role(role_id, user_profile_id) VALUES(?,?)";
                for (Long id : idMap.keySet()) {
                    String handle = idMap.get(id);
                    if (result.get(handle)) {
                        ps = conn.prepareStatement(ADD_ROLE_USER); 
                        ps.setLong(1, roleId);
                        ps.setLong(2, id); 
                        ps.executeUpdate();
                    }
                }                
            }
        
            conn.commit();
        } catch (SQLException e) {
            Database.rollback(conn);
            throw new PersistenceException("Failed to manage role users.", e);
        } finally {
            Database.dispose(conn, ps, null);
        }
        return result;
    }

}


