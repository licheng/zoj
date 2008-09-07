<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Contest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Submission" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.AccessLog" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.Language" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.form.LogSearchForm" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.UserProfile" %>


        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
        <div id="content_title">
        Access Logs
        </div>
        <div id="content_body">
                
                <form id="LogSearchForm" name="LogSearchForm" action="<%=request.getContextPath()%>/showDashboard.do" method="GET">
                <div>
                <a href="<%=request.getContextPath() + "/showDashboard.do?timeStart=" + Utility.toTimestamp(new Date(System.currentTimeMillis() - 5 * 60 * 1000))%>">last 5 minutes</a>
                &nbsp;&nbsp;&nbsp; 
                <a href="<%=request.getContextPath() + "/showDashboard.do?timeStart=" + Utility.toTimestamp(new Date(System.currentTimeMillis() - 60 * 60 * 1000))%>">last 1 hour</a> 
                &nbsp;&nbsp;&nbsp; 
                <a href="<%=request.getContextPath() + "/showDashboard.do?timeStart=" + Utility.toTimestamp(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))%>">last day</a> 
                </div>
                <div>
                Time From: <input type="text" name="timeStart" value="<bean:write name="LogSearchForm" property="timeStart"/>"/>
                <logic:messagesPresent property="timeStart">
                <span class="error">
                    <html:errors property="timeStart"/>
                </span>
                </logic:messagesPresent>
                To: <input type="text" name="timeEnd"/ value="<bean:write name="LogSearchForm" property="timeEnd"/>">
                <logic:messagesPresent property="timeEnd">
                <span class="error">
                    <html:errors property="timeEnd"/>
                </span>
                </logic:messagesPresent>
                </div>
                <div>
                Action: <input type="text" name="action" style="width:100px" value="<bean:write name="LogSearchForm" property="action"/>"/>
                User: <input type="text" name="handle" style="width:100px" value="<bean:write name="LogSearchForm" property="handle"/>"/>
                IP <input type="text" name="ip"/ style="width:100px" value="<bean:write name="LogSearchForm" property="ip"/>">
                </div>
                <input type="submit" value="Search"/>
                </form>
                <logic:empty name="logs">
                <div>
                <br/>
                <font size='3' color='red'>No log available.</font>
                <br/>
                </div>
                </logic:empty>
                <logic:notEmpty name="logs">
                <div>
                <%
                Map p = (Map) request.getAttribute("parameters");
                int pageNumber = ((Integer) request.getAttribute("page")).intValue();
                %>
                <%
                if (pageNumber > 1) {
                	p.put("page", String.valueOf(pageNumber - 1));
                %>
                <html:link action="showDashboard" name="parameters">&lt;&lt;Previous</html:link>
                <% } else  {%>
                <font color="#777777">&lt;&lt;Previous</font>
                <% 
                }
                p.put("page", String.valueOf(pageNumber + 1));
                %>
                &nbsp;&nbsp;&nbsp;
                <html:link action="showDashboard" name="parameters">Next&gt;&gt;</html:link>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Page: <bean:write name="page" />
                </div>
                
                <table class="list">
                    <tr class="rowHeader">
                        <td class="logId">Log ID</td>
                        <td class="logAction">Action</td>
                        <% 
                        p.remove("page");
                        String orderBy = ((LogSearchForm) request.getAttribute("LogSearchForm")).getOrderBy();
                        if ("accessTimeDesc".equals(orderBy)) {
                        	p.put("orderBy", "accessTimeAsc");
                        } else {
                        	p.put("orderBy", "accessTimeDesc");
                        }
                        %>
                        <td class="logAccessTime"><html:link action="showDashboard" name="parameters">Access Time</html:link></td>
                        <% 
                        p.remove("page");
                        if ("timestampDesc".equals(orderBy) || orderBy == null || orderBy.trim().length() == 0) {
                            p.put("orderBy", "timestampAsc");
                        } else {
                            p.put("orderBy", "timestampDesc");
                        }
                        %>
                        <td class="logTimestamp"><html:link action="showDashboard" name="parameters">Timestamp</html:link></td>
                        <td class="logUser">User</td>
                        <td class="logIp">IP</td>
                    </tr>
                    
                    <%
                        
                    List logs = (List) request.getAttribute("logs");
                    for (int i = 0; i < logs.size(); ++i) {
                         AccessLog log = (AccessLog) logs.get(i);
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td class="logId"><%=log.getId()%></td>
                        <td class="logAction"><%=log.getAction()%></td>
                        <td class="logAccessTime"><%=log.getAccessTime()%></td>
                        <td class="logTimestamp"><%=Utility.toTimestamp(log.getTimestamp())%></td>
                        <td class="logUser"><%=log.getHandle() == null ? "" : log.getHandle()%></td>
                        <td class="logIp"><%=log.getIp()%></td>
                    </tr>
                    <%
                    }
                    %>
                </table>
                
                </logic:notEmpty>
        </div>
        </logic:messagesNotPresent>