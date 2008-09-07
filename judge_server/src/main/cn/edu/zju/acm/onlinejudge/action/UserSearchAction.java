/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.action;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
import org.apache.struts.action.ActionMessages;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.Reference;
import cn.edu.zju.acm.onlinejudge.bean.Submission;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.Language;
import cn.edu.zju.acm.onlinejudge.bean.request.ProblemCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.SubmissionCriteria;
import cn.edu.zju.acm.onlinejudge.bean.request.UserCriteria;
import cn.edu.zju.acm.onlinejudge.form.SubmissionSearchForm;
import cn.edu.zju.acm.onlinejudge.form.UserSearchForm;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.ProblemPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * UserSearchAction
 * </p>
 * 
 * 
 * @author ZOJDEV
 * @version 2.0
 */
public class UserSearchAction extends BaseAction {
    
	
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public UserSearchAction() {
    	
    }

    /**
     * ShowRunsAction.
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
    	
        ActionForward forward = checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }
        
        context.setAttribute("UserSearchForm", form);
        
        context.getRequest().setAttribute("Countries", 
                PersistenceManager.getInstance().getUserPersistence().getAllCountries());
        context.getRequest().setAttribute("Roles", 
                PersistenceManager.getInstance().getAuthorizationPersistence().getAllRoles());
        
        UserSearchForm userForm = (UserSearchForm) form;
        
        if (empty(userForm.getCountryId()) &&
            empty(userForm.getEmail()) &&
            empty(userForm.getFirstName()) &&
            empty(userForm.getHandle()) &&
            empty(userForm.getLastName()) &&
            empty(userForm.getRoleId()) &&
            empty(userForm.getSchool())) {
            
            return handleSuccess(mapping, context, "success");
            
        }
        
        
        UserCriteria criteria = userForm.toUserCriteria();         
        String export = context.getRequest().getParameter("exportFormat");
        
        if ("txt".equalsIgnoreCase(export)) {
            List users = PersistenceManager.getInstance().getUserPersistence().searchUserProfiles(
                    criteria, 0, Integer.MAX_VALUE);
            return export(context, criteria, users, export);
        } else if ("xls".equalsIgnoreCase(export)) {
            List users = PersistenceManager.getInstance().getUserPersistence().searchUserProfiles(
                    criteria, 0, Integer.MAX_VALUE);            
            return export(context, criteria, users, export);
        } 
        long paging = Utility.parseLong(userForm.getPaging(), 10, 50);;
        
        long usersNumber = PersistenceManager.getInstance().getUserPersistence().searchUserProfilesCount(criteria);
        if (usersNumber == 0) {
            context.setAttribute("users", new ArrayList());
            context.setAttribute("pageNumber", new Long(0));
            context.setAttribute("totalPages", new Long(0));      
            context.setAttribute("paging", new Long(paging));
            context.setAttribute("total", new Long(0));
            return handleSuccess(mapping, context, "success");
        } 
        
        long totalPages = (usersNumber - 1) / paging + 1;
        long pageNumber = Utility.parseLong(userForm.getPageNumber(), 1, totalPages);        
        long startIndex = paging * (pageNumber - 1);
    	
    	List users = PersistenceManager.getInstance().getUserPersistence().searchUserProfiles(
                criteria, (int) startIndex, (int) paging);                      

        context.setAttribute("users", users);
        context.setAttribute("pageNumber", new Long(pageNumber));
        context.setAttribute("totalPages", new Long(totalPages));
        context.setAttribute("paging", new Long(paging));
        context.setAttribute("total", new Long(usersNumber));
                        
        return handleSuccess(mapping, context, "success");
                  	    	   
    }   
    
    private boolean empty(String value) {
        return value == null || value.trim().length() == 0;
    }
    
    
    private ActionForward export(ContextAdapter context, UserCriteria criteria, List users, String export) throws Exception {        
        
        byte[] out;
        String fileName = "userlist";
        HttpServletResponse response = context.getResponse();
        if ("xls".equalsIgnoreCase(export)) {
            out = exportToExcel(criteria, users);            
            response.setContentType("application/doc");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");
        } else {
            boolean windows = true;
            String userAgentHeader = context.getRequest().getHeader("user-agent");
            if (userAgentHeader != null && userAgentHeader.length() > 0) {
                windows = userAgentHeader.indexOf("Windows") != -1;
            }
            out = exportToText(criteria, users, windows);
            response.setContentType("text/plain");      
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".txt");
        }        
        response.getOutputStream().write(out);
        response.getOutputStream().close();
        return null;
    }

    private byte[] exportToText(UserCriteria criteria, List users, boolean windows) throws Exception {
        String lineHolder = windows ? "\r\n" : "\n";
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();        
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        for (Object user : users) {
            writer.write(((UserProfile) user).getHandle());
            writer.write(lineHolder);
        }        
        writer.close();
        
        return out.toByteArray();
    }
    
    private byte[] exportToExcel(UserCriteria criteria, List users) throws Exception {
        
        HSSFWorkbook wb = new HSSFWorkbook();       
        HSSFSheet sheet = wb.createSheet();  
        short rowId = 0;
        for (Object user : users) {
            HSSFRow row = sheet.createRow(rowId);
            rowId++;
            HSSFCell cell = row.createCell((short) 0);
            cell.setCellValue(((UserProfile) user).getHandle());
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
}
    