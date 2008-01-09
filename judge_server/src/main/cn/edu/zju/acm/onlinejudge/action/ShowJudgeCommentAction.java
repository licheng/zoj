/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;

/**
 * <p>
 * ShowReferenceAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ShowJudgeCommentAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowJudgeCommentAction() {
        // empty
    }

    /**
     * ShowRankListAction.
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
        HttpServletResponse response = context.getResponse();
        
        UserProfile user = context.getUserProfile();
        if (user == null) {
            response.sendError(404);
            return null;
        }        
        
    	long id = Utility.parseLong(context.getRequest().getParameter("submissionId"));
        Submission submission = null;
        if (id > 0) {
            submission = PersistenceManager.getInstance().getSubmissionPersistence().getSubmission(id);
        }
        if (submission == null) {
            response.sendError(404);
            return null;
        }
        if (!context.isAdmin() 
                && (submission.getUserProfileId() != user.getId()        
                || !JudgeReply.COMPILATION_ERROR.equals(submission.getJudgeReply()))) {
            response.sendError(404);
            return null;
        }
                                
        response.setContentType("text/plain");   
        response.getOutputStream().write(
                (submission.getContent() == null ? "" : submission.getJudgeComment()).getBytes());                          
        response.getOutputStream().close();
                
        return null;
                          	    	   
    }                 
}
    