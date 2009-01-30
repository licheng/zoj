<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>

<%
    boolean isProblemset =  "Problems".equals(request.getAttribute("region")); 
    boolean isCourse =  "Courses".equals(request.getAttribute("region"));    
    String submitPath = isProblemset ? "submit.do" : (isCourse? "courseSubmit.do":"contestSubmit.do");
    
%>
        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title"><bean:write name="contest" property="title"/> - <bean:write name="problem" property="code"/></div>
        <div id="content_body">
            <center><span class="bigProblemTitle"><bean:write name="problem" property="title"/></span></center>
            <hr>
            <center>
                <font color="green">Time Limit: </font> <bean:write name="problem" property="limit.timeLimit"/> Second<logic:greaterThan name="problem" property="limit.timeLimit" value="1">s</logic:greaterThan>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <font color="green">Memory Limit: </font> <bean:write name="problem" property="limit.memoryLimit"/> KB
                <logic:equal name="problem" property="checker" value="true">
                &nbsp;&nbsp;&nbsp;&nbsp;
                <font color="blue">Special Judge</font>
                </logic:equal>
            </center>
            <hr>
            <%=new String((byte[]) request.getAttribute("text"))%>
            <hr>
            <logic:notEmpty name="problem" property="author">
                Author: <strong><bean:write name="problem" property="author"/></strong><br>
            </logic:notEmpty>
            <logic:notEmpty name="problem" property="source">
                Source: <strong><bean:write name="problem" property="source"/></strong><br>
            </logic:notEmpty>
            <logic:notEmpty name="problem" property="contest">
                Contest: <strong><bean:write name="problem" property="contest"/></strong><br>
            </logic:notEmpty>
            <center>
                <a href="<%=request.getContextPath() + "/" + submitPath%>?problemId=<bean:write name="problem" property="id"/>"><font color="blue">Submit</font></a>
                <% if(!isCourse){ %>
                &nbsp;&nbsp;
                <a href="<%=request.getContextPath() + (isProblemset ? "/showProblemStatus.do" : "/showContestProblemStatus.do")%>?problemId=<bean:write name="problem" property="id"/>"><font color="blue">Status</font></a>
                <%--
                &nbsp;&nbsp;&nbsp;&nbsp;<font color="blue">Status</font>
                --%>
                <%} %>
            </center>
        </div>
        </logic:messagesNotPresent>

