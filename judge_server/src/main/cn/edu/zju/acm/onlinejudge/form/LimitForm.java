/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.form;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>
 * ContestForm.
 * </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class LimitForm extends ActionForm implements Serializable {
	
    /**
     * The id.
     */
    private String id = null;
	    
    /**
     * The timeLimit.
     */
    private String timeLimit = null;

    /**
     * The MemoryLimit.
     */
    private String memoryLimit = null;

    /**
     * The outputLimit.
     */
    private String outputLimit = null;

    /**
     * The submissionLimit.
     */
    private String submissionLimit = null;

    /**
     * Empty constructor.
     */
    public LimitForm() {
        // Empty constructor
    }
    
    /**
     * Sets the id.
     *
     * @prama id the id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the id.
     *
     * @return the id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the timeLimit.
     *
     * @prama timeLimit the timeLimit to set.
     */
    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * Gets the timeLimit.
     *
     * @return the timeLimit.
     */
    public String getTimeLimit() {
        return timeLimit;
    }

    /**
     * Sets the MemoryLimit.
     *
     * @prama memoryLimit the MemoryLimit to set.
     */
    public void setMemoryLimit(String memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    /**
     * Gets the MemoryLimit.
     *
     * @return the MemoryLimit.
     */
    public String getMemoryLimit() {
        return memoryLimit;
    }

    /**
     * Sets the outputLimit.
     *
     * @prama outputLimit the outputLimit to set.
     */
    public void setOutputLimit(String outputLimit) {
        this.outputLimit = outputLimit;
    }

    /**
     * Gets the outputLimit.
     *
     * @return the outputLimit.
     */
    public String getOutputLimit() {
        return outputLimit;
    }

    /**
     * Sets the submissionLimit.
     *
     * @prama submissionLimit the submissionLimit to set.
     */
    public void setSubmissionLimit(String submissionLimit) {
        this.submissionLimit = submissionLimit;
    }

    /**
     * Gets the submissionLimit.
     *
     * @return the submissionLimit.
     */
    public String getSubmissionLimit() {
        return submissionLimit;
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
         if (id == null) {
             return null;
         }
         ActionErrors errors = new ActionErrors();     
         checkInteger(timeLimit, 0, 3600, "timeLimit", errors);
         checkInteger(memoryLimit, 0, 1024 * 1024, "memoryLimit", errors);
         checkInteger(outputLimit, 0, 128 * 1024, "outputLimit", errors);
         checkInteger(submissionLimit, 0, 16 * 1024, "submissionLimit", errors);
         return errors;
     }
     
     
    /**
     * 
     * @param value
     * @param min
     * @param max
     * @param name
     * @param errors
     */
    private void checkInteger(String value, int min, int max, String name, ActionErrors errors) {
        if ((value == null) || (value.trim().length() == 0)) {
            errors.add(name, new ActionMessage("LimitForm." + name + ".required"));
            return;
        }        
        try {
            int intValue = Integer.parseInt(value);
            if (intValue < min || intValue > max) {
                errors.add(name, new ActionMessage("LimitForm." + name + ".outrange"));
            }
        } catch (NumberFormatException e) {
            errors.add(name, new ActionMessage("LimitForm." + name + ".invalid"));            
        }
    }
    
}
