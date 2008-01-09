<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>

                <tr><td class="nav_header">
                    <img src="<%=request.getContextPath()%>/image/arrow_sub2.gif"><div><a href="<%=request.getContextPath()%>/showContests.do">Contests</a></div></img>
                </td></tr>
                <logic:notPresent name="contest">
                <tr><td class="<%="ShowContests".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                        <a href="<%=request.getContextPath()%>/showContests.do">Select Contest</a>
                </td></tr>
                </logic:notPresent>
                <logic:present name="contest">

                <tr><td class="<%="ContestInfo".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/contestInfo.do?contestId=<bean:write name="contest" property="id"/>">Information</a>
                </td></tr>
                <tr><td class="<%="ContestProblems".equals(request.getAttribute("pageId")) || "ShowContestProblem".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showContestProblems.do?contestId=<bean:write name="contest" property="id"/>">Problems</a>
                </td></tr>
                <tr><td class="<%="ContestRuns".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showContestRuns.do?contestId=<bean:write name="contest" property="id"/>">Runs</a>
                </td></tr>
                <tr><td class="<%="ContestStatistics".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showContestStatistics.do?contestId=<bean:write name="contest" property="id"/>">Statistics</a>
                </td></tr>
                <tr><td class="<%="ContestRankList".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showContestRankList.do?contestId=<bean:write name="contest" property="id"/>">Ranklist</a>
                </td></tr>
				<%--
                <tr><td class="<%="Clarification".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="http://acm.zju.edu.cn/forum/viewforum.php?f=10" target="_blank">Clarification</a>
                </td></tr>
				--%>
                <logic:present name="oj_security">
                
                <%
                AbstractContest contest = (AbstractContest) request.getAttribute("contest");
                UserSecurity userSecurity = (UserSecurity) request.getSession().getAttribute("oj_security");                                
                if (userSecurity.canAdminContest(contest.getId())) {
                %>
                <tr><td class="<%="EditContest".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/editContest.do?contestId=<bean:write name="contest" property="id"/>">Edit Contest</a>
                </td></tr>
                <tr><td class="<%="AddProblem".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/addContestProblem.do?contestId=<bean:write name="contest" property="id"/>">Add Problem</a>
                </td></tr>
                <tr><td class="<%="ShowQQ".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showQQs.do?contestId=<bean:write name="contest" property="id"/>">QQ</a>
                </td></tr>
                <%
                }
                %>  
                                
                </logic:present>
                </logic:present>

                <tr><td class="icpc_logo"><img src="<%=request.getContextPath()%>/image/cpc_acm.jpg"/></td></tr>
