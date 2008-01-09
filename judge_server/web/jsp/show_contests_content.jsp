<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problemset" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>

<%
    boolean isProblemset =  "Problems".equals(request.getAttribute("region"));    
    String actionPath = request.getContextPath() + "/" +  (isProblemset ? "showProblemsets.do" : "showContests.do");
    String name = isProblemset ? "problemset" : "contest";
    String longName = isProblemset ? "Problem Set" : "Contest";
    
%>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <div id="content_body">
            <form name="ContestListForm" method="get" action="<%=actionPath%>">
                <table class="list">
                    <tr class="rowHeader">
                        <td class="contestNameHeader"><%=longName%> Name</td>
                        <td class="contestStatusHeader">Status</td>
                    </tr>
                    <%
                    List contests = (List) request.getAttribute("contests");
                    for (int i = 0; i < contests.size(); ++i) {
                        
                        AbstractContest contest = (AbstractContest) contests.get(i);
                        String contestStatus = "<font color=\"red\">Always run</font>";
                        String problemLink = isProblemset ? "showProblems.do" : "showContestProblems.do";
                        String startDate = "";
                        long current = System.currentTimeMillis();
                         
                        if (contest.getStartTime() != null) {
                            if (current < contest.getStartTime().getTime()) {
                                problemLink = name + "Info.do";
                                contestStatus = "Starts at ";
                                startDate = Utility.toTimestampWithTimeZone(contest.getStartTime());
                            } else if (current > contest.getEndTime().getTime()) {                                
                                contestStatus = "Finished";
                            } else {
                                contestStatus = "<font color=\"red\">Running</font>";
                            }
                        }

                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                      <td class="contestName"><a href="<%=request.getContextPath()%>/<%=problemLink%>?contestId=<%=contest.getId()%>"><font color="blue"><%=contest.getTitle()%></font></a></td>
                      <td class="contestStatus"><%=contestStatus%>
                    <% 
                    if (startDate.length() > 0) {
                    %>
                        <a class="dateLink" target="_blank" href="<%=Utility.toTimeUrl(contest.getStartTime())%>"><font color=blue><%=startDate%></font></a>
                    <% 
                    }
                    %>
                      </td>
                    </tr>
                    <%
                    }
                    %>
                    <tr/>
                    <%
                        Long totalPages = (Long) request.getAttribute("totalPages");
                        if (totalPages != null && totalPages.longValue() > 1) {
                    %>
                    <tr>
                        <td class="contestName">
                          Page <input id="pageNumber" name="pageNumber" value="<bean:write name="pageNumber"/>"/> of <span id="totalPages"><bean:write name="totalPages"/> </span>Pages
                          <input id="pageButton" type="submit" value="Go!"/>
                        </td>
                        <td class="contestStatus">First Previous Next Last</td>
                    </tr>
                    <%
                        }
                    %>
                </table>
            </form>
        </div>


