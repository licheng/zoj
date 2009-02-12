<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.bean.UserProfile" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.TreeSet" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.UserStatistics" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problemset" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>

<%
    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
    boolean isProblemset = contest instanceof Problemset;
    String problemPath = request.getContextPath() + (isProblemset ? "/showProblem.do" : "/showContestProblem.do") + "?problemCode=";
    String contestPath = request.getContextPath() + (isProblemset ? "/showProblems.do" : "/showContestProblems.do") + "?contestId=";
    UserStatistics statistics = (UserStatistics) request.getAttribute("UserStatistics");
    UserProfile user = (UserProfile) request.getAttribute("user");
    
%>

    <logic:messagesPresent property="error">
    <div class="internalError">
        <html:errors property="error"/>
    </div>
    </logic:messagesPresent>

<logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
<logic:messagesNotPresent property="error">
        <div id="content_title">User Statistics
        <logic:present name="contest"> 
        - <a href="<%=contestPath + contest.getId()%>"><bean:write name="contest" property="title"/></a>
        </logic:present>
        </div>
        <div id="content_body">
        <br/>
	    <blockquote>
        <logic:notPresent name="user">
	        <font color="red" size="5">No such user.</font>
	    </logic:notPresent>
	    <logic:present name="user">
        <logic:notPresent name="contest">
            <font color="red" size="5">No such contest.</font>
        </logic:notPresent>
        <logic:present name="contest">
        <div>
        <font color="db6d00" size="5"><bean:write name="user" property="handle"/></font>
        <logic:notEmpty name="user" property="nickName"><font size="4">(<bean:write name="user" property="nickName"/>)</font></logic:notEmpty>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <font size="3">AC Ratio:</font> <font color="red" size="4"><%=statistics.getSolved().size()%>/<%=statistics.getTotal()%> </font><br/>
        </div>
        <logic:notEmpty name="preference" property="plan">
        <br/>
        <div>
        <b>Plan:</b><br/><pre><bean:write name="preference" property="plan"/></pre>
        </div>
        </logic:notEmpty>
        <br/>
        <div>
        <b>Solved Problems:</b><br/>
        <% 
            if (statistics.getSolved() != null) {
                TreeSet<String> s=new TreeSet<String>();
                for (Iterator it = statistics.getSolved().iterator(); it.hasNext();) {
                    Problem problem = (Problem) it.next();
            		s.add(problem.getCode());
            	}
            	for (Iterator it = s.iterator(); it.hasNext();) {
            		String problemCode = (String) it.next();
        %>
               <a href="<%=problemPath + problemCode%>"><%=problemCode%></a>
        <% }} %>
        </div>
        </logic:present>
        </logic:present>
        </blockquote>
        </div>
</logic:messagesNotPresent>
