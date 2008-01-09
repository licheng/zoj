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
    if ("EditContest".equals(request.getAttribute("pageId"))) {
        actionPath = request.getContextPath() + "/editContest.do";
        submitName = "Edit Contest";
        importOrExport = "Export Contest";
    }        
    Limit defaultLimit = PersistenceManager.getInstance().getContestPersistence().getDefaultLimit();
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

</script>



<form name="contestForm" method="post" action="<%=actionPath%>">

            <input name="id" type="hidden"
                        <logic:notEmpty name="ContestForm" property="id">
                            value="<bean:write name="ContestForm" property="id" />"
                        </logic:notEmpty>
                        <logic:empty name="ContestForm" property="id">
                            value="0"
                        </logic:empty>
            >
    <logic:messagesPresent property="error">
    <div class="internalError">
        <html:errors property="error"/>
    </div>
    </logic:messagesPresent>

    <fieldset>
        <legend>Manage Contests</legend><br>
            
        <ul class="contestFields">
            <li>
            </li>
            <li>
                <div class="contestFieldHeader">Contest Information</div>
            </li>
        
            <li>
                <div class="contestFieldName">Contest Name</div>
                <div class="contestField">
                    <input name="name" type="text"
                        value="<bean:write name="ContestForm" property="name" />"
                        
                    >
                    <span class="error">*
                        <logic:messagesPresent property="name">
                            <html:errors property="name"/>
                        </logic:messagesPresent>
                    </span>                    
                </div>
            </li>
            
            <li>
                <div class="contestFieldName">Description</div>
                <div class="contestField">                    
                    <input name="description" type="text"
                        value="<bean:write name="ContestForm" property="description" />"
                        
                    >            
                </div>
            </li>
            
            <li>
                <div class="contestFieldName">Start Time</div>
                <div class="contestField">
                    <input name="startTime" type="text"
                        <logic:present name="ContestForm">
                            value="<bean:write name="ContestForm" property="startTime" />"
                        </logic:present>
                    >
                    <span class="error">* (yyyy-mm-dd HH:MM:SS) 
                        <logic:messagesPresent property="startTime">
                            <html:errors property="startTime"/>
                        </logic:messagesPresent>
                    </span>                    
                </div>
            </li>
            
            <li>
                <div class="contestFieldName">Contest Length</div>
                <div class="contestField">
                    <input name="contestLength" type="text"
                        <logic:present name="ContestForm">
                            value="<bean:write name="ContestForm" property="contestLength" />"
                        </logic:present>
                    >
                    <span class="error">* (HHH:MM:SS) 
                        <logic:messagesPresent property="contestLength">
                            <html:errors property="contestLength"/>
                        </logic:messagesPresent>
                    </span>                    
                </div>
            </li>
            <li>
                <div class="contestFieldName">Forum Link</div>
                <div class="contestField">
                    <%
                        String selectedForumId = "1";
                        ContestForm contestForm = (ContestForm) request.getAttribute("ContestForm");
                        if (contestForm != null && contestForm.getForumId() != null) {
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
                </div>
            </li>            
            <li>
                <div class="contestFieldName">Problemset</div>
                <div class="contestField">
                    <input type="checkbox" name="problemset" 
                    <%=contestForm != null && contestForm.isProblemset() ? "checked" : "" %>                    
                    >
                </div>
            </li>
            
            
            <li/>
            <li>
                <div class="contestFieldHeader">Default Limits</div>
            </li>
            <li>
                <div class="contestFieldName">Time Limit</div>
                <div class="contestField">
                    <input name="timeLimit" type="text"
                        <%
                          if (contestForm == null || contestForm.isUseGlobalDefault()) {
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
                </div>
            </li>
            
            <li>
                <div class="contestFieldName">Memory Limit</div>
                <div class="contestField">
                    <input name="memoryLimit" type="text"
                        <%
                          if (contestForm == null || contestForm.isUseGlobalDefault()) {
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
                </div>
            </li>
            
            <li>
                <div class="contestFieldName">Output Limit</div>
                <div class="contestField">
                    <input name="outputLimit" type="text"
                        <%
                          if (contestForm == null || contestForm.isUseGlobalDefault()) {
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
                </div>
            </li>
            
            <li>
                <div class="contestFieldName">Submission Limit</div>
                <div class="contestField">
                    <input name="submissionLimit" type="text"
                        <%
                          if (contestForm == null || contestForm.isUseGlobalDefault()) {
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
                </div>
            </li>
            <li>
                <div class="contestFieldName">Use Global Default</div>
                <div class="contestField">
                    <input type="checkbox" name="useGlobalDefault" 
                    <%=contestForm == null || contestForm.isUseGlobalDefault() ? "checked" : "" %>
                    onclick="javascript:switchLimits();return true;"
                    >
                </div>
            </li>
            
            <li/>
            <li>
                <div class="contestFieldHeader">Language Select</div>
            </li>
            
            <li>
                <div class="contestFieldName">Language List</div>
                <div class="contestField">
                    <%
                        List allLanguages = PersistenceManager.getInstance().getContestPersistence().getAllLanguages();
                        Set selectedIds = new HashSet();                        
                        if (contestForm != null && contestForm.getLanguageIds() != null) {
                            selectedIds.addAll(Arrays.asList(contestForm.getLanguageIds()));
                        } 
                          
                    %>
                    
                    <select name="languageIds" multiple>
                    <%
                        for (Iterator it = allLanguages.iterator(); it.hasNext();) {                    
                            Language language = (Language) it.next();
                    %>
                        <option value="<%=language.getId()%>" <%=contestForm == null || selectedIds.contains(String.valueOf(language.getId())) ? "selected" : "" %> ><%=language.getName()%></option>  
                    <%
                    }
                    %>
                    </select>    
                    <span class="error">
                        <logic:messagesPresent property="languageIds">
                            <html:errors property="languageIds"/>
                        </logic:messagesPresent>
                    </span>                    
                </div>
            </li>
            <li>   
    <div class="contestButtons">
        <div class="contestButton">
            <input name="submit" type="submit" id="submit" value="<%=submitName%>">
        </div>
        <div class="contestButton">
            <input name="importExport" type="submit" id="importExport" value="<%=importOrExport%>" onClick="javascript:return false;">
        </div>
        
        <div class="contestButton">
            <input name="cacel" type="reset" id="cacel" value="Cancel" onchange="javascript:switchLimits();return true;">
        </div>
    </div>
    </li>
    </ul>
    </fieldset>
</form>
