<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.QQ" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>
<%
%>

        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title"><bean:write name="contest" property="title"/></div>
        <div id="content_body">
                <table class="list">
                    <tr class="rowHeader">
                        <td align="center">ID</td>
                        <td align="center">Team</td>
                        <td align="center">Name</td>
                        <td align="center">Problem</td>
                        <td align="center" width='40px'>Color</td>
                        <td align="center">Time</td>
                        <td align="center">Status</td>
                        <td align="center">Action</td>
                    </tr>
                    <%
                    AbstractContest contest = (AbstractContest) request.getAttribute("contest");                    
                    List qqs = (List) request.getAttribute("qqs");
                    for (int i = 0; i < qqs.size(); ++i) {
                         QQ qq = (QQ) qqs.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td align="center"><%=qq.getSubmissionId()%></td>
                        <td align="center"><%=qq.getHandle()%></td>
                        <td align="center"><%=qq.getNickName()%></td>
                        <td align="center"><%=qq.getCode()%></td>                                                                                                
                        <td align="center" width='40px' bgcolor="<%=qq.getColor() == null ? "white" : qq.getColor()%>"></td>                                                                                                
                        <td align="center"><%=Utility.toTimestamp(qq.getSubmissionDate())%></td>                                                                                                
                        <td align="center"><%=qq.getStatus()%></td>    
						<td align="center">
						<% if (QQ.QQ_NEW.equals(qq.getStatus())) { %>
							<a href="<%=request.getContextPath()%>/changeQQStatus.do?problemId=<%=qq.getProblemId()%>&userProfileId=<%=qq.getUserProfileId()%>&status=<%=QQ.QQ_DELIVERING%>">Deliver</a>
						<% } else if (QQ.QQ_DELIVERING.equals(qq.getStatus())) { %>
							<a href="<%=request.getContextPath()%>/changeQQStatus.do?problemId=<%=qq.getProblemId()%>&userProfileId=<%=qq.getUserProfileId()%>&status=<%=QQ.QQ_FINISHED%>">Finish</a>
						<% } %>
						</td>                                                                                           
                    </tr>
                    <%
                    }
                    %>
                </table>
        </div>

        </logic:messagesNotPresent>
