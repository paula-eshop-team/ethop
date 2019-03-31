package com.eshop.test;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Created by Paula
 */
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class TestBase extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	public void test() {
		System.out.println("11");
	}

}
