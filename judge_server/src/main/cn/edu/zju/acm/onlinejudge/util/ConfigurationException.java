/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
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
