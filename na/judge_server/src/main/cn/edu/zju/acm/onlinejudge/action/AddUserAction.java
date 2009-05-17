/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com>
 * 
 * This file is part of ZOJ.
 * 
 * ZOJ is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either revision 3 of the License, or (at your option) any later revision.
 * 
 * ZOJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with ZOJ. if not, see
 * <http://www.gnu.org/licenses/>.
 */

package cn.edu.zju.acm.onlinejudge.action;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.edu.zju.acm.onlinejudge.form.AddUserForm;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.UserManager;
import cn.edu.zju.acm.onlinejudge.util.Utility;

/**
 * <p>
 * AddUserRoleAction
 * </p>
 * 
 * 
 * @author Chen Zhengguang
 * @version 2.0
 */
public class AddUserAction extends BaseAction {

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public AddUserAction() {

    }

    /**
     * AddUserRoleAction.
     * 
     * @param mapping
     *            action mapping
     * @param form
     *            action form
     * @param request
     *            http servlet request
     * @param response
     *            http servlet response
     * 
     * @return action forward instance
     * 
     * @throws Exception
     *             any errors happened
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception {
    	AddUserForm addUserForm = (AddUserForm) form;// TODO Auto-generated method stub
    	boolean isTeacher = context.getRequest().getRequestURI().endsWith("addTeacher.do");
		
    	UserProfile u = context.getUserProfile();
		long teacherId = u.getId();
		UserProfile student = null;
		student=new UserProfile();
		student.setAddressLine1("line1");
		student.setAddressLine2("line2");

		if(addUserForm.getStudentNumber()!=null) {
			student.setHandle(addUserForm.getStudentNumber());
		} else {
			student.setHandle(addUserForm.getUsername());
		}
		student.setPassword(addUserForm.getPassword());
		String str=addUserForm.getUsername();
		//str = new String(str.getBytes("GBK"), "UTF-8");
		student.setFirstName(str);
		student.setLastName("");
		student.setEmail(new Integer(new Date().hashCode()).toString());
		student.setCity("null");
		student.setState("null");
		student.setBirthDate(new Date());
		student.setCountry(PersistenceManager.getInstance().getCountry("44"));
		student.setZipCode("null");
		student.setPhoneNumber("null");
		student.setGender('M');
		student.setSchool("Zhejiang University");
		student.setMajor("null");
		student.setGraduateStudent(false);
		student.setGraduationYear(2010);
		if(addUserForm.getStudentNumber()!=null)
		student.setStudentNumber(addUserForm.getStudentNumber());  
		student.setConfirmed(true);
		PersistenceManager.getInstance().getUserPersistence().createUserProfile(student, teacherId);
		List<String> list = new LinkedList<String>();
		if(!isTeacher){
			list.add(addUserForm.getStudentNumber());
		} else {
			list.add(addUserForm.getUsername());
		}
		if(!isTeacher){
			PersistenceManager.getInstance().getAuthorizationPersistence().addRoleUsers(list, 4);
		} else {
			PersistenceManager.getInstance().getAuthorizationPersistence().addRoleUsers(list, 3);
		}
		return handleSuccess(mapping, context, "success");
    }

}
