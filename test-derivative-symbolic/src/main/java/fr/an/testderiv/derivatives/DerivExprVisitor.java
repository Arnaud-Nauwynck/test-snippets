package fr.an.testderiv.derivatives;

import com.google.common.collect.ImmutableList;

import fr.an.testderiv.model.Expr;
import fr.an.testderiv.model.Expr.BinaryOpExpr;
import fr.an.testderiv.model.Expr.FuncExpr;
import fr.an.testderiv.model.Expr.NumberExpr;
import fr.an.testderiv.model.Expr.UnaryOpExpr;
import fr.an.testderiv.model.Expr.VarExpr;
import fr.an.testderiv.model.ExprVisitor;
import fr.an.testderiv.util.Exprs;

public class DerivExprVisitor extends ExprVisitor {

	private final String derivVarName;
	Expr result;

	
	public DerivExprVisitor(String derivVarName) {
		this.derivVarName = derivVarName;
	}

	public static Expr derivBy(Expr expr, String derivVarName) {
		DerivExprVisitor vis = new DerivExprVisitor(derivVarName);
		return vis.eval(expr);
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
		this.result = Exprs.C_0;
	}

	@Override
	public void caseVar(VarExpr p) {
		if (p.getName().equals(derivVarName)) {
			this.result = Exprs.C_1;
		} else {
			this.result = Exprs.C_0;
		}
	}

	@Override
	public void caseUnaryOp(UnaryOpExpr p) {
		String op = p.getOp();
		switch(op) {
		case "-":
			Expr e = eval(p.getExpr());
			this.result = new UnaryOpExpr("-", e);
			break;
			
		default:
			throw new UnsupportedOperationException("derivative op " + op);
			
		}
	}

	@Override
	public void caseBinaryOp(BinaryOpExpr p) {
		String op = p.getOp();
		Expr lhs = p.getLhs();
		Expr derLhs = eval(lhs);
		Expr rhs = p.getRhs();
		Expr derRhs = eval(rhs);
		switch(op) {
		case "+":
			this.result = Exprs.plus(derLhs, derRhs);
			break;

		case "-":
			this.result = Exprs.minus(derLhs, derRhs);
			break;

		case "*":
			// (f*g)' = f' * g + f * g'
			this.result = Exprs.plus(Exprs.mult(derLhs, rhs), Exprs.mult(lhs, derRhs));
			break;

		case "/":
			// (f / g)' = (f' * g - f * g' ) / g^2
			this.result = Exprs.div( 
					Exprs.minus(Exprs.mult(derLhs, rhs), Exprs.mult(lhs, derRhs)),
					Exprs.square(rhs));
			break;

		default:
			throw new UnsupportedOperationException("derivative op " + op);
			
		}
	}

	@Override
	public void caseFunc(FuncExpr p) {
		String funcName = p.getFuncName();
		ImmutableList<Expr> args = p.getArgs();
		if (args.size() == 1) {
			// f(g(x))' = g'(x) * f'(g(x))
			Expr arg0 = args.get(0);
			Expr derArg = eval(arg0);
			Expr derFuncArg = BuiltinFuncDerivatives.funcDerivativeForArgs(funcName, args);
			this.result = Exprs.mult(derArg, derFuncArg);
		} else if (funcName.equals("pow") && args.size() == 2) {
			Expr arg0 = args.get(0);
			Expr arg1 = args.get(1);
			Expr derArg0 = eval(arg0);
			Expr derArg1 = eval(arg1);
			Expr derFuncArg0 = BuiltinFuncDerivatives.funcDerivativeForArgs("pow_der_arg0", args);
			Expr derFuncArg1 = BuiltinFuncDerivatives.funcDerivativeForArgs("pow_der_arg1", args);
			// d/dx pow(arg0(x),arg1(x)) = d/dx arg0(x) * d/darg0 pow(..) + d/dx arg1(x) * d/darg1 pow(..) 
			this.result = Exprs.plus(Exprs.mult(derArg0, derFuncArg0), Exprs.mult(derArg1, derFuncArg1));
		} else {
			// throw new UnsupportedOperationException("derivative func multivar");
			this.result = Exprs.func("der", ImmutableList.of(Exprs.func(funcName, args), Exprs.var(derivVarName)));
		}
	}
	
}
