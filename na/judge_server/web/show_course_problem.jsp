<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="ShowCourseProblem" toScope="request" />
<bean:define id="region" value="Courses" toScope="request" />

<tiles:insert definition="course.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Course :: Show Problem" />
    <tiles:put name="content" value="/jsp/show_problem_content.jsp" />
</tiles:insert>

