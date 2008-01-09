package cn.edu.zju.acm.onlinejudge.persistence.sql;


import com.mysql.jdbc.Driver;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>DatabaseHelper.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
class DatabaseHelper {
	
	/**
	 * The query to get last id.
	 */
	private static final String GET_LAST_ID = "SELECT LAST_INSERT_ID()";
	
	
	/**
	 * The data source config file.
	 */
	public static String CONFIG_FILE = "test_files/persistence/mysql_data_source.properties";
	
	/**
	 * The initialization script
	 */
	public static String INITIAL_INSERTS = "test_files/persistence/initial.sql";
	
	
	/**
	 * A flag indicates whether all tables are cleared.
	 */
	public static boolean allTablesCleared = false;
	
	/**
	 * The initial inserts.
	 */
	public static Map initialInserts = new HashMap();
	
	/**
	 * A string array containing table names.
	 */
	public static String[] tables = new String[] {
		"submission",
		"user_profile",
		"user_preference",
		"confirmation",
		"role",
		"user_role",
		"contest_permission",
		"forum_permission",
		"permission_level",	
		"problem",
		"contest_language",
		"language",
		"contest",
		"limits",				
		"judge_reply",
		"forum",
		"thread",
		"post",
		"reference",
		"reference_type",
		"contest_reference",
		"problem_reference",
		"forum_reference",
		"configuration"		
	};
	
	/**
	 * Private constructor.
	 */
	private DatabaseHelper() {
		// empty
	}	
	
	/**
	 * Gets a connection.
	 * @return a connection
	 * @throws Exception to JUnit
	 */
	public static Connection createConnection() throws Exception  {
		Properties properties = new Properties();		
		properties.load(new FileInputStream(CONFIG_FILE));
		 		
		Driver driver = (Driver) Class.forName(properties.getProperty("driver")).newInstance();
		String url = properties.getProperty("url");		
		return driver.connect(url, properties);
	}				

	
	/**
	 * Execute the given sql script.
	 * @param filename the script file name
	 * @throws Exception to JUnit
	 */
	public static void executeScript(String filename) throws Exception {
		List commands = new ArrayList();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		for (;;) {
			String cmd = reader.readLine();
			if (cmd == null) {
				break;
			}
			commands.add(cmd);			
		}
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
        	
        	conn = createConnection();
        	conn.setAutoCommit(false);        	

        	for (Iterator it = commands.iterator(); it.hasNext();) {
        		ps = conn.prepareStatement((String) it.next());
        		ps.executeUpdate();
        	}
        	conn.commit();               
	    } finally {
	    	Database.dispose(conn, ps, rs);
	    }         
	}
	
	/**
	 * Execute the given sql update command.
	 * @param commad the command
	 * @throws Exception to JUnit
	 */
	public static void executeUpdate(String command) throws Exception {
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
        	
        	conn = createConnection();        	        	
        	ps = conn.prepareStatement(command);
        	ps.executeUpdate();
        	        	        
	    } finally {
	    	Database.dispose(conn, ps, rs);
	    }         
	}
	
	/**
	 * Execute the given sql insert command.
	 * @param commad the command
	 * @return the last id.
	 * @throws Exception to JUnit
	 */
	public static long executeInsert(String command) throws Exception {
		Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
        	
        	conn = createConnection();        	        	
        	ps = conn.prepareStatement(command);
        	ps.executeUpdate();
        	
        	ps = conn.prepareStatement(GET_LAST_ID);
            rs = ps.executeQuery();
            rs.next();            
            return rs.getLong(1);
        	        
	    } finally {
	    	Database.dispose(conn, ps, rs);
	    }         
	}
	
	/**
	 * Clears given table.
	 * @param tableName the table to clear
	 * @throws Exception to JUnit
	 */
	public static void clearTable(String tableName) throws Exception {
		executeUpdate("DELETE FROM " + tableName);
			
		if (initialInserts.containsKey(tableName)) {
			List cmds = (List) initialInserts.get(tableName);
			for (Iterator it = cmds.iterator(); it.hasNext();) {
				executeUpdate((String) it.next());
			}
		}
		
		
	}
	
	/**
	 * Clears all tables
	 * @param flag;
	 * @throws Exception to JUnit
	 */
	public static void resetAllTables(boolean flag) throws Exception {
		if (!allTablesCleared || flag) {
			for (int i = 0; i < tables.length; ++i) {
				clearTable(tables[i]);
			}
			BufferedReader reader = new BufferedReader(new FileReader(INITIAL_INSERTS));
			for (;;) {
				String cmd = reader.readLine();
				if (cmd == null) {
					break;
				}
				cmd = cmd.trim();
				if (cmd.length() > 0) {
					executeUpdate(cmd);
					if (cmd.startsWith("INSERT INTO")) {
						String table = cmd.substring(12, cmd.indexOf('('));
						List list = (List) initialInserts.get(table);
						if (list == null) {
							list = new ArrayList();
							initialInserts.put(table, list);
						}
						list.add(cmd);
					}
				}
			}
			allTablesCleared = true;
		}	
	}
	
	
    /**
     * Dispose JDBC resources.
     *
     * @param conn the connection.
     * @param ps the prepared statement.
     * @param rs the result set.
     */
    public static void dispose(Connection conn, PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
            }
        }
    }       

}
