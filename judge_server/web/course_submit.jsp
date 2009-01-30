<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="CourseSubmit" toScope="request" />
<bean:define id="region" value="Courses" toScope="request" />

<tiles:insert definition="course.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Courses :: Submit" />
    <tiles:put name="content" value="/jsp/submit_content.jsp" />
</tiles:insert>

