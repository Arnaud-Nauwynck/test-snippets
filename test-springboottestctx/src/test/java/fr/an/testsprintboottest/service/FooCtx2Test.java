package fr.an.testsprintboottest.service;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.an.testsprintboottest.AbstractCtx2Test;

public class FooCtx2Test extends AbstractCtx2Test {
	
	private static final Logger log = LoggerFactory.getLogger(FooCtx2Test.class);

	@Autowired
	private Foo foo;
	
	@Test
	public void testFoo() {
		log.info("FooCtx2Test.testFoo");
		int res = foo.foo(1);
		Assert.assertEquals(2, res);
	}

	@Test
	public void testAppKey() {
		Assert.assertEquals("key-profile2", foo.confAppKey());
	}

}
