/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.bean.enumeration;

/**
 * <p>This class represents the Programming Language. </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class Language {

    /**
     * <p>Represents id of the Language.</p>
     */
    private long id;

    /**
     * <p>Represents name of the Language.</p>
     */
    private String name;

    /**
     * <p>Represents the description of the Language.</p>
     */
    private String description;

    /**
     * <p>Represents compiler of the Language.</p>
     */
    private String compiler;

    /**
     * <p>Represents options of the Language.</p>
     */
    private String options;

    /**
     * <p>Create a new instance.</p>
     *
     * @param id the id of the Language.
     * @param name the name of the Language.
     * @param description the description of the Language.
     * @param compiler the compiler of the Language.
     * @param options the options of the Language.
     *
     * @throws NullPointerException if any argument is null.
     * @throws IllegalArgumentException if the name is an empty string.
     */
    public Language(long id, String name, String description, String compiler, String options) {
        if (name == null) {
            throw new NullPointerException("name should not be null.");
        }
        if (description == null) {
            throw new NullPointerException("description should not be null.");
        }
        if (compiler == null) {
            throw new NullPointerException("compiler should not be null.");
        }
        if (options == null) {
            throw new NullPointerException("options should not be null.");
        }
        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("name should not be empty string.");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.compiler = compiler;
        this.options  = options;
    }

    /**
     * <p>Get the id of the Language.</p>
     *
     * @return the id of the Language.
     */
    public long getId() {
        return this.id;
    }

    /**
     * <p>Get the name of the Language.</p>
     *
     * @return the name of the Language.
     */
    public String getName() {
        return this.name;
    }

    /**
     * <p>Get the description of the Language.</p>
     *
     * @return the description of the Language.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * <p>Get the compiler of the Language.</p>
     *
     * @return the compiler of the Language.
     */
    public String getCompiler() {
        return this.compiler;
    }

    /**
     * <p>Get the options of the Language.</p>
     *
     * @return the options of the Language.
     */
    public String getOptions() {
        return this.options;
    }

    /**
     * <p>Compares to the object.</p>
     *
     * @return true if and only if the ids of the two are the same.
     *
     * @param object the object to compare.
     */
    public boolean equals(Object object) {
        return object instanceof Language && this.id == ((Language) object).id;
    }

    /**
     * <p>get the hashCode of this instance.</p>
     *
     * @return the hash code of this instance.
     */
    public int hashCode() {
        return new Long(this.id).hashCode();
    }
    
    /**
     * <p>Get the name of the Language.</p>
     *
     * @return the name of the Language.
     */
    public String toString() {
        return this.name;
    }

}
