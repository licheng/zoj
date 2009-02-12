<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%> 
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.StatisticsManager" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.PersistenceManager" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.UserProfile" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.Problem" %>
<hr>
<H2><font color=red>Add Book Reader Information</font></H2>
<hr>
<html:form action="/addBookReader">
			username      : <html:text property="username"/><html:errors property="username"/><br/>
			password      : <html:password property="password"/><html:errors property="password"/><br/>
			<html:submit/><html:cancel/>
		</html:form>
<hr>
<H2><font color=red>Add Teacher Information</font></H2>
<hr>
<html:form action="/addTeacher">
			username      : <html:text property="username"/><html:errors property="username"/><br/>
			password      : <html:password property="password"/><html:errors property="password"/><br/>
			<html:submit/><html:cancel/>
		</html:form>
<hr>