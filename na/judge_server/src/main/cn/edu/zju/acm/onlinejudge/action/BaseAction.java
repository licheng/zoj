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
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.RedirectingActionForward;

import cn.edu.zju.acm.onlinejudge.bean.AbstractContest;
import cn.edu.zju.acm.onlinejudge.bean.Contest;
import cn.edu.zju.acm.onlinejudge.bean.Course;
import cn.edu.zju.acm.onlinejudge.bean.Problem;
import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.enumeration.PermissionLevel;
import cn.edu.zju.acm.onlinejudge.persistence.ContestPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.security.UserSecurity;
import cn.edu.zju.acm.onlinejudge.util.ContestManager;
import cn.edu.zju.acm.onlinejudge.util.PerformanceManager;
import cn.edu.zju.acm.onlinejudge.util.PersistenceManager;

/**
 * BaseAction.
 * 
 * @author Zhang, Zheng
 * @version 2.0
 */
public abstract class BaseAction extends Action {

    /**
     * The enter operation.
     */
    private static final String ENTER_OP = "Enter";

    /**
     * The exit operation.
     */
    private static final String EXIT_OP = "Exit";

    /**
     * The generic error message key.
     */
    private static final String GENERIC_ERROR_MESSAGE_KEY = "error";

    /**
     * The generic error resource key.
     */
    private static final String GENERIC_ERROR_RESOURCE_KEY = "onlinejudge.failure";

    /**
     * The logger name.
     */
    private static final String LOGGER_NAME = "cn.edu.zju.acm.onlinejudge";

    /**
     * The logger.
     */
    private static Logger logger = null;

    /**
     * This is where the action processes the request. It forwards the invocation to the abstract execute() method and
     * returns its forward. Unexcepted exceptions are handled.
     * 
     * @param mapping
     *            the action mapping that holds the forwards.
     * @param form
     *            the form bean for input.
     * @param request
     *            the http servlet request.
     * @param response
     *            the http servlet response.
     * 
     * @return an action forward or null if the response is committed.
     * 
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        UserProfile user = (UserProfile) request.getSession().getAttribute(ContextAdapter.USER_PROFILE_SESSION_KEY);
        long actionId = PerformanceManager.getInstance().actionStart(this, request, user);

        ContextAdapter context = null;
        ActionForward forward = null;
        try {
            context = new ContextAdapter(request, response);
            this.info(this.makeInfo(BaseAction.ENTER_OP, context.getOperator(), null, request));

            // log parameters with debug level
            /*
             * debug("Received parameters:"); for (Enumeration enu = request.getParameterNames();
             * enu.hasMoreElements();) { String name = (String) enu.nextElement(); debug("[" + name + "]"); for (int i =
             * 0; i < request.getParameterValues(name).length; ++i) { debug("   [" + request.getParameterValues(name)[i]
             * + "]"); } }
             */

