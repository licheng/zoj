<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.bean.UserProfile" %>
<%@ page import="java.util.Set" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.RankListEntry" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.PersistenceManager" %>

<%
    Set solved=(Set)request.getAttribute("solved");
    UserProfile user=(UserProfile)request.getAttribute("user");
    RankListEntry re=(RankListEntry)request.getAttribute("RankListEntry");
%>


    <logic:messagesPresent property="error">
    <div class="internalError">
        <html:errors property="error"/>
    </div>
    </logic:messagesPresent>

<div id="content_body">
        <blockquote>
        <table class="profileTable" border="1">
        <tr>
        <td>
        <table border="1">
        <center><font color=blue size="+2"><%=user.getHandle() %></font></center>
        <tr>
        <% String nick=user.getNickName(); %>
        <td>Nick Name</td><td><%=(nick==null)? user.getHandle() : nick %><td></tr>
        <tr><td>E-mail</td><td><%=user.getEmail() %></td></tr>
        <tr><td>Plan</td><td><%=user.getDeclaration() %></td><tr>
        <tr><td>Total Submit</td><td><%=re.getSubmitted() %></td><tr>
        <tr><td>Total Solved</td><td><%=re.getSolved() %></td><tr>
        </table>
        </td>
        <td width=60% align=left rowspan=4>
        <% for(java.util.Iterator it=solved.iterator();it.hasNext();) {
            Problem p = PersistenceManager.getInstance().getProblemPersistence().getProblem(Long.parseLong(it.next().toString())); %>
               &nbsp; <a href="<%=request.getContextPath()%>/showProblem.do?contestId=1&problemId=<%=p.getId()%>"><font color="blue"><%=p.getCode()%></font></a>

        <% } %>
        </td>
        </tr>
        </table>
        </blockquote>
</div>
