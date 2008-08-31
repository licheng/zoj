<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ProblemStatistics" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Submission" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>
<%@ page import="java.util.List" %>
<% Problem p = (Problem)request.getAttribute("Problem"); %>
        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_body">
                <center><font color=blue size="+2"><%=p.getTitle() %></font></center>
                <table class="list" border="1">

                    <tr class="rowHeader">
                        <td style="color:red">AC</td>
                        <td style="color:green">WA</td>
                        <td style="color:blue">PE</td>
                        <td style="color:green">RTE</td>
                        <td style="color:green">FPE</td>
                        <td style="color:green">SF</td>
                        <td style="color:green">TLE</td>
                        <td style="color:green">MLE</td>
                        <td style="color:green">OLE</td>
                        <td style="color:green">CE</td>
                        <td style="color:green">Total</td>
                    </tr>
                    <tr class="rowOdd">
                    <%
                    ProblemStatistics statistics = (ProblemStatistics) request.getAttribute("ProblemStatistics");
                     %>
                        <td style="color:red"><%=statistics.getCount(0)%></td>
                        <td style="color:green"><%=statistics.getCount(1)%></td>
                        <td style="color:blue"><%=statistics.getCount(2)%></td>
                        <td style="color:green"><%=statistics.getCount(3)%></td>
                        <td style="color:green"><%=statistics.getCount(4)%></td>
                        <td style="color:green"><%=statistics.getCount(5)%></td>
                        <td style="color:green"><%=statistics.getCount(6)%></td>
                        <td style="color:green"><%=statistics.getCount(7)%></td>
                        <td style="color:green"><%=statistics.getCount(8)%></td>
                        <td style="color:green"><%=statistics.getCount(9)%></td>
                        <td style="color:green"><%=statistics.getTotal()%></td>
                    </tr>
                </table>
                <p></p>
                <table class="list">
                    <tr class="rowHeader">
                        <td class="runSubmitTime">Submit Time</td>
                        <td class="runLanguage">Language</td>
                        <td class="runTime">Run Time(ms)</td>
                        <td class="runMemory">Run Memory(KB)</td>
                        <td class="runUserName">User Name</td>
                    </tr>
                    <%
                    List runs = statistics.getBestRuns();
                    for (int i = 0; i < runs.size(); ++i) {
                         Submission submission = (Submission) runs.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td class="runSubmitTime"><%=Utility.toTimestamp(submission.getSubmitDate())%></td>
                        <td class="runLanguage"><%=submission.getLanguage()%></td>
                        <td class="runTime"><%=submission.getTimeConsumption()%></td>
                        <td class="runMemory"><%=submission.getMemoryConsumption()%></td>
                        <td class="runUserName"><font color="db6d00"><%=submission.getUserName()%></font></td>                      
                    </tr>
                    <%
                    }
                    %>
                </table>
        </div>
        </logic:messagesNotPresent>

