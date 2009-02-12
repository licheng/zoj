<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.form.ContestForm" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.Language" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Limit" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.PersistenceManager" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Iterator" %>
<%
    String actionPath = request.getContextPath() + "/manageContests.do";
    String submitName = "Add Contest";
    String importOrExport = "Import Contest";
    String title = "Create Contest";
    
    if ("EditContest".equals(request.getAttribute("pageId"))) {
        title = "Edit Contest";
        actionPath = request.getContextPath() + "/editContest.do";
        submitName = "Edit Contest";
        importOrExport = "Export Contest";
    } if ("EditProblemset".equals(request.getAttribute("pageId"))) {
        title = "Edit Problemset";
        actionPath = request.getContextPath() + "/editProblemset.do";
        submitName = "Edit Problemset";
        importOrExport = "Export Problemset";
    } if ("EditCourse".equals(request.getAttribute("pageId"))) {
        title = "Edit Course";
        actionPath = request.getContextPath() + "/editcourse.do";
        submitName = "Edit Course";
        importOrExport = "Export Course";
    }

    boolean isProblemset = "EditProblemset".equals(request.getAttribute("pageId"));
    String name = isProblemset ? "Problemset" : "Contest";
    String importAction = isProblemset ? "importProblems.do" : "importContestProblems.do";
    
    Limit defaultLimit = PersistenceManager.getInstance().getContestPersistence().getDefaultLimit();
    ContestForm contestForm = (ContestForm) request.getAttribute("ContestForm");
%>

<script language="JavaScript">

function switchLimits() {
    if (document.contestForm.useGlobalDefault.checked) {
        document.contestForm.timeLimit.disabled = true;
        document.contestForm.memoryLimit.disabled = true;
        document.contestForm.outputLimit.disabled = true;
        document.contestForm.submissionLimit.disabled = true;
    } else {
        document.contestForm.timeLimit.disabled = false;
        document.contestForm.memoryLimit.disabled = false;
        document.contestForm.outputLimit.disabled = false;
        document.contestForm.submissionLimit.disabled = false;
    }
}
function deleteContest(contestId) {
    if (!confirm("Are you sure to delete this <%=isProblemset ? "problemset" : "contest"%>?")) {
      return;
    }
    location.href="<%=request.getContextPath()%>/delete<%=name%>.do?contestId=" + contestId;
}


function checkProblemFile(pform) {
    if (pform.problemFile.value == '') {
        alert('Please select a zip file to import.');
        return false;
    }
    return true;
}

