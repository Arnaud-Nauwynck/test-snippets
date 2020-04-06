package fr.an.testsprintboottest.service;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.an.testsprintboottest.AbstractCtx1Test;

public class FooCtx1Test extends AbstractCtx1Test {
	
	private static final Logger log = LoggerFactory.getLogger(FooCtx1Test.class);
	
	@Autowired
	private Foo foo;
	
	@Test
	public void testFoo() {
		log.info("FooCtx1Test.testFoo");
		int res = foo.foo(1);
		Assert.assertEquals(2, res);
	}
	
	@Test
	public void testAppKey() {
		Assert.assertEquals("key-profile1", foo.confAppKey());
	}

}
