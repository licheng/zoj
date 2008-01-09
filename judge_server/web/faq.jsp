<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="FAQ" toScope="request" />
<bean:define id="region" value="Home" toScope="request" />

<tiles:insert definition="home.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Home :: FAQ" />
    <tiles:put name="content" value="/jsp/faq_content.jsp" />
</tiles:insert>

