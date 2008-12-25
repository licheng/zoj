<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ContestStatistics" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.UserStatistics" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.Set" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ConfigManager" %>
<%
	boolean isProblemset =  "Problems".equals(request.getAttribute("region"));    
    String actionName = isProblemset ? "Problem" : "ContestProblem";
    String actionPath = request.getContextPath() + "/show" 
        + (isProblemset ? "Problems" : "ContestProblems") + ".do";
    String searchactionPath = request.getContextPath() + "/search" 
    + actionName + ".do";
    String problemLink = request.getContextPath() + "/show" + actionName + ".do";
	UserSecurity userSecurity = (UserSecurity) request.getSession().getAttribute("oj_security");
    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
    boolean admin = (userSecurity != null && userSecurity.canAdminContest(contest.getId()));
    String query=request.getAttribute("query").toString();
    int TitleQueryResultCount = Integer.parseInt(request.getAttribute("TitleQueryResultCount").toString());
    int AuthorQueryResultCount = Integer.parseInt(request.getAttribute("AuthorQueryResultCount").toString());
    int SourceQueryResultCount = Integer.parseInt(request.getAttribute("SourceQueryResultCount").toString());
    int titlefrom = Integer.parseInt(request.getAttribute("titlefrom").toString());
    int authorfrom = Integer.parseInt(request.getAttribute("authorfrom").toString());
    int sourcefrom = Integer.parseInt(request.getAttribute("sourcefrom").toString());
%>
<form name="searchForm" id="searchForm" action="<%=searchactionPath %>">
    	<input type="hidden" name="contestId" id="contestId" value="<bean:write name="contest" property="id"/>">
    	<input type="hidden" name="titlefrom" id="titlefrom" value=<%=titlefrom%>>
    	<input type="hidden" name="authorfrom" id="authorfrom" value=<%=authorfrom%>>
    	<input type="hidden" name="sourcefrom" id="sourcefrom" value=<%=sourcefrom%>>
    	<input type="hidden" name="query" value="<%=query %>" />

<div id="content_title"> Search by Tilte (from <%=titlefrom*50+1 %> to <%=(titlefrom*50+49 >TitleQueryResultCount)?TitleQueryResultCount:titlefrom*50+49+1 %> result in all <%=TitleQueryResultCount %> ) </div>
<table class="list">
    <%
    List problems = (List) request.getAttribute("TitleQueryResult");
    %>
    <tr class="rowHeader">            
        <td class="problemId">ID</td>
        <td class="problemTitle">Title</td>
        <% if (admin) { %>
            <td class="problemAdmin">Admin</td>
        <% } %>                     
    </tr>
    <%
    for (int i = 0; i < problems.size(); ++i) {
         Problem problem = (Problem) problems.get(i);
    %>
    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">  
        <% if(contest.getId()!=ConfigManager.getDefaultProblemSetId()) { %>
        <td class="problemId"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getCode()%></font></a></td>
        <td class="problemTitle"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getTitle()%></font></a></td>
        <% } else {%>
        <td class="problemId"><a href="<%=problemLink%>?problemCode=<%=problem.getCode()%>"><font color="blue"><%=problem.getCode()%></font></a></td>
        <td class="problemTitle"><a href="<%=problemLink%>?problemCode=<%=problem.getCode()%>"><font color="blue"><%=problem.getTitle()%></font></a></td>
        <% } %>
        <% if (admin) { %>
        <td class="problemAdmin">
            <a href="<%=request.getContextPath()%>/edit<%=actionName%>.do?problemId=<%=problem.getId()%>"><font color="red">Edit</font></a>
            <a href="javascript:deleteProblem('<%=problem.getCode()%>',<%=problem.getId()%>)"><font color="red">Delete</font></a>
        </td>
        <% } %>                  
    </tr>
    <%
    }
    %>
