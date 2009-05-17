package cn.edu.zju.acm.onlinejudge.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

public class DeleteAllStudentAction extends BaseAction  {

	@Override
	protected ActionForward execute(ActionMapping mapping, ActionForm form,
			ContextAdapter context) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("update user_profile set active=0 where create_user="+2);
		PersistenceManager.getInstance().getUserPersistence().deleteAllUserProfile(context.getUserProfile().getId());
		System.out.println("ok");
		return handleSuccess(mapping, context, "success");
	}
}