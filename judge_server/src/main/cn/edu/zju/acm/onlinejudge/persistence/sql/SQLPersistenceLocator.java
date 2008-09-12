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

package cn.edu.zju.acm.onlinejudge.persistence.sql;

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

/**
 * <p>A SQL PersistenceLocator implementation..</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class SQLPersistenceLocator extends PersistenceLocator {
	
    /**
     * <p>Constructor of SQLPersistenceLocator class.</p>
     *
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public SQLPersistenceLocator() throws PersistenceCreationException {
        // empty
    }

    /**
     * <p>Creates a UserPersistence implementation.</p>
     *
     * @return a UserPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public UserPersistence createUserPersistence() throws PersistenceCreationException {
    	return new UserPersistenceImpl();    	
    }

    /**
     * <p>Creates an AuthorizationPersistence implementation.</p>
     *
     * @return an AuthorizationPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public AuthorizationPersistence createAuthorizationPersistence() throws PersistenceCreationException {
        return new AuthorizationPersistenceImpl();
    }

    /**
     * <p>Creates a ContestPersistence implementation.</p>
     *
     * @return a ContestPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public ContestPersistence createContestPersistence() throws PersistenceCreationException {
        return new ContestPersistenceImpl();
    }

    /**
     * <p>Creates a ProblemPersistence implementation.</p>
     *
     * @return a ProblemPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public ProblemPersistence createProblemPersistence() throws PersistenceCreationException {
        return new ProblemPersistenceImpl();
    }

    /**
     * <p>Creates a SubmissionPersistence implementation.</p>
     *
     * @return a SubmissionPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public SubmissionPersistence createSubmissionPersistence() throws PersistenceCreationException {
        return new SubmissionPersistenceImpl();
    }

    /**
     * <p>Creates a ForumPersistence implementation.</p>
     *
     * @return a ForumPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public ForumPersistence createForumPersistence() throws PersistenceCreationException {
        return new ForumPersistenceImpl();
    }

    /**
     * <p>Creates a ConfigurationPersistence implementation.</p>
     *
     * @return a ConfigurationPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public ConfigurationPersistence createConfigurationPersistence() throws PersistenceCreationException {
        return new ConfigurationPersistenceImpl();
    }

    /**
     * <p>Creates a ReferencePersistence implementation.</p>
     *
     * @return a ReferencePersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public ReferencePersistence createReferencePersistence() throws PersistenceCreationException {
        return new ReferencePersistenceImpl();
    }

}