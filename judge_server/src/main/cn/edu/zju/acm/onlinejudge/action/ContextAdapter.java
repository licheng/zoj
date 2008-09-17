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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.UserPreference;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;
import cn.edu.zju.acm.onlinejudge.util.ConfigManager;

/**
 * ContextAdapter wraps configuration and session logic for the actions. It provides Java Bean like API to access
 * session variables whose namespaces are configurable. A ContextAdapter instance is created in BaseAction and passed to
 * various action implementations. It also holds the http request and response.
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public class ContextAdapter {

    /**
     * USER_PROFILE_SESSION_KEY.
     */
    public static final String USER_PROFILE_SESSION_KEY = "oj_user";

    /**
     * SECURITY_SESSION_KEY.
     */
    public static final String SECURITY_SESSION_KEY = "oj_security";

    /**
     * PREFERENCE_SESSION_KEY.
     */
    public static final String PREFERENCE_SESSION_KEY = "oj_user_preference";

    public static UserSecurity defaultUserSecurity = null;

    /**
     * Represents the HTTP servlet request for this context. It is set from constructor and is never changed.
     * ContextAdapter uses this to reference the server session for the user.
     */
    private final HttpServletRequest request;

    /**
     * Represents the HTTP servlet response for this context. It is set from constructor and is never changed.
     */
    private final HttpServletResponse response;

    /**
     * Constructor with both request and response. The constructor also pulls configuration from TopCoder Configuration
     * Manager.
     * 
     * @param request
     *            the http servlet request.
     * @param response
     *            the http servlet response.
     * 
     * @throws NullPointerException
     *             if either request or response is null
     */
    public ContextAdapter(HttpServletRequest request, HttpServletResponse response) {
        if (request == null) {
            throw new NullPointerException("request should not be null.");
        }
        if (response == null) {
            throw new NullPointerException("response should not be null.");
        }

        this.request = request;
        this.response = response;

    }

    /**
     * Get the http servlet request.
     * 
     * @return http servlet request.
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }

    /**
     * Get the http servlet response.
     * 
     * @return http servlet response.
     */
    public HttpServletResponse getResponse() {
        return this.response;
    }

    /**
     * Get the UserProfile instance cached in session.
     * 
     * @return the UserProfile for the context, null is returned if the attribute does not exist in the session.
     */
    public UserProfile getUserProfile() {
        Object obj = this.request.getSession().getAttribute(ContextAdapter.USER_PROFILE_SESSION_KEY);
        if (obj instanceof UserProfile) {
            return (UserProfile) obj;
        } else {
            return null;
        }
    }

    /**
     * Set the UserProfile instance to session.
     * 
     * @param profile
     *            the profile.
     */
    public void setUserProfile(UserProfile profile) {
        this.request.getSession().setAttribute(ContextAdapter.USER_PROFILE_SESSION_KEY, profile);
    }

    /**
     * Get operator.
     * 
     * @return the operator.
     */
    public String getOperator() {
        UserProfile user = this.getUserProfile();
        if (user == null) {
            return "anoymouse";
        } else {
            return user.getHandle();
        }
    }

    /**
     * Get the UserSecurity instance cached in session.
     * 
     * @return the UserSecurity for the context, null is returned if the attribute does not exist in the session.
     */
    public UserSecurity getUserSecurity() throws PersistenceException {
        Object obj = this.request.getSession().getAttribute(ContextAdapter.SECURITY_SESSION_KEY);
        if (obj instanceof UserSecurity) {
            return (UserSecurity) obj;
        } else {
            this.setUserSecurity(this.getDefaultUserSecurity());
            return this.getDefaultUserSecurity();
        }
    }

    /**
     * Set the UserSecurity instance to session.
     * 
     * @param security
     *            the user security.
     */
    public void setUserSecurity(UserSecurity security) {
        this.request.getSession().setAttribute(ContextAdapter.SECURITY_SESSION_KEY, security);
    }

    /**
     * Get the UserPreference instance cached in session.
     * 
     * @return the UserPreference for the context, null is returned if the attribute does not exist in the session.
     */
    public UserPreference getUserPreference() {
        Object obj = this.request.getSession().getAttribute(ContextAdapter.PREFERENCE_SESSION_KEY);
        if (obj instanceof UserPreference) {
            return (UserPreference) obj;
        } else {
            return null;
        }
    }

    /**
     * Set the UserPreference instance to session.
     * 
     * @param preference
     *            the user preference.
     */
    public void setUserPreference(UserPreference preference) {
        this.request.getSession().setAttribute(ContextAdapter.PREFERENCE_SESSION_KEY, preference);
    }

    /**
     * Set the object to session.
     * 
     * @param key
     * @param obj
     */
    public void setSessionAttribute(String key, Object obj) {
        this.request.getSession().setAttribute(key, obj);
    }

    /**
     * Get the object from session.
     * 
     * @param key
     * @return obj
     */
    public Object getSessionAttribute(String key) {
        return this.request.getSession().getAttribute(key);
    }

    /**
     * Set the object to request.
     * 
     * @param key
     * @param obj
     */
    public void setAttribute(String key, Object obj) {
        this.request.setAttribute(key, obj);
    }

    /**
     * Get the object from request.
     * 
     * @param key
     * @return obj
     */
    public Object getAttribute(String key) {
        return this.request.getAttribute(key);
    }

    /**
     * Get the string from request.
     * 
     * @param key
     * @return obj
     */
    public String getStringAttribute(String key) {
        Object obj = this.request.getAttribute(key);
        if (obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    public AbstractContest getContest() throws PersistenceException {

        if (this.getAttribute("contest") instanceof AbstractContest) {
            return (AbstractContest) this.getAttribute("contest");
        }

        long contestId = -1;
        String stringId = null;
        if (this.request.getParameter("contestId") != null) {
            stringId = this.request.getParameter("contestId");
        } else if (this.request.getParameter("id") != null) {
            stringId = this.request.getParameter("id");
        }
        this.setAttribute("contestId", stringId);

        if (stringId != null) {
            try {
                contestId = Long.parseLong(stringId);
            } catch (NumberFormatException e) {}
        }

        if (contestId < 0) {
            return null;
        }
        AbstractContest contest = ContestManager.getInstance().getContest(contestId);
        this.setAttribute("contest", contest);

        return contest;
    }

    public Problem getProblem() throws PersistenceException {

        if (this.getAttribute("problem") instanceof Problem) {
            return (Problem) this.getAttribute("problem");
        }
        long problemId = -1;
        String stringCode = this.request.getParameter("problemCode");
        String stringId = this.request.getParameter("problemId");
        if(stringCode!=null)
        {
			long problemindex=Long.parseLong(stringCode)-1001;
			stringId = ((Problem)(ContestManager.getInstance().getContestProblems(
			ConfigManager.getDefaultProblemSetId()).get(problemindex))).getId().toString();
        }
        this.setAttribute("problemId", stringId);
        if (stringId != null) {
            try {
                problemId = Long.parseLong(stringId);
            } catch (NumberFormatException e) {}
        }

        if (problemId < 0) {
            return null;
        }
        Problem problem = ContestManager.getInstance().getProblem(problemId);
        this.setAttribute("problem", problem);
        return problem;
    }

    public List<Problem> getProblems() throws PersistenceException {

        AbstractContest contest = this.getContest();
        if (contest == null) {
            return new ArrayList<Problem>();
        }
        return ContestManager.getInstance().getContestProblems(contest.getId());

    }

    protected List<AbstractContest> getAllProblemSets() throws PersistenceException {
        return this.getCheckedContests(ContestManager.getInstance().getAllProblemsets());
    }

    protected List<AbstractContest> getAllContests() throws PersistenceException {
        return this.getCheckedContests(ContestManager.getInstance().getAllContests());
    }

    protected List<AbstractContest> getCheckedContests(List<AbstractContest> contests) throws PersistenceException {
        UserSecurity userSecurity = this.getUserSecurity();

        List<AbstractContest> ret = new ArrayList<AbstractContest>();
        for (AbstractContest contest : contests) {
            if (userSecurity.canViewContest(contest.getId())) {
                ret.add(contest);
            }
        }
        return ret;
    }

    /**
     * Checks whether user is logged in.
     * 
     * @return
     */
    public boolean isAdmin() throws PersistenceException {
        return this.getUserSecurity().isSuperAdmin();
    }

    public UserSecurity getDefaultUserSecurity() throws PersistenceException {
        if (ContextAdapter.defaultUserSecurity == null) {
            synchronized (this) {
                if (ContextAdapter.defaultUserSecurity == null) {
                    ContextAdapter.defaultUserSecurity = new UserSecurity(0);
                    ContextAdapter.defaultUserSecurity.importRole(PersistenceManager.getInstance()
                                                                                    .getAuthorizationPersistence()
                                                                                    .getRole(1));
                }
            }
        }
        return ContextAdapter.defaultUserSecurity;
    }

    public static void resetDefaultUserSecurity() {
        ContextAdapter.defaultUserSecurity = null;
    }

}
