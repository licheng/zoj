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

<%@ page import="java.util.Enumeration" %>
<%@ page import="org.apache.struts.action.ActionMessages" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ProblemPackage" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ProblemEntry" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Limit" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>

<%
    boolean isProblemset =  "Problems".equals(request.getAttribute("region"));        
    String actionPath = isProblemset ? "importProblems.do" : "importContestProblems.do";    
    String backPath = isProblemset ? "editProblemset.do" : "editContest.do";    
%>
<script language="JavaScript">
    
function stopEvent(event) {
    event = event || window.event;
    if (event.stopPropagation) {
        event.preventDefault();
        event.stopPropagation();        
    } else {
        event.cancelBubble = true;
        event.returnValue = false;
    }
    return false;
} 
function importProblems() {    
    if (confirm("Are you sure to import the problems?")) {
      document.location.href="<%=request.getContextPath() + "/" + actionPath %>?contestId=<bean:write name="contestId"/>";
    }    
}

</script>
        <div id="content_title">Import Problem Perview</div>
                <div id="content_body">
                
            <logic:messagesPresent property="error">
            <div class="internalError">
                <html:errors property="error"/>
            </div>
            </logic:messagesPresent>
            <logic:messagesNotPresent property="error">
            <div class="div1">
            <%
                boolean hasError = false;
                ActionMessages m = (ActionMessages) request.getAttribute("org.apache.struts.action.ERROR");
		        if (m != null && m.size() > 0) {
    			    for (Iterator it = m.properties(); it.hasNext();) {
    				    String key = (String) it.next();
    				    if (key.startsWith("Line ")) {
    				        if (!hasError) {
    				            hasError = true;
    		 %>
    				        <p><font color="red"><b>Some errors exist in the zip file.</b></font></p>
             <%
    				        }
    	     %>
    	                    <p>
    	                    <b><%=key%>:</b><br>
                            <html:errors property="<%=key%>"/>
                            </p>
             <%
        				}
        			}
        		}

                if (hasError) {
            %>
                <p><a href="<%=request.getContextPath() + "/" + backPath%>?contestId=<bean:write name="contestId"/>">Back</a></p>
            <%
                }
            %>
            </div>

            <%
                if (!hasError) {
                    ProblemPackage pack = (ProblemPackage) request.getSession().getAttribute("ProblemPackage");
                    if (pack != null) {
            %>
            <div class="div1">
                <p><b>Problems</b></p>
            <table>
                <tr>
                    <td>Code</td><td>Title</td><td>Use Checker</td><td>TL(s)</td><td>ML(KB)</td><td>OL(KB)</td><td>SL(KB)</td><td>Author</td><td>Source</td><td>Contest</td><td>Text</td><td>Input</td><td>Output</td><td>Checker</td>
                </tr>
            <%

            		    ProblemEntry[] e = pack.getProblemEntries();
            		    for (int i = 0; i < e.length; ++i) {
            			    Problem problem = e[i].getProblem();
            			    Limit limit = problem.getLimit();

	        %>
	        <tr>
	        <td><%=problem.getCode()%></td>
	        <td><%=problem.getTitle()%></td>
	        <td><%=problem.isChecker()%></td>
	        <td><%=limit == null ? "-" : "" + limit.getTimeLimit()%></td>
	        <td><%=limit == null ? "-" : "" + limit.getMemoryLimit()%></td>
	        <td><%=limit == null ? "-" : "" + limit.getOutputLimit()%></td>
	        <td><%=limit == null ? "-" : "" + limit.getSubmissionLimit()%></td>
	        <td><%=problem.getAuthor()%></td>
	        <td><%=problem.getSource()%></td>
	        <td><%=problem.getContest()%></td>
	        <td><%=e[i].getText() == null ? "No" : "Yes"%></td>
	        <td><%=e[i].getInput() == null ? "No" : "Yes"%></td>
	        <td><%=e[i].getOutput() == null ? "No" : "Yes"%></td>
			<td><%=e[i].getCheckerSource() == null ? (problem.isChecker() ? "No" : "N/A") : "Yes"%></td>
		    </tr>
	        <%
		                }
            %>
            </table>
            <p><b>Images</b></p>
            <p>
            <%
                        for (Iterator it = pack.getImages().keySet().iterator(); it.hasNext();) {
			                Object imageName = it.next();
            %>
                <%=imageName%>
            <% if (pack.getUsedImages().containsKey(imageName)) {%>
                - <font color="blue">Already Exists <%=pack.getUsedImages().get(imageName)%></font>
            <% }%>
            <% if (pack.getDuplicateImages().containsKey(imageName)) {%>
                - <font color="red">Duplicate <%=pack.getUsedImages().get(imageName)%></font>
            <% }%>
                <br>
            <%
		       }
            %>
            </p>
            <p>
                <a href="<%=request.getContextPath() + "/" + backPath%>?contestId=<bean:write name="contestId"/>">Cancle</a>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <a href="%" onclick="importProblems();return stopEvent(event);">Import</a>
            </p>
            </div>
            <%
                    }
                }
            %>

            </logic:messagesNotPresent>
        </div>

