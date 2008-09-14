<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>

                <%
                   UserSecurity currentUser = (UserSecurity) request.getSession().getAttribute("oj_security");
                   if (currentUser != null && currentUser.isSuperAdmin()) {
                %>

                <tr><td class="nav_header">
                    <img src="<%=request.getContextPath()%>/image/arrow_sub2.gif"/><div><a href="<%=request.getContextPath()%>/systemParameters.do">Configure System</a></div>
                </td></tr>

                <logic:equal name="menuId" value="ConfigueSystem">
                <tr><td class="<%="ShowDashBoard".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showActionDashboard.do">Dashboard</a>
                </td></tr>
                <tr><td class="<%="SystemParameters".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/systemParameters.do">System Parameters</a>
                </td></tr>
                <tr><td class="<%="DefaultLimits".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/defaultLimits.do">Default Limits</a>
                </td></tr>

                <tr><td class="<%="ShowRoles".equals(request.getAttribute("pageId")) ||
                                  "ManageRoleUsers".equals(request.getAttribute("pageId")) ||
                                  "EditRole".equals(request.getAttribute("pageId"))                               
                                  ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showRoles.do">Role Management</a>
                </td></tr>

                <tr><td class="<%="Judge".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/judge.do">Judge</a>
                </td></tr>

                <tr><td class="<%="Language".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/language.do">Language</a>
                </td></tr>

                <tr><td class="<%="JudgeReply".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/judgeReply.do">Judge Reply</a>
                </td></tr>
                </logic:equal>

                <tr><td class="nav_header">
                    <img src="<%=request.getContextPath()%>/image/arrow_sub2.gif"/><div><a href="<%=request.getContextPath()%>/manageUsers.do">Manage Users</a></div>
                </td></tr>

                <logic:equal name="menuId" value="ManageUsers">
                <tr><td class="<%="ManageUsers".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/manageUsers.do">Manage Users</a>
                </td></tr>
                </logic:equal>

                <tr><td class="nav_header">
                    <img src="<%=request.getContextPath()%>/image/arrow_sub2.gif"/><div><a href="<%=request.getContextPath()%>/manageContests.do">Manage Contests</a></div>
                </td></tr>

                <logic:equal name="menuId" value="ManageContests">
                <tr><td class="<%="ManageContests".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/manageContests.do">Manage Contests</a>
                </td></tr>
                </logic:equal>

                <tr><td class="nav_header">
                    <img src="<%=request.getContextPath()%>/image/arrow_sub2.gif"/><div><a href="<%=request.getContextPath()%>/manageForums.do">Manage Forums</a></div>
                </td></tr>

                <logic:equal name="menuId" value="ManageForums">
                <tr><td class="<%="ManageForums".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/manageForums.do">Manage Forums</a>
                </td></tr>
                </logic:equal>




                <tr><td class="icpc_logo"><img src="<%=request.getContextPath()%>/image/cpc_acm.jpg"/></td></tr>

                <%
                  } else {
                %>
                <div>:(</div>
                <%
                  }
                %>
