package fr.an.testderiv.derivatives;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import fr.an.testderiv.model.Expr;
import fr.an.testderiv.model.Expr.BinaryOpExpr;
import fr.an.testderiv.model.Expr.FuncExpr;
import fr.an.testderiv.model.Expr.NumberExpr;
import fr.an.testderiv.model.Expr.UnaryOpExpr;
import fr.an.testderiv.util.Exprs;

public class BuiltinFuncDerivatives {

	@FunctionalInterface
	public interface FuncArgsBuilder {
		public Expr build(ImmutableList<Expr> args);
	}
	
	static Map<String,FuncArgsBuilder> builtinDerivatives;
	static {
		Map<String,FuncArgsBuilder> b = new HashMap<>();
		// b.put("identity", (args) -> ExprCst.C_1);
		b.put("pow", (args) -> {
			Expr base = args.get(0);
			Expr pow = args.get(1);
			if (pow instanceof NumberExpr) {
				double powNum = ((NumberExpr) pow).getValue();
				if (powNum == 0.0) {
					return Exprs.C_0;
				} if (powNum == 1.0) {
					return base;
				} else {
					return new BinaryOpExpr(pow, 
							"*", new FuncExpr("pow", ImmutableList.of(base, new NumberExpr(powNum - 1.0))));
				}
			}
			throw new UnsupportedOperationException("deriv pow..");
		});
		b.put("sqrt", (args) -> new BinaryOpExpr(Exprs.C_0_5 , "/", new FuncExpr("sqrt", args)));
		
		b.put("sin", (args) -> new FuncExpr("cos", args));
		b.put("cos", (args) -> new UnaryOpExpr("-", new FuncExpr("sin", args)));

		b.put("exp", (args) -> new FuncExpr("exp", args));
		b.put("ln", (args) -> new BinaryOpExpr(Exprs.C_1, "/", args.get(0)));
		
		builtinDerivatives = b;
	}
	
	public static Expr funcDerivativeForArgs(String func, ImmutableList<Expr> args) {
		FuncArgsBuilder builder = builtinDerivatives.get(func);
		if (builder == null) {
			throw new UnsupportedOperationException("deriv " + func);
		}
		return builder.build(args);
	}

}
