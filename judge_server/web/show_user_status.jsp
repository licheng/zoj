<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="ShowUserStatus" toScope="request" />
<bean:define id="region" value="Home" toScope="request" />

<tiles:insert definition="home.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Home :: Shou User Status" />
    <tiles:put name="content" value="/jsp/show_user_status_content.jsp" />
</tiles:insert>
