<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Features" %>
                <tr><td class="nav_header">
                <img src="<%=request.getContextPath()%>/image/arrow_sub2.gif"><div><a href="<%=request.getContextPath()%>">Home</a></div></img>
                </td></tr>
                <logic:present name="oj_user">
<% if (Features.register()) {%>                
                <tr><td class="<%="EditProfile".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                    <a href="<%=request.getContextPath()%>/editProfile.do">Edit Profile</a>
                </td></tr>
<% } %>                
<%--                
                <tr><td class="<%="EditPreference".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                        <a href="<%=request.getContextPath()%>/editPreference.do">Edit Preference</a>
                </td></tr>
--%>                
                </logic:present>
                <tr><td class="<%="FAQ".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                        <a href="<%=request.getContextPath()%>/faq.do">FAQ</a>
                </td></tr>
<%--                
                <tr><td class="<%="About".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                        <a href="<%=request.getContextPath()%>/about.do">About This Site</a>
                </td></tr>
                <tr><td class="<%="ContactUs".equals(request.getAttribute("pageId")) ? "selected_item" : "item"%>">
                        <a href="<%=request.getContextPath()%>/contactUs.do">Contact Us</a>
                </td></tr>
--%>
                <tr><td class="icpc_logo"><img src="<%=request.getContextPath()%>/image/cpc_acm.jpg"/></td></tr>
