<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <div id="content_title">Forgot Password</div>
        <div id="content_body">
        
        <blockquote>
          <br/>
          <br/>
          <%
          String handle = (String) request.getAttribute("handle");
          String email = (String) request.getAttribute("email");
          Boolean ok = (Boolean) request.getAttribute("ok");
          if (((handle != null && handle.trim().length() > 0) ||
              (email != null && email.trim().length() > 0)) && ok == Boolean.TRUE) {
          %>
                <p>An email had been sent to your email.</p>
          <% } else { %>
            <p>Fill your handle blow. We will send an email to you.</p>
            <form name="forgotPasswordForm" method="post" action="<%=request.getContextPath()%>/forgotPassword.do">
            <p>
                <input name="handle" type="text"/><br/>
                Or<br/>
                <input name="email" type="text"/><br/>
                <% if (ok == Boolean.FALSE) { %>
                <font color="red">no such user.</font>
                <% } %> 
                <input type="submit" value="Submit"/>
            </p>
            </form>            
          <% } %>
        </blockquote>
        </div>
        