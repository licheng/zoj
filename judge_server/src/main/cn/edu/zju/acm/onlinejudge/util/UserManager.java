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

package cn.edu.zju.acm.onlinejudge.util;


import cn.edu.zju.acm.onlinejudge.persistence.PersistenceCreationException;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.util.cache.Cache;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
public class UserManager {

	
	private final Cache<UserProfile> userCache;
	
	/**
	 * UserManager.
	 */
	private static UserManager instance = null; 

	
    /**
     * <p>Constructor of UserManager class.</p>
     * @throws PersistenceCreationException 
     *
     */
    private UserManager() throws PersistenceCreationException {    	 
    	userCache = new Cache<UserProfile>(60000, 200); 
    }
    
    /**
     * Gets the singleton instance.
     * @return the singleton instance.
     * @throws PersistenceCreationException 
     */
    public static UserManager getInstance() throws PersistenceCreationException {
    	if (instance == null) {
    		synchronized (UserManager.class) {
    			if (instance == null) {
    				instance = new UserManager();
    			}
    		}
    	}
    	return instance;
    }
    
    public UserProfile getUserProfile(long userId) throws PersistenceException {
    	Object key = new Long(userId);
    	synchronized (userCache) {
    		UserProfile user = (UserProfile) userCache.get(key);
    		if (user == null) {
    			UserPersistence userPersistence = PersistenceManager.getInstance().getUserPersistence();
    			user = userPersistence.getUserProfile(userId);
    			userCache.put(key, user);
    		}
    		return user;
    	}
    }
    
    
    public void refresh(long userId) {

    	Object key = new Long(userId);
    	synchronized (userCache) {
    		userCache.remove(key);
    	}    	
    }
    
}
