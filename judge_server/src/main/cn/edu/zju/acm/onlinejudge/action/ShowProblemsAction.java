/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * ShowProblemsAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ShowProblemsAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowProblemsAction() {
        // empty
    }

    /**
     * ShowProblemsAction.
     *
     * @param mapping action mapping
     * @param form action form
     * @param request http sevelet request
     * @param response http sevelet response
     *
     * @return action forward instance
     *
     * @throws Exception any errors happened
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
    		
    	// check contest
    	boolean isProblemset = context.getRequest().getRequestURI().endsWith("showProblems.do");
    	
    	ActionForward forward = checkContestViewPermission(mapping, context, isProblemset, true);
    	if (forward != null) {
    		return  forward;
    	}    
        
    	AbstractContest contest = context.getContest();

        
        long problemsCount = ContestManager.getInstance().getProblemsCount(contest.getId());
        
        long pageNumber = Utility.parseLong(context.getRequest().getParameter("pageNumber"));
        if (pageNumber < 1) {
            pageNumber = 1;            
        }
        long problemsPerPage = 100;
    	if (problemsCount <= (pageNumber - 1) * problemsPerPage) {
            pageNumber = 1;
        }
        long totalPages = 1;
        if (problemsCount > 0) {
            totalPages = (problemsCount - 1) / problemsPerPage + 1;
        }
        List problems = ContestManager.getInstance().getContestProblems(
                contest.getId(), (int) ((pageNumber - 1) * problemsPerPage), (int) problemsPerPage);               
        
    	ContestStatistics statistics = StatisticsManager.getInstance().getContestStatistics(
                contest.getId(), (int) ((pageNumber - 1) * problemsPerPage), (int) problemsPerPage);
        
        if (context.getUserProfile() != null && problems.size() > 0) {
        	Set solved = StatisticsManager.getInstance().getSolvedProblems(
        			contest.getId(), context.getUserProfile().getId());
        	context.setAttribute("solved", solved);
        } else {
        	context.setAttribute("solved", new HashSet());
        }
        
	    context.setAttribute("problems", problems);
	    context.setAttribute("ContestStatistics", statistics);
        context.setAttribute("totalPages", new Long(totalPages));
        context.setAttribute("currentPage", new Long(pageNumber));                 
         
        if (context.isAdmin() && "ture".equalsIgnoreCase(context.getRequest().getParameter("check"))) {
            List<String> checkMessages = new ArrayList<String>();
            for (Object obj : problems) {
                Problem p = (Problem) obj;
                checkExists("Text", getReferenceLength(p, ReferenceType.DESCRIPTION), "ERROR", checkMessages, p);
                checkExists("Input", getReferenceLength(p, ReferenceType.INPUT), "ERROR", checkMessages, p);
                if (p.isChecker()) {
                    checkExists("Output", getReferenceLength(p, ReferenceType.OUTPUT), "WARNING", checkMessages, p);
                    //checkExists("Checker", getReferenceLength(p, ReferenceType.CHECKER), "WARNING", checkMessages, p);
                    checkExists("Checker source", getReferenceLength(p, ReferenceType.CHECKER_SOURCE), "ERROR", checkMessages, p);
                } else {
                    checkExists("Output", getReferenceLength(p, ReferenceType.OUTPUT), "ERROR", checkMessages, p);
                }
                checkExists("Judge solution", getReferenceLength(p, ReferenceType.JUDGE_SOLUTION), "WARNING", checkMessages, p);
            }
            context.setAttribute("CheckMessages", checkMessages);
        }
        return handleSuccess(mapping, context, "success");                
                  	    	   
    }  
    
    private void checkExists(String name, long length, String level, List<String> checkMessages, Problem p) {
        if (length == -1) {
            checkMessages.add(level + "[ "+ p.getCode() + "] - " + name + " is missing.");
        } else if (length == 0) {
            checkMessages.add(level + "[ "+ p.getCode() + "] - " + name + " is empty.");
        }
    }
    
    
    private long getReferenceLength(Problem p, ReferenceType type) throws Exception {
        ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
        List refs = referencePersistence.getProblemReferenceInfo(p.getId(), type);
        
        if (refs.size() == 0) {
            return -1;
        }
        Reference ref = (Reference) refs.get(0);
        return ref.getSize();        
    }

}
    