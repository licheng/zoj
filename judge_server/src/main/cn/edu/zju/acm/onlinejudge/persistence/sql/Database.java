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

package cn.edu.zju.acm.onlinejudge.persistence.sql;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.sql.DataSource;

import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;


/**
 * <p>Dateabase.</p>
 * 
 * @version 2.0
 * @author ZOJDEV
 */
public class Database {
	
	/**
	 * The query to get last id.
	 */
	private static final String GET_LAST_ID = "SELECT LAST_INSERT_ID()";
	
	
	/**
	 * The data source config file.
	 */
	public static String CONFIG_FILE = "data_source.properties";
	
	
	/**
	 * A BasicDataSource instance used to create connection. 
	 */
	private static BasicDataSource ds = null;
			
	/**
	 * Private constructor.
	 */
	private Database() {
		// empty
	}	
	
	/**
	 * Gets a data source.
	 * @return a data source
	 * @throws PersistenceException if fail to create the data source
	 */
	private static DataSource getDataSource() throws PersistenceException {
		if (ds == null) {
			BasicDataSource dataSource = new BasicDataSource();
						
			Properties properties = new Properties();
			
			try {			
                
                //properties.load(new FileInputStream("D:\\work\\workspace\\zoj2dev\\conf\\data_source.properties"));
				properties.load(Database.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
			} catch (IOException ioe) {
				throw new PersistenceException("IO error occurs when load " + CONFIG_FILE + ".", ioe);
			}
			dataSource.setDefaultAutoCommit("true".equalsIgnoreCase(getStringProperty(properties, "DefaultAutoCommit")));								
			dataSource.setDefaultCatalog(getStringProperty(properties, "DefaultCatalog"));
			dataSource.setDriverClassName(getStringProperty(properties, "DriverClassName"));			
			dataSource.setMaxActive(getIntegerProperty(properties, "MaxActive"));			
			dataSource.setMaxIdle(getIntegerProperty(properties, "MaxIdle"));
			dataSource.setMaxWait(getLongProperty(properties, "MaxWait"));			
			dataSource.setUrl(getStringProperty(properties, "Url"));			
			dataSource.setPassword(getStringProperty(properties, "Password"));
			dataSource.setUsername(getStringProperty(properties, "Username"));	
			
			ds = dataSource;
		}
		return ds;
	}
    
    private static void writeByte(FileWriter w, byte[] bs) throws Exception {
        for (byte b : bs) {
            int i = b;
            if (i < 0) i+=256;
            w.write(Integer.toHexString(i) + " ");
            System.out.print(Integer.toHexString(i) + " ");
        }
        w.write("\n");
        System.out.println();
    }
	public static void main(String[] args) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = Database.createConnection();         
    
            ps = conn.prepareStatement("select * from contest where contest_id=7");  
            rs = ps.executeQuery();
            rs.next();
            FileWriter w = new FileWriter("c:\\test.txt");
            String s = rs.getString("title").substring(6);
            w.write(s);
            w.write("\n");
            writeByte(w, s.getBytes());
            writeByte(w, s.getBytes("gb2312"));
            writeByte(w, s.getBytes("utf-8"));
            s = new String(s.getBytes("utf-8"), "utf-8"); 
            w.write(s);
            w.write("\n");
            
            
            
            w.close();
            
            
            
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create forum.", e);
        } finally {
            Database.dispose(conn, ps, rs);
        }   
        
    }
	/**
	 * Gets a connection. This method is synchronized.
	 * @return a connection
	 * @throws PersistenceException if any error occurs.
	 */
	public static Connection createConnection() throws PersistenceException  {
		synchronized (Database.class) {
			try {
				return getDataSource().getConnection();
			} catch (SQLException se) {
				throw new PersistenceException("Failed to crate the connection", se);
			}
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
    
    
    /**
     * Create a string containing given values. 
     * The string is like '(value1, value2, value3)'
     * @param values the values
     * @return a string like '(value1, value2, value3)'
     */
    public static String createValues(Collection<String> values) {
    	StringBuffer ret = new StringBuffer();
    	ret.append('(');
    	for (Iterator<String> itr = values.iterator(); itr.hasNext();) {
    		if (ret.length() > 1) {
    			ret.append(", ");
    		}
    		ret.append("'").append(itr.next()).append("'");
    	} 
    	ret.append(')');     	
    	return ret.toString();
    }
    
    /**
     * Create a string containing given number values. 
     * The string is like '(value1, value2, value3)'
     * @param values the values
     * @return a string like '(value1, value2, value3)'
     */
    public static String createNumberValues(Collection<Long> values) {
    	StringBuffer ret = new StringBuffer();
    	ret.append('(');
    	for (Iterator<Long> itr = values.iterator(); itr.hasNext();) {
    		if (ret.length() > 1) {
    			ret.append(", ");
    		}
    		ret.append(itr.next());
    	} 
    	ret.append(')');     	
    	return ret.toString();
    }
    
    /**
     * Gets the given string property
     * @param properties properties
     * @param key property key
     * @return the property value in string
     */
    private static String getStringProperty(Properties properties, String key) throws PersistenceException {
    	String value = properties.getProperty(key);
    	if (value == null) {
    		throw new PersistenceException(key + " property is missing in " + CONFIG_FILE);
    	}
    	return value;
    }
    
    /**
     * Gets the given int property
     * @param properties properties
     * @param key property key
     * @return the property value in int
     */
    private static int getIntegerProperty(Properties properties, String key) throws PersistenceException {
    	
    	String value = getStringProperty(properties, key);
    	try {
    		return Integer.parseInt(value);
    	} catch (NumberFormatException nfe) {
    		throw new PersistenceException(key + " property is invalid integer");
    	}    	
    }
    
    /**
     * Gets the given long property
     * @param properties properties
     * @param key property key
     * @return the property value in long
     */
    private static long getLongProperty(Properties properties, String key) throws PersistenceException {
    	
    	String value = getStringProperty(properties, key);
    	try {
    		return Long.parseLong(value);
    	} catch (NumberFormatException nfe) {
    		throw new PersistenceException(key + " property is invalid long");
    	}    	
    }    
    
    /**
     * Rollback given connection.
     * 
     * @param conn the connection
     */
    public static void rollback(Connection conn) {
    	try {
    		conn.rollback(); 
    	} catch (SQLException e) {
    		// ignore
    	}
    	
    }
    
    /**
     * Gets the last id.
     * 
     * @param conn 
     * @param ps
     * @param rs
     * @return the last id
     * @throws SQLException
     */
    public static long getLastId(Connection conn, PreparedStatement ps, ResultSet rs) throws SQLException {
        ps = conn.prepareStatement(GET_LAST_ID);            
        rs = ps.executeQuery();
        rs.next();  
        return rs.getLong(1);                
    }
    
    /**
     * Gets a date value from given ResultSet.
     * @param rs the ResultSet
     * @param column the column name
     * @return a date value from given ResultSet.
     * @throws SQLException
     */
    public static Date getDate(ResultSet rs, String column) throws SQLException {
    	Timestamp date = rs.getTimestamp(column);
    	if (date == null) {
    		return null;
    	} 
    	return new Date(date.getTime());
    }
    
    /**
     * Convert given Date to Timestamp.
     * @param date the date
     * @return a Timestamp instance. 
     */
    public static Timestamp toTimestamp(Date date) {
    	if (date == null) {
    		return null;
    	}
    	return new Timestamp(date.getTime());
    }

}
