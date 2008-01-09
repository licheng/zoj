<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.RoleSecurity" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.util.RankListEntry" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.RankList" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>

<%
		RankList ranklist = (RankList) request.getAttribute("RankList");
        List entries = ranklist.getEntries();
        List problems = (List) request.getAttribute("problems");
    	List roles = ranklist.getRoles();
    	RoleSecurity role = ranklist.getRole();
%>
        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title">
            <div id="export">
                <a href="<%=request.getContextPath()%>/showContestRankList.do?export=txt&contestId=<bean:write name="contest" property="id"/><%=role==null?"":"&roleId="+role.getId()%>">[Export to txt]</a>
                <a href="<%=request.getContextPath()%>/showContestRankList.do?export=xls&contestId=<bean:write name="contest" property="id"/><%=role==null?"":"&roleId="+role.getId()%>">[Export to xls]</a>
            </div>
            
            <bean:write name="contest" property="title"/><%=role==null?"": " - " + role.getDescription()%>
        </div>
        <div id="content_body">
                <%
                    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
                    String status;
                    long time = System.currentTimeMillis() - contest.getStartTime().getTime();
                    if (time < 0) {
                        status = "Inactive";
                    } else if (time > contest.getLength()) {
                        status = "Final Standing";
                    } else {
                        String contestLength = Utility.toTextTime(contest.getLength() / 1000);
                        String timeElapsed = Utility.toTextTime(time / 1000);
                        status = "Contest Length: " + contestLength +  " &nbsp; &nbsp; Time Elapsed: " + timeElapsed;
                    }                    
                %>
				<br/>                
                <%
				if (roles != null && roles.size() > 1) {
				%>
	                <a href="<%=request.getContextPath()%>/showContestRankList.do?contestId=<bean:write name="contest" property="id"/>">All</a>	                
					<%
					for (int i = 0; i < roles.size(); ++i) {
						RoleSecurity r = (RoleSecurity) roles.get(i);
					%>
		            <a href="<%=request.getContextPath()%>/showContestRankList.do?contestId=<bean:write name="contest" property="id"/>&roleId=<%=r.getId()%>"><%=r.getDescription()%></a>
					<%
					}
					%>
				<%
				}
				%>
				<br/>
                <font color="green" size="3"><%=status%></font><br>
                <table class="list">
                    <tr class="rowHeader">
                        <td class="ranklistRank">Rank</td>
                        <td class="ranklistUser">Name</td>
                        <td class="ranklistSolved">Solved</td>
                        <logic:iterate id="problem" name="problems">
                            <td class="ranklistProblem"><bean:write name="problem" property="code"/></td>
                        </logic:iterate>
                        <td class="ranklistPenalty">Penalty</td>
                    </tr>
                    <%
                    int rank = 0;
                    int count = 1;
                    int lastSolved = -1;
                    int lastPen = -1;
                    for (int i = 0; i < entries.size(); ++i) {
                         RankListEntry entry = (RankListEntry) entries.get(i);
                         String displayRank = "";                         
                         if (!entry.getUserProfile().getNickName().endsWith("***")) {
	                         if (entry.getPenalty() == lastPen && entry.getSolved() == lastSolved) {
	                      		count++;
	                         } else {
	                         	rank += count;
	                         	count = 1;
	                         }
	                         displayRank = "" + rank;
	                         
 	                         lastSolved = entry.getSolved();
    	                     lastPen = entry.getPenalty(); 
                         }
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td class="ranklistRank"><%=displayRank%></td>
                        <td class="ranklistUser"><font color="db6d00"><%=
                        	entry.getUserProfile().getNickName() == null ||
                        	entry.getUserProfile().getNickName().length() == 0
                        	?  entry.getUserProfile().getHandle() : entry.getUserProfile().getNickName()
                        %></font></td>
                        <td class="ranklistSolved"><%=entry.getSolved()%></td>
                        <%
                        for (int j = 0; j < problems.size(); ++j) {
                            String acceptTime;
                            if (entry.getAcceptTime(j) >= 0) {
                                acceptTime = entry.getAcceptTime(j) + " (" + entry.getSubmitNumber(j) + ")";
                            } else {
                                acceptTime = "" + entry.getSubmitNumber(j);
                            }
                        %>
                        <td class="ranklistProblem"><%=acceptTime%></td>
                        <%
                        }
                        %>
                        <td class="ranklistPenalty"><%=entry.getPenalty()%></td>
                    </tr>
                    <%
                    }
                    %>
                </table>

        </div>
        </logic:messagesNotPresent>

