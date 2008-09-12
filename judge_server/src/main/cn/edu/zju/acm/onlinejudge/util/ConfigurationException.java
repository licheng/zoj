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

package cn.edu.zju.acm.onlinejudge.util;

public class ConfigurationException extends Exception {

    /**
     * <p>Creates a ConfigurationException instance with the error message.</p>
     *
     * @param message a descriptive message for this exception
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * <p>Creates a ConfigurationException instance with the error message and cause.</p>
     *
     * @param message a descriptive message for this exception
     * @param cause the cause of this exception
     */
    public ConfigurationException(String message, Exception cause) {
        super(message, cause);
    }
}
