/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.request.LogCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.form.LogSearchForm;
import cn.edu.zju.acm.onlinejudge.form.SubmissionSearchForm;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.AccessLog;
import cn.edu.zju.acm.onlinejudge.util.PerformanceManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * ShowDashboardAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ShowDashboardAction extends BaseAction {
    
	
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowDashboardAction() {

    }

    /**
     * ShowRolesAction.
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
        
        ActionForward forward = checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }
        
        LogSearchForm searchForm = (LogSearchForm) form;
    	ActionMessages errors = searchForm.check();
    	if (errors.size() > 0) {
    		context.setAttribute("logs", new ArrayList<AccessLog>());
            return handleFailure(mapping, context, errors);
    	}
        int page = Utility.parseInt(context.getRequest().getParameter("page"));
        if (page < 0) {
        	page = 1;
        }
        int logsPerPage = 20;
        
        LogCriteria criteria = searchForm.toLogCriteria();
        
        List<AccessLog> logs = PerformanceManager.getInstance().searchLogs(criteria, (page - 1) * 20, logsPerPage, searchForm.getOrderBy());
        
        context.setAttribute("parameters", searchForm.toParameterMap());
        context.setAttribute("page", page);
        context.setAttribute("logsPerPage", logsPerPage);
        context.setAttribute("logs", logs);
                        
        return handleSuccess(mapping, context, "success");
                  	    	   
    }         

}
    