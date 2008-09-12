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


import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.request.LogCriteria;
import cn.edu.zju.acm.onlinejudge.form.LogSearchForm;
import cn.edu.zju.acm.onlinejudge.util.AccessLog;
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
    