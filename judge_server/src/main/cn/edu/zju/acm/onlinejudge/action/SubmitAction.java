/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.ExtendedSubmission;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.judgeservice.JudgeService;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * SubmitAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class SubmitAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public SubmitAction() {
        // empty
    }

    /**
     * SubmitAction.
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
    	
    	if (!isLogin(context, true)) {   
    		return handleSuccess(mapping, context, "login");    		    		
    	}

    	boolean isProblemset = context.getRequest().getRequestURI().endsWith("submit.do");
    	
    	ActionForward forward = checkProblemParticipatePermission(mapping, context, isProblemset);
    	if (forward != null) {
    		return forward;
    	}    	    
        
        AbstractContest contest = context.getContest();    	
        Problem problem = context.getProblem();
                
        long languageId = Utility.parseLong(context.getRequest().getParameter("languageId"));
        ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
        Language language = contestPersistence.getLanguage(languageId);        
        if (language == null) {
        	return handleSuccess(mapping, context, "submit");
        }
        String source = context.getRequest().getParameter("source");
        if (source == null || source.length() == 0) {
        	return handleSuccess(mapping, context, "submit");
        }
        
        Long lastSubmitDate = (Long) context.getSessionAttribute("last_submit");
        long now = System.currentTimeMillis();
        long submitInterval = Long.parseLong(ConfigManager.getValue("submit_interval"));
        
        if (lastSubmitDate != null && now - lastSubmitDate < submitInterval) {
        	
        	ActionMessages messages = new ActionMessages();       
            messages.add("message", new ActionMessage("onlinejudge.submit.interval"));
            this.saveErrors(context.getRequest(), messages);
            
            context.setAttribute("source", source);
            
        	return handleSuccess(mapping, context, "submit");
        }
        context.setSessionAttribute("last_submit", now);

        if (contest.isCheckIp()) {
            forward = checkLastLoginIP(mapping, context, isProblemset);
            if (forward != null) {
                return forward;
            }           
        }
        UserProfile user = context.getUserProfile();
        Submission submission = new Submission();
        submission.setLanguage(language);
        submission.setProblemId(problem.getId());
        submission.setUserProfileId(user.getId());        
        submission.setContent(source);        
        submission.setMemoryConsumption(0);
        submission.setTimeConsumption(0);
        submission.setSubmitDate(new Date());
        SubmissionPersistence submissionPersistence = PersistenceManager.getInstance().getSubmissionPersistence();
        
        if (contest.getEndTime() != null && new Date().after(contest.getEndTime())) {
            submission.setJudgeReply(JudgeReply.OUT_OF_CONTEST_TIME);
            submissionPersistence.createSubmission(submission, user.getId(), contest.getId());
        } else if (source.getBytes().length > problem.getLimit().getSubmissionLimit() * 1024) {
            submission.setJudgeReply(JudgeReply.SUBMISSION_LIMIT_EXCEEDED);
            submissionPersistence.createSubmission(submission, user.getId(), contest.getId());
        } else {
            submission.setJudgeReply(JudgeReply.QUEUING);
            submissionPersistence.createSubmission(submission, user.getId(), contest.getId());
            JudgeService.getInstance().judge(submission);
        }
        context.setAttribute("submissionId", submission.getId());
        return handleSuccess(mapping, context, "success");
                  	    	   
    }       
}
    