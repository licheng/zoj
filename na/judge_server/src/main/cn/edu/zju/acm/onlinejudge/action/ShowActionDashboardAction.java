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

import cn.edu.zju.acm.onlinejudge.bean.request.LogCriteria;
import cn.edu.zju.acm.onlinejudge.form.LogSearchForm;
import cn.edu.zju.acm.onlinejudge.util.AccessLog;
import cn.edu.zju.acm.onlinejudge.util.ActionLog;
import cn.edu.zju.acm.onlinejudge.util.PerformanceManager;
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
public class ShowActionDashboardAction extends BaseAction {
    
	
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowActionDashboardAction() {

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
        if (searchForm.getTimeStart() == null) {
        	searchForm.setTimeStart(Utility.toTimestamp(new Date(System.currentTimeMillis() - 24 * 3600 * 1000)));
        }
    	ActionMessages errors = searchForm.check();
    	if (errors.size() > 0) {
    		context.setAttribute("logs", new ArrayList<AccessLog>());
            return handleFailure(mapping, context, errors);
    	}
        
    	LogCriteria criteria = searchForm.toLogCriteria();
        if (criteria.getTimeStart() == null) {
        	criteria.setTimeStart(new Date(System.currentTimeMillis() - 24 * 3600 * 1000));
        }
        List<ActionLog> logs = PerformanceManager.getInstance().getActionDashboard(criteria, searchForm.getOrderBy());
        
        context.setAttribute("parameters", searchForm.toParameterMap());
        context.setAttribute("logs", logs);
                        
        return handleSuccess(mapping, context, "success");
                  	    	   
    }         

}
    