<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<bean:define id="pageId" value="ShowProblemsets" toScope="request" />
<bean:define id="region" value="Problems" toScope="request" />

<tiles:insert definition="problem.default" flush="true" >
    <tiles:put name="title" value="ZOJ :: Problems :: Show Problem Sets" />
    <tiles:put name="content" value="/jsp/show_contests_content.jsp" />
</tiles:insert>