            forward = this.execute(mapping, form, context);
        } catch (Exception e) {
            this.error(e);
            forward = this.handleFailure(mapping, context, BaseAction.GENERIC_ERROR_RESOURCE_KEY);
        }

        PerformanceManager.getInstance().actionEnd(actionId);
        return forward;

    }

    /**
     * This is the template method for BaseAction. All the actions in this component implements this metod.
     * 
     * @return an action forward or null if the response is committed.
     * @param mapping
     *            the action mapping that holds the forwards.
     * @param form
     *            the form bean for input.
     * @param context
     *            the action context to access resources.
     * 
     * @throws Exception
     *             for unexpected errors during exection.
     */
    protected abstract ActionForward execute(ActionMapping mapping, ActionForm form, ContextAdapter context) throws Exception;

    /**
     * Gets the logger.
     * 
     * @return the logger.
     */
    private Logger getLogger() {
        if (BaseAction.logger == null) {
            synchronized (this) {
                if (BaseAction.logger == null) {
                    String fileName = BaseAction.class.getClassLoader().getResource("log4j.properties").getFile();
                    PropertyConfigurator.configure(fileName);
                    BaseAction.logger = Logger.getLogger(BaseAction.LOGGER_NAME);
                }
            }
        }
        return BaseAction.logger;
    }

    /**
     * This logs a message with a level of DEBUG.
     * 
     * @param message
     *            the message to log.
     */
    protected void debug(String message) {
        this.getLogger().debug(message);
    }

    /**
     * This logs a message with a level of INFO.
     * 
     * @param message
     *            the message to log.
     */
    protected void info(String message) {
        this.getLogger().info(message);
    }

    /**
     * This logs a message with a level of ERROR.
     * 
     * @param message
     *            the message to log.
     */
    protected void error(String message) {
        this.getLogger().error(message);
    }

    /**
     * This logs an exception's stack trace with a level of ERROR.
     * 
     * @param exception
     *            the exception to log.
     */
    protected void error(Throwable exception) {
        this.getLogger().error(null, exception);
    }

    /**
     * Provides convenience method to handle errors.
     * 
     * @param mapping
     *            the action mapping that holds the forwards.
     * @param request
     *            the http servlet request.
     * @param messageKey
     *            the resource key to retrieve the error message.
     * 
     * @return the failure forward.
     */
    protected ActionForward handleFailure(ActionMapping mapping, ContextAdapter context, String resourceKey) {
        return this.handleFailure(mapping, context, BaseAction.GENERIC_ERROR_MESSAGE_KEY, resourceKey);
    }

    /**
     * Provides convenience method to handle errors.
     * 
     * @param mapping
     *            the action mapping that holds the forwards.
     * @param request
     *            the http servlet request.
     * @param messageKey
     *            the resource key to retrieve the error message.
     * @param errorKey
     *            the error key.
     * 
     * @return the failure forward.
     */
    protected ActionForward handleFailure(ActionMapping mapping, ContextAdapter context, String errorKey,
                                          String resourceKey) {
        this.info(this.makeInfo(BaseAction.EXIT_OP, context.getOperator(), "failure"));

        ActionMessages errors = new ActionMessages();
        errors.add(errorKey, new ActionMessage(resourceKey));
        this.saveErrors(context.getRequest(), errors);

        return mapping.findForward("failure");
    }

    /**
     * Provides convenience method to handle errors.
     * 
     * @param mapping
     *            the action mapping that holds the forwards.
     * @param request
     *            the http servlet request.
     * @param errors
     *            ActionMessages
     * 
     * @return the failure forward.
     */
    protected ActionForward handleFailure(ActionMapping mapping, ContextAdapter context, ActionMessages errors,
                                          String forwardName) {
        this.info(this.makeInfo(BaseAction.EXIT_OP, context.getOperator(), forwardName));
        this.saveErrors(context.getRequest(), errors);
        return mapping.findForward(forwardName);
    }

    /**
     * Provides convenience method to handle errors.
     * 
     * @param mapping
     *            the action mapping that holds the forwards.
     * @param request
     *            the http servlet request.
     * @param errors
     *            ActionMessages
     * 
     * @return the failure forward.
     */
    protected ActionForward handleFailure(ActionMapping mapping, ContextAdapter context, ActionMessages errors) {
        return this.handleFailure(mapping, context, errors, "failure");
    }

    /**
     * Handle successful exit with log. Helper method.
     * 
     * @param mapping
     *            the action mapping that holds the forwards.
     * @param context
     *            the action context to access resources.
     * @param forwardName
     *            represents messsageKey if fail, forward name if succeed.
     * 
     * @return the specified forward.
     */
    protected ActionForward handleSuccess(ActionMapping mapping, ContextAdapter context) {
        return this.handleSuccess(mapping.getInputForward(), context, mapping.getInput());
    }

    /**
     * Handle successful exit with log. Helper method.
     * 
     * @param mapping
     *            the action mapping that holds the forwards.
     * @param context
     *            the action context to access resources.
     * @param forwardName
     *            represents messsageKey if fail, forward name if succeed.
     * 
     * @return the specified forward.
     */
    protected ActionForward handleSuccess(ActionMapping mapping, ContextAdapter context, String forwardName) {

        return this.handleSuccess(mapping.findForward(forwardName), context, forwardName);
    }

    /**
     * Handle successful exit with log. Helper method.
     * 
     * @param mapping
     *            the action mapping that holds the forwards.
     * @param context
     *            the action context to access resources.
     * @param forwardName
     *            represents messsageKey if fail, forward name if succeed.
     * 
     * @return the specified forward.
     */
    protected ActionForward handleSuccess(ActionMapping mapping, ContextAdapter context, String forwardName,
                                          String parameter) {
        String newPath = mapping.findForward(forwardName).getPath() + parameter;
        ActionForward forward = new RedirectingActionForward(newPath);
        return this.handleSuccess(forward, context, forwardName);
    }

    /**
     * Handle successful exit with log. Helper method.
     * 
     * @param forward
     *            the forward.
     * @param context
     *            the action context to access resources.
     * @param forwardName
     *            represents messsageKey if fail, forward name if succeed.
     * 
     * @return the specified forward.
     */
    protected ActionForward handleSuccess(ActionForward forward, ContextAdapter context, String forwardName) {
        this.info(this.makeInfo(BaseAction.EXIT_OP, context.getOperator(), forwardName));
        return forward;
    }

    /**
     * Synthesize information for entrance or exit in each action. Helper method.
     * 
     * @param operation
     *            "Enter" or "Exit".
     * @param actionType
     *            type of action.
     * @param user
     *            the user.
     * @param forward
     *            the action forward string.
     * 
     * @return the synthesized information.
     */
    private String makeInfo(String operation, Object user, String forward) {
        return this.makeInfo(operation, user, forward, null);
    }

    /**
     * Synthesize information for entrance or exit in each action. Helper method.
     * 
     * @param operation
     *            "Enter" or "Exit".
     * @param actionType
     *            type of action.
     * @param user
     *            the user.
     * @param forward
     *            the action forward string.
     * 
     * @return the synthesized information.
     */
    private String makeInfo(String operation, Object user, String forward, HttpServletRequest request) {
        String actionType = this.getClass().getName();
        actionType = actionType.substring(actionType.lastIndexOf(".") + 1);
        StringBuffer buffer = new StringBuffer();
        buffer.append(operation);
        buffer.append(" ").append(actionType);
        buffer.append(" with user = ").append(user);
        if (forward != null) {
            buffer.append(", action forward to ").append(forward);
        }
        buffer.append(", timestamp = ").append(new Date()).append(". ");
        if (request != null) {
            buffer.append(request.getRemoteHost());
        }
        return buffer.toString();
    }

    /**
     * Checks whether user is logged in.
     * 
     * @return
     */
    protected boolean isLogin(ContextAdapter context) {
        return this.isLogin(context, false);
    }

    /**
     * Checks whether user is logged in.
     * 
     * @return
     */
    protected boolean isLogin(ContextAdapter context, boolean includeParameters) {
        if (context.getUserProfile() == null) {
            String uri = context.getRequest().getRequestURI();
            uri = uri.substring(uri.indexOf('/', 1));
            if (includeParameters) {
                StringBuffer sb = new StringBuffer();
                sb.append(uri);
                Enumeration<String> e = context.getRequest().getParameterNames();
                boolean first = true;
                while (e.hasMoreElements()) {
                    String key = e.nextElement();
                    if ("source".equals(key)) {
                        continue;
                    }
                    if (first) {
                        sb.append("?");
                        first = false;
                    } else {
                        sb.append("&");
                    }
                    sb.append(key + "=" + context.getRequest().getParameter(key));
                }
                uri = sb.toString();
            }
            context.setAttribute("forward", uri);

            return false;
        }
        return true;
    }

    protected ActionForward checkContestViewPermission(ActionMapping mapping, ContextAdapter context,
                                                       Boolean isProblemset, boolean checkStart) throws Exception {
        return this.checkContestPermission(mapping, context, isProblemset, checkStart, PermissionLevel.VIEW);
    }

    protected ActionForward checkContestParticipatePermission(ActionMapping mapping, ContextAdapter context,
                                                              Boolean isProblemset, boolean checkStart) throws Exception {
        return this.checkContestPermission(mapping, context, isProblemset, checkStart, PermissionLevel.PARTICIPATE);
    }

    protected ActionForward checkContestAdminPermission(ActionMapping mapping, ContextAdapter context,
                                                        Boolean isProblemset, boolean checkStart) throws Exception {
        return this.checkContestPermission(mapping, context, isProblemset, checkStart, PermissionLevel.ADMIN);
    }

    protected ActionForward checkContestViewSourcePermission(ActionMapping mapping, ContextAdapter context,
                                                             Boolean isProblemset, boolean checkStart) throws Exception {
        return this.checkContestPermission(mapping, context, isProblemset, checkStart,
                                           PermissionLevel.PARTICIPATECANVIEWSOURCE);
    }

    protected ActionForward checkContestPermission(ActionMapping mapping, ContextAdapter context, Boolean isProblemset,
                                                   boolean checkStart, PermissionLevel level) throws Exception {
        // get the contest
        AbstractContest contest = context.getContest();
        if (contest == null || isProblemset != null && (contest instanceof Contest || contest instanceof Course) == isProblemset.booleanValue()) {
            context.setAttribute("contest", null);
            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("onlinejudge.showcontest.nocontestid"));
            this.saveErrors(context.getRequest(), messages);
            if (isProblemset != null) {
                context.setAttribute("back", isProblemset ? "showProblemsets.do" : "showContests.do");
            }
            return this.handleFailure(mapping, context, messages, "nopermission");
        }

        context.setAttribute("contest", contest);
        // check contest permission
        UserSecurity userSecurity = context.getUserSecurity();
        boolean hasPermisstion = false;
        if (level == PermissionLevel.ADMIN) {
            hasPermisstion = userSecurity.canAdminContest(contest.getId());
        } else if (level == PermissionLevel.PARTICIPATE) {
            hasPermisstion = userSecurity.canParticipateContest(contest.getId());
        } else if (level == PermissionLevel.VIEW) {
            hasPermisstion = userSecurity.canViewContest(contest.getId());
        } else if (level == PermissionLevel.PARTICIPATECANVIEWSOURCE) {
            hasPermisstion = userSecurity.canViewSource(contest.getId());
        }
        if (!hasPermisstion) {
            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("onlinejudge.showcontest.nopermission"));
            this.saveErrors(context.getRequest(), messages);
            if (isProblemset != null) {
                context.setAttribute("back", isProblemset ? "showProblemsets.do" : "showContests.do");
            }
            return this.handleFailure(mapping, context, messages, "nopermission");
        }

        // check start time
        if (checkStart && !userSecurity.canAdminContest(contest.getId())) {
            return this.checkContestStart(mapping, context, contest);
        }
        return null;
    }

    protected ActionForward checkProblemViewPermission(ActionMapping mapping, ContextAdapter context,
                                                       Boolean isProblemset) throws Exception {
        return this.checkProblemPermission(mapping, context, isProblemset, PermissionLevel.VIEW);
    }

    protected ActionForward checkProblemParticipatePermission(ActionMapping mapping, ContextAdapter context,
                                                              Boolean isProblemset) throws Exception {
        return this.checkProblemPermission(mapping, context, isProblemset, PermissionLevel.PARTICIPATE);
    }

    protected ActionForward checkProblemAdminPermission(ActionMapping mapping, ContextAdapter context,
                                                        Boolean isProblemset) throws Exception {
        return this.checkProblemPermission(mapping, context, isProblemset, PermissionLevel.ADMIN);
    }

    protected ActionForward checkProblemViewSourecPermission(ActionMapping mapping, ContextAdapter context,
                                                             Boolean isProblemset) throws Exception {
        return this.checkProblemPermission(mapping, context, isProblemset, PermissionLevel.PARTICIPATECANVIEWSOURCE);
    }

    protected ActionForward checkProblemPermission(ActionMapping mapping, ContextAdapter context, Boolean isProblemset,
                                                   PermissionLevel level) throws Exception {

        Problem problem = context.getProblem();
        AbstractContest contest = null;

        if (problem != null) {
            contest = ContestManager.getInstance().getContest(problem.getContestId());
        }

        if (problem == null || contest == null || isProblemset != null &&
            (contest instanceof Contest || contest instanceof Course) == isProblemset.booleanValue()) {
            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("onlinejudge.showproblem.noproblemid"));
            this.saveErrors(context.getRequest(), messages);
            if (isProblemset != null) {
                context.setAttribute("back", isProblemset ? "showProblemsets.do" : "showContests.do");
            }
            return this.handleFailure(mapping, context, messages, "nopermission");
        }

        context.setAttribute("contest", contest);
        context.setAttribute("problem", problem);

        // check contest permission
        UserSecurity userSecurity = context.getUserSecurity();
        boolean hasPermisstion = false;
        if (level == PermissionLevel.ADMIN) {
            hasPermisstion = userSecurity.canAdminContest(contest.getId());
        } else if (level == PermissionLevel.PARTICIPATE) {
            hasPermisstion = userSecurity.canParticipateContest(contest.getId());
        } else if (level == PermissionLevel.PARTICIPATECANVIEWSOURCE) {
            hasPermisstion = userSecurity.canViewSource(contest.getId());
        } else if (level == PermissionLevel.VIEW) {
            hasPermisstion = userSecurity.canViewContest(contest.getId());
        }
        if (!hasPermisstion) {
            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("onlinejudge.showcontest.nopermission"));
            this.saveErrors(context.getRequest(), messages);
            if (isProblemset != null) {
                context.setAttribute("back", isProblemset ? "showProblemsets.do" : "showContests.do");
            }
            return this.handleFailure(mapping, context, messages, "nopermission");
        }

        // check start time
        if (userSecurity.canAdminContest(contest.getId())) {
            return null;
        } else {
            return this.checkContestStart(mapping, context, contest);
        }

    }

    private ActionForward checkContestStart(ActionMapping mapping, ContextAdapter context, AbstractContest contest) throws PersistenceException {
        if (contest.getStartTime() == null) {
            return null;
        }
        if (contest.getStartTime().getTime() > System.currentTimeMillis()) {
            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("onlinejudge.showcontest.nostarted"));
            this.saveErrors(context.getRequest(), messages);
            context.setAttribute("back", "contestInfo.do?contestId=" + contest.getId());

            return this.handleFailure(mapping, context, messages, "nopermission");
        }
        return null;
    }

    protected ActionForward checkAdmin(ActionMapping mapping, ContextAdapter context) throws Exception {
        UserSecurity security = context.getUserSecurity();
        if (security == null || !security.isSuperAdmin()) {
            return this.handleSuccess(mapping, context, "nopermission");
        }
        return null;
    }

    protected ActionForward checkLastLoginIP(ActionMapping mapping, ContextAdapter context, boolean isProblemset) throws Exception {
        String ip = context.getRequest().getRemoteHost();
        long contestId = context.getContest().getId();
        String ipSessionKey = "last_submit_ip" + contestId;
        String lastIp = (String) context.getSessionAttribute(ipSessionKey);
        if (lastIp == null) {
            ContestPersistence contestPersistence = PersistenceManager.getInstance().getContestPersistence();
            long userId = context.getUserProfile().getId();
            lastIp = contestPersistence.getLastSubmitIP(userId, contestId);
            if (lastIp == null) {
                // first submit
                contestPersistence.setLastSubmitIP(userId, contestId, ip);
                context.setSessionAttribute(ipSessionKey, lastIp);
                return null;
            }
            context.setSessionAttribute(ipSessionKey, lastIp);
        }
        if (!lastIp.equals(ip)) {
            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("onlinejudge.submit.invalid_ip"));
            this.saveErrors(context.getRequest(), messages);
            context.setAttribute("back", "contestInfo.do?contestId=" + contestId);

            return this.handleFailure(mapping, context, messages, "nopermission");
        }
        return null;

    }
}
