<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%
    boolean isProblemset =  "Problems".equals(request.getAttribute("region"));    
    String actionPath = isProblemset ? "showRuns.do" : "showContestRuns.do";    
    
%>
        <div id="content_title">Submit Successfully</div>
        <div id="content_body">
            <p>Your source has been submitted. The submission id is <font color='red'><bean:write name="contestOrder"/></font>. Please check the <a href="<%=request.getContextPath() + "/" + actionPath%>?contestId=<bean:write name="contest" property="id"/>"><font color="blue">status</font></a> page.</p>    
        </div>
        
        