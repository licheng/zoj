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

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.UserPreference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Country;
import cn.edu.zju.acm.onlinejudge.bean.request.UserCriteria;
import java.util.List;

/**
 * <p>UserPersistence interface defines the API used to manager the user profile related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public interface UserPersistence {

    /**
     * <p>Creates the specified user profile in persistence layer.</p>
     *
     * @param profile the UserProfile instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void createUserProfile(UserProfile profile, long user) throws PersistenceException;

    /**
     * <p>Updates the specified user profile in persistence layer.</p>
     *
     * @param profile the UserProfile instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void updateUserProfile(UserProfile profile, long user) throws PersistenceException;

    /**
     * <p>Deletes the specified user profile in persistence layer.</p>
     *
     * @param id the id of the user profile to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void deleteUserProfile(long id, long user) throws PersistenceException;

    /**
     * <p>Gets the user profile with given id in persistence layer.</p>
     *
     * @param id the id of the user profile
     * @return the user profile with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    UserProfile getUserProfile(long id) throws PersistenceException;


    /**
     * <p>Gets the user profile with given handle in persistence layer.</p>
     *
     * @param handle the handle of the user profile
     * @return the user profile with given handle in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    UserProfile getUserProfileByHandle(String handle) throws PersistenceException;
    
    UserProfile login(String handle, String password) throws PersistenceException;

    /**
     * <p>Gets the user profile with given email in persistence layer.</p>
     *
     * @param email the email of the user profile
     * @return the user profile with given email in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    UserProfile getUserProfileByEmail(String email) throws PersistenceException;

    /**
     * <p>Gets the user profile with given code in persistence layer.</p>
     *
     * @param code the code of the user profile
     * @return the user profile with given code in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    UserProfile getUserProfileByCode(String code) throws PersistenceException;

    /**
     * <p>Searchs all user profiles according with the given criteria in persistence layer.</p>
     *
     * @return a list of user profiles according with the given criteria
     * @param criteria the user profile search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of user profiles in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List<UserProfile> searchUserProfiles(UserCriteria criteria, int offset, int count) throws PersistenceException;
    
    int searchUserProfilesCount(UserCriteria criteria) throws PersistenceException;
    

    /**
     * <p>Creates the specified user preference in persistence layer.</p>
     *
     * @param preference the UserPreference instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void createUserPreference(UserPreference preference, long user) throws PersistenceException;

    /**
     * <p>Updates the specified user preference in persistence layer.</p>
     *
     * @param preference the UserPreference instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void updateUserPreference(UserPreference preference, long user) throws PersistenceException;


    /**
     * <p>Gets the user preference with given id in persistence layer.</p>
     *
     * @param id the id of the user preference
     * @return the user preference with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    UserPreference getUserPreference(long id) throws PersistenceException;

    /**
     * <p>Creates a confirm code for given user in persistence layer.</p>
     *
     * @param id the id of the user
     * @param code the confirm code
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void createConfirmCode(long id, String code, long user) throws PersistenceException;


    /**
     * <p>Deletes the confirm code of given user in persistence layer.</p>
     *
     * @param id the id of the user
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void deleteConfirmCode(long id, long user) throws PersistenceException;

    /**
     * <p>Gets the confirm code with given id in persistence layer.</p>
     *
     * @param id the id of the user
     * @return the confirm code of given user
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    String getConfirmCode(long id) throws PersistenceException;

    /**
     * <p>Gets all countries from the persistence layer.</p>
     *
     * @return a list containing all country names
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List<Country> getAllCountries() throws PersistenceException;
}


