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
import cn.edu.zju.acm.onlinejudge.util.ProblemsetRankList;
import cn.edu.zju.acm.onlinejudge.util.RankList;
import cn.edu.zju.acm.onlinejudge.util.RankListEntry;
import cn.edu.zju.acm.onlinejudge.util.StatisticsManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * ShowRankListAction
 * </p>
 * 
 * 
 * @author Zhang, Zheng
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

        // check contest
        boolean isProblemset = context.getRequest().getRequestURI().endsWith("showRankList.do");

        ActionForward forward = this.checkContestViewPermission(mapping, context, isProblemset, true);
        if (forward != null) {
            return forward;
        }
        AbstractContest contest = context.getContest();
        if (!isProblemset) {
            List<Problem> problems = context.getProblems();
            context.setAttribute("problems", problems);
            long roleId = Utility.parseLong(context.getRequest().getParameter("roleId"));

            RankList ranklist = StatisticsManager.getInstance().getRankList(contest.getId(), roleId);

            String export = context.getRequest().getParameter("export");

            if ("txt".equalsIgnoreCase(export)) {
                return this.export(context, contest, problems, ranklist, export);
            } else if ("xls".equalsIgnoreCase(export)) {
                return this.export(context, contest, problems, ranklist, export);
            }
            context.setAttribute("RankList", ranklist);
        } else {
            int from = Utility.parseInt(context.getRequest().getParameter("from"));
            if (from < 0) {
                from = 0;
            }
            int count = 30;

            ProblemsetRankList ranklist =
                    StatisticsManager.getInstance().getProblemsetRankList(contest.getId(), from, count);
            if (from > 0) {
                context.setAttribute("previousFrom", from - count > 0 ? from - count : 0);
            }
            if (ranklist.getSolved().length == count) {
                context.setAttribute("nextFrom", from + count);
            }

            context.setAttribute("RankList", ranklist);
        }
        return this.handleSuccess(mapping, context, "success");

    }

    private ActionForward export(ContextAdapter context, AbstractContest contest, List<Problem> problems,
                                 RankList ranklist, String export) throws Exception {
        byte[] out;
        String fileName = this.getFileName(contest);
        HttpServletResponse response = context.getResponse();
        if ("xls".equalsIgnoreCase(export)) {
            out = this.exportToExcel(contest, problems, ranklist);
            response.setContentType("application/doc");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");
        } else {
            boolean windows = true;
            String userAgentHeader = context.getRequest().getHeader("user-agent");
            if (userAgentHeader != null && userAgentHeader.length() > 0) {
                windows = userAgentHeader.indexOf("Windows") != -1;
            }
            out = this.exportToText(contest, problems, ranklist, windows);
            response.setContentType("text/plain");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".txt");
        }
        response.getOutputStream().write(out);
        response.getOutputStream().close();
        return null;
    }

    private byte[] exportToExcel(AbstractContest contest, List<Problem> problems, RankList ranklist) throws Exception {
        List<RankListEntry> entries = ranklist.getEntries();
        long time = this.getTimeEscaped(contest);

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
        for (Problem problem2 : problems) {
            Problem problem = problem2;
            row.createCell(columnIndex).setCellValue(problem.getCode());
            columnIndex++;
        }
        row.createCell(columnIndex).setCellValue("Penalty");

        int rowIndex = 6;
        for (RankListEntry rankListEntry : entries) {
            RankListEntry entry = rankListEntry;
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
                String score =
                        entry.getAcceptTime(i) > 0 ? entry.getAcceptTime(i) + "(" + entry.getSubmitNumber(i) + ")"
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

    private byte[] exportToText(AbstractContest contest, List<Problem> problems, RankList ranklist, boolean windows) throws Exception {
        List<RankListEntry> entries = ranklist.getEntries();
        String lineHolder = windows ? "\r\n" : "\n";
        long time = this.getTimeEscaped(contest);

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
        for (Problem problem2 : problems) {
            Problem problem = problem2;
            writer.write(problem.getCode());
            writer.write("\t");
        }
        writer.write("Penalty");
        writer.write(lineHolder);

        int index = 0;
        for (RankListEntry rankListEntry : entries) {
            index++;
            RankListEntry entry = rankListEntry;
            writer.write(index + "\t");
            writer.write(entry.getUserProfile().getHandle() + "\t");
            writer.write((entry.getUserProfile().getNickName() == null ? entry.getUserProfile().getHandle()
                                                                      : entry.getUserProfile().getNickName()) +
                "\t");
            writer.write(entry.getSolved() + "\t");
            for (int i = 0; i < problems.size(); ++i) {
                String score =
                        entry.getAcceptTime(i) > 0 ? entry.getAcceptTime(i) + "(" + entry.getSubmitNumber(i) + ")"
                                                  : "" + entry.getSubmitNumber(i);
                writer.write(score + "\t");
            }
            writer.write(entry.getPenalty() + lineHolder);
        }
        writer.close();

        return out.toByteArray();
    }

    private String getFileName(AbstractContest contest) {
        long time = this.getTimeEscaped(contest);
        String contestName = contest.getTitle().trim() + "_RankList_" + Utility.toTime(time / 1000);
        StringBuilder sb = new StringBuilder();
        boolean last = false;
        for (int i = 0; i < contestName.length(); ++i) {
            char ch = contestName.charAt(i);
            if (ch <= 'Z' && ch >= 'A' || ch <= 'z' && ch >= 'a' || ch <= '9' && ch >= '0') {
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
