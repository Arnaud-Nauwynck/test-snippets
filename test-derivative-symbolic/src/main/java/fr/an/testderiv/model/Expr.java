package fr.an.testderiv.model;

import com.google.common.collect.ImmutableList;

public abstract class Expr {

	public abstract void accept(ExprVisitor visitor);

	/**
	 * 
	 */
	public static class NumberExpr extends Expr {
		private final double value;

		public NumberExpr(double value) {
			this.value = value;
		}

		public double getValue() {
			return value;
		}
		
		@Override
		public void accept(ExprVisitor visitor) {
			visitor.caseNumber(this);
		}

	}


	/**
	 * 
	 */
	public static class VarExpr extends Expr {
		private final String name;

		public VarExpr(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		@Override
		public void accept(ExprVisitor visitor) {
			visitor.caseVar(this);
		}
		
	}

	/**
	 * 
	 */
	public static class UnaryOpExpr extends Expr {
		private final String op;
		private final Expr expr;
		
		public UnaryOpExpr(String op, Expr expr) {
			this.op = op;
			this.expr = expr;
		}

		public String getOp() {
			return op;
		}

		public Expr getExpr() {
			return expr;
		}

		@Override
		public void accept(ExprVisitor visitor) {
			visitor.caseUnaryOp(this);
		}
		
	}

	/**
	 * 
	 */
	public static class BinaryOpExpr extends Expr {
		private final Expr lhs;
		private final String op;
		private final Expr rhs;
		
		public BinaryOpExpr(Expr lhs, String op, Expr rhs) {
			this.lhs = lhs;
			this.op = op;
			this.rhs = rhs;
		}
		
		public Expr getLhs() {
			return lhs;
		}

		public String getOp() {
			return op;
		}

		public Expr getRhs() {
			return rhs;
		}
		
		@Override
		public void accept(ExprVisitor visitor) {
			visitor.caseBinaryOp(this);
		}
		
	}

	/**
	 * 
	 */
	public static class FuncExpr extends Expr {
		private final String funcName;
		private final ImmutableList<Expr> args;
		
		public FuncExpr(String funcName, ImmutableList<Expr> args) {
			this.funcName = funcName;
			this.args = args;
		}

		public FuncExpr(String funcName, Expr arg0) {
			this(funcName, ImmutableList.of(arg0));
		}
		public FuncExpr(String funcName, Expr arg0, Expr arg1) {
			this(funcName, ImmutableList.of(arg0, arg1));
		}
		public FuncExpr(String funcName, Expr arg0, Expr arg1, Expr arg2) {
			this(funcName, ImmutableList.of(arg0, arg1, arg2));
		}
		
		public String getFuncName() {
			return funcName;
		}
		
		public ImmutableList<Expr> getArgs() {
			return args;
		}

		@Override
		public void accept(ExprVisitor visitor) {
			visitor.caseFunc(this);
		}
		
	}

	
}
