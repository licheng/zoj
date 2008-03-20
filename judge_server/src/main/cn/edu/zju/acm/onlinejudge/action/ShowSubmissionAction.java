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
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.SubmissionPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;

/**
 * <p>
 * ShowSourceAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ShowSubmissionAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowSubmissionAction() {
        // empty
    }

    /**
     * ShowSourceAction.
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
        
        long id = Utility.parseLong(context.getRequest().getParameter("submissionId"));
        
        Submission submission = ContestManager.getInstance().getSubmission(id);
        if (submission == null) {
            response.sendError(404);
            return null;
        }
        Problem problem = ContestManager.getInstance().getProblem(submission.getProblemId());
        context.setAttribute("problem", problem);
        ActionForward forward = checkProblemAdminPermission(mapping, context, null);
        if (forward != null) {
            response.sendError(404);
            return null;
        }
                
        response.setContentType("text/plain");
        boolean download = "true".equalsIgnoreCase(context.getRequest().getParameter("download"));        
        if (download) { 
            response.setHeader("Content-disposition", 
                    "attachment; filename=" + id + "." + submission.getLanguage().getOptions());
        }
        response.getOutputStream().write(submission.getContent().getBytes());
        
        response.getOutputStream().close();
                
        return null;
                          	    	   
    }                 
}
    