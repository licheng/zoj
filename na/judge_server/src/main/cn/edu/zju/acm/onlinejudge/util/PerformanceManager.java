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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.Action;

import cn.edu.zju.acm.onlinejudge.bean.UserProfile;
import cn.edu.zju.acm.onlinejudge.bean.request.LogCriteria;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;
import cn.edu.zju.acm.onlinejudge.persistence.sql.Database;

public class PerformanceManager {
	
	private static final String ACTION_PACKAGE = "cn.edu.zju.acm.onlinejudge.action.";
	private static final int TIME_LIMIT = 60000;
	private static final int SIZE_LIMIT = 50;
	private static PerformanceManager instance = null;
	
	private List<AccessLog> finished = new ArrayList<AccessLog>();
	private Map<Long, AccessLog> waiting = new HashMap<Long, AccessLog>();
	private long id = 0;
	
	private long lastSave = 0;
	private Runner runner = new Runner();
	public PerformanceManager() {
		runner.start();
	}
	
	public synchronized long actionStart(Action action, HttpServletRequest request, UserProfile user) {
		String actionName = action.getClass().getName();
		if (actionName.startsWith(ACTION_PACKAGE)) {
			actionName = actionName.substring(ACTION_PACKAGE.length());
		}
		AccessLog log = new AccessLog();
		log.setTimestamp(new Date());
		String url = request.getServletPath() + (request.getQueryString() == null ? "" : "?" + request.getQueryString()) + " " + request.getMethod();
		
		log.setUrl(url);
		log.setAction(actionName);
		if (user != null) {
			log.setUserId(user.getId());
			log.setHandle(user.getHandle());
		}
		log.setIp(request.getRemoteHost());
		id++;
		
		waiting.put(id, log);
		return id;
	}
	
	public synchronized void actionEnd(long id) {
		AccessLog log = waiting.get(id);
		if (log == null) {
			return;
		}
		log.setAccessTime(System.currentTimeMillis() - log.getTimestamp().getTime());
		waiting.remove(id);
		addAccessLog(log);
		
	}
	
	public synchronized void addAccessLog(AccessLog log) {
		finished.add(log);
		if (finished.size() >= SIZE_LIMIT) {
			saveFinished();
		}
	}
	
	public synchronized void saveFinished() {
		lastSave = System.currentTimeMillis();
		if (finished.size() == 0) {
			return;
		}
		try {
			saveAccessLog(finished);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		
		// TODO save;
		finished.clear();
		
	}
	public synchronized void saveAccessLog(List<AccessLog> logs) throws PersistenceException {
		Connection conn = null;
	    PreparedStatement ps = null;        
	    try {
	    	conn = Database.createConnection();
	    	ps = conn.prepareStatement("INSERT INTO access_log (user_profile_id, handle, action, url, ip, timestamp, access_time) " +
	        		"VALUES(?,?,?,?,?,?,?)");
	        for (AccessLog log : logs) {
	        	if (log.getHandle() == null) {
	        		ps.setNull(1, Types.BIGINT);
	        		ps.setNull(2, Types.VARCHAR);
	        	} else {
	        		ps.setLong(1, log.getUserId());
	        		ps.setString(2, log.getHandle());
	        	}
	        	ps.setString(3, log.getAction());
	        	ps.setString(4, log.getUrl());
	        	ps.setString(5, log.getIp());
	        	ps.setTimestamp(6, new Timestamp(log.getTimestamp().getTime()));
	        	ps.setLong(7, log.getAccessTime());
	        	ps.addBatch();
	        }
	        ps.executeBatch();
	        
		} catch (SQLException e) {
	    	throw new PersistenceException("Failed to save logs.", e);
		} finally {
	    	Database.dispose(ps);
	    	Database.dispose(conn);
	    }   
	}
	
	public PreparedStatement buildQuery(LogCriteria criteria, String orderBy, Connection conn) throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		if ("action".equals(orderBy)) {
			orderBy = "action";
		} else if ("count".equals(orderBy)) {
			orderBy = "count DESC";
		} else if ("max".equals(orderBy)) {
			orderBy = "max DESC";
		} else if ("min".equals(orderBy)) {
			orderBy = "min ASC";
		} else {
			orderBy = "avg DESC";
		}
		
		if (criteria.getTimeStart() != null) {
			sb.append(" AND timestamp>=?");
		}
		if (criteria.getTimeEnd() != null) {
			sb.append(" AND timestamp<=?");
		}
		if (criteria.getIp() != null && criteria.getIp().trim().length() > 0) {
			sb.append(" AND ip='" + criteria.getIp() + "'");
		}
		if (criteria.getUserId() != null) {
			sb.append(" AND user_profile_id=" + criteria.getUserId());
		}
		if (criteria.getHandle() != null && criteria.getHandle().trim().length() > 0) {
			sb.append(" AND handle='" + criteria.getHandle() + "'");
		}
		
		String where = sb.length() == 0 ? "" : "WHERE " + sb.toString().substring(4);
		String sql = "SELECT action, count(*) count, max(access_time) max, min(access_time) min, avg(access_time) avg " +
				"FROM access_log " + where + " GROUP BY action ORDER BY " + orderBy; 
		
		PreparedStatement ps = conn.prepareStatement(sql);
		
		int index = 1;
		if (criteria.getTimeStart() != null) {
			ps.setTimestamp(index, new Timestamp(criteria.getTimeStart().getTime()));
			index++;
		}
		if (criteria.getTimeEnd() != null) {
			ps.setTimestamp(index, new Timestamp(criteria.getTimeEnd().getTime()));
			index++;
		}
		
		return ps;
	}
	
