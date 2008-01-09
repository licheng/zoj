<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.JudgeReply" %>
<%@ page import="java.util.List" %>
                <div id="content_title">Judge Reply</div>
                <div id="content_body">
                    <blockquote>
                    <br>
                    <br>
                    <table>
                    <tr class="rowHeader">
                        <td width=40>ID</td>
                        <td width=60>Name</td>
                        <td width=200>Description</td>
                        <td width=100>Style</td>
                    </tr>
                    <%
                    List judgeReplies = (List) request.getAttribute("JudgeReplies");
                    for (int i = 0; i < judgeReplies.size(); ++i) {
                        JudgeReply judgeReply = (JudgeReply) judgeReplies.get(i);                        
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td><%=judgeReply.getId()%></td>
                        <td><%=judgeReply.getName()%></td>
                        <td><%=judgeReply.getDescription()%></td>
                        <td></td>
                    </tr>
                    <%
                    }
                    %>
                    </table>
                    </blockquote>
                </div>

                                                