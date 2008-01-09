<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.UserProfile" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.RoleSecurity" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.form.UserSearchForm" %>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title"><div id="export">
            <a href="JavaScript: exportUserList('txt')">[Export to txt]</a>
            <a href="JavaScript: exportUserList('xls')">[Export to xls]</a>            
            </div>
            User Search</div>
        <div id="content_body">            
            <%
                        
                UserSearchForm userSearchForm = (UserSearchForm) request.getAttribute("UserSearchForm");
                String selectedCountryId = "";
                if (userSearchForm != null && userSearchForm.getCountryId() != null) {
                    selectedCountryId = userSearchForm.getCountryId();
                }
                String selectedRoleId = "";
                if (userSearchForm != null && userSearchForm.getRoleId() != null) {
                    selectedRoleId = userSearchForm.getRoleId();
                }
            %>
                    
            <form name="UserSearchForm" method="POST" action="<%=request.getContextPath()%>/manageUsers.do">            
            <input type="hidden" name="exportFormat" value="">                                       
            <input type="hidden" name="search" value="<bean:write name="UserSearchForm" property="search"/>">
                <div id="searchUsersLink">
                    <a href="JavaScript: showSearch(true);">Search</a>
                </div>
                <div id="searchUsersParameter">
                    <a href="JavaScript: showSearch(false);">Hide Search Form</a><br>
                    
                    <table id="searchTable" >
                        <tr>
                            <td width="60" align="right">Handle</td>
                            <td width="100" align="left">
                                <input name="handle" type="text" class="userSearchSelectBox"
                                    value="<bean:write name="UserSearchForm" property="handle" />"
                                >
                            </td>
                            <td width="100" align="right">First Name</td>
                            <td width="100" align="left">
                                <input name="firstName" type="text" class="userSearchSelectBox"
                                    value="<bean:write name="UserSearchForm" property="firstName" />"
                                >
                            </td>
                        </tr>     
                        <tr>
                            <td width="60" align="right">Email</td>
                            <td width="100" align="left">
                                <input name="email" type="text" class="userSearchSelectBox"
                                    value="<bean:write name="UserSearchForm" property="email" />"
                                >
                            </td>
                            <td width="100" align="right">Last Name</td>
                            <td width="100" align="left">
                                <input name="lastName" type="text" class="userSearchSelectBox"
                                    value="<bean:write name="UserSearchForm" property="lastName" />"
                                >
                            </td>
                            <td width="100" align="right">School</td>
                            <td width="100" align="left">
                                <input name="school" type="text" class="userSearchSelectBox"
                                    value="<bean:write name="UserSearchForm" property="school" />"
                                >
                            </td>
                        </tr>     
                        <tr>
                            <td width="60" align="right">Role</td>
                            <td width="100" align="left">
                                <select name="roleId" class="userSearchSelectBox">
                                    <option value=""></option>
                                    <logic:iterate id="Role" name="Roles">
                                        <option
                                            value="<bean:write name="Role" property="id" />"
                                            <logic:equal name="Role" property="id" value="<%=selectedRoleId%>">selected</logic:equal> ><bean:write name="Role" property="name" /></option>
                                    </logic:iterate>
                                </select>
                            </td>
                            <td width="100" align="right">Country</td>
                            <td width="100" align="left">                                
                                <select name="countryId" class="userSearchSelectBox">
                                    <option value=""></option>
                                    <logic:iterate id="Country" name="Countries">
                                        <option
                                            value="<bean:write name="Country" property="id" />"
                                            <logic:equal name="Country" property="id" value="<%=selectedCountryId%>">selected</logic:equal> ><bean:write name="Country" property="name" /></option>
                                    </logic:iterate>
                                </select>
                            </td>
                        </tr>                              
                    </table>
                    <input id="serachButton" type="submit" value="Search"/>
                </div>
                <br>
                <logic:present name="users">
                <%
                    long pageNumber = ((Long) request.getAttribute("pageNumber")).longValue();
                    long paging = ((Long) request.getAttribute("paging")).longValue();
                    long totalPages = ((Long) request.getAttribute("totalPages")).longValue();
                %>
                <%
                    if (totalPages > 0) {
                %>               
                
                <div class="pageFooter">
                 <%
                    if (totalPages > 1) {
                %>
                      <a href="JavaScript: goPage(1,<%=totalPages%>,<%=pageNumber%>,document.UserSearchForm);">&lt;&lt;First</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=pageNumber-1%>,<%=totalPages%>,<%=pageNumber%>,document.UserSearchForm);">&lt;Previous</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=pageNumber+1%>,<%=totalPages%>,<%=pageNumber%>,document.UserSearchForm);">Next&gt;</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=totalPages%>,<%=totalPages%>,<%=pageNumber%>,document.UserSearchForm);">Last&gt;&gt;</a>
                      &nbsp;&nbsp;
                <%
                    }
                %>
                      <font color="red"> Total: <bean:write name="total"/></font>                
                </div>
                                
                <table>
                    <tr class="rowHeader">
                        <td width="50" align="center">Status</td>
                        <td width="120" align="center">Handle</td>
                        <td width="180" align="center">Email</td>
                        <td width="120" align="center">Name</td> 
                        <logic:present name="oj_security">
                        <logic:equal name="oj_security" property="superAdmin" value="true">
                            <td align="center">Admin</td>
                        </logic:equal>
                        </logic:present>                       
                    </tr>
                    <%
                    List users = (List) request.getAttribute("users");
                    for (int i = 0; i < users.size(); ++i) {
                         UserProfile user = (UserProfile) users.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td align="center"><%=user.isActive() ? "Active" : "Inactive"%></td>
                        <td align="center"><%=user.getHandle()%></td>
                        <td align="center"><%=user.getEmail()%></td>
                        <%
                            String first = user.getFirstName() == null ? "" : user.getFirstName();
                            String last = user.getLastName() == null ? "" : user.getLastName();
                            String username = "";
                            if (first.length() == 0) {
                                username = last;
                            } else if (Character.isLetterOrDigit(first.charAt(0))) {
                                username = first + " " + last;
                            } else {
                                username = last + first;
                            }                                                        
                        %>
                        <td align="center"><%=username%></td>
                        <logic:present name="oj_security">
                        <logic:equal name="oj_security" property="superAdmin" value="true">
                        <td align="center">
                            <a href="<%=request.getContextPath()%>/manageUser.do?userId=<%=user.getId()%>">Edit</a>
                            <a href="<%=request.getContextPath()%>/manageUserRole.do?userId=<%=user.getId()%>">Roles</a>                            
                        </td>
                        </logic:equal>
                        </logic:present>  
                    </tr>
                    <%
                    }
                    %>
                </table>
                <%
                    if (totalPages > 1) {
                %>
                <div class="pageFooter">
                      <a href="JavaScript: goPage(1,<%=totalPages%>,<%=pageNumber%>,document.UserSearchForm);">&lt;&lt;First</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=pageNumber-1%>,<%=totalPages%>,<%=pageNumber%>,document.UserSearchForm);">&lt;Previous</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=pageNumber+1%>,<%=totalPages%>,<%=pageNumber%>,document.UserSearchForm);">Next&gt;</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=totalPages%>,<%=totalPages%>,<%=pageNumber%>,document.UserSearchForm);">Last&gt;&gt;</a>
                      <%--<a href="<%=request.getContextPath()%>/showContestRuns.do?contestId=<bean:write name="contest" property="id"/>">Last&gt;&gt;</a>--%>
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page <input id="pageNumber" name="pageNumber" value="<bean:write name="pageNumber"/>"/> of <span id="totalPages"><bean:write name="totalPages"/> </span>Pages
                      <input id="pageButton" type="button" onclick="JavaScript: UserSearchForm.submit();return false;" value="Go!"/>
                </div>
                <%
                    }
                %>
                <%
                    } else {
                %>
                    <p>
                    <font size="4" color="red">No User Found.</font>
                    </p>
                <%
                    }
                %>
                </logic:present>
            </form>
        </div>

        </logic:messagesNotPresent>

<script language="JavaScript">
function goPage(n, total, now, table) {
    if (n == now || n < 1 || n > total) {
        return;
    }
    table.pageNumber.value = n;
    table.exportFormat.value = "";
    table.submit();
}
function showSearch(show) {
    if (show) {
        document.getElementById("searchUsersLink").style.display="none";
        document.getElementById("searchUsersParameter").style.display="block";
        document.UserSearchForm.search.value="true";

    } else {
        document.getElementById("searchUsersLink").style.display="block";
        document.getElementById("searchUsersParameter").style.display="none";
        document.UserSearchForm.search.value="false";
    }
}
function exportUserList(exportFormat) {    
    document.UserSearchForm.exportFormat.value = exportFormat;
    document.UserSearchForm.submit();
    document.UserSearchForm.exportFormat.value = "";
} 
</script>

<logic:present name="users">
    <script language="JavaScript">
    showSearch(<bean:write name="UserSearchForm" property="search"/>);
    </script>
</logic:present>
<logic:notPresent name="users">
    <script language="JavaScript">
    showSearch(true);
    </script>
</logic:notPresent>
