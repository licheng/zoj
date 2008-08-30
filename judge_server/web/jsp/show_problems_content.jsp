<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ContestStatistics" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.Set" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>


<%
    boolean isProblemset =  "Problems".equals(request.getAttribute("region"));    
    String actionName = isProblemset ? "Problem" : "ContestProblem";
    String actionPath = request.getContextPath() + "/show" 
        + (isProblemset ? "Problems" : "ContestProblems") + ".do";
    String problemLink = request.getContextPath() + "/show" + actionName + ".do";
    String showProblemsAction = isProblemset ? "showProblems.do" : "showContestProblems.do";
    UserSecurity userSecurity = (UserSecurity) request.getSession().getAttribute("oj_security");
    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
    boolean admin = (userSecurity != null && userSecurity.canAdminContest(contest.getId()));
%>

<logic:present name="oj_security">
<logic:equal name="oj_security" property="superAdmin" value="true">

</logic:equal>
</logic:present>
<% if(admin){
	%>
	<script language="JavaScript">
function deleteProblem(code, problemId) {
    if (!confirm("Are you sure to delete " + code +"?")) {
      return;
    }
    location.href="<%=request.getContextPath()%>/delete<%=actionName%>.do?problemId=" + problemId;
}
</script>
	<%
}
%>



        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title"><bean:write name="contest" property="title"/>
            <%
                long totalPages = ((Long) request.getAttribute("totalPages")).longValue();
                if (totalPages > 1) {
            %>
            Vol <bean:write name="currentPage"/>
            <%
                }
            %>
        </div>
        <div id="content_body">
            <form name="ProblemListForm" method="get" action="<%=actionPath%>">
                <%
                if (totalPages > 1) {
                for (int i = 0; i < totalPages; ++i) {
                %>
                <a href="<%=request.getContextPath() + "/" + showProblemsAction%>?contestId=<bean:write name="contest" property="id"/>&pageNumber=<%=i+1%>">Vol <%=i+1%>&nbsp;&nbsp;</a>
                <%
                }
                }
                %>
                <table class="list">
                    <%
                    Set solved = (Set) request.getAttribute("solved");
                    List problems = (List) request.getAttribute("problems");
                    ContestStatistics statistics = (ContestStatistics) request.getAttribute("ContestStatistics");
                    
                    boolean showColor = false;
                    for (int i = 0; i < problems.size(); ++i) {
                      Problem p = (Problem) problems.get(i);
                      if (p.getColor() != null && p.getColor().trim().length() > 0) {
                        showColor = true;
                        break;
                      }
                    }
                    %>
                    
                    <tr class="rowHeader">
                    	<% if (showColor) { %>
                        <td class="problemColor">Color</td>
                      	<% } %>
                        <td class="problemSolved">Solved</td>                        
                        <td class="problemId">ID</td>
                        <td class="problemTitle">Title</td>
                        <td class="problemStatus">Status</td>
                        <% if (admin) { %>
                            <td class="problemAdmin">Admin</td>
                        <% } %>                     
                    </tr>
                    <%
                    for (int i = 0; i < problems.size(); ++i) {
                         Problem problem = (Problem) problems.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                    	<% if (showColor) { %>
                        <td class="problemColor" bgcolor="<%=problem.getColor() == null || problem.getColor().trim().length() == 0 ? "white" : problem.getColor()%>" > </td>
                      	<% } %>
                    
                        <td class="problemSolved"><%=solved.contains(new Long(problem.getId())) ? "Yes" : ""%></td>
                        <td class="problemId"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getCode()%></font></a></td>
                        <td class="problemTitle"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getTitle()%></font></a></td>
                        <%
                            //int ac = statistics.getCount(i, 0);
                            //int total = statistics.getProblemCount(i);
                            int ac = 0;
                            int total = 0;
                            String status = "0.00% (0/0)";
                            if (total > 0) {
                            	int r = ac * 10000 / total;
                            	status = MessageFormat.format("{0}.{1}% ({2})", new Object[] {"" + r/100, "" + r%100/10 + r%10, ac + "/" + total});
                            }
                        %>
                        <td class="problemStatus"><%=status%></td>
                        <% if (admin) { %>
                        <td class="problemAdmin">
                            <a href="<%=request.getContextPath()%>/edit<%=actionName%>.do?problemId=<%=problem.getId()%>"><font color="red">Edit</font></a>
                            <a href="javascript:deleteProblem('<%=problem.getCode()%>',<%=problem.getId()%>)"><font color="red">Delete</font></a>
                        </td>
                        <% } %> 
                                                
                    </tr>
                    <%
                    }
                    %>

                    
                </table>
                <% if (admin) { %>
                <blockquote>
                <a href="<%=request.getContextPath()%>/add<%=actionName%>.do?contestId=<%=request.getAttribute("contestId")%>"><font color="red">Add Problem</font></a>&nbsp;&nbsp;&nbsp;
                <a href="<%=request.getContextPath()%>/export<%=actionName%>s.do?contestId=<%=request.getAttribute("contestId")%>"><font color="red">Export Problems</font></a>&nbsp;&nbsp;&nbsp;
                <a href="<%=request.getContextPath()%>/show<%=actionName%>s.do?contestId=<%=request.getAttribute("contestId")%>&check=ture"><font color="red">Check Problems</font></a>
                </blockquote>
                <% } %>   
                
            </form>
            
            <logic:present name="CheckMessages">
                <font color=green>Check Results:</font><br>
                <logic:empty name="CheckMessages">
                    <font color=green>No error or warning</font><br>
                </logic:empty>
                <logic:notEmpty name="CheckMessages">
                <%
                    List msg = (List) request.getAttribute("CheckMessages");  
                    for (int i = 0; i < msg.size(); ++i) {
                        String s = (String) msg.get(i);
                        String color = s.startsWith("ERROR") ? "red" : "green";
                        
                %>
                    <font color=<%=color%>><%=s%></font><br>
                <%                                
                    }
                    
                %>
                </logic:notEmpty>
            </logic:present>
        </div>
        </logic:messagesNotPresent>


