/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Forum;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;

/**
 * <p>Tests for ProblemPersistenceImpl.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class ProblemPersistenceImplTest extends TestCase {	
	
	/**
	 * A ForumPersistenceImpl instance.
	 */
	private ForumPersistenceImpl forumPersistence = new ForumPersistenceImpl();
	
	/**
	 * A ContestPersistenceImpl instance.
	 */
	private ContestPersistenceImpl contestPersistence = new ContestPersistenceImpl();
	
	/**
	 * A ProblemPersistenceImpl instance.
	 */
	private ProblemPersistenceImpl persistence = new ProblemPersistenceImpl();
	
	/**
	 * A Forum instance.
	 */
	private Forum forum = null;	
	
	/**
	 * A Limit instance.
	 */
	private Limit limit1 = null;
	
	/**
	 * A Limit instance.
	 */
	private Limit limit2 = null;	
	
	/**
	 * A Problem instance.
	 */
	private Problem problem1 = null;
	
	/**
	 * A Problem instance.
	 */
	private Problem problem2 = null;
	
	/**
	 * A Problem instance.
	 */
	private Problem problem3 = null;
	
	/**
	 * A Problemset instance.
	 */
	private Problemset problemset = null;
	
	/**
	 * A Contest instance.
	 */
	private Contest contest = null;
	
	/**
	 * A Language instance.
	 */
	private Language language = new Language(1, "language1", "Language 1", "compiler1", "options1");

	/**
	 * Setup.
	 * @throws Exception to JUnit
	 */
	protected void setUp() throws Exception {
		DatabaseHelper.resetAllTables(false);	
		
		forum = newForum(1);		
		forumPersistence.createForum(forum, 10);		
		
		contestPersistence.createLanguage(language, 10);
						
		limit1 = newLimit(1);
		limit2 = newLimit(2);		
		contest = newContest(
				1, forum.getId(), limit1, Arrays.asList(new Object[] {language}));
		problemset = newProblemset(
				3, forum.getId(), limit2, Arrays.asList(new Object[] {language}));
		contestPersistence.createContest(contest, 10);
		contestPersistence.createContest(problemset, 10);
		
		
		problem1 = newProblem(1, contest.getId(), limit1);
		problem2 = newProblem(2, contest.getId(), null);
		problem3 = newProblem(3, problemset.getId(), limit2);

		
		persistence.createProblem(problem1, 10);
		persistence.createProblem(problem2, 10);
		persistence.createProblem(problem3, 10);
					
		
	}
	
	/**
	 * Tear down.
	 * @throws Exception to JUnit
	 */
	protected void tearDown() throws Exception {				
				
		
		DatabaseHelper.clearTable("problem");
		DatabaseHelper.clearTable("contest_language");
		DatabaseHelper.clearTable("contest");
		DatabaseHelper.clearTable("submission");
		DatabaseHelper.clearTable("limits");
		DatabaseHelper.clearTable("language");
		DatabaseHelper.clearTable("forum");		
							
	}
	
	
	/**
	 * Tests getProblem method
	 * @throws Exception to JUnit
	 */
	public void testGetProblem1() throws Exception {		
		Problem problem = persistence.getProblem(problem1.getId());		
		checkProblem(problem1, problem);				
	}
	
	/**
	 * Tests getProblem method
	 * @throws Exception to JUnit
	 */
	public void testGetProblem2() throws Exception {		
		Problem problem = persistence.getProblem(problem2.getId());		
		checkProblem(problem2, problem);				
	}
	
	/**
	 * Tests getProblem method
	 * @throws Exception to JUnit
	 */
	public void testGetProblem3() throws Exception {		
		Problem problem = persistence.getProblem(problem3.getId());		
		checkProblem(problem3, problem);				
	}
	
	/**
	 * Tests getProblem method
	 * @throws Exception to JUnit
	 */
	public void testGetProblem4() throws Exception {		
		Problem problem = persistence.getProblem(1234567890l);		
		assertNull("no such problem", problem);				
	}
	
	/**
	 * Tests updateProblem method
	 * @throws Exception to JUnit
	 */
	public void testUpdateProblem1() throws Exception {
		long id = problem1.getId();
		problem1.setContestId(problemset.getId());
		problem1.setCode("new" + id);
		problem1.setAuthor("new author" + id);
		problem1.setChecker(id % 2 != 1);
		problem1.setContest("new contest" + id);
		problem1.setLimit(null);
		problem1.setRevision((int) id * 100);
		problem1.setSource("new source" + id);
		problem1.setTitle("new title" + id);		
	
		
		persistence.updateProblem(problem1, 11);
		
		Problem problem = (Problem) persistence.getProblem(problem1.getId());		
		checkProblem(problem1, problem);				
		
	}
	
	/**
	 * Tests updateProblem method
	 * @throws Exception to JUnit
	 */
	public void testUpdateProblem2() throws Exception {		
		long id = problem2.getId();
		problem2.setContestId(problemset.getId());
		problem2.setCode("new" + id);
		problem2.setAuthor("new author" + id);
		problem2.setChecker(id % 2 != 1);
		problem2.setContest("new contest" + id);
		problem2.setLimit(limit2);
		problem2.setRevision((int) id * 200);
		problem2.setSource("new source" + id);
		problem2.setTitle("new title" + id);		
			
		persistence.updateProblem(problem2, 11);
		
		Problem problem = (Problem) persistence.getProblem(problem2.getId());		
		checkProblem(problem2, problem);	
	}

	/**
	 * Tests updateProblem method
	 * @throws Exception to JUnit
	 */
	public void testUpdateProblem3() throws Exception {		
		long id = problem3.getId();
		problem3.setContestId(problemset.getId());
		problem3.setCode("new" + id);
		problem3.setAuthor("new author" + id);
		problem3.setChecker(id % 2 != 1);
		problem3.setContest("new contest" + id);
		problem3.setLimit(limit1);
		problem3.setRevision((int) id * 300);
		problem3.setSource("new source" + id);
		problem3.setTitle("new title" + id);		
			
		persistence.updateProblem(problem3, 11);
		
		Problem problem = (Problem) persistence.getProblem(problem3.getId());		
		checkProblem(problem3, problem);			
		
	}
	
	
	/**
	 * Tests deleteProblem method
	 * @throws Exception to JUnit
	 */
	public void testDeleteProblem() throws Exception {					
		persistence.deleteProblem(problem1.getId(), 11);
		assertNull("problem should be deleted", persistence.getProblem(problem1.getId()));		
		persistence.deleteProblem(problem2.getId(), 11);
		assertNull("problem should be deleted", persistence.getProblem(problem2.getId()));
		persistence.deleteProblem(problem3.getId(), 11);
		assertNull("problem should be deleted", persistence.getProblem(problem3.getId()));
		
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
		Problemset problem = new Problemset();
		problem.setId(id);
		problem.setDescription("desc" + id);
		problem.setLimit(limit);
		problem.setLanguages(languages);
		problem.setTitle("title" + id);
		problem.setForumId(forumId);
		return problem;
	}
	

	
	/**
	 * Creates a new problem.
	 * @param id the id
	 * @param forumId the forum id
	 * @param limit the limit
	 * @param languages  a list of languages
	 * @return a new Problem instance
	 */
	private Problem newProblem(long id, long contestId, Limit limit) {
		Problem problem = new Problem();
		problem.setId(id);
		problem.setContestId(contestId);
		problem.setCode("code" + id);
		problem.setAuthor("author" + id);
		problem.setChecker(id % 2 == 1);
		problem.setContest("contest" + id);
		problem.setLimit(limit);
		problem.setRevision((int) id * 10);
		problem.setSource("source" + id);
		problem.setTitle("title" + id);		
		return problem;
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
	 * Checks whether the two Problem instances are same.
	 * @param problem1 the expected problem
	 * @param problem2 the problem to check
	 */
	private void checkProblem(Problem problem1, Problem problem2) {
		
		assertEquals("wrong id", problem1.getId(), problem2.getId());
		assertEquals("wrong author", problem1.getAuthor(), problem2.getAuthor());
		assertEquals("wrong checker", problem1.isChecker(), problem2.isChecker());
		assertEquals("wrong code", problem1.getCode(), problem2.getCode());
		assertEquals("wrong contest", problem1.getContest(), problem2.getContest());
		assertEquals("wrong contest id", problem1.getContestId(), problem2.getContestId());
		assertEquals("wrong revision", problem1.getRevision(), problem2.getRevision());
		assertEquals("wrong source", problem1.getSource(), problem2.getSource());		
		assertEquals("wrong title", problem1.getTitle(), problem2.getTitle());
		
		checkLimit(problem1.getLimit(), problem2.getLimit());								
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


