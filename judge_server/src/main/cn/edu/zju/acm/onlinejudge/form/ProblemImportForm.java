package cn.edu.zju.acm.onlinejudge.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

/**
 * <p>ProblemImportForm.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class ProblemImportForm extends ActionForm {
    /** The file that the user has uploaded */
    private FormFile problemFile;
    
    private String contestId;

    /**
     * Constructor.
     */
    public ProblemImportForm() {
    	
    }
    /**
     * Retrieve a representation of the file the user has uploaded
     *
     * @return the uploaded file
     */
    public FormFile getProblemFile() {
        return problemFile;
    }

    /**
     * Set a representation of the file the user has uploaded
     *
     * @param problemFile uploaded by user
     */
    public void setProblemFile(FormFile problemFile) {
        this.problemFile = problemFile;
    }
    
    public String getContestId() {
    	return contestId;
    }
    public void setcontestId(String contestId) {
    	this.contestId = contestId;
    }    
}
