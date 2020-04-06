package fr.an.tests.testmockito.beans;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import fr.an.tests.testmockito.AbstractMockitoTest;
import fr.an.tests.testmockito.C;

public class NotInjectedABeanTest extends AbstractMockitoTest {

	@InjectMocks 
	A a;
	
	@Spy
	B b; // ok, instanciated + injected into A.B

	@Spy
	C c; // ERROR.. instanciated, BUT not injected into B.c !!

	@Test
	public void testA() {
		Assert.assertNotNull(a);
		Assert.assertNotNull(b);
		Assert.assertNotNull(c);
		
		try {
			a.foo("x");
		
			Assert.fail();
		} catch(NullPointerException ex) {
			// FAIL inside a.foo() : field c NOT injected !!!!!!!
//			java.lang.NullPointerException
//			at fr.an.tests.testmockito.beans.BBean.foo(BBean.java:13)
//			at fr.an.tests.testmockito.beans.ABean.foo(ABean.java:11)
//			at fr.an.tests.testmockito.beans.NotInjectedABeanTest.testA(NotInjectedABeanTest.java:30)

			StackTraceElement[] stack = ex.getStackTrace();
			Assert.assertEquals(B.class.getName(), stack[0].getClassName());
			Assert.assertEquals("foo", stack[0].getMethodName());
			Assert.assertEquals(13, stack[0].getLineNumber());
		}
	}

}
