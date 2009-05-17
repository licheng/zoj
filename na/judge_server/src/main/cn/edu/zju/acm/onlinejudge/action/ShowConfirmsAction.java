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
import java.util.Collections;
import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.form.SubmissionSearchForm;
import cn.edu.zju.acm.onlinejudge.judgeservice.JudgeService;
import cn.edu.zju.acm.onlinejudge.judgeservice.Priority;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * ShowRunsAction
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ShowConfirmsAction extends BaseAction {

	private final List<JudgeReply> judgeReplies;
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowConfirmsAction() {
    	this.judgeReplies = new ArrayList<JudgeReply>();
        this.judgeReplies.add(JudgeReply.ACCEPTED);
    }

    /**
     * ShowRunsAction.
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
  
        context.setAttribute("judgeReplies", this.judgeReplies);

        SubmissionSearchForm serachForm = (SubmissionSearchForm) form;
        ActionMessages errors = serachForm.check();
        if (errors.size() > 0) {

            // TODO
            context.setAttribute("runs", new ArrayList<Submission>());
            return this.handleFailure(mapping, context, errors);
        }

        long lastId = Utility.parseLong(serachForm.getLastId());
        long firstId = -1;
        if (lastId < 0) {
            lastId = Long.MAX_VALUE;
            firstId = Utility.parseLong(serachForm.getFirstId());
        }

        int RUNS_PER_PAGE = 15;
        List<Submission> runs =
        	PersistenceManager.getInstance().getSubmissionPersistence().getUnConfirmSubmissions(context.getContest().getId(), firstId, lastId, RUNS_PER_PAGE + 1);

        long newLastId = -1;
        long newFirstId = -1;
        long nextId = -1;
        long startId = -1;
        if (runs.size() > 0) {
            startId = runs.get(0).getContestOrder();
        }
        if (runs.size() > RUNS_PER_PAGE) {
            nextId = runs.get(runs.size() - 2).getContestOrder();
            runs = runs.subList(0, runs.size() - 1);
        }
        if (firstId > -1) {
            runs = new ArrayList<Submission>(runs);
            Collections.reverse(runs);
        }

        if (runs.size() > 0) {
            if (lastId == Long.MAX_VALUE && firstId == -1) {
                newLastId = nextId;
            } else if (firstId == -1) {
                newLastId = nextId;
                newFirstId = startId;
            } else {
                newFirstId = nextId;
                newLastId = startId;
            }
        }
        context.setAttribute("runs", runs);
        if (newFirstId > -1) {
            context.setAttribute("firstId", newFirstId);
        }
        if (newLastId > -1) {
            context.setAttribute("lastId", newLastId);
        }

        return this.handleSuccess(mapping, context, "success");

    }
}
