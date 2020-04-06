package fr.an.tests.testmockito.annotedctorbeans;

import org.junit.Assert;
import org.junit.Test;

import fr.an.tests.testmockito.AbstractMockitoTest;
import fr.an.tests.testmockito.C;


public class OK_Explicit_AnnotatedCtorATest extends AbstractMockitoTest {

	C c = spy(new C());
	AnnotedCtorB b = spy(new AnnotedCtorB(c));
	
	AnnotedCtorA a = spy(new AnnotedCtorA(b));
	
	
	@Test
	public void testA() {
		String res = a.foo("x");
		Assert.assertEquals("A.foo(B.foo(C.foo(x)))", res);
	}

	@Test
	public void testA_whenC() {
		
		String res = a.foo("x");
		Assert.assertEquals("A.foo(B.foo(C.foo(x)))", res);
	}

}
