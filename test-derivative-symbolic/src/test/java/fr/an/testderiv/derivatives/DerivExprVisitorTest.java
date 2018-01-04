package fr.an.testderiv.derivatives;

import org.junit.Test;

import fr.an.testderiv.alg.ExprAsserts;
import fr.an.testderiv.model.Expr;
import fr.an.testderiv.util.DumpExprVisitor;
import fr.an.testderiv.util.Exprs;

public class DerivExprVisitorTest {

	static final DerivExprVisitor deriveByX = new DerivExprVisitor("x");
	static final Expr x = Exprs.var("x");
	static final Expr y = Exprs.var("y");
	
	private static boolean DEBUG = false;
	
	@Test
	public void test_2_x() {
		// 2x
		Expr e = Exprs.mult(Exprs.C_2, x);
		// => 2
		Expr expectedDer = Exprs.C_2;
		assertDeriv(expectedDer, e);
	}

	@Test
	public void test_3_x_square() {
		// 3x^2
		Expr e = Exprs.mult(Exprs.of(3), Exprs.mult(x, x));
		// => 3*(x+x)  =  6x  ... similar terms not factorised!
		Expr expectedDer = Exprs.mult(Exprs.of(3), Exprs.plus(x, x));
		assertDeriv(expectedDer, e);
	}
	
	@Test
	public void test_y() {
		// y
		Expr e = y;
		// => 0
		Expr expectedDer = Exprs.C_0;
		assertDeriv(expectedDer, e);
	}
	
	@Test
	public void test_x_mult_y() {
		// x.y
		Expr e = Exprs.mult(x, y);
		// => y
		Expr expectedDer = y;
		assertDeriv(expectedDer, e);
	}
	
	@Test
	public void test_sin_open_2_mult_x_close() {
		// sin(x*x)
		Expr e = Exprs.func("sin", Exprs.mult(Exprs.C_2, x));
		// => (2) * cos(2*x)
		Expr expectedDer = Exprs.mult(Exprs.C_2, Exprs.func("cos", Exprs.mult(Exprs.C_2, x)));
		assertDeriv(expectedDer, e);
	}

	@Test
	public void test_sin_open_x_mult_x_close() {
		// sin(x*x)
		Expr e = Exprs.func("sin", Exprs.mult(x, x));
		// => (2*x) * cos(x*x)  =  (x+x) * cos(x*x)  .. similar terms not factorised!
		// Expr expectedDer = Exprs.mult(Exprs.mult(Exprs.C_2, x), Exprs.func("cos", Exprs.mult(x, x)));
		Expr expectedDer = Exprs.mult(Exprs.plus(x, x), Exprs.func("cos", Exprs.mult(x, x)));
		assertDeriv(expectedDer, e);
	}

	
	@Test
	public void test_derive_sqrt_open_1_plus_2_mult_x_close() {
		// sqrt(1 + 2*x)
		Expr e = Exprs.func("sqrt", Exprs.plus(Exprs.C_1, Exprs.mult(Exprs.C_2, x)));
		// => .. ((0.0)+(((0.0)*(x))+((2.0)*(1.0))))*((0.5)/(sqrt((1.0)+((2.0)*(x)))))
		// => (1.0)/(sqrt((1.0)+((2.0)*(x))))
		Expr expectedDer = Exprs.div(Exprs.C_1, Exprs.func("sqrt", Exprs.plus(Exprs.C_1, Exprs.mult(Exprs.C_2, x))));
		assertDeriv(expectedDer, e);
	}

	private void assertDeriv(Expr expected, Expr e) {
		if (DEBUG) System.out.println(DumpExprVisitor.dumpToString(e));
		
		Expr derE = deriveByX.eval(e);
		if (DEBUG) System.out.println(DumpExprVisitor.dumpToString(derE));
		
		Expr simplifyDerE = Exprs.simplify(derE);
		if (DEBUG) System.out.println(DumpExprVisitor.dumpToString(simplifyDerE));
		
		ExprAsserts.assertEquals(expected, simplifyDerE);
	}
	
}
