/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.security;


/**
 * <p>This class represents the RoleSecurity. </p>
 *
 * @author ZOJDEV
 * @version 2.0
 */
public class RoleSecurity extends AbstractSecurity {
    /**
     * <p>Represents role name.</p>
     */
    private final String name;
    
    /**
     * <p>Represents role description.</p>
     */
    private final String description;

    /**
     * <p>Constructor with id and name.</p>
     * @param id the id
     * @param name the name
     * @param description the description
     * @throws NullPointerException if name or description is null
     * @throws IllegalArgumentException if name is empty;
     */
    public RoleSecurity(long id, String name, String description) {    	
        super(id);
        if (name == null) {
        	throw new NullPointerException("name is null.");
        }
        if (name.trim().length() == 0) {
        	throw new IllegalArgumentException("name is empty.");
        }
        if (description == null) {
            throw new NullPointerException("description is null.");
        }
        this.description = description;
        this.name = name;
    }

    /**
     * <p>Gets the name.</p>
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Gets the description.</p>
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
