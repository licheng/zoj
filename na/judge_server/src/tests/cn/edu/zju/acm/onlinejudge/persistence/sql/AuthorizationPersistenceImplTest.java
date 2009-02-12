/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.edu.zju.acm.onlinejudge.bean.Forum;
import cn.edu.zju.acm.onlinejudge.bean.Thread;
import cn.edu.zju.acm.onlinejudge.bean.Post;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Country;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

/**
 * <p>Tests for AuthorizationPersistenceImpl.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class AuthorizationPersistenceImplTest extends TestCase {	
	
	/**
	 * A ForumPersistenceImpl instance.
	 */
	private ForumPersistenceImpl persistence = new ForumPersistenceImpl();
	
	/**
	 * A UserProfile instance.
	 */
	private UserProfile profile = new UserProfile();
	
	/**
	 * A Forum instance.
	 */
	private Forum forum1 = null;
	
	/**
	 * A Forum instance.
	 */
	private Forum forum2 = null;
	
	/**
	 * A Forum instance.
	 */
	private Forum forum3 = null;
	
	/**
	 * A Thread instance.
	 */
	private Thread thread1 = null;
	
	/**
	 * A Thread instance.
	 */
	private Thread thread2 = null;
	
	/**
	 * A Thread instance.
	 */
	private Thread thread3 = null;
	
			
	/**
	 * A Post instance.
	 */
	private Post post1 = null;
	
	/**
	 * A Post instance.
	 */
	private Post post2 = null;
	
	/**
	 * A Post instance.
	 */
	private Post post3 = null;
	
	/**
	 * A list containing some posts.
	 */
	private List thread3Posts = null;
	
	/**
	 * Setup.
	 * @throws Exception to JUnit
	 */
	protected void setUp() throws Exception {
		DatabaseHelper.resetAllTables(false);				
		
		profile.setHandle("myHandle");
		profile.setPassword("myPassword");
		profile.setEmail("myEmail");
		profile.setRegDate(new Date());
		profile.setFirstName("myFirstName");
		profile.setLastName("myLastName");
		profile.setAddressLine1("myAddressLine1");
		profile.setAddressLine2("myAddressLine2");
		profile.setCity("myCity");
		profile.setState("myState");
		profile.setCountry(new Country(1, "foo"));
		profile.setZipCode("myZipCode");
		profile.setPhoneNumber("myPhoneNumber");
		profile.setBirthDate(DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse("1/1/1980"));
		profile.setGender('M');            
		profile.setSchool("mySchool");
		profile.setMajor("myMajor");
		profile.setGraduateStudent(true);
		profile.setGraduationYear(2005);
		profile.setStudentNumber("myStudentNumber");
		profile.setConfirmed(false);		
		
		new UserPersistenceImpl().createUserProfile(profile, 1);
		
		forum1 = newForum(1);
		forum2 = newForum(2);
		forum3 = newForum(3);
		persistence.createForum(forum1, 1);		
		persistence.createForum(forum2, 1);
		persistence.createForum(forum3, 1);
		
		thread1 = newThread(1, forum1.getId(), profile.getId());
		thread2 = newThread(2, forum1.getId(), profile.getId());
		thread3 = newThread(3, forum2.getId(), profile.getId());
		persistence.createThread(thread1, 1);		
		persistence.createThread(thread2, 1);
		persistence.createThread(thread3, 1);	
		
		post1 = newPost(1, thread1.getId(), profile.getId());
		post2 = newPost(2, thread1.getId(), profile.getId());
		post3 = newPost(3, thread2.getId(), profile.getId());
		persistence.createPost(post1, 1);		
		persistence.createPost(post2, 1);
		persistence.createPost(post3, 1);

		thread3Posts = new ArrayList();
		for (int i = 1; i <= 10; ++i) {
			Post post = newPost(i, thread3.getId(), profile.getId());
			thread3Posts.add(post);
			persistence.createPost(post, 1);
		}
	}
	
	/**
	 * Tear down.
	 * @throws Exception to JUnit
	 */
	protected void tearDown() throws Exception {
		
		DatabaseHelper.clearTable("post");
		DatabaseHelper.clearTable("thread");
		DatabaseHelper.clearTable("forum");
		
		DatabaseHelper.clearTable("user_profile");	
				
	}
	
	/**
	 * Tests getAllForums method
	 * @throws Exception to JUnit
	 */
	public void testGetAllForums() throws Exception {		
		
		List forums = persistence.getAllForums();
		assertEquals("size is wrong", 3, forums.size());
				
		Set nameSet = new HashSet(Arrays.asList(new String[] {"forum1", "forum2", "forum3"}));		
		Set descSet = new HashSet(Arrays.asList(new String[] {
				"forum1 description", "forum2 description", "forum3 description"}));
		for (Iterator it = forums.iterator(); it.hasNext();) {
			Forum forum = (Forum) it.next();
			assertTrue("wrong name", nameSet.contains(forum.getName()));			
			assertTrue("wrong description", descSet.contains(forum.getDescription()));
			nameSet.remove(forum.getName());			
			descSet.remove(forum.getDescription());
			
		}
	}

	/**
	 * Tests getForum method
	 * @throws Exception to JUnit
	 */
	public void testGetForum() throws Exception {		
		
		List forums = persistence.getAllForums();
		for (Iterator it = forums.iterator(); it.hasNext();) {
			Forum forum = (Forum) it.next();
			Forum forum1 = persistence.getForum(forum.getId());
			checkForum(forum, forum1);			
		}
	}
	
	
	/**
	 * Tests createForum method
	 * @throws Exception to JUnit
	 */
	public void testCreateForum1() throws Exception {		
		
		Forum forum = new Forum();
		forum.setName("name");
		forum.setDescription("desc");
		
		persistence.createForum(forum, 1);
		
		Forum forum1 = persistence.getForum(forum.getId());
		checkForum(forum, forum1);		

	}
	
	/**
	 * Tests createForum method
	 * @throws Exception to JUnit
	 */
	public void testCreateForum2() throws Exception {		
		Forum forum1 = new Forum();
		forum1.setName("name1");
		forum1.setDescription("desc1");
		Forum forum2 = new Forum();
		forum2.setName("name2");
		forum2.setDescription("desc2");
		
	
		persistence.createForum(forum1, 1);
		persistence.createForum(forum2, 1);
		
		Forum forum11 = persistence.getForum(forum1.getId());
		checkForum(forum1, forum11);
		
		Forum forum22 = persistence.getForum(forum2.getId());
		checkForum(forum2, forum22);

	}	
	
	/**
	 * Tests updateForum method
	 * @throws Exception to JUnit
	 */
	public void testUpdateForum1() throws Exception {	
		
		forum1.setName("new name");
		forum1.setDescription("new desc");
		persistence.updateForum(forum1, 1);
		
		Forum forum11 = persistence.getForum(forum1.getId());
		checkForum(forum1, forum11);
		
	}
	
	/**
	 * Tests updateForum method
	 * @throws Exception to JUnit
	 */
	public void testUpdateForum2() throws Exception {	
		try {
			Forum forum = new Forum();
			forum.setName("foo");
			forum.setDescription("bar");			
			persistence.updateForum(forum, 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}				
	}
	
	/**
	 * Tests deleteForum method
	 * @throws Exception to JUnit
	 */
	public void testDeleteForum1() throws Exception {			
		persistence.deleteForum(forum1.getId(), 1);		
		assertNull("forum should be deleted", persistence.getForum(forum1.getId()));		
	}
	
	/**
	 * Tests deleteForum method
	 * @throws Exception to JUnit
	 */
	public void testDeleteForum2() throws Exception {	
		try {
			persistence.deleteForum(-1, 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}				
	}	
	
	/**
	 * Tests searchThreads method
	 * @throws Exception to JUnit
	 */
	public void testSearchThreads() throws Exception {		
		// TODO
	}

	/**
	 * Tests getThread method
	 * @throws Exception to JUnit
	 */
	public void testGetThread() throws Exception {		
		
		Thread thread = persistence.getThread(thread1.getId());
		checkThread(thread1, thread);		
	}
	
	
	/**
	 * Tests createThread method
	 * @throws Exception to JUnit
	 */
	public void testCreateThread1() throws Exception {		
		
		
		Thread thread = newThread(-1, forum1.getId(), profile.getId());
		persistence.createThread(thread, 1);
		
		Thread newThread = persistence.getThread(thread.getId());
		checkThread(thread, newThread);		

	}
	
	/**
	 * Tests createThread method
	 * @throws Exception to JUnit
	 */
	public void testCreateThread2() throws Exception {		
		
		try {
			persistence.createThread(newThread(1, -1, profile.getId()), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}	

	}
	
	/**
	 * Tests createThread method
	 * @throws Exception to JUnit
	 */
	public void testCreateThread3() throws Exception {		
		
		try {
			persistence.createThread(newThread(1, forum1.getId(), -1), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}	

	}
	
	/**
	 * Tests updateThread method
	 * @throws Exception to JUnit
	 */
	public void testUpdateThread1() throws Exception {
		thread1.setId(thread2.getId());
		persistence.updateThread(thread1, 1);
				
		Thread thread = persistence.getThread(thread1.getId());
		checkThread(thread1, thread);	
		
	}
	
	/**
	 * Tests updateThread method
	 * @throws Exception to JUnit
	 */
	public void testUpdateThread2() throws Exception {	
		
		try {
			persistence.updateThread(newThread(-1, forum1.getId(), profile.getId()), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}						
	}
	
	/**
	 * Tests updateThread method
	 * @throws Exception to JUnit
	 */
	public void testUpdateThread3() throws Exception {	
		
		try {
			persistence.updateThread(newThread(forum1.getId(), -1, profile.getId()), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}						
	}
	
	/**
	 * Tests updateThread method
	 * @throws Exception to JUnit
	 */
	public void testUpdateThread4() throws Exception {	
		
		try {
			persistence.updateThread(newThread(forum1.getId(), forum1.getId(), -1), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}						
	}
	
	/**
	 * Tests deleteThread method
	 * @throws Exception to JUnit
	 */
	public void testDeleteThread1() throws Exception {	
				
		persistence.deleteThread(thread1.getId(), 1);		
		assertNull("thread should be deleted", persistence.getThread(thread1.getId()));	
		
	}
	
	/**
	 * Tests deleteThread method
	 * @throws Exception to JUnit
	 */
	public void testDeleteThread2() throws Exception {	
		try {
			persistence.deleteThread(-1, 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}				
	}	
	
	/**
	 * Tests getPost method
	 * @throws Exception to JUnit
	 */
	public void testGetPost() throws Exception {		
		
		Post post = persistence.getPost(post1.getId());
		checkPost(post1, post);		
	}
	
	
	/**
	 * Tests createPost method
	 * @throws Exception to JUnit
	 */
	public void testCreatePost1() throws Exception {		
		
		
		Post post = newPost(-1, thread1.getId(), profile.getId());
		persistence.createPost(post, 1);
		
		Post newPost = persistence.getPost(post.getId());
		checkPost(post, newPost);		

	}
	
	/**
	 * Tests createPost method
	 * @throws Exception to JUnit
	 */
	public void testCreatePost2() throws Exception {		
		
		try {
			persistence.createPost(newPost(1, -1, profile.getId()), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}	

	}
	
	/**
	 * Tests createPost method
	 * @throws Exception to JUnit
	 */
	public void testCreatePost3() throws Exception {		
		
		try {
			persistence.createPost(newPost(1, thread1.getId(), -1), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}	

	}
	
	/**
	 * Tests updatePost method
	 * @throws Exception to JUnit
	 */
	public void testUpdatePost1() throws Exception {
		post1.setId(post2.getId());
		persistence.updatePost(post1, 1);
				
		Post post = persistence.getPost(post1.getId());
		checkPost(post1, post);	
		
	}
	
	/**
	 * Tests updatePost method
	 * @throws Exception to JUnit
	 */
	public void testUpdatePost2() throws Exception {	
		
		try {
			persistence.updatePost(newPost(-1, thread1.getId(), profile.getId()), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}						
	}
	
	/**
	 * Tests updatePost method
	 * @throws Exception to JUnit
	 */
	public void testUpdatePost3() throws Exception {	
		
		try {
			persistence.updatePost(newPost(thread1.getId(), -1, profile.getId()), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}						
	}
	
	/**
	 * Tests updatePost method
	 * @throws Exception to JUnit
	 */
	public void testUpdatePost4() throws Exception {	
		
		try {
			persistence.updatePost(newPost(thread1.getId(), thread1.getId(), -1), 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}						
	}
	
	/**
	 * Tests deletePost method
	 * @throws Exception to JUnit
	 */
	public void testDeletePost1() throws Exception {	
				
		persistence.deletePost(post1.getId(), 1);		
		assertNull("post should be deleted", persistence.getPost(post1.getId()));	
		
	}
	
	/**
	 * Tests deletePost method
	 * @throws Exception to JUnit
	 */
	public void testDeletePost2() throws Exception {	
		try {
			persistence.deletePost(-1, 1);
			fail("PersistenceException should be thrown");
		} catch (PersistenceException pe) {			
			// ok
		}				
	}	
	
	
	/**
	 * Tests getPosts method
	 * @throws Exception to JUnit
	 */
	public void testGetPosts1() throws Exception {		
		
		List posts = persistence.getPosts(thread3.getId(), 5, 0);
		assertEquals("the size is wrong", 0, posts.size());				
	}
	
	/**
	 * Tests getPosts method
	 * @throws Exception to JUnit
	 */
	public void testGetPosts2() throws Exception {		
		
		List posts = persistence.getPosts(thread3.getId(), 10, 10);
		assertEquals("the size is wrong", 0, posts.size());				
	}
	
	/**
	 * Tests getPosts method
	 * @throws Exception to JUnit
	 */
	public void testGetPosts3() throws Exception {		
		
		List posts = persistence.getPosts(thread3.getId(), 0, 10);		
		assertEquals("the size is wrong", 10, posts.size());
		checkPostList(thread3Posts, 0, posts, 0, 10);
	}
	
	/**
	 * Tests getPosts method
	 * @throws Exception to JUnit
	 */
	public void testGetPosts4() throws Exception {		
		
		List posts = persistence.getPosts(thread3.getId(), 3, 5);		
		assertEquals("the size is wrong", 5, posts.size());
		checkPostList(thread3Posts, 3, posts, 0, 5);
	}
	
	/**
	 * Tests getPosts method
	 * @throws Exception to JUnit
	 */
	public void testGetPosts5() throws Exception {		
		
		List posts = persistence.getPosts(thread3.getId(), 5, 10);		
		assertEquals("the size is wrong", 5, posts.size());
		checkPostList(thread3Posts, 5, posts, 0, 5);
	}
		
	/**
	 * Checks whether the two Forum instances are same.
	 * @param forum1 the expected forum
	 * @param forum2 the forum to check
	 */
	private void checkForum(Forum forum1, Forum forum2) {
		
		assertEquals("wrong id", forum1.getId(), forum2.getId());
		assertEquals("wrong Name", forum1.getName(), forum2.getName());
		assertEquals("wrong Description", forum1.getDescription(), forum2.getDescription());		
		
	}
	
	/**
	 * Checks whether the two Thread instances are same.
	 * @param thread1 the expected thread
	 * @param thread2 the thread to check
	 */
	private void checkThread(Thread thread1, Thread thread2) {
		
		assertEquals("wrong id", thread1.getId(), thread2.getId());
		assertEquals("wrong forum id", thread1.getForumId(), thread2.getForumId());
		assertEquals("wrong usre id", thread1.getUserProfileId(), thread2.getUserProfileId());		
		assertEquals("wrong usre title", thread1.getTitle(), thread2.getTitle());		
	}
	
	/**
	 * Checks whether the two Post instances are same.
	 * @param post1 the expected post
	 * @param post2 the post to check
	 */
	private void checkPost(Post post1, Post post2) {
		
		assertEquals("wrong id", post1.getId(), post2.getId());
		assertEquals("wrong thread id", post1.getThreadId(), post2.getThreadId());
		assertEquals("wrong content", post1.getContent(), post2.getContent());				
	}
	
	/**
	 * Checks whether the two lists are same.
	 * @param posts1 the expected profile
	 * @param posts2 the profile to check
	 * @param offset1 the offset in post list 1.
	 * @param offset2 the offset in post list 2.
	 * @param count the count
	 */
	private void checkPostList(List posts1, int offset1, List posts2, int offset2, int count) {
		
		for (int i = 0; i < count; ++i) {
			Post post1 = (Post) posts1.get(offset1 + i);
			Post post2 = (Post) posts2.get(offset2 + i);
			checkPost(post1, post2);
		}			
	}
	
	
	/**
	 * Creates a new forum.
	 * @param id the id
	 * @return a new forum instance
	 */
	private Forum newForum(long id) {
		Forum forum = new Forum();
		forum.setId(id);
		forum.setName("forum" + id);
		forum.setDescription("forum" + id + " description");
		return forum;
	}
	
	/**
	 * Creates a new thread.
	 * @param id the id
	 * @param forumId the forum id
	 * @param userId the user id
	 * @return a new thread instance
	 */
	private Thread newThread(long id, long forumId, long userId) {
		Thread thread = new Thread();
		thread.setId(id);
		thread.setForumId(forumId);
		thread.setUserProfileId(userId);
		thread.setTitle("thread title" + id);
		return thread;
	}
	
	/**
	 * Creates a new post.
	 * @param id the id
	 * @param threadId the thread id
	 * @param userId the user id
	 * @return a new post instance
	 */
	private Post newPost(long id, long threadId, long userId) {
		Post post = new Post();
		post.setId(id);
		post.setThreadId(threadId);
		post.setUserProfileId(userId);
		post.setContent("post content" + id);		
		return post;
	}
}

