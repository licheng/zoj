/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * <p>This test case aggregates all test cases for persistence layer.</p>
 *
 * @author  oldbig
 * @version 1.0
 */
public class PersistenceTests extends TestCase {
    /**
     * <p>Gets the functional test suite for persistence layer.</p>
     *
     * @return a <code>TestSuite</code> providing the tests for persistence layer.
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();

        suite.addTestSuite(UserPersistenceImplTest.class);
        suite.addTestSuite(ConfigurationPersistenceImplTest.class);
        suite.addTestSuite(ForumPersistenceImplTest.class);

        return suite;
    }
}
