/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.form.SubmissionSearchForm;
import cn.edu.zju.acm.onlinejudge.judgeserver.JudgeService;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * ShowRunsAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ShowRunsAction extends BaseAction {
    
	private final List judgeReplies;
	
	private final List adminJudgeReplies;
	
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowRunsAction() {
    	judgeReplies = new ArrayList();
    	judgeReplies.add(JudgeReply.ACCEPTED);
    	judgeReplies.add(JudgeReply.PRESENTATION_ERROR);    	
    	judgeReplies.add(JudgeReply.WRONG_ANSWER);
    	judgeReplies.add(JudgeReply.RUNTIME_ERROR);    	
        judgeReplies.add(JudgeReply.FLOATING_POINT_ERROR);
        judgeReplies.add(JudgeReply.SEGMENTATION_FAULT);
    	judgeReplies.add(JudgeReply.TIME_LIMIT_EXCEEDED);
    	judgeReplies.add(JudgeReply.MEMORY_LIMIT_EXCEEDED);
    	judgeReplies.add(JudgeReply.OUTPUT_LIMIT_EXCEEDED);
    	judgeReplies.add(JudgeReply.COMPILATION_ERROR); 
    	
    	adminJudgeReplies = new ArrayList(judgeReplies);
    	adminJudgeReplies.add(JudgeReply.QUEUING);
    	adminJudgeReplies.add(JudgeReply.JUDGE_INTERNAL_ERROR);
    	
    }

    
    /**
     * ShowRunsAction.
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
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("showRuns.do");
        
        ActionForward forward = checkContestViewPermission(mapping, context, isProblemset, true);
        if (forward != null) {
            return forward;
        }
        
    	context.setAttribute("judgeReplies", context.isAdmin() ? adminJudgeReplies : judgeReplies);
    	
    	
        context.setAttribute("totalSubmissions", new Long(0));
    	// check contest
    	boolean isRejudge = "true".equalsIgnoreCase(context.getRequest().getParameter("rejudge"));
        
    	if (isRejudge) {
            checkContestAdminPermission(mapping, context, isProblemset, true);
        } else {
            checkContestViewPermission(mapping, context, isProblemset, true);
        }
    	if (forward != null) {
    		return forward;
    	}    	    	    	
        
    	SubmissionSearchForm serachForm = (SubmissionSearchForm) form;
    	ActionMessages errors = serachForm.check();
    	if (errors.size() > 0) {
    		
    		context.setAttribute("runs", new ArrayList());
            context.setAttribute("pageNumber", new Long(0));
            context.setAttribute("totalPages", new Long(0));
            context.setAttribute("startIndex", new Long(0));
            context.setAttribute("totalSubmissions", new Long(0));
    		return handleFailure(mapping, context, errors);
    	}
    	
    	long pageNumber = Utility.parseLong(serachForm.getPageNumber());
    	if (pageNumber < 1) {
    		pageNumber = Long.MAX_VALUE;    		
    	}
    	int runsPerPage = 20;
    	
    	
    	SubmissionCriteria criteria = serachForm.toSubmissionCriteria();   
        
        if (isRejudge) {
            List allRuns = PersistenceManager.getInstance().getSubmissionPersistence().searchSubmissions(
                    criteria, 0, Integer.MAX_VALUE);
            rejudge(allRuns);            
        }
        
        long runsNumber = StatisticsManager.getInstance().getSubmissionsNumber(criteria);
        if (runsNumber == 0) {
            context.setAttribute("runs", new ArrayList());
            context.setAttribute("pageNumber", new Long(0));
            context.setAttribute("totalPages", new Long(0));
            context.setAttribute("startIndex", new Long(0));
            context.setAttribute("totalSubmissions", new Long(0));
            return handleSuccess(mapping, context, "success");
        } 
        long totalPages = (runsNumber - 1) / runsPerPage + 1;
        if (pageNumber > totalPages) {
        	pageNumber = totalPages;
        }
        long startIndex;
        if (pageNumber == totalPages) {
        	startIndex = runsNumber;
        } else {
        	startIndex = pageNumber * 20;
        }
        List runs = StatisticsManager.getInstance().getSubmissions(criteria, (int) (runsNumber - startIndex), runsPerPage);
        
        
        context.setAttribute("runs", runs);
        context.setAttribute("pageNumber", new Long(pageNumber));
        context.setAttribute("totalPages", new Long(totalPages));
        context.setAttribute("startIndex", new Long(startIndex));
        context.setAttribute("totalSubmissions", new Long(runsNumber));
        

        return handleSuccess(mapping, context, "success");
                  	    	   
    }         

    private void rejudge(List runs) throws Exception {
        for (Object obj : runs) {
            Submission submission = (Submission) obj;
            if(!submission.getJudgeReply().equals(JudgeReply.OUT_OF_CONTEST_TIME))
            {
	            submission.setJudgeReply(JudgeReply.QUEUING);
	            submission.setMemoryConsumption(0);
	            submission.setTimeConsumption(0);
	            PersistenceManager.getInstance().getSubmissionPersistence().updateSubmission(submission, 0);
	            JudgeService.getInstance().rejudge(submission);  
            }
        }
    }
}
    