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

import cn.edu.zju.acm.onlinejudge.bean.Forum;
import cn.edu.zju.acm.onlinejudge.bean.Thread;
import cn.edu.zju.acm.onlinejudge.bean.Post;
import cn.edu.zju.acm.onlinejudge.bean.request.ThreadCriteria;
import java.util.List;


/**
 * <p>ForumPersistence interface defines the API used to manager the forum related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public interface ForumPersistence {

    /**
     * <p>Creates the specified forum in persistence layer.</p>
     *
     * @param forum the Forum instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void createForum(Forum forum, long user) throws PersistenceException;

    /**
     * <p>Updates the specified forum in persistence layer.</p>
     *
     * @param forum the Forum instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void updateForum(Forum forum, long user) throws PersistenceException;

    /**
     * <p>Deletes the specified forum in persistence layer.</p>
     *
     * @param id the id of the forum to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void deleteForum(long id, long user) throws PersistenceException;

    /**
     * <p>Get the forum with given id in persistence layer.</p>
     *
     * @param id the id of the forum
     * @return the forum with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    Forum getForum(long id) throws PersistenceException;

    /**
     * <p>Get all forums in persistence layer.</p>
     *
     * @return a list of Forum instances containing all forums in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List<Forum> getAllForums() throws PersistenceException;


    /**
     * <p>Creates the specified thread in persistence layer.</p>
     *
     * @param thread the Thread instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void createThread(Thread thread, long user) throws PersistenceException;

    /**
     * <p>Updates the specified thread in persistence layer.</p>
     *
     * @param thread the Thread instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void updateThread(Thread thread, long user) throws PersistenceException;

    /**
     * <p>Deletes the specified thread in persistence layer.</p>
     *
     * @param id the id of the thread to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void deleteThread(long id, long user) throws PersistenceException;

    /**
     * <p>Get the thread with given id in persistence layer.</p>
     *
     * @param id the id of the thread
     * @return the thread with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    Thread getThread(long id) throws PersistenceException;


    /**
     * <p>Searchs all threads according with the given criteria in persistence layer.</p>
     *
     * @return a list of threads according with the given criteria
     * @param criteria the thread search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of thread in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List<Thread> searchThreads(ThreadCriteria criteria, int offset, int count) throws PersistenceException;

    /**
     * <p>Creates the specified post in persistence layer.</p>
     *
     * @param post the Post instance to create
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void createPost(Post post, long user) throws PersistenceException;

    /**
     * <p>Updates the specified post in persistence layer.</p>
     *
     * @param post the Post instance to update
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void updatePost(Post post, long user) throws PersistenceException;

    /**
     * <p>Deletes the specified post in persistence layer.</p>
     *
     * @param id the id of the post to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void deletePost(long id, long user) throws PersistenceException;

    /**
     * <p>Gets the post with given id in persistence layer.</p>
     *
     * @param id the id of the post
     * @return the post with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    Post getPost(long id) throws PersistenceException;

    /**
     * <p>Gets all posts in the specified thread from persistence layer.</p>
     *
     * @param threadId the id of the thread
     * @param offset the offset of the start position to get the posts
     * @param count the maximum number of posts in returned list
     * @return a list of Post instances containing all posts in the specified thread
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List<Post> getPosts(long threadId, int offset, int count) throws PersistenceException;

}


