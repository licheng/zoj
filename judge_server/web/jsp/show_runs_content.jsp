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
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Submission" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.Language" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.form.SubmissionSearchForm" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.UserProfile" %>

<%
    boolean isProblemset =  "Problems".equals(request.getAttribute("region"));        
    String actionPath = request.getContextPath() + (isProblemset ? "/showRuns.do" : "/showContestRuns.do");    
    String showProblemPath = request.getContextPath() + (isProblemset ? "/showProblem.do" : "/showContestProblem.do");    
    UserSecurity userSecurity = (UserSecurity) request.getSession().getAttribute("oj_security");                                                
    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
    boolean admin = (userSecurity != null && userSecurity.canAdminContest(contest.getId()));
%>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title"><bean:write name="contest" property="title"/></div>
        <div id="content_body">
            <form name="SubmissionSearchForm" method="GET" action="<%=actionPath%>">
            <input type="hidden" name="contestId" value="<bean:write name="contest" property="id"/>">
            <input type="hidden" name="search" value="<bean:write name="SubmissionSearchForm" property="search"/>">
                <div id="searchRunsLink">
                <a href="JavaScript: showSearch(true);">Search</a>
                </div>
                <div id="searchRunsParameter">
                    <a href="JavaScript: showSearch(false);">Hide Search Form</a>
                    <table id="searchTable" >
                        <tr>
                            <td width="115">Problem ID<br>
                                <input name="problemCode" type="text" size="10"
                                    value="<bean:write name="SubmissionSearchForm" property="problemCode" />"
                                ><br>
                                <logic:messagesPresent property="problemCode">
                                <span class="error">
                                    <html:errors property="problemCode"/>
                                </span>
                                </logic:messagesPresent><br>
                                UserHandle<br>
                                <input name="handle" type="text" size="10"
                                    value="<bean:write name="SubmissionSearchForm" property="handle" />"
                                ><br>
                                <logic:messagesPresent property="handle">
                                <span class="error">
                                    <html:errors property="handle"/>
                                </span>
                                </logic:messagesPresent>
                                <br>
                            </td>
                            <td width="115">Submit ID From<br>
                                <input name="idStart" type="text" size="10"
                                    value="<bean:write name="SubmissionSearchForm" property="idStart" />"
                                ><br>
                                <logic:messagesPresent property="idStart">
                                <span class="error">
                                    <html:errors property="idStart"/>
                                </span>
                                </logic:messagesPresent><br>
                                To<br>
                                <input name="idEnd" type="text" size="10"
                                    value="<bean:write name="SubmissionSearchForm" property="idEnd" />"
                                ><br>
                                <logic:messagesPresent property="idEnd">
                                <span class="error">
                                    <html:errors property="idEnd"/>
                                </span>
                                </logic:messagesPresent>
                                <br>
                            </td>
                            <td width="220">Submit Time From<br>
                                <input name="timeStart" type="text" size="24"
                                    value="<bean:write name="SubmissionSearchForm" property="timeStart" />"
                                ><br>
                                <span class="error">yyyy-mm-dd HH:MM:SS
                                <logic:messagesPresent property="timeStart">

                                    <html:errors property="timeStart"/>
                                </logic:messagesPresent><br>
                                </span>
                                To<br>
                                <input name="timeEnd" type="text" size="24"
                                    value="<bean:write name="SubmissionSearchForm" property="timeEnd" />"
                                ><br>
                                <span class="error">yyyy-mm-dd HH:MM:SS
                                <logic:messagesPresent property="timeEnd">

                                    <html:errors property="timeEnd"/>
                                </logic:messagesPresent>
                                </span>
                                <br>
                            </td>
                            <td width="80">Languages<br>
                            <%
                                
                                SubmissionSearchForm searchForm = (SubmissionSearchForm) request.getAttribute("SubmissionSearchForm");

                                List languages = contest.getLanguages();
                                Set selectedIds = new HashSet();
                                if (searchForm.getLanguageIds() != null) {
                                    selectedIds.addAll(Arrays.asList(searchForm.getLanguageIds()));
                                }

                            %>
                            <select name="languageIds" width="60" size="6" multiple >
                            <%
                                for (Iterator it = languages.iterator(); it.hasNext();) {
                                    Language language = (Language) it.next();
                            %>
                                <option value="<%=language.getId()%>" <%=selectedIds.contains(String.valueOf(language.getId())) ? "selected" : "" %> ><%=language.getName()%></option>
                            <%
                            }
                            %>
                            </select><br>
                                <logic:messagesPresent property="languageIds">
                                <span class="error">
                                    <html:errors property="languageIds"/>
                                </span>
                                </logic:messagesPresent>
                            </td>
                            <td width="100">Judge Replies<br>
                            <%
                                List judgeReplies = (List) request.getAttribute("judgeReplies");
                                Set selectedJudgeIds = new HashSet();
                                if (searchForm.getJudgeReplyIds() != null) {
                                    selectedJudgeIds.addAll(Arrays.asList(searchForm.getJudgeReplyIds()));
                                }
                            %>
                            <select name="judgeReplyIds" size="6" multiple>
                            <%
                                for (Iterator it = judgeReplies.iterator(); it.hasNext();) {
                                    JudgeReply reply = (JudgeReply) it.next();
                            %>
                                <option value="<%=reply.getId()%>" <%=selectedJudgeIds.contains(String.valueOf(reply.getId())) ? "selected" : "" %> ><%=reply.getName()%></option>
                            <%
                            }
                            %>
                            </select><br>
                                <logic:messagesPresent property="judgeReplyIds">
                                <span class="error">
                                    <html:errors property="judgeReplyIds"/>
                                </span>
                                </logic:messagesPresent>
                            </td>
                        </tr>
                    </table>
                    <input id="serachButton" type="submit" value="Search"/>
                </div>

                <%
                    long pageNumber = ((Long) request.getAttribute("pageNumber")).longValue();
                    long totalPages = ((Long) request.getAttribute("totalPages")).longValue();
                %>
                <%
                    if (totalPages > 0) {
                %>
                <%
                    if (totalPages > 1) {
                %>
                <div class="pageFooter">
                      <a href="JavaScript: goPage(<%=totalPages%>,<%=totalPages%>,<%=pageNumber%>,document.SubmissionSearchForm);">&lt;&lt;First</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=pageNumber+1%>,<%=totalPages%>,<%=pageNumber%>,document.SubmissionSearchForm);">&lt;Previous</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=pageNumber-1%>,<%=totalPages%>,<%=pageNumber%>,document.SubmissionSearchForm);">Next&gt;</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(1,<%=totalPages%>,<%=pageNumber%>,document.SubmissionSearchForm);">Last&gt;&gt;</a>
                      &nbsp;&nbsp; <font color='red'>Total: <bean:write name="totalSubmissions"/></font> 
                </div>
                <%
                    }
                %>
                <table class="list">
                    <tr class="rowHeader">
                        <td class="runId">Run ID</td>
                        <td class="runSubmitTime">Submit Time</td>
                        <td class="runJudgeStatus">Judge Status</td>
                        <td class="runProblemId">Problem ID</td>
                        <td class="runLanguage">Language</td>
                        <td class="runTime">Run Time(ms)</td>
                        <td class="runMemory">Run Memory(KB)</td>
                        <td class="runUserName">User Name</td>
                        <% if (admin) { %>
                            <td class="runAdmin">Admin</td>
                        <% } %>
                    </tr>
                    <%
                    long userId = -1;
                    if (request.getSession().getAttribute("oj_user") != null) {
                        userId = ((UserProfile) request.getSession().getAttribute("oj_user")).getId();
                    }
                        
                    List runs = (List) request.getAttribute("runs");
                    for (int i = 0; i < runs.size(); ++i) {
                         Submission submission = (Submission) runs.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td class="runId"><%=submission.getId()%></td>
                        <td class="runSubmitTime"><%=Utility.toTimestamp(submission.getSubmitDate())%></td>
                        <td class="runJudgeStatus">
                            <span class="<%=JudgeReply.ACCEPTED.equals(submission.getJudgeReply()) ? "judgeReplyAC" : "judgeReplyOther"%>">
                                <%
                                    if (JudgeReply.COMPILATION_ERROR.equals(submission.getJudgeReply()) && submission.getUserProfileId() == userId) {
                                %>
                                    <a href="<%=request.getContextPath()%>/showJudgeComment.do?submissionId=<%=submission.getId()%>"><%=submission.getJudgeReply()%></a>
                                <%
                                    } else {
                                %>
                                    <%=submission.getJudgeReply()%>
                                <%
                                    }
                                %>
                            </span></td>
                        <td class="runProblemId"><a href="<%=showProblemPath%>?problemId=<%=submission.getProblemId()%>"><%=submission.getProblemCode()%></a></td>
                        <td class="runLanguage"><%=submission.getLanguage()%></td>
                        <td class="runTime"><%=submission.getTimeConsumption()%></td>
                        <td class="runMemory"><%=submission.getMemoryConsumption()%></td>
                        <td class="runUserName"><font color="db6d00"><%=submission.getUserName()%></font></td>
                        <% if (admin) { %>
                            <td class="runAdmin"><a href="<%=request.getContextPath()%>/showSubmission.do?submissionId=<%=submission.getId()%>" target="_blank">Source</a></td>
                        <% } %>                        
                    </tr>
                    <%
                    }
                    %>
                </table>
                <%
                    if (totalPages > 1) {
                %>
                <div class="pageFooter">
                      <a href="JavaScript: goPage(<%=totalPages%>,<%=totalPages%>,<%=pageNumber%>,document.SubmissionSearchForm);">&lt;&lt;First</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=pageNumber+1%>,<%=totalPages%>,<%=pageNumber%>,document.SubmissionSearchForm);">&lt;Previous</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(<%=pageNumber-1%>,<%=totalPages%>,<%=pageNumber%>,document.SubmissionSearchForm);">Next&gt;</a>
                      &nbsp;&nbsp;
                      <a href="JavaScript: goPage(1,<%=totalPages%>,<%=pageNumber%>,document.SubmissionSearchForm);">Last&gt;&gt;</a>
                      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page <input id="pageNumber" name="pageNumber" value="<bean:write name="pageNumber"/>"/> of <span id="totalPages"><bean:write name="totalPages"/> </span>Pages
                      <input id="pageButton" type="button" onclick="JavaScript: SubmissionSearchForm.submit();return false;" value="Go!"/>
                </div>
                
                
                <%
                    }
                %>
                
                <%
                if (admin) {
                %>
                    <input type="hidden" name="rejudge" value="false">
                    <blockquote>
                    <a href="JavaScript: rejudge()"><font color='red' size='3'>Rejudge</font></a>
                    </blockquote>
                <%
                }
                %> 
                                                
                
                <%
                    } else {
                %>
                    <p>
                    <font size="4" color="red">No Submission Found.</font>
                    </p>
                <%
                    }
                %>
            </form>
        </div>

        </logic:messagesNotPresent>


<script language="JavaScript">

<% 
if (admin) {
%>
function rejudge() {
    if (!confirm("<bean:write name="totalSubmissions"/> submissions are selected. Are you sure to rejudge them?")) {
      return;
    }
    document.SubmissionSearchForm.method = 'POST';
    document.SubmissionSearchForm.rejudge.value = 'true';
    document.SubmissionSearchForm.submit();
} 
<% 
}
%>
function goPage(n, total, now, table) {
    if (n == now || n < 1 || n > total) {
        return;
    }
    table.pageNumber.value = n;
    table.submit();
}

function showSearch(show) {
    if (show) {
        document.getElementById("searchRunsLink").style.display="none";
        document.getElementById("searchRunsParameter").style.display="block";
        document.SubmissionSearchForm.search.value="true";

    } else {
        document.getElementById("searchRunsLink").style.display="block";
        document.getElementById("searchRunsParameter").style.display="none";
        document.SubmissionSearchForm.search.value="false";
    }
}
showSearch(<bean:write name="SubmissionSearchForm" property="search"/>);
</script>