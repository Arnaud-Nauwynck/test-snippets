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
	public void test_6_x2_plus_3x_minus7() {
		// 6.x^2 + 3.x - 7
		Expr e = Exprs.minus(
				Exprs.plus(
							Exprs.mult(Exprs.of(6), Exprs.square(x)),
							Exprs.mult(Exprs.of(3), x)),
				Exprs.of(7)
				);
		// => 6.2.x + 3.1 - 0
		// => 12 x + 3
		Expr expectedDer = Exprs.plus(Exprs.mult(Exprs.of(12), x), Exprs.of(3));
		assertDeriv(expectedDer, e);

		// 6.x^2 + 3.x - 7
		// 6 enter x enter square * 3 enter x * + 7 - 
		// x deriv
		
	}

	
	
	
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

	@Test
	public void test_deriv_x_pow_0() {
		// pow(x, 0) = 1
		Expr e = Exprs.pow(x, Exprs.C_0);
		// => 0
		Expr expectedDer = Exprs.C_0;
		assertDeriv(expectedDer, e);
	}

	@Test
	public void test_deriv_x_pow_1() {
		// pow(x, 1) = x
		Expr e = Exprs.pow(x, Exprs.C_1);
		// => 1
		Expr expectedDer = Exprs.C_1;
		assertDeriv(expectedDer, e);
	}
	
	@Test
	public void test_deriv_x_pow_2() {
		// pow(x, 2)
		Expr e = Exprs.pow(x, Exprs.C_2);
		// => 2.pow(x, 1) = 2.x
		Expr expectedDer = Exprs.mult(Exprs.C_2, x);
		assertDeriv(expectedDer, e);
	}

	@Test
	public void test_deriv_x_pow_3() {
		// pow(x, 2)
		Expr e = Exprs.pow(x, Exprs.C_3);
		// => 3.pow(x, 3-1) = 3.x^2
		Expr expectedDer = Exprs.mult(Exprs.C_3, Exprs.pow(x, Exprs.C_2));
		assertDeriv(expectedDer, e);
	}

	@Test
	public void test_deriv_x_pow_minus1() {
		// pow(x, -1) = 1/x
		Expr e = Exprs.pow(x, Exprs.C_minus1);
		// => -1 * x^-2
		Expr expectedDer = Exprs.mult(Exprs.C_minus1, Exprs.pow(x, Exprs.C_minus2));
		assertDeriv(expectedDer, e);
	}
	
	@Test
	public void test_deriv_sinx_pow_3() {
		// pow(sin(x), 3) = sin(x)^3
		Expr e = Exprs.pow(Exprs.sin(x), Exprs.C_3);
		// => cos(x) * 3 * pow(sin(x), 3-1) = cos(x) * 3 * sin(x)^2
		Expr expectedDer = Exprs.mult(Exprs.cos(x), Exprs.mult(Exprs.C_3, Exprs.pow(Exprs.sin(x), Exprs.C_2)));
		assertDeriv(expectedDer, e);
	}
	
	@Test
	public void test_deriv_2_pow_sinx() {
		// pow(2, sin(x)) = 2^sin(x)  = e^(ln2 * sinx)
		Expr e = Exprs.pow(Exprs.C_2, Exprs.sin(x));
		// => cos(x) * ln2 * 2^sin(x)
		Expr expectedDer = Exprs.mult(Exprs.cos(x), Exprs.mult(Exprs.ln(Exprs.C_2), Exprs.pow( Exprs.C_2, Exprs.sin(x))));
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
