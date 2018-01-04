package fr.an.testderiv.util;

import com.google.common.collect.ImmutableList;

import fr.an.testderiv.alg.SimplifyConstExprVisitor;
import fr.an.testderiv.alg.SimplifyNumAssocExprVisitor;
import fr.an.testderiv.model.Expr;
import fr.an.testderiv.model.Expr.BinaryOpExpr;
import fr.an.testderiv.model.Expr.FuncExpr;
import fr.an.testderiv.model.Expr.NumberExpr;
import fr.an.testderiv.model.Expr.UnaryOpExpr;
import fr.an.testderiv.model.Expr.VarExpr;

public final class Exprs {

	private Exprs() {}
	
	public static final NumberExpr C_0 = new NumberExpr(0.0);
	public static final NumberExpr C_0_5 = new NumberExpr(0.5);
	public static final NumberExpr C_1 = new NumberExpr(1.0);
	public static final NumberExpr C_2 = new NumberExpr(2.0);
	public static final NumberExpr C_3 = new NumberExpr(3.0);

	public static final NumberExpr C_minus1 = new NumberExpr(-1.0);
	public static final NumberExpr C_minus2 = new NumberExpr(-2.0);

	
	public static boolean is0(Expr expr) {
		if (expr == Exprs.C_0) {
			return true;
		}
		return expr instanceof NumberExpr && ((NumberExpr) expr).getValue() == 0.0; 
	}

	public static boolean is1(Expr expr) {
		if (expr == Exprs.C_1) {
			return true;
		}
		return expr instanceof NumberExpr && ((NumberExpr) expr).getValue() == 1.0; 
	}

	public static NumberExpr of(double val) {
		return new NumberExpr(val);
	}

	public static VarExpr var(String name) {
		return new VarExpr(name);
	}

	public static UnaryOpExpr unaryOp(String op, Expr e) {
		return new UnaryOpExpr(op, e);
	}

	public static UnaryOpExpr unaryMinus(Expr e) {
		return unaryOp("-", e);
	}

	public static BinaryOpExpr binaryOp(Expr lhs, String op, Expr rhs) {
		return new BinaryOpExpr(lhs, op, rhs);
	}
	public static BinaryOpExpr binaryOp(double lhsVal, String op, Expr rhs) {
		return binaryOp(of(lhsVal), op, rhs);
	}

	public static BinaryOpExpr plus(Expr lhs, Expr rhs) {
		return binaryOp(lhs, "+", rhs);
	}
	public static BinaryOpExpr minus(Expr lhs, Expr rhs) {
		return binaryOp(lhs, "-", rhs);
	}
	public static BinaryOpExpr mult(Expr lhs, Expr rhs) {
		return binaryOp(lhs, "*", rhs);
	}
	public static BinaryOpExpr div(Expr lhs, Expr rhs) {
		return binaryOp(lhs, "/", rhs);
	}
	public static BinaryOpExpr inv(Expr e) {
		return div(C_1, e);
	}

	public static FuncExpr func(String funcName, ImmutableList<Expr> args) {
		return new FuncExpr(funcName, args);
	}

	public static FuncExpr func(String funcName, Expr arg0) {
		return func(funcName, ImmutableList.of(arg0));
	}
	public static FuncExpr func(String funcName, Expr arg0, Expr arg1) {
		return func(funcName, ImmutableList.of(arg0, arg1));
	}
	public static FuncExpr func(String funcName, Expr arg0, Expr arg1, Expr arg2) {
		return func(funcName, ImmutableList.of(arg0, arg1, arg2));
	}
	public static FuncExpr func(String funcName, Expr arg0, Expr arg1, Expr arg2, Expr arg3) {
		return func(funcName, ImmutableList.of(arg0, arg1, arg2, arg3));
	}
	
	public static FuncExpr pow(Expr e, Expr exponent) {
		return func("pow", e, exponent);
	}
	
	public static FuncExpr square(Expr e) {
		return pow(e, C_2);
	}

	public static FuncExpr ln(Expr e) {
		return func("ln", e);
	}
	public static FuncExpr exp(Expr e) {
		return func("exp", e);
	}
	public static FuncExpr sin(Expr e) {
		return func("sin", e);
	}
	public static FuncExpr cos(Expr e) {
		return func("cos", e);
	}

	
	public static Expr simplifyConst(Expr e) {
		return SimplifyConstExprVisitor.simplify(e);
	}
	
	public static Expr simplify(Expr e) {
		Expr res = SimplifyConstExprVisitor.simplify(e);
		res = SimplifyNumAssocExprVisitor.simplify(res);
		return res;
	}

	public static String dumpToString(Expr e) {
		return DumpExprVisitor.dumpToString(e);
	}

}
