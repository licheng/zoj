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

import cn.edu.zju.acm.onlinejudge.bean.Limit;
import cn.edu.zju.acm.onlinejudge.form.LimitForm;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * <p>
 * Edit Limit Action.
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class EditLimitAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public EditLimitAction() {
    // empty
    }

    /**
     * Edit Role.
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

        // check admin
        ActionForward forward = this.checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }

        LimitForm limitForm = (LimitForm) form;

        if (limitForm.getId() == null || limitForm.getId().trim().length() == 0) {

            Limit limit = PersistenceManager.getInstance().getContestPersistence().getDefaultLimit();
            limitForm.setId("1");
            limitForm.setTimeLimit("" + limit.getTimeLimit());
            limitForm.setMemoryLimit("" + limit.getMemoryLimit());
            limitForm.setSubmissionLimit("" + limit.getSubmissionLimit());
            limitForm.setOutputLimit("" + limit.getOutputLimit());

            return this.handleSuccess(mapping, context, "failure");
        }

        Limit limit = new Limit();
        limit.setId(1);
        limit.setTimeLimit(Integer.parseInt(limitForm.getTimeLimit()));
        limit.setMemoryLimit(Integer.parseInt(limitForm.getMemoryLimit()));
        limit.setOutputLimit(Integer.parseInt(limitForm.getOutputLimit()));
        limit.setSubmissionLimit(Integer.parseInt(limitForm.getSubmissionLimit()));

        PersistenceManager.getInstance().getContestPersistence().updateDefaultLimit(limit);

        ActionMessages messages = new ActionMessages();
        messages.add("success", new ActionMessage("onlinejudge.DefaultLimit.success"));
        this.saveErrors(context.getRequest(), messages);

        return this.handleSuccess(mapping, context, "success");
    }

}
