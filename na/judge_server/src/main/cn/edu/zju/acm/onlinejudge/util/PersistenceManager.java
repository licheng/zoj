/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com> Xu Chuan <xuchuan@gmail.com>
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
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ConfigurationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ForumPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.LanguagePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.sql.AuthorizationPersistenceImpl;
import cn.edu.zju.acm.onlinejudge.persistence.sql.ConfigurationPersistenceImpl;
import cn.edu.zju.acm.onlinejudge.persistence.sql.ContestPersistenceImpl;
import cn.edu.zju.acm.onlinejudge.persistence.sql.ForumPersistenceImpl;
import cn.edu.zju.acm.onlinejudge.persistence.sql.LanguagePersistenceImpl;
import cn.edu.zju.acm.onlinejudge.persistence.sql.ProblemPersistenceImpl;
import cn.edu.zju.acm.onlinejudge.persistence.sql.ReferencePersistenceImpl;
import cn.edu.zju.acm.onlinejudge.persistence.sql.SubmissionPersistenceImpl;
import cn.edu.zju.acm.onlinejudge.persistence.sql.UserPersistenceImpl;

/**
 * <p>
 * PersistenceManager class.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 * @author Xu, Chuan
 */
public class PersistenceManager {
    /**
     * UserPersistence.
     */
    private UserPersistence userPersistence = new UserPersistenceImpl();

    /**
     * ContestPersistence.
     */
    private ContestPersistence contestPersistence = new ContestPersistenceImpl();

    /**
     * AuthorizationPersistence.
     */
    private AuthorizationPersistence authorizationPersistence = new AuthorizationPersistenceImpl();

    /**
     * SubmissionPersistence.
     */
    private SubmissionPersistence submissionPersistence = new SubmissionPersistenceImpl();

    /**
     * ForumPersistence.
     */
    private ForumPersistence forumPersistence = new ForumPersistenceImpl();

    /**
     * ConfigurationPersistence.
     */
    private ConfigurationPersistence configurationPersistence = new ConfigurationPersistenceImpl();

    /**
     * ReferencePersistence.
     */
    private ReferencePersistence referencePersistence = new ReferencePersistenceImpl();

    /**
     * ProblemPersistence.
     */
    private ProblemPersistence problemPersistence = new ProblemPersistenceImpl();

    private LanguagePersistence languagePersistence = new LanguagePersistenceImpl();

    /**
     * PersistenceManager.
     */
    private static PersistenceManager instance;

    static {
        try {
            PersistenceManager.instance = new PersistenceManager();
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
    }

    /**
     * Gets the singleton instance.
     * 
     * @return the singleton instance.
     * @throws PersistenceCreationException
     *             if any exceptions occurs while creating the persistence
     */
    public static PersistenceManager getInstance() {
        return PersistenceManager.instance;
    }

    /**
     * <p>
     * Gets a UserPersistence implementation.
     * </p>
     * 
     * @return a UserPersistence implementation
     */
    public UserPersistence getUserPersistence() {
        return this.userPersistence;
    }

    /**
     * <p>
     * Gets an AuthorizationPersistence implementation.
     * </p>
     * 
     * @return an AuthorizationPersistence implementation
     */
    public AuthorizationPersistence getAuthorizationPersistence() {
        return this.authorizationPersistence;
    }

    /**
     * <p>
     * Gets a ContestPersistence implementation.
     * </p>
     * 
     * @return a ContestPersistence implementation
     */
    public ContestPersistence getContestPersistence() {
        return this.contestPersistence;
    }

    /**
     * <p>
     * Gets a ProblemPersistence implementation.
     * </p>
     * 
     * @return a ProblemPersistence implementation
     */
    public ProblemPersistence getProblemPersistence() {
        return this.problemPersistence;
    }

    /**
     * <p>
     * Gets a SubmissionPersistence implementation.
     * </p>
     * 
     * @return a SubmissionPersistence implementation
     */
    public SubmissionPersistence getSubmissionPersistence() {
        return this.submissionPersistence;
    }

    /**
     * <p>
     * Gets a ForumPersistence implementation.
     * </p>
     * 
     * @return a ForumPersistence implementation
     */
    public ForumPersistence getForumPersistence() {
        return this.forumPersistence;
    }

    /**
     * <p>
     * Gets a ConfigurationPersistence implementation.
     * </p>
     * 
     * @return a ConfigurationPersistence implementation
     */
    public ConfigurationPersistence getConfigurationPersistence() {
        return this.configurationPersistence;
    }

    /**
     * <p>
     * Gets a ReferencePersistence implementation.
     * </p>
     * 
     * @return a ReferencePersistence implementation
     */
    public ReferencePersistence getReferencePersistence() {
        return this.referencePersistence;
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

        for (Country c : this.userPersistence.getAllCountries()) {
            if (c.getId() == countryId) {
                return c;
            }
        }
        return null;

    }

    public LanguagePersistence getLanguagePersistence() {
        return this.languagePersistence;
    }

}