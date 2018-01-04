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
 * a Expr evaluator to return simplification for basic associative terms with  a*b*c, a+b+c ..
 */
public class SimplifyNumAssocExprVisitor extends ExprVisitor {

	Expr result;

	public static Expr simplify(Expr e) {
		SimplifyNumAssocExprVisitor vis = new SimplifyNumAssocExprVisitor();
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
	
	public static boolean isNum(Expr e) {
		return e instanceof NumberExpr;
	}
	public static double asNum(Expr e) {
		return ((NumberExpr) e).getValue();
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
		this.result = p;
	}

	protected static BinaryOpExpr binaryOpOrSame(BinaryOpExpr prev, 
			Expr evalLhs, String op, Expr evalRhs) {
		if (prev.getLhs() == evalLhs && prev.getRhs() == evalRhs && prev.getOp() == op) {
			return prev;
		}
		return Exprs.binaryOp(evalLhs, op, evalRhs);
	}
	
	protected static boolean canCombineBinOps(String op1, String op2) {
		switch(op1) {
		case "+": return op2.equals("+") || op2.equals("-");
		case "-": return op2.equals("+") || op2.equals("-");
		case "*": return op2.equals("*") || op2.equals("/");
		case "/": return op2.equals("*") || op2.equals("/");
		default: return false;
		}
	}

	protected static double binaryOpVal(double l, String op, double r) {
		switch(op) {
		case "+": return l+r;
		case "-": return l-r;
		case "*": return l*r;
		case "/": return l/r;
		default: throw new UnsupportedOperationException();
		}
	}


	protected static String combineOp(String op1, String op2) {
		switch(op1) {
		case "+": {
			switch(op2) {
			case "+": return "+"; // . + (. + c) => . + c
			case "-": return "-"; // . + (. - c) => . - c
			default: throw new UnsupportedOperationException();
			}
		}
		case "-": {
			switch(op2) {
			case "+": return "-"; // . - (. + c) => . - c
			case "-": return "+"; // . - (. - c) => . + c
			default: throw new UnsupportedOperationException();
			}
		} 
		case "*": {
			switch(op2) {
			case "*": return "*"; // . * (. * c) => . * c
			case "/": return "/"; // . * (. / c) => . / c
			default: throw new UnsupportedOperationException();
			}
		}
		case "/": {
			switch(op2) {
			case "*": return "/"; // . / (. * c) => . / c
			case "/": return "*"; // . / (. / c) => . * c
			default: throw new UnsupportedOperationException();
			}
		} 
		default: throw new UnsupportedOperationException();
		}
	}


	
	@Override
	public void caseBinaryOp(BinaryOpExpr p) {
		String op = p.getOp();
		Expr lhs = p.getLhs();
		Expr evalLhs = eval(lhs);
		Expr rhs = p.getRhs();
		Expr evalRhs = eval(rhs);
		boolean isLhsBin = evalLhs instanceof BinaryOpExpr;
		boolean isRhsBin = evalRhs instanceof BinaryOpExpr;
		
		if (!isLhsBin && !isRhsBin) {
			this.result = binaryOpOrSame(p, evalLhs, op, evalRhs);
			return;
		}
		
		
		if (isRhsBin) { 
			String rhsBinOp = ((BinaryOpExpr) evalRhs).getOp();
			if (canCombineBinOps(op, rhsBinOp)) {
				// lhs*(rhs1*rhs2) or lhs*(rhs1/rhs2)  or lhs+(rhs1+rhs2) or ... 
				Expr rhs1 = ((BinaryOpExpr) evalRhs).getLhs();
				Expr rhs2 = ((BinaryOpExpr) evalRhs).getRhs();
	
				if (isNum(evalLhs)) {
					double lhsVal = asNum(evalLhs);
					
					if (isNum(rhs1)) {
						// lhs:num op (rhs1:num rhsBinOp rhs2) =>  (val) op.. rhs2  
						double rhs1Val = asNum(rhs1);
						double val = binaryOpVal(lhsVal, op, rhs1Val);
						String newOp = combineOp(op, rhsBinOp);
						this.result = Exprs.binaryOp(val, newOp, rhs2);
						return;
					} else if (isNum(rhs2)) {
						// lhs:num op (rhs1 rhsBinOp rhs2:num) =>  (val) op rhs1  
						double rhs2Val = asNum(rhs2);
						String newOp = combineOp(op, rhsBinOp);
						double val = binaryOpVal(lhsVal, newOp, rhs2Val);
						this.result = Exprs.binaryOp(val, op, rhs1);
						return;
							
					} else {
						this.result = binaryOpOrSame(p, evalLhs, op, evalRhs);
						return;
					}
				}
			}
		} else if (isLhsBin) { 
			// TOCHANGE: reuse code by symmetry?
			String lhsBinOp = ((BinaryOpExpr) evalLhs).getOp();
			if (canCombineBinOps(op, lhsBinOp)) {
				// (lhs1*lhs2)*rhs2) or (lhs1*lhs2)/rhs2)  or ...
				Expr lhs1 = ((BinaryOpExpr) evalLhs).getLhs();
				Expr lhs2 = ((BinaryOpExpr) evalLhs).getRhs();
				
				if (isNum(evalRhs)) {
					double rhsVal = asNum(evalRhs);
					
					if (isNum(lhs1)) {
						// (lhs1:num lhsBinOp lhs2) op rhs:num =>  (val) op lhs2
						double lhs1Val = asNum(lhs1);
						double val = binaryOpVal(lhs1Val, op, rhsVal);
						this.result = Exprs.binaryOp(val, lhsBinOp, lhs2);
						return;
					} else if (isNum(lhs2)) {
						// (lhs1 lhsBinOp lhs2:num) op rhs:num =>  lhs op.. (val) 
						double lhs2Val = asNum(lhs2);
						String newOp = combineOp(op, lhsBinOp);
						double val = binaryOpVal(lhs2Val, newOp, rhsVal);
						this.result = Exprs.binaryOp(lhs1, op, Exprs.of(val));
						return;
							
					} else {
						this.result = binaryOpOrSame(p, evalLhs, op, evalRhs);
						return;
					}
				}
			}
			
		}
			
		this.result = binaryOpOrSame(p, evalLhs, op, evalRhs);
		return;
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
