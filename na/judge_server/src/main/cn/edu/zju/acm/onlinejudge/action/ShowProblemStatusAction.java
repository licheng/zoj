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

import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.util.ProblemStatistics;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;

/**
 * MyEclipse Struts Creation date: 07-27-2008
 * 
 * XDoclet definition:
 * 
 * @struts.action validate="true"
 * @struts.action-forward name="success" path="/show_problem_status.jsp"
 */
public class ShowProblemStatusAction extends BaseAction {
    /*
     * Generated Methods
     */

    /**
     * Method execute
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {

        // check contest
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("showProblemStatus.do");

        ActionForward forward = this.checkProblemViewPermission(mapping, context, isProblemset);
        if (forward != null) {
            return forward;
        }

        Problem problem = context.getProblem();
        String orderBy = context.getRequest().getParameter("orderBy");
        if (!"date".equals(orderBy) && !"memory".equals(orderBy)) {
            orderBy = "time";
        }
        if (problem != null) {
            ProblemStatistics statistics =
                    StatisticsManager.getInstance().getProblemStatistics(problem.getId(), orderBy, 20);
            context.setAttribute("ProblemStatistics", statistics);

        }

        return this.handleSuccess(mapping, context, "success");
    }
}