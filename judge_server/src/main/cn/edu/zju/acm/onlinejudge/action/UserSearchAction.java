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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.request.UserCriteria;
import cn.edu.zju.acm.onlinejudge.form.UserSearchForm;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * UserSearchAction
 * </p>
 * 
 * 
 * @author Zhang, Zheng
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

        ActionForward forward = this.checkAdmin(mapping, context);
        if (forward != null) {
            return forward;
        }

        context.setAttribute("UserSearchForm", form);

        context.getRequest().setAttribute("Countries",
                                          PersistenceManager.getInstance().getUserPersistence().getAllCountries());
        context.getRequest().setAttribute("Roles",
                                          PersistenceManager.getInstance().getAuthorizationPersistence().getAllRoles());

        UserSearchForm userForm = (UserSearchForm) form;

        if (this.empty(userForm.getCountryId()) && this.empty(userForm.getEmail()) &&
            this.empty(userForm.getFirstName()) && this.empty(userForm.getHandle()) &&
            this.empty(userForm.getLastName()) && this.empty(userForm.getRoleId()) && this.empty(userForm.getSchool())) {

            return this.handleSuccess(mapping, context, "success");

        }

        UserCriteria criteria = userForm.toUserCriteria();
        String export = context.getRequest().getParameter("exportFormat");

        if ("txt".equalsIgnoreCase(export)) {
            List<UserProfile> users =
                    PersistenceManager.getInstance().getUserPersistence().searchUserProfiles(criteria, 0,
                                                                                             Integer.MAX_VALUE);
            return this.export(context, criteria, users, export);
        } else if ("xls".equalsIgnoreCase(export)) {
            List<UserProfile> users =
                    PersistenceManager.getInstance().getUserPersistence().searchUserProfiles(criteria, 0,
                                                                                             Integer.MAX_VALUE);
            return this.export(context, criteria, users, export);
        }
        long paging = Utility.parseLong(userForm.getPaging(), 10, 50);;

        long usersNumber = PersistenceManager.getInstance().getUserPersistence().searchUserProfilesCount(criteria);
        if (usersNumber == 0) {
            context.setAttribute("users", new ArrayList<UserProfile>());
            context.setAttribute("pageNumber", new Long(0));
            context.setAttribute("totalPages", new Long(0));
            context.setAttribute("paging", new Long(paging));
            context.setAttribute("total", new Long(0));
            return this.handleSuccess(mapping, context, "success");
        }

        long totalPages = (usersNumber - 1) / paging + 1;
        long pageNumber = Utility.parseLong(userForm.getPageNumber(), 1, totalPages);
        long startIndex = paging * (pageNumber - 1);

        List<UserProfile> users =
                PersistenceManager.getInstance().getUserPersistence().searchUserProfiles(criteria, (int) startIndex,
                                                                                         (int) paging);

        context.setAttribute("users", users);
        context.setAttribute("pageNumber", new Long(pageNumber));
        context.setAttribute("totalPages", new Long(totalPages));
        context.setAttribute("paging", new Long(paging));
        context.setAttribute("total", new Long(usersNumber));

        return this.handleSuccess(mapping, context, "success");

    }

    private boolean empty(String value) {
        return value == null || value.trim().length() == 0;
    }

    private ActionForward export(ContextAdapter context, UserCriteria criteria, List<UserProfile> users, String export) throws Exception {

        byte[] out;
        String fileName = "userlist";
        HttpServletResponse response = context.getResponse();
        if ("xls".equalsIgnoreCase(export)) {
            out = this.exportToExcel(criteria, users);
            response.setContentType("application/doc");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");
        } else {
            boolean windows = true;
            String userAgentHeader = context.getRequest().getHeader("user-agent");
            if (userAgentHeader != null && userAgentHeader.length() > 0) {
                windows = userAgentHeader.indexOf("Windows") != -1;
            }
            out = this.exportToText(criteria, users, windows);
            response.setContentType("text/plain");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".txt");
        }
        response.getOutputStream().write(out);
        response.getOutputStream().close();
        return null;
    }

    private byte[] exportToText(UserCriteria criteria, List<UserProfile> users, boolean windows) throws Exception {
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

    private byte[] exportToExcel(UserCriteria criteria, List<UserProfile> users) throws Exception {

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
