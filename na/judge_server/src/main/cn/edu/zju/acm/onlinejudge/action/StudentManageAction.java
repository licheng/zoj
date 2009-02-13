package cn.edu.zju.acm.onlinejudge.action;

import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

public class StudentManageAction extends BaseAction {

	@Override
	protected ActionForward execute(ActionMapping mapping, ActionForm form,
			ContextAdapter context) throws Exception {
		// TODO Auto-generated method stub
		long contestId = Utility.parseLong(context.getRequest().getParameter("contestId"));
		AbstractContest contest = ContestManager.getInstance().getContest(contestId);
		List students=PersistenceManager.getInstance().getUserPersistence().getStudents(context.getUserProfile().getId());
		context.setAttribute("students", students);
		context.setAttribute("contest", contest);
		context.setAttribute("userProfile", context.getUserProfile());
		return this.handleSuccess(mapping, context, "success");
	}

}
