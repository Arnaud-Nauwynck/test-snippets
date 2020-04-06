package fr.an.testsprintboottest.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class FooMockitoTest {
	
	private static final Logger log = LoggerFactory.getLogger(FooMockitoTest.class);

	@InjectMocks
	private Foo foo = new Foo();
	
	@Mock
	private Bar bar;
	
	@Test
	public void testFoo() {
		log.info("FooMockitoTest.testFoo");
		// given
		Mockito.when(bar.bar(Mockito.eq(1))).thenReturn(10);
		
		// when
		int res = foo.foo(1);

		// then
		Assert.assertEquals(10,  res);
		Mockito.verify(bar).bar(1);
		Mockito.verifyNoMoreInteractions(bar);
	}
}
