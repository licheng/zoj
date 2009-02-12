<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>

<%
    boolean isProblemset =  "Problemsets".equals(request.getAttribute("region"));    
    String name = isProblemset ? "problemset" : "contest";
    String longName = isProblemset ? "Problem Set" : "Contest";
    
%>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title"><bean:write name="contest" property="title"/></div>
        <div id="content_body">
                     <%
                        AbstractContest contest = (AbstractContest) request.getAttribute("contest");
                     %>
                     <table class="contestInfoTable">
                        <tr>
                            <td class="contestInfo"> </td>
                        </tr>
                        <tr>
                          <td class="contestInfo">Description:</td>
                          <td><bean:write name="contest" property="description"/></td>
                        </tr>
                        <%
                        if (contest.getStartTime() != null) {                          
                        %>
                        <tr>
                          <td class="contestInfo">Start Time:</td>
                          <td>
                          <a class="dateLink" target="_blank" href="<%=Utility.toTimeUrl(contest.getStartTime())%>"><font color=blue><%=Utility.toTimestampWithTimeZone(contest.getStartTime())%></font></a>
                          </td>
                        </tr>
                        <tr>
                          <td class="contestInfo">Length:</td>
                          <td><%=Utility.toTextTime(contest.getLength() / 1000)%></td>
                        </tr>                        
                        <%
                        }
                        %>
                          

                        <tr>
                          <td class="contestInfo">Status:</td>
                          <%
                          String contestStatus = "Always Run";
                          long current = System.currentTimeMillis();
                          if (contest.getStartTime() != null) {                          
                            
                            if (current < contest.getStartTime().getTime()) {
                               contestStatus = "Waiting";
                            } else if (current > contest.getEndTime().getTime()) {
                                contestStatus = "Finished";
                            } else {
                                contestStatus = "Running";
                            }
                          }
                          %>
                          <td><%=contestStatus%></td>
                        </tr>
                        <%
                        if ("Waiting".equals(contestStatus)) {
                        %>
                        <tr>
                          <td class="contestInfo">Countdown:</td>
                          <td>
                            <%=Utility.toTextTime((contest.getStartTime().getTime() - current) / 1000)%>
                          </td>
                        </tr>
                        <%
                        }
                        %>
                        <tr>
                            <td class="contestInfo"> </td>
                        </tr>
                      </table>
        </div>
        </logic:messagesNotPresent>

