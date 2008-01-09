/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Forum;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;

/**
 * <p>Tests for ContestPersistenceImpl.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class ContestPersistenceImplTest extends TestCase {	
	
	/**
	 * A ForumPersistenceImpl instance.
	 */
	private ForumPersistenceImpl forumPersistence = new ForumPersistenceImpl();
	
	/**
	 * A ContestPersistenceImpl instance.
	 */
	private ContestPersistenceImpl persistence = new ContestPersistenceImpl();
	
	/**
	 * A Forum instance.
	 */
	private Forum forum1 = null;
	
	/**
	 * A Forum instance.
	 */
	private Forum forum2 = null;
	
	/**
	 * A Limit instance.
	 */
	private Limit limit1 = null;
	
	/**
	 * A Limit instance.
	 */
	private Limit limit2 = null;	
	
	/**
	 * A Contest instance.
	 */
	private Contest contest1 = null;
	
	/**
	 * A Contest instance.
	 */
	private Contest contest2 = null;
	
	/**
	 * A Problemset instance.
	 */
	private Problemset problemset1 = null;
	
	/**
	 * A Problemset instance.
	 */
	private Problemset problemset2 = null;
	
	/**
	 * A Language instance.
	 */
	private Language language1 = new Language(1, "language1", "Language 1", "compiler1", "options1");
	
	/**
	 * A Language instance.
	 */
	private Language language2 = new Language(2, "language2", "Language 2", "compiler2", "options2");
	
	/**
	 * A Language instance.
	 */
	private Language language3 = new Language(3, "language3", "Language 3", "compiler3", "options3");
	

	/**
	 * Setup.
	 * @throws Exception to JUnit
	 */
	protected void setUp() throws Exception {
		DatabaseHelper.resetAllTables(false);	
		
		persistence.createLanguage(language1, 10);
		persistence.createLanguage(language2, 10);
		persistence.createLanguage(language3, 10);
		
		forum1 = newForum(1);
		forum2 = newForum(2);
		forumPersistence.createForum(forum1, 10);
		forumPersistence.createForum(forum2, 10);
		
		limit1 = newLimit(1);
		limit2 = newLimit(2);
		
		contest1 = newContest(
				1, forum1.getId(), limit1, Arrays.asList(new Object[] {language1, language2, language3}));
		contest2 = newContest(
				2, forum2.getId(), null, null);
		problemset1 = newProblemset(
				3, forum1.getId(), limit2, Arrays.asList(new Object[] {language3}));
		problemset2 = newProblemset(
				4, forum2.getId(), null, new ArrayList());
		
		persistence.createContest(contest1, 10);
		persistence.createContest(contest2, 10);
		persistence.createContest(problemset1, 10);
		persistence.createContest(problemset2, 10);
					
		
	}
	
	/**
	 * Tear down.
	 * @throws Exception to JUnit
	 */
	protected void tearDown() throws Exception {				
		
		DatabaseHelper.clearTable("contest_language");
		DatabaseHelper.clearTable("contest");
		DatabaseHelper.clearTable("submission");
		DatabaseHelper.clearTable("limits");
		DatabaseHelper.clearTable("language");
		DatabaseHelper.clearTable("forum");		
							
	}
	
	
	/**
	 * Tests getContest method
	 * @throws Exception to JUnit
	 */
	public void testGetContest1() throws Exception {		
		Contest contest = (Contest) persistence.getContest(contest1.getId());		
		checkAbstractContest(contest1, contest);
		assertEquals("wrong start time", contest.getStartTime(), contest1.getStartTime());
		assertEquals("wrong end time", contest.getEndTime(), contest1.getEndTime());		
	}
	
	/**
	 * Tests getContest method
	 * @throws Exception to JUnit
	 */
	public void testGetContest2() throws Exception {		
		Contest contest = (Contest) persistence.getContest(contest2.getId());		
		checkAbstractContest(contest2, contest);
		assertEquals("wrong start time", contest.getStartTime(), contest2.getStartTime());
		assertEquals("wrong end time", contest.getEndTime(), contest2.getEndTime());		
	}	
	
	/**
	 * Tests getContest method
	 * @throws Exception to JUnit
	 */
	public void testGetContest3() throws Exception {		
		Problemset contest = (Problemset) persistence.getContest(problemset1.getId());		
		checkAbstractContest(problemset1, contest);				
	}
	
	/**
	 * Tests getContest method
	 * @throws Exception to JUnit
	 */
	public void testGetContest4() throws Exception {		
		Problemset contest = (Problemset) persistence.getContest(problemset2.getId());		
		checkAbstractContest(problemset2, contest);				
	}
	
	/**
	 * Tests getContest method
	 * @throws Exception to JUnit
	 */
	public void testGetContest5() throws Exception {		
		AbstractContest contest = (AbstractContest) persistence.getContest(1234567890l);
		assertNull("no such contest", contest);				
	}
	
	/**
	 * Tests updateContest method
	 * @throws Exception to JUnit
	 */
	public void testUpdateContest1() throws Exception {		
		contest1.setDescription("new");
		contest1.setTitle("new");
		contest1.setLanguages(null);
		contest1.setLimit(null);
		contest1.setForumId(forum2.getId());
		contest1.setStartTime(new Date(20000));
		contest1.setEndTime(new Date(30000));
		
		persistence.updateContest(contest1, 11);
		
		Contest contest = (Contest) persistence.getContest(contest1.getId());		
		checkAbstractContest(contest1, contest);
		assertEquals("wrong start time", contest.getStartTime(), contest1.getStartTime());
		assertEquals("wrong end time", contest.getEndTime(), contest1.getEndTime());		
		
	}
	
	/**
	 * Tests updateContest method
	 * @throws Exception to JUnit
	 */
	public void testUpdateContest2() throws Exception {		
		contest2.setDescription("new");
		contest2.setTitle("new");
		contest2.setLanguages(Arrays.asList(new Object[] {language1, language2, language3}));
		contest2.setLimit(limit2);
		contest2.setForumId(forum1.getId());
		contest2.setStartTime(new Date(50000));
		contest2.setEndTime(new Date(60000));
		
		persistence.updateContest(contest2, 11);
		
		Contest contest = (Contest) persistence.getContest(contest2.getId());		
		checkAbstractContest(contest2, contest);
		assertEquals("wrong start time", contest.getStartTime(), contest2.getStartTime());
		assertEquals("wrong end time", contest.getEndTime(), contest2.getEndTime());		
		
	}

	/**
	 * Tests updateContest method
	 * @throws Exception to JUnit
	 */
	public void testUpdateContest3() throws Exception {		
		problemset1.setDescription("new");
		problemset1.setTitle("new");
		problemset1.setLanguages(null);
		problemset1.setLimit(null);
		problemset1.setForumId(forum2.getId());
		
		persistence.updateContest(problemset1, 11);
		
		Problemset contest = (Problemset) persistence.getContest(problemset1.getId());		
		checkAbstractContest(problemset1, contest);				
		
	}
	
	/**
	 * Tests updateContest method
	 * @throws Exception to JUnit
	 */
	public void testUpdateContest4() throws Exception {		
		problemset2.setDescription("new");
		problemset2.setTitle("new");
		problemset2.setLanguages(Arrays.asList(new Object[] {language1, language2, language3}));
		problemset2.setLimit(limit2);
		problemset2.setForumId(forum1.getId());
		
		persistence.updateContest(problemset2, 11);
		
		Problemset contest = (Problemset) persistence.getContest(problemset2.getId());		
		checkAbstractContest(problemset2, contest);				
		
	}
	
	/**
	 * Tests deleteContest method
	 * @throws Exception to JUnit
	 */
	public void testDeleteContest1() throws Exception {					
		persistence.deleteContest(contest1.getId(), 11);
		assertNull("contest should be deleted", persistence.getContest(contest1.getId()));		
		persistence.deleteContest(contest2.getId(), 11);
		assertNull("contest should be deleted", persistence.getContest(contest2.getId()));
		persistence.deleteContest(problemset1.getId(), 11);
		assertNull("contest should be deleted", persistence.getContest(problemset1.getId()));
		persistence.deleteContest(problemset2.getId(), 11);
		assertNull("contest should be deleted", persistence.getContest(problemset2.getId()));
	}
	
	/**
	 * Tests deleteContest method
	 * @throws Exception to JUnit
	 */
	public void testDeleteContest2() throws Exception {					
		persistence.deleteContest(contest1.getId(), 11);
		persistence.deleteContest(contest2.getId(), 11);
		assertTrue("contests should be deleted", persistence.getAllContests().isEmpty());
		
	}	
	
	/**
	 * Tests deleteContest method
	 * @throws Exception to JUnit
	 */
	public void testDeleteContest3() throws Exception {					
		persistence.deleteContest(problemset1.getId(), 11);
		persistence.deleteContest(problemset2.getId(), 11);
		assertTrue("contests should be deleted", persistence.getAllProblemsets().isEmpty());
		
	}	
	
	
	/**
	 * Tests getAllLanguages method
	 * @throws Exception to JUnit
	 */
	public void testGetAllLanguages() throws Exception {		
		
		List languages = persistence.getAllLanguages();
		for (int i = 0; i < 3; ++i) {
			Language language = (Language) languages.get(i);
			long id = language.getId();			
			assertEquals("wrong name", "language" + id, language.getName());
			assertEquals("wrong desc", "Language " + id, language.getDescription());
			assertEquals("wrong options", "options" + id, language.getOptions());
			assertEquals("wrong compiler", "compiler" + id, language.getCompiler());						
		}
	}
	
	/**
	 * Tests createLanguage method
	 * @throws Exception to JUnit
	 */
	public void testCreateLanguage() throws Exception {		
		
		persistence.createLanguage(new Language(4, "language4", "Language 4", "compiler4", "options4"), 10);
		
		List languages = persistence.getAllLanguages();
		for (int i = 0; i < 4; ++i) {
			Language language = (Language) languages.get(i);
			long id = language.getId();			
			assertEquals("wrong name", "language" + id, language.getName());
			assertEquals("wrong desc", "Language " + id, language.getDescription());
			assertEquals("wrong options", "options" + id, language.getOptions());
			assertEquals("wrong compiler", "compiler" + id, language.getCompiler());						
		}
	}
	
	/**
	 * Tests updateLanguage method
	 * @throws Exception to JUnit
	 */
	public void testUpdateLanguage() throws Exception {		
		
		List languages = persistence.getAllLanguages();
		for (int i = 0; i < 3; ++i) {
			Language language = (Language) languages.get(i);			
			long id = language.getId();		
			persistence.updateLanguage(
					new Language(id, "new language" + id, "new Language " + id, "new compiler" + id, "new options" + id), 10);
		}
		
		languages = persistence.getAllLanguages();
		for (int i = 0; i < 3; ++i) {
			Language language = (Language) languages.get(i);
			long id = language.getId();			
			assertEquals("wrong name", "new language" + id, language.getName());
			assertEquals("wrong desc", "new Language " + id, language.getDescription());
			assertEquals("wrong options", "new options" + id, language.getOptions());
			assertEquals("wrong compiler", "new compiler" + id, language.getCompiler());						
		}		
	}
	
	/**
	 * Tests deleteLanguage method
	 * @throws Exception to JUnit
	 */
	public void testDeleteLanguage1() throws Exception {		
		
		List languages = persistence.getAllLanguages();
		for (int i = 0; i < 3; ++i) {
			Language language = (Language) languages.get(i);			
			long id = language.getId();		
			persistence.deleteLanguage(id, 10);
		}
			
		languages = persistence.getAllLanguages();
		assertEquals("wrong size", 0, languages.size());
		
	}
	
	/**
	 * Tests getAllContests method
	 * @throws Exception to JUnit
	 */
	public void testGetAllContests() throws Exception {
		List contests = persistence.getAllContests();
		assertEquals("wrong size", 2, contests.size());
		Contest contest = (Contest) contests.get(0);
		checkAbstractContest(contest1, contest);		
		assertEquals("wrong start time", contest.getStartTime(), contest1.getStartTime());
		assertEquals("wrong end time", contest.getEndTime(), contest1.getEndTime());
		
		contest = (Contest) contests.get(1);
		checkAbstractContest(contest2, contest);
		assertEquals("wrong start time", contest.getStartTime(), contest2.getStartTime());
		assertEquals("wrong end time", contest.getEndTime(), contest2.getEndTime());
				
	}
	
	/**
	 * Tests getAllProblemsets method
	 * @throws Exception to JUnit
	 */
	public void testGetAllProblemsets() throws Exception {
		List contests = persistence.getAllProblemsets();
		assertEquals("wrong size", 2, contests.size());
		Problemset contest = (Problemset) contests.get(0);
		checkAbstractContest(problemset1, contest);		
		
		contest = (Problemset) contests.get(1);
		checkAbstractContest(problemset2, contest);
						
	}	
	
	/**
	 * Creates a new limit.
	 * @param id the id
	 * @return a new limit instance
	 */
	private Limit newLimit(long id) {
		Limit limit = new Limit();
		limit.setId(id);
		limit.setTimeLimit((int) id * 10);
		limit.setMemoryLimit((int) id * 100);
		limit.setOutputLimit((int) id * 1000);
		limit.setSubmissionLimit((int) id * 10000);		
		return limit;
	}
	
	/**
	 * Creates a new problemset.
	 * @param id the id
	 * @param forumId the forum id
	 * @param limit the limit
	 * @param languages  a list of languages
	 * @return a new problemset instance
	 */
	private Problemset newProblemset(long id, long forumId, Limit limit, List languages) {
		Problemset contest = new Problemset();
		contest.setId(id);
		contest.setDescription("desc" + id);
		contest.setLimit(limit);
		contest.setLanguages(languages);
		contest.setTitle("title" + id);
		contest.setForumId(forumId);
		return contest;
	}
	

	
	/**
	 * Creates a new contest.
	 * @param id the id
	 * @param forumId the forum id
	 * @param limit the limit
	 * @param languages  a list of languages
	 * @return a new Contest instance
	 */
	private Contest newContest(long id, long forumId, Limit limit, List languages) {
		Contest contest = new Contest();
		contest.setId(id);
		contest.setDescription("desc" + id);
		contest.setLimit(limit);
		contest.setLanguages(languages);
		contest.setTitle("title" + id);
		contest.setForumId(forumId);
		contest.setStartTime(new Date(id * 1000));
		contest.setEndTime(new Date(id * 2000));
		return contest;
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
	 * Checks whether the two UserPreference instances are same.
	 * @param contest1 the expected contest
	 * @param contest2 the contest to check
	 */
	private void checkAbstractContest(AbstractContest contest1, AbstractContest contest2) {
		
		assertEquals("wrong id", contest1.getId(), contest2.getId());
		assertEquals("wrong title", contest1.getTitle(), contest2.getTitle());
		assertEquals("wrong desc", contest1.getDescription(), contest2.getDescription());
		assertEquals("wrong forum id", contest1.getForumId(), contest2.getForumId());
		checkLanguages(contest1.getLanguages(), contest2.getLanguages());
		checkLimit(contest1.getLimit(), contest2.getLimit());								
	}
	
	/**
	 * Checks whether the two Language instances are same.
	 * @param language1 the expected language
	 * @param language2 the language to check
	 */
	private void checkLanguages(List languages1, List languages2) {
		if (languages1 == null && languages2 == null) {
			return;
		}
		if (languages1 == null) {
			assertEquals("wrong size", 0, languages2.size());
		} else {
			assertEquals("wrong size", languages1.size(), languages1.size());
			Iterator it1 = languages1.iterator();
			Iterator it2 = languages2.iterator();
			while (it1.hasNext()) {
				checkLanguage((Language) it1.next(), (Language) it2.next());
			}
		}
	}
	
	/**
	 * Checks whether the two Language instances are same.
	 * @param language1 the expected language
	 * @param language2 the language to check
	 */
	private void checkLanguage(Language language1, Language language2) {
		
		assertEquals("wrong id", language1.getId(), language2.getId());
		assertEquals("wrong name", language1.getName(), language2.getName());
		assertEquals("wrong desc", language1.getDescription(), language2.getDescription());
		assertEquals("wrong options", language1.getOptions(), language2.getOptions());
		assertEquals("wrong compiler", language1.getCompiler(), language2.getCompiler());	
	}
	
	/**
	 * Checks whether the two Limit instances are same.
	 * @param limit1 the expected limit
	 * @param limit2 the limit to check
	 */
	private void checkLimit(Limit limit1, Limit limit2) {
		if (limit1 == null && limit2 == null) {
			return;
		}
		if (limit1 == null) {
			assertEquals("wrong id", 1, limit2.getId());	
			assertEquals("wrong time limit", 1, limit2.getTimeLimit());
			assertEquals("wrong memoty limit", 32768, limit2.getMemoryLimit());
			assertEquals("wrong output limit", 32768, limit2.getOutputLimit());
			assertEquals("wrong submission limit", 32, limit2.getSubmissionLimit());
		} else {
			assertEquals("wrong id", limit1.getId(), limit2.getId());	
			assertEquals("wrong time limit", limit1.getTimeLimit(), limit2.getTimeLimit());
			assertEquals("wrong memoty limit", limit1.getMemoryLimit(), limit2.getMemoryLimit());
			assertEquals("wrong output limit", limit1.getOutputLimit(), limit2.getOutputLimit());
			assertEquals("wrong submission limit", limit1.getSubmissionLimit(), limit2.getSubmissionLimit());
		}
		
	}
	
}


