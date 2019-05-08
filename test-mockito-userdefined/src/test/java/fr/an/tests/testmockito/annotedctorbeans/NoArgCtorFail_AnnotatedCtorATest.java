package fr.an.tests.testmockito.annotedctorbeans;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;

import fr.an.tests.testmockito.C;


public class NoArgCtorFail_AnnotatedCtorATest { // extends AbstractMockitoTest {
	
	@InjectMocks 
	AnnotedCtorA a;
	
	@Spy
	AnnotedCtorB b; // ERROR b==null, fail to be instanciated !!!

	@Spy
	C c;
	
	@Before
	public void setup() {
		try {
			MockitoAnnotations.initMocks(this);
			Assert.fail();
		} catch(MockitoException ex) {
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
