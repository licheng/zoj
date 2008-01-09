<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>
                <tr><td class="nav_header">
                    <img src="<%=request.getContextPath()%>/image/arrow_sub2.gif"><div><a href="<%=request.getContextPath()%>/showProblemsets.do">Problem Sets</a></div></img>
                </td></tr>
                <logic:notPresent name="contest">
                <tr><td class="<%="ShowProblemsets".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                        <a href="<%=request.getContextPath()%>/showProblemsets.do">Select Problem Set</a>
                </td></tr>
                </logic:notPresent>
                <logic:present name="contest">
                    
                
                <tr><td class="<%="ProblemsetInfo".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/problemsetInfo.do?contestId=<bean:write name="contest" property="id"/>">Information</a>
                </td></tr>
                <tr><td class="<%="Problems".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showProblems.do?contestId=<bean:write name="contest" property="id"/>">Select Problem</a>
                </td></tr>
                <!--
                <tr><td class="<%="SearchProblem".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/searchProblem.do?contestId=<bean:write name="contest" property="id"/>">Search Problem</a>
                </td></tr>
                -->
                <tr><td class="<%="ProblemsetRuns".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showRuns.do?contestId=<bean:write name="contest" property="id"/>">Runs</a>
                </td></tr>
                <tr><td class="<%="ProblemsetRankList".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showRankList.do?contestId=<bean:write name="contest" property="id"/>">Ranklist</a>
                </td></tr>
                <!--
                <tr><td class="<%="Clarification".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/clarification.do?contestId=<bean:write name="contest" property="id"/>">Clarification</a>
                </td></tr>
                -->
                <logic:present name="oj_security">
                <%
                AbstractContest contest = (AbstractContest) request.getAttribute("contest");
                UserSecurity userSecurity = (UserSecurity) request.getSession().getAttribute("oj_security");                                
                if (userSecurity.canAdminContest(contest.getId())) {
                %>
                <tr><td class="<%="EditProblemset".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/editProblemset.do?contestId=<bean:write name="contest" property="id"/>">Edit Problem Set</a>
                </td></tr>
                <tr><td class="<%="AddProblem".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/addProblem.do?contestId=<bean:write name="contest" property="id"/>">Add Problem</a>
                </td></tr>
                <%
                }
                %>   
                </logic:present>
                </logic:present>

                <tr><td class="icpc_logo"><img src="<%=request.getContextPath()%>/image/cpc_acm.jpg"/></td></tr>
