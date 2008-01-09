/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence;

/**
 * <p>The PersistenceException exception is used to wrap any persistence implementation specific exception.
 * This exception is thrown by the those persistence interface implementations.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class PersistenceException extends Exception {

    /**
     * <p>Creates a PersistenceException instance with the error message.</p>
     *
     * @param message a descriptive message for this exception
     */
    public PersistenceException(String message) {
        super(message);
    }

    /**
     * <p>Creates a PersistenceException instance with the error message and cuase.</p>
     *
     * @param message a descriptive message for this exception
     * @param cause the cause of this exception
     */
    public PersistenceException(String message, Exception cause) {
        super(message, cause);
    }
}
