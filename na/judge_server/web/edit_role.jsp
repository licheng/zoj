<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="EditRole" toScope="request" />
<bean:define id="menuId" value="ConfigueSystem" toScope="request" />
<bean:define id="region" value="Admin" toScope="request" />

<tiles:insert definition="admin.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Admin :: Edit Role" />
    <tiles:put name="content" value="/jsp/edit_role_content.jsp" />
</tiles:insert>

