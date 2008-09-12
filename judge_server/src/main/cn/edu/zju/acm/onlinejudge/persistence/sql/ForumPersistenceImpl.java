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

import cn.edu.zju.acm.onlinejudge.bean.Forum;
import cn.edu.zju.acm.onlinejudge.bean.Thread;
import cn.edu.zju.acm.onlinejudge.bean.Post;
import cn.edu.zju.acm.onlinejudge.bean.request.ThreadCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ForumPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p>ForumPersistenceImpl implements ForumPersistence interface</p>
 * <p>ForumPersistence interface defines the API used to manager the forum related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class ForumPersistenceImpl implements ForumPersistence {
	
	/**
	 * The statement to create a forum.
	 */
	private static final String INSERT_FORUM = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}) VALUES(?, ?, ?, ?, ?, ?, 1)", 
							 new Object[] {DatabaseConstants.FORUM_TABLE, 
				  						   DatabaseConstants.FORUM_NAME,
				  						   DatabaseConstants.FORUM_DESCRIPTION,
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,				  						   
				  						   DatabaseConstants.FORUM_ACTIVE}); 
	/**
	 * The statement to update a forum.
	 */
	private static final String UPDATE_FORUM = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=? WHERE {5}=?", 
							 new Object[] {DatabaseConstants.FORUM_TABLE, 
				  						   DatabaseConstants.FORUM_NAME,
				  						   DatabaseConstants.FORUM_DESCRIPTION,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.FORUM_FORUM_ID}); 
	
	/**
	 * The statement to delete a forum.
	 */
	private static final String DELETE_FORUM = 
		MessageFormat.format("UPDATE {0} SET {1}=0, {2}=?, {3}=? WHERE {4}=?", 
							 new Object[] {DatabaseConstants.FORUM_TABLE, 
										   DatabaseConstants.FORUM_ACTIVE,
										   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.FORUM_FORUM_ID}); 
			
	/**
	 * The query to get all forums.
	 */
	private static final String GET_ALL_FORUMS = 
		MessageFormat.format("SELECT {0}, {1}, {2} FROM {3} WHERE {4}=1",
				 			 new Object[] {DatabaseConstants.FORUM_FORUM_ID, 
				   					       DatabaseConstants.FORUM_NAME,
				   					       DatabaseConstants.FORUM_DESCRIPTION,
				   					       DatabaseConstants.FORUM_TABLE,
				   					       DatabaseConstants.FORUM_ACTIVE});

	/**
	 * The query to get a forum.
	 */
	private static final String GET_FORUM = 
		MessageFormat.format("SELECT {0}, {1}, {2} FROM {3} WHERE {0}=? AND {4}=1",
				 			 new Object[] {DatabaseConstants.FORUM_FORUM_ID, 
				   					       DatabaseConstants.FORUM_NAME,
				   					       DatabaseConstants.FORUM_DESCRIPTION,
				   					       DatabaseConstants.FORUM_TABLE,
				   					       DatabaseConstants.FORUM_ACTIVE});
	
	
	/**
	 * The statement to create a thread.
	 */
	private static final String INSERT_THREAD = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}) VALUES(?, ?, ?, ?, ?, ?, ?, 1)", 
							 new Object[] {DatabaseConstants.THREAD_TABLE, 
				  						   DatabaseConstants.THREAD_FORUM_ID,
				  						   DatabaseConstants.THREAD_USER_PROFILE_ID,
				  						   DatabaseConstants.THREAD_TITLE,
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.THREAD_ACTIVE}); 
	/**
	 * The statement to update a thread.
	 */
	private static final String UPDATE_THREAD = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=? WHERE {6}=?", 
							 new Object[] {DatabaseConstants.THREAD_TABLE, 
				  						   DatabaseConstants.THREAD_FORUM_ID,
				  						   DatabaseConstants.THREAD_USER_PROFILE_ID,
				  						   DatabaseConstants.THREAD_TITLE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.THREAD_THREAD_ID}); 
	
	/**
	 * The statement to delete a thread.
	 */
	private static final String DELETE_THREAD = 
		MessageFormat.format("UPDATE {0} SET {1}=0, {2}=?, {3}=? WHERE {4}=?", 
							 new Object[] {DatabaseConstants.THREAD_TABLE, 
										   DatabaseConstants.THREAD_ACTIVE,
										   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.THREAD_THREAD_ID}); 
			
	/**
	 * The query to get all threads.
	 */
	private static final String GET_THREADS = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3} FROM {4} WHERE {5}=1",
				 			 new Object[] {DatabaseConstants.THREAD_THREAD_ID, 
										   DatabaseConstants.THREAD_FORUM_ID,
										   DatabaseConstants.THREAD_USER_PROFILE_ID,
										   DatabaseConstants.THREAD_TITLE,
										   DatabaseConstants.THREAD_TABLE,
				   					       DatabaseConstants.THREAD_ACTIVE});

	/**
	 * The query to get a thread.
	 */
	private static final String GET_THREAD = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3} FROM {4} WHERE {0}=? AND {5}=1",
				 			 new Object[] {DatabaseConstants.THREAD_THREAD_ID, 
										   DatabaseConstants.THREAD_FORUM_ID,
										   DatabaseConstants.THREAD_USER_PROFILE_ID,
										   DatabaseConstants.THREAD_TITLE,				   				   					       
				   					       DatabaseConstants.THREAD_TABLE,
				   					       DatabaseConstants.THREAD_ACTIVE});
	
	/**
	 * The statement to create a post.
	 */
	private static final String INSERT_POST = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}) VALUES(?, ?, ?, ?, ?, ?, ?, 1)", 
							 new Object[] {DatabaseConstants.POST_TABLE, 
				  						   DatabaseConstants.POST_THREAD_ID,
				  						   DatabaseConstants.POST_USER_PROFILE_ID,
				  						   DatabaseConstants.POST_CONTENT,
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.POST_ACTIVE}); 
	/**
	 * The statement to update a post.
	 */
	private static final String UPDATE_POST = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=? WHERE {6}=?", 
							 new Object[] {DatabaseConstants.POST_TABLE, 
				  						   DatabaseConstants.POST_THREAD_ID,
				  						   DatabaseConstants.POST_USER_PROFILE_ID,
				  						   DatabaseConstants.POST_CONTENT,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.POST_POST_ID}); 
	
	/**
	 * The statement to delete a post.
	 */
	private static final String DELETE_POST = 
		MessageFormat.format("UPDATE {0} SET {1}=0, {2}=?, {3}=? WHERE {4}=?", 
							 new Object[] {DatabaseConstants.POST_TABLE, 
										   DatabaseConstants.POST_ACTIVE,
										   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.POST_POST_ID}); 
			
	/**
	 * The query to get all posts.
	 */
	private static final String GET_POSTS = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3} FROM {4} WHERE {1}=? AND {5}=1 ORDER BY {0}",
				 			 new Object[] {DatabaseConstants.POST_POST_ID, 
										   DatabaseConstants.POST_THREAD_ID,
										   DatabaseConstants.POST_USER_PROFILE_ID,
										   DatabaseConstants.POST_CONTENT,
										   DatabaseConstants.POST_TABLE,
				   					       DatabaseConstants.POST_ACTIVE});

	/**
	 * The query to get a post.
	 */
	private static final String GET_POST = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3} FROM {4} WHERE {0}=? AND {5}=1",
				 			 new Object[] {DatabaseConstants.POST_POST_ID, 
										   DatabaseConstants.POST_THREAD_ID,
										   DatabaseConstants.POST_USER_PROFILE_ID,
										   DatabaseConstants.POST_CONTENT,				   				   					       
				   					       DatabaseConstants.POST_TABLE,
				   					       DatabaseConstants.POST_ACTIVE});				
	
	/**
	 * The query to get last id.
	 */
	private static final String GET_LAST_ID = "SELECT LAST_INSERT_ID()";
	
    /**
     * <p>Creates the specified forum in persistence layer.</p>
     *
     * @param forum the Forum instance to create
     * @param user the id of the user who made this modification
     * @throws NullPointerException if arguemtn is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createForum(Forum forum, long user) throws PersistenceException {    
    	if (forum == null) {
    		throw new NullPointerException("forum is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
            ps = conn.prepareStatement(INSERT_FORUM);  
            ps.setString(1, forum.getName());
            ps.setString(2, forum.getDescription());
            ps.setLong(3, user);
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            ps.setLong(5, user);
            ps.setTimestamp(6, new Timestamp(new Date().getTime()));
            ps.executeUpdate();
            
            ps = conn.prepareStatement(GET_LAST_ID);
            rs = ps.executeQuery();
            rs.next();            
            forum.setId(rs.getLong(1));
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create forum.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    	
    }

    /**
     * <p>Updates the specified forum in persistence layer.</p>
     *
     * @param forum the Forum instance to update
     * @param user the id of the user who made this modification
     * @throws NullPointerException if arguemtn is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateForum(Forum forum, long user) throws PersistenceException {  
    	if (forum == null) {
    		throw new NullPointerException("forum is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(UPDATE_FORUM);  
            ps.setString(1, forum.getName());
            ps.setString(2, forum.getDescription());
            ps.setLong(3, user);
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            ps.setLong(5, forum.getId());
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such forum");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to update forum.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }      	
    }

    /**
     * <p>Deletes the specified forum in persistence layer.</p>
     *
     * @param id the id of the forum to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteForum(long id, long user) throws PersistenceException {
    	
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(DELETE_FORUM);  
            ps.setLong(1, user);
            ps.setTimestamp(2, new Timestamp(new Date().getTime()));
            ps.setLong(3, id);
            
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such forum");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to delete forum.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }   
    }

    /**
     * <p>Get the forum with given id in persistence layer.</p>
     *
     * @param id the id of the forum
     * @return the forum with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public Forum getForum(long id) throws PersistenceException {  
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_FORUM);   
            ps.setLong(1, id);
            rs = ps.executeQuery();
                        
            if (rs.next()) {
            	Forum forum = new Forum();
            	forum.setId(rs.getLong(DatabaseConstants.FORUM_FORUM_ID));
            	forum.setName(rs.getString(DatabaseConstants.FORUM_NAME));            	
            	forum.setDescription(rs.getString(DatabaseConstants.FORUM_DESCRIPTION));            			
            	return forum;
            } else {
            	return null;
            } 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the forum with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Get all forums in persistence layer.</p>
     *
     * @return a list of Forum instances containing all forums in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<Forum> getAllForums() throws PersistenceException {    
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_ALL_FORUMS);            
            rs = ps.executeQuery();
            
            List<Forum> forums = new ArrayList<Forum>();
            while (rs.next()) {
            	Forum forum = new Forum();
            	forum.setId(rs.getLong(DatabaseConstants.FORUM_FORUM_ID));
            	forum.setName(rs.getString(DatabaseConstants.FORUM_NAME));            	
            	forum.setDescription(rs.getString(DatabaseConstants.FORUM_DESCRIPTION));            			
            	forums.add(forum);
            }            
            return forums; 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get all forums", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }


    /**
     * <p>Creates the specified thread in persistence layer.</p>
     *
     * @param thread the Thread instance to create
     * @param user the id of the user who made this modification
     * @throws NullPointerException if arguemtn is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createThread(Thread thread, long user) throws PersistenceException {
    	if (thread == null) {
    		throw new NullPointerException("thread is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
            ps = conn.prepareStatement(INSERT_THREAD);  
            ps.setLong(1, thread.getForumId());
            ps.setLong(2, thread.getUserProfileId());
            ps.setString(3, thread.getTitle());
            ps.setLong(4, user);
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            ps.setLong(6, user);
            ps.setTimestamp(7, new Timestamp(new Date().getTime()));
            ps.executeUpdate();
            
            ps = conn.prepareStatement(GET_LAST_ID);
            rs = ps.executeQuery();
            rs.next();            
            thread.setId(rs.getLong(1));
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create thread.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }     	
    }

    /**
     * <p>Updates the specified thread in persistence layer.</p>
     *
     * @param thread the Thread instance to update
     * @param user the id of the user who made this modification
     * @throws NullPointerException if arguemtn is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateThread(Thread thread, long user) throws PersistenceException {    
    	if (thread == null) {
    		throw new NullPointerException("thread is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(UPDATE_THREAD);  
            ps.setLong(1, thread.getForumId());
            ps.setLong(2, thread.getUserProfileId());
            ps.setString(3, thread.getTitle());
            ps.setLong(4, user);
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            ps.setLong(6, thread.getId());
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such thread");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to update thread.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }   
    }

    /**
     * <p>Deletes the specified thread in persistence layer.</p>
     *
     * @param id the id of the thread to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteThread(long id, long user) throws PersistenceException {    	
    	Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(DELETE_THREAD);  
            ps.setLong(1, user);
            ps.setTimestamp(2, new Timestamp(new Date().getTime()));
            ps.setLong(3, id);
            
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such thread");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to delete thread.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }   
    }

    /**
     * <p>Get the thread with given id in persistence layer.</p>
     *
     * @param id the id of the thread
     * @return the thread with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public Thread getThread(long id) throws PersistenceException {   
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_THREAD);   
            ps.setLong(1, id);
            rs = ps.executeQuery();
                        
            if (rs.next()) {
            	Thread thread = new Thread();
            	thread.setId(rs.getLong(DatabaseConstants.THREAD_THREAD_ID));
            	thread.setForumId(rs.getLong(DatabaseConstants.THREAD_FORUM_ID));
            	thread.setUserProfileId(rs.getLong(DatabaseConstants.THREAD_USER_PROFILE_ID));
            	thread.setTitle(rs.getString(DatabaseConstants.THREAD_TITLE));
            	return thread;
            } else {
            	return null;
            } 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the thread with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }


    /**
     * <p>Searchs all threads according with the given criteria in persistence layer.</p>
     *
     * @return a list of threads according with the given criteria
     * @param criteria the thread search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of thread in returned list
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<Thread> searchThreads(ThreadCriteria criteria, int offset, int count) throws PersistenceException {   
    	return null;
    }

    /**
     * <p>Creates the specified post in persistence layer.</p>
     *
     * @param post the Post instance to create
     * @param user the id of the user who made this modification
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createPost(Post post, long user) throws PersistenceException {    	
    	if (post == null) {
    		throw new NullPointerException("post is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
            ps = conn.prepareStatement(INSERT_POST);  
            ps.setLong(1, post.getThreadId());
            ps.setLong(2, post.getUserProfileId());
            ps.setString(3, post.getContent());
            ps.setLong(4, user);
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            ps.setLong(6, user);
            ps.setTimestamp(7, new Timestamp(new Date().getTime()));
            ps.executeUpdate();
            
            ps = conn.prepareStatement(GET_LAST_ID);
            rs = ps.executeQuery();
            rs.next();            
            post.setId(rs.getLong(1));
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create post.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }     	
    }

    /**
     * <p>Updates the specified post in persistence layer.</p>
     *
     * @param post the Post instance to update
     * @param user the id of the user who made this modification
     * @throws NullPointerException if arguemtn is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updatePost(Post post, long user) throws PersistenceException {    
    	if (post == null) {
    		throw new NullPointerException("post is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(UPDATE_POST);  
            ps.setLong(1, post.getThreadId());
            ps.setLong(2, post.getUserProfileId());
            ps.setString(3, post.getContent());
            ps.setLong(4, user);
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            ps.setLong(6, post.getId());
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such post");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to update post.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }   
    }

    /**
     * <p>Deletes the specified post in persistence layer.</p>
     *
     * @param id the id of the post to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deletePost(long id, long user) throws PersistenceException {   
    	Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(DELETE_POST);  
            ps.setLong(1, user);
            ps.setTimestamp(2, new Timestamp(new Date().getTime()));
            ps.setLong(3, id);
            
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("no such post");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to delete post.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }   
    }

    /**
     * <p>Gets the post with given id in persistence layer.</p>
     *
     * @param id the id of the post
     * @return the post with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public Post getPost(long id) throws PersistenceException {  
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_POST);   
            ps.setLong(1, id);
            rs = ps.executeQuery();
                        
            if (rs.next()) {
            	Post post = new Post();
            	post.setId(rs.getLong(DatabaseConstants.POST_POST_ID));
            	post.setThreadId(rs.getLong(DatabaseConstants.POST_THREAD_ID));
            	post.setUserProfileId(rs.getLong(DatabaseConstants.POST_USER_PROFILE_ID));
            	post.setContent(rs.getString(DatabaseConstants.POST_CONTENT));
            	return post;
            } else {
            	return null;
            } 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the post with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Gets all posts in the specified thread from persistence layer.</p>
     *
     * @param threadId the id of the thread
     * @param offset the offset of the start position to get the posts
     * @param count the maximum number of posts in returned list
     * @return a list of Post instances containing all posts in the specified thread
     * @throws IllegalArgumentException if offset or count is negative
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List<Post> getPosts(long threadId, int offset, int count) throws PersistenceException {
    	if (offset < 0) {
    		throw new IllegalArgumentException("offset should not be negative");
    	}
    	if (count < 0) {
    		throw new IllegalArgumentException("count should not be negative");
    	}
    	if (count == 0) {
    		return new ArrayList<Post>();
    	}
    	Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_POSTS);   
            ps.setLong(1, threadId);
            rs = ps.executeQuery();                     
            List<Post> posts = new ArrayList<Post>();
            int index = 0;
            while (rs.next() && index - offset < count) {
            	++index;            	
            	if (index > offset) {
            		Post post = new Post();
            		post.setId(rs.getLong(DatabaseConstants.POST_POST_ID));
            		post.setThreadId(rs.getLong(DatabaseConstants.POST_THREAD_ID));
            		post.setUserProfileId(rs.getLong(DatabaseConstants.POST_USER_PROFILE_ID));
            		post.setContent(rs.getString(DatabaseConstants.POST_CONTENT));
            		posts.add(post);
            	}            	
            } 
            return posts;
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the posts", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

}



