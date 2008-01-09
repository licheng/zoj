<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.enumeration.Language" %>
<%@ page import="java.util.List" %>
                <div id="content_title">Language</div>
                <div id="content_body">
                    <blockquote>
                    <br>
                    <br>
                    <table>
                    <tr class="rowHeader">
                        <td width=40>ID</td>
                        <td width=60>Name</td>
                        <td width=200>Description</td>
                        <td width=100>Options</td>
                    </tr>
                    <%
                    List languages = (List) request.getAttribute("Languages");
                    for (int i = 0; i < languages.size(); ++i) {
                        Language language = (Language) languages.get(i);                        
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td><%=language.getId()%></td>
                        <td><%=language.getName()%></td>
                        <td><%=language.getDescription()%></td>
                        <td><%=language.getOptions()%></td>
                    </tr>
                    <%
                    }
                    %>
                    </table>
                    </blockquote>
                </div>

                