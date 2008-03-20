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
          <logic:notEmpty name="handle">
            <p>An email had been sent to your email.</p>
          </logic:notEmpty>
          <logic:empty name="handle">
            <p>Fill your handle blow. We will send an email to you.</p>
            <form name="forgotPasswordForm" method="post" action="<%=request.getContextPath()%>/forgotPassword.do">
            <p>
                <input name="handle" type="text"/>
                <input type="submit" value="Submit"/>
            </p>
            </form>            
          </logic:empty>
        </blockquote>
        </div>
        