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
import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessage;

import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;


/**
 * <p>SubmissionSerachForm.</p>
 * 
 * @version 2.0
 * @author ZOJDEV
 */
public class SubmissionSearchForm extends ActionForm implements Serializable {
	
    /**
     * <p>Represents search.</p>
     */
    private boolean search = false;
    
    /**
     * <p>Represents contestId.</p>
     */
    private String contestId;

    /**
     * <p>Represents problemId.</p>
     */
    private String problemCode;

    /**
     * <p>Represents handle.</p>
     */
    private String handle;

    /**
     * <p>Represents idStart.</p>
     */
    private String idStart;

    /**
     * <p>Represents idEnd.</p>
     */
    private String idEnd;

    /**
     * <p>Represents timeStart.</p>
     */
    private String timeStart;

    /**
     * <p>Represents timeEnd.</p>
     */
    private String timeEnd;
    
    /**
     * <p>Represents lastId.</p>
     */
    private String lastId;

    /**
     * <p>Represents firstId.</p>
     */
    private String firstId;
    
    /**
     * <p>Represents judgeReplies.</p>
     */
    private String[] judgeReplyIds;

    /**
     * <p>Represents languages.</p>
     */
    private String[] languageIds;

    /**
     * SubmissionCriteria.
     */
    public SubmissionSearchForm() {    	
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
    
    /**
     * @param problemId The problemId to set.
     */
    public void setProblemCode(String problemCode) {
        this.problemCode = problemCode;
    }

    /**
     * @return Returns the problemId.
     */
    public String getProblemCode() {
        return problemCode;
    }

    /**
     * @param contestId The contestId to set.
     */
    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    /**
     * @return Returns the contestId.
     */
    public String getContestId() {
        return contestId;
    }

    /**
     * @param handle The handle to set.
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }

    /**
     * @return Returns the handle.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * @param idStart The idStart to set.
     */
    public void setIdStart(String idStart) {
        this.idStart = idStart;
    }

    /**
     * @return Returns the idStart.
     */
    public String getIdStart() {
        return idStart;
    }

    /**
     * @param idEnd The idEnd to set.
     */
    public void setIdEnd(String idEnd) {
        this.idEnd = idEnd;
    }

    /**
     * @return Returns the idEnd.
     */
    public String getIdEnd() {
        return idEnd;
    }

    /**
     * @param timeStart The timeStart to set.
     */
    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    /**
     * @return Returns the timeStart.
     */
    public String getTimeStart() {
        return timeStart;
    }

    /**
     * @param timeEnd The timeEnd to set.
     */
    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    /**
     * @return Returns the timeEnd.
     */
    public String getTimeEnd() {
        return timeEnd;
    }

    /**
     * @param judgeReplyIds The judgeReplyIds to set.
     */
    public void setJudgeReplyIds(String[] judgeReplyIds) {
        this.judgeReplyIds = judgeReplyIds;
    }

    /**
     * @return Returns the judgeReplies.
     */
    public String[] getJudgeReplyIds() {
        return judgeReplyIds;
    }

    /**
     * @param languageIds The languages to set.
     */
    public void setLanguageIds(String[] languageIds) {
        this.languageIds = languageIds;
    }

    /**
     * @return Returns the languages.
     */
    public String[] getLanguageIds() {
        return languageIds;
    }
   
    /**
     * Validates the form.
     *
     * @param mapping the action mapping.
     * @param request the user request.
     *
     * @return collection of validation errors.
     */
    public ActionErrors check() {    	
       
        ActionErrors errors = new ActionErrors();
        
        checkString(errors, problemCode, 8, "problemCode", "SubmissionSearchForm.problemCode.invalid");
        checkString(errors, handle, 32, "handle", "SubmissionSearchForm.handle.invalid");
        checkLong(errors, idStart, "idStart", "SubmissionSearchForm.idStart.invalid");
        checkLong(errors, idEnd, "idEnd", "SubmissionSearchForm.idEnd.invalid");
        checkTime(errors, timeStart, "timeStart", "SubmissionSearchForm.timeStart.invalid");
        checkTime(errors, timeEnd, "timeEnd", "SubmissionSearchForm.timeEnd.invalid");
        checkLongArray(errors, languageIds, "languageIds", "SubmissionSearchForm.languageIds.invalid");
        checkLongArray(errors, judgeReplyIds, "judgeReplyIds", "SubmissionSearchForm.judgeReplyIds.invalid");
                        
        return errors;
    }
    
    private void checkString(ActionErrors errors, String value, int length, String key, String message) {
    	if (value != null && value.trim().length() > 0) {
        	if (value.trim().length() > length) {
        		errors.add(key, new ActionMessage(message));
        	}
        }
    }
    
    private void checkLong(ActionErrors errors, String value, String key, String message) {
    	if (value != null && value.trim().length() > 0) {    		 
    		try {
    			long l = Long.parseLong(value);
    			if (l < 0) {
    				errors.add(key, new ActionMessage(message));
    			}
    		} catch (Exception e) {
    			errors.add(key, new ActionMessage(message));
    		}
    	}
    }
    
    private void checkLongArray(ActionErrors errors, String[] value, String key, String message) {
    	if (value != null && value.length > 0) {    
    		if (value.length > 100) {
    			errors.add(key, new ActionMessage(message));
				return;
    		}
    		for(int i = 0; i < value.length; ++i) {
    			try {
	    			long l = Long.parseLong(value[i]);
	    			if (l < 0) {
	    				errors.add(key, new ActionMessage(message));
	    				return;
	    			}
	    		} catch (Exception e) {
	    			errors.add(key, new ActionMessage(message));
	    			return;
	    		}
    	}
    	}
    }
    
    private void checkTime(ActionErrors errors, String value, String key, String message) {
    	if (value != null && value.trim().length() > 0) {
	    	if (!Utility.validateTimestamp(value)) {
	            errors.add(key, new ActionMessage(message));
	        }
    	}
    }
    
    
    public SubmissionCriteria toSubmissionCriteria() throws ParseException, NumberFormatException, PersistenceException {
    	
    	SubmissionCriteria criteria = new SubmissionCriteria();
    	if (contestId != null && contestId.trim().length() > 0) {
    		criteria.setContestId(Long.valueOf(contestId.trim()));
    	}
    	if (problemCode != null && problemCode.trim().length() > 0) {
    		criteria.setProblemCode(problemCode);
    	}
    	if (handle != null && handle.trim().length() > 0) {
    		criteria.setHandle(handle);
    	}
    	if (idStart != null && idStart.trim().length() > 0) {
    		criteria.setIdStart(Long.valueOf(idStart.trim()));
    	}
    	if (idEnd != null && idEnd.trim().length() > 0) {
    		criteria.setIdEnd(Long.valueOf(idEnd.trim()));
    	}
    	if (timeStart != null && timeStart.trim().length() > 0) {
    		criteria.setTimeStart(Utility.parseTimestamp(timeStart));
    	}
    	if (timeEnd != null && timeEnd.trim().length() > 0) {
    		criteria.setTimeEnd(Utility.parseTimestamp(timeEnd));
    	}
    	if (languageIds != null && languageIds.length > 0) {
    		ContestPersistence persistence = PersistenceManager.getInstance().getContestPersistence();
    		List<Language> languages = new ArrayList<Language>();
    		for (int i = 0; i < languageIds.length; ++i) {
    			languages.add(persistence.getLanguage(Long.parseLong(languageIds[i])));
    		}
    		criteria.setLanguages(languages);
    	}
    	if (judgeReplyIds != null && judgeReplyIds.length > 0) {
    		List<JudgeReply> judgeReplies = new ArrayList<JudgeReply>();
    		for (int i = 0; i < judgeReplyIds.length; ++i) {
    			judgeReplies.add(JudgeReply.findById(Long.parseLong(judgeReplyIds[i])));
    		}
    		criteria.setJudgeReplies(judgeReplies);
    	}    	    	    	
    	return criteria;
    }

	public String getLastId() {
		return lastId;
	}

	public void setLastId(String lastId) {
		this.lastId = lastId;
	}

	public String getFirstId() {
		return firstId;
	}

	public void setFirstId(String firstId) {
		this.firstId = firstId;
	}
    
    
}
