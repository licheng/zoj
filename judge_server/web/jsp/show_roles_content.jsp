<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.RoleSecurity" %>

<script language="JavaScript">
function deleteRole(roleId) {
    if (!confirm("Are you sure to delete this role?")) {
      return;
    }
    location.href="<%=request.getContextPath()%>/deleteRole.do?roleId=" + roleId;
}
</script>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title">Manage Roles</div>
        <div id="content_body">
            <table class="list">
                <tr class="rowHeader">
                    <td class="roleNameHeader">Role Name</td>
                    <td class="roleDescriptionHeader">Description</td>
                    <td class="roleAdminHeader">Admin</td>
                </tr>
                <%
                List roles = (List) request.getAttribute("Roles");
                for (int i = 0; i < roles.size(); ++i) {
                     RoleSecurity role = (RoleSecurity) roles.get(i);                         
                %>
                <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                  <td class="roleName"><a href="<%=request.getContextPath()%>/editRole.do?roleId=<%=role.getId()%>"><%=role.getName()%></a></td>
                  <td class="roleDescription"><a href="<%=request.getContextPath()%>/editRole.do?roleId=<%=role.getId()%>"><%=role.getDescription()%></a></td>
                  <td class="roleAdmin">
                  <a href="javascript:deleteRole('<%=role.getId()%>')">Delete</a>
                  &nbsp;&nbsp;
                  <a href="<%=request.getContextPath()%>/manageRoleUsers.do?roleId=<%=role.getId()%>">Role Users</a></td>
                </tr>
                <%
                }
                %>                                                            
            </table>
            <br>
            <form name="RoleForm" method="post" action="<%=request.getContextPath()%>/addRole.do">
                Role Name <input name="name" type="input">
                Description <input name="description" type="input">
                <input value="Add Role" type="submit">
            </form>
        </div>
        </logic:messagesNotPresent>


