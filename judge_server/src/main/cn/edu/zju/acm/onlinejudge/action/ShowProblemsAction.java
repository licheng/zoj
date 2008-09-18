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
import java.util.*;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.UserStatistics;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * ShowProblemsAction
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ShowProblemsAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowProblemsAction() {
    // empty
    }

    /**
     * ShowProblemsAction.
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
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("showProblems.do");

        ActionForward forward = this.checkContestViewPermission(mapping, context, isProblemset, true);
        if (forward != null) {
            return forward;
        }
        AbstractContest contest = context.getContest();

        long problemsCount = ContestManager.getInstance().getProblemsCount(contest.getId());

        long pageNumber = Utility.parseLong(context.getRequest().getParameter("pageNumber"));
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        long problemsPerPage = 100;
        if (problemsCount <= (pageNumber - 1) * problemsPerPage) {
            pageNumber = 1;
        }
        long totalPages = 1;
        if (problemsCount > 0) {
            totalPages = (problemsCount - 1) / problemsPerPage + 1;
        }

        List<Problem> oproblems =
                ContestManager.getInstance().getContestProblems(contest.getId(),
                                                                (int) ((pageNumber - 1) * problemsPerPage),
                                                                (int) problemsPerPage);
        List<Problem> problems=new ArrayList<Problem>();
        problems.addAll(oproblems);


        ContestStatistics contestStatistics = null;
        contestStatistics =
                StatisticsManager.getInstance().getContestStatistics(contest.getId(),
                                                                     (int) ((pageNumber - 1) * problemsPerPage),
                                                                     (int) problemsPerPage);
        for(int i=0;i<problems.size();++i)
        {
            Problem p = (Problem)problems.get(i);
            int ac = contestStatistics.getCount(i, 0);
            int total = contestStatistics.getProblemCount(i);
            p.setTotal(total);
            p.setAC(ac);
            if(total==0)
            {
                p.setRatio(0);
            }
            else
            {
                p.setRatio((double)ac/(double)total);
            }
        }
        String order=context.getRequest().getParameter("order");
        if(order!=null)
        {
            if(order.equalsIgnoreCase("ratio"))
            {
				Collections.sort(problems, new Comparator() {
  				public int compare(Object o1, Object o2) {
  					return (((Problem)o2).getRatio() - ((Problem)o1).getRatio())>0? 1 : -1;
  				}}
  				);
  		} else if(order.equalsIgnoreCase("ac"))
            {
				Collections.sort(problems, new Comparator() {
  				public int compare(Object o1, Object o2) {
  					return ((Problem)o2).getAC() - ((Problem)o1).getAC();
  				}}
  				);
  		} else if(order.equalsIgnoreCase("all"))
            {
				Collections.sort(problems, new Comparator() {
  				public int compare(Object o1, Object o2) {
  					return ((Problem)o2).getTotal() - ((Problem)o1).getTotal();
  				}}
  				);
  		}
  	}
  		
        UserStatistics userStatistics = null;
        if (context.getUserProfile() != null && problems.size() > 0) {
            userStatistics =
                    StatisticsManager.getInstance()
                                     .getUserStatistics(contest.getId(), context.getUserProfile().getId());
        }

        context.setAttribute("problems", problems);
        context.setAttribute("pageNumber", (new Long(pageNumber)).toString());
        context.setAttribute("ContestStatistics", contestStatistics);
        context.setAttribute("UserStatistics", userStatistics);
        context.setAttribute("totalPages", new Long(totalPages));
        context.setAttribute("currentPage", new Long(pageNumber));
        if (this.checkContestAdminPermission(mapping, context, isProblemset, true) == null &&
            "true".equalsIgnoreCase(context.getRequest().getParameter("check"))) {
            List<String> checkMessages = new ArrayList<String>();
            for (Object obj : problems) {
                Problem p = (Problem) obj;
                this.checkExists("Text", this.getReferenceLength(p, ReferenceType.DESCRIPTION), "ERROR", checkMessages,
                                 p);
                this.checkExists("Input", this.getReferenceLength(p, ReferenceType.INPUT), "ERROR", checkMessages, p);
                if (p.isChecker()) {
                    this.checkExists("Output", this.getReferenceLength(p, ReferenceType.OUTPUT), "WARNING",
                                     checkMessages, p);
                    // checkExists("Checker", getReferenceLength(p, ReferenceType.CHECKER), "WARNING", checkMessages,
                    // p);
                    this.checkExists("Checker source", this.getReferenceLength(p, ReferenceType.CHECKER_SOURCE),
                                     "ERROR", checkMessages, p);
                } else {
                    this.checkExists("Output", this.getReferenceLength(p, ReferenceType.OUTPUT), "ERROR",
                                     checkMessages, p);
                }
                this.checkExists("Judge solution", this.getReferenceLength(p, ReferenceType.JUDGE_SOLUTION), "WARNING",
                                 checkMessages, p);
            }
            context.setAttribute("CheckMessages", checkMessages);
        }

        return this.handleSuccess(mapping, context, "success");

    }

    private void checkExists(String name, long length, String level, List<String> checkMessages, Problem p) {
        if (length == -1) {
            checkMessages.add(level + "[ " + p.getCode() + "] - " + name + " is missing.");
        } else if (length == 0) {
            checkMessages.add(level + "[ " + p.getCode() + "] - " + name + " is empty.");
        }
    }

    private long getReferenceLength(Problem p, ReferenceType type) throws Exception {
        ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
        List<Reference> refs = referencePersistence.getProblemReferenceInfo(p.getId(), type);

        if (refs.size() == 0) {
            return -1;
        }
        Reference ref = refs.get(0);
        return ref.getSize();
    }

}
