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

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>
 * ContestForm.
 * </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class ContestForm extends ActionForm implements Serializable {
    /**
     * The id.
     */
    private String id = null;

    /**
     * The name.
     */
    private String name = null;

    /**
     * The Description.
     */
    private String description = null;

    /**
     * The startTime.
     */
    private String startTime = null;

    /**
     * The contestLength.
     */
    private String contestLength = null;

    /**
     * The forumId.
     */
    private String forumId = null;

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
     * The useGlobalDefault.
     */
    private boolean useGlobalDefault = false;

    /**
     * The problemset.
     */
    private boolean problemset = false;
    
    /**
     * The checkIp.
     */
    private boolean checkIp = false;
    
    /**
     * The languageIds.
     */
    private String[] languageIds = null;

    /**
     * Empty constructor.
     */
    public ContestForm() {
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
     * Sets the name.
     *
     * @prama name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Description.
     *
     * @prama description the Description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Description.
     *
     * @return the Description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the startTime.
     *
     * @prama startTime the startTime to set.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the startTime.
     *
     * @return the startTime.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the contestLength.
     *
     * @prama contestLength the contestLength to set.
     */
    public void setContestLength(String contestLength) {
        this.contestLength = contestLength;
    }

    /**
     * Gets the contestLength.
     *
     * @return the contestLength.
     */
    public String getContestLength() {
        return contestLength;
    }

    /**
     * Sets the forumId.
     *
     * @prama forumId the forumId to set.
     */
    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    /**
     * Gets the forumId.
     *
     * @return the forumId.
     */
    public String getForumId() {
        return forumId;
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
     * Sets the useGlobalDefault.
     *
     * @prama useGlobalDefault the useGlobalDefault to set.
     */
    public void setUseGlobalDefault(boolean useGlobalDefault) {
        this.useGlobalDefault = useGlobalDefault;
    }

    /**
     * Gets the useGlobalDefault.
     *
     * @return the useGlobalDefault.
     */
    public boolean isUseGlobalDefault() {
        return useGlobalDefault;
    }
    
    /**
     * Sets the problemset.
     *
     * @prama problemset the problemset to set.
     */
    public void setProblemset(boolean problemset) {
        this.problemset = problemset;
    }

    /**
     * Gets the problemset.
     *
     * @return the problemset.
     */
    public boolean isProblemset() {
        return problemset;
    }
    
    public boolean isCheckIp() {
        return checkIp;
    }

    public void setCheckIp(boolean checkIp) {
        this.checkIp = checkIp;
    }

    
    /**
     * Sets the languageIds.
     *
     * @prama languageIds the languageIds to set.
     */
    public void setLanguageIds(String[] languageIds) {
        this.languageIds = languageIds;
    }

    /**
     * Gets the languageIds.
     *
     * @return the languageIds.
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
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        
        if (id == null) {
            return errors;
        }
        
        checkInteger(id, 0, Integer.MAX_VALUE, "id", errors);
        
        if ((name == null) || (name.trim().length() == 0)) {
            errors.add("name", new ActionMessage("ContestForm.name.required"));
        }
        
        
        
        if (description == null) {
            description = "";            
        }
        
        if ((startTime == null) || (startTime.trim().length() == 0)) {
            if (!problemset || ((contestLength != null) && (contestLength.trim().length() > 0))) {
                errors.add("startTime", new ActionMessage("ContestForm.startTime.required"));
            }
        } else if (!Utility.validateTimestamp(startTime)) {
            errors.add("startTime", new ActionMessage("ContestForm.startTime.invalid"));
        }
        
        if ((contestLength == null) || (contestLength.trim().length() == 0)) {
            if (!problemset || ((startTime != null) && (startTime.trim().length() > 0))) {
                errors.add("contestLength", new ActionMessage("ContestForm.contestLength.required"));
            }
        } else if (!Utility.validateTime(contestLength)) {
            errors.add("contestLength", new ActionMessage("ContestForm.contestLength.invalid"));
        }
        
        
        checkInteger(forumId, 0, Integer.MAX_VALUE, "forumId", errors);
        
        if (!useGlobalDefault) {
            checkInteger(timeLimit, 0, 3600, "timeLimit", errors);
            checkInteger(memoryLimit, 0, 1024 * 1024, "memoryLimit", errors);
            checkInteger(outputLimit, 0, 100 * 1024, "outputLimit", errors);
            checkInteger(submissionLimit, 0, 10 * 1024, "submissionLimit", errors);
        }
        if (languageIds != null) {
            for (int i = 0; i < languageIds.length; ++i) {
                checkInteger(languageIds[i], 0, Integer.MAX_VALUE, "languageIds", errors);
            }
        }
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
            errors.add(name, new ActionMessage("ContestForm." + name + ".required"));
            return;
        }        
        try {
            int intValue = Integer.parseInt(value);
            if (intValue < min || intValue > max) {
                errors.add(name, new ActionMessage("ContestForm." + name + ".outrange"));
            }
        } catch (NumberFormatException e) {
            errors.add(name, new ActionMessage("ContestForm." + name + ".invalid"));            
        }
    }

    public void populate(AbstractContest contest) {
        this.id = String.valueOf(contest.getId());
        this.name = contest.getTitle();
        this.description = contest.getDescription();
        this.forumId = String.valueOf(contest.getForumId());
        if (contest instanceof Contest) {
            this.problemset = false;
        } else {
            this.problemset = true;
        }
        if (contest.getStartTime() != null) {
            this.startTime = Utility.toTimestamp(contest.getStartTime());
            this.contestLength = contest.getHours() + ":" 
                + contest.getMinutes() + ":"
                + contest.getSeconds();
        } else {
            this.startTime = "";
            this.contestLength = "";
        }
        this.checkIp = contest.isCheckIp();
        
        Limit limit = contest.getLimit();
        this.useGlobalDefault = (limit.getId() == Limit.DEFAULT_LIMIT_ID);
        this.timeLimit = String.valueOf(limit.getTimeLimit());
        this.memoryLimit = String.valueOf(limit.getMemoryLimit());
        this.submissionLimit = String.valueOf(limit.getSubmissionLimit());
        this.outputLimit = String.valueOf(limit.getOutputLimit());
        
        this.languageIds = new String[contest.getLanguages().size()];
        for (int i = 0; i < this.languageIds.length; ++i) {
            this.languageIds[i] = String.valueOf((((Language) contest.getLanguages().get(i)).getId()));
        }
    }
    
    public AbstractContest toContest() throws ParseException, NumberFormatException, PersistenceException {
        AbstractContest contest = null;
        if (problemset) {
            contest = new Problemset();            
        } else {
            contest = new Contest();            
        }
        if (startTime != null && startTime.trim().length() > 0) {
            contest.setStartTime(Utility.parseTimestamp(startTime));
            contest.setEndTime(
                    new Date(Utility.parseTimestamp(startTime).getTime() + Utility.parseTime(contestLength) * 1000));
        }
        
        
        try {
            if (id != null) {
                contest.setId(Long.parseLong(id));
            }
        } catch (NumberFormatException e) {
        }
        
        contest.setTitle(name);
        contest.setDescription(description);
        contest.setCheckIp(checkIp);
        Limit limit = new Limit();
        if (useGlobalDefault) {
            limit.setId(1);
        } else {
            limit.setTimeLimit(Integer.parseInt(timeLimit));
            limit.setMemoryLimit(Integer.parseInt(memoryLimit));
            limit.setSubmissionLimit(Integer.parseInt(submissionLimit));
            limit.setOutputLimit(Integer.parseInt(outputLimit));           
        }
        contest.setLimit(limit);
        contest.setForumId(Long.parseLong(forumId));

        List<Language> languages = new ArrayList<Language>();
        ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
        if (languageIds != null) {
            for (int i = 0; i < languageIds.length; ++i) {
                Language language = contestPersistence.getLanguage(Long.parseLong(languageIds[i]));
                if (language != null) {
                    languages.add(language);
                }
            }
        }
        contest.setLanguages(languages);
        
        return contest;
    }


}
