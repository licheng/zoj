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

import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * ShowReferenceAction
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ShowReferenceAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowReferenceAction() {
    // empty
    }

    /**
     * ShowRankListAction.
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
	 ActionForward forward = this.checkContestAdminPermission(mapping, context, null, false);
        if (forward != null) {
            return forward;
        }
        HttpServletResponse response = context.getResponse();
        if (context.getUserSecurity()==context.getDefaultUserSecurity()) {
            response.sendError(404);
            return null;
        }
        try {
	        if (!context.getUserSecurity().canAdminContest(context.getContest().getId())) {
	            response.sendError(404);
	            return null;
	        }
        } catch (Exception e) {
        	
        }

        long id = Utility.parseLong(context.getRequest().getParameter("referenceId"));
        String problemCode = context.getRequest().getParameter("code");
        boolean download = "true".equalsIgnoreCase(context.getRequest().getParameter("download"));

        ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
        Reference ref = referencePersistence.getReference(id);

        if (ref == null) {
            response.sendError(404);
            return null;
        }

        response.setContentType("text/plain");
        if (download) {

            response.setHeader("Content-disposition", "attachment; filename=" + problemCode + "_" +
                ref.getReferenceType().getDescription() + ".txt");
            response.getOutputStream().write(ref.getContent());
        } else {
            int length = ref.getContent().length;
            if (length > 100 * 1024) {
                response.getOutputStream().write(ref.getContent(), 0, 100 * 1024);
                response.getOutputStream().write("\n\n...\n".getBytes());
            } else {
                response.getOutputStream().write(ref.getContent());
            }
        }

        response.getOutputStream().close();

        return null;

    }
}
