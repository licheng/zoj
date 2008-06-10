<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.RoleSecurity" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.form.RoleForm" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel" %>
<%
RoleForm roleForm = (RoleForm) request.getAttribute("RoleForm");
Map contestNames = (Map) request.getAttribute("ContestNames");
Map forumNames = (Map) request.getAttribute("ForumNames");
%>
<script language="JavaScript">
rowId = 0;
function deletePermission(permisisonType, id) {
    var table = document.getElementById(permisisonType + 'Table');
    for (var i = 0; i < table.rows.length; ++i) {
        if (table.rows[i].id == "row" + id) {
            table.deleteRow(i);
        }
    }       
    
}
function addPermission(permisisonType) {
        
    var message = "";
    
    var cSelect = document.getElementById(permisisonType + 'Select');
    var cid = cSelect.value;    
    if (cid == '-1') {
        message = "Please select a " + permisisonType + ".\n";        
    }   
    var cText = cSelect.options[cSelect.selectedIndex].text;   
    
    var pSelect = document.getElementById(permisisonType + 'PermissionSelect');
    var pid = pSelect.value;        
    if (pid == '-1') {
        message += "Please select a permission.\n";        
    }
    var pText = pSelect.options[pSelect.selectedIndex].text;   
    
    if (message != "") {        
        alert(message);   
        return;     
    }
    
    addPermissionTable(permisisonType, cid, pid, cText, pText);            
}
function addPermissionTable(permisisonType, cid, pid, cText, pText) {
    var cPermisisonType = permisisonType.substring(0, 1).toUpperCase() + permisisonType.substring(1);
    //alert(permisisonType + " " + cid + " " + pid + " " + cText + " " + pText);
    table = document.getElementById(permisisonType + 'Table');
    tr = table.insertRow(table.rows.length);
    tr.id = "row"  + rowId;
    td0 = tr.insertCell(0);
    td0.innerHTML = cText;
    td1 = tr.insertCell(1);
    td1.innerHTML = pText;
    td2 = tr.insertCell(2);
    td2.innerHTML = "<a href=\"javascript:deletePermission('" + permisisonType + "', '" + rowId + "')\")>Delete</a>"        
        + "<input name='selected" + cPermisisonType + "Ids' value='" + cid + "' type='hidden'>" 
        + "<input name='" + permisisonType + "Permissions' value='" + pid + "' type='hidden'>"        
    rowId++;        
}
</script>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_body">
            <form name="RoleForm" method="post" action="<%=request.getContextPath()%>/editRole.do">
            <input name="id" type="hidden" value="<bean:write name="RoleForm" property="id" />">
            <blockquote>
            Role Name <input name="name" type="input" value="<bean:write name="RoleForm" property="name" />">
            <span class="error">*
                <logic:messagesPresent property="name">
                            <html:errors property="name"/>
                </logic:messagesPresent>
            </span><br>
             
            Description <input name="description" type="input" value="<bean:write name="RoleForm" property="description" />"><br>
            
            <br>
            <select id="contestSelect">
                             <option value="-1">--select Contest--</option>
                             <%                             
                                
                                for (Iterator it = contestNames.entrySet().iterator(); it.hasNext();) {
                                    Map.Entry entry = (Map.Entry) it.next();
                             %>
                             <option value="<%=entry.getKey()%>"><%=entry.getValue()%></option>
                             <%
                                }
                             %>
            </select> &nbsp; 
            <select id="contestPermissionSelect">                    
                              <option value="-1">--select Clearance--</option>
                              <option value="<%=PermissionLevel.VIEW.getId()%>">
                                <%=PermissionLevel.VIEW.getDescription()%></option>
                              <option value="<%=PermissionLevel.PARTICIPATE.getId()%>">
                                <%=PermissionLevel.PARTICIPATE.getDescription()%></option>
                              <option value="<%=PermissionLevel.ADMIN.getId()%>">
                                <%=PermissionLevel.ADMIN.getDescription()%></option>                              
            </select> &nbsp; <input type="button" value=" Add " onclick="addPermission('contest')">                         
            <br>  
            <table id="contestTable">
                <tr class="rowHeader">
                    <td width="200">Contest</td>
                    <td width="100">Clearance</td>
                    <td width="50">Admin</td>
                </tr>
                
            </table>
                <script language="JavaScript">
                    <%
                    
                    for (int i = 0; i < roleForm.getSelectedContestIds().length; ++i) {
                        String cid = roleForm.getSelectedContestIds()[i];
                        String pid = roleForm.getContestPermissions()[i];
                        String cText = (String) contestNames.get(Long.valueOf(cid));
                        String pText = PermissionLevel.findById(Long.parseLong(pid)).getDescription();
						if(cText != null)
						{
                    %>
                    addPermissionTable('contest', '<%=cid%>', '<%=pid%>', '<%=cText%>', '<%=pText%>');
                    <%
						}
                    }                    
                    %>
                </script>            
            <br>
            <select id="forumSelect">
                             <option value="-1">--select Forum--</option>
                             <%                             
                                
                                for (Iterator it = forumNames.entrySet().iterator(); it.hasNext();) {
                                    Map.Entry entry = (Map.Entry) it.next();
                             %>
                             <option value="<%=entry.getKey()%>"><%=entry.getValue()%></option>
                             <%
                                }
                             %>
            </select> &nbsp; 
            <select id="forumPermissionSelect">                    
                              <option value="-1">--select Clearance--</option>
                              <option value="<%=PermissionLevel.VIEW.getId()%>">
                                <%=PermissionLevel.VIEW.getDescription()%></option>
                              <option value="<%=PermissionLevel.PARTICIPATE.getId()%>">
                                <%=PermissionLevel.PARTICIPATE.getDescription()%></option>
                              <option value="<%=PermissionLevel.ADMIN.getId()%>">
                                <%=PermissionLevel.ADMIN.getDescription()%></option>                              
            </select> &nbsp; <input type="button" value=" Add " onclick="addPermission('forum')">                         
            <br>  
            <table id="forumTable">
                <tr class="rowHeader">
                    <td width="200">Forum</td>
                    <td width="100">Clearance</td>
                    <td width="50">Admin</td>
                </tr>
                
            </table>
                <script language="JavaScript">
                    <%
                    
                    for (int i = 0; i < roleForm.getSelectedForumIds().length; ++i) {
                        String cid = roleForm.getSelectedForumIds()[i];
                        String pid = roleForm.getForumPermissions()[i];
                        String cText = (String) forumNames.get(Long.valueOf(cid));
                        String pText = PermissionLevel.findById(Long.parseLong(pid)).getDescription();
						if(cText != null)
						{
                    %>
                    addPermissionTable('forum', '<%=cid%>', '<%=pid%>', '<%=cText%>', '<%=pText%>');
                    <%
						}
                    }                    
                    %>
                </script>            
            <br>            
            <br>
            <input value="Edit Role" type="submit">
            <input value="Cancel" type="button">
            </blockquote>
            </form>
        </div>
        </logic:messagesNotPresent>


