/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence;

/**
 * <p>The PersistenceCreationException exception is used to wrap any exception thrown while creating the persistence
 * classes. This exception is thrown by PersistenceLocator class.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class PersistenceCreationException extends PersistenceException {

    /**
     * <p>Creates a PersistenceCreationException instance with the error message.</p>
     *
     * @param message a descriptive message for this exception
     */
    public PersistenceCreationException(String message) {
        super(message);
    }

    /**
     * <p>Creates a PersistenceCreationException instance with the error message and cuase.</p>
     *
     * @param message a descriptive message for this exception
     * @param cause the cause of this exception
     */
    public PersistenceCreationException(String message, Exception cause) {
        super(message, cause);
    }
}
