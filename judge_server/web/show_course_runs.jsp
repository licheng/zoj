<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="ShowCourseRuns" toScope="request" />
<bean:define id="region" value="Course" toScope="request" />

<tiles:insert definition="course.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Course :: Runs" />
    <tiles:put name="content" value="/jsp/show_runs_content.jsp" />
</tiles:insert>

