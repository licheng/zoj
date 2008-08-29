<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>


<%@ page import="cn.edu.zju.acm.onlinejudge.form.ContestForm" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.Language" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Limit" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.PersistenceManager" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%
    boolean isProblemset =  "Problems".equals(request.getAttribute("region"));        
    String actionPath = request.getContextPath() + (isProblemset ? "/submit.do" : "/contestSubmit.do");    

%>
<script language="JavaScript">

function checkProblemFile(submitForm) {

    var message = "";
    if (submitForm.languageId.value == '') {
        message = 'Language should be selected.';
    }
    if (submitForm.source.value == '') {
        message += '\nSource cannot be empty';
    }
    if (message != "") {
        alert(message);
        return false;
    }
    
    if 
    
    if (message != "") {
        alert(message);
        return false;
    }
    return true;
}
</script>
        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title"><bean:write name="contest" property="title"/></div>
        <div id="content_body">
				<br>
				<form name="submitForm" method="post" action="<%=actionPath%>" onsubmit="return checkProblemFile(this);">
				    <input type="hidden" name="problemId" value="<bean:write name="problem" property="id"/>">
                <table width="98%" border="0" cellpadding="0" cellspacing="0">
                  <tr>
                    <td width="26%" height="10"> <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">

                        <tr>
                          <td>&nbsp; </td>
                          <td height="25"><div align="right">Problem ID&nbsp;&nbsp;</div></td>
                          <td><font color=red><bean:write name="problem" property="code"/></font> </td>
                        </tr>
                        <tr>
                          <td>&nbsp;</td>
                          <td height="25"> <div align="right">Language&nbsp;&nbsp;</div></td>
                          <td> <select name="languageId">
                              <option value=''>--select Language--</option>
                            <%
                                for (Iterator it = ((AbstractContest) request.getAttribute("contest")).getLanguages().iterator(); it.hasNext();) {
                                    Language language = (Language) it.next();
                            %>
                              <option value="<%=language.getId()%>" <%=("" +language.getId()).equals(request.getParameter("languageId")) ? "selected" : ""%> ><%=language.getName()%></option>
                            <%
                            }
                            %>

                        </tr>
                        <tr>
                          <td>&nbsp; </td>
                          <td height="25"><div align="right"></div></td>
                          <td>&nbsp;<logic:messagesPresent property="message"><font color='red'><html:errors property="message"/></font></logic:messagesPresent></td>
                        </tr>
                        <tr>
                          <td>&nbsp; </td>
                          <td height="25" valign="top"> <div align="right"> Submit
                              Box &nbsp;&nbsp;</div></td>
                          <td><font color=red>
                            <textarea name="source" cols="60" rows="20" id="source"><bean:write name="source"/></textarea>
                            </font></td>
                        </tr>
                      </table>
                      <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr>
                          <td width="49%" align="center">&nbsp;</td>
                          <td width="51%" align="center">&nbsp;</td>
                        </tr>
                      </table>
                      <br> <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr>
                          <td width="10">&nbsp; </td>
                          <td width="120" height="25"><div align="right"> &nbsp;</div></td>
                          <td> <input type="submit" value="Submit">                            
                          </td>
                        </tr>
                      </table>
                      </td>
                  </tr>
                </table>
                </form>

        </div>
        </logic:messagesNotPresent>

