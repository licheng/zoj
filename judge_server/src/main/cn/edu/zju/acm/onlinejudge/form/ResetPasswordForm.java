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

import java.io.Serializable;

/**
 * <p>
 * ProfileForm.
 * </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class ResetPasswordForm extends ActionForm implements Serializable {

    /**
     * The handle.
     */
    private String code = null;

    /**
     * The password.
     */
    private String password = null;

    /**
     * The confirmPassword.
     */
    private String confirmPassword = null;

	 /**
     * Validates the form.
     *
     * @param mapping the action mapping.
     * @param request the user request.
     *
     * @return collection of validation errors.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        
        if (password == null) {
        	return null;
        }

        ActionErrors errors = new ActionErrors();
        checkRequired(password, errors, "password", "ResetPasswordForm.password.required");
        checkRequired(confirmPassword, errors, "confirmPassword", "ResetPasswordForm.confirmPassword.required");
        if (!password.equals(confirmPassword)) {
        	errors.add("confirmPassword", new ActionMessage("ResetPasswordForm.confirmPassword.notMatch"));
        }
                
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
    
}
