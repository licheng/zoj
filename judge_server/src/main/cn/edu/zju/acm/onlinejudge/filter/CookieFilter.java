package cn.edu.zju.acm.onlinejudge.filter;
        
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.zju.acm.onlinejudge.action.ContextAdapter;
import cn.edu.zju.acm.onlinejudge.bean.UserPreference;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.persistence.AuthorizationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.UserPersistence;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

public class CookieFilter implements Filter {

    public void destroy() {
        
    } 
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
        throws IOException, ServletException {
    	HttpServletRequest r = (HttpServletRequest) request;
    	if (r.getAttribute(ContextAdapter.SECURITY_SESSION_KEY) == null) {
    	
    		Cookie[] cookies = r.getCookies();
    		String handle = null;
    		String password = null;
    		if (cookies != null) {
	    		for (Cookie cookie : cookies) {
	    			if (cookie.getName().equals("oj_handle")) {
	    				handle = cookie.getValue(); 
	    			}
	    			if (cookie.getName().equals("oj_password")) {
	    				password = cookie.getValue(); 
	    			}
	    		}
    		}
    		if (handle != null && password != null) {
    			try {
    				UserPersistence userPersistence = PersistenceManager.getInstance().getUserPersistence();
    		    	UserProfile profile = userPersistence.login(handle, password);
    		    	
    		    	if (profile != null && profile.isActive()) {
    		    	
	    		    	AuthorizationPersistence authorizationPersistence 
	    		    		= PersistenceManager.getInstance().getAuthorizationPersistence();
	    		    	// get UserSecurity
	    		    	UserSecurity security = authorizationPersistence.getUserSecurity(profile.getId());
	    		    	// get UserPreference
	    		    	UserPreference perference = userPersistence.getUserPreference(profile.getId());
	    		    	r.getSession().setAttribute(ContextAdapter.USER_PROFILE_SESSION_KEY, profile);
	    		    	r.getSession().setAttribute(ContextAdapter.SECURITY_SESSION_KEY, security);
	    		    	r.getSession().setAttribute(ContextAdapter.PREFERENCE_SESSION_KEY, perference);
    		    	} else {
    		    		Cookie ch = new Cookie("oj_handle", "");
    		    		ch.setMaxAge(0);
    		    		ch.setPath("/");
    		    		((HttpServletResponse) response).addCookie(ch);
    		    		 
    		    		Cookie cp = new Cookie("oj_password", "");
    		    		cp.setMaxAge(0);
    		    		cp.setPath("/");
    		    		((HttpServletResponse) response).addCookie(cp);
    		    	}
    			} catch (Exception e) {
    				throw new ServletException("failed to auth with cookie.", e);
    			}
    		}
    		
    	}
    	
    	chain.doFilter(request, response);
    } 
    
    public void init(FilterConfig filterConfig) throws ServletException {
        
    } 
} 
