/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.UserPreference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Country;
import cn.edu.zju.acm.onlinejudge.bean.request.UserCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>UserPersistenceImpl implements UserPersistence interface</p>
 * <p>UserPersistence interface defines the API used to manager the user profile related affairs
 * in persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class UserPersistenceImpl implements UserPersistence {

	
	/**
	 * The query to get last id.
	 */
	private static final String GET_LAST_ID = "SELECT LAST_INSERT_ID()";
	
	/**
	 * The query to select all countries.
	 */
	private static final String GET_ALL_COUNTRIES = 
		MessageFormat.format("SELECT {0}, {1} FROM {2}", 
							 new Object[] {DatabaseConstants.COUNTRY_COUNTRY_ID,
										   DatabaseConstants.COUNTRY_NAME,
										   DatabaseConstants.COUNTRY_TABLE});
												
	
	/**
	 * The statement to create a user.
	 */
	private static final String INSERT_USER = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}, " +
								"{13}, {14}, {15}, {16}, {17}, {18}, {19}, {20}, {21}, {22}, {23}, {24}, {25}, {26}, {27}, {28}) " +
							 	"VALUES(?, MD5(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 1, ?)", 
							 new Object[] {DatabaseConstants.USER_PROFILE_TABLE, 
				  						   DatabaseConstants.USER_PROFILE_HANDLE,
				  						   DatabaseConstants.USER_PROFILE_PASSWORD,
				  						   DatabaseConstants.USER_PROFILE_EMAIL_ADDRESS,
				  						   DatabaseConstants.USER_PROFILE_REG_DATE,
				  						   DatabaseConstants.USER_PROFILE_FIRST_NAME,
				  						   DatabaseConstants.USER_PROFILE_LAST_NAME,
				  						   DatabaseConstants.USER_PROFILE_ADDRESS_LINE1,
				  						   DatabaseConstants.USER_PROFILE_ADDRESS_LINE2,
				  						   DatabaseConstants.USER_PROFILE_CITY,
				  						   DatabaseConstants.USER_PROFILE_STATE,
				  						   DatabaseConstants.USER_PROFILE_COUNTRY_ID,
				  						   DatabaseConstants.USER_PROFILE_ZIP_CODE,
				  						   DatabaseConstants.USER_PROFILE_PHONE_NUMBER,
				  						   DatabaseConstants.USER_PROFILE_BIRTH_DATE,
				  						   DatabaseConstants.USER_PROFILE_GENDER,
				  						   DatabaseConstants.USER_PROFILE_SCHOOL,
				  						   DatabaseConstants.USER_PROFILE_MAJOR,
				  						   DatabaseConstants.USER_PROFILE_GRADUATE_STUDENT,
				  						   DatabaseConstants.USER_PROFILE_GRADUATION_YEAR,
				  						   DatabaseConstants.USER_PROFILE_STUDENT_NUMBER,
				  						   DatabaseConstants.USER_PROFILE_CONFIRMED,						  						   
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.USER_PROFILE_SUPER_ADMIN,
				  						   DatabaseConstants.USER_PROFILE_ACTIVE,
                                           "nickname"});
	
	
	
	/**
	 * The statement to update a user.
	 */
	private static final String UPDATE_USER = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=MD5(?), {3}=?, {4}=?, {5}=?, {6}=?, {7}=?, {8}=?, {9}=?, " +
								"{10}=?, {11}=?, {12}=?, {13}=?, {14}=?, {15}=?, {16}=?, {17}=?, {18}=?, " +
								"{19}=?, {20}=?, {21}=?, {22}=?, {23}=? WHERE {24}=?",
							 new Object[] {DatabaseConstants.USER_PROFILE_TABLE, 
				  						   DatabaseConstants.USER_PROFILE_HANDLE,
				  						   DatabaseConstants.USER_PROFILE_PASSWORD,
				  						   DatabaseConstants.USER_PROFILE_EMAIL_ADDRESS,
				  						   DatabaseConstants.USER_PROFILE_FIRST_NAME,
				  						   DatabaseConstants.USER_PROFILE_LAST_NAME,
				  						   DatabaseConstants.USER_PROFILE_ADDRESS_LINE1,
				  						   DatabaseConstants.USER_PROFILE_ADDRESS_LINE2,
				  						   DatabaseConstants.USER_PROFILE_CITY,
				  						   DatabaseConstants.USER_PROFILE_STATE,
				  						   DatabaseConstants.USER_PROFILE_COUNTRY_ID,
				  						   DatabaseConstants.USER_PROFILE_ZIP_CODE,
				  						   DatabaseConstants.USER_PROFILE_PHONE_NUMBER,
				  						   DatabaseConstants.USER_PROFILE_BIRTH_DATE,
				  						   DatabaseConstants.USER_PROFILE_GENDER,
				  						   DatabaseConstants.USER_PROFILE_SCHOOL,
				  						   DatabaseConstants.USER_PROFILE_MAJOR,
				  						   DatabaseConstants.USER_PROFILE_GRADUATE_STUDENT,
				  						   DatabaseConstants.USER_PROFILE_GRADUATION_YEAR,
				  						   DatabaseConstants.USER_PROFILE_STUDENT_NUMBER,
				  						   DatabaseConstants.USER_PROFILE_CONFIRMED,				  						  				  						   
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   "nickname",            
				  						   DatabaseConstants.USER_PROFILE_USER_PROFILE_ID});

	/**
	 * The statement to update a user without password.
	 */
	private static final String UPDATE_USER_1 = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=MD5(?), {3}=?, {4}=?, {5}=?, {6}=?, {7}=?, {8}=?, " +
								"{9}=?, {10}=?, {11}=?, {12}=?, {13}=?, {14}=?, {15}=?, {16}=?, {17}=?, " +
								"{18}=?, {19}=?, {20}=?, {21}=?, {22}=? WHERE {23}=?",
							 new Object[] {DatabaseConstants.USER_PROFILE_TABLE, 
				  						   DatabaseConstants.USER_PROFILE_HANDLE,
				  						   DatabaseConstants.USER_PROFILE_EMAIL_ADDRESS,
				  						   DatabaseConstants.USER_PROFILE_FIRST_NAME,
				  						   DatabaseConstants.USER_PROFILE_LAST_NAME,
				  						   DatabaseConstants.USER_PROFILE_ADDRESS_LINE1,
				  						   DatabaseConstants.USER_PROFILE_ADDRESS_LINE2,
				  						   DatabaseConstants.USER_PROFILE_CITY,
				  						   DatabaseConstants.USER_PROFILE_STATE,
				  						   DatabaseConstants.USER_PROFILE_COUNTRY_ID,
				  						   DatabaseConstants.USER_PROFILE_ZIP_CODE,
				  						   DatabaseConstants.USER_PROFILE_PHONE_NUMBER,
				  						   DatabaseConstants.USER_PROFILE_BIRTH_DATE,
				  						   DatabaseConstants.USER_PROFILE_GENDER,
				  						   DatabaseConstants.USER_PROFILE_SCHOOL,
				  						   DatabaseConstants.USER_PROFILE_MAJOR,
				  						   DatabaseConstants.USER_PROFILE_GRADUATE_STUDENT,
				  						   DatabaseConstants.USER_PROFILE_GRADUATION_YEAR,
				  						   DatabaseConstants.USER_PROFILE_STUDENT_NUMBER,
				  						   DatabaseConstants.USER_PROFILE_CONFIRMED,				  						  				  						   
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   "nickname",            
				  						   DatabaseConstants.USER_PROFILE_USER_PROFILE_ID});

	/**
	 * The statement to delete a thread.
	 */
	private static final String DELETE_USER = 
		MessageFormat.format("UPDATE {0} SET {1}=0, {2}=?, {3}=? WHERE {4}=?", 
							 new Object[] {DatabaseConstants.USER_PROFILE_TABLE, 
										   DatabaseConstants.USER_PROFILE_ACTIVE,
										   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE,
				  						   DatabaseConstants.USER_PROFILE_USER_PROFILE_ID}); 
	
	
								
	/**
	 * The query to get a user profile.
	 */
	private static final String GET_USER = 
		MessageFormat.format("SELECT  {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, " +
								"{10}, {11}, {12}, {13}, {14}, {15}, {16}, {17}, {18}, " +
								"{19}, {20}, {21}, {22}, {23}, {24}, {25} FROM {26}",								 
							 new Object[] {DatabaseConstants.USER_PROFILE_USER_PROFILE_ID, 
							     		   DatabaseConstants.USER_PROFILE_HANDLE,
							     		   DatabaseConstants.USER_PROFILE_PASSWORD,
							     		   DatabaseConstants.USER_PROFILE_EMAIL_ADDRESS,
							     		   DatabaseConstants.USER_PROFILE_REG_DATE,
							     		   DatabaseConstants.USER_PROFILE_FIRST_NAME,
							     		   DatabaseConstants.USER_PROFILE_LAST_NAME,
							     		   DatabaseConstants.USER_PROFILE_ADDRESS_LINE1,
							     		   DatabaseConstants.USER_PROFILE_ADDRESS_LINE2,
							     		   DatabaseConstants.USER_PROFILE_CITY,
							     		   DatabaseConstants.USER_PROFILE_STATE,
							     		   DatabaseConstants.USER_PROFILE_COUNTRY_ID,
							     		   DatabaseConstants.USER_PROFILE_ZIP_CODE,
							     		   DatabaseConstants.USER_PROFILE_PHONE_NUMBER,
							     		   DatabaseConstants.USER_PROFILE_BIRTH_DATE,
							     		   DatabaseConstants.USER_PROFILE_GENDER,
							     		   DatabaseConstants.USER_PROFILE_SCHOOL,
							     		   DatabaseConstants.USER_PROFILE_MAJOR,
							     		   DatabaseConstants.USER_PROFILE_GRADUATE_STUDENT,
							     		   DatabaseConstants.USER_PROFILE_GRADUATION_YEAR,
							     		   DatabaseConstants.USER_PROFILE_STUDENT_NUMBER,
							     		   DatabaseConstants.USER_PROFILE_CONFIRMED,				  						  
							     		   DatabaseConstants.USER_PROFILE_ACTIVE,
                                           "nickname",
                                           "last_login_date",
                                           "last_login_ip",
							     		   DatabaseConstants.USER_PROFILE_TABLE});		
	
	
	/**
	 * The query to get a user profile by id.
	 */
	private static final String GET_USER_BY_ID = 
		GET_USER + " WHERE " + DatabaseConstants.USER_PROFILE_USER_PROFILE_ID + "=?";
	
	/**
	 * The query to get a user profile by handle.
	 */
	private static final String GET_USER_BY_HANDLE = 
		GET_USER + " WHERE " + DatabaseConstants.USER_PROFILE_HANDLE + "=?";
	
    /**
     * The query to login a user.
     */
    private static final String LOGIN = 
        GET_USER + " WHERE handle=? AND password=MD5(?)" ;

    
    /**
     * The query to login a user.
     */
    private static final String LOGIN_ZOJ1 = 
        GET_USER + " WHERE handle=? AND password=ENCRYPT(?, ?)" ;
 
	/**
	 * The query to get a user profile by email.
	 */
	private static final String GET_USER_BY_EMAIL = 
		GET_USER + " WHERE " + DatabaseConstants.USER_PROFILE_EMAIL_ADDRESS + "=?";
	
	/**
	 * The query to get a user profile by code.
	 */
	private static final String GET_USER_BY_CODE = 
		MessageFormat.format(GET_USER + " WHERE {0} IN (SELECT {1} FROM {2} WHERE {3}=?)",
							 new Object[] {DatabaseConstants.USER_PROFILE_USER_PROFILE_ID, 
	     		   			  			   DatabaseConstants.CONFIRMATION_USER_PROFILE_ID,
	     		   			  			   DatabaseConstants.CONFIRMATION_TABLE,
	     		   			  			   DatabaseConstants.CONFIRMATION_CODE});
	
	
    /**
	 * The statement to create a user preference.
	 */
	private static final String INSERT_USER_PREFERENCE = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}, {12}) " +
							 	"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
							 new Object[] {DatabaseConstants.USER_PREFERENCE_TABLE, 
				  						   DatabaseConstants.USER_PREFERENCE_USER_PROFILE_ID,
				  						   DatabaseConstants.USER_PREFERENCE_PLAN,
				  						   DatabaseConstants.USER_PREFERENCE_PROBLEM_PAGING,
				  						   DatabaseConstants.USER_PREFERENCE_SUBMISSION_PAGING,
				  						   DatabaseConstants.USER_PREFERENCE_STATUS_PAGING,
				  						   DatabaseConstants.USER_PREFERENCE_USER_PAGING,
				  						   DatabaseConstants.USER_PREFERENCE_THREAD_PAGING,
				  						   DatabaseConstants.USER_PREFERENCE_POST_PAGING,				  						
				  						   DatabaseConstants.CREATE_USER,
				  						   DatabaseConstants.CREATE_DATE,
				  						   DatabaseConstants.LAST_UPDATE_USER,
				  						   DatabaseConstants.LAST_UPDATE_DATE}); 
	/**
	 * The statement to update a user preference.
	 */
	private static final String UPDATE_USER_PREFERENCE = 
		MessageFormat.format("UPDATE {0} SET {1}=?, {2}=?, {3}=?, {4}=?, {5}=?, {6}=?, {7}=?, {8}=?, {9}=? " +
								"WHERE {10}=?",
							  new Object[] {DatabaseConstants.USER_PREFERENCE_TABLE, 				  						   
				  				  		   	DatabaseConstants.USER_PREFERENCE_PLAN,
				  				  		   	DatabaseConstants.USER_PREFERENCE_PROBLEM_PAGING,
				  				  		   	DatabaseConstants.USER_PREFERENCE_SUBMISSION_PAGING,
				  				  		   	DatabaseConstants.USER_PREFERENCE_STATUS_PAGING,
				  				  		   	DatabaseConstants.USER_PREFERENCE_USER_PAGING,
				  				  		   	DatabaseConstants.USER_PREFERENCE_THREAD_PAGING,
				  				  		   	DatabaseConstants.USER_PREFERENCE_POST_PAGING,				  						
				  				  		   	DatabaseConstants.LAST_UPDATE_USER,
				  				  		   	DatabaseConstants.LAST_UPDATE_DATE,
				  				  		   	DatabaseConstants.USER_PREFERENCE_USER_PROFILE_ID}); 

	/**
	 * The query to get a user preference.
	 */
	private static final String GET_USER_PREFERENCE = 
		MessageFormat.format("SELECT {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7} FROM {8} WHERE {0}=?",
				 			 new Object[] {DatabaseConstants.USER_PREFERENCE_USER_PROFILE_ID, 				  						   
							  		   	   DatabaseConstants.USER_PREFERENCE_PLAN,
							  		   	   DatabaseConstants.USER_PREFERENCE_PROBLEM_PAGING,
								  		   DatabaseConstants.USER_PREFERENCE_SUBMISSION_PAGING,
							  		   	   DatabaseConstants.USER_PREFERENCE_STATUS_PAGING,
							  		   	   DatabaseConstants.USER_PREFERENCE_USER_PAGING,
							  		   	   DatabaseConstants.USER_PREFERENCE_THREAD_PAGING,
							  		   	   DatabaseConstants.USER_PREFERENCE_POST_PAGING,				  						
							  		   	   DatabaseConstants.USER_PREFERENCE_TABLE}); 

	
	
	/**
	 * The statement to create a confirmation code.
	 */
	private static final String INSERT_CODE = 
		MessageFormat.format("INSERT INTO {0} ({1}, {2}) VALUES(?, ?)", 
							 new Object[] {DatabaseConstants.CONFIRMATION_TABLE, 
				  						   DatabaseConstants.CONFIRMATION_USER_PROFILE_ID,
				  						   DatabaseConstants.CONFIRMATION_CODE});
	
	/**
	 * The statement to update a confirmation code.
	 */
	private static final String UPDATE_CODE = 
		MessageFormat.format("UPDATE {0} SET {1}=? WHERE {2}=?", 
							 new Object[] {DatabaseConstants.CONFIRMATION_TABLE, 
				  						   DatabaseConstants.CONFIRMATION_CODE,
				  						   DatabaseConstants.CONFIRMATION_USER_PROFILE_ID});
	
	/**
	 * The statement to delete a confirmation code.
	 */
	private static final String DELETE_CODE = 
		MessageFormat.format("DELETE FROM {0} WHERE {1}=?", 
							 new Object[] {DatabaseConstants.CONFIRMATION_TABLE, 
				  						   DatabaseConstants.CONFIRMATION_USER_PROFILE_ID});
	
	/**
	 * The query to get a confirmation code.
	 */
	private static final String GET_CODE = 
		MessageFormat.format("SELECT {0} FROM {1} WHERE {2}=?", 
							 new Object[] {DatabaseConstants.CONFIRMATION_CODE,
										   DatabaseConstants.CONFIRMATION_TABLE, 
				  						   DatabaseConstants.CONFIRMATION_USER_PROFILE_ID});
	
	
	/**
	 * The list containing all countries.
	 */
	private List allCountries = null;
	
	
	/**
	 * UserPersistenceImpl. 
	 */
	public UserPersistenceImpl() {
		// empty
	}
	
    /**
     * <p>Creates the specified user profile in persistence layer.</p>
     *
     * @param profile the UserProfile instance to create
     * @param user the id of the user who made this modification
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createUserProfile(UserProfile profile, long user) throws PersistenceException {
    	if (profile == null) {
    		throw new NullPointerException("profile is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
            ps = conn.prepareStatement(INSERT_USER);  
            ps.setString(1, profile.getHandle());
            ps.setString(2, profile.getPassword());
            ps.setString(3, profile.getEmail());
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            ps.setString(5, profile.getFirstName());
            ps.setString(6, profile.getLastName());
            ps.setString(7, profile.getAddressLine1());
            ps.setString(8, profile.getAddressLine2());
            ps.setString(9, profile.getCity());
            ps.setString(10, profile.getState());
            ps.setLong(11, profile.getCountry().getId());
            ps.setString(12, profile.getZipCode());
            ps.setString(13, profile.getPhoneNumber());
            ps.setTimestamp(14, new Timestamp(profile.getBirthDate().getTime()));
            ps.setString(15, "" + profile.getGender());            
            ps.setString(16, profile.getSchool());
            ps.setString(17, profile.getMajor());
            ps.setBoolean(18, profile.isGraduateStudent());
            ps.setInt(19, profile.getGraduationYear());
            ps.setString(20, profile.getStudentNumber());            
            ps.setBoolean(21, profile.isConfirmed());
            ps.setLong(22, user);
            ps.setTimestamp(23, new Timestamp(new Date().getTime()));            
            ps.setLong(24, user);
            ps.setTimestamp(25, new Timestamp(new Date().getTime()));
            ps.setString(26, profile.getNickName());
            ps.executeUpdate();
            
            ps = conn.prepareStatement(GET_LAST_ID);
            rs = ps.executeQuery();
            rs.next();            
            profile.setId(rs.getLong(1));
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create user.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Updates the specified user profile in persistence layer.</p>
     *
     * @param profile the UserProfile instance to update
     * @param user the id of the user who made this modification
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateUserProfile(UserProfile profile, long user) throws PersistenceException {
    	if (profile == null) {
    		throw new NullPointerException("profile is null.");
    	}
    	if(profile.getPassword() == null || profile.getPassword().trim().length()==0)
		{
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();     

        	ps = conn.prepareStatement(UPDATE_USER_1);  
            ps.setString(1, profile.getHandle());
            ps.setString(2, profile.getEmail());
            ps.setString(3, profile.getFirstName());
            ps.setString(4, profile.getLastName());
            ps.setString(5, profile.getAddressLine1());
            ps.setString(6, profile.getAddressLine2());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setLong(9, profile.getCountry().getId());
            ps.setString(10, profile.getZipCode());
            ps.setString(11, profile.getPhoneNumber());
            ps.setTimestamp(12, new Timestamp(profile.getBirthDate().getTime()));
            ps.setString(13, "" + profile.getGender());            
            ps.setString(14, profile.getSchool());
            ps.setString(15, profile.getMajor());
            ps.setBoolean(16, profile.isGraduateStudent());
            ps.setInt(17, profile.getGraduationYear());
            ps.setString(18, profile.getStudentNumber());            
            ps.setBoolean(19, profile.isConfirmed());
            ps.setLong(20, user);            
            ps.setTimestamp(21, new Timestamp(new Date().getTime()));
            ps.setString(22, profile.getNickName());
            ps.setLong(23, profile.getId());
            
            ps.executeUpdate();                        
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to update user.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
		}
		else
		{
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();     

        	ps = conn.prepareStatement(UPDATE_USER);  
            ps.setString(1, profile.getHandle());
            ps.setString(2, profile.getPassword());
            ps.setString(3, profile.getEmail());
            ps.setString(4, profile.getFirstName());
            ps.setString(5, profile.getLastName());
            ps.setString(6, profile.getAddressLine1());
            ps.setString(7, profile.getAddressLine2());
            ps.setString(8, profile.getCity());
            ps.setString(9, profile.getState());
            ps.setLong(10, profile.getCountry().getId());
            ps.setString(11, profile.getZipCode());
            ps.setString(12, profile.getPhoneNumber());
            ps.setTimestamp(13, new Timestamp(profile.getBirthDate().getTime()));
            ps.setString(14, "" + profile.getGender());            
            ps.setString(15, profile.getSchool());
            ps.setString(16, profile.getMajor());
            ps.setBoolean(17, profile.isGraduateStudent());
            ps.setInt(18, profile.getGraduationYear());
            ps.setString(19, profile.getStudentNumber());            
            ps.setBoolean(20, profile.isConfirmed());
            ps.setLong(21, user);            
            ps.setTimestamp(22, new Timestamp(new Date().getTime()));
            ps.setString(23, profile.getNickName());
            ps.setLong(24, profile.getId());
            
            ps.executeUpdate();                        
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to update user.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }
		}
    }

    /**
     * <p>Deletes the specified user profile in persistence layer.</p>
     *
     * @param id the id of the user profile to delete
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteUserProfile(long id, long user) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;        
        try {
        	conn = Database.createConnection();
	
            ps = conn.prepareStatement(DELETE_USER);  
            ps.setLong(1, user);
            ps.setTimestamp(2, new Timestamp(new Date().getTime()));
            ps.setLong(3, id);
            
            if (ps.executeUpdate() == 0) {
            	throw new PersistenceException("No such user profile.");
            }
            
        } catch (PersistenceException pe) {
        	throw pe;
		} catch (SQLException e) {
        	throw new PersistenceException("Failed to delete user profile.", e);
		} finally {
        	Database.dispose(conn, ps, null);
        }   
    }

    /**
     * <p>Gets the user profile with given id in persistence layer.</p>
     *
     * @param id the id of the user profile
     * @return the user profile with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public UserProfile getUserProfile(long id) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_USER_BY_ID);   
            ps.setLong(1, id);
            rs = ps.executeQuery();
                        
            if (rs.next()) {
            	return populateUserProfile(rs);
            } else {
            	return null;
            } 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the user profile with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }  
    }


    /**
     * <p>Gets the user profile with given handle in persistence layer.</p>
     *
     * @param handle the handle of the user profile
     * @return the user profile with given handle in persistence layer
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public UserProfile getUserProfileByHandle(String handle) throws PersistenceException {
    	if (handle == null) {
    		throw new NullPointerException("handle is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_USER_BY_HANDLE);               
            ps.setString(1, handle);
            rs = ps.executeQuery();
                        
            if (rs.next()) {
            	
            	return populateUserProfile(rs);
            } else {
            	return null;
            } 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the user profile with handle " + handle, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }  
    }

    /**
     * <p>Gets the user profile with given handle in persistence layer.</p>
     *
     * @param handle the handle of the user profile
     * @return the user profile with given handle in persistence layer
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public UserProfile login(String handle, String password) throws PersistenceException {
        if (handle == null) {
            throw new NullPointerException("handle is null.");
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
         
        try {
            conn = Database.createConnection();
            ps = conn.prepareStatement(LOGIN);               
            ps.setString(1, handle);
            ps.setString(2, password);
            rs = ps.executeQuery();
                        
            if (rs.next()) {                
                return populateUserProfile(rs);
            } 
            
            // try to login with 1.0 password
            ps = conn.prepareStatement(LOGIN_ZOJ1);               
            ps.setString(1, handle);
            ps.setString(2, password);
            ps.setString(3, handle.length() > 1 ? handle.substring(0, 2) : handle + handle);
            
            if (!rs.next()) {
            	return null;
            }
            
            UserProfile user = populateUserProfile(rs);
            
            // fix user password and email
            fixUserPassword(conn, user, password);
            fixUserEmail(conn, user);
            
            return user;
            
        } catch (SQLException e) {
            throw new PersistenceException("Failed to login the user" + handle + " " + password, e);
        } finally {
            Database.dispose(conn, ps, rs);
        }  
    }
    
    private void fixUserPassword(Connection conn, UserProfile user, String password) {
    	try {
    		String sql = "update user_profile set password=MD5(?) where user_profile_id=?";
    		PreparedStatement ps = conn.prepareStatement(sql);               
    		ps.setString(1, password);
            ps.setLong(2, user.getId());
            ps.executeUpdate();
            
    	} catch (Exception e) {
    		
    	}
    }
    
    private void fixUserEmail(Connection conn, UserProfile user) {
    	try {
    		String sql = "update user_profile set email_address=old_email where user_profile_id=? and email_address=?";
    		PreparedStatement ps = conn.prepareStatement(sql);               
    		ps.setLong(1, user.getId());
    		ps.setString(2, user.getHandle() + "@magicemailhost.com");
            ps.executeUpdate();
    	} catch (Exception e) {
    		
    	}
    }

    
    
    /**
     * <p>Gets the user profile with given email in persistence layer.</p>
     *
     * @param email the email of the user profile
     * @return the user profile with given email in persistence layer
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public UserProfile getUserProfileByEmail(String email) throws PersistenceException {
    	if (email == null) {
    		throw new NullPointerException("email is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_USER_BY_EMAIL);   
            ps.setString(1, email);
            rs = ps.executeQuery();
                        
            if (rs.next()) {
            	return populateUserProfile(rs);
            } else {
            	return null;
            } 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the user profile with email " + email, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }  
    }

    /**
     * <p>Gets the user profile with given code in persistence layer.</p>
     *
     * @param code the code of the user profile
     * @return the user profile with given code in persistence layer
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public UserProfile getUserProfileByCode(String code) throws PersistenceException {
    	if (code == null) {
    		throw new NullPointerException("code is null.");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	        
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_USER_BY_CODE);   
            ps.setString(1, code);
            rs = ps.executeQuery();
                        
            if (rs.next()) {
            	return populateUserProfile(rs);
            } else {
            	return null;
            } 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the user profile with code " + code, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }  
    }

    /**
     * <p>Searchs all user profiles according with the given criteria in persistence layer.</p>
     *
     * @return a list of user profiles according with the given criteria
     * @param criteria the user profile search criteria
     * @param offset the offset of the start position to search
     * @param count the maximum number of user profiles in returned list
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public List searchUserProfiles(UserCriteria criteria, int offset, int count) throws PersistenceException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                        
        try {
            conn = Database.createConnection();
            ps = getUserSearchSql(conn, criteria, false, offset, count);   
            rs = ps.executeQuery();

            List users = new ArrayList();
            while (rs.next()) {
                users.add(populateUserProfile(rs));              
            } 
            return users;
            
        } catch (SQLException e) {
            throw new PersistenceException("Failed to search user." , e);
        } finally {
            Database.dispose(conn, ps, rs);
        }  
    }
    
    public int searchUserProfilesCount(UserCriteria criteria) throws PersistenceException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                        
        try {
            conn = Database.createConnection();
            ps = getUserSearchSql(conn, criteria, true, 0, 0);   
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
            
        } catch (SQLException e) {
            throw new PersistenceException("Failed to search user." , e);
        } finally {
            Database.dispose(conn, ps, rs);
        }  
    }

    private void appendParameter(String name, Object value, StringBuilder sb, List values) {
        if (value == null) {
            return;
        }
        if (values.size() == 0) {
            sb.append(" WHERE ");
        } else {
            sb.append(" AND ");
        }
        if (value instanceof String) {
            sb.append(name + " like ?");
            values.add("%" + value + "%");
        } else {
            sb.append(name + "= ?");
            values.add(value);
        }
        
    }
    private PreparedStatement getUserSearchSql(Connection conn, UserCriteria criteria, 
            boolean isCount, int offset, int count)
        throws SQLException {
        
        List values = new ArrayList();
        StringBuilder sb = new StringBuilder();        
        sb.append(isCount ? "SELECT count(*) FROM user_profile" : GET_USER);
        appendParameter("email_address", criteria.getEmail(), sb, values);        
        appendParameter("country_id", criteria.getCountryId(), sb, values);
        appendParameter("first_name", criteria.getFirstName(), sb, values);
        appendParameter("last_name", criteria.getLastName(), sb, values);
        appendParameter("school", criteria.getSchool(), sb, values);
        appendParameter("handle", criteria.getHandle(), sb, values);        
        if (criteria.getRoleId() != null) {
            if (values.size() == 0) {
                sb.append(" WHERE ");
            } else {
                sb.append(" AND ");
            }
            sb.append("user_profile_id IN (SELECT user_profile_id FROM user_role WHERE role_id=?)");
            values.add(criteria.getRoleId());
        }
        
        if (!isCount) {
            sb.append(" ORDER BY handle");
            sb.append(" LIMIT " + offset + "," + count);
        }
        
        //System.out.println(sb);
        PreparedStatement ps = conn.prepareStatement(sb.toString());
        for (int i = 0; i < values.size(); ++i) {
            Object value = values.get(i);
            if (value instanceof String) {
                ps.setString(i + 1, (String) value); 
            } else {
                ps.setLong(i + 1, ((Long) value).longValue());
            }
                
        }
        return ps;               
        
    }

    /**
     * <p>Creates the specified user preference in persistence layer.</p>
     *
     * @param preference the UserPreference instance to create
     * @param user the id of the user who made this modification
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createUserPreference(UserPreference preference, long user) throws PersistenceException {
    	if (preference == null) {
    		throw new NullPointerException("preference is null.");
    	}    
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
            ps = conn.prepareStatement(INSERT_USER_PREFERENCE);              
            ps.setLong(1, preference.getId());
            ps.setString(2, preference.getPlan());
            ps.setInt(3, preference.getProblemPaging());
            ps.setInt(4, preference.getSubmissionPaging());
            ps.setInt(5, preference.getStatusPaging());
            ps.setInt(6, preference.getUserPaging());            
            ps.setInt(7, preference.getThreadPaging());
            ps.setInt(8, preference.getPostPaging());
            ps.setLong(9, user);            
            ps.setTimestamp(10, new Timestamp(new Date().getTime()));                        
            ps.setLong(11, user);            
            ps.setTimestamp(12, new Timestamp(new Date().getTime()));
            
            ps.executeUpdate();            
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create preference.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }

    /**
     * <p>Updates the specified user preference in persistence layer.</p>
     *
     * @param preference the UserPreference instance to update
     * @param user the id of the user who made this modification
     * @throws NullPointerException if argument is null
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void updateUserPreference(UserPreference preference, long user) throws PersistenceException {
    	if (preference == null) {
    		throw new NullPointerException("preference is null.");
    	}    
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
            ps = conn.prepareStatement(UPDATE_USER_PREFERENCE);                          
            ps.setString(1, preference.getPlan());
            ps.setInt(2, preference.getProblemPaging());
            ps.setInt(3, preference.getSubmissionPaging());
            ps.setInt(4, preference.getStatusPaging());
            ps.setInt(5, preference.getUserPaging());            
            ps.setInt(6, preference.getThreadPaging());
            ps.setInt(7, preference.getPostPaging());
            ps.setLong(8, user);            
            ps.setTimestamp(9, new Timestamp(new Date().getTime()));       
            ps.setLong(10, preference.getId());
            
            ps.executeUpdate();            
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create preference.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        } 
    }


    /**
     * <p>Gets the user preference with given id in persistence layer.</p>
     *
     * @param id the id of the user preference
     * @return the user preference with given id in persistence layer
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public UserPreference getUserPreference(long id) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_USER_PREFERENCE);   
            ps.setLong(1, id);
            rs = ps.executeQuery();
                        
            if (rs.next()) {
            	UserPreference preference = new UserPreference();
            	preference.setId(rs.getLong(DatabaseConstants.USER_PREFERENCE_USER_PROFILE_ID));
            	preference.setPlan(rs.getString(DatabaseConstants.USER_PREFERENCE_PLAN));
            	preference.setPostPaging(rs.getInt(DatabaseConstants.USER_PREFERENCE_POST_PAGING));
            	preference.setProblemPaging(rs.getInt(DatabaseConstants.USER_PREFERENCE_PROBLEM_PAGING));
            	preference.setStatusPaging(rs.getInt(DatabaseConstants.USER_PREFERENCE_STATUS_PAGING));
            	preference.setSubmissionPaging(rs.getInt(DatabaseConstants.USER_PREFERENCE_SUBMISSION_PAGING));
            	preference.setThreadPaging(rs.getInt(DatabaseConstants.USER_PREFERENCE_THREAD_PAGING));
            	preference.setUserPaging(rs.getInt(DatabaseConstants.USER_PREFERENCE_USER_PAGING));
            	return preference;
            } else {
            	return null;
            } 
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to get the user preference with id " + id, e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }  
    }

    /**
     * <p>Creates a confirm code for given user in persistence layer.</p>
     *
     * @param id the id of the user
     * @param code the confirm code
     * @param user the id of the user who made this modification
     * @throws NullPointerException if argument is null
     * @throws IllegalArgumentException if argument is empty
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void createConfirmCode(long id, String code, long user) throws PersistenceException {
    	if (code == null) {
    		throw new NullPointerException("code is null.");
    	}
    	
    	if (code.trim().length() == 0) {
    		throw new IllegalArgumentException("code is empty");
    	}
    	
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
        	ps = conn.prepareStatement(UPDATE_CODE);
        	ps.setString(1, code);
        	ps.setLong(2, id);
        	int row = ps.executeUpdate();
        	if (row == 0) {
	            ps = conn.prepareStatement(INSERT_CODE);  
	            ps.setLong(1, id);
	            ps.setString(2, code);
	            ps.executeUpdate();            
        	}
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create code.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        }   
    }


    /**
     * <p>Deletes the confirm code of given user in persistence layer.</p>
     *
     * @param id the id of the user
     * @param user the id of the user who made this modification
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public void deleteConfirmCode(long id, long user) throws PersistenceException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
        	conn = Database.createConnection();        	
	
            ps = conn.prepareStatement(DELETE_CODE);  
            ps.setLong(1, id);            
            ps.executeUpdate();            
            
        } catch (SQLException e) {
        	throw new PersistenceException("Failed to create code.", e);
		} finally {
        	Database.dispose(conn, ps, rs);
        } 
    }

    /**
     * <p>Gets the confirm code with given id in persistence layer.</p>
     *
     * @param id the id of the user
     * @return the confirm code of given user
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    public String getConfirmCode(long id) throws PersistenceException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
            	
        try {
        	conn = Database.createConnection();
            ps = conn.prepareStatement(GET_CODE);   
            ps.setLong(1, id);
            rs = ps.executeQuery();
                        
            if (rs.next()) {
            	return rs.getString(DatabaseConstants.CONFIRMATION_CODE);
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
     * <p>Gets all countries from the persistence layer.</p>
     *
     * @return a list containing all country names 
     */
    public List getAllCountries() throws PersistenceException {    
    	if (allCountries == null) {
    		synchronized (this) {
    			if (allCountries == null) {
    				Connection conn = null;
    			    PreparedStatement ps = null;
    			    ResultSet rs = null;
    			            	
    		        try {
    			    	conn = Database.createConnection();
    			        ps = conn.prepareStatement(GET_ALL_COUNTRIES);            
    			        rs = ps.executeQuery();
    			        
    			        List countries = new ArrayList();
    			        while (rs.next()) {
    			        	countries.add(new Country(rs.getLong(DatabaseConstants.COUNTRY_COUNTRY_ID),
    			        							  rs.getString(DatabaseConstants.COUNTRY_NAME)));   	            			   	            	
    			        }        
    			        allCountries = Collections.unmodifiableList(countries);   	                			            			     
    				} catch (Exception e) {
    					throw new PersistenceCreationException("Failed to get all countries", e);
    				} finally {
    			    	Database.dispose(conn, ps, rs);
    		        }     				
    			}
    		}
    	}
    	
    	return allCountries;    	
    }
    
    /**
     * Populates a UserProfile instance with the given ResultSet.
     * @param rs the ResultSet
     * @return the UserProfile instance
     * @throws SQLException if any error occurs
     */
    private UserProfile populateUserProfile(ResultSet rs) throws SQLException {
    	UserProfile profile = new UserProfile();
    	
		profile.setId(rs.getLong(DatabaseConstants.USER_PROFILE_USER_PROFILE_ID));            			
		profile.setHandle(rs.getString(DatabaseConstants.USER_PROFILE_HANDLE));
		profile.setPassword(rs.getString(DatabaseConstants.USER_PROFILE_PASSWORD));
		profile.setEmail(rs.getString(DatabaseConstants.USER_PROFILE_EMAIL_ADDRESS));
		profile.setRegDate(rs.getTimestamp(DatabaseConstants.USER_PROFILE_REG_DATE));
		profile.setFirstName(rs.getString(DatabaseConstants.USER_PROFILE_FIRST_NAME));
		profile.setLastName(rs.getString(DatabaseConstants.USER_PROFILE_LAST_NAME));
		profile.setAddressLine1(rs.getString(DatabaseConstants.USER_PROFILE_ADDRESS_LINE1));
		profile.setAddressLine2(rs.getString(DatabaseConstants.USER_PROFILE_ADDRESS_LINE2));
		profile.setCity(rs.getString(DatabaseConstants.USER_PROFILE_CITY));
		profile.setState(rs.getString(DatabaseConstants.USER_PROFILE_STATE));
		profile.setCountry(new Country(rs.getLong(DatabaseConstants.USER_PROFILE_COUNTRY_ID), "foo"));
		profile.setZipCode(rs.getString(DatabaseConstants.USER_PROFILE_ZIP_CODE));
		profile.setPhoneNumber(rs.getString(DatabaseConstants.USER_PROFILE_PHONE_NUMBER));
		profile.setBirthDate(rs.getDate(DatabaseConstants.USER_PROFILE_BIRTH_DATE));
		String gender = rs.getString(DatabaseConstants.USER_PROFILE_GENDER);		
		profile.setGender(gender == null || gender.length() == 0 ? ' ' : gender.charAt(0));            
		profile.setSchool(rs.getString(DatabaseConstants.USER_PROFILE_SCHOOL));
		profile.setMajor(rs.getString(DatabaseConstants.USER_PROFILE_MAJOR));
		profile.setGraduateStudent(rs.getBoolean(DatabaseConstants.USER_PROFILE_GRADUATE_STUDENT));
		profile.setGraduationYear(rs.getInt(DatabaseConstants.USER_PROFILE_GRADUATION_YEAR));
		profile.setStudentNumber(rs.getString(DatabaseConstants.USER_PROFILE_STUDENT_NUMBER));
		profile.setConfirmed(rs.getBoolean(DatabaseConstants.USER_PROFILE_CONFIRMED));
		profile.setActive(rs.getBoolean(DatabaseConstants.USER_PROFILE_ACTIVE));
        profile.setNickName(rs.getString("nickname"));
        Timestamp loginDate = rs.getTimestamp("last_login_date");
        profile.setLastLoginDate(loginDate == null ? null : new Date(loginDate.getTime()));
        profile.setLastLoginIP(rs.getString("last_login_ip"));
		return profile;
    }
}


