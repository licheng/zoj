<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.UserSecurity" %>

                <tr><td class="nav_header">
                    <img src="<%=request.getContextPath()%>/image/arrow_sub2.gif"><div><a href="<%=request.getContextPath()%>/showCourses.do">Courses</a></div></img>
                </td></tr>
                <logic:notPresent name="contest">
                <tr><td class="<%="ShowCourses".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                        <a href="<%=request.getContextPath()%>/showCourses.do">Select Course</a>
                </td></tr>
                </logic:notPresent>
                <logic:present name="contest">

                <tr><td class="<%="CourseInfo".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/courseInfo.do?contestId=<bean:write name="contest" property="id"/>">Information</a>
                </td></tr>
                <tr><td class="<%="CourseProblems".equals(request.getAttribute("pageId")) || "ShowContestProblem".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showCourseProblems.do?contestId=<bean:write name="contest" property="id"/>">Problems</a>
                </td></tr>
                <tr><td class="<%="ShowCourseRuns".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/showCourseRuns.do?contestId=<bean:write name="contest" property="id"/>">Runs</a>
                </td></tr>
                <logic:present name="oj_security">
                
                <%
                AbstractContest contest = (AbstractContest) request.getAttribute("contest");
                UserSecurity userSecurity = (UserSecurity) request.getSession().getAttribute("oj_security");                                
                if (userSecurity.canAdminContest(contest.getId())) {
                %>
                <tr><td class="<%="EditCourse".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/editCourse.do?contestId=<bean:write name="contest" property="id"/>">Edit Course</a>
                </td></tr>
                <tr><td class="<%="AddCourseProblem".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/addCourseProblem.do?contestId=<bean:write name="contest" property="id"/>">Add Problem</a>
                </td></tr>
                <%
                }
                %>  
                                
                </logic:present>
                </logic:present>

                <tr><td class="icpc_logo"><img src="<%=request.getContextPath()%>/image/cpc_acm.jpg"/></td></tr>