	public PreparedStatement buildQuery(LogCriteria criteria, int offset, int count, String orderBy, Connection conn) throws SQLException {
		StringBuilder sb = new StringBuilder();
		
		if ("accessTimeDesc".equals(orderBy)) {
			orderBy = "access_time DESC, timestamp DESC";
		} else if ("accessTimeAsc".equals(orderBy)) {
			orderBy = "access_time ASC, timestamp DESC";
		} else if ("timestampAsc".equals(orderBy)) {
			orderBy = "timestamp ASC";
		} else {
			orderBy = "timestamp DESC";
		}
		
		if (criteria.getTimeStart() != null) {
			sb.append(" AND timestamp>=?");
		}
		if (criteria.getTimeEnd() != null) {
			sb.append(" AND timestamp<=?");
		}
		if (criteria.getAction() != null && criteria.getAction().trim().length() > 0) {
			sb.append(" AND action='" + criteria.getAction() + "'");
		}
		if (criteria.getIp() != null && criteria.getIp().trim().length() > 0) {
			sb.append(" AND ip='" + criteria.getIp() + "'");
		}
		if (criteria.getUserId() != null) {
			sb.append(" AND user_profile_id=" + criteria.getUserId());
		}
		if (criteria.getHandle() != null && criteria.getHandle().trim().length() > 0) {
			sb.append(" AND handle='" + criteria.getHandle() + "'");
		}
		
		String where = sb.length() == 0 ? "" : "WHERE " + sb.toString().substring(4);
		String sql = "SELECT * FROM access_log " + where + " ORDER BY " + orderBy + " LIMIT " + offset + "," + count; 
		
		PreparedStatement ps = conn.prepareStatement(sql);
		
		int index = 1;
		if (criteria.getTimeStart() != null) {
			ps.setTimestamp(index, new Timestamp(criteria.getTimeStart().getTime()));
			index++;
		}
		if (criteria.getTimeEnd() != null) {
			ps.setTimestamp(index, new Timestamp(criteria.getTimeEnd().getTime()));
			index++;
		}
		
		return ps;
	}
	public synchronized List<ActionLog> getActionDashboard(LogCriteria criteria, String orderBy) throws PersistenceException {
		Connection conn = null;
	    PreparedStatement ps = null;        
	    ResultSet rs = null;
	    try {
	    	conn = Database.createConnection();
	    	
	    	ps = buildQuery(criteria, orderBy, conn);
	    	
	    	rs = ps.executeQuery();
	    	List<ActionLog> logs = new ArrayList<ActionLog>();
	    	while (rs.next()) {
	    		ActionLog log = new ActionLog();
	    		log.setAction(rs.getString("action"));
	    		log.setAvgAccessTime(rs.getLong("avg"));
	    		log.setMinAccessTime(rs.getLong("min"));
	    		log.setMaxAccessTime(rs.getLong("max"));
	    		log.setCount(rs.getLong("count"));
	    		
	    		logs.add(log);
	    	}
	    	return logs;
		} catch (SQLException e) {
	    	throw new PersistenceException("Failed to search logs.", e);
		} finally {
			Database.dispose(ps);
	    	Database.dispose(conn);
	    } 
	}
	public synchronized List<AccessLog> searchLogs(LogCriteria criteria, int offset, int count, String orderBy) throws PersistenceException {
		Connection conn = null;
	    PreparedStatement ps = null;        
	    ResultSet rs = null;
	    try {
	    	conn = Database.createConnection();
	    	ps = buildQuery(criteria, offset, count, orderBy, conn);
	    	
	    	rs = ps.executeQuery();
	    	List<AccessLog> logs = new ArrayList<AccessLog>();
	    	while (rs.next()) {
	    		AccessLog log = new AccessLog();
	    		log.setId(rs.getLong("access_log_id"));
	    		if (rs.getObject("user_profile_id") != null) {
	    			log.setUserId(rs.getLong("user_profile_id"));
	    		}
	    		if (rs.getObject("handle") != null) {
	    			log.setHandle(rs.getString("handle"));
	    		}

	    		log.setUrl(rs.getString("url"));
	    		log.setAction(rs.getString("action"));
	    		log.setIp(rs.getString("ip"));
	    		log.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime()));
	    		log.setAccessTime(rs.getLong("access_time"));
	    		
	    		logs.add(log);
	    	}
	    	return logs;
		} catch (SQLException e) {
	    	throw new PersistenceException("Failed to search logs.", e);
		} finally {
			Database.dispose(ps);
	    	Database.dispose(conn);
	    }   
	}
	
	
	
	public static PerformanceManager getInstance() {
    	
		if (instance == null) {
		    synchronized (StatisticsManager.class) {
				if (instance == null) {
				    instance = new PerformanceManager();
				}
		    }
		}
		return instance;
    }

	private class Runner extends Thread {
		public void run() {
			for (;;) {
				try {
					if (System.currentTimeMillis() - lastSave > TIME_LIMIT - 1000) {
						saveFinished();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(TIME_LIMIT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
