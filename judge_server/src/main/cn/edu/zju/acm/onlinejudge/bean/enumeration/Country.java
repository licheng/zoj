/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean.enumeration;

/**
 * <p>This class represents the Country. It has two attributes: id, name.</p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class Country {

    /**
     * <p>Represents the id of the country. It is initialize in constructor.</p>
     */
    private long id;

    /**
     * <p>Represents the name of the country. It is initialize in constructor.</p>
     */
    private String name;

    /**
     * <p>Constructs a new instance.</p>
     *
     * @param id the id of the country.
     * @param name the name of the country.
     *
     * @throws NullPointerException if the name argument is null.
     * @throws IllegalArgumentException if the name is an empty string.
     */
    public Country(long id, String name) {
        if (name == null) {
            throw new NullPointerException("name should not be null.");
        }
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("name should not be empty string.");
        }
        this.id = id;
        this.name = name;
    }

    /**
     * <p>Get the id of the country.</p>
     *
     * @return id the id of the country.
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>Get the name of the country.</p>
     *
     * @return name the name of the country.
     */
    public String getName() {
        return this.name;
    }

    /**
     * <p>Compares to the object.</p>
     *
     * @return true if and only if the ids of the two are the same.
     *
     * @param object the object to compare.
     */
    public boolean equals(Object object) {
        return object instanceof Country && this.id == ((Country) object).id;
    }

    /**
     * <p>get the hashCode of this instance.</p>
     *
     * @return the hash code of this instance.
     */
    public int hashCode() {
        return new Long(this.id).hashCode();
    }

}
