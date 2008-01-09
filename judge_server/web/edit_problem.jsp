<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="EditProblem" toScope="request" />
<bean:define id="menuId" value="Problem Sets" toScope="request" />
<bean:define id="region" value="Problems" toScope="request" />

<tiles:insert definition="contest.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Problems :: Edit Problem" />
    <tiles:put name="content" value="/jsp/problem_table.jsp" />
</tiles:insert>

