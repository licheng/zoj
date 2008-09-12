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

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Country;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceLocator;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ForumPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ConfigurationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.sql.SQLPersistenceLocator;

/**
 * <p>
 * PersistenceManager class.
 * </p>
 * 
 * @version 2.0
 * @author ZOJDEV
 */
public class PersistenceManager {

    /**
     * PersistenceLocator.
     */
    PersistenceLocator locator = null;

    /**
     * UserPersistence.
     */
    private UserPersistence userPersistence = null;

    /**
     * ContestPersistence.
     */
    private ContestPersistence contestPersistence = null;

    /**
     * AuthorizationPersistence.
     */
    private AuthorizationPersistence authorizationPersistence = null;

    /**
     * SubmissionPersistence.
     */
    private SubmissionPersistence submissionPersistence = null;

    /**
     * ForumPersistence.
     */
    private ForumPersistence forumPersistence = null;

    /**
     * ConfigurationPersistence.
     */
    private ConfigurationPersistence configurationPersistence = null;

    /**
     * ReferencePersistence.
     */
    private ReferencePersistence referencePersistence = null;

    /**
     * ProblemPersistence.
     */
    private ProblemPersistence problemPersistence = null;

    /**
     * PersistenceManager.
     */
    private static PersistenceManager instance;

    static {
        try {
            instance = new PersistenceManager();
        } catch (PersistenceCreationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * <p>
     * Constructor of SQLPersistenceLocator class.
     * </p>
     * 
     * @throws PersistenceCreationException
     *             if any exceptions occurs while creating the persistence
     */
    private PersistenceManager() throws PersistenceCreationException {
        locator = new SQLPersistenceLocator();
        userPersistence = locator.createUserPersistence();
        contestPersistence = locator.createContestPersistence();
        authorizationPersistence = locator.createAuthorizationPersistence();
        submissionPersistence = locator.createSubmissionPersistence();
        forumPersistence = locator.createForumPersistence();
        configurationPersistence = locator.createConfigurationPersistence();
        referencePersistence = locator.createReferencePersistence();
        problemPersistence = locator.createProblemPersistence();
    }

    /**
     * Gets the singleton instance.
     * 
     * @return the singleton instance.
     * @throws PersistenceCreationException
     *             if any exceptions occurs while creating the persistence
     */
    public static PersistenceManager getInstance() {
        return instance;
    }

    /**
     * <p>
     * Gets a UserPersistence implementation.
     * </p>
     * 
     * @return a UserPersistence implementation
     */
    public UserPersistence getUserPersistence() {
        return userPersistence;
    }

    /**
     * <p>
     * Gets an AuthorizationPersistence implementation.
     * </p>
     * 
     * @return an AuthorizationPersistence implementation
     */
    public AuthorizationPersistence getAuthorizationPersistence() {
        return authorizationPersistence;
    }

    /**
     * <p>
     * Gets a ContestPersistence implementation.
     * </p>
     * 
     * @return a ContestPersistence implementation
     */
    public ContestPersistence getContestPersistence() {
        return contestPersistence;
    }

    /**
     * <p>
     * Gets a ProblemPersistence implementation.
     * </p>
     * 
     * @return a ProblemPersistence implementation
     */
    public ProblemPersistence getProblemPersistence() {
        return problemPersistence;
    }

    /**
     * <p>
     * Gets a SubmissionPersistence implementation.
     * </p>
     * 
     * @return a SubmissionPersistence implementation
     */
    public SubmissionPersistence getSubmissionPersistence() {
        return submissionPersistence;
    }

    /**
     * <p>
     * Gets a ForumPersistence implementation.
     * </p>
     * 
     * @return a ForumPersistence implementation
     */
    public ForumPersistence getForumPersistence() {
        return forumPersistence;
    }

    /**
     * <p>
     * Gets a ConfigurationPersistence implementation.
     * </p>
     * 
     * @return a ConfigurationPersistence implementation
     */
    public ConfigurationPersistence getConfigurationPersistence() {
        return configurationPersistence;
    }

    /**
     * <p>
     * Gets a ReferencePersistence implementation.
     * </p>
     * 
     * @return a ReferencePersistence implementation
     */
    public ReferencePersistence getReferencePersistence() {
        return referencePersistence;
    }

    /**
     * <p>
     * Gets a Country with given id.
     * </p>
     * 
     * @return a Country with given id.
     * @throws PersistenceException
     */
    public Country getCountry(String id) throws PersistenceException {
        long countryId = -1;
        try {
            countryId = Integer.parseInt(id);
        } catch (Exception e) {
            return null;
        }

        for (Country c : userPersistence.getAllCountries()) {
            if (c.getId() == countryId) {
                return c;
            }
        }
        return null;

    }

}