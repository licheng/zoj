<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Features" %>
        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <div id="content_title">Login</div>
        <div id="content_body">
            <br>
            <br>
            <form name="loginForm" method="post" action="<%=request.getContextPath()%>/login.do">
                <logic:present name="forward">
                <input name="forward" type="hidden" value="<bean:write name="forward"/>"/>
                </logic:present>
                <%--
                    <logic:messagesPresent property="reason">
                        <font color="red"><html:errors property="reason"/></font>
                    </logic:messagesPresent>
                --%>
                <blockquote>
                <table class="profileTable">
                  <tr>
                      <td align="right">Handle</td>             
                      <td>                                      
                          <input name="handle" type="text"/
                                <logic:present name="LoginForm">
                                    value="<bean:write name="LoginForm" property="handle" />"
                                </logic:present>
                            >
                            <span class="error">
                                <logic:messagesPresent property="handle">
                                    <html:errors property="handle"/>
                                </logic:messagesPresent>
                            </span>
                      </td>
                  </tr>
                  <tr>
                      <td align="right">Password</td>             
                      <td>                                      
                          <input name="password" type="password" value="">
                            <span class="error">
                                <logic:messagesPresent property="password">
                                    <html:errors property="password"/>
                                </logic:messagesPresent>
                            </span>
                      </td>
                  </tr>
                  <tr>
                      <td align="right"></td>             
                      <td>                                      
                          <span><input type="checkbox" name="rememberMe" style="width:20px"/>Remember me</span>
                      </td>
                  </tr>
                  </table>
                  </blockquote>
    <div class="profileButtons">
        <div class="profileButton">
            <input type="submit" value="Login">
        </div>
    </div>                  
            </form>
            
            <br/>
            <blockquote>
            <% if (Features.forgotPassword()) {%>
            <b>Forgot your password?</b> <a href="<%=request.getContextPath()%>/forgotPassword.do">Click here</a><br/>
            <% } %>
            <% if (Features.register()) {%>
            <b>New to ZOJ?</b> <a href="<%=request.getContextPath()%>/register.do">Register now</a>
            <% } %>
            </blockquote>
        </div>
