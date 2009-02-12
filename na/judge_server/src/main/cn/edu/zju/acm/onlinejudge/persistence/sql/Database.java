/*
 * Copyright 2007 Zhang, Zheng <oldbig@gmail.com> Xu, Chuan <xuchuan@gmail.com>
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

/**
 * <p>
 * Dateabase.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 * @author Xu, Chuan
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

    static {
        try {
            Properties properties = new Properties();
            try {
                properties.load(Database.class.getClassLoader().getResourceAsStream(Database.CONFIG_FILE));
            } catch (IOException ioe) {
                throw new PersistenceException("IO error occurs when load " + Database.CONFIG_FILE + ".", ioe);
            }

            Database.ds = new BasicDataSource();
            Database.ds.setDefaultAutoCommit("true".equalsIgnoreCase(Database.getStringProperty(properties,
                                                                                                "DefaultAutoCommit")));
            Database.ds.setDefaultCatalog(Database.getStringProperty(properties, "DefaultCatalog"));
            Database.ds.setDriverClassName(Database.getStringProperty(properties, "DriverClassName"));
            Database.ds.setMaxActive(Database.getIntegerProperty(properties, "MaxActive"));
            Database.ds.setMaxIdle(Database.getIntegerProperty(properties, "MaxIdle"));
            Database.ds.setMaxWait(Database.getLongProperty(properties, "MaxWait"));
            Database.ds.setUrl(Database.getStringProperty(properties, "Url"));
            Database.ds.setPassword(Database.getStringProperty(properties, "Password"));
            Database.ds.setUsername(Database.getStringProperty(properties, "Username"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Gets a connection. This method is synchronized.
     * 
     * @return a connection
     * @throws PersistenceException
     *             if any error occurs.
     */
    public static Connection createConnection() throws PersistenceException {
        try {
            return Database.ds.getConnection();
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create connection", e);
        }
    }

    public static void dispose(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (Exception e) {}
        }
    }

    public static void dispose(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {}
        }
    }

    /**
     * Create a string containing given values. The string is like '(value1, value2, value3)'
     * 
     * @param values
     *            the values
     * @return a string like '(value1, value2, value3)'
     */
    public static String createValues(Collection<String> values) {
        StringBuilder ret = new StringBuilder();
        ret.append('(');
        for (String value : values) {
            if (ret.length() > 1) {
                ret.append(',');
            }
            ret.append('\'').append(value.replaceAll("'", "''")).append('\'');
        }
        ret.append(')');
        return ret.toString();
    }

    /**
     * Create a string containing given number values. The string is like '(value1, value2, value3)'
     * 
     * @param values
     *            the values
     * @return a string like '(value1, value2, value3)'
     */
    public static String createNumberValues(Collection<Long> values) {
        StringBuilder ret = new StringBuilder();
        ret.append('(');
        for (Long value : values) {
            if (ret.length() > 1) {
                ret.append(',');
            }
            ret.append(value);
        }
        ret.append(')');
        return ret.toString();
    }

    /**
     * Rollback given connection.
     * 
     * @param conn
     *            the connection
     */
    public static void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
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
    public static long getLastId(Connection conn) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(Database.GET_LAST_ID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);
        } finally {
            Database.dispose(ps);
        }
    }

    /**
     * Gets a date value from given ResultSet.
     * 
     * @param rs
     *            the ResultSet
     * @param column
     *            the column name
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
     * 
     * @param date
     *            the date
     * @return a Timestamp instance.
     */
    public static Timestamp toTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    /**
     * Gets the given string property
     * 
     * @param properties
     *            properties
     * @param key
     *            property key
     * @return the property value in string
     */
    private static String getStringProperty(Properties properties, String key) throws PersistenceException {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new PersistenceException(key + " property is missing in " + Database.CONFIG_FILE);
        }
        return value;
    }

    /**
     * Gets the given int property
     * 
     * @param properties
     *            properties
     * @param key
     *            property key
     * @return the property value in int
     */
    private static int getIntegerProperty(Properties properties, String key) throws PersistenceException {
        String value = Database.getStringProperty(properties, key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            throw new PersistenceException(key + " property is an invalid integer");
        }
    }

    /**
     * Gets the given long property
     * 
     * @param properties
     *            properties
     * @param key
     *            property key
     * @return the property value in long
     */
    private static long getLongProperty(Properties properties, String key) throws PersistenceException {
        String value = Database.getStringProperty(properties, key);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            throw new PersistenceException(key + " property is an invalid long");
        }
    }
}
