package cn.edu.zju.acm.onlinejudge.action;

import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

public class DeleteUserAction extends BaseAction  {

	@Override
	protected ActionForward execute(ActionMapping mapping, ActionForm form,
			ContextAdapter context) throws Exception {
		// TODO Auto-generated method stub
		long userid = Long.parseLong(context.getRequest().getParameter("userId"));
		PersistenceManager.getInstance().getUserPersistence().deleteUserProfile(userid, context.getUserProfile().getId());
		return handleSuccess(mapping, context, "success");
	}
}
