/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;

/**
 * <p>Forum bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class Forum {

    /**
     * <p>Represents id.</p>
     */
    private long id = -1;

    /**
     * <p>Represents name.</p>
     */
    private String name = null;

    /**
     * <p>Represents description.</p>
     */
    private String description = null;

    /**
     * <p>Empty constructor.</p>
     */
    public Forum() {
    }

    /**
     * <p>Gets id.</p>
     *
     * @return id
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>Sets id.</p>
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>Gets name.</p>
     *
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * <p>Sets name.</p>
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Gets description.</p>
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * <p>Sets description.</p>
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
