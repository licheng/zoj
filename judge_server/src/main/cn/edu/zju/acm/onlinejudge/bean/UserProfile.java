/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.Country;

import java.util.Date;

/**
 * <p>UserProfile bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class UserProfile {

    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents handle.</p>
     */
    private String handle = null;
    
    
    /**
     * <p>Represents nick name.</p>
     */
    private String nickName = null;
    

    /**
     * <p>Represents password.</p>
     */
    private String password = null;

    /**
     * <p>Represents regDate.</p>
     */
    private Date regDate = null;

    /**
     * <p>Represents email.</p>
     */
    private String email = null;

    /**
     * <p>Represents confirmed.</p>
     */
    private boolean confirmed = false;

    /**
     * <p>Represents firstName.</p>
     */
    private String firstName = null;

    /**
     * <p>Represents lastName.</p>
     */
    private String lastName = null;

    /**
     * <p>Represents birthDate.</p>
     */
    private Date birthDate = null;

    /**
     * <p>Represents addressLine1.</p>
     */
    private String addressLine1 = null;

    /**
     * <p>Represents addressLine2.</p>
     */
    private String addressLine2 = null;

    /**
     * <p>Represents city.</p>
     */
    private String city = null;

    /**
     * <p>Represents state.</p>
     */
    private String state = null;

    /**
     * <p>Represents country.</p>
     */
    private Country country = null;

    /**
     * <p>Represents zipCode.</p>
     */
    private String zipCode = null;

    /**
     * <p>Represents phoneNumber.</p>
     */
    private String phoneNumber = null;

    /**
     * <p>Represents school.</p>
     */
    private String school = null;

    /**
     * <p>Represents major.</p>
     */
    private String major = null;

    /**
     * <p>Represents graduateStudent.</p>
     */
    private boolean graduateStudent = false;

    /**
     * <p>Represents graduationYear.</p>
     */
    private int graduationYear = -1;

    /**
     * <p>Represents studentNumber.</p>
     */
    private String studentNumber = null;
    
    private String declaration=null;

    /**
     * <p>Represents gender.</p>
     */
    private char gender = ' ';

    /**
     * <p>Represents active.</p>
     */
    private boolean active = true;
    
    /**
     * <p>Empty constructor.</p>
     */
    public UserProfile() {
    }

    /**
     * <p>Gets id.</p>
     *
     * @return id
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>Sets id.</p>
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>Gets handle.</p>
     *
     * @return handle
     */
    public String getHandle() {
        return this.handle;
    }

    /**
     * <p>Sets handle.</p>
     *
     * @param handle handle
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }

    /**
     * <p>Gets password.</p>
     *
     * @return password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * <p>Sets password.</p>
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <p>Gets regDate.</p>
     *
     * @return regDate
     */
    public Date getRegDate() {
        return this.regDate;
    }

    /**
     * <p>Sets regDate.</p>
     *
     * @param regDate regDate
     */
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    /**
     * <p>Gets email.</p>
     *
     * @return email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * <p>Sets email.</p>
     *
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * <p>Gets confirmed.</p>
     *
     * @return confirmed
     */
    public boolean isConfirmed() {
        return this.confirmed;
    }

    /**
     * <p>Sets confirmed.</p>
     *
     * @param confirmed confirmed
     */
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * <p>Gets firstName.</p>
     *
     * @return firstName
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * <p>Sets firstName.</p>
     *
     * @param firstName firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * <p>Gets lastName.</p>
     *
     * @return lastName
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * <p>Sets lastName.</p>
     *
     * @param lastName lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * <p>Gets birthDate.</p>
     *
     * @return birthDate
     */
    public Date getBirthDate() {
        return this.birthDate;
    }

    /**
     * <p>Sets birthDate.</p>
     *
     * @param birthDate birthDate
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * <p>Gets addressLine1.</p>
     *
     * @return addressLine1
     */
    public String getAddressLine1() {
        return this.addressLine1;
    }

    /**
     * <p>Sets addressLine1.</p>
     *
     * @param addressLine1 addressLine1
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    /**
     * <p>Gets addressLine2.</p>
     *
     * @return addressLine2
     */
    public String getAddressLine2() {
        return this.addressLine2;
    }

    /**
     * <p>Sets addressLine2.</p>
     *
     * @param addressLine2 addressLine2
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    /**
     * <p>Gets city.</p>
     *
     * @return city
     */
    public String getCity() {
        return this.city;
    }

    /**
     * <p>Sets city.</p>
     *
     * @param city city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * <p>Gets state.</p>
     *
     * @return state
     */
    public String getState() {
        return this.state;
    }

    /**
     * <p>Sets state.</p>
     *
     * @param state state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * <p>Gets country.</p>
     *
     * @return country
     */
    public Country getCountry() {
        return this.country;
    }

    /**
     * <p>Sets country.</p>
     *
     * @param country country
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * <p>Gets zipCode.</p>
     *
     * @return zipCode
     */
    public String getZipCode() {
        return this.zipCode;
    }

    /**
     * <p>Sets zipCode.</p>
     *
     * @param zipCode zipCode
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * <p>Gets phoneNumber.</p>
     *
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * <p>Sets phoneNumber.</p>
     *
     * @param phoneNumber phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * <p>Gets school.</p>
     *
     * @return school
     */
    public String getSchool() {
        return this.school;
    }

    /**
     * <p>Sets school.</p>
     *
     * @param school school
     */
    public void setSchool(String school) {
        this.school = school;
    }

    /**
     * <p>Gets major.</p>
     *
     * @return major
     */
    public String getMajor() {
        return this.major;
    }

    /**
     * <p>Sets major.</p>
     *
     * @param major major
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * <p>Gets graduateStudent.</p>
     *
     * @return graduateStudent
     */
    public boolean isGraduateStudent() {
        return this.graduateStudent;
    }

    /**
     * <p>Sets graduateStudent.</p>
     *
     * @param graduateStudent graduateStudent
     */
    public void setGraduateStudent(boolean graduateStudent) {
        this.graduateStudent = graduateStudent;
    }

    /**
     * <p>Gets graduationYear.</p>
     *
     * @return graduationYear
     */
    public int getGraduationYear() {
        return this.graduationYear;
    }

    /**
     * <p>Sets graduationYear.</p>
     *
     * @param graduationYear graduationYear
     */
    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    /**
     * <p>Gets studentNumber.</p>
     *
     * @return studentNumber
     */
    public String getStudentNumber() {
        return this.studentNumber;
    }

    /**
     * <p>Sets studentNumber.</p>
     *
     * @param studentNumber studentNumber
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    /**
     * <p>Gets gender.</p>
     *
     * @return gender
     */
    public char getGender() {
        return this.gender;
    }

    /**
     * <p>Sets gender.</p>
     *
     * @param gender gender
     */
    public void setGender(char gender) {
        this.gender = gender;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    
    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }
}
