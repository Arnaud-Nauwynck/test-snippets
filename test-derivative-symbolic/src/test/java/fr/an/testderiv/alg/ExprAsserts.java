package fr.an.testderiv.alg;

import org.junit.Assert;

import fr.an.testderiv.model.Expr;
import fr.an.testderiv.util.DumpExprVisitor;

public class ExprAsserts {
	
	public static void assertEquals(Expr expected, Expr actual) {
		// TOCHANGE : could use recursive visitor to traverse at same time expected and actual ast
		String expectedText = DumpExprVisitor.dumpToString(expected);
		String actualText = DumpExprVisitor.dumpToString(actual);
		Assert.assertEquals(expectedText, actualText);
	}
	
}
