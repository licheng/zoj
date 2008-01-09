<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

        <logic:messagesPresent property="error">
        lala
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
<script language="javascript">
function submit(remove) {
    var message = "";
    if (remove) {
        message = "Are you sure to remove those users from this role?";
    } else {
        message = "Are you sure to add those users to this role?";
    }
    if (!confirm(message)) {
      return;
    }
    if (remove) {
        document.RoleUsersForm.operation.value = 'remove';
    } else {
        document.RoleUsersForm.operation.value = 'add';
    }    
    document.RoleUsersForm.submit();
} 
</script>          
        <div id="content_title"><bean:write name="role" property="name" /></div>
          
        <div id="content_body">
            <form name="RoleUsersForm" method="post" action="<%=request.getContextPath()%>/manageRoleUsers.do">
            <input name="roleId" type="hidden" value="<bean:write name="role" property="id"/>">
            <input name="operation" type="hidden" value="">
            <blockquote>
            <bean:write name="role" property="description" /><br>
            <br>
            <a href="<%=request.getContextPath()%>/manageUsers.do?roleId=<bean:write name="role" property="id"/>">Show Users</a>
            <br><br>
            Enter the users here, one handle per line.<br>
            <textarea name="users" cols="30" rows="15"></textarea>
            <br>
            <a href="JavaScript: submit(false)">Add Role Users</a>            
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <a href="JavaScript: submit(true)">Remove Role Users</a>
            <br>
            <br>
            
            
            <%=request.getAttribute("importMessage")%>
                                
            </blockquote>
            </form>
            
        </div>
        </logic:messagesNotPresent>


