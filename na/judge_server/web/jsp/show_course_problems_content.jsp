<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ContestStatistics" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.UserStatistics" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.Set" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Reference" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ConfigManager" %>

<%
	String actionName = "CourseProblem";
    String actionPath = request.getContextPath() + "/showCourseProblems.do";
    String searchactionPath = request.getContextPath() + "/search" 
        + actionName + ".do";
    String problemLink = request.getContextPath() + "/show" + actionName + ".do";
    String runsLink = request.getContextPath() + "/showCourseRuns.do";
    String showProblemsAction = "showCourseProblems.do";
    UserSecurity userSecurity = (UserSecurity) request.getSession().getAttribute("oj_security");
    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
    boolean admin = (userSecurity != null && userSecurity.canAdminContest(contest.getId()));
%>
<% 
if(admin){
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
                    List problems = (List) request.getAttribute("problems");
                    UserStatistics userStatistics = (UserStatistics) request.getAttribute("UserStatistics");
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
                    <% if (userStatistics != null) { %>
                        <td class="problemSolved">Confirmed</td>
                        <td class="problemSolved">Solved</td>
                        <% } %>                 
                        <td class="problemId">ID</td>
                        <td class="problemTitle">Title</td>
                        <%
                         String headerPath=request.getContextPath() + "/" + showProblemsAction+"?contestId="+contest.getId()+"&pageNumber=";
                         %>
                        <td class="problemStatus"><a href="<%=headerPath%><bean:write name="currentPage"/><%="&order=ratio"%>">Ratio</a> (<a href="<%=headerPath%><bean:write name="currentPage"/><%="&order=ac"%>">AC</a>/<a href="<%=headerPath%><bean:write name="currentPage"/><%="&order=all"%>">All</n>)</td>
                        <td class="problemStatus">Score</td>
                        <% if (admin) { %>
                            <td class="problemAdmin">Admin</td>
                        <% } %>                     
                    </tr>
                    <%
                    for (int i = 0; i < problems.size(); ++i) {
                         Problem problem = (Problem) problems.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <% if (userStatistics != null) { %>
                        <td class="problemSolved"><font color="red"><%=userStatistics.getConfirmed().contains(problem) ? "Yes" : ""%></font></td>
                        <td class="problemSolved"><font color="red"><%=userStatistics.getSolved().contains(problem) ? "Yes" : ""%></font></td>
                        <% } %>   
                        <% if(contest.getId()!=ConfigManager.getDefaultProblemSetId()) { %>
                        <td class="problemId"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getCode()%></font></a></td>
                        <td class="problemTitle"><a href="<%=problemLink%>?problemId=<%=problem.getId()%>"><font color="blue"><%=problem.getTitle()%></font></a></td>
                        <% } else {%>
                        <td class="problemId"><a href="<%=problemLink%>?problemCode=<%=problem.getCode()%>"><font color="blue"><%=problem.getCode()%></font></a></td>
                        <td class="problemTitle"><a href="<%=problemLink%>?problemCode=<%=problem.getCode()%>"><font color="blue"><%=problem.getTitle()%></font></a></td>
                        <% } %>
                        <%
                            int ac = problem.getAC();
                            int total = problem.getTotal();
                            String ratio = "0.00%";
                            String acLink = "0";
                            String totalLink = "0";
                            
                            if (total > 0) {
                                int r = ac * 10000 / total;
                                ratio = r/100 + "." + r%100/10 + r%10 + "%";
                                if (ac > 0) {
                                    acLink = "<a href='" + runsLink + "?contestId=" + problem.getContestId() + "&problemCode=" + problem.getCode() + "&judgeReplyIds=" + JudgeReply.ACCEPTED.getId() + "'>" + ac + "</a>";
                                }
                                totalLink = "<a href='" + runsLink + "?contestId=" + problem.getContestId() + "&problemCode=" + problem.getCode() + "'>" + total + "</a>";
                            }
                        %>
                        <td class="problemStatus"><%=ratio%> (<%=acLink%>/<%=totalLink%>)</td>
                        <td class="problemStatus"><%=problem.getScore() %></td>
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
                <%
                List<Reference> TAname = (List<Reference>)request.getAttribute("TAname");
                List<Reference> TAphone = (List<Reference>)request.getAttribute("TAphone");
                List<Reference> TAemail = (List<Reference>)request.getAttribute("TAemail");
                 if (TAname.size()>0) { %>

                <blockquote>
                TA name: <%=new String(TAname.get(0).getContent()) %>
                </blockquote>
                <% }
                if (TAphone.size()>0) { %>

                <blockquote>
                TA phone: <%=new String(TAphone.get(0).getContent()) %>
                </blockquote>
                <% }
                if (TAemail.size()>0) { %>

                <blockquote>
                TA email: <%=new String(TAemail.get(0).getContent()) %>
                </blockquote>
                <% } %>   
                <% if (admin) { %>

                <blockquote>
                <a href="<%=request.getContextPath()%>/add<%=actionName%>.do?contestId=<%=request.getAttribute("contestId")%>"><font color="red">Add Problem</font></a>&nbsp;&nbsp;&nbsp;
                <a href="<%=request.getContextPath()%>/export<%=actionName%>s.do?contestId=<%=request.getAttribute("contestId")%>"><font color="red">Export Problems</font></a>&nbsp;&nbsp;&nbsp;
                <a href="<%=request.getContextPath()%>/show<%=actionName%>s.do?contestId=<%=request.getAttribute("contestId")%>&check=true"><font color="red">Check Problems</font></a>
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


