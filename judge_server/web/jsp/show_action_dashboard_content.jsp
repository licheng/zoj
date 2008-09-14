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
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ActionLog" %>

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
        Action Logs | <span style="font-size:12px"><html:link action="showDashboard">Access Logs</html:link></span>
        
        </div>
        <div id="content_body">
                
                <form id="LogSearchForm" name="LogSearchForm" action="<%=request.getContextPath()%>/showActionDashboard.do" method="GET">
                <div>
                <a href="<%=request.getContextPath() + "/showActionDashboard.do?timeStart=" + Utility.toTimestamp(new Date(System.currentTimeMillis() - 5 * 60 * 1000))%>">last 5 minutes</a>
                &nbsp;&nbsp;&nbsp; 
                <a href="<%=request.getContextPath() + "/showActionDashboard.do?timeStart=" + Utility.toTimestamp(new Date(System.currentTimeMillis() - 60 * 60 * 1000))%>">last hour</a> 
                &nbsp;&nbsp;&nbsp; 
                <a href="<%=request.getContextPath() + "/showActionDashboard.do?timeStart=" + Utility.toTimestamp(new Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000))%>">last day</a> 
                &nbsp;&nbsp;&nbsp; 
                <a href="<%=request.getContextPath() + "/showActionDashboard.do?timeStart=" + Utility.toTimestamp(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000))%>">last month</a> 
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
                %>
                
                <table class="list">
                    <tr class="rowHeader">
                        <% p.put("orderBy", "action"); %>
                        <td class="logAction"><html:link action="showActionDashboard" name="parameters">action</html:link></td>
                        <% p.put("orderBy", "count"); %>
                        <td class="logAction"><html:link action="showActionDashboard" name="parameters">count</html:link></td>
                        <% p.put("orderBy", "avg"); %>
                        <td class="logAction"><html:link action="showActionDashboard" name="parameters">avg access time</html:link></td>
                        <% p.put("orderBy", "min"); %>
                        <td class="logAction"><html:link action="showActionDashboard" name="parameters">min access time</html:link></td>
                        <% p.put("orderBy", "max"); %>
                        <td class="logAction"><html:link action="showActionDashboard" name="parameters">max access time</html:link></td>
                    </tr>
                    
                    <%
                    List logs = (List) request.getAttribute("logs");
                    long allCount = 0;
                    long allAvg = 0;
                    long allMin = Long.MAX_VALUE;
                    long allMax = 0;
                    for (int i = 0; i < logs.size(); ++i) {
                         ActionLog log = (ActionLog) logs.get(i);
                         allCount += log.getCount();
                         allAvg += log.getAvgAccessTime() * log.getCount();
                         allMin = Math.min(allMin, log.getMinAccessTime());
                         allMax = Math.max(allMax, log.getMaxAccessTime());
                         
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <% p.put("action", log.getAction()); %>
                        <td class="logAction"><html:link action="showDashboard" name="parameters"><%=log.getAction()%></html:link></td>
                        <td class="logAccessTime"><%=log.getCount()%></td>
                        <td class="logAccessTime"><%=log.getAvgAccessTime()%></td>
                        <td class="logAccessTime"><%=log.getMinAccessTime()%></td>
                        <td class="logAccessTime"><%=log.getMaxAccessTime()%></td>
                    </tr>
                    <%
                    }
                    %>
                     <tr class="<%=logs.size() % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td class="logAction"><b>Summary</b></td>
                        <td class="logAccessTime"><b><%=allCount%></b></td>
                        <td class="logAccessTime"><b><%=allAvg / allCount%></b></td>
                        <td class="logAccessTime"><b><%=allMin%></b></td>
                        <td class="logAccessTime"><b><%=allMax%></b></td>
                    </tr>
                </table>
                
                </logic:notEmpty>
        </div>
        </logic:messagesNotPresent>