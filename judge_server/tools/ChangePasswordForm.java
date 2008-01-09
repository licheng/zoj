/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.selfservice.useradministration.forms;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * <p>
 * ChangePasswordForm is used to ...
 * </p>
 *
 * @author oldbig
 * @version 1.0
 */
public class ChangePasswordForm extends ActionForm {

    /**
     * The password.
     */
    private String password = null;

    /**
     * The new password.
     */
    private String newPassword = null;

    /**
     * The confirm password.
     */
    private String confirmPassword = null;

    /**
     * Empty constructor that does nothing.
     */
    public ChangePasswordForm() {
        // Empty constructor
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
     * Sets the new password.
     *
     * @prama newPassword the new password to set.
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Gets the new password.
     *
     * @return the new password.
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the confirm password.
     *
     * @prama confirmPassword the confirm password to set.
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Gets the confirm password.
     *
     * @return the confirm password.
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * <p>
     * Validates the user input.
     * </p>
     *
     * @return collection of validation errors
     * @param mapping ignored
     * @param request ignored
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = null;

        return errors;
    }
}
