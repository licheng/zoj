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
        <div id="content_title">Login</div>
        <div id="content_body">
            <br>
            <br>
            <form name="loginForm" method="post" action="<%=request.getContextPath()%>/login.do">
                <logic:present name="forward">
                <input name="forward" type="hidden" value="<bean:write name="forward"/>"/>
                </logic:present>
                <ul class="profileFields">
                    <logic:messagesPresent property="reason">
                    <li>
                        <font color="red"><html:errors property="reason"/></font>
                    </li>
                    </logic:messagesPresent>
                    <li>
                        <div class="profileFieldName">Handle</div>
                        <div class="profileField">
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
                        </div>
                    </li>

                    <li>
                        <div class="profileFieldName">Password</div>
                        <div class="profileField">
                            <input name="password" type="password" value="">
                            <span class="error">
                                <logic:messagesPresent property="password">
                                    <html:errors property="password"/>
                                </logic:messagesPresent>
                            </span>
                        </div>
                    </li>

                    <li>
                        <div class="profileFieldName"></div>
                        <%--
                        <div class="profileFieldCheckBox">
                            <input type="checkbox" name="rememberMe" value="checkbox" />Remember Me
                        </div>
                        --%>
                    </li>
                    <li/>
                    <li>
                        <div class="profileFieldName"></div>
                        <div class="profileButton">
                            <input type="submit" value="Login"/>
                        </div>
                    </li>
                </ul>
            </form>
            <%--
            <p><br><b>Forgot your password?</b> <a href="#">Click here</a>.<br>
            <b>New to ZOJ?</b> <a href="<%=request.getContextPath()%>/register.do">Register now</a>.</p>
            --%>
        </div>
