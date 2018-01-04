package fr.an.testderiv.model;

import fr.an.testderiv.model.Expr.BinaryOpExpr;
import fr.an.testderiv.model.Expr.FuncExpr;
import fr.an.testderiv.model.Expr.NumberExpr;
import fr.an.testderiv.model.Expr.UnaryOpExpr;
import fr.an.testderiv.model.Expr.VarExpr;

public abstract class ExprVisitor {

	public abstract void caseNumber(NumberExpr p);
	public abstract void caseVar(VarExpr p);
	public abstract void caseUnaryOp(UnaryOpExpr p);
	public abstract void caseBinaryOp(BinaryOpExpr p);
	public abstract void caseFunc(FuncExpr p);
	
}
