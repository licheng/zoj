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

import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.form.ContestForm;
import cn.edu.zju.acm.onlinejudge.form.ContestRefImportForm;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;

/**
 * <p>
 * Edit Contest Action.
 * </p>
 * 
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class EditContestAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public EditContestAction() {
    // empty
    }

    /**
     * Edit Contest.
     * 
     * <pre>
     * </pre>
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
    	if(context.getRequest().getRequestURI().endsWith("editCourseTAInfo.do")) {
    		ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
    		ContestRefImportForm contestForm = (ContestRefImportForm) form;
    		if(contestForm.getTAname()!=null) {
    			List<Reference> reflist = referencePersistence.getContestReferences(Long.parseLong(contestForm.getContestId()), ReferenceType.TAName);
    			if(reflist.size()>0) {
    				Reference ref = reflist.get(0);
    				ref.setContent(contestForm.getTAname().getBytes());
    				referencePersistence.updateReference(ref, 1);
    			} else {
    				Reference ref = new Reference();
    				ref.setCompressed(false);
    				ref.setContent(contestForm.getTAname().getBytes());
    				ref.setContentType("string");
    				ref.setName("TA name");
    				ref.setReferenceType(ReferenceType.TAName);
    				ref.setSize(contestForm.getTAname().getBytes().length);
    				referencePersistence.createContestReference(Long.parseLong(contestForm.getContestId()), ref, 1);
    			}
    		}
    		if(contestForm.getTAphone()!=null) {
    			List<Reference> reflist = referencePersistence.getContestReferences(Long.parseLong(contestForm.getContestId()), ReferenceType.TAPhone);
    			if(reflist.size()>0) {
    				Reference ref = reflist.get(0);
    				ref.setContent(contestForm.getTAphone().getBytes());
    				referencePersistence.updateReference(ref, 1);
    			} else {
    				Reference ref = new Reference();
    				ref.setCompressed(false);
    				ref.setContent(contestForm.getTAphone().getBytes());
    				ref.setContentType("string");
    				ref.setName("TA phone");
    				ref.setReferenceType(ReferenceType.TAPhone);
    				ref.setSize(contestForm.getTAphone().getBytes().length);
    				referencePersistence.createContestReference(Long.parseLong(contestForm.getContestId()), ref, 1);
    			}
    		}
    		if(contestForm.getTAemail()!=null) {
    			List<Reference> reflist = referencePersistence.getContestReferences(Long.parseLong(contestForm.getContestId()), ReferenceType.TAEmail);
    			if(reflist.size()>0) {
    				Reference ref = reflist.get(0);
    				ref.setContent(contestForm.getTAemail().getBytes());
    				referencePersistence.updateReference(ref, 1);
    			} else {
    				Reference ref = new Reference();
    				ref.setCompressed(false);
    				ref.setContent(contestForm.getTAemail().getBytes());
    				ref.setContentType("string");
    				ref.setName("TA email");
    				ref.setReferenceType(ReferenceType.TAEmail);
    				ref.setSize(contestForm.getTAemail().getBytes().length);
    				referencePersistence.createContestReference(Long.parseLong(contestForm.getContestId()), ref, 1);
    			}
    		}
    	} else {
    		boolean isProblemset = context.getRequest().getRequestURI().endsWith("editProblemset.do");

            ActionForward forward = this.checkContestAdminPermission(mapping, context, isProblemset, false);
            if (forward != null) {
                return forward;
            }

            ContestForm contestForm = (ContestForm) form;
            
            if (contestForm.getId() == null) {
                AbstractContest contest = context.getContest();
                contestForm.populate(contest);
                return this.handleSuccess(mapping, context);
            } else {
                ContestPersistence persistence = PersistenceManager.getInstance().getContestPersistence();
                AbstractContest contest = contestForm.toContest();
                persistence.updateContest(contest, context.getUserSecurity().getId());
                ContestManager.getInstance().refreshContest(contest.getId());
                ContestRefImportForm contestRefImportForm = new ContestRefImportForm();
                List<Reference> TAname=PersistenceManager.getInstance().getReferencePersistence().getContestReferences(contest.getId(), ReferenceType.TAName);
                List<Reference> TAphone=PersistenceManager.getInstance().getReferencePersistence().getContestReferences(contest.getId(), ReferenceType.TAPhone);
                List<Reference> TAemail=PersistenceManager.getInstance().getReferencePersistence().getContestReferences(contest.getId(), ReferenceType.TAEmail);
                if(TAname.size()>0) {
                	contestRefImportForm.setTAname(new String(TAname.get(0).getContent()));
                }
                if(TAphone.size()>0) {
                	contestRefImportForm.setTAphone(new String(TAphone.get(0).getContent()));
                }
                if(TAemail.size()>0) {
                	contestRefImportForm.setTAemail(new String(TAemail.get(0).getContent()));
                }
                ActionMessages messages = new ActionMessages();
                messages.add("message", new ActionMessage("onlinejudge.editContest.success"));
                this.saveErrors(context.getRequest(), messages);
                context.setAttribute("back", (isProblemset ? "problemsetInfo.do" : "contestInfo.do") + "?contestId=" +
                    contest.getId());

            }
    	}
        return this.handleSuccess(mapping, context, "success");
    }
}
