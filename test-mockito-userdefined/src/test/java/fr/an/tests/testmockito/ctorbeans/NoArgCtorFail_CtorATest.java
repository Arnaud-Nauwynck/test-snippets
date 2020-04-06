package fr.an.tests.testmockito.ctorbeans;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;

import fr.an.tests.testmockito.C;
import fr.an.tests.testmockito.beans.A;
import fr.an.tests.testmockito.beans.B;


public class NoArgCtorFail_CtorATest { // extends AbstractMockitoTest {
	
	@InjectMocks 
	CtorA a;
	
	@Spy
	CtorB b; // ERROR b==null, fail to be instanciated !!!

	@Spy
	C c;
	
	@Before
	public void setup() {
		try {
			MockitoAnnotations.initMocks(this);
			Assert.fail();
		} catch(MockitoException ex) {
//			org.mockito.exceptions.base.MockitoException: Unable to initialize @Spy annotated field 'b'.
//			Please ensure that the type 'CtorB' has a no-arg constructor.
//				at fr.an.tests.testmockito.ctorbeans.NoArgCtorFail_CtorATest.setup(NoArgCtorFail_CtorATest.java:30)
			Assert.assertEquals("Unable to initialize @Spy annotated field 'b'.\n" + 
					"Please ensure that the type 'CtorB' has a no-arg constructor.", ex.getMessage());
			// fail !!
		}
	}

	@Test
	public void testA() {
		Assert.assertNull(a);
		Assert.assertNull(b);
		Assert.assertNull(c);
	}
}
