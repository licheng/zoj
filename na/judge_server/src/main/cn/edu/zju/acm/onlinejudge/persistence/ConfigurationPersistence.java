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

package cn.edu.zju.acm.onlinejudge.persistence;

import java.util.List;

import cn.edu.zju.acm.onlinejudge.bean.Configuration;

/**
 * <p>
 * ConfigurationPersistence interface defines the API used to load and store Configurations from the persistence layer.
 * </p>
 * 
 * @version 2.0
 * @author Zhang, Zheng
 */
public interface ConfigurationPersistence {

    /**
     * <p>
     * Returns a list of Configuration instances retrieved from persistence layer.
     * </p>
     * 
     * @return a list of Configuration instances retrieved from persistence layer.
     * @throws PersistenceException
     *             wrapping a persistence implementation specific exception
     */
    List<Configuration> getConfigurations() throws PersistenceException;

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
     */
    void setConfigurations(List<Configuration> configurations, long user) throws PersistenceException;

}
