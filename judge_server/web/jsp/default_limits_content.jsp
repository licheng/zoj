<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
                <div id="content_title">Default Limits</div>
                <div id="content_body">
                   
                <br>
                <form method="POST" action="<%=request.getContextPath()%>/defaultLimits.do">
                <input type="hidden" name="id" value="<bean:write name="LimitForm" property="id"/>">
                <blockquote>
                <table>
                <tr>
                    <td width=100>Time Limit</td>
                    <td width=300>
                        <input type="text" name="timeLimit" value="<bean:write name="LimitForm" property="timeLimit"/>">
                        <span class="error">s *
                        <logic:messagesPresent property="timeLimit">
                            <html:errors property="timeLimit"/>
                        </logic:messagesPresent>
                        </span>
                    </td>
                </tr>
                <tr>                        
                    <td width=100>Memory Limit</td>
                    <td width=300>
                        <input type="text" name="memoryLimit" value="<bean:write name="LimitForm" property="memoryLimit"/>">
                        <span class="error">KB *
                        <logic:messagesPresent property="memoryLimit">
                            <html:errors property="memoryLimit"/>
                        </logic:messagesPresent>
                        </span>
                    </td>
                </tr>
                <tr>
                    <td width=100>Output Limit</td>
                    <td width=300>
                        <input type="text" name="outputLimit" value="<bean:write name="LimitForm" property="outputLimit"/>">
                        <span class="error">KB *
                        <logic:messagesPresent property="outputLimit">
                            <html:errors property="outputLimit"/>
                        </logic:messagesPresent>
                        </span>
                    </td>
                </tr>
                <tr>
                    <td width=100>Submission Limit</td>
                    <td width=300>
                        <input type="text" name="submissionLimit" value="<bean:write name="LimitForm" property="submissionLimit"/>">
                        <span class="error">KB *
                        <logic:messagesPresent property="submissionLimit">
                            <html:errors property="submissionLimit"/>
                        </logic:messagesPresent>
                        </span>
                    </td>
                </tr>
                </table>  
                <input type="submit" value=" Edit ">
                </form>          
                <span class="error">
                        <logic:messagesPresent property="success">
                            <html:errors property="success"/>
                        </logic:messagesPresent>
                </span>
                <br>
                </blockquote>                  
                </div>