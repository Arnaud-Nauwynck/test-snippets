package fr.an.tests.testmockito.annotedctorbeans;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import fr.an.tests.testmockito.AbstractMockitoTest;
import fr.an.tests.testmockito.C;


public class NPE_NoAnnotation_AnnotatedCtorATest extends AbstractMockitoTest {
	
	@InjectMocks
	AnnotedCtorA a; // ERROR a.b == null
	
	AnnotedCtorB b; // explicitely created in setup(), but not injected into A.b
	
	@Spy
	C c;
	
	@Before
	public void setup() {
		b = spy(new AnnotedCtorB(c));
	}
	
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
			Assert.assertEquals(AnnotedCtorA.class.getName(), stack[0].getClassName());
			Assert.assertEquals("foo", stack[0].getMethodName());
			Assert.assertEquals(15, stack[0].getLineNumber());
		}
	}
}
