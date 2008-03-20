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
        <div id="content_title">Reset Password</div>
<div id="content_body">
      <form name="resetPasswordForm" method="post" action="<%=request.getContextPath()%>/resetPassword.do">
      <input name="code" type="hidden" value="<bean:write name="ResetPasswordForm" property="code"/>" />
        <blockquote>
        <br/>
        <br/>
        <table class="profileTable">
            <tr>
                <td align="right">Password</td>             
                <td>                                      
                    <input name="password" type="password" value="" />
                    <span class="error">*
                        <logic:messagesPresent property="password">
                            <html:errors property="password"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Confirm Password</td>             
                <td>
                    <input name="confirmPassword" type="password" value=""/>
                    <span class="error">*
                        <logic:messagesPresent property="confirmPassword">
                            <html:errors property="confirmPassword"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
          </table>
        </blockquote>
    <div class="profileButtons">
        <div class="profileButton">
            <input class="button" name="profile" type="submit" id="profile" value="Submit">
        </div>
        <div class="profileButton">
            <input class="button" name="cacel" type="reset" id="cacel" value="Reset">
        </div>
    </div>
</form>
</div>
        