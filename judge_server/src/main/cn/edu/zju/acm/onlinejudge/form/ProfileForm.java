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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.UserPreference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Country;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * ProfileForm.
 * </p>
 * 
 * @author Zhang, Zheng
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
     * The plan.
     */
    private String plan = null;

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
        return this.handle;
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
        return this.nick;
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
        return this.password;
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
        return this.confirmPassword;
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
        return this.newPassword;
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
        return this.email;
    }

    /**
     * Sets the plan.
     * 
     * @prama plan the email to set.
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * Gets the plan.
     * 
     * @return the plan.
     */
    public String getPlan() {
        return this.plan;
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
        return this.firstName;
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
        return this.lastName;
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
        return this.birthday;
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
        return this.gender;
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
        return this.addressLine1;
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
        return this.addressLine2;
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
        return this.state;
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
        return this.city;
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
        return this.country;
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
        return this.zipCode;
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
        return this.phone;
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
        return this.school;
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
        return this.major;
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
        return this.studentNumber;
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
        return this.graduationYear;
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
        return this.graduateStudent;
    }

    /**
     * Gets the active.
     * 
     * @return the active.
     */
    public boolean isActive() {
        return this.active;
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
        return this.roles;
    }

    /**
     * Validates the form.
     * 
     * @param mapping
     *            the action mapping.
     * @param request
     *            the user request.
     * 
     * @return collection of validation errors.
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

        try {
            request.setAttribute("Countries", PersistenceManager.getInstance().getUserPersistence().getAllCountries());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("Countries", new ArrayList<Country>());
        }

        if (this.handle == null) {
            return null;
        }
        this.handle = this.handle.trim();

        ActionErrors errors = new ActionErrors();
        this.checkHandle(this.handle, errors, "handle", "ProfileForm.handle.required");
        this.checkEmail(this.email, errors, "email", "ProfileForm.email.required");

        if (this.password == null) {
            this.checkRequired(this.newPassword, errors, "newPassword", "ProfileForm.newPassword.required");
            this.checkRequired(this.confirmPassword, errors, "confirmPassword", "ProfileForm.confirmPassword.required");
        } else {
            this.checkRequired(this.password, errors, "password", "ProfileForm.password.required");
        }

        if ((this.newPassword != null && this.newPassword.length() > 0 || this.confirmPassword != null &&
            this.confirmPassword.length() > 0) &&
            !this.newPassword.equals(this.confirmPassword)) {
            errors.add("confirmPassword", new ActionMessage("ProfileForm.confirmPassword.notMatch"));
        }

        this.checkRequired(this.firstName, errors, "firstName", "ProfileForm.firstName.required");
        this.checkRequired(this.lastName, errors, "lastName", "ProfileForm.lastName.required");
        this.checkRequired(this.addressLine1, errors, "addressLine1", "ProfileForm.addressLine1.required");
        this.checkRequired(this.city, errors, "city", "ProfileForm.city.required");
        this.checkRequired(this.state, errors, "state", "ProfileForm.state.required");
        this.checkRequired(this.zipCode, errors, "zipCode", "ProfileForm.zipCode.required");
        this.checkRequired(this.phone, errors, "phone", "ProfileForm.phone.required");

        this.checkBirthday(errors);
        this.checkCountry(errors);
        this.checkGender(errors);
        this.checkGraduationYear(errors);

        return errors;
    }

    /**
     * Checks the required field.
     * 
     * @param field
     *            the field to check
     * @param errors
     *            the errors
     * @param property
     *            the error property
     * @param message
     *            the error message
     */
    private void checkRequired(String field, ActionErrors errors, String property, String message) {
        if (field == null || field.trim().length() == 0) {
            errors.add(property, new ActionMessage(message));
        }
    }

    private void checkHandle(String handle, ActionErrors errors, String property, String message) {
        this.checkRequired(handle, errors, "handle", "ProfileForm.handle.required");
        if (handle != null && handle.length() > 0) {
            if (handle.length() < 2) {
                errors.add("handle", new ActionMessage("ProfileForm.handle.invalid"));
                return;
            }
            for (int i = 0; i < handle.length(); ++i) {
                char c = handle.charAt(i);
                if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_')) {
                    errors.add("handle", new ActionMessage("ProfileForm.handle.invalid"));
                    return;
                }
            }
        }
    }

    private void checkEmail(String email, ActionErrors errors, String property, String message) {
        this.checkRequired(email, errors, "email", "ProfileForm.email.required");
        if (errors.size() == 0) {
            // TODO
            // errors.add("email", new ActionMessage("ProfileForm.email.invalid"));
        }
    }

    /**
     * Checks the gender. Gender is required and should be ' ', 'M' or 'F'.
     * 
     * @param errors
     *            the errors
     */
    private void checkGender(ActionErrors errors) {
        if (this.gender == null || this.gender.length() == 0) {
            errors.add("gender", new ActionMessage("ProfileForm.gender.required"));
        } else if (!(" ".equals(this.gender) || "M".equals(this.gender) || "F".equals(this.gender))) {
            errors.add("gender", new ActionMessage("ProfileForm.gender.invalid"));
        }
    }

    /**
     * Checks the graduation year. Graduation year is not required and should be a valid integer.
     * 
     * @param errors
     *            the errors
     */
    private void checkGraduationYear(ActionErrors errors) {
        if (!(this.graduationYear == null || this.graduationYear.trim().length() == 0)) {
            int year = 10000;
            try {
                year = Integer.parseInt(this.graduationYear);
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
     * @param errors
     *            the errors
     */
    private void checkCountry(ActionErrors errors) {
        if (this.country == null || this.country.trim().length() == 0) {
            errors.add("country", new ActionMessage("ProfileForm.country.required"));
        } else {
            Country c = null;
            try {
                c = PersistenceManager.getInstance().getCountry(this.country);
            } catch (Exception e) {}
            if (c == null) {
                errors.add("country", new ActionMessage("ProfileForm.country.invalid"));
            }
        }
    }

    /**
     * Checks the birthday. Birthday should be a valid date.
     * 
     * @param errors
     *            the errors
     */
    private void checkBirthday(ActionErrors errors) {
        if (this.birthday == null || this.birthday.trim().length() == 0) {
            errors.add("birthday", new ActionMessage("ProfileForm.birthday.required"));
        } else {
            if (this.parseDate(this.birthday) == null) {
                errors.add("birthday", new ActionMessage("ProfileForm.birthday.invalid"));
            }
        }
    }

    /**
     * Parses given date, return null if it's invalid.
     * 
     * @param date
     *            the date to parse
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
     * 
     * @return the UserProfile bean.
     * @throws PersistenceException
     *             if failed to convert
     */
    public UserProfile toUserProfile() throws PersistenceException {
        UserProfile profile = new UserProfile();
        if (this.nick != null && this.nick.trim().length() > 0) {
            profile.setNickName(this.nick);
        } else {
            profile.setNickName(this.handle);
        }
        profile.setAddressLine1(this.addressLine1);
        profile.setAddressLine2(this.addressLine2);
        profile.setBirthDate(this.parseDate(this.birthday));
        profile.setCity(this.city);
        profile.setCountry(PersistenceManager.getInstance().getCountry(this.country));
        profile.setEmail(this.email.trim());
        profile.setFirstName(this.firstName.trim());
        profile.setLastName(this.lastName.trim());
        profile.setGender(this.gender.charAt(0));
        profile.setGraduateStudent(this.graduateStudent);
        profile
               .setGraduationYear(this.graduationYear == null || this.graduationYear.trim().length() == 0 ? 0
                                                                                                         : Integer
                                                                                                                  .parseInt(this.graduationYear));
        profile.setHandle(this.handle.trim());
        profile.setMajor(this.major);
        profile.setPhoneNumber(this.phone);
        profile.setSchool(this.school);
        profile.setState(this.state);
        profile.setStudentNumber(this.studentNumber);
        profile.setZipCode(this.zipCode);
        profile.setPassword(this.newPassword);
        return profile;
    }

    public UserPreference toUserPreference() throws PersistenceException {
        UserPreference preference= new UserPreference();
        perference.setId(profile.getId());
        perference.setPlan(this.plan);
        perference.setPostPaging(20); // TODO...
        perference.setProblemPaging(100);
        perference.setStatusPaging(20);
        perference.setThreadPaging(20);
        perference.setUserPaging(20);
        return perference;
    }

    /**
     * Converts the form bean to UserProfile bean.
     * 
     * @return the UserProfile bean.
     * @throws PersistenceException
     *             if failed to convert
     */
    public void populate(UserProfile profile, UserPreference preference) throws PersistenceException {

        this.setNick(profile.getNickName());
        this.setAddressLine1(profile.getAddressLine1());
        this.setAddressLine2(profile.getAddressLine2());
        this.setBirthday(new SimpleDateFormat("MM/dd/yyyy").format(profile.getBirthDate()));
        this.setCity(profile.getCity());
        this.setCountry(String.valueOf(profile.getCountry().getId()));
        this.setEmail(profile.getEmail());
        this.setPlan(preference.getPlan());
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
