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

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.UserPreference;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.UserStatistics;
import cn.edu.zju.acm.onlinejudge.util.Utility;

public class ShowUserStatusAction extends BaseAction {

    private static long defaultProblemSetId = ConfigManager.getDefaultProblemSetId();

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

        UserProfile user = null;
        String handle = context.getRequest().getParameter("handle");
        if (handle != null && handle.length() > 0) {
            // TODO cache?
            user = PersistenceManager.getInstance().getUserPersistence().getUserProfileByHandle(handle);
        } else if (context.getRequest().getParameter("userId") != null) {
            long userId = Utility.parseLong(context.getRequest().getParameter("userId"));
            if (userId != -1) {
                user = PersistenceManager.getInstance().getUserPersistence().getUserProfile(userId);
            }
        } else {
            user = context.getUserProfile();
        }
        AbstractContest contest = null;
        if (user != null) {
            long contestId = Utility.parseLong(context.getRequest().getParameter("contestId"));
            if (contestId == -1) {
                contestId = ShowUserStatusAction.defaultProblemSetId;
            }
            contest = ContestManager.getInstance().getContest(contestId);
        }
        if (contest != null) {
            context.setAttribute("contest", contest);
            ActionForward forward = this.checkContestViewPermission(mapping, context, null, true);
            if (forward != null) {
                contest = null;
            }
        }

        UserStatistics statistics = null;
        UserPreference pref = null;
        if (contest != null && user != null) {
        	// TODO cache?
        	pref = PersistenceManager.getInstance().getUserPersistence().getUserPreference(user.getId());
            statistics = StatisticsManager.getInstance().getUserStatistics(contest.getId(), user.getId());
        }

        context.setAttribute("user", user);
        context.setAttribute("preference", pref);
        context.setAttribute("contest", contest);
        context.setAttribute("UserStatistics", statistics);

        return this.handleSuccess(mapping, context, "success");
    }
}
