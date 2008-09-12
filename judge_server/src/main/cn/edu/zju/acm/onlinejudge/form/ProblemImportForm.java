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

import org.apache.struts.action.ActionForm;
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
