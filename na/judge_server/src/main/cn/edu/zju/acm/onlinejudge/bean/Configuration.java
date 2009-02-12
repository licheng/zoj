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

package cn.edu.zju.acm.onlinejudge.bean;

/**
 * <p>
 * Configuration bean.
 * </p>
 * 
 * @author Zhang, Zheng
 * 
 * @version 2.0
 */
public class Configuration {

    /**
     * <p>
     * Represents name.
     * </p>
     */
    private String name = null;

    /**
     * <p>
     * Represents value.
     * </p>
     */
    private String value = null;

    /**
     * <p>
     * Represents description.
     * </p>
     */
    private String description = null;

    /**
     * <p>
     * Empty constructor.
     * </p>
     */
    public Configuration() {}

    /**
     * <p>
     * Gets name.
     * </p>
     * 
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * <p>
     * Sets name.
     * </p>
     * 
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     * Gets value.
     * </p>
     * 
     * @return value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * <p>
     * Sets value.
     * </p>
     * 
     * @param value
     *            value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * <p>
     * Gets description.
     * </p>
     * 
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * <p>
     * Sets description.
     * </p>
     * 
     * @param description
     *            description
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
