<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.form.ProblemForm" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Limit" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>


<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Iterator" %>
<%
    String actionPath = request.getContextPath() + "/addProblem.do";
    String submitName = "Add Problem";
    String title = "Add Problem";
    if ("EditProblem".equals(request.getAttribute("pageId"))) {
        title = "Edit Problem";
        actionPath = request.getContextPath() + "/editProblem.do";
        submitName = "Edit Problem";
    } else if ("AddContestProblem".equals(request.getAttribute("pageId"))) {
        title = "Add Contest Problem";
        actionPath = request.getContextPath() + "/addContestProblem.do";
        submitName = "Add Problem";
    } else if ("EditContestProblem".equals(request.getAttribute("pageId"))) {
        title = "Edit Contest Problem";
        actionPath = request.getContextPath() + "/editContestProblem.do";
        submitName = "Edit Problem";
    }

    boolean isProblemset = "EditProblem".equals(request.getAttribute("pageId"))
        || "AddProblem".equals(request.getAttribute("pageId"));

    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
    
    Limit defaultLimit = contest.getLimit();
    ProblemForm problemForm = (ProblemForm) request.getAttribute("ProblemForm");
%>

<script language="JavaScript">

function switchLimits() {
    if (document.problemForm.useContestDefault.checked) {
        document.problemForm.timeLimit.disabled = true;
        document.problemForm.memoryLimit.disabled = true;
        document.problemForm.outputLimit.disabled = true;
        document.problemForm.submissionLimit.disabled = true;
    } else {
        document.problemForm.timeLimit.disabled = false;
        document.problemForm.memoryLimit.disabled = false;
        document.problemForm.outputLimit.disabled = false;
        document.problemForm.submissionLimit.disabled = false;
    }
}
function deleteProblem(problemId) {
    if (!confirm("Are you sure to delete this problem?")) {
      return;
    }
    location.href="<%=request.getContextPath()%>/deleteProblem.do?problemId=" + problemId;
}
</script>

    <logic:messagesPresent property="error">
    <div class="internalError">
        <html:errors property="error"/>
    </div>
    </logic:messagesPresent>

    <logic:messagesNotPresent property="error">
