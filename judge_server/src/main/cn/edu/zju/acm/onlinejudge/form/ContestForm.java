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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Course;
import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.persistence.LanguagePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * ContestForm.
 * </p>
 * 
 * @author Zhang, Zheng
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
    private int contestType = 0;

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
        return this.id;
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
        return this.name;
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
        return this.description;
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
        return this.startTime;
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
        return this.contestLength;
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
        return this.forumId;
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
        return this.timeLimit;
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
        return this.memoryLimit;
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
        return this.outputLimit;
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
        return this.submissionLimit;
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
        return this.useGlobalDefault;
    }

    /**
     * Sets the problemset.
     * 
     * @prama problemset the problemset to set.
     */
    public void setContestType(int contestType) {
        this.contestType = contestType;
    }

    /**
     * Gets the problemset.
     * 
     * @return the problemset.
     */
    public int getContestType() {
        return this.contestType;
    }

    public boolean isCheckIp() {
        return this.checkIp;
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
        return this.languageIds;
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

        if (this.id == null) {
            return errors;
        }

        this.checkInteger(this.id, 0, Integer.MAX_VALUE, "id", errors);

        if (this.name == null || this.name.trim().length() == 0) {
            errors.add("name", new ActionMessage("ContestForm.name.required"));
        }

        if (this.description == null) {
            this.description = "";
        }

        if (this.startTime == null || this.startTime.trim().length() == 0) {
            if (this.contestType == 0 || this.contestLength != null && this.contestLength.trim().length() > 0) {
                errors.add("startTime", new ActionMessage("ContestForm.startTime.required"));
            }
        } else if (!Utility.validateTimestamp(this.startTime)) {
            errors.add("startTime", new ActionMessage("ContestForm.startTime.invalid"));
        }

        if (this.contestLength == null || this.contestLength.trim().length() == 0) {
            if (this.contestType == 0 || this.startTime != null && this.startTime.trim().length() > 0) {
                errors.add("contestLength", new ActionMessage("ContestForm.contestLength.required"));
            }
        } else if (!Utility.validateTime(this.contestLength)) {
            errors.add("contestLength", new ActionMessage("ContestForm.contestLength.invalid"));
        }

        this.checkInteger(this.forumId, 0, Integer.MAX_VALUE, "forumId", errors);

        if (!this.useGlobalDefault) {
            this.checkInteger(this.timeLimit, 0, 3600, "timeLimit", errors);
            this.checkInteger(this.memoryLimit, 0, 1024 * 1024, "memoryLimit", errors);
            this.checkInteger(this.outputLimit, 0, 100 * 1024, "outputLimit", errors);
            this.checkInteger(this.submissionLimit, 0, 10 * 1024, "submissionLimit", errors);
        }
        if (this.languageIds != null) {
            for (int i = 0; i < this.languageIds.length; ++i) {
                this.checkInteger(this.languageIds[i], 0, Integer.MAX_VALUE, "languageIds", errors);
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
        if (value == null || value.trim().length() == 0) {
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
            this.contestType = 0;
        } else if (contest instanceof Problemset) {
            this.contestType = 1;
        } else {
            this.contestType = 2;
        }
        if (contest.getStartTime() != null) {
            this.startTime = Utility.toTimestamp(contest.getStartTime());
            this.contestLength = contest.getHours() + ":" + contest.getMinutes() + ":" + contest.getSeconds();
        } else {
            this.startTime = "";
            this.contestLength = "";
        }
        this.checkIp = contest.isCheckIp();

        Limit limit = contest.getLimit();
        this.useGlobalDefault = limit.getId() == Limit.DEFAULT_LIMIT_ID;
        this.timeLimit = String.valueOf(limit.getTimeLimit());
        this.memoryLimit = String.valueOf(limit.getMemoryLimit());
        this.submissionLimit = String.valueOf(limit.getSubmissionLimit());
        this.outputLimit = String.valueOf(limit.getOutputLimit());

        this.languageIds = new String[contest.getLanguages().size()];
        for (int i = 0; i < this.languageIds.length; ++i) {
            this.languageIds[i] = String.valueOf(contest.getLanguages().get(i).getId());
        }
    }

    public AbstractContest toContest() throws ParseException, NumberFormatException, PersistenceException {
        AbstractContest contest = null;
        if (this.contestType == 1) {
            contest = new Problemset();
        } else if (this.contestType == 0) {
            contest = new Contest();
        } else {
        	contest = new Course();
        }
        if (this.startTime != null && this.startTime.trim().length() > 0) {
            contest.setStartTime(Utility.parseTimestamp(this.startTime));
            contest.setEndTime(new Date(Utility.parseTimestamp(this.startTime).getTime() +
                Utility.parseTime(this.contestLength) * 1000));
        }

        try {
            if (this.id != null) {
                contest.setId(Long.parseLong(this.id));
            }
        } catch (NumberFormatException e) {}

        contest.setTitle(this.name);
        contest.setDescription(this.description);
        contest.setCheckIp(this.checkIp);
        Limit limit = new Limit();
        if (this.useGlobalDefault) {
            limit.setId(1);
        } else {
            limit.setTimeLimit(Integer.parseInt(this.timeLimit));
            limit.setMemoryLimit(Integer.parseInt(this.memoryLimit));
            limit.setSubmissionLimit(Integer.parseInt(this.submissionLimit));
            limit.setOutputLimit(Integer.parseInt(this.outputLimit));
        }
        contest.setLimit(limit);
        contest.setForumId(Long.parseLong(this.forumId));

        List<Language> languages = new ArrayList<Language>();
        LanguagePersistence languagePersistence = PersistenceManager.getInstance().getLanguagePersistence();
        if (this.languageIds != null) {
            for (int i = 0; i < this.languageIds.length; ++i) {
                Language language = languagePersistence.getLanguage(Long.parseLong(this.languageIds[i]));
                if (language != null) {
                    languages.add(language);
                }
            }
        }
        contest.setLanguages(languages);

        return contest;
    }

}
