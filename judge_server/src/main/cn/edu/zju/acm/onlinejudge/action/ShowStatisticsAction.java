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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
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
     * @param request http servlet request
     * @param response http servlet response
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
    