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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * <p>
 * LoginForm.
 * </p>
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class LoginForm extends ActionForm implements Serializable {

    /**
     * The handle.
     */
    private String handle = null;

    /**
     * The password.
     */
    private String password = null;

    /**
     * The rememberMe.
     */
    private boolean rememberMe = false;

    /**
     * Empty constructor.
     */
    public LoginForm() {
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
     * Sets the rememberMe.
     * 
     * @prama rememberMe the rememberMe to set.
     */
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    /**
     * Gets the rememberMe.
     * 
     * @return the rememberMe.
     */
    public boolean isRememberMe() {
        return this.rememberMe;
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
        ActionErrors errors = new ActionErrors();

        if (this.handle == null) {
            return errors;
        }
        if (this.handle == null || this.handle.trim().length() == 0) {
            errors.add("handle", new ActionMessage("LoginForm.handle.required"));
        }
        if (this.password == null || this.password.length() == 0) {
            errors.add("password", new ActionMessage("LoginForm.password.required"));
        }

        return errors;
    }

}
