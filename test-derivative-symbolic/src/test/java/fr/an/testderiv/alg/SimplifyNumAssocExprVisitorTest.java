package fr.an.testderiv.alg;

import org.junit.Test;

import fr.an.testderiv.model.Expr;
import fr.an.testderiv.util.Exprs;

public class SimplifyNumAssocExprVisitorTest {

	SimplifyNumAssocExprVisitor sut = new SimplifyNumAssocExprVisitor();
	static final Expr x = Exprs.var("x");
	
	protected static void assertSimplify(Expr expected, Expr actual) {
		Expr actual2 = SimplifyConstExprVisitor.simplify(actual); // !!!
		Expr res = SimplifyNumAssocExprVisitor.simplify(actual2);
		ExprAsserts.assertEquals(expected, res);
	}
	
	@Test
	public void test_2_mult_open_4_mult_x_close() {
		// 2 * (4 * x) => 8 * x
		Expr e = Exprs.mult(Exprs.of(2), Exprs.mult(Exprs.of(4), x));
		Expr expected = Exprs.mult(Exprs.of(8), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_2_mult_open_x_mult_4_close() {
		// 2 * (x * 4) => 8 * x
		Expr e = Exprs.mult(Exprs.of(2), Exprs.mult(x, Exprs.of(4)));
		Expr expected = Exprs.mult(Exprs.of(8), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_2_mult_open_x_div_4_close() {
		// 2 * (x / 4) => 0.5 * x
		Expr e = Exprs.mult(Exprs.of(2), Exprs.div(x, Exprs.of(4)));
		Expr expected = Exprs.mult(Exprs.of(0.5), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_2_mult_open_4_div_x_close() {
		// 2 * (4 / x) => 8 / x
		Expr e = Exprs.mult(Exprs.of(2), Exprs.div(Exprs.of(4), x));
		Expr expected = Exprs.div(Exprs.of(8), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_2_div_open_x_div_4_close() {
		// 2 / (x / 4) => 8 / x
		Expr e = Exprs.div(Exprs.of(2), Exprs.div(x, Exprs.of(4)));
		Expr expected = Exprs.div(Exprs.of(8), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_2_div_open_4_div_x_close() {
		// 2 / (4 / x) => 0.5 * x
		Expr e = Exprs.div(Exprs.of(2), Exprs.div(Exprs.of(4), x));
		Expr expected = Exprs.mult(Exprs.of(0.5), x);
		assertSimplify(expected, e);
	}

	// -----------------
	
	@Test
	public void test_open_2_mult_4_close_mult_x() {
		// (2 * 4) * x => 8 * x
		Expr e = Exprs.mult(Exprs.mult(Exprs.of(2), Exprs.of(4)), x);
		Expr expected = Exprs.mult(Exprs.of(8), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_open_2_mult_x_close_mult_4() {
		// (2 * x) * 4 => 8 * x
		Expr e = Exprs.mult(Exprs.mult(Exprs.of(2), x), Exprs.of(4));
		Expr expected = Exprs.mult(Exprs.of(8), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_open_2_mult_x_close_div_4() {
		// (2 * x) / 4) => 0.5 * x
		Expr e = Exprs.div(Exprs.mult(Exprs.of(2), x), Exprs.of(4));
		Expr expected = Exprs.mult(Exprs.of(0.5), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_open_2_mult_4_close_div_x() {
		// (2 * 4) / x => 8 / x
		Expr e = Exprs.div(Exprs.mult(Exprs.of(2), Exprs.of(4)), x);
		Expr expected = Exprs.div(Exprs.of(8), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_open_2_div_x_close_div_4() {
		// (2 / x) / 4 => 0.5 / x
		Expr e = Exprs.div(Exprs.div(Exprs.of(2), x), Exprs.of(4));
		Expr expected = Exprs.div(Exprs.of(0.5), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_open_2_div_4_close_div_x_close() {
		// (2 / 4) / x => 0.5 / x
		Expr e = Exprs.div(Exprs.div(Exprs.of(2), Exprs.of(4)), x);
		Expr expected = Exprs.div(Exprs.of(0.5), x);
		assertSimplify(expected, e);
	}

	
	
	@Test
	public void test_2_plus_open_4_plus_x_close() {
		// 2 + (4 + x) => 6 + x
		Expr e = Exprs.plus(Exprs.of(2), Exprs.plus(Exprs.of(4), x));
		Expr expected = Exprs.plus(Exprs.of(6), x);
		assertSimplify(expected, e);
	}

	@Test
	public void test_2_minus_open_4_minus_x_close() {
		// 2 - (4 - x) => -2 + x
		Expr e = Exprs.minus(Exprs.of(2), Exprs.minus(Exprs.of(4), x));
		Expr expected = Exprs.plus(Exprs.of(-2), x);
		assertSimplify(expected, e);
	}

}
