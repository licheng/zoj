/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean;

/**
 * <p>Configuration bean.</p>
 *
 * @author ZOJDEV
 *
 * @version 2.0
 */
public class Configuration {

    /**
     * <p>Represents name.</p>
     */
    private String name = null;

    /**
     * <p>Represents value.</p>
     */
    private String value = null;

    /**
     * <p>Represents description.</p>
     */
    private String description = null;

    /**
     * <p>Empty constructor.</p>
     */
    public Configuration() {
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
     * <p>Gets value.</p>
     *
     * @return value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * <p>Sets value.</p>
     *
     * @param value value
     */
    public void setValue(String value) {
        this.value = value;
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
