/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.util.ArrayList;
import java.util.Collections;
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
import cn.edu.zju.acm.onlinejudge.judgeservice.JudgeService;
import cn.edu.zju.acm.onlinejudge.judgeservice.Priority;
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
    
	private final List<JudgeReply> judgeReplies;
	
	private final List<JudgeReply> adminJudgeReplies;
	
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowRunsAction() {
    	judgeReplies = new ArrayList<JudgeReply>();
    	judgeReplies.add(JudgeReply.ACCEPTED);
    	judgeReplies.add(JudgeReply.PRESENTATION_ERROR);    	
    	judgeReplies.add(JudgeReply.WRONG_ANSWER);
    	judgeReplies.add(JudgeReply.TIME_LIMIT_EXCEEDED);
    	judgeReplies.add(JudgeReply.MEMORY_LIMIT_EXCEEDED);
    	judgeReplies.add(JudgeReply.SEGMENTATION_FAULT);
    	judgeReplies.add(JudgeReply.FLOATING_POINT_ERROR);
    	judgeReplies.add(JudgeReply.COMPILATION_ERROR); 
    	judgeReplies.add(JudgeReply.OUTPUT_LIMIT_EXCEEDED);
    	
    	adminJudgeReplies = new ArrayList<JudgeReply>(judgeReplies);
    	adminJudgeReplies.add(JudgeReply.RUNTIME_ERROR);
    	adminJudgeReplies.add(JudgeReply.QUEUING);
    	adminJudgeReplies.add(JudgeReply.JUDGE_INTERNAL_ERROR);
    }

    
    /**
     * ShowRunsAction.
     *
     * @param mapping action mapping
     * @param form action form
     * @param request http servlet request
     * @param response http servlet response
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
    	
    		// TODO
    		context.setAttribute("runs", new ArrayList<Submission>());
            return handleFailure(mapping, context, errors);
    	}
    	
    	long lastId = Utility.parseLong(serachForm.getLastId());
    	long firstId = -1;
    	if (lastId < 0) {
    		lastId = Long.MAX_VALUE;
    		firstId = Utility.parseLong(serachForm.getFirstId());
    	} 
    	
    	int RUNS_PER_PAGE = 15;

    	SubmissionCriteria criteria = serachForm.toSubmissionCriteria();   
        
        if (isRejudge) {
        	int maxN = 100;
            List allRuns = PersistenceManager.getInstance().getSubmissionPersistence().searchSubmissions(
                    criteria, -1, Long.MAX_VALUE, maxN + 1);
            if (allRuns.size() > maxN) {
            	// TODO
            }
            rejudge(allRuns);
            // TODO
        }
        
        List<Submission> runs = StatisticsManager.getInstance().getSubmissions(criteria, firstId, lastId, RUNS_PER_PAGE + 1);
        
        long newLastId = -1;
        long newFirstId = -1;
        long nextId = -1;
        long startId = -1;
        if (runs.size() > 0) {
        	startId = runs.get(0).getContestOrder();
        }
        if (runs.size() > RUNS_PER_PAGE) {
        	nextId = runs.get(runs.size() - 2).getContestOrder();
        	runs = runs.subList(0, runs.size() - 1);
        }
        if (firstId > -1) {
        	runs = new ArrayList(runs);
    		Collections.reverse(runs);
    	}
        
        if (runs.size() > 0) {
        	if (lastId == Long.MAX_VALUE && firstId == -1) {
        		newLastId = nextId;
        	} else if (firstId == -1) {
        		newLastId = nextId;
        		newFirstId = startId;
        	} else {
        		newFirstId = nextId;
        		newLastId = startId;
        	}
        }
        context.setAttribute("runs", runs);
        if (newFirstId > -1) {
        	context.setAttribute("firstId", newFirstId);
        }
        if (newLastId > -1) {
        	context.setAttribute("lastId", newLastId);
        }
        
        return handleSuccess(mapping, context, "success");
                  	    	   
    }         

    private void rejudge(List<Submission> runs) throws Exception {
        for (Submission submission : runs) {
            if(!submission.getJudgeReply().equals(JudgeReply.OUT_OF_CONTEST_TIME)) {
	            submission.setJudgeReply(JudgeReply.QUEUING);
	            submission.setMemoryConsumption(0);
	            submission.setTimeConsumption(0);
	            PersistenceManager.getInstance().getSubmissionPersistence().updateSubmission(submission, 1);
	            JudgeService.getInstance().judge(submission, Priority.LOW);  
            }
        }
    }
}
    
