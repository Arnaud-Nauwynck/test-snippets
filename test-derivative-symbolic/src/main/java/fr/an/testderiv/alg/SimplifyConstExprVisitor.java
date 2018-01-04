package fr.an.testderiv.alg;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import fr.an.testderiv.model.Expr;
import fr.an.testderiv.model.Expr.BinaryOpExpr;
import fr.an.testderiv.model.Expr.FuncExpr;
import fr.an.testderiv.model.Expr.NumberExpr;
import fr.an.testderiv.model.Expr.UnaryOpExpr;
import fr.an.testderiv.model.Expr.VarExpr;
import fr.an.testderiv.model.ExprVisitor;
import fr.an.testderiv.util.Exprs;

/**
 * a simple Expr evaluator to return simplification for basic terms with 0, 1, and constants
 */
public class SimplifyConstExprVisitor extends ExprVisitor {

	Expr result;

	public static Expr simplify(Expr e) {
		SimplifyConstExprVisitor vis = new SimplifyConstExprVisitor();
		return vis.eval(e);
	}
	
	public Expr eval(Expr expr) {
		if (expr == null) {
			return null;
		}
		Expr prev = result;
		expr.accept(this);
		Expr res = this.result;
		this.result = prev;
		return res;
	}
	
	@Override
	public void caseNumber(NumberExpr p) {
		this.result = p;
	}

	@Override
	public void caseVar(VarExpr p) {
		this.result = p;
	}

	@Override
	public void caseUnaryOp(UnaryOpExpr p) {
		String op = p.getOp();
		Expr evalExpr = eval(p.getExpr());
		switch(op) {
		case "-":
			if (Exprs.is0(evalExpr)) {
				// -0 => 0
				this.result = Exprs.C_0; 
			} else if (evalExpr instanceof NumberExpr) {
				// -(val) => -val
				double v = ((NumberExpr) evalExpr).getValue();
				this.result = Exprs.of(-v);
			} else {
				this.result = Exprs.unaryMinus(evalExpr);
			}
			break;
			
		default:
			this.result = new UnaryOpExpr(op, evalExpr);
		}
	}

	@Override
	public void caseBinaryOp(BinaryOpExpr p) {
		String op = p.getOp();
		Expr lhs = p.getLhs();
		Expr evalLhs = eval(lhs);
		Expr rhs = p.getRhs();
		Expr evalRhs = eval(rhs);
		boolean isLhsNum = evalLhs instanceof NumberExpr;
		boolean isRhsNum = evalRhs instanceof NumberExpr;
		double lhsVal = (isLhsNum)? ((NumberExpr) evalLhs).getValue() : 0.0;
		double rhsVal = (isRhsNum)? ((NumberExpr) evalRhs).getValue() : 0.0;
		switch(op) {
		case "+":
			if (Exprs.is0(evalLhs)) {
				// 0+e => e
				this.result = evalRhs;
			} else if (Exprs.is0(evalRhs)) {
				// e+0 => e
				this.result = evalLhs;
			} else if (isLhsNum && isRhsNum) {
				this.result = Exprs.of(lhsVal + rhsVal);
			} else {
				this.result = Exprs.plus(evalLhs, evalRhs);
			}
			break;

		case "-":
			if (Exprs.is0(evalLhs)) {
				// 0-e => e
				this.result = Exprs.unaryMinus(evalRhs);
			} else if (Exprs.is0(evalRhs)) {
				// e-0 => e
				this.result = evalLhs;
			} else if (isLhsNum && isRhsNum) {
				this.result = Exprs.of(lhsVal - rhsVal);
			} else {
				this.result = Exprs.minus(evalLhs, evalRhs);
			}
			break;

		case "*":
			if (Exprs.is0(evalLhs) || Exprs.is0(evalRhs)) {
				// 0*e => 0, e*0 => 0
				this.result = Exprs.C_0;
			} else if (Exprs.is1(evalRhs)) {
				// e*1 => e
				this.result = evalLhs;
			} else if (Exprs.is1(evalLhs)) {
				// 1*e => e
				this.result = evalRhs;
			} else if (isLhsNum && isRhsNum) {
				this.result = Exprs.of(lhsVal * rhsVal);
			} else {
				this.result = Exprs.mult(evalLhs, evalRhs);
			}
			break;

		case "/":
			if (Exprs.is0(evalRhs)) {
				// NaN !!
				this.result = Exprs.div(evalLhs, evalRhs);
			} else if (Exprs.is1(evalRhs)) {
				this.result = evalLhs;
			} else if (isLhsNum && isRhsNum && rhsVal != 0.0) {
				this.result = Exprs.of(lhsVal / rhsVal);
			} else {
				this.result = Exprs.div(evalLhs, evalRhs);
			}
			break;
			
		default:
			this.result = new BinaryOpExpr(evalLhs, op, evalRhs);
		}
	}

	@Override
	public void caseFunc(FuncExpr p) {
		String funcName = p.getFuncName();
		ImmutableList<Expr> args = p.getArgs();
		List<Expr> tmpEvals = new ArrayList<>(args.size());
		boolean foundDiff = false;
		for(Expr a : args) {
			Expr evalA = eval(a);
			if (evalA != a) {
				foundDiff = true;
			}
			tmpEvals.add(evalA);
		}
		if (!foundDiff) {
			this.result = p;
		} else {
			this.result = Exprs.func(funcName, ImmutableList.copyOf(tmpEvals));
		}
	}
	
}
