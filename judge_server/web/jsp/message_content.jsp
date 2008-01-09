<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
        <div id="content_title"><%=request.getAttribute("title") == null ? "Message" : request.getAttribute("title").toString()%></div>
        <div id="content_body">
            <logic:messagesPresent property="error">
            <div class="internalError">
                <html:errors property="error"/><br>
            </div>
            </logic:messagesPresent>
            
            <div class="message">
                <logic:messagesPresent property="message"><html:errors property="message"/></logic:messagesPresent>
            </div> 
            <logic:present name="back">
            <div class="back">
                <a href="<%=request.getContextPath() + "/" + request.getAttribute("back")%>">Back</a>
            </div>
            </logic:present>            
        </div>


