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

package cn.edu.zju.acm.onlinejudge.form;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Country;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * <p>
 * ProfileForm.
 * </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class ProfileForm extends ActionForm implements Serializable {

    /**
     * The handle.
     */
    private String handle = null;

    /**
     * The nick.
     */
    private String nick = null;

    /**
     * The password.
     */
    private String password = null;

    /**
     * The confirmPassword.
     */
    private String confirmPassword = null;
    
    /**
     * The newPassword.
     */
    private String newPassword = null;

    /**
     * The email.
     */
    private String email = null;    

    /**
     * The firstName.
     */
    private String firstName = null;

    /**
     * The lastName.
     */
    private String lastName = null;

    /**
     * The birthday.
     */
    private String birthday = null;

    /**
     * The gender.
     */
    private String gender = null;

    /**
     * The addressLine1.
     */
    private String addressLine1 = null;

    /**
     * The addressLine2.
     */
    private String addressLine2 = null;

    /**
     * The state.
     */
    private String state = null;

    /**
     * The city.
     */
    private String city = null;

    /**
     * The country.
     */
    private String country = null;

    /**
     * The zipCode.
     */
    private String zipCode = null;

    /**
     * The phone.
     */
    private String phone = null;

    /**
     * The school.
     */
    private String school = null;

    /**
     * The graduationYear.
     */
    private String graduationYear = null;

    /**
     * The major.
     */
    private String major = null;

    /**
     * The studentNumber.
     */
    private String studentNumber = null;

    /**
     * The graduateStudent.
     */
    private boolean graduateStudent = false;
    
    /**
     * The active.
     */
    private boolean active = false;
       
    
    /**
     * The roles.
     */
    private String[] roles = null;

    /**
     * Empty constructor.
     */
    public ProfileForm() {
        // Empty constructor
    }

    /**
     * Sets the handle.
     *
     * @prama handle the handle to set.
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }

    /**
     * Gets the handle.
     *
     * @return the handle.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the nick.
     *
     * @prama nick the nick to set.
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * Gets the nick.
     *
     * @return the nick.
     */
    public String getNick() {
        return nick;
    }

    /**
     * Sets the password.
     *
     * @prama password the password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the password.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the confirmPassword.
     *
     * @prama confirmPassword the confirmPassword to set.
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Gets the confirmPassword.
     *
     * @return the confirmPassword.
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * Sets the newPassword.
     *
     * @prama newPassword the newPassword to set.
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Gets the newPassword.
     *
     * @return the newPassword.
     */
    public String getNewPassword() {
        return newPassword;
    }
    
    /**
     * Sets the email.
     *
     * @prama email the email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the email.
     *
     * @return the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the firstName.
     *
     * @prama firstName the firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the firstName.
     *
     * @return the firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the lastName.
     *
     * @prama lastName the lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the lastName.
     *
     * @return the lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the birthday.
     *
     * @prama birthday the birthday to set.
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * Gets the birthday.
     *
     * @return the birthday.
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * Sets the gender.
     *
     * @prama gender the gender to set.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the gender.
     *
     * @return the gender.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the addressLine1.
     *
     * @prama addressLine1 the addressLine1 to set.
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * Gets the addressLine1.
     *
     * @return the addressLine1.
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets the addressLine2.
     *
     * @prama addressLine2 the addressLine2 to set.
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * Gets the addressLine2.
     *
     * @return the addressLine2.
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets the state.
     *
     * @prama state the state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the state.
     *
     * @return the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the city.
     *
     * @prama city the city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the city.
     *
     * @return the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the country.
     *
     * @prama country the country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the country.
     *
     * @return the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the zipCode.
     *
     * @prama zipCode the zipCode to set.
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets the zipCode.
     *
     * @return the zipCode.
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the phone.
     *
     * @prama phone the phone to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the phone.
     *
     * @return the phone.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the school.
     *
     * @prama school the school to set.
     */
    public void setSchool(String school) {
        this.school = school;
    }

    /**
     * Gets the school.
     *
     * @return the school.
     */
    public String getSchool() {
        return school;
    }


    /**
     * Sets the major.
     *
     * @prama major the major to set.
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * Gets the major.
     *
     * @return the major.
     */
    public String getMajor() {
        return major;
    }

    /**
     * Sets the studentNumber.
     *
     * @prama studentNumber the studentNumber to set.
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * Gets the studentNumber.
     *
     * @return the studentNumber.
     */
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * Sets the graduationYear.
     *
     * @prama graduationYear the graduationYear to set.
     */
    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    /**
     * Gets the graduationYear.
     *
     * @return the graduationYear.
     */
    public String getGraduationYear() {
        return graduationYear;
    }
    
    
    
    /**
     * Sets the graduateStudent.
     *
     * @prama graduateStudent the graduateStudent to set.
     */
    public void setGraduateStudent(boolean graduateStudent) {
        this.graduateStudent = graduateStudent;
    }

    /**
     * Gets the graduateStudent.
     *
     * @return the graduateStudent.
     */
    public boolean isGraduateStudent() {
        return graduateStudent;
    }
    
    /**
     * Gets the active.
     *
     * @return the active.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets the active.
     *
     * @prama active the graduateStudent to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Sets the roles.
     *
     * @prama roles the roles to set.
     */
    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    /**
     * Gets the roles.
     *
     * @return the roles.
     */
    public String[] getRoles() {
        return roles;
    }

	 /**
     * Validates the form.
     *
     * @param mapping the action mapping.
     * @param request the user request.
     *
     * @return collection of validation errors.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        
        try {
        	request.setAttribute("Countries", 
        	PersistenceManager.getInstance().getUserPersistence().getAllCountries());	            
        } catch (Exception e) {
            e.printStackTrace();
        	request.setAttribute("Countries", new ArrayList<Country>());
        }
        
        if (handle == null) {
        	return null;
        }
        handle = handle.trim();

        ActionErrors errors = new ActionErrors();
        checkHandle(handle, errors, "handle", "ProfileForm.handle.required");        
        checkEmail(email, errors, "email", "ProfileForm.email.required");
        
        
        if (password == null) {
        	checkRequired(newPassword, errors, "newPassword", "ProfileForm.newPassword.required");
        	checkRequired(confirmPassword, errors, "confirmPassword", "ProfileForm.confirmPassword.required");        	                    	        
        } else {
        	checkRequired(password, errors, "password", "ProfileForm.password.required");        	
        }
        	
        if (((newPassword != null && newPassword.length() > 0)
        	|| (confirmPassword != null && confirmPassword.length() > 0))
        	&& !newPassword.equals(confirmPassword)) {
        	errors.add("confirmPassword", new ActionMessage("ProfileForm.confirmPassword.notMatch"));
        }
        
        checkRequired(firstName, errors, "firstName", "ProfileForm.firstName.required");
        checkRequired(lastName, errors, "lastName", "ProfileForm.lastName.required");
        checkRequired(addressLine1, errors, "addressLine1", "ProfileForm.addressLine1.required");
        checkRequired(city, errors, "city", "ProfileForm.city.required");
        checkRequired(state, errors, "state", "ProfileForm.state.required");        
        checkRequired(zipCode, errors, "zipCode", "ProfileForm.zipCode.required");
        checkRequired(phone, errors, "phone", "ProfileForm.phone.required");
        
        checkBirthday(errors);
        checkCountry(errors);
        checkGender(errors);
        checkGraduationYear(errors);    
                
        return errors;
    }
    
    /**
     * Checks the required field.
     * 
     * @param field the field to check
     * @param errors the errors
     * @param property the error property
     * @param message the error message
     */
    private void checkRequired(String field, ActionErrors errors, String property, String message) {
    	if (field == null || field.trim().length() == 0) {
        	errors.add(property, new ActionMessage(message));
        }
    }
    
    private void checkHandle(String handle, ActionErrors errors, String property, String message) {
        checkRequired(handle, errors, "handle", "ProfileForm.handle.required");
        if (handle != null && handle.length() > 0) {    
            if (handle.length() < 2) {
                errors.add("handle", new ActionMessage("ProfileForm.handle.invalid"));
                return;         
            }
            for (int i = 0; i < handle.length(); ++i) {
                char c = handle.charAt(i);
                if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_')) {
                    errors.add("handle", new ActionMessage("ProfileForm.handle.invalid"));
                    return;
                }
            }            
        }
    }
    
    private void checkEmail(String email, ActionErrors errors, String property, String message) {
        checkRequired(email, errors, "email", "ProfileForm.email.required");
        if (errors.size() == 0) {
            // TODO
            //errors.add("email", new ActionMessage("ProfileForm.email.invalid"));
        }
    }
    
    
    /**
     * Checks the gender. Gender is required and should be ' ', 'M' or 'F'.
     * 
     * @param errors the errors
     */
    private void checkGender(ActionErrors errors) {
    	if (gender == null || gender.length() == 0) {
    		errors.add("gender", new ActionMessage("ProfileForm.gender.required"));        	
        } else if (!(" ".equals(gender) || "M".equals(gender) || "F".equals(gender))) {
        	errors.add("gender", new ActionMessage("ProfileForm.gender.invalid"));
        }
    }
    
    /**
     * Checks the graduation year. Graduation year is not required and should be a valid integer.
     * 
     * @param errors the errors
     */
    private void checkGraduationYear(ActionErrors errors) {
    	if (!(graduationYear == null || graduationYear.trim().length() == 0)) {    		        	        
        	int year = 10000;
        	try {
        		year = Integer.parseInt(graduationYear);
        	} catch (Exception e) {
        		
        	}
        	if (year > 9999 || year < 0) {
        		errors.add("graduationYear", new ActionMessage("ProfileForm.graduationYear.invalid"));
        	}
        }
    }
    
    /**
     * Checks the country id. Country id is required and should be a valid integer.
     * 
     * @param errors the errors
     */
    private void checkCountry(ActionErrors errors) {
    	if (country == null || country.trim().length() ==0) {
    		errors.add("country", new ActionMessage("ProfileForm.country.required"));
    	} else {
	    	Country c = null;
	    	try {
	    		c = PersistenceManager.getInstance().getCountry(country);	    		
	    	} catch (Exception e) {    		
	    	}
	    	if (c == null) {
	    		errors.add("country", new ActionMessage("ProfileForm.country.invalid"));
	    	}
    	}
    }
    
    /**
     * Checks the birthday. Birthday should be a valid date.
     * 
     * @param errors the errors
     */
    private void checkBirthday(ActionErrors errors) {
    	if (birthday == null || birthday.trim().length() ==0) {
    		errors.add("birthday", new ActionMessage("ProfileForm.birthday.required"));
    	} else {
    		if (parseDate(birthday) == null) {
        		errors.add("birthday", new ActionMessage("ProfileForm.birthday.invalid"));
        	}
    	}    	    	
    }
    
    /**
     * Parses given date, return null if it's invalid.
     * 
     * @param date the date to parse
     * @return the Date instance or null.
     */
    private Date parseDate(String date) {
    	try {    		
    		return DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(date);
    	} catch (Exception e) {
    		return null;
    	}    	
    }    
    
    /**
     * Converts the form bean to UserProfile bean.
     * @return the UserProfile bean.
     * @throws PersistenceException if failed to convert
     */
    public UserProfile toUserProfile() throws PersistenceException {
    	UserProfile profile = new UserProfile();
        if (nick != null && nick.trim().length() > 0) {
            profile.setNickName(nick);
        } else {
            profile.setNickName(handle);
        }
    	profile.setAddressLine1(addressLine1);
    	profile.setAddressLine2(addressLine2);
    	profile.setBirthDate(parseDate(birthday));
    	profile.setCity(city);    	
    	profile.setCountry(PersistenceManager.getInstance().getCountry(country));
    	profile.setEmail(email.trim());
    	profile.setFirstName(firstName.trim());
    	profile.setLastName(lastName.trim());
    	profile.setGender(gender.charAt(0));
    	profile.setGraduateStudent(graduateStudent);
    	profile.setGraduationYear(
    			graduationYear == null || graduationYear.trim().length() == 0 ? 0 : Integer.parseInt(graduationYear));
    	profile.setHandle(handle.trim());
    	profile.setMajor(major);
    	profile.setPhoneNumber(phone);    	
    	profile.setSchool(school);
    	profile.setState(state);
    	profile.setStudentNumber(studentNumber);
    	profile.setZipCode(zipCode);
    	profile.setPassword(newPassword);    	
    	return profile;
    }
    
    /**
     * Converts the form bean to UserProfile bean.
     * @return the UserProfile bean.
     * @throws PersistenceException if failed to convert
     */
    public void populate(UserProfile profile) throws PersistenceException {
    	
        this.setNick(profile.getNickName());
    	this.setAddressLine1(profile.getAddressLine1());
    	this.setAddressLine2(profile.getAddressLine2());
    	this.setBirthday(new SimpleDateFormat("MM/dd/yyyy").format(profile.getBirthDate()));
    	this.setCity(profile.getCity());    	
    	this.setCountry(String.valueOf(profile.getCountry().getId()));
    	this.setEmail(profile.getEmail());
    	this.setFirstName(profile.getFirstName());
    	this.setLastName(profile.getLastName());
    	this.setGender(String.valueOf(profile.getGender()));
    	this.setGraduateStudent(profile.isGraduateStudent());
    	this.setGraduationYear(profile.getGraduationYear() == 0 ? "" : String.valueOf(profile.getGraduationYear()));
    	this.setHandle(profile.getHandle());
    	this.setMajor(profile.getMajor());
    	this.setPhone(profile.getPhoneNumber());
    	this.setSchool(profile.getSchool());
    	this.setState(profile.getState());
    	this.setStudentNumber(profile.getStudentNumber());
    	this.setZipCode(profile.getZipCode());
    	this.setPassword(null);
    	this.setNewPassword(null);
    	this.setConfirmPassword(null);
    }
}
