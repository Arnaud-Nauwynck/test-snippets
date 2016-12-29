package fr.an.tests.detectobjectcycle;

import org.junit.Test;

public class TypeCheckObjectGraphVisitorTest {

	@Test
	public void testVisit() {
		TypeCheckObjectGraphVisitor typeCheckVisitor = new TypeCheckObjectGraphVisitor();
    	
    	typeCheckVisitor.visitRoot(Foo1.class);
	}
}
