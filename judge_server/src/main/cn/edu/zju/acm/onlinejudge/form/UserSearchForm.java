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
import java.text.ParseException;

import org.apache.struts.action.ActionForm;

import cn.edu.zju.acm.onlinejudge.bean.request.UserCriteria;

/**
 * <p>
 * UserSearchForm.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 */
public class UserSearchForm extends ActionForm implements Serializable {

    /**
     * <p>
     * Represents search.
     * </p>
     */
    private boolean search = false;

    /**
     * <p>
     * Represents handle.
     * </p>
     */
    private String handle;

    /**
     * <p>
     * Represents email.
     * </p>
     */
    private String email;

    /**
     * <p>
     * Represents roleId.
     * </p>
     */
    private String roleId;

    /**
     * <p>
     * Represents firstName.
     * </p>
     */
    private String firstName;

    /**
     * <p>
     * Represents lastName.
     * </p>
     */
    private String lastName;

    /**
     * <p>
     * Represents countryId.
     * </p>
     */
    private String countryId;

    /**
     * <p>
     * Represents school.
     * </p>
     */
    private String school;

    /**
     * <p>
     * Represents totalPages.
     * </p>
     */
    private String totalPages;

    /**
     * <p>
     * Represents pageNumber.
     * </p>
     */
    private String pageNumber;

    /**
     * <p>
     * Represents paging.
     * </p>
     */
    private String paging;

    /**
     * UserSearchForm.
     */
    public UserSearchForm() {}

    /**
     * @param search
     *            The search to set.
     */
    public void setSearch(boolean search) {
        this.search = search;
    }

    /**
     * @return Returns the search.
     */
    public boolean isSearch() {
        return this.search;
    }

    public String getCountryId() {
        return this.countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getHandle() {
        return this.handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getSchool() {
        return this.school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public UserCriteria toUserCriteria() throws ParseException, NumberFormatException {

        UserCriteria criteria = new UserCriteria();

        if (this.handle != null && this.handle.trim().length() > 0) {
            criteria.setHandle(this.handle.trim());
        }
        if (this.email != null && this.email.trim().length() > 0) {
            criteria.setEmail(this.email.trim());
        }
        if (this.roleId != null && this.roleId.trim().length() > 0) {
            criteria.setRoleId(Long.valueOf(this.roleId));
        }
        if (this.firstName != null && this.firstName.trim().length() > 0) {
            criteria.setFirstName(this.firstName.trim());
        }
        if (this.lastName != null && this.lastName.trim().length() > 0) {
            criteria.setLastName(this.lastName.trim());
        }
        if (this.countryId != null && this.countryId.trim().length() > 0) {
            criteria.setCountryId(Long.valueOf(this.countryId));
        }
        if (this.school != null && this.school.trim().length() > 0) {
            criteria.setSchool(this.school.trim());
        }
        return criteria;
    }

    public String getPaging() {
        return this.paging;
    }

    public void setPaging(String paging) {
        this.paging = paging;
    }
}
