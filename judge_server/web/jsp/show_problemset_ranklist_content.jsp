<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.*" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.RoleSecurity" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.util.RankListEntry" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.RankList" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>

<%
        Object srank=request.getAttribute("from");
        int rank = (srank==null)? 0:Integer.parseInt(srank.toString());
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
        <div id="content_body">
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
                <table class="list">
                    <tr class="rowHeader">
                        <td class="ranklistRank">Rank</td>
                        <td class="ranklistUser">Name</td>
                        <td class="ranklistUser">Declaration</td>
                        <td class="ranklistSolved">Solved</td>
                        <td class="ranklistSolved">Submitted</td>
                        <td class="ranklistPenalty">AC Ratio</td>
                    </tr>
                    <%
                    int count = 1;
                    int lastSolved = -1;
                    int lastsub = -1;
                    for (int i = 0; i < entries.size(); ++i) {
                         RankListEntry entry = (RankListEntry) entries.get(i);
                         String displayRank = "";                         
                         if (!entry.getUserProfile().getNickName().endsWith("***")) {
	                         if (entry.getSubmitted() == lastsub && entry.getSolved() == lastSolved) {
	                      		count++;
	                         } else {
	                         	rank += count;
	                         	count = 1;
	                         }
	                         displayRank = "" + rank;
	                         
 	                         lastSolved = entry.getSolved();
    	                     lastsub = entry.getPenalty(); 
                         }
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td class="ranklistRank"><%=displayRank%></td>
                        <td class="ranklistUser"><font color="db6d00"><%=
                        	entry.getUserProfile().getNickName() == null ||
                        	entry.getUserProfile().getNickName().length() == 0
                        	?  entry.getUserProfile().getHandle() : entry.getUserProfile().getNickName()
                        %></font></td>
                        <% String declaration=entry.getUserProfile().getDeclaration();
                          if(declaration==null)declaration=""; %>
                        <td class="ranklistUser"><%=declaration %></td>
                        <td class="ranklistSolved"><%=entry.getSolved()%></td>
                        <td class="ranklistSolved"><%=entry.getSubmitted()%></td>
                        <td class="ranklistPenalty"><%=(new DecimalFormat("0.##")).format(entry.getACRatio()*100)+"%"%></td>
                    </tr>
                    <%
                    }
                    %>
                </table>

        </div>
        </logic:messagesNotPresent>

