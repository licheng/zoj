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
import cn.edu.zju.acm.onlinejudge.util.ContestManager;

/**
 * <p>
 * ShowProblemAction
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ShowProblemAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowProblemAction() {
    // empty
    }

    /**
     * ShowProblemAction.
     * 
     * @param mapping
     *            action mapping
     * @param form
     *            action form
     * @param request
     *            http servlet request
     * @param response
     *            http servlet response
     * 
     * @return action forward instance
     * 
     * @throws Exception
     *             any errors happened
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {

        // check contest
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("showProblem.do");

        ActionForward forward = this.checkProblemViewPermission(mapping, context, isProblemset);
        if (forward != null) {
            return forward;
        }

        Problem problem = context.getProblem();
        byte[] text = ContestManager.getInstance().getDescription(problem.getId());

        // StringBuffer sb = new StringBuffer();
        context.setAttribute("text", text);

        return this.handleSuccess(mapping, context, "success");

    }

}
