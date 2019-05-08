package fr.an.tests.testmockito.beans;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import fr.an.tests.testmockito.AbstractMockitoTest;
import fr.an.tests.testmockito.C;

public class ABeanTest extends AbstractMockitoTest {

	@InjectMocks 
	A a;
	
	@Spy @InjectMocks
	B b; 

	@Spy
	C c;

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
