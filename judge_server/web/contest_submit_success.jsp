<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="ContestSubmitSuccess" toScope="request" />
<bean:define id="region" value="Contests" toScope="request" />

<tiles:insert definition="contest.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Contests :: Submit Success" />
    <tiles:put name="content" value="/jsp/submit_success_content.jsp" />
</tiles:insert>

