package fr.an.testderiv.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import fr.an.testderiv.model.Expr;
import fr.an.testderiv.model.Expr.BinaryOpExpr;
import fr.an.testderiv.model.Expr.FuncExpr;
import fr.an.testderiv.model.Expr.NumberExpr;
import fr.an.testderiv.model.Expr.UnaryOpExpr;
import fr.an.testderiv.model.Expr.VarExpr;
import fr.an.testderiv.model.ExprVisitor;

public class DumpExprVisitor extends ExprVisitor {

	private PrintWriter out;
	
	public DumpExprVisitor(PrintWriter out) {
		this.out = out;
	}

	public static String dumpToString(Expr expr) {
		if (expr == null) {
			return "null";
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(bout);
		expr.accept(new DumpExprVisitor(out));
		out.flush();
		return bout.toString();
	}
	
	protected void recurse(Expr expr) {
		if (expr != null) {
			out.print("(");
			expr.accept(this);
			out.print(")");
		}
	}
	
	protected void recurseLs(List<Expr> exprs) {
		for (Iterator<Expr> iter = exprs.iterator(); iter.hasNext();) {
			Expr expr = iter.next();
			if (expr != null) {
				expr.accept(this);
			}
			if (iter.hasNext()) {
				out.print(", ");
			}
		}
	}
	
	@Override
	public void caseNumber(NumberExpr p) {
		out.print(p.getValue());
	}

	@Override
	public void caseVar(VarExpr p) {
		out.print(p.getName());
	}

	@Override
	public void caseUnaryOp(UnaryOpExpr p) {
		out.print(p.getOp());
		recurse(p.getExpr());
	}

	@Override
	public void caseBinaryOp(BinaryOpExpr p) {
		recurse(p.getLhs());
		out.print(p.getOp());
		recurse(p.getRhs());
	}

	@Override
	public void caseFunc(FuncExpr p) {
		out.print(p.getFuncName());
		out.print("(");
		recurseLs(p.getArgs());
		out.print(")");
	}

	
}
