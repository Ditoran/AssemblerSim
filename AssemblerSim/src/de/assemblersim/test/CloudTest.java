package de.assemblersim.test;

import static org.junit.Assert.*;

import java.awt.SplashScreen;

import org.junit.BeforeClass;
import org.junit.Test;

import de.assemblersim.application.Cloud;

public class CloudTest {
	
	static Cloud cloud;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cloud = Cloud.getInstance();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(cloud);
	}

	@Test
	public void testLogin() {
		assertTrue(cloud.login("Admin", "test123"));
	}

	@Test
	public void testIsLoggedIn() {
		assertTrue(cloud.isLoggedIn());
	}

}
