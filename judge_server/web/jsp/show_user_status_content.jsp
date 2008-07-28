<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.bean.UserProfile" %>
<%@ page import="java.util.Set" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.RankListEntry" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>

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
        <table class="profileTable">
        <tr>
        <td>
        <table>
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
        <%for(Object o : solved) { 
        	Problem p=(Problem)o;
        %>
        &nbsp;<a href="/showProblems.do?problemId=<%=p.getId() %>"><font color=blue><%=p.getCode() %></font></a>
        <%} %>
        </tr>
        </table>
        </blockquote>
</div>