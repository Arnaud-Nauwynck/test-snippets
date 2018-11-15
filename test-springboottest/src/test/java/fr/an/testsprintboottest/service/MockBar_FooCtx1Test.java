package fr.an.testsprintboottest.service;

import static org.mockito.BDDMockito.willReturn;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import fr.an.testsprintboottest.AbstractCtx1Test;

public class MockBar_FooCtx1Test extends AbstractCtx1Test {
	
	private static final Logger log = LoggerFactory.getLogger(MockBar_FooCtx1Test.class);
	
	@Autowired
	private Foo foo;
	
	@MockBean
	private Bar bar;
	
	@Test
	public void testFoo() {
		log.info("MockBar_FooCtx1Test.testFoo");
		// given
		willReturn(10).given(bar).bar(1);
		// Mockito.when(bar.bar(1)).thenReturn(10);

		// when
		int res = foo.foo(1);

		// then
		Assert.assertEquals(10, res);
	}
	
	@Test
	public void testAppKey() {
		Assert.assertEquals("key-profile1", foo.confAppKey());
	}

}
