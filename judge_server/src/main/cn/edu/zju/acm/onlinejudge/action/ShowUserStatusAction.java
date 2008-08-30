package cn.edu.zju.acm.onlinejudge.action;

import java.util.Set;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.ProblemStatistics;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;

public class ShowUserStatusAction extends BaseAction {
	private static long defaultContestId=ConfigManager.getDefaultContest();
	private static AbstractContest contest=null;
	/** 
	 * Method execute
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
		long userId=Long.parseLong(context.getRequest().getParameter("userId"));
		UserProfile user=PersistenceManager.getInstance().getUserPersistence().getUserProfile(userId);
		Set solved = StatisticsManager.getInstance().getSolvedProblems(
				defaultContestId, userId);
		
		RankListEntry re=PersistenceManager.getInstance().getSubmissionPersistence().getRankListEntry(defaultContestId, userId);
		if(contest==null)
		{
			contest=PersistenceManager.getInstance().getContestPersistence().getContest(defaultContestId);
		}
		context.setAttribute("solved", solved);
		context.setAttribute("user", user);
		context.setAttribute("RankListEntry", re);
	
		return handleSuccess(mapping, context, "success");
	}
}
