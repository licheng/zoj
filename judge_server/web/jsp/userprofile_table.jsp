<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page import="cn.edu.zju.acm.onlinejudge.form.ProfileForm" %>

<%
    String actionPath = request.getContextPath() + "/register.do";
    String submitName = "Register";
    String title = "Register";
    if ("EditProfile".equals(request.getAttribute("pageId"))) {
        actionPath = request.getContextPath() + "/editProfile.do";
        submitName = "Edit";
        title = "Edit Profile";
    }
%>


    <logic:messagesPresent property="error">
    <div class="internalError">
        <html:errors property="error"/>
    </div>
    </logic:messagesPresent>

<div id="content_title"><%=title%></div>
<div id="content_body">
<form name="userProfileForm" method="post" action="<%=actionPath%>">

        <blockquote>
        <table class="profileTable">
            <tr>
                <td align="right"><b>Handle Info</b></td>             
            </tr>
            <tr>
                <td align="right">Handle</td>             
                <td>                                      
                    <logic:equal name="pageId" value="Register">
                    <input name="handle" type="text"
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="handle" />"
                        </logic:present>
                    />
                    <span class="error">*
                        <logic:messagesPresent property="handle">
                            <html:errors property="handle"/>
                        </logic:messagesPresent>
                    </span>
                    </logic:equal>
                    <logic:equal name="pageId" value="EditProfile">
                        <span class="user_name"> <bean:write name="oj_user" property="handle"/></span>
                        <input name="handle" type="hidden" value="<bean:write name="oj_user" property="handle" scope="session"/>" />
                    </logic:equal>
                </td>
            </tr>

            <logic:notEqual name="pageId" value="Register">
            <tr>
                <td align="right">Password</td>             
                <td>                                      
                    <input name="password" type="password" value="" />
                    <span class="error">*
                        <logic:messagesPresent property="password">
                            <html:errors property="password"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            </logic:notEqual>
            <tr>
                <td align="right"><%="Register".equals(request.getAttribute("pageId")) ? "" : "New "%>Password</td>             
                <td>                      
                    <input name="newPassword" type="password" value=""/>
                    <span class="error"><logic:equal name="pageId" value="Register">*</logic:equal>
                        <logic:messagesPresent property="newPassword">
                            <html:errors property="newPassword"/>
                        </logic:messagesPresent>
                    </span>
                
                </td>
            </tr>
            <tr>
                <td align="right">Confirm Password</td>             
                <td>
                    <input name="confirmPassword" type="password" value=""/>
                    <span class="error"><logic:equal name="pageId" value="Register">*</logic:equal>
                        <logic:messagesPresent property="confirmPassword">
                            <html:errors property="confirmPassword"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Email</td>             
                <td>
                    <input name="email" type="text"
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="email" />"
                        </logic:present>
                    >
                    <span class="error">*
                        <logic:messagesPresent property="email">
                            <html:errors property="email"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Nick Name</td>             
                <td>                                      
                    <input name="nick" type="text"
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="nick" />"
                        </logic:present>
                    >
                </td>
            </tr>
            
            <tr>
                <td align="right">Plan</td>             
                <td>                                      
                    <textarea name="plan" type="text"
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="plan" />"
                        </logic:present>
                    >
                </td>
            </tr>
            <tr>
                <td align="right"><b>Personal Info</b></td> 
            </tr>
            <tr>
                <td align="right">First Name</td> 
                <td>  
                    <input name="firstName" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="firstName" />"
                        </logic:present>
                    >
                    <span class="error">*
                        <logic:messagesPresent property="firstName">
                            <html:errors property="firstName"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Last Name</td> 
                <td>  
                    <input name="lastName" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="lastName" />"
                        </logic:present>
                    >
                    <span class="error">*
                        <logic:messagesPresent property="lastName">
                            <html:errors property="lastName"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Birthday</td> 
                <td>  
                    <input name="birthday" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="birthday" />"
                        </logic:present>
                    >
                    <span class="error">*
                        <logic:messagesPresent property="birthday">
                            <html:errors property="birthday"/>
                        </logic:messagesPresent>
                        (mm/dd/yyyy)
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Gender</td> 
                <td>  
                    <select name="gender">
                        <logic:notPresent name="ProfileForm">
                            <option value="" selected>-----select gender-----</option>
                            <option value="M">Male</option>
                            <option value="F">Female</option>
                            <option value=" ">Why do you ask...</option>
                        </logic:notPresent>
                        <logic:present name="ProfileForm">
                        <option value="" <logic:empty name="ProfileForm" property="gender">selected</logic:empty> >-----select gender-----</option>
                        <option value="M" <logic:equal name="ProfileForm" property="gender" value="M">selected</logic:equal> >Male</option>
                        <option value="F" <logic:equal name="ProfileForm" property="gender" value="F">selected</logic:equal> >Female</option>
                        <option value=" " <logic:equal name="ProfileForm" property="gender" value=" ">selected</logic:equal> >Why do you ask...</option>
                        </logic:present>
                    </select>
                    <span class="error">*
                        <logic:messagesPresent property="gender">
                            <html:errors property="gender"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Address Line 1</td> 
                <td>  
                    <input name="addressLine1" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="addressLine1" />"
                        </logic:present>
                    >
                    <span class="error">*
                        <logic:messagesPresent property="addressLine1">
                            <html:errors property="addressLine1"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            
            <tr>
                <td align="right">Address Line 2</td> 
                <td>  
                    <input name="addressLine2" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="addressLine2" />"
                        </logic:present>
                    >
                </td>
            </tr>
            <tr>
                <td align="right">City</td> 
                <td>  
                    <input name="city" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="city" />"
                        </logic:present>
                    >
                    <span class="error">*
                        <logic:messagesPresent property="city">
                            <html:errors property="city"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Province/State</td> 
                <td>  
                    <input name="state" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="state" />"
                        </logic:present>
                    >
                    <span class="error">*
                        <logic:messagesPresent property="state">
                            <html:errors property="state"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Country</td> 
                <td>  
                    <%
                        String selectedCountryId = "44";
                        ProfileForm profileForm = (ProfileForm) request.getAttribute("ProfileForm");
                        if (profileForm != null && profileForm.getCountry() != null) {
                            selectedCountryId = profileForm.getCountry();
                        }
                    %>

                    <select name="country">
                        <logic:iterate id="Country" name="Countries">
                            <option
                                value="<bean:write name="Country" property="id" />"
                                <logic:equal name="Country" property="id" value="<%=selectedCountryId%>">selected</logic:equal> ><bean:write name="Country" property="name" /></option>
                        </logic:iterate>
                    </select>

                    <span class="error">*
                        <logic:messagesPresent property="country">
                            <html:errors property="country"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Zip Code</td> 
                <td>
                    <input name="zipCode" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="zipCode" />"
                        </logic:present>
                    >
                    <span class="error">*
                        <logic:messagesPresent property="zipCode">
                            <html:errors property="zipCode"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right">Phone</td> 
                <td>                               
                    <input name="phone" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="phone" />"
                        </logic:present>
                    >
                    <span class="error">*
                        <logic:messagesPresent property="phone">
                            <html:errors property="phone"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>
            <tr>
                <td align="right"><b>School Info</b></td>                
            </tr>        
            <tr>  
                <td align="right">School</td>                              
                <td>
                    <input name="school" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="school" />"
                        </logic:present>
                    >
                </td>
            </tr>        
            <tr>
                <td align="right">Graduate Year</td>
                <td>
                    <input name="graduationYear" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="graduationYear" />"
                        </logic:present>
                    >
                    <span class="error">
                        <logic:messagesPresent property="graduationYear">
                            <html:errors property="graduationYear"/>
                        </logic:messagesPresent>
                    </span>
                </td>
            </tr>        
            <tr>
                <td align="right">Major</td>
                <td>
                    <input name="major" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="major" />"
                        </logic:present>
                    >
                </td>
            </tr>        
            <tr>
                <td align="right">Student ID</td>
                <td>
                    <input name="studentNumber" type="text"/
                        <logic:present name="ProfileForm">
                            value="<bean:write name="ProfileForm" property="studentNumber" />"
                        </logic:present>
                    >
                </td>
            </tr>        
            <tr>
                <td align="right">Graduate Student</td>
                <td>
                    <input type="checkbox" name="graduateStudent"
                        <logic:present name="ProfileForm">
                        <logic:equal name="ProfileForm" property="graduateStudent" value="true">
                            checked
                        </logic:equal>
                        </logic:present>
                    >
                </td>
            </tr>
        </table>
        </blockquote>

    <div class="profileButtons">
        <div class="profileButton">
            <input class="button" name="profile" type="submit" id="profile" value="<%=submitName%>" />
        </div>
        <div class="profileButton">
            <input class="button" name="cacel" type="reset" id="cacel" value="Reset">
        </div>
    </div>
</form>
</div>
