<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ProblemStatistics" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<% Problem p = (Problem)request.getAttribute("Problem"); %>
        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_body">
                <center><font color=blue size="+2"><%=p.getTitle() %></font></center>
                <table class="list">

                    <tr class="rowHeader">
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
                        <td class="statisticsSubmit">Total</td>
                    </tr>
                    <tr class="rowOdd">
                    <%
                    ProblemStatistics statistics = (ProblemStatistics) request.getAttribute("ProblemStatistics");
                     %>
                        <td class="statisticsAC"><%=statistics.getCount(0)%></td>
                        <td class="statisticsWA"><%=statistics.getCount(1)%></td>
                        <td class="statisticsPE"><%=statistics.getCount(2)%></td>
                        <td class="statisticsRTE"><%=statistics.getCount(3)%></td>
                        <td class="statisticsFPE"><%=statistics.getCount(4)%></td>
                        <td class="statisticsSF"><%=statistics.getCount(5)%></td>
                        <td class="statisticsTLE"><%=statistics.getCount(6)%></td>
                        <td class="statisticsMLE"><%=statistics.getCount(7)%></td>
                        <td class="statisticsOLE"><%=statistics.getCount(8)%></td>
                        <td class="statisticsCE"><%=statistics.getCount(9)%></td>
                        <td class="statisticsSubmit"><%=statistics.getTotal()%></td>
                    </tr>
                </table>
                <p></p>
        </div>
        </logic:messagesNotPresent>

