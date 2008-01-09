/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;

/**
 * <p>
 * ShowStatisticsAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ShowStatisticsAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowStatisticsAction() {
        // empty
    }

    /**
     * ShowStatisticsAction.
     *
     * @param mapping action mapping
     * @param form action form
     * @param request http sevelet request
     * @param response http sevelet response
     *
     * @return action forward instance
     *
     * @throws Exception any errors happened
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
    	
    	// check contest
    	boolean isProblemset = context.getRequest().getRequestURI().endsWith("showStatistics.do");
    	
    	ActionForward forward = checkContestViewPermission(mapping, context, isProblemset, true);
    	if (forward != null) {
    		return forward;
    	}
    	AbstractContest contest = context.getContest();
    	
    	ContestStatistics statistics = StatisticsManager.getInstance().getContestStatistics(contest.getId());    	    	
        context.setAttribute("ContestStatistics", statistics);
                
        return handleSuccess(mapping, context, "success");
                  	    	   
    }         

}
    