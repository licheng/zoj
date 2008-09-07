/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.ReferenceType;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ReferencePersistence;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;

/**
 * <p>
 * ExportProblemsAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ExportProblemsAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ExportProblemsAction() {
        // empty
    }

    /**
     * ExportProblemsAction.
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
        
        // check contest
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("exportProblems.do");
        
        ActionForward forward = checkContestAdminPermission(mapping, context, isProblemset, false);
        if (forward != null) {
            return  forward;
        }                             
        
        AbstractContest contest = context.getContest();                
        List problems = ContestManager.getInstance().getContestProblems(contest.getId());

        
        HttpServletResponse response = context.getResponse();
        response.setContentType("application/zip");        
        response.setHeader("Content-disposition", 
                    "attachment; filename=" + convertFilename(contest.getTitle()) + ".zip");

        ZipOutputStream out = null;
        StringBuilder sb = new StringBuilder();
        try {
            out = new ZipOutputStream(response.getOutputStream());
            for (Object obj : problems) {
                Problem p = (Problem) obj;
                zipReference(p, "text", ReferenceType.DESCRIPTION, out); 
                zipReference(p, "input", ReferenceType.INPUT, out);
                zipReference(p, "output", ReferenceType.OUTPUT, out);
                zipReference(p, "solution", ReferenceType.JUDGE_SOLUTION, out);
                zipReference(p, "checker", ReferenceType.CHECKER, out);
                zipReference(p, "checker", ReferenceType.CHECKER_SOURCE, out);
                
                sb.append(p.getCode()).append(",");
                sb.append(p.getTitle()).append(",");
                sb.append(p.isChecker()).append(",");
                sb.append(p.getLimit().getTimeLimit()).append(",");
                sb.append(p.getLimit().getMemoryLimit()).append(",");
                sb.append(p.getLimit().getOutputLimit()).append(",");
                sb.append(p.getLimit().getSubmissionLimit()).append(",");
                sb.append(p.getAuthor()).append(",");
                sb.append(p.getSource()).append(",");
                sb.append(p.getContest()).append("\n");                                   
            }
            out.putNextEntry(new ZipEntry("problems.csv"));
            out.write(sb.toString().getBytes());
            
        } catch (IOException e) {            
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
        }
               
        return null;
                          	    	   
    } 
    
    
    private void zipReference(Problem p, String fileName, ReferenceType type, ZipOutputStream out) throws Exception {
        ReferencePersistence referencePersistence = PersistenceManager.getInstance().getReferencePersistence();
        List refs = referencePersistence.getProblemReferences(p.getId(), type);
        
        if (refs.size() == 0) {
            return;
        }
        Reference ref = (Reference) refs.get(0);
        if (type == ReferenceType.CHECKER_SOURCE || type == ReferenceType.JUDGE_SOLUTION) {
        	String contentType = ref.getContentType();
        	if (contentType == null) {
        		contentType = "cc";
        	}
        	fileName += "." + contentType;
        }
        out.putNextEntry(new ZipEntry(p.getCode() + "/" + fileName));
        
        byte[] data = ref.getContent();
        out.write(data);                               
        out.closeEntry();        
    }
    
    private String convertFilename(String name) {
        char[] s = name.toCharArray();        
        for (int i = 0; i < s.length; i++) {
            if (Character.isSpaceChar(s[i])) {
                s[i] = '_';
            }
        }
        return new String(s);
    }
}
    