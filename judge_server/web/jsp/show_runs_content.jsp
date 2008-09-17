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
    boolean canViewSource = (userSecurity != null && userSecurity.canViewSource(contest.getId()));
    String userStatusPath = request.getContextPath() + "/showUserStatus.do?userId=";
    SubmissionSearchForm searchForm = (SubmissionSearchForm) request.getAttribute("SubmissionSearchForm");
    
%>
        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title">
          <div id="export">
            <span id="searchRunsLink" style="display:<%=searchForm.isSearch() ? "none" : "block"%>">
                <a href="JavaScript: showSearch(true);">Search</a>
            </span>
            <span id="hideSearchRunsLink" style="display:<%=searchForm.isSearch() ? "block" : "none"%>">
                <a href="JavaScript: showSearch(false);">Hide Search Form</a>
            </span>
          </div>
          &nbsp;<bean:write name="contest" property="title"/>
        </div>
        <div id="content_body">
            <form id="SubmissionSearchForm" name="SubmissionSearchForm" method="GET" action="<%=actionPath%>">
            <input type="hidden" name="contestId" value="<bean:write name="contest" property="id"/>">
            <input type="hidden" name="search" value="<bean:write name="SubmissionSearchForm" property="search"/>">
            <input type="hidden" name="firstId" value="-1">
            <input type="hidden" name="lastId" value="-1">
                <div id="searchRunsParameter" style="display:<%=searchForm.isSearch() ? "block" : "none"%>">
                    <table id="searchTable" >
                        <tr>
                            <td width="115">Problem Code<br>
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
                            <td width="80">Languages<br>
                            <%
                                
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

                <logic:empty name="runs">
                <div>
                <br/>
                <font size='3' color='red'>No submission available.</font>
                <br/>
                </div>
                </logic:empty>
                <logic:notEmpty name="runs">
                <div>
                <logic:present name="firstId">
                <a href="JavaScript: goPrevious(<%=request.getAttribute("firstId")%>);">&lt;&lt;Previous</a>
                </logic:present>
                <logic:notPresent name="firstId">
                <font color="#777777">&lt;&lt;Previous</font>
                </logic:notPresent>
                <logic:present name="lastId">
                <a href="JavaScript: goNext(<%=request.getAttribute("lastId")%>);">Next&gt;&gt;</a>
                </logic:present>
                <logic:notPresent name="lastId">
                <font color="#777777">Next&gt;&gt;</font>
                </logic:notPresent>
                
                </div>
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
                        <% if (admin || canViewSource) { %>
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
                        <td class="runId"><%=submission.getContestOrder()%></td>
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
                        <td class="runUserName"><a href="<%=userStatusPath + submission.getUserProfileId() %>"><font color="db6d00"><%=submission.getUserName()%></font></a></td>
                        <% if (admin || canViewSource) { %>
                            <td class="runAdmin"><a href="<%=request.getContextPath()%>/showSubmission.do?submissionId=<%=submission.getId()%>" target="_blank">Source</a></td>
                        <% } %>                        
                    </tr>
                    <%
                    }
                    %>
                </table>
                <div>
                <logic:present name="firstId">
                <a href="JavaScript: goPrevious(<%=request.getAttribute("firstId")%>);">&lt;&lt;Previous</a>
                </logic:present>
                <logic:notPresent name="firstId">
                <font color="#777777">&lt;&lt;Previous</font>
                </logic:notPresent>
                <logic:present name="lastId">
                <a href="JavaScript: goNext(<%=request.getAttribute("lastId")%>);">Next&gt;&gt;</a>
                </logic:present>
                <logic:notPresent name="lastId">
                <font color="#777777">Next&gt;&gt;</font>
                </logic:notPresent>
                </div>
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
                </logic:notEmpty>
            </form>
        </div>

        </logic:messagesNotPresent>


<script language="JavaScript">

<% 
if (admin) {
%>
function rejudge() {
    if (!confirm("Are you sure to rejudge them?")) {
      return;
    }
    document.SubmissionSearchForm.method = 'POST';
    document.SubmissionSearchForm.rejudge.value = 'true';
    document.SubmissionSearchForm.submit();
} 
<% 
}
%>


function goPrevious(id) {
    var form = document.getElementById('SubmissionSearchForm');
    form.firstId.value = id;
    form.submit();
}
function goNext(id) {
    var form = document.getElementById('SubmissionSearchForm');
    form.lastId.value = id;
    form.submit();
}

function showSearch(show) {
    if (show) {
        document.getElementById("searchRunsLink").style.display="none";
        document.getElementById("hideSearchRunsLink").style.display="block";
        document.getElementById("searchRunsParameter").style.display="block";
        document.SubmissionSearchForm.search.value="true";

    } else {
        document.getElementById("searchRunsLink").style.display="block";
        document.getElementById("hideSearchRunsLink").style.display="none";
        document.getElementById("searchRunsParameter").style.display="none";
        document.SubmissionSearchForm.search.value="false";
    }
}
showSearch(<bean:write name="SubmissionSearchForm" property="search"/>);
</script>
