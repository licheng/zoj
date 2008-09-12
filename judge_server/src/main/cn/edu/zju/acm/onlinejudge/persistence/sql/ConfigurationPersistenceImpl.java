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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.zju.acm.onlinejudge.bean.Configuration;
import cn.edu.zju.acm.onlinejudge.persistence.ConfigurationPersistence;
import cn.edu.zju.acm.onlinejudge.persistence.PersistenceException;

/**
 * <p>
 * ConfigurationPersistenceImpl implements ConfigurationPersistence interface
 * </p>
 * <p>
 * ConfigurationPersistence interface defines the API used to load and store Configurations from the persistence layer.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 */
public class ConfigurationPersistenceImpl implements ConfigurationPersistence {

    /**
     * The query to get all configurations.
     */
    private static final String GET_ALL_CONFIGURATIONS = "SELECT * FROM configuration";

    /**
     * The query to get configuration names.
     */
    private static final String GET_CONFIGURATION_NAMES = "SELECT name FROM configuration WHERE name IN ";

    /**
     * The query to insert a configuration.
     */
    private static final String INSERT_CONFIGURATION =
            "INSERT INTO configuration (name, value, description, create_user, create_date, "
                + "last_update_user, last_update_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

    /**
     * The query to insert a configuration.
     */
    private static final String UPDATE_CONFIGURATION =
            "UPDATE configuration SET value=?, description=?, last_update_user=?, last_update_date=? WHERE name=?";

    /**
     * <p>
     * Returns a list of Configuration instances retrieved from persistence layer.
     * </p>
     * 
     * @return a list of Configuration instances retrieved from persistence layer.
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    public List<Configuration> getConfigurations() throws PersistenceException {
        Connection conn = null;
        try {
            conn = Database.createConnection();
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(ConfigurationPersistenceImpl.GET_ALL_CONFIGURATIONS);
                ResultSet rs = ps.executeQuery();
                List<Configuration> configurations = new ArrayList<Configuration>();
                while (rs.next()) {
                    Configuration configuration = new Configuration();
                    configuration.setName(rs.getString(DatabaseConstants.CONFIGURATION_NAME));
                    configuration.setValue(rs.getString(DatabaseConstants.CONFIGURATION_VALUE));
                    configuration.setDescription(rs.getString(DatabaseConstants.CONFIGURATION_DESCRIPTION));
                    configurations.add(configuration);
                }
                return configurations;
            } finally {
                Database.dispose(ps);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error.", e);
        } finally {
            Database.dispose(conn);
        }
    }

    /**
     * <p>
     * Stores the given list of Configuration instances to persistence layer.
     * </p>
     * 
     * @param configurations
     *            a list of Configuration instances to store
     * @param user
     *            the id of the user who made this modification.
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     * @throws NullPointerException
     *             if configurations is null
     * @throws IllegalArgumentException
     *             if configurations contains invalid or duplicate element
     */
    public void setConfigurations(List<Configuration> configurations, long user) throws PersistenceException {
        if (configurations.size() == 0) {
            return;
        }
        Set<String> nameSet = new HashSet<String>();
        for (Configuration configuration : configurations) {
            String name = configuration.getName();
            if (!nameSet.add(name)) {
                throw new IllegalArgumentException("configurations contains duplicate element");
            }
        }

        Connection conn = null;
        try {
            conn = Database.createConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            Set<String> existingConfigurations = new HashSet<String>();
            try {
                // get existing configurations
                ps =
                        conn.prepareStatement(ConfigurationPersistenceImpl.GET_CONFIGURATION_NAMES +
                            Database.createValues(nameSet));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    existingConfigurations.add(rs.getString(DatabaseConstants.CONFIGURATION_NAME));
                }
            } finally {
                Database.dispose(ps);
            }
            // update
            for (Configuration configuration : configurations) {
                try {
                    if (existingConfigurations.contains(configuration.getName())) {
                        ps = conn.prepareStatement(ConfigurationPersistenceImpl.UPDATE_CONFIGURATION);
                        ps.setString(5, configuration.getName());
                        ps.setString(1, configuration.getValue());
                        ps.setString(2, configuration.getDescription());
                        ps.setLong(3, user);
                        ps.setTimestamp(4, new Timestamp(new Date().getTime()));
                    } else {
                        ps = conn.prepareStatement(ConfigurationPersistenceImpl.INSERT_CONFIGURATION);
                        ps.setString(1, configuration.getName());
                        ps.setString(2, configuration.getValue());
                        ps.setString(3, configuration.getDescription());
                        ps.setLong(4, user);
                        ps.setTimestamp(5, new Timestamp(new Date().getTime()));
                        ps.setLong(6, user);
                        ps.setTimestamp(7, new Timestamp(new Date().getTime()));
                    }
                    ps.executeUpdate();
                } finally {
                    Database.dispose(ps);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            Database.rollback(conn);
            throw new PersistenceException("Error.", e);
        } finally {
            Database.dispose(conn);
        }
    }

}
