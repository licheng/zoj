/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence;

import java.util.List;

/**
 * <p>ConfigurationPersistence interface defines the API used to load and store Configurations
 * from the persistence layer.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public interface ConfigurationPersistence {

    /**
     * <p>Returns a list of Configuration instances retrieved from persistence layer.</p>
     *
     * @return a list of Configuration instances retrieved from persistence layer.
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    List getConfigurations() throws PersistenceException;

    /**
     * <p>Stores the given list of Configuration instances to persistence layer.</p>
     *
     * @param configurations a list of Configuration instances to store
     * @param user the id of the user who made this modification.
     * @throws PersistenceException wrapping a persistence implementation specific exception
     */
    void setConfigurations(List configurations, long user) throws PersistenceException;

}


