<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ConfigManager" %>
<% 
File inputFile=new File(ConfigManager.getDefaultHomeContent());
try
{
FileReader in=new FileReader(inputFile);
BufferedReader inputText=new BufferedReader(in);
String line=null;
line=inputText.readLine();  
while(line!=null)
{
out.println(line);
line=inputText.readLine();  
}
} catch (Exception e) {
}
%>