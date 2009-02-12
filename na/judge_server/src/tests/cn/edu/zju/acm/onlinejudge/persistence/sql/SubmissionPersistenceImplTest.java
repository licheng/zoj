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

import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.ExtendedSubmission;
import cn.edu.zju.acm.onlinejudge.bean.Forum;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Country;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;

/**
 * <p>Tests for ContestPersistenceImpl.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class SubmissionPersistenceImplTest extends TestCase {	
	
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
	private ProblemPersistenceImpl problemPersistence = new ProblemPersistenceImpl();
	
	/**
	 * A UserPersistenceImpl instance.
	 */
	private UserPersistenceImpl userPersistence = null; 
	
	
	/**
	 * A SubmissionPersistenceImpl instance.
	 */
	private SubmissionPersistenceImpl persistence = new SubmissionPersistenceImpl();
	
	/**
	 * A Forum instance.
	 */
	private Forum forum = null;
	
	/**
	 * A UserProfile instance.
	 */
	private UserProfile userProfile1 = null;
	
	/**
	 * A UserProfile instance.
	 */
	private UserProfile userProfile2 = null;
	
	/**
	 * A Limit instance.
	 */
	private Limit limit = null;
			
	/**
	 * A Contest instance.
	 */
	private Contest contest = null;
	
	/**
	 * A Problem instance.
	 */
	private Problem problem1 = null;
	
	/**
	 * A Problem instance.
	 */
	private Problem problem2 = null;
	
	
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
	 * A JudgeReply instance.
	 */
	private JudgeReply judgeReply1 = new JudgeReply(1, "judgeReply1", "JudgeReply 1", "style1", true);
	
	/**
	 * A Language instance.
	 */
	private JudgeReply judgeReply2 = new JudgeReply(2, "judgeReply2", "JudgeReply 2", "style2", false);
	
	/**
	 * A Language instance.
	 */
	private JudgeReply judgeReply3 = new JudgeReply(3, "judgeReply3", "JudgeReply 3", "style3", true);
	
	/**
	 * A Submission instance.
	 */
	private ExtendedSubmission submission1 = null;
	
	/**
	 * A Submission instance.
	 */
	private ExtendedSubmission submission2 = null;

	/**
	 * A Submission instance.
	 */
	private ExtendedSubmission submission3 = null;

	/**
	 * A Submission instance.
	 */
	private ExtendedSubmission submission4 = null;
	
	/**
	 * Setup.
	 * @throws Exception to JUnit
	 */
	protected void setUp() throws Exception {
		DatabaseHelper.resetAllTables(false);	
		
		userPersistence = new UserPersistenceImpl();
		
		contestPersistence.createLanguage(language1, 10);
		contestPersistence.createLanguage(language2, 10);
		contestPersistence.createLanguage(language3, 10);
		
		persistence.createJudgeReply(judgeReply1, 10);
		persistence.createJudgeReply(judgeReply2, 10);
		persistence.createJudgeReply(judgeReply3, 10);
		
		forum = newForum(1);
		forumPersistence.createForum(forum, 10);
		
		userProfile1 = newUserProfile(1);
		userProfile2 = newUserProfile(2);
		userPersistence.createUserProfile(userProfile1, 10);
		userPersistence.createUserProfile(userProfile2, 10);
		
		limit = newLimit(1);
				
		contest = newContest(
				1, forum.getId(), limit, Arrays.asList(new Object[] {language1, language2, language3}));				
		contestPersistence.createContest(contest, 10);
		
		problem1 = newProblem(1, contest.getId(), null);
		problem2 = newProblem(2, contest.getId(), limit);
		problemPersistence.createProblem(problem1, 10);
		problemPersistence.createProblem(problem2, 10);
		
		submission1 = newSubmission(1, problem1.getId(), userProfile1.getId(), language1, judgeReply1);
		submission2 = newSubmission(2, problem2.getId(), userProfile1.getId(), language2, judgeReply2);
		submission2.setJudgeDate(null);
		submission2.setSubmitDate(null);
		submission3 = newSubmission(3, problem2.getId(), userProfile2.getId(), language3, judgeReply3);
		submission4 = newSubmission(4, problem2.getId(), userProfile1.getId(), language1, judgeReply2);
		
		persistence.createSubmission(submission1, 10);
		persistence.createSubmission(submission2, 10);
		persistence.createSubmission(submission3, 10);
		persistence.createSubmission(submission4, 10);
	}
	
	/**
	 * Tear down.
	 * @throws Exception to JUnit
	 */
	protected void tearDown() throws Exception {				
		
		DatabaseHelper.clearTable("submission");
		DatabaseHelper.clearTable("problem");
		DatabaseHelper.clearTable("contest_language");	
		
		DatabaseHelper.clearTable("contest");		
		DatabaseHelper.clearTable("limits");
		DatabaseHelper.clearTable("language");
		DatabaseHelper.clearTable("forum");		
		DatabaseHelper.clearTable("judge_reply");
		DatabaseHelper.clearTable("user_profile");
		
							
	}
	
	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions1() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();
		
		criteria.setProblemId(new Long(problem2.getId()));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission2, submission3, submission4}), submissions);
				
		criteria.setProblemId(new Long(problem1.getId()));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1}), submissions);
		
		criteria.setProblemId(new Long(1234567890l));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
	}
	
	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions2() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();
		
		criteria.setContestId(new Long(contest.getId()));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission2, submission3, submission4}), submissions);
						
		criteria.setContestId(new Long(1234567890l));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
	}
	
	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions3() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();
		
		criteria.setHandle(userProfile1.getHandle());				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission2, submission4}), submissions);
		
		criteria.setHandle(userProfile2.getHandle());				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission3}), submissions);
						
		criteria.setHandle("foobar");				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
	}	
	
	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions4() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();
		
		criteria.setIdEnd(new Long(submission2.getId()));				
		criteria.setIdStart(null);
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission2}), submissions);
		
		criteria.setIdEnd(null);
		criteria.setIdStart(new Long(submission3.getId()));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission3, submission4}), submissions);
		
		criteria.setIdEnd(new Long(submission3.getId()));				
		criteria.setIdStart(new Long(submission2.getId()));
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission2, submission3}), submissions);
		
		criteria.setIdEnd(new Long(submission1.getId() - 1));				
		criteria.setIdStart(null);
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
		criteria.setIdEnd(null);
		criteria.setIdStart(new Long(submission4.getId() + 1));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
		criteria.setIdEnd(new Long(submission2.getId()));				
		criteria.setIdStart(new Long(submission3.getId()));
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);				
		
	}	
	
	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions5() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();
		
		criteria.setTimeEnd(submission3.getSubmitDate());				
		criteria.setTimeStart(null);
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission3}), submissions);
		
		criteria.setTimeStart(submission3.getSubmitDate());				
		criteria.setTimeEnd(null);
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission3, submission4}), submissions);
		
		criteria.setTimeStart(submission1.getSubmitDate());				
		criteria.setTimeEnd(submission3.getSubmitDate());
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission3}), submissions);
		
		criteria.setTimeEnd(new Date(-1000));				
		criteria.setTimeStart(null);
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
		criteria.setTimeStart(new Date(submission4.getSubmitDate().getTime() + 1000));				
		criteria.setTimeEnd(null);
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
		criteria.setTimeStart(submission3.getSubmitDate());				
		criteria.setTimeEnd(submission1.getSubmitDate());		
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);				
		
	}	

	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions6() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();
		
		criteria.setLanguages(Arrays.asList(new Object[] {language1, language3}));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission3, submission4}), submissions);
		
		criteria.setLanguages(Arrays.asList(new Object[] {language2}));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission2}), submissions);		
						
		criteria.setLanguages(Arrays.asList(new Object[] {
				new Language(4, "x", "x", "x", "x")}));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
		criteria.setLanguages(new ArrayList());				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
	}	
	
	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions7() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();		
		
		criteria.setJudgeReplies(Arrays.asList(new Object[] {judgeReply1, judgeReply3}));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission3}), submissions);
		
		criteria.setJudgeReplies(Arrays.asList(new Object[] {judgeReply2}));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission2, submission4}), submissions);		
						
		criteria.setJudgeReplies(Arrays.asList(new Object[] {
				new JudgeReply(4, "x", "x", "x", true)}));				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
		criteria.setJudgeReplies(new ArrayList());				
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(new ArrayList(), submissions);
		
	}	
	
	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions8() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();
		
		criteria.setProblemId(new Long(problem2.getId()));		
		criteria.setContestId(new Long(contest.getId()));
		criteria.setHandle(userProfile2.getHandle());
		criteria.setIdStart(new Long(submission1.getId()));
		criteria.setIdEnd(new Long(submission4.getId()));
		criteria.setTimeStart(submission1.getSubmitDate());
		criteria.setTimeEnd(submission4.getSubmitDate());
		criteria.setJudgeReplies(Arrays.asList(new Object[] {judgeReply1, judgeReply2, judgeReply3}));
		criteria.setLanguages(Arrays.asList(new Object[] {language1, language2, language3}));
		
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission3}), submissions);				
		
	}
	
	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions9() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();
					
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission2, submission3, submission4}), submissions);
		
		persistence.deleteSubmission(submission2.getId(), 11);
		submissions = persistence.searchSubmissions(criteria, 0, 100);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission3, submission4}), submissions);
		
	}
	
	/**
	 * Tests searchSubmissions method
	 * @throws Exception to JUnit
	 */
	public void testSearchSubmissions10() throws Exception {
		List submissions = null;
		SubmissionCriteria criteria = new SubmissionCriteria();
					
		submissions = persistence.searchSubmissions(criteria, 0, 0);
		checkSubmissions(new ArrayList(), submissions);
		
		submissions = persistence.searchSubmissions(criteria, 0, 1);
		checkSubmissions(Arrays.asList(new Object[] {submission1}), submissions);
		
		submissions = persistence.searchSubmissions(criteria, 0, 4);
		checkSubmissions(Arrays.asList(new Object[] {submission1, submission2, submission3, submission4}), submissions);
		
		submissions = persistence.searchSubmissions(criteria, 1, 2);
		checkSubmissions(Arrays.asList(new Object[] {submission2, submission3}), submissions);
		
		
		
	}
	
	
	/*

	private Language language3 = new Language(3, "language3", "Language 3", "compiler3", "options3");
	
	

	private JudgeReply judgeReply1 = new JudgeReply(1, "judgeReply1", "JudgeReply 1", "style1", true);
	submission1 = newSubmission(1, problem1.getId(), userProfile1.getId(), language1, judgeReply1);
	submission2 = newSubmission(2, problem2.getId(), userProfile1.getId(), language2, judgeReply2);
	submission2.setJudgeDate(null);
	submission2.setSubmitDate(null);
	submission3 = newSubmission(3, problem2.getId(), userProfile2.getId(), language3, judgeReply3);
	submission4 = newSubmission(4, problem2.getId(), userProfile1.getId(), language1, judgeReply2);
	 */
	/**
	 * Tests getSubmission method
	 * @throws Exception to JUnit
	 */
	public void testGetSubmission1() throws Exception {		
		Submission submission = persistence.getSubmission(submission1.getId());		
		checkSubmission(submission1, submission);				
	}
	
	/**
	 * Tests getSubmission method
	 * @throws Exception to JUnit
	 */
	public void testGetSubmission2() throws Exception {		
		Submission submission = persistence.getSubmission(submission2.getId());		
		checkSubmission(submission2, submission);				
	}
	
	/**
	 * Tests getSubmission method
	 * @throws Exception to JUnit
	 */
	public void testGetSubmission3() throws Exception {		
		Submission submission = persistence.getSubmission(submission3.getId());		
		checkSubmission(submission3, submission);				
	}
	
	/**
	 * Tests getSubmission method
	 * @throws Exception to JUnit
	 */
	public void testGetSubmission4() throws Exception {		
		Submission submission = (Submission) persistence.getSubmission(1234567890l);		
		assertNull("no such submission", submission);				
	}
	
	/**
	 * Tests updateSubmission method
	 * @throws Exception to JUnit
	 */
	public void testUpdateSubmission1() throws Exception {
		long id = submission1.getId();
		submission1.setUserProfileId(userProfile2.getId());
		submission1.setProblemId(problem2.getId());
		submission1.setLanguage(language2);
		submission1.setJudgeReply(judgeReply3);
		submission1.setContent("new contest" + id);
		submission1.setTimeConsumption((int) id * 100);
		submission1.setMemoryConsumption((int) id * 1000);
		submission1.setSubmitDate(new Date(id * 100000));
		submission1.setJudgeDate(new Date(id * 200000));
		submission1.setJudgeComment("new comment" + id);
				
		persistence.updateSubmission(submission1, 11);
		
		Submission submission = persistence.getSubmission(submission1.getId());		
		checkSubmission(submission1, submission);				
		
	}
	
	/**
	 * Tests updateSubmission method
	 * @throws Exception to JUnit
	 */
	public void testUpdateSubmission2() throws Exception {		
		long id = submission2.getId();
		submission2.setUserProfileId(userProfile2.getId());
		submission2.setProblemId(problem2.getId());
		submission2.setLanguage(language2);
		submission2.setJudgeReply(judgeReply3);
		submission2.setContent("new contest" + id);
		submission2.setTimeConsumption((int) id * 100);
		submission2.setMemoryConsumption((int) id * 1000);
		submission2.setSubmitDate(null);
		submission2.setJudgeDate(null);
		submission2.setJudgeComment("new comment" + id);
				
		persistence.updateSubmission(submission2, 11);
		
		Submission submission = persistence.getSubmission(submission2.getId());		
		checkSubmission(submission2, submission);	
	}

	/**
	 * Tests updateSubmission method
	 * @throws Exception to JUnit
	 */
	public void testUpdateSubmission3() throws Exception {		
		long id = submission3.getId();
		submission3.setUserProfileId(userProfile2.getId());
		submission3.setProblemId(problem2.getId());
		submission3.setLanguage(language2);
		submission3.setJudgeReply(judgeReply3);
		submission3.setContent("new contest" + id);
		submission3.setTimeConsumption((int) id * 100);
		submission3.setMemoryConsumption((int) id * 1000);
		submission3.setSubmitDate(new Date(id * 100000));
		submission3.setJudgeDate(new Date(id * 200000));
		submission3.setJudgeComment("new comment" + id);
				
		persistence.updateSubmission(submission3, 11);
		
		Submission submission = persistence.getSubmission(submission3.getId());		
		checkSubmission(submission3, submission);			
		
	}
	
	
	/**
	 * Tests deleteSubmission method
	 * @throws Exception to JUnit
	 */
	public void testDeleteSubmission() throws Exception {					
		persistence.deleteSubmission(submission1.getId(), 11);
		assertNull("submission should be deleted", persistence.getSubmission(submission1.getId()));		
		persistence.deleteSubmission(submission2.getId(), 11);
		assertNull("submission should be deleted", persistence.getSubmission(submission2.getId()));
		persistence.deleteSubmission(submission3.getId(), 11);
		assertNull("submission should be deleted", persistence.getSubmission(submission3.getId()));
		
	}
	
	/**
	 * Tests getJudgeReply method
	 * @throws Exception to JUnit
	 */
	public void testGetJudgeReply1() throws Exception {		
		JudgeReply judgeReply = persistence.getJudgeReply(judgeReply1.getId());		
		checkJudgeReply(judgeReply1, judgeReply);				
	}
	
	/**
	 * Tests getJudgeReply method
	 * @throws Exception to JUnit
	 */
	public void testGetJudgeReply2() throws Exception {		
		JudgeReply judgeReply = persistence.getJudgeReply(judgeReply1.getId());		
		checkJudgeReply(judgeReply1, judgeReply);				
	}
	
	/**
	 * Tests getJudgeReply method
	 * @throws Exception to JUnit
	 */
	public void testGetJudgeReply3() throws Exception {		
		JudgeReply judgeReply = persistence.getJudgeReply(judgeReply1.getId());		
		checkJudgeReply(judgeReply1, judgeReply);				
	}
	
	/**
	 * Tests getJudgeReply method
	 * @throws Exception to JUnit
	 */
	public void testGetJudgeReply4() throws Exception {		
		JudgeReply judgeReply = persistence.getJudgeReply(1234567890l);			
		assertNull("no such contest", judgeReply);				
	}
	
	
	
	/**
	 * Tests getAllJudgeReplies method
	 * @throws Exception to JUnit
	 */
	public void testGetAllJudgeReplies() throws Exception {		
		
		List judgeReplies = persistence.getAllJudgeReplies();
		for (int i = 0; i < 3; ++i) {
			JudgeReply judgeReply = (JudgeReply) judgeReplies.get(i);
			long id = judgeReply.getId();			
			assertEquals("wrong name", "judgeReply" + id, judgeReply.getName());
			assertEquals("wrong desc", "JudgeReply " + id, judgeReply.getDescription());
			assertEquals("wrong options", "style" + id, judgeReply.getStyle());
			assertEquals("wrong compiler", i % 2 == 0, judgeReply.isCommitted());						
		}
	}
	
	/**
	 * Tests createJudgeReply method
	 * @throws Exception to JUnit
	 */
	public void testCreateJudgeReply() throws Exception {		
		
		persistence.createJudgeReply(new JudgeReply(4, "judgeReply4", "JudgeReply 4", "style4", false), 10);
		
		List judgeReplies = persistence.getAllJudgeReplies();
		for (int i = 0; i < 4; ++i) {
			JudgeReply judgeReply = (JudgeReply) judgeReplies.get(i);
			long id = judgeReply.getId();			
			assertEquals("wrong name", "judgeReply" + id, judgeReply.getName());
			assertEquals("wrong desc", "JudgeReply " + id, judgeReply.getDescription());
			assertEquals("wrong options", "style" + id, judgeReply.getStyle());
			assertEquals("wrong compiler", i % 2 == 0, judgeReply.isCommitted());						
		}
	}
	
	/**
	 * Tests updateJudgeReply method
	 * @throws Exception to JUnit
	 */
	public void testUpdateJudgeReply() throws Exception {		
		
		List judgeReplies = persistence.getAllJudgeReplies();
		for (int i = 0; i < 3; ++i) {
			JudgeReply judgeReply = (JudgeReply) judgeReplies.get(i);			
			long id = judgeReply.getId();		
			persistence.updateJudgeReply(
					new JudgeReply(id, "new judgeReply" + id, "new JudgeReply " + id, "new style" + id, i % 2 == 1), 10);
		}
		
		judgeReplies = persistence.getAllJudgeReplies();
		for (int i = 0; i < 3; ++i) {
			JudgeReply judgeReply = (JudgeReply) judgeReplies.get(i);
			long id = judgeReply.getId();			
			assertEquals("wrong name", "new judgeReply" + id, judgeReply.getName());
			assertEquals("wrong desc", "new JudgeReply " + id, judgeReply.getDescription());
			assertEquals("wrong options", "new style" + id, judgeReply.getStyle());
			assertEquals("wrong compiler", i %2 == 1, judgeReply.isCommitted());						
		}		
	}
	
	/**
	 * Tests deleteJudgeReply method
	 * @throws Exception to JUnit
	 */
	public void testDeleteJudgeReply() throws Exception {		
		
		List judgeReplies = persistence.getAllJudgeReplies();
		for (int i = 0; i < 3; ++i) {
			JudgeReply judgeReply = (JudgeReply) judgeReplies.get(i);			
			long id = judgeReply.getId();		
			persistence.deleteJudgeReply(id, 10);
		}
			
		judgeReplies = persistence.getAllJudgeReplies();
		assertEquals("wrong size", 0, judgeReplies.size());
		
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
	 * Creates a new user profile.
	 * @param id the id
	 * @return a new user profile instance
	 */
	private UserProfile newUserProfile(long id) {
		UserProfile profile = new UserProfile();
		profile.setId(id);
		profile.setHandle("myHandle" + id);
		profile.setPassword("myPassword");
		profile.setEmail("myEmail" + id);
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
		profile.setBirthDate(new Date(0));
		profile.setGender('M');            
		profile.setSchool("mySchool");
		profile.setMajor("myMajor");
		profile.setGraduateStudent(true);
		profile.setGraduationYear(2005);
		profile.setStudentNumber("myStudentNumber");
		profile.setConfirmed(false);	
		return profile;
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
	 * Creates a new submission.
	 * @param id the id
	 * @param forumId the forum id
	 * @param limit the limit
	 * @param languages  a list of languages
	 * @return a new Problem instance
	 */
	private ExtendedSubmission newSubmission(
			long id, long problemId, long userId, Language language, JudgeReply reply) {
		ExtendedSubmission submission = new ExtendedSubmission();
		submission.setId(id);
		submission.setUserProfileId(userId);
		submission.setProblemId(problemId);
		submission.setLanguage(language);
		submission.setJudgeReply(reply);
		submission.setContent("contest" + id);
		submission.setTimeConsumption((int) id * 10);
		submission.setMemoryConsumption((int) id * 100);
		submission.setSubmitDate(new Date(id * 10000));
		submission.setJudgeDate(new Date(id * 20000));
		submission.setJudgeComment("comment" + id);
	    
		return submission;
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
	 * Checks whether the two JudgeReply instances are same.
	 * @param judgeReply1 the expected language
	 * @param judgeReply2 the language to check
	 */
	private void checkJudgeReply(JudgeReply judgeReply1, JudgeReply judgeReply2) {
		
		assertEquals("wrong id", judgeReply1.getId(), judgeReply2.getId());
		assertEquals("wrong name", judgeReply1.getName(), judgeReply2.getName());
		assertEquals("wrong desc", judgeReply1.getDescription(), judgeReply2.getDescription());
		assertEquals("wrong style", judgeReply1.getStyle(), judgeReply2.getStyle());
		assertEquals("wrong committed", judgeReply1.isCommitted(), judgeReply2.isCommitted());	
	}
	
	/**
	 * Checks whether the two Submission instances are same.
	 * @param submission1 the expected submission
	 * @param submission2 the submission to check
	 */
	private void checkSubmission(Submission submission1, Submission submission2) {
		
		assertEquals("wrong id", submission1.getId(), submission2.getId());
		assertEquals("wrong content", submission1.getContent(), submission2.getContent());
		assertEquals("wrong comment", submission1.getJudgeComment(), submission2.getJudgeComment());
		assertEquals("wrong judge date", submission1.getJudgeDate(), submission2.getJudgeDate());
		assertEquals("wrong memory consumption", submission1.getMemoryConsumption(), submission2.getMemoryConsumption());
		assertEquals("wrong problem i", submission1.getProblemId(), submission2.getProblemId());
		assertEquals("wrong submit date", submission1.getSubmitDate(), submission2.getSubmitDate());	
		assertEquals("wrong time consumption", submission1.getTimeConsumption(), submission2.getTimeConsumption());
		assertEquals("wrong user", submission1.getUserProfileId(), submission2.getUserProfileId());
		checkJudgeReply(submission1.getJudgeReply(), submission2.getJudgeReply());
		checkLanguage(submission1.getLanguage(), submission2.getLanguage());
	}
	
	/**
	 * Checks whether the two Submission  lists are same.
	 * @param submissions1 the expected submission
	 * @param submissions2 the submission to check
	 */	
	private void checkSubmissions(List submissions1, List submissions2) {
		assertEquals("wrong size", submissions1.size(), submissions2.size());
		Iterator it1 = submissions1.iterator();
		Iterator it2 = submissions2.iterator();
		while (it1.hasNext()) {
			checkSubmission((ExtendedSubmission) it1.next(), (ExtendedSubmission) it2.next());			
		}				
	}	
}



