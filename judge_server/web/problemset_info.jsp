<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="ProblemsetInfo" toScope="request" />
<bean:define id="region" value="Problems" toScope="request" />

<tiles:insert definition="problem.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Problems :: Problem Set Info" />
    <tiles:put name="content" value="/jsp/contest_info_content.jsp" />
</tiles:insert>
