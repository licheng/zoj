<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="AddProblem" toScope="request" />
<bean:define id="menuId" value="Problem Sets" toScope="request" />
<bean:define id="region" value="Problems" toScope="request" />

<tiles:insert definition="problem.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Problems :: Add Problem" />
    <tiles:put name="content" value="/jsp/problem_table.jsp" />
</tiles:insert>

