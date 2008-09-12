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

import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;

/**
 * <p>
 * Show Contests Action.
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ShowContestsAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowContestsAction() {
    // empty
    }

    /**
     * Show Contests.
     * 
     * <pre>
     * </pre>
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

        boolean isProblemset = context.getRequest().getRequestURI().endsWith("showProblemsets.do");

        List<AbstractContest> contests = null;
        if (isProblemset) {
            contests = context.getAllProblemSets();
        } else {
            contests = context.getAllContests();
        }

        context.setAttribute("contests", contests);
        context.setAttribute("totalPages", new Long(1)); // TODO
        context.setAttribute("currentPage", new Long(1)); // TODO

        return this.handleSuccess(mapping, context, "success");
    }

}
