package fr.an.tests.testmockito.ctorbeans;

import org.junit.Assert;
import org.junit.Test;

import fr.an.tests.testmockito.AbstractMockitoTest;
import fr.an.tests.testmockito.C;


public class OK_ExplicitCtor_CtorATest extends AbstractMockitoTest {

	C c = spy(new C());
	CtorB b = spy(new CtorB(c));
	
	CtorA a = spy(new CtorA(b));
	
	
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
