/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import cn.edu.zju.acm.onlinejudge.bean.UserPreference;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Country;


/**
 * <p>Tests for UserPersistenceImpl.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class UserPersistenceImplTest extends TestCase {	
	
	/**
	 * A UserPersistenceImpl instance.
	 */
	private UserPersistenceImpl persistence = null;
	
	/**
	 * A UserProfile instance.
	 */
	private UserProfile profile = new UserProfile();
	
	/**
	 * A UserPreference instance.
	 */
	private UserPreference perference = new UserPreference();
		
			
	/**
	 * Setup.
	 * @throws Exception to JUnit
	 */
	protected void setUp() throws Exception {
		
		DatabaseHelper.resetAllTables(false);
		
		persistence = new UserPersistenceImpl();		
				
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
		
		
		perference.setPlan("my plan");
		perference.setPostPaging(1);
		perference.setProblemPaging(2);
		perference.setStatusPaging(3);
		perference.setSubmissionPaging(4);
		perference.setThreadPaging(5);
		perference.setUserPaging(6);
	}
	
	/**
	 * Tear down.
	 * @throws Exception to JUnit
	 */
	protected void tearDown() throws Exception {
		DatabaseHelper.clearTable("confirmation");
		DatabaseHelper.clearTable("user_preference");
		DatabaseHelper.clearTable("user_profile");		
	}
	
	/**
	 * Tests createUserProfile method
	 * @throws Exception to JUnit
	 */
	public void testCreateUserProfile() throws Exception {		
		persistence.createUserProfile(profile, 1);
		UserProfile profile1 = persistence.getUserProfile(profile.getId());
		checkUserProfile(profile, profile1);
	}
	
	/**
	 * Tests updateUserProfile method
	 * @throws Exception to JUnit
	 */
	public void testUpdateUserProfile() throws Exception {		
		persistence.createUserProfile(profile, 1);
		
		profile.setHandle("myNewHandle");
		profile.setPassword("myNewPassword");
		profile.setEmail("myNewEmail");
		profile.setRegDate(new Date());
		profile.setFirstName("myNewFirstName");
		profile.setLastName("myNewLastName");
		profile.setAddressLine1("myNewAddressLine1");
		profile.setAddressLine2("myNewAddressLine2");
		profile.setCity("myNewCity");
		profile.setState("myNewState");
		profile.setCountry(new Country(2, "foo"));
		profile.setZipCode("myNewZipCode");
		profile.setPhoneNumber("myNewPhoneNumber");
		profile.setBirthDate(DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse("1/1/1980"));
		profile.setGender('F');            
		profile.setSchool("myNewSchool");
		profile.setMajor("myNewMajor");
		profile.setGraduateStudent(false);
		profile.setGraduationYear(2006);
		profile.setStudentNumber("myNewStudentNumber");
		profile.setConfirmed(false);
		
		persistence.updateUserProfile(profile, 1);
		
		UserProfile profile1 = persistence.getUserProfile(profile.getId());
		checkUserProfile(profile, profile1);
	}

	/**
	 * Tests deleteUserProfile method
	 * @throws Exception to JUnit
	 */
	public void testDeleteUserProfile() throws Exception {		
		persistence.createUserProfile(profile, 1);
		persistence.deleteUserProfile(profile.getId(), 1);
		
		UserProfile profile1 = persistence.getUserProfile(profile.getId());
		
		assertFalse("should be removed", profile1.isActive());
		
	}
	
	/**
	 * Tests getUserProfile method
	 * @throws Exception to JUnit
	 */
	public void testGetUserProfile() throws Exception {		
		persistence.createUserProfile(profile, 1);
		UserProfile profile1 = persistence.getUserProfile(profile.getId());
		checkUserProfile(profile, profile1);		
	}
	
	/**
	 * Tests getUserProfileByHandle method
	 * @throws Exception to JUnit
	 */
	public void testGetUserProfileByHandle() throws Exception {		
		persistence.createUserProfile(profile, 1);
		UserProfile profile1 = persistence.getUserProfileByHandle(profile.getHandle());
		checkUserProfile(profile, profile1);		
	}
	
	/**
	 * Tests getUserProfileByEmail method
	 * @throws Exception to JUnit
	 */
	public void testGetUserProfileByEmail() throws Exception {		
		persistence.createUserProfile(profile, 1);
		UserProfile profile1 = persistence.getUserProfileByEmail(profile.getEmail());
		checkUserProfile(profile, profile1);		
	}
	
	/**
	 * Tests getUserProfileByCode method
	 * @throws Exception to JUnit
	 */
	public void testGetUserProfileByCode() throws Exception {		
		String code = "foobar";
		persistence.createUserProfile(profile, 1);
		persistence.createConfirmCode(profile.getId(), code, 1);
		UserProfile profile1 = persistence.getUserProfileByCode(code);
		checkUserProfile(profile, profile1);		
	}
	
	/**
	 * Tests getCreateCode method
	 * @throws Exception to JUnit
	 */
	public void testCreateCode() throws Exception {		
		String code = "foobar";
		persistence.createUserProfile(profile, 1);
		persistence.createConfirmCode(profile.getId(), code, 1);
		assertEquals("code is wrong", code, persistence.getConfirmCode(profile.getId()));
				
	}
	
	/**
	 * Tests getDeleteCode method
	 * @throws Exception to JUnit
	 */
	public void testDeleteCode() throws Exception {		
		String code = "foobar";
		persistence.createUserProfile(profile, 1);
		persistence.createConfirmCode(profile.getId(), code, 1);
		persistence.deleteConfirmCode(profile.getId(), 1);
		assertNull("code is wrong", persistence.getConfirmCode(profile.getId()));
				
	}
	
	/**
	 * Tests getGetCode method
	 * @throws Exception to JUnit
	 */
	public void testGetCode1() throws Exception {		
		String code = "foobar";
		persistence.createUserProfile(profile, 1);
		persistence.createConfirmCode(profile.getId(), code, 1);
		assertEquals("code is wrong", code, persistence.getConfirmCode(profile.getId()));
				
	}
	
	/**
	 * Tests getGetCode method
	 * @throws Exception to JUnit
	 */
	public void testGetCode2() throws Exception {		
		assertNull("code is wrong", persistence.getConfirmCode(-1));				
	}
	
	
	
	/**
	 * Tests getAllCountries method
	 * @throws Exception to JUnit
	 */
	public void testGetAllCountries() throws Exception {
		List countries = persistence.getAllCountries();
		assertTrue("wrong size", countries.size() > 0);
		for (Iterator it = countries.iterator(); it.hasNext();) {
			Country country = (Country) it.next();
			assertNotNull("wrong name", country.getName());						
		}				
	}
	
	/**
	 * Tests getCreateUserPreference method
	 * @throws Exception to JUnit
	 */
	public void testCreateUserPreference() throws Exception {	
		persistence.createUserProfile(profile, 1);	
		perference.setId(profile.getId());
		persistence.createUserPreference(perference, 1);
		
		UserPreference perference1 = persistence.getUserPreference(perference.getId());
		checkUserPreference(perference, perference1);		
				
	}
	
	/**
	 * Tests getUpdateUserPreference method
	 * @throws Exception to JUnit
	 */
	public void testUpdateUserPreference() throws Exception {	
		persistence.createUserProfile(profile, 1);
		perference.setId(profile.getId());
		persistence.createUserPreference(perference, 1);
		
		perference.setPlan("my new plan");
		perference.setPostPaging(11);
		perference.setProblemPaging(22);
		perference.setStatusPaging(33);
		perference.setSubmissionPaging(44);
		perference.setThreadPaging(55);
		perference.setUserPaging(66);
		
		persistence.updateUserPreference(perference, 1);
		
		UserPreference perference1 = persistence.getUserPreference(perference.getId());
		checkUserPreference(perference, perference1);		
				
	}
	
	/**
	 * Tests getGetUserPreference method
	 * @throws Exception to JUnit
	 */
	public void testGetUserPreference() throws Exception {	
		persistence.createUserProfile(profile, 1);	
		perference.setId(profile.getId());
		persistence.createUserPreference(perference, 1);
		
		UserPreference perference1 = persistence.getUserPreference(perference.getId());
		checkUserPreference(perference, perference1);		
				
	}
	
	/**
	 * Checks whether the two UserProfile instances are same.
	 * @param profile1 the expected profile
	 * @param profile2 the profile to check
	 */
	private void checkUserProfile(UserProfile profile1, UserProfile profile2) {
		assertEquals("wrongi id", profile1.getId(), profile2.getId());
		assertEquals("worong handle", profile1.getHandle(), profile2.getHandle());
		assertEquals("worong password", profile1.getPassword(), profile2.getPassword());
		assertEquals("worong email", profile1.getEmail(), profile2.getEmail());
		assertEquals("worong reg date", profile1.getRegDate().getTime() / 1000, profile2.getRegDate().getTime() / 1000);
		assertEquals("worong first name", profile1.getFirstName(), profile2.getFirstName());
		assertEquals("worong last name", profile1.getLastName(), profile2.getLastName());
		assertEquals("worong address line1", profile1.getAddressLine1(), profile2.getAddressLine1());
		assertEquals("worong address line2", profile1.getAddressLine2(), profile2.getAddressLine2());
		assertEquals("worong city", profile1.getCity(), profile2.getCity());
		assertEquals("worong state", profile1.getState(), profile2.getState());
		assertEquals("worong country id", profile1.getCountry().getId(), profile2.getCountry().getId());
		assertEquals("worong zip code", profile1.getZipCode(), profile2.getZipCode());
		assertEquals("worong phone number", profile1.getPhoneNumber(), profile2.getPhoneNumber());
		assertEquals("worong birth date", profile1.getBirthDate().getTime() / 1000, profile2.getBirthDate().getTime() / 1000);
		assertEquals("worong gender", profile1.getGender(), profile2.getGender());          
		assertEquals("worong school", profile1.getSchool(), profile2.getSchool());
		assertEquals("worong major", profile1.getMajor(), profile2.getMajor());
		assertEquals("worong graduate student", profile1.isGraduateStudent(), profile2.isGraduateStudent());
		assertEquals("worong graduation year", profile1.getGraduationYear(), profile2.getGraduationYear());
		assertEquals("worong student number", profile1.getStudentNumber(), profile2.getStudentNumber());          
		assertEquals("worong confirmed", profile1.isConfirmed(), profile2.isConfirmed());
	}
	
	/**
	 * Checks whether the two UserPreference instances are same.
	 * @param perference1 the expected profile
	 * @param perference2 the profile to check
	 */
	private void checkUserPreference(UserPreference perference1, UserPreference perference2) {
		
		assertEquals("wrong id", perference1.getId(), perference2.getId());
		assertEquals("wrong Plan", perference1.getPlan(), perference2.getPlan());
		assertEquals("wrong PostPaging", perference1.getPostPaging(), perference2.getPostPaging());
		assertEquals("wrong ProblemPaging", perference1.getProblemPaging(), perference2.getProblemPaging());
		assertEquals("wrong StatusPaging", perference1.getStatusPaging(), perference2.getStatusPaging());
		assertEquals("wrong SubmissionPaging", perference1.getSubmissionPaging(), perference2.getSubmissionPaging());
		assertEquals("wrong ThreadPaging", perference1.getThreadPaging(), perference2.getThreadPaging());
		assertEquals("wrong UserPaging", perference1.getUserPaging(), perference2.getUserPaging());
				
	}

}