<form enctype="multipart/form-data" name="problemForm" method="post" action="<%=actionPath%>">
            <input name="problemId" type="hidden"
                        <logic:notEmpty name="ProblemForm" property="problemId">
                            value="<bean:write name="ProblemForm" property="problemId" />"
                        </logic:notEmpty>         
                        <logic:empty name="ProblemForm" property="problemId">
                            value="0"
                        </logic:empty>                                       
            >
            <input name="contestId" type="hidden" value="<bean:write name="ProblemForm" property="contestId" />"
            >    	
    <div id="content_title"><%=title%></div>
    <div id="content_body">  

        <table>
            <tr/>
            
            <tr>
                <td align="right">Title</td>
                <td>
                    <input name="name" type="text"
                        value="<bean:write name="ProblemForm" property="name" />"
                    >
                    <span class="error">*
                        <logic:messagesPresent property="name">
                            <html:errors property="name"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>                               	              
            
            <tr>
                <td align="right">Code</td>
                <td>
                    <input name="code" type="text"
                        value="<bean:write name="ProblemForm" property="code" />"
                    >
                    <span class="error">*
                        <logic:messagesPresent property="code">
                            <html:errors property="code"/>
                        </logic:messagesPresent>
                    </span>
                <td> 
            </tr>

            
            <tr>
                <td align="right">Source</td>
                <td>
                    <input name="source" type="text"
                        value="<bean:write name="ProblemForm" property="source" />"
                    >
                    <span class="error">
                        <logic:messagesPresent property="source">
                            <html:errors property="source"/>
                        </logic:messagesPresent>
                    </span>
                <td> 
            </tr>

            
            <tr>
                <td align="right">Contest</td>
                <td>
                    <input name="contest" type="text"
                        value="<bean:write name="ProblemForm" property="contest" />"
                    >
                    <span class="error">
                        <logic:messagesPresent property="contest">
                            <html:errors property="contest"/>
                        </logic:messagesPresent>
                    </span>
                <td> 
            </tr>


            <tr>
                <td align="right">Author</td>
                <td>
                    <input name="author" type="text"
                        value="<bean:write name="ProblemForm" property="author" />"
                    >
                    <span class="error">
                        <logic:messagesPresent property="author">
                            <html:errors property="author"/>
                        </logic:messagesPresent>
                    </span>
                <td> 
            </tr>
            <tr>
                <td align="right">Color</td>
                <td>
                    <input name="color" type="text"
                        value="<bean:write name="ProblemForm" property="color" />"
                    > <font color="red">red</font>, <font color="black">black</font>, <font color="008000">008000</font>, ..., <a href="<%=request.getContextPath()%>/color_table.html" target="_blank">more</a>
                    <span class="error">
                        <logic:messagesPresent property="color">
                            <html:errors property="color"/>
                        </logic:messagesPresent>
                    </span>
                <td> 
            </tr>

            
            <tr>
                <td align="right">Special Judge</td>
                <td>
                    <input type="checkbox" name="specialJudge"
                    <%=problemForm.isSpecialJudge() ? "checked" : "" %>
                    >
                <td> 
            </tr>

            
            <tr/>
            
            <tr>
                <td align="right">Time Limit</td>
                <td>
                    <input name="timeLimit" type="text"
                        <%
                          if (problemForm.getProblemId() == null || problemForm.isUseContestDefault()) {
                        %>
                            disabled
                            value="<%=defaultLimit.getTimeLimit()%>"
                        <%
                          } else {
                        %>
                            value="<%=problemForm.getTimeLimit()%>"
                        <%
                          }
                        %>

                    >
                    <span class="error">s *
                        <logic:messagesPresent property="timeLimit">
                            <html:errors property="timeLimit"/>
                        </logic:messagesPresent>

                    </span>
                <td> 
            </tr>


            <tr>
                <td align="right">Memory Limit</td>
                <td>
                    <input name="memoryLimit" type="text"
                        <%
                          if (problemForm.getProblemId() == null || problemForm.isUseContestDefault()) {
                        %>
                            disabled
                            value="<%=defaultLimit.getMemoryLimit()%>"
                        <%
                          } else {
                        %>
                            value="<%=problemForm.getMemoryLimit()%>"
                        <%
                          }
                        %>
                    >
                    <span class="error">KB *
                        <logic:messagesPresent property="memoryLimit">
                            <html:errors property="memoryLimit"/>
                        </logic:messagesPresent>


                    </span>
                <td> 
            </tr>


            <tr>
                <td align="right">Output Limit</td>
                <td>
                    <input name="outputLimit" type="text"
                        <%
                          if (problemForm.getProblemId() == null || problemForm.isUseContestDefault()) {
                        %>
                            disabled
                            value="<%=defaultLimit.getOutputLimit()%>"
                        <%
                          } else {
                        %>
                            value="<%=problemForm.getOutputLimit()%>"
                        <%
                          }
                        %>

                    >
                    <span class="error">KB *
                        <logic:messagesPresent property="outputLimit">
                            <html:errors property="outputLimit"/>
                        </logic:messagesPresent>
                    </span>
                <td> 
            </tr>


            <tr>
                <td align="right">Submission Limit</td>
                <td>
                    <input name="submissionLimit" type="text"
                        <%
                          if (problemForm.getProblemId() == null || problemForm.isUseContestDefault()) {
                        %>
                            disabled
                            value="<%=defaultLimit.getSubmissionLimit()%>"
                        <%
                          } else {
                        %>
                            value="<%=problemForm.getSubmissionLimit()%>"
                        <%
                          }
                        %>

                    >
                    <span class="error">KB *
                        <logic:messagesPresent property="submissionLimit">
                            <html:errors property="submissionLimit"/>
                        </logic:messagesPresent>
                    </span>                
                <td> 
            </tr>
            <tr>
                <td align="right">Use Contest Default</td>
                <td>
                    <input type="checkbox" name="useContestDefault"
                    <%=problemForm.getProblemId() == null || problemForm.isUseContestDefault() ? "checked" : "" %>
                    onclick="javascript:switchLimits();return true;"
                    >
                <td> 
            </tr>

            <tr>
                <td align="right">Description</td>
                <td>
                    <input name="description" type="file"/><br>
                <logic:present name="DescriptionRef">
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="DescriptionRef" property="id"/>&code=<%=problemForm.getCode()%>" target="_blank">View</a>
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="DescriptionRef" property="id"/>&code=<%=problemForm.getCode()%>&download=true" target="_blank">Download</a>
                </logic:present>   
                <td> 
            </tr>
            
            <tr>
                <td align="right">Input</td>
                <td>
                    <input name="inputData" type="file"/><br>                 
                <logic:present name="InputRef">
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="InputRef" property="id"/>&code=<%=problemForm.getCode()%>" target="_blank">View</a>
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="InputRef" property="id"/>&code=<%=problemForm.getCode()%>&download=true" target="_blank">Download</a>
                </logic:present>  
                
                <td> 
            </tr>
            <tr>
                <td align="right">Output</td>
                <td>
                    <input name="outputData" type="file"/><br>                
                <logic:present name="OutputRef">
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="OutputRef" property="id"/>&code=<%=problemForm.getCode()%>" target="_blank">View</a>
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="OutputRef" property="id"/>&code=<%=problemForm.getCode()%>&download=true" target="_blank">Download</a>
                </logic:present>  
                <td> 
            </tr>
            <tr>
                <td align="right">Judge Solution</td>
                <td>
                    <input name="judgeSolution" type="file"/><br>                    
                <logic:present name="JudgeSolutionRef">
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="JudgeSolutionRef" property="id"/>&code=<%=problemForm.getCode()%>" target="_blank">View</a>
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="JudgeSolutionRef" property="id"/>&code=<%=problemForm.getCode()%>&download=true" target="_blank">Download</a>
                </logic:present>  
                <td> 
            </tr>
            <tr>
                <td align="right">Checker</td>
                <td>
                    <input name="checker" type="file"/><br>                  
                <logic:present name="CheckerRef">                    
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="CheckerRef" property="id"/>&code=<%=problemForm.getCode()%>&download=true" target="_blank">Download</a>
                </logic:present>  
                <td> 
            </tr>
            <tr>
                <td align="right">Checker Source</td>
                <td>
                    <input name="checkerSource" type="file"/><br>                  
                <logic:present name="CheckerSourceRef">
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="CheckerSourceRef" property="id"/>&code=<%=problemForm.getCode()%>" target="_blank">View</a>
                    <a href="<%=request.getContextPath()%>/showReference.do?referenceId=<bean:write name="CheckerSourceRef" property="id"/>&code=<%=problemForm.getCode()%>&download=true" target="_blank">Download</a>
                </logic:present>  
                <td> 
            </tr>
            
            <tr/>
        </table> 
        <blockquote>          
            <input name="submit" type="submit" id="submit" value="<%=submitName%>">
        </blockquote>
    </div>
</form>    
    </logic:messagesNotPresent>

