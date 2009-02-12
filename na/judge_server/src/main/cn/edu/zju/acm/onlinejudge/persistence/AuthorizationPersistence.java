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

package cn.edu.zju.acm.onlinejudge.persistence;

import java.util.List;
import java.util.Map;

import cn.edu.zju.acm.onlinejudge.security.RoleSecurity;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;

/**
 * <p>
 * AuthorizationPersistence interface defines the API used to manager the authorization related affairs in persistence
 * layer.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 */
public interface AuthorizationPersistence {

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
    void createRole(RoleSecurity role, long user) throws PersistenceException;

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
    void updateRole(RoleSecurity role, long user) throws PersistenceException;

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
    void deleteRole(long id, long user) throws PersistenceException;

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
    RoleSecurity getRole(long id) throws PersistenceException;

    /**
     * <p>
     * Gets all roles from persistence layer.
     * </p>
     * 
     * @return a list of RoleSecurity instances
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    List<RoleSecurity> getAllRoles() throws PersistenceException;

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
    void updateUserSecurity(UserSecurity security, long user) throws PersistenceException;

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
    UserSecurity getUserSecurity(long userProfileId) throws PersistenceException;

    void addUserRole(long userProfileId, long roleId) throws PersistenceException;

    void deleteUserRole(long userProfileId, long roleId) throws PersistenceException;

    Map<String, Boolean> addRoleUsers(List<String> users, long roleId) throws PersistenceException;

    Map<String, Boolean> removeRoleUsers(List<String> users, long roleId) throws PersistenceException;

    List<RoleSecurity> getContestRoles(long contestId) throws PersistenceException;
}
