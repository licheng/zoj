<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ProblemStatistics" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Submission" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>
<%@ page import="java.util.List" %>

<% 
    boolean isProblemset = "Problems".equals(request.getAttribute("region"));
    Problem problem = (Problem) request.getAttribute("problem");
    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
    ProblemStatistics statistics = (ProblemStatistics) request.getAttribute("ProblemStatistics");
    String problemPath = request.getContextPath() + (isProblemset ? "/showProblem.do" : "/showContestProblem.do") + "?problemId=" + problem.getId();
    String problemStatusPath = request.getContextPath() + (isProblemset ? "/showProblemStatus.do" : "/showContestProblemStatus.do") + "?problemId=" + problem.getId(); 
    
    String runsPath = request.getContextPath() + (isProblemset ? "/showRuns.do" : "/showContestRuns.do") + "?contestId=" + contest.getId() + "&problemCode=" + problem.getCode();
    String userStatusPath = request.getContextPath() + "/showUserStatus.do";
    String orderBy = request.getParameter("orderBy");
    
%>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_body">
                <div id="content_title"><bean:write name="contest" property="title"/> - <bean:write name="problem" property="code"/></div>
        
                <center><font size="4"><a href="<%=problemPath%>"><bean:write name="problem" property="code"/></a></font></center>
                <p/>
                <div>
                <table class="list">
                    <tr class="rowHeader" align="center">
                        <td style="color:red">AC</td>
                        <td style="color:green">WA</td>
                        <td style="color:green">PE</td>
                        <td style="color:green">FPE</td>
                        <td style="color:green">SF</td>
                        <td style="color:green">TLE</td>
                        <td style="color:green">MLE</td>
                        <td style="color:green">CE</td>
                        <td style="color:green">Total</td>
                    </tr>
                    <tr class="rowOdd" align="center">
                        <td style="color:red"><a href="<%=runsPath + "&judgeReplyIds=" + JudgeReply.ACCEPTED.getId()%>"><%=statistics.getCount(JudgeReply.ACCEPTED)%>(<%=statistics.getPercentageInt(JudgeReply.ACCEPTED)%>%)</a></td>
                        <td style="color:green"><a href="<%=runsPath + "&judgeReplyIds=" + JudgeReply.WRONG_ANSWER.getId()%>"><%=statistics.getCount(JudgeReply.WRONG_ANSWER)%>(<%=statistics.getPercentageInt(JudgeReply.WRONG_ANSWER)%>%)</a></td>
                        <td style="color:green"><a href="<%=runsPath + "&judgeReplyIds=" + JudgeReply.PRESENTATION_ERROR.getId()%>"><%=statistics.getCount(JudgeReply.PRESENTATION_ERROR)%>(<%=statistics.getPercentageInt(JudgeReply.PRESENTATION_ERROR)%>%)</a></td>
                        <td style="color:green"><a href="<%=runsPath + "&judgeReplyIds=" + JudgeReply.FLOATING_POINT_ERROR.getId()%>"><%=statistics.getCount(JudgeReply.FLOATING_POINT_ERROR)%>(<%=statistics.getPercentageInt(JudgeReply.FLOATING_POINT_ERROR)%>%)</a></td>
                        <td style="color:green"><a href="<%=runsPath + "&judgeReplyIds=" + JudgeReply.SEGMENTATION_FAULT.getId()%>"><%=statistics.getCount(JudgeReply.SEGMENTATION_FAULT)%>(<%=statistics.getPercentageInt(JudgeReply.SEGMENTATION_FAULT)%>%)</a></td>
                        <td style="color:green"><a href="<%=runsPath + "&judgeReplyIds=" + JudgeReply.TIME_LIMIT_EXCEEDED.getId()%>"><%=statistics.getCount(JudgeReply.TIME_LIMIT_EXCEEDED)%>(<%=statistics.getPercentageInt(JudgeReply.TIME_LIMIT_EXCEEDED)%>%)</a></td>
                        <td style="color:green"><a href="<%=runsPath + "&judgeReplyIds=" + JudgeReply.MEMORY_LIMIT_EXCEEDED.getId()%>"><%=statistics.getCount(JudgeReply.MEMORY_LIMIT_EXCEEDED)%>(<%=statistics.getPercentageInt(JudgeReply.MEMORY_LIMIT_EXCEEDED)%>%)</a></td>
                        <td style="color:green"><a href="<%=runsPath + "&judgeReplyIds=" + JudgeReply.COMPILATION_ERROR.getId()%>"><%=statistics.getCount(JudgeReply.COMPILATION_ERROR)%>(<%=statistics.getPercentageInt(JudgeReply.COMPILATION_ERROR)%>%)</a></td>
                        <td style="color:green"><a href="<%=runsPath%>"><%=statistics.getTotal()%></a></td>
                    </tr>
                </table>
                <br/>
                </div>
                <%
                    List runs = statistics.getBestRuns();
                    if (runs != null && runs.size() > 0) {
                %>
                <div>Top Submssions by <font color="blue"><%="submittime".equals(orderBy) ? "Submit Time" : ("memory".equals(orderBy) ? "Run Memory" : "Run Time")%></font>
                <table class="list">
                    <tr class="rowHeader">
                        <td class="runSubmitTime"><a href="<%=problemStatusPath%>&orderBy=date">Submit Time</a></td>
                        <td class="runLanguage">Language</td>
                        <td class="runTime"><a href="<%=problemStatusPath%>&orderBy=time">Run Time(ms)</a></td>
                        <td class="runMemory"><a href="<%=problemStatusPath%>&orderBy=memory">Run Memory(KB)</a></td>
                        <td class="runUserName">User Name</td>
                    </tr>
                    <%
                    for (int i = 0; i < runs.size(); ++i) {
                         Submission submission = (Submission) runs.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td class="runSubmitTime"><%=Utility.toTimestamp(submission.getSubmitDate())%></td>
                        <td class="runLanguage"><%=submission.getLanguage()%></td>
                        <td class="runTime"><%=submission.getTimeConsumption()%></td>
                        <td class="runMemory"><%=submission.getMemoryConsumption()%></td>
                        <td class="runUserName"><a href="<%=userStatusPath + "?handle=" + submission.getUserName()%>"><font color="db6d00"><%=submission.getUserName()%></font></a></td>                      
                    </tr>
                    <%
                    }
                    %>
                </table>
                </div>
                <% } %>
        </div>
        </logic:messagesNotPresent>

