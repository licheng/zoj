/*
 * Copyright (C) 2001 - 2005 ZJU Online Judge, All Rights Reserved.
 */
package cn.edu.zju.acm.onlinejudge.persistence.sql;

import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.edu.zju.acm.onlinejudge.bean.Configuration;

/**
 * <p>Tests for AuthorizationPersistenceImpl.</p>
 *
 * @version 2.0
 * @author ZOJDEV
 */
public class ConfigurationPersistenceImplTest extends TestCase {
	
			
	/**
	 * Setup.
	 * @throws Exception to JUnit
	 */
	protected void setUp() throws Exception {
		DatabaseHelper.resetAllTables(false);
		
		List configurations = new ArrayList();
		Configuration configuration1 = new Configuration();
		configuration1.setName("n1");
		configuration1.setValue("v1");
		configuration1.setDescription("d1");
		Configuration configuration2 = new Configuration();
		configuration2.setName("n2");
		configuration2.setValue("v2");
		configuration2.setDescription("d2");
		Configuration configuration3 = new Configuration();
		configuration3.setName("n3");
		configuration3.setValue("v3");
		configuration3.setDescription("d3");
		configurations.add(configuration1); 
		configurations.add(configuration2);
		configurations.add(configuration3);
		
		new ConfigurationPersistenceImpl().setConfigurations(configurations, 1);	

	}
	
	/**
	 * Tear down.
	 * @throws Exception to JUnit
	 */
	protected void tearDown() throws Exception {
		DatabaseHelper.clearTable("configuration");
		
	}
	
	/**
	 * Tests getConfigurations method
	 * @throws Exception to JUnit
	 */
	public void testGetConfigurations() throws Exception {

		ConfigurationPersistenceImpl persistence = new ConfigurationPersistenceImpl();
		
		List configurations = persistence.getConfigurations();
		assertEquals("size is wrong", 3, configurations.size());
		
		
		Set nameSet = new HashSet(Arrays.asList(new String[] {"n1", "n2", "n3"}));
		Set valueSet = new HashSet(Arrays.asList(new String[] {"v1", "v2", "v3"}));
		Set descSet = new HashSet(Arrays.asList(new String[] {"d1", "d2", "d3"}));
		for (Iterator it = configurations.iterator(); it.hasNext();) {
			Configuration configuration = (Configuration) it.next();
			assertTrue("wrong name", nameSet.contains(configuration.getName()));
			assertTrue("wrong value", valueSet.contains(configuration.getValue()));
			assertTrue("wrong description", descSet.contains(configuration.getDescription()));
			nameSet.remove(configuration.getName());
			valueSet.remove(configuration.getValue());
			descSet.remove(configuration.getDescription());
			
		}
	}

	
	/**
	 * Tests setConfigurations method
	 * @throws Exception to JUnit
	 */
	public void testSetConfigurations1() throws Exception {
		List configurations = new ArrayList();
		Configuration configuration4 = new Configuration();
		configuration4.setName("n4");
		configuration4.setValue("v4");
		configuration4.setDescription("d4");
		Configuration configuration5 = new Configuration();
		configuration5.setName("n5");
		configuration5.setValue("v5");
		configuration5.setDescription("d5");
		
		configurations.add(configuration4); 
		configurations.add(configuration5);
		ConfigurationPersistenceImpl persistence = new ConfigurationPersistenceImpl();
		
		persistence.setConfigurations(configurations, 1);
		
		configurations = persistence.getConfigurations();
		assertEquals("size is wrong", 5, configurations.size());
		
		
		Set nameSet = new HashSet(Arrays.asList(new String[] {"n1", "n2", "n3", "n4", "n5"}));
		Set valueSet = new HashSet(Arrays.asList(new String[] {"v1", "v2", "v3", "v4", "v5"}));
		Set descSet = new HashSet(Arrays.asList(new String[] {"d1", "d2", "d3", "d4", "d5"}));
		for (Iterator it = configurations.iterator(); it.hasNext();) {
			Configuration configuration = (Configuration) it.next();
			assertTrue("wrong name", nameSet.contains(configuration.getName()));
			assertTrue("wrong value", valueSet.contains(configuration.getValue()));
			assertTrue("wrong description", descSet.contains(configuration.getDescription()));
			nameSet.remove(configuration.getName());
			valueSet.remove(configuration.getValue());
			descSet.remove(configuration.getDescription());
			
		}
	}

	/**
	 * Tests setConfigurations method
	 * @throws Exception to JUnit
	 */
	public void testSetConfigurations2() throws Exception {
		List configurations = new ArrayList();
		Configuration configuration1 = new Configuration();
		configuration1.setName("n1");
		configuration1.setValue("nv1");
		configuration1.setDescription("nd1");
		Configuration configuration4 = new Configuration();
		configuration4.setName("n4");
		configuration4.setValue("v4");
		configuration4.setDescription("d4");
		
		configurations.add(configuration1); 
		configurations.add(configuration4);
		
		ConfigurationPersistenceImpl persistence = new ConfigurationPersistenceImpl();
		
		persistence.setConfigurations(configurations, 1);
		
		configurations = persistence.getConfigurations();
		assertEquals("size is wrong", 4, configurations.size());
		
		
		Set nameSet = new HashSet(Arrays.asList(new String[] {"n1", "n2", "n3", "n4"}));
		Set valueSet = new HashSet(Arrays.asList(new String[] {"nv1", "v2", "v3", "v4"}));
		Set descSet = new HashSet(Arrays.asList(new String[] {"nd1", "d2", "d3", "d4"}));
		for (Iterator it = configurations.iterator(); it.hasNext();) {
			Configuration configuration = (Configuration) it.next();
			assertTrue("wrong name", nameSet.contains(configuration.getName()));
			assertTrue("wrong value", valueSet.contains(configuration.getValue()));
			assertTrue("wrong description", descSet.contains(configuration.getDescription()));
			nameSet.remove(configuration.getName());
			valueSet.remove(configuration.getValue());
			descSet.remove(configuration.getDescription());
			
		}
	}
}

