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
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.form.ContestForm;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * Register Action.
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class CreateContestAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public CreateContestAction() {
    // empty
    }

    /**
     * Register.
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

        ActionForward forward = this.checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }

        ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
        ContestForm contestForm = (ContestForm) form;
        if (contestForm == null || contestForm.getId() == null) {
            return this.handleSuccess(mapping, context, "failure");
        }

        context.setAttribute("ContestForm", contestForm);

        // create user profile
        AbstractContest contest = contestForm.toContest();

        contestPersistence.createContest(contest, context.getUserProfile().getId());
        ContestManager.getInstance().refreshContest(contest.getId());

        ActionMessages messages = new ActionMessages();
        messages.add("message", new ActionMessage("onlinejudge.createContest.success"));
        this.saveErrors(context.getRequest(), messages);
        context.setAttribute("back", "manageContests.do");

        return this.handleSuccess(mapping, context, "success");
    }

}
