<%@ page contentType="text/html; charset=utf-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
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
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.form.AddUserForm" %>
<% AbstractContest contest = (AbstractContest) request.getAttribute("contest");
UserProfile userProfile = (UserProfile) request.getAttribute("userProfile");%>
<hr>
<H2><font color=red>Add Student Information</font></H2>
<hr>
<form enctype="multipart/form-data"  name="addUserForm" method="post" action="<%=request.getContextPath()%>/addUser.do">
			username      : <input name="username" type="text"/><br/>
			studentNumber : <input name="studentNumber" type="text"/><br/>
			password      : <input name="password" type="text"/><br/>
			<input type="submit" value="Add User">
</form>
<hr>
<H2><font color=red>Change Student Password</font></H2>
<hr>
<form enctype="multipart/form-data"  name="addUserForm" method="post" action="<%=request.getContextPath()%>/changePassword.do">
			studentNumber : <input name="studentNumber" type="text"/><br/>
			password      : <input name="password" type="text"/><br/>
			<input type="submit" value="ChangePassword">
</form>
<hr>
<H2><font color=red>Delete Student Information</font></H2>
<hr>

 <table width=100% border >
 <tr>
	    <td align=center width=5%><font color=blue>No.</font></td>
		<td align=center width=10%><font color=blue>Name</font></td>
		<td align=center width=10%><font color=blue>StudentID</font></td>
		<td align=center width=10%><font color=blue>1001</font></td>
		<td align=center width=10%><font color=blue>1002</font></td>
		<td align=center width=10%><font color=blue>1003</font></td>
		<td align=center width=10%><font color=blue>1004</font></td>
		<td align=center width=10%><font color=blue>1005</font></td>
		<td align=center width=10%><font color=blue>1006</font></td>
		<td align=center width=10%><font color=blue>1007</font></td>
		<td align=center width=10%><font color=blue>1008</font></td>
		<td align=center width=10%><font color=blue>Score</font></td>
		<td align=center><font color=blue>Admin<font></td>
	</tr>
<%
  List students=(List)request.getAttribute("students");
  HashSet<String> set = new HashSet<String>();
  if(students!=null) {
	for(int i =0; i<students.size();++i) {
		set.clear();
		UserProfile u = (UserProfile)students.get(i);
		Set conformed = StatisticsManager.getInstance().getUserStatistics(contest.getId(), u.getId()).getConfirmed();
		int conformedScore=0;
		for(Object o : conformed)
		{
			Problem p = (Problem)o;
			conformedScore+=p.getScore();
			set.add(p.getCode());
		}
		%>
		 <tr>
		<td align=center width=5%><font color=blue><%=i+1 %></font></td>
		<td align=center width=10%><font color=blue><%=u.getFirstName()+u.getLastName() %></font></td>
		<td align=center width=10%><font color=blue><%=u.getStudentNumber() %></font></td>
		<td align=center width=10%><font color=red><%=set.contains("1001")? "Yes" : "&nbsp;" %></font></td>
		<td align=center width=10%><font color=red><%=set.contains("1002")? "Yes" : "&nbsp;" %></font></td>
		<td align=center width=10%><font color=red><%=set.contains("1003")? "Yes" : "&nbsp;" %></font></td>
		<td align=center width=10%><font color=red><%=set.contains("1004")? "Yes" : "&nbsp;" %></font></td>
		<td align=center width=10%><font color=red><%=set.contains("1005")? "Yes" : "&nbsp;" %></font></td>
		<td align=center width=10%><font color=red><%=set.contains("1006")? "Yes" : "&nbsp;" %></font></td>
		<td align=center width=10%><font color=red><%=set.contains("1007")? "Yes" : "&nbsp;" %></font></td>
		<td align=center width=10%><font color=red><%=set.contains("1008")? "Yes" : "&nbsp;" %></font></td>
		<td align=center width=10%><font color=red><%=conformedScore %></font></td>
		<td align=center><font color=blue><a href="<%=request.getContextPath()%>/deleteUser.do?userId=<%=u.getId() %>">Delete</a><font></td>
		</tr>
		<%
	}
}%>
 </table>
 <a href="<%=request.getContextPath()%>/exportCodes.do"><font color="red">Export Student Code</font></a>&nbsp;&nbsp;&nbsp;
 <hr>
<center>
<H2><font color="red">Import Student Information</font><br></H2>
<hr>
</center>
<p>
提交的数据文件格式为文本文件，每行包括两个字段，分别是学生的学号和姓名，用空格分隔，提交成功以后将对应每行数据生成一条学生纪录，登陆id和密码相同，都是学号。
</p>
<p>
数据文件范例:<br>
<B><pre>
20111212 张三
20111213 李四
</pre>
</B>
</p>
<hr>
 <form enctype="multipart/form-data" name="importUserForm" method="post" action="<%=request.getContextPath()%>/importUser.do">
			userinfo : <input name="userinfo" type="file"/><br>   <br/>
			<input name="submit" type="submit" id="submit" value="upload">
		</form>
