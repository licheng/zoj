<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ConfigManager" %>
<div id="content_title">
    ZOJ News</div>
<div id="content_body">
<% 
File inputFile=new File(ConfigManager.getDefaultHomeContent());
FileReader in=new FileReader(inputFile);
BufferedReader inputText=new BufferedReader(in);
String line=null;
line=inputText.readLine();  
while(line!=null)
{
out.printline(line);
}
%>
</div>