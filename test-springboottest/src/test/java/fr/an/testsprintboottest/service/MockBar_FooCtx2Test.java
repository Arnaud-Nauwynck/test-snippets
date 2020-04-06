package fr.an.testsprintboottest.service;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import fr.an.testsprintboottest.AbstractCtx2Test;

public class MockBar_FooCtx2Test extends AbstractCtx2Test {
	
	private static final Logger log = LoggerFactory.getLogger(MockBar_FooCtx2Test.class);

	@Autowired
	private Foo foo;
	
	@MockBean 
	private Bar bar;
	
	@Test
	public void testFoo() {
		log.info("MockBar_FooCtx2Test.testFoo");
		int res = foo.foo(1);
		Assert.assertEquals(0, res);
	}

	@Test
	public void testAppKey() {
		Assert.assertEquals("key-profile2", foo.confAppKey());
	}

}
