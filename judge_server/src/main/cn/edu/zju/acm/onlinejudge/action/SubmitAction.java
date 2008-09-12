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

package cn.edu.zju.acm.onlinejudge.action;

import java.util.Date;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.judgeservice.JudgeService;
import cn.edu.zju.acm.onlinejudge.judgeservice.Priority;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
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
     * @param request http servlet request
     * @param response http servlet response
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
        if (contest.isCheckIp()) {
            forward = checkLastLoginIP(mapping, context, isProblemset);
            if (forward != null) {
                return forward;
            }           
        }
        UserProfile user = context.getUserProfile();
        Submission submission = new Submission();
        submission.setContestId(contest.getId());
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
            submissionPersistence.createSubmission(submission, user.getId());
        } else if (source.getBytes().length > problem.getLimit().getSubmissionLimit() * 1024) {
        	submission.setContent(source.substring(0, problem.getLimit().getSubmissionLimit() * 1024));
            submission.setJudgeReply(JudgeReply.SUBMISSION_LIMIT_EXCEEDED);
            submissionPersistence.createSubmission(submission, user.getId());
        } else {
        	submission.setJudgeReply(JudgeReply.QUEUING);
            submissionPersistence.createSubmission(submission, user.getId());
            JudgeService.getInstance().judge(submission, Priority.NORMAL);
        }
        context.setAttribute("submissionId", submission.getId());
        return handleSuccess(mapping, context, "success");
                  	    	   
    }       
}
    