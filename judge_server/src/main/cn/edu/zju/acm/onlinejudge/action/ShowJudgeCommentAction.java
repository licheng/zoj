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

import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * ShowReferenceAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ShowJudgeCommentAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowJudgeCommentAction() {
        // empty
    }

    /**
     * ShowRankListAction.
     *
     * @param mapping action mapping
     * @param form action form
     * @param request http servlet request
     * @param response http servlet response
     *
     * @return action forward instance
     *
     * @throws Exception any errors happened
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
        HttpServletResponse response = context.getResponse();
        
        UserProfile user = context.getUserProfile();
        if (user == null) {
            response.sendError(404);
            return null;
        }        
        
    	long id = Utility.parseLong(context.getRequest().getParameter("submissionId"));
        Submission submission = null;
        if (id > 0) {
            submission = PersistenceManager.getInstance().getSubmissionPersistence().getSubmission(id);
        }
        if (submission == null) {
            response.sendError(404);
            return null;
        }
        if (!context.isAdmin() 
                && (submission.getUserProfileId() != user.getId()        
                || !JudgeReply.COMPILATION_ERROR.equals(submission.getJudgeReply()))) {
            response.sendError(404);
            return null;
        }
                                
        response.setContentType("text/plain");   
        response.getOutputStream().write(
                (submission.getJudgeComment() == null ? "" : submission.getJudgeComment()).getBytes());                          
        response.getOutputStream().close();
                
        return null;
                          	    	   
    }                 
}
    