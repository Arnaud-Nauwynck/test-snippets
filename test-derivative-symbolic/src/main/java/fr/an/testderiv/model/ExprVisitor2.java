package fr.an.testderiv.model;

import fr.an.testderiv.model.Expr.BinaryOpExpr;
import fr.an.testderiv.model.Expr.FuncExpr;
import fr.an.testderiv.model.Expr.NumberExpr;
import fr.an.testderiv.model.Expr.UnaryOpExpr;
import fr.an.testderiv.model.Expr.VarExpr;

public abstract class ExprVisitor2<R,T> {

	public abstract R caseNumber(NumberExpr p, T args);
	public abstract R caseVar(VarExpr p, T args);
	public abstract R caseUnaryOp(UnaryOpExpr p, T args);
	public abstract R caseBinaryOp(BinaryOpExpr p, T args);
	public abstract R caseFunc(FuncExpr p, T args);
	
}
