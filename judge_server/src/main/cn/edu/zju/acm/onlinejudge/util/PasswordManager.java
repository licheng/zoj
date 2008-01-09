/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.util;


/**
 * <p>PasswordManager class.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class PasswordManager {

	/**
	 * Encode the password.
	 * 
	 * @param pwd
	 * @return
	 */
    public static String encodePassword1(String pwd) {
    	if (pwd == null || pwd.length() == 0) {
    		return pwd;
    	}
    	return pwd + "lala";
    }
}
