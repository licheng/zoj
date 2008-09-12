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

import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;

/**
 * <p>
 * ReferencePersistence interface defines the API used to manager the reference related affairs in persistence layer.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 */
public interface ReferencePersistence {

    /**
     * <p>
     * Creates the specified problem reference in persistence layer.
     * </p>
     * 
     * @param problemId
     *            the id of the referred problem
     * @param reference
     *            the reference which the problem refer to
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    void createProblemReference(long problemId, Reference reference, long user) throws PersistenceException;

    /**
     * <p>
     * Creates the specified contest reference in persistence layer.
     * </p>
     * 
     * @param contestId
     *            the id of the referred contest
     * @param reference
     *            the reference which the contest refer to
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    void createContestReference(long contestId, Reference reference, long user) throws PersistenceException;

    /**
     * <p>
     * Creates the specified post reference in persistence layer.
     * </p>
     * 
     * @param postId
     *            the id of the referred post
     * @param reference
     *            the reference which the contest refer to
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    void createPostReference(long postId, Reference reference, long user) throws PersistenceException;

    /**
     * <p>
     * Updates the specified reference in persistence layer.
     * </p>
     * 
     * @param reference
     *            the Reference instance to update
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    void updateReference(Reference reference, long user) throws PersistenceException;

    /**
     * <p>
     * Deletes the specified reference in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the reference to delete
     * @param user
     *            the id of the user who made this modification
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    void deleteReference(long id, long user) throws PersistenceException;

    /**
     * <p>
     * Gets the reference with given id in persistence layer.
     * </p>
     * 
     * @param id
     *            the id of the reference
     * @return the reference with given id in persistence layer
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    Reference getReference(long id) throws PersistenceException;

    /**
     * <p>
     * Gets all problem references to the given problem with specified reference type.
     * </p>
     * 
     * @return a list containing all problem references to the given problem with specified reference type
     * @param problemId
     *            the id of the referred problem
     * @param referenceType
     *            the reference type of the returned references
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    List<Reference> getProblemReferences(long problemId, ReferenceType referenceType) throws PersistenceException;

    /**
     * <p>
     * Gets all problem reference without data to the given problem with specified reference type.
     * </p>
     * 
     * @return a list containing all problem references to the given problem with specified reference type
     * @param problemId
     *            the id of the referred problem
     * @param referenceType
     *            the reference type of the returned references
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<Reference> getProblemReferenceInfo(long problemId, ReferenceType referenceType) throws PersistenceException;

    /**
     * <p>
     * Gets all contest references to the given contest with specified reference type.
     * </p>
     * 
     * @return a list containing all contest references to the given contest with specified reference type
     * @param contestId
     *            the id of the referred contest
     * @param referenceType
     *            the reference type of the returned references
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    List<Reference> getContestReferences(long contestId, ReferenceType referenceType) throws PersistenceException;

    /**
     * <p>
     * Gets all post references to the given post with specified reference type.
     * </p>
     * 
     * @return a list containing all post references to the given post with specified reference type
     * @param postId
     *            the id of the referred post
     * @param referenceType
     *            the reference type of the returned references
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    List<Reference> getPostReferences(long postId, ReferenceType referenceType) throws PersistenceException;
}
