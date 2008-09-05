package cn.edu.zju.acm.onlinejudge.action;

import java.util.Set;

import javax.swing.text.Utilities;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Problemset;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.ProblemStatistics;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.UserStatistics;
import cn.edu.zju.acm.onlinejudge.util.Utility;

public class ShowUserStatusAction extends BaseAction {
	
	private static long defaultProblemSetId = ConfigManager.getDefaultProblemSetId();
	
	/** 
	 * Method execute
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
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
				contestId = defaultProblemSetId;
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
		if (contest != null && user != null) {
			statistics = StatisticsManager.getInstance().getUserStatistics(contest.getId(), user.getId());
		}
		
		context.setAttribute("user", user);
		context.setAttribute("contest", contest);
		context.setAttribute("UserStatistics", statistics);
		
		return handleSuccess(mapping, context, "success");
	}
}
