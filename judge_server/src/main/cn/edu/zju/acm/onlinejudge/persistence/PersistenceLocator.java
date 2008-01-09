/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence;

import cn.edu.zju.acm.onlinejudge.persistence.sql.SQLPersistenceLocator;

/**
 * <p>UserProfilePersistence interface defines the API used to manager the user profile related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public abstract class PersistenceLocator {

    /**
     * <p>The singleton instance of this class.</p>
     */
    private static PersistenceLocator instance = null;


    /**
     * <p>Protected constructor of PersistenceLocator class.</p>
     *
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    protected PersistenceLocator() throws PersistenceCreationException {
        // empty
    }

    /**
     * <p>Returns the singleton instanc of this class.</p>
     *
     * @return the singleton instanc of this class
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public static PersistenceLocator getInstance() throws PersistenceCreationException {
        if (instance == null) {
            instance = new SQLPersistenceLocator();
        }

        return instance;
    }


    /**
     * <p>Creates a UserPersistence implementation.</p>
     *
     * @return a UserPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public abstract UserPersistence createUserPersistence() throws PersistenceCreationException;

    /**
     * <p>Creates an AuthorizationPersistence implementation.</p>
     *
     * @return an AuthorizationPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public abstract AuthorizationPersistence createAuthorizationPersistence() throws PersistenceCreationException;

    /**
     * <p>Creates a ContestPersistence implementation.</p>
     *
     * @return a ContestPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public abstract ContestPersistence createContestPersistence() throws PersistenceCreationException;

    /**
     * <p>Creates a ProblemPersistence implementation.</p>
     *
     * @return a ProblemPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public abstract ProblemPersistence createProblemPersistence() throws PersistenceCreationException;

    /**
     * <p>Creates a SubmissionPersistence implementation.</p>
     *
     * @return a SubmissionPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public abstract SubmissionPersistence createSubmissionPersistence() throws PersistenceCreationException;

    /**
     * <p>Creates a ForumPersistence implementation.</p>
     *
     * @return a ForumPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public abstract ForumPersistence createForumPersistence() throws PersistenceCreationException;

    /**
     * <p>Creates a ConfigurationPersistence implementation.</p>
     *
     * @return a ConfigurationPersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public abstract ConfigurationPersistence createConfigurationPersistence() throws PersistenceCreationException;

    /**
     * <p>Creates a ReferencePersistence implementation.</p>
     *
     * @return a ReferencePersistence implementation
     * @throws PersistenceCreationException if any exceptions occurs while creating the persistence
     */
    public abstract ReferencePersistence createReferencePersistence() throws PersistenceCreationException;

}
