<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.*" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.UserProfile" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.bean.AbstractContest" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.security.RoleSecurity" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.util.RankListEntry" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.ProblemsetRankList" %>
<%@ page import="cn.edu.zju.acm.onlinejudge.util.Utility" %>

<%
    
    AbstractContest contest = (AbstractContest) request.getAttribute("contest");
    String userStatusPath = request.getContextPath() + "/showUserStatus.do?contestId=" + contest.getId() + "&userId=";    
    String ranklistPath = request.getContextPath() + "/showRankList.do?contestId=" + contest.getId() + "&from=";
      
%>
        <logic:messagesPresent property="error">
        <div class="internalError">
            <html:errors property="error"/>
        </div>
        </logic:messagesPresent>
        <logic:messagesNotPresent property="error">
         <div id="content_title">
            <bean:write name="contest" property="title"/>
        </div>
        <div id="content_body">
                <div align="center" >
                <logic:present name="previousFrom">
                <a href="<%=ranklistPath + request.getAttribute("previousFrom")%>">&lt;&lt;Previous</a>
                </logic:present>
                <logic:notPresent name="previousFrom">
                <font color="#777777">&lt;&lt;Previous</font>
                </logic:notPresent>
                &nbsp;&nbsp;
                <logic:present name="nextFrom">
                <a href="<%=ranklistPath + request.getAttribute("nextFrom")%>">Next&gt;&gt;</a>
                </logic:present>
                <logic:notPresent name="nextFrom">
                <font color="#777777">Next&gt;&gt;</font>
                </logic:notPresent>
                </div>        
                <table class="problemsetList" style="width:100%">
                    <tr class="rowHeader" >
                        <td class="problemsetRanklistRank">Rank</td>
                        <td class="problemsetRanklistUser">Name</td>
                        <td class="problemsetRanklistUser">Plan</td>
                        <td class="problemsetRanklistSolved">Solved</td>
                        <td class="problemsetRanklistSubmitted">Submitted</td>
                        <td class="problemsetRanklistACRatio">AC Ratio</td>
                    </tr>
                    <%
                    ProblemsetRankList ranklist = (ProblemsetRankList) request.getAttribute("RankList");
                    int[] solved = ranklist.getSolved();
                    int[] total = ranklist.getTotal();
                    UserProfile[] users = ranklist.getUsers();
                    int rank = ranklist.getOffset();
                    int count = 1;
                    for (int i = 0; i < solved.length; ++i) {
                    	if (i == 0 || solved[i] != solved[i - 1] || total[i] != total[i - 1]) {
                    		rank += count;
                    		count = 1;
                    	} else {
                    		count++;
                    	}
                    %>
                    <tr class="<%=i % 2 == 0 ? "rowOdd" : "rowEven"%>">
                        <td class="problemsetRanklistRank"><%=rank%></td>
                        <td class="problemsetRanklistUser"><a href="<%=userStatusPath + users[i].getId()%>"><font color="db6d00"><%
                        String nickname = 
                        	users[i].getNickName() == null || users[i].getNickName().length() == 0
                            ? users[i].getHandle() : users[i].getNickName();
                            request.setAttribute("nickname", nickname);
                        %><bean:write name="nickname"/></font></a></td>
                        <td class="problemsetRanklistUser"><%
                        String plan = (users[i].getDeclaration() == null) ? "" : users[i].getDeclaration();
                            request.setAttribute("plan", plan);
                        %><bean:write name="plan"/></td>
                        <td class="problemsetRanklistSolved"><%=solved[i]%></td>
                        <td class="problemsetRanklistSubmitted"><%=total[i]%></td>
                        <td class="problemsetRanklistACRatio"><%=(new DecimalFormat("0.##")).format(100.0 * solved[i] / total[i])+"%"%></td>
                    </tr>
                    <%
                    }
                    %>
                </table>
                <div align="center" >
                <logic:present name="previousFrom">
                <a href="<%=ranklistPath + request.getAttribute("previousFrom")%>">&lt;&lt;Previous</a>
                </logic:present>
                <logic:notPresent name="previousFrom">
                <font color="#777777">&lt;&lt;Previous</font>
                </logic:notPresent>
                &nbsp;&nbsp;
                <logic:present name="nextFrom">
                <a href="<%=ranklistPath + request.getAttribute("nextFrom")%>">Next&gt;&gt;</a>
                </logic:present>
                <logic:notPresent name="nextFrom">
                <font color="#777777">Next&gt;&gt;</font>
                </logic:notPresent>
                </div>
                
        </div>
        </logic:messagesNotPresent>

