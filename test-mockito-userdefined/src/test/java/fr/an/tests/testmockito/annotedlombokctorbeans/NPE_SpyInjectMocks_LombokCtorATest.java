package fr.an.tests.testmockito.annotedlombokctorbeans;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import fr.an.tests.testmockito.AbstractMockitoTest;
import fr.an.tests.testmockito.C;
import fr.an.tests.testmockito.annotedlombokctorbeans.AnnotedLombokCtorA;
import fr.an.tests.testmockito.annotedlombokctorbeans.AnnotedLombokCtorB;

public class NPE_SpyInjectMocks_LombokCtorATest extends AbstractMockitoTest {

	@InjectMocks 
	AnnotedLombokCtorA a; // ERROR.. a.b == null
	
	@Spy @InjectMocks
	AnnotedLombokCtorB b; // b instanciated, but not injected into a !!!

	@Spy
	C c;

	@Test
	public void testA() {
		Assert.assertNotNull(a);
		Assert.assertNotNull(b);
		Assert.assertNotNull(c);

		try {
			a.foo("x");
			
			Assert.fail();
		} catch(NullPointerException ex) {
			// FAIL inside a.foo() : field b NOT injected !!!!!!!
			StackTraceElement[] stack = ex.getStackTrace();
			Assert.assertEquals(AnnotedLombokCtorA.class.getName(), stack[0].getClassName());
			Assert.assertEquals("foo", stack[0].getMethodName());
			Assert.assertEquals(14, stack[0].getLineNumber());
		}
	}

}
