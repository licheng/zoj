/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.form;

import java.io.Serializable;
import java.text.ParseException;
import org.apache.struts.action.ActionForm;
import cn.edu.zju.acm.onlinejudge.bean.request.UserCriteria;


/**
 * <p>UserSearchForm.</p>
 * 
 * @version 2.0
 * @author ZOJDEV
 */
public class UserSearchForm extends ActionForm implements Serializable {
	
    /**
     * <p>Represents search.</p>
     */
    private boolean search = false;

    /**
     * <p>Represents handle.</p>
     */
    private String handle;

    /**
     * <p>Represents email.</p>
     */
    private String email;

    /**
     * <p>Represents roleId.</p>
     */
    private String roleId;
    
    /**
     * <p>Represents firstName.</p>
     */
    private String firstName;
    
    /**
     * <p>Represents lastName.</p>
     */
    private String lastName;

    /**
     * <p>Represents countryId.</p>
     */
    private String countryId;

    /**
     * <p>Represents school.</p>
     */
    private String school;
    
    /**
     * <p>Represents totalPages.</p>
     */
    private String totalPages;
    
    /**
     * <p>Represents pageNumber.</p>
     */
    private String pageNumber;
    
    /**
     * <p>Represents paging.</p>
     */
    private String paging;
    
    /**
     * UserSearchForm.
     */
    public UserSearchForm() {    	
    }
    
    /**
     * @param search The search to set.
     */
    public void setSearch(boolean search) {
        this.search = search;
    }

    /**
     * @return Returns the search.
     */
    public boolean isSearch() {
        return search;
    }
            
    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }    
    
    public UserCriteria toUserCriteria() throws ParseException, NumberFormatException {
    	
    	UserCriteria criteria = new UserCriteria();
                
    	if (handle != null && handle.trim().length() > 0) {
    		criteria.setHandle(handle.trim());
    	}
    	if (email != null && email.trim().length() > 0) {
    		criteria.setEmail(email.trim());
    	}
    	if (roleId != null && roleId.trim().length() > 0) {
    		criteria.setRoleId(Long.valueOf(roleId));
    	}
        if (firstName != null && firstName.trim().length() > 0) {
            criteria.setFirstName(firstName.trim());
        }
        if (lastName != null && lastName.trim().length() > 0) {
            criteria.setLastName(lastName.trim());
        }
        if (countryId != null && countryId.trim().length() > 0) {
            criteria.setCountryId(Long.valueOf(countryId));
        }        
        if (school != null && school.trim().length() > 0) {
            criteria.setSchool(school.trim());
        }
    	return criteria;
    }

    public String getPaging() {
        return paging;
    }

    public void setPaging(String paging) {
        this.paging = paging;
    }        
}
