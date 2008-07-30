/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

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
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.RankList;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;
import cn.edu.zju.acm.onlinejudge.util.ContestStatistics;

/**
 * <p>
 * ShowRankListAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class ShowRankListAction extends BaseAction {
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public ShowRankListAction() {
        // empty
    }

    /**
     * ShowRankListAction.
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
    	boolean isProblemset = context.getRequest().getRequestURI().endsWith("showRankList.do");
    	
    	ActionForward forward = checkContestViewPermission(mapping, context, isProblemset, true);
    	if (forward != null) {
    		return forward;
    	}    	
    	AbstractContest contest = context.getContest();
    	List problems = context.getProblems();
    	context.setAttribute("problems", problems);
        long roleId = Utility.parseLong(context.getRequest().getParameter("roleId"));
        if(!isProblemset)
        {
	    	RankList ranklist = StatisticsManager.getInstance().getRankList(contest.getId(), roleId);
	    	
	    	String export = context.getRequest().getParameter("export");
	    	
	    	if ("txt".equalsIgnoreCase(export)) {
	    		return export(context, contest, problems, ranklist, export);
	    	} else if ("xls".equalsIgnoreCase(export)) {
	    		return export(context, contest, problems, ranklist, export);
	    	}
	    	context.setAttribute("RankList", ranklist);
        }
        else
        {
        	Object sorder=context.getRequest().getParameter("order");
        	Object sbegin=context.getRequest().getParameter("from");
        	int begin=(sbegin==null)? 0 : Integer.parseInt(sbegin.toString());
        	int order=(sorder.equals("AC"))? 0 : 1;
        	System.out.println(order);
        	RankList ranklist = StatisticsManager.getInstance().getProblemsetRankList(contest.getId(), roleId, begin,order);
        	context.setAttribute("RankList", ranklist);
        }
        return handleSuccess(mapping, context, "success");
                  	    	   
    }         
    
    
    private ActionForward export(ContextAdapter context, AbstractContest contest, List problems, RankList ranklist, String export) throws Exception {  
        List entries = ranklist.getEntries();
    	
    	byte[] out;
    	String fileName = getFileName(contest);
    	HttpServletResponse response = context.getResponse();
    	if ("xls".equalsIgnoreCase(export)) {
    		out = exportToExcel(contest, problems, ranklist);    		
    		response.setContentType("application/doc");
    		response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");
    	} else {
    		boolean windows = true;
    		String userAgentHeader = context.getRequest().getHeader("user-agent");
    		if (userAgentHeader != null && userAgentHeader.length() > 0) {
    			windows = userAgentHeader.indexOf("Windows") != -1;
    		}
    		out = exportToText(contest, problems, ranklist, windows);
    		response.setContentType("text/plain");    	
    		response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".txt");
    	}        
    	response.getOutputStream().write(out);
        response.getOutputStream().close();
        return null;
    }

    private byte[] exportToExcel(AbstractContest contest, List problems, RankList ranklist) throws Exception {
        List entries = ranklist.getEntries();
    	long time = getTimeEscaped(contest);
    	
    	HSSFWorkbook wb = new HSSFWorkbook();    	
    	HSSFSheet sheet = wb.createSheet();    	
    	HSSFRow row = sheet.createRow(0);
    	HSSFCell cell = row.createCell((short) 0);
    	cell.setCellValue(contest.getTitle());
        if (ranklist.getRole() != null) {
            row = sheet.createRow(1);
            cell = row.createCell((short) 0);
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(ranklist.getRole().getDescription());
        }
        
    	row = sheet.createRow(2);
    	cell = row.createCell((short) 0);
    	cell.setCellValue("Length");
    	cell = row.createCell((short) 1);
    	cell.setCellValue(Utility.toTime(contest.getLength() / 1000));
    	
    	row = sheet.createRow(3);
    	cell = row.createCell((short) 0);
    	cell.setCellValue("Time Escaped");
    	cell = row.createCell((short) 1);
    	cell.setCellValue(Utility.toTime(time / 1000));
    	
    	row = sheet.createRow(5);
    	row.createCell((short) 0).setCellValue("Rank");
    	row.createCell((short) 1).setCellValue("Handle");
        row.createCell((short) 2).setCellValue("Nickname");
    	row.createCell((short) 3).setCellValue("Solved");
    	short columnIndex = 4;
    	for (Iterator it = problems.iterator(); it.hasNext();) {    		
    		Problem problem = (Problem) it.next();
    		row.createCell(columnIndex).setCellValue(problem.getCode());
    		columnIndex++;
    	}
    	row.createCell(columnIndex).setCellValue("Penalty");
    	
    	int rowIndex = 6;
    	for (Iterator it = entries.iterator(); it.hasNext();) {    		
    		RankListEntry entry = (RankListEntry) it.next();
    		row = sheet.createRow(rowIndex);
    		row.createCell((short) 0).setCellValue(rowIndex - 5);
    		row.createCell((short) 1).setCellValue(entry.getUserProfile().getHandle());
            String nick = entry.getUserProfile().getHandle();            
            if (entry.getUserProfile().getNickName() != null) {
                nick = entry.getUserProfile().getNickName();
            }
            cell = row.createCell((short) 2);            
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellValue(nick);
    		
            row.createCell((short) 3).setCellValue(entry.getSolved());
            
    		for (short i = 0; i < problems.size(); ++i) { 
    			String score = entry.getAcceptTime(i) > 0 ? 
    					entry.getAcceptTime(i) +"(" + entry.getSubmitNumber(i) + ")"
    					: "" + entry.getSubmitNumber(i);
    		   row.createCell((short) (4 + i)).setCellValue(score);    		   
    		}
    		row.createCell((short) (4 + problems.size())).setCellValue(entry.getPenalty());    
    		rowIndex++;
    	}       
    	
    	// output to stream
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	try {
    		wb.write(out);
    		return out.toByteArray();
    	} finally {
    		out.close();
    	}    	         
    }
    
    private byte[] exportToText(AbstractContest contest, List problems, RankList ranklist, boolean windows) throws Exception {
        List entries = ranklist.getEntries();
    	String lineHolder = windows ? "\r\n" : "\n";
    	long time = getTimeEscaped(contest);
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();    	
    	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
    	writer.write(contest.getTitle());
    	writer.write(lineHolder);
    	writer.write(ranklist.getRole() == null ? "" : ranklist.getRole().getDescription());
        writer.write(lineHolder);              
        writer.write("Length: " + Utility.toTime(contest.getLength() / 1000));
    	writer.write(lineHolder);
    	writer.write("Time Escaped: " + Utility.toTime(time / 1000));
    	writer.write(lineHolder);
    	writer.write(lineHolder);
    	
    	writer.write("Rank\tHandle\tNickname\tSolved\t");
    	for (Iterator it = problems.iterator(); it.hasNext();) {
    		Problem problem = (Problem) it.next();
    		writer.write(problem.getCode());
    		writer.write("\t");
    	}
    	writer.write("Penalty");
    	writer.write(lineHolder);
    	
    	int index = 0;
    	for (Iterator it = entries.iterator(); it.hasNext();) {
    		index++;
    		RankListEntry entry = (RankListEntry) it.next();
    		writer.write(index + "\t");
    		writer.write(entry.getUserProfile().getHandle() + "\t");
            writer.write((entry.getUserProfile().getNickName() == null ? entry.getUserProfile().getHandle() : entry.getUserProfile().getNickName()) + "\t");
    		writer.write(entry.getSolved() + "\t");
    		for (int i = 0; i < problems.size(); ++i) { 
    			String score = entry.getAcceptTime(i) > 0 ? 
    					entry.getAcceptTime(i) +"(" + entry.getSubmitNumber(i) + ")"
    					: "" + entry.getSubmitNumber(i);
    		   writer.write(score + "\t");
    		}
    		writer.write(entry.getPenalty() + lineHolder);    		
    	}       
    	writer.close();
    	
        return out.toByteArray();
    }
    
    private String getFileName(AbstractContest contest) {
    	long time = getTimeEscaped(contest);
    	String contestName = contest.getTitle().trim() + "_RankList_" + Utility.toTime(time / 1000);
    	StringBuilder sb = new StringBuilder();
    	boolean last = false;
    	for (int i = 0; i < contestName.length(); ++i) {
    		char ch = contestName.charAt(i);
    		if ((ch <= 'Z' && ch >= 'A') || (ch <= 'z' && ch >= 'a') || (ch <= '9' && ch >= '0')) {
    			if (last) {
    				sb.append('_');
    				last = false;
    			}
    			sb.append(ch);
    		} else {
    			last = true;
    		}
    	}
    	return sb.toString();
    }
    private long getTimeEscaped(AbstractContest contest) {
    	long time = System.currentTimeMillis() - contest.getStartTime().getTime();
    	if (time < 0) {
    		time = 0;
    	}
    	if (time > contest.getLength()) {
    		time = contest.getLength();
    	}
    	return time;
    }
}
    