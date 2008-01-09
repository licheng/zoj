<%@ page contentType="text/html; charset=utf-8" language="java" %>
                <fieldset>
                <legend>Manage Contests</legend>
            <form name="contestForm" method="post" action="<%=request.getContextPath()%>/login.do">
                <ul class="profileFields">
                    <li>
                        <div class="profileFieldName">Handle</div>
                        <div class="profileField">
                            <input name="handle" type="text"/
                                <logic:present name="LoginForm">
                                    value="<bean:write name="LoginForm" property="handle" />"
                                </logic:present>
                            >
                            <span class="error">
                                <logic:messagesPresent property="handle">
                                    <html:errors property="handle"/>
                                </logic:messagesPresent>
                            </span>
                        </div>
                    </li>

                    <li>
                        <div class="profileFieldName">Password</div>
                        <div class="profileField">
                            <input name="password" type="password" value="">
                            <span class="error">
                                <logic:messagesPresent property="password">
                                    <html:errors property="password"/>
                                </logic:messagesPresent>
                            </span>
                        </div>
                    </li>

                    <li>
                        <div class="profileFieldName"></div>
                        <div class="profileFieldCheckBox">
                            <input type="checkbox" name="rememberMe" value="checkbox" />Remember Me
                        </div>
                    </li>
                    <li/>
                    <li>
                        <div class="profileFieldName"></div>
                        <div class="profileButton">
                            <input type="submit" value="Login"/>
                        </div>
                    </li>
                    <li/>
                    <li/>
                    <%--
                    <li>
                        <div class="profileFieldName"> </div>
                        <div class="profileField"><a href="forgot_psw.html">Forgot Handle?</a></div>
                    </li>
                    <li>
                        <div class="profileFieldName"> </div>
                        <div class="profileField"><a href="forgot_psw.html">Forgot Password?</a></div>
                    </li>
                    --%>
                </ul>
            </form>
                
                <table width="98%" border="0" cellpadding="0" cellspacing="0">
                  <tr> 
                    <td width="26%" height="10"><table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr> 
                          <td>&nbsp;&nbsp;</td>
                        </tr>
                      </table>
                      <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right"><strong>Contest Info</strong>&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td>&nbsp;</td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Contest Name&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td> <input name="textfield222" type="text" size="55"></td>
                        </tr>
                        <tr> 
                          <td width="10" height="25">&nbsp;</td>
                          <td width="120"><div align="right">Information&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td> <input name="textfield" type="text" size="55"> 
                          </td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Start Time&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td> <input name="textfield2" type="text" size="55"></td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Contest Length&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td> <input name="textfield22" type="text" size="55"> 
                          </td>
                        </tr>
						<tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Forum Link&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td><select name="select2">
                              <option>----- select Forum ----</option>
                            </select>
                            </td>
                        </tr>
						<tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Problemset&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td><input name="checkbox" type="checkbox" value="checkbox">
                            </td>
                        </tr>
                        <tr> 
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right"><strong>Default Limits</strong>&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td>&nbsp;</td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Time Limit&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td><input name="handle22" type="text" value="1">
                            s</td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Memory Limit&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td><input name="handle23" type="text" value="1">
                            K</td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Output Limit&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td><input name="handle23" type="text" value="1">
                            K</td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Submission Limit&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td><input name="handle23" type="text" value="1">
                            K</td>
                        </tr>
						<tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right">Use Global Default&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td><input name="checkbox" type="checkbox" value="checkbox" checked>
                            </td>
                        </tr>
                        <tr> 
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td><div align="right"><strong>Language Select</strong>&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td>&nbsp;</td>
                        </tr>
                        <tr> 
                          <td height="25">&nbsp;</td>
                          <td valign="top"> <div align="right">Language List&nbsp;&nbsp;&nbsp;&nbsp;</div></td>
                          <td><select name="select" size="3" multiple>
                              <option selected>JAVA&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
                              <option selected>C++</option>
                              <option selected>Pascal</option>
                            </select> </td>
                        </tr>
                        <tr> 
                          <td>&nbsp;</td>
                          <td><div align="right"></div></td>
                          <td>&nbsp;</td>
                        </tr>
                      </table>
                      <br> <table width="98%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr> 
                          <td width="10" height="25">&nbsp;</td>
                          <td width="120"><div align="right"></div></td>
                          <td> <input name="add" type="submit" id="clear23" value="Add Contest" onClick="MM_goToURL('self','contest_list.html');return document.MM_returnValue"> 
                            &nbsp;&nbsp;&nbsp;&nbsp; <input name="export" type="submit" id="clear24" value="Import Contest"> 
                            &nbsp;&nbsp;&nbsp;&nbsp; <input name="cancel" type="submit" id="cancel" value="   Cancel   "> 
                          </td>
                        </tr>
                      </table></td>
                  </tr>
                </table>
                <table width="98%" border="0" cellpadding="0" cellspacing="0">
                  <tr> 
                    <td width="26%" height="10"> <br> </td>
                  </tr>
                </table>
                <br>
                <br>
				</fieldset>                