</table>
<% if(titlefrom>0) {%>
<input type="submit" onclick="document.all['titlefrom'].value='<%=titlefrom-1 %>'" value="Prev Page"/>
<%} %>
<% if(titlefrom*50+49<TitleQueryResultCount) {%>
<input type="submit" onclick="document.all['titlefrom'].value='<%=titlefrom+1 %>'" value="Next Page"/>
<%} %>
<!-- 
<div id="content_title"> Search by Author (from <%=authorfrom*50+1 %> to <%=(authorfrom*50+49 >AuthorQueryResultCount)?AuthorQueryResultCount:authorfrom*50+49+1 %> result in all <%=AuthorQueryResultCount %> )</div>
<table class="list">
    <%
    problems = (List) request.getAttribute("AuthorQueryResult");
    %>
    <tr class="rowHeader">            
        <td class="problemId">ID</td>
        <td class="problemTitle">Title</td>
        <td class="problemTitle">Author</td>
        <% if (admin) { %>
            <td class="problemAdmin">Admin</td>
        <% } %>                     
    </tr>
    <%
    for (int i = 0; i < problems.size(); ++i) {
         Problem problem = (Problem) problems.get(i);
    %>
    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">  
        <% if(contest.getId()!=ConfigManager.getDefaultProblemSetId()) { %>
        <td class="problemId"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getCode()%></font></a></td>
        <td class="problemTitle"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getTitle()%></font></a></td>
        <td class="problemTitle"><font color="blue"><%=problem.getAuthor()%></font></td>
        <% } else {%>
        <td class="problemId"><a href="<%=problemLink%>?problemCode=<%=problem.getCode()%>"><font color="blue"><%=problem.getCode()%></font></a></td>
        <td class="problemTitle"><a href="<%=problemLink%>?problemCode=<%=problem.getCode()%>"><font color="blue"><%=problem.getTitle()%></font></a></td>
        <td class="problemTitle"><font color="blue"><%=problem.getAuthor()%></font></td>
        <% } %>
        <% if (admin) { %>
        <td class="problemAdmin">
            <a href="<%=request.getContextPath()%>/edit<%=actionName%>.do?problemId=<%=problem.getId()%>"><font color="red">Edit</font></a>
            <a href="javascript:deleteProblem('<%=problem.getCode()%>',<%=problem.getId()%>)"><font color="red">Delete</font></a>
        </td>
        <% } %>                  
    </tr>
    <%
    }
    %>
</table>

<% if(authorfrom>0) {%>
<input type="submit" onclick="document.all['authorfrom'].value='<%=authorfrom-1 %>'" value="Prev Page"/>
<%} %>
<% if(authorfrom*50+49<AuthorQueryResultCount) {%>
<input type="submit" onclick="document.all['authorfrom'].value='<%=authorfrom+1 %>'" value="Next Page"/>
<%} %>
 -->
<div id="content_title"> Search by Source (from <%=sourcefrom*50+1 %> to <%=(sourcefrom*50+49 >SourceQueryResultCount)?SourceQueryResultCount:sourcefrom*50+49+1 %> result in all <%=SourceQueryResultCount %> ) </div>
<table class="list">
    <%
    problems = (List) request.getAttribute("SourceQueryResult");
    %>
    <tr class="rowHeader">            
        <td class="problemId">ID</td>
        <td class="problemTitle">Title</td>
        <td class="problemTitle">Source</td>
        <% if (admin) { %>
            <td class="problemAdmin">Admin</td>
        <% } %>                     
    </tr>
    <%
    for (int i = 0; i < problems.size(); ++i) {
         Problem problem = (Problem) problems.get(i);
    %>
    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">  
        <% if(contest.getId()!=ConfigManager.getDefaultProblemSetId()) { %>
        <td class="problemId"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getCode()%></font></a></td>
        <td class="problemTitle"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getTitle()%></font></a></td>
        <td class="problemTitle"><font color="blue"><%=problem.getSource()%></font></td>
        <% } else {%>
        <td class="problemId"><a href="<%=problemLink%>?problemCode=<%=problem.getCode()%>"><font color="blue"><%=problem.getCode()%></font></a></td>
        <td class="problemTitle"><a href="<%=problemLink%>?problemCode=<%=problem.getCode()%>"><font color="blue"><%=problem.getTitle()%></font></a></td>
        <td class="problemTitle"><font color="blue"><%=problem.getSource()%></font></td>
        <% } %>
        <% if (admin) { %>
        <td class="problemAdmin">
            <a href="<%=request.getContextPath()%>/edit<%=actionName%>.do?problemId=<%=problem.getId()%>"><font color="red">Edit</font></a>
            <a href="javascript:deleteProblem('<%=problem.getCode()%>',<%=problem.getId()%>)"><font color="red">Delete</font></a>
        </td>
        <% } %>                  
    </tr>
    <%
    }
    %>
</table>
<% if(sourcefrom>0) {%>
<input type="submit" onclick="document.all['sourcefrom'].value='<%=sourcefrom-1 %>'" value="Prev Page"/>
<%} %>
<% if(sourcefrom*50+49<SourceQueryResultCount) {%>
<input type="submit" onclick="document.all['sourcefrom'].value='<%=sourcefrom+1 %>'" value="Next Page"/>
<%} %>

<form>
