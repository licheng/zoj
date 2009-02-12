<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ContestStatistics" %>

<%
    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
    String statisticsPath = request.getContextPath() + "/showContestRuns.do?contestId=" + contest.getId();
%>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title"><bean:write name="contest" property="title"/></div>
        <div id="content_body">
                <p></p>
                <table class="list">

                    <tr class="rowHeader">
                        <td class="statisticsProblem">Problem</td>
                        <td class="statisticsAC">AC</td>
                        <td class="statisticsWA">WA</td>
                        <td class="statisticsPE">PE</td>
                        <td class="statisticsRTE">RTE</td>
                        <td class="statisticsFPE">FPE</td>
                        <td class="statisticsSF">SF</td>
                        <td class="statisticsTLE">TLE</td>
                        <td class="statisticsMLE">MLE</td>
                        <td class="statisticsOLE">OLE</td>
                        <td class="statisticsCE">CE</td>
                        <td class="statisticsSubmit">Submit</td>
                    </tr>
                    <%
                    ContestStatistics statistics = (ContestStatistics) request.getAttribute("ContestStatistics");
                    List problems = statistics.getProblems();
                    for (int i = 0; i < problems.size(); ++i) {
                         Problem problem = (Problem) problems.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td class="statisticsProblem"><%=problem.getCode()%></td>
                        <td class="statisticsAC"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.ACCEPTED.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 0)%></a></td>
                        <td class="statisticsWA"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.WRONG_ANSWER.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 1)%></a></td>
                        <td class="statisticsPE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.PRESENTATION_ERROR.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 2)%></a></td>
                        <td class="statisticsRTE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.RUNTIME_ERROR.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 3)%></a></td>
                        <td class="statisticsFPE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.FLOATING_POINT_ERROR.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 4)%></a></td>
                        <td class="statisticsSF"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.SEGMENTATION_FAULT.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 5)%></a></td>
                        <td class="statisticsTLE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.TIME_LIMIT_EXCEEDED.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 6)%></a></td>
                        <td class="statisticsMLE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.MEMORY_LIMIT_EXCEEDED.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 7)%></a></td>
                        <td class="statisticsOLE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.OUTPUT_LIMIT_EXCEEDED.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 8)%></a></td>
                        <td class="statisticsCE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.COMPILATION_ERROR.getId()+"&problemCode=" + problem.getCode()%>"><%=statistics.getCount(i, 9)%></a></td>
                        <td class="statisticsSubmit"><a href="<%=statisticsPath + "&problemCode=" + problem.getCode()%>"><%=statistics.getProblemCount(i)%></a></td>
                    </tr>                                      																
                    <%
                    }
                    %>
                    <tr class="statisticsSummaryRow">
                        <td class="statisticsProblem">Summary</td>
                        <td class="statisticsAC"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.ACCEPTED.getId()%>"><%=statistics.getJudgeReplyCount(0)%></a></td>
                        <td class="statisticsWA"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.WRONG_ANSWER.getId()%>"><%=statistics.getJudgeReplyCount(1)%></a></td>
                        <td class="statisticsPE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.PRESENTATION_ERROR.getId()%>"><%=statistics.getJudgeReplyCount(2)%></a></td>
                        <td class="statisticsRTE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.RUNTIME_ERROR.getId()%>"><%=statistics.getJudgeReplyCount(3)%></a></td>
                        <td class="statisticsFPE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.FLOATING_POINT_ERROR.getId()%>"><%=statistics.getJudgeReplyCount(4)%></a></td>                        
                        <td class="statisticsSF"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.SEGMENTATION_FAULT.getId()%>"><%=statistics.getJudgeReplyCount(5)%></a></td>
                        <td class="statisticsTLE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.TIME_LIMIT_EXCEEDED.getId()%>"><%=statistics.getJudgeReplyCount(6)%></a></td>
                        <td class="statisticsMLE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.MEMORY_LIMIT_EXCEEDED.getId()%>"><%=statistics.getJudgeReplyCount(7)%></a></td>
                        <td class="statisticsOLE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.OUTPUT_LIMIT_EXCEEDED.getId()%>"><%=statistics.getJudgeReplyCount(8)%></a></td>
                        <td class="statisticsCE"><a href="<%=statisticsPath + "&judgeReplyIds=" + JudgeReply.COMPILATION_ERROR.getId()%>"><%=statistics.getJudgeReplyCount(9)%></a></td>
                        <td class="statisticsSubmit"><a href="<%=statisticsPath%>"><%=statistics.getTotal()%></a></td>
                    </tr>
                </table>
                <p></p>
        </div>
        </logic:messagesNotPresent>

