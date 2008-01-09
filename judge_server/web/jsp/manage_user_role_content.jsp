<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.UserProfile" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.RoleSecurity" %>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title">User Roles</div>
        <div id="content_body">                                
            <form name="AddUserRoleForm" method="POST" action="<%=request.getContextPath()%>/addUserRole.do">            
                <input type="hidden" name="userId" value="<bean:write name="User" property="id"/>">
                <blockquote>
                <select name="roleId" class="userSearchSelectBox">
                <%
                UserProfile user = (UserProfile) request.getAttribute("User");
                List userRoles = (List) request.getAttribute("UserRoles");
                List roles = (List) request.getAttribute("Roles");
                int roleCount = 0;
                for (int i = 0; i < roles.size(); ++i) {
                    RoleSecurity role = (RoleSecurity) roles.get(i);
                    int j = 0;
                    for (; j < userRoles.size() && role.getId() != ((RoleSecurity) userRoles.get(j)).getId(); ++j);
                    if (j == userRoles.size() && role.getId() > 1) {
                    roleCount++;
                %>
                    <option value="<%=role.getId()%>"><%=role.getName()%></option>
                <%
                    }
                }
                %>
                </select> <input type="submit" value="Add Role" <%=roleCount>0?"":"DISABLED"%> >
                </blockquote>
                <table>
                    <tr class="rowHeader">
                        <td width="80" align="center">Role Name</td>
                        <td width="200" align="center">Description</td> 
                        <td width="70" align="center">Admin</td>                       
                    </tr>
                    <%
                    for (int i = 0; i < userRoles.size(); ++i) {
                         RoleSecurity role = (RoleSecurity) userRoles.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td align="center"><%=role.getName()%></td>
                        <td align="center"><%=role.getDescription()%></td>
                        <td align="center">
                            <a href="<%=request.getContextPath()%>/deleteUserRole.do?userId=<%=user.getId()%>&roleId=<%=role.getId()%>">Delete</a>
                        </td>                        
                    </tr>
                    <%
                    }
                    %>
                </table>                
        </div>
        </logic:messagesNotPresent>