function checkLocalProblemFile(pform) {
    if (pform.problemFilePath.value == '') {
        alert('Please enter a zip file path on the server.');
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
    <div id="content_title"><%=title%></div>
    <div id="content_body">

        <form name="contestForm" method="post" action="<%=actionPath%>">
            <input name="id" type="hidden"
                        <logic:notEmpty name="ContestForm" property="id">
                            value="<bean:write name="ContestForm" property="id" />"
                        </logic:notEmpty>
                        <logic:empty name="ContestForm" property="id">
                            value="0"
                        </logic:empty>
            >
        <table>
            <tr/>
            <tr>
                <td width="150" align="right"><%=isProblemset ? "Problemset" : "Contest"%> Information</td>
                <td width="300"></td>
            </tr>
                
            <tr>
                <td align="right"><%=isProblemset ? "Problemset" : "Contest"%> Name</td>
                <td>
                    <input name="name" type="text"
                        value="<bean:write name="ContestForm" property="name" />"

                    >
                    <span class="error">*
                        <logic:messagesPresent property="name">
                            <html:errors property="name"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>        
            <tr>
                <td align="right">Description</td>
                <td>
                    <input name="description" type="text"
                        value="<bean:write name="ContestForm" property="description" />"
                    >
                </td>
            </tr>
            <tr>
                <td align="right">Start Time</td>
                <td>
                    <input name="startTime" type="text"
                            value="<bean:write name="ContestForm" property="startTime" />"
                    >
                    <span class="error">* (yyyy-mm-dd HH:MM:SS)
                        <logic:messagesPresent property="startTime">
                            <html:errors property="startTime"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>

            <tr>
                <td align="right">Contest Length</td>
                <td>
                    <input name="contestLength" type="text"
                            value="<bean:write name="ContestForm" property="contestLength" />"
                    >
                    <span class="error">* (HHH:MM:SS)
                        <logic:messagesPresent property="contestLength">
                            <html:errors property="contestLength"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Forum Link</td>
                <td>
                    <%
                        String selectedForumId = "1";
                        if (contestForm.getForumId() != null) {
                            selectedForumId = contestForm.getForumId();
                        }
                        request.setAttribute("Fourms", PersistenceManager.getInstance().getForumPersistence().getAllForums());
                    %>

                    <select name="forumId">
                        <logic:iterate id="Fourm" name="Fourms">
                            <option
                                value="<bean:write name="Fourm" property="id" />"
                                <logic:equal name="Fourm" property="id" value="<%=selectedForumId%>">selected</logic:equal> ><bean:write name="Fourm" property="name" /></option>
                        </logic:iterate>
                    </select>

                    <span class="error">
                        <logic:messagesPresent property="forumId">
                            <html:errors property="forumId"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <%
            if ("ManageContests".equals(request.getAttribute("pageId"))) {
            %>
            <tr>
                <td align="right">Contest Type</td>
                <td>
                <select name="contestType">
                            <option value="0" >Contest</option>
                            <option value="1" >Problem Set</option>
                            <option value="2" >Course</option>
                    </select>
                </td>
            </tr>
            <%
            } else {
            %>
              <input type="hidden" name="contestType" value="<%=contestForm.getContestType()%>">
            <%
            }
            %>

            <tr/>
            <tr>
                <td align="right">Default Limits</td>
            </tr>
            <tr>
                <td align="right">Time Limit</td>
                <td>
                    <input name="timeLimit" type="text"
                        <%
                          if (contestForm.getId() == null || contestForm.isUseGlobalDefault()) {
                        %>
                            disabled
                            value="<%=defaultLimit.getTimeLimit()%>"
                        <%
                          } else {
                        %>
                            value="<%=contestForm.getTimeLimit()%>"
                        <%
                          }
                        %>

                    >
                    <span class="error">s *
                        <logic:messagesPresent property="timeLimit">
                            <html:errors property="timeLimit"/>
                        </logic:messagesPresent>

                    </span>
                </td>
            </tr>

            <tr>
                <td align="right">Memory Limit</td>
                <td>
                    <input name="memoryLimit" type="text"
                        <%
                          if (contestForm.getId() == null || contestForm.isUseGlobalDefault()) {
                        %>
                            disabled
                            value="<%=defaultLimit.getMemoryLimit()%>"
                        <%
                          } else {
                        %>
                            value="<%=contestForm.getMemoryLimit()%>"
                        <%
                          }
                        %>
                    >
                    <span class="error">KB *
                        <logic:messagesPresent property="memoryLimit">
                            <html:errors property="memoryLimit"/>
                        </logic:messagesPresent>


                    </span>
                </td>
            </tr>

            <tr>
                <td align="right">Output Limit</td>
                <td>
                    <input name="outputLimit" type="text"
                        <%
                          if (contestForm.getId() == null || contestForm.isUseGlobalDefault()) {
                        %>
                            disabled
                            value="<%=defaultLimit.getOutputLimit()%>"
                        <%
                          } else {
                        %>
                            value="<%=contestForm.getOutputLimit()%>"
                        <%
                          }
                        %>

                    >
                    <span class="error">KB *
                        <logic:messagesPresent property="outputLimit">
                            <html:errors property="outputLimit"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>

            <tr>
                <td align="right">Submission Limit</td>
                <td>
                    <input name="submissionLimit" type="text"
                        <%
                          if (contestForm.getId() == null || contestForm.isUseGlobalDefault()) {
                        %>
                            disabled
                            value="<%=defaultLimit.getSubmissionLimit()%>"
                        <%
                          } else {
                        %>
                            value="<%=contestForm.getSubmissionLimit()%>"
                        <%
                          }
                        %>

                    >
                    <span class="error">KB *
                        <logic:messagesPresent property="submissionLimit">
                            <html:errors property="submissionLimit"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Use Global Default</td>
                <td>
                    <input type="checkbox" name="useGlobalDefault"
                    <%=contestForm.getId() == null || contestForm.isUseGlobalDefault() ? "checked" : "" %>
                    onclick="javascript:switchLimits();return true;"
                    >
                </td>
            </tr>
			<tr>
                <td align="right">Check Submit IP</td>
                <td>
                    <input type="checkbox" name="checkIp"
                    <%=contestForm.getId() != null && contestForm.isCheckIp() ? "checked" : "" %>
                    >
                </td>
            </tr>
            <tr/>
            <tr>
                <td align="right">Language Select</td>
            </tr>

            <tr>
                <td align="right">Language List</td>
                <td>
                    <%
                        List allLanguages = PersistenceManager.getInstance().getLanguagePersistence().getAllLanguages();
                        Set selectedIds = new HashSet();
                        if (contestForm.getId() != null && contestForm.getLanguageIds() != null) {
                            selectedIds.addAll(Arrays.asList(contestForm.getLanguageIds()));
                        }

                    %>

                    <select name="languageIds" multiple>
                    <%
                        for (Iterator it = allLanguages.iterator(); it.hasNext();) {
                            Language language = (Language) it.next();
                    %>
                        <option value="<%=language.getId()%>" <%=contestForm.getId() == null || selectedIds.contains(String.valueOf(language.getId())) ? "selected" : "" %> ><%=language.getName()%></option>
                    <%
                    }
                    %>
                    </select>
                    <span class="error">
                        <logic:messagesPresent property="languageIds">
                            <html:errors property="languageIds"/>
                        </logic:messagesPresent>
                    </span>
                </td>
                
            </tr>
            </table>
            <blockquote>
                    <input name="submit" type="submit" id="submit" value="<%=submitName%>"> &nbsp;&nbsp;&nbsp;
                    <%
                    if ("ManageContests".equals(request.getAttribute("pageId"))) {
                    %>
                    <input name="cacel" type="reset" id="cacel" value="Cancel" onchange="javascript:switchLimits();return true;">
                    <%
                    } else {
                    %>
                    <input name="delete" type="button" id="delete" value="Delete" onclick="javascript:deleteContest('<%=contestForm.getId()%>'); return false;">
                    <%
                    }
                    %>
            </blockquote>
    </form>
    <%
    if ("EditContest".equals(request.getAttribute("pageId")) || "EditProblemset".equals(request.getAttribute("pageId"))) {
    %>
        <hr>
        <form enctype="multipart/form-data" name="ProblemImportForm" method="post" action="<%=request.getContextPath() + "/" + importAction%>" onsubmit="return checkProblemFile(this);" >
            <input name="contestId" type="hidden" value="<bean:write name="ContestForm" property="id" />">
            <p><b>Import Problems</b></p>
            <p>Select a .zip file <input name="problemFile" type="file"/>                
            </p>
            <p><input type="submit" value="Import Problems"
                ></p>
        </form>
        <form name="LocalProblemImportForm" method="post" action="<%=request.getContextPath() + "/" + importAction%>" onsubmit="return checkLocalProblemFile(this);">
            <input name="contestId" type="hidden" value="<bean:write name="ContestForm" property="id" />">
            <p><b>Local Import Problems</b></p>
            <p>Enter the .zip file path on the server <input name="problemFilePath" type="text"/>                
            </p>
            <p><input type="submit" value="Import Problems"
                ></p>
        </form>        
        
        
        <hr>
        <form name="ProblemExportForm" method="post" action="<%=request.getContextPath() + "/exportProblems.do"%>">
            <input name="contestId" type="hidden" value="<bean:write name="ContestForm" property="id" />">
            <p><b>Export Problems</b></p>
            <p><input type="submit" value="Export Problems"></p>
        </form>
    <%
    }
    %>
    </div>
    </logic:messagesNotPresent>

