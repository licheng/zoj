package cn.edu.zju.acm.onlinejudge.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

public class ConfirmRunAction extends BaseAction {

	@Override
	protected ActionForward execute(ActionMapping mapping, ActionForm form,
			ContextAdapter context) throws Exception {
		long submissionId=Long.parseLong(context.getRequest().getParameter("submissionId").toString());
		PersistenceManager.getInstance().getSubmissionPersistence().conformSubmission(1, submissionId);
		return handleSuccess(mapping, context, "success");
	}
}