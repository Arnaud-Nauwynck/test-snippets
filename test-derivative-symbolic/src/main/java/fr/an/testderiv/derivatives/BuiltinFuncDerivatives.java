package fr.an.testderiv.derivatives;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import fr.an.testderiv.model.Expr;
import fr.an.testderiv.model.Expr.NumberExpr;
import fr.an.testderiv.util.Exprs;

public class BuiltinFuncDerivatives {

	@FunctionalInterface
	public interface FuncArgsBuilder {
		public Expr build(ImmutableList<Expr> args);
	}
	
	static Map<String,FuncArgsBuilder> builtinDerivatives;
	static {
		Map<String,FuncArgsBuilder> b = new HashMap<>();

		b.put("pow_der_arg0", (args) -> {
			// pow(arg0, arg1) = arg0^arg1
			// x^exponent = exp(log(x) * exponent)
			// d/dx (x^exponent) = exponent * x^(exponent-1)   when exponent!=0
			// for exponent integer number : can be prooved by recursion.
			// d/dx (x^exponent) = exponent * (1/x) * exp(log(x) * exponent) = exponent * exp( -log(x) + log(x) * exponent) = exponent * exp( log(x) * (exponent-1))
			Expr base = args.get(0);
			Expr exponent = args.get(1);
			if (exponent instanceof NumberExpr) {
				double powNum = ((NumberExpr) exponent).getValue();
				if (powNum == 0.0) {
					return Exprs.C_0;
				} if (powNum == 1.0) {
					return Exprs.C_1; // ?? base;
				} else if (powNum == 2.0) {
					// simplify.. pow(x,2-1) = pow(x,1) = x
					return Exprs.mult(exponent, base);
				} else {
					return Exprs.mult(exponent, Exprs.pow(base, Exprs.of(powNum - 1.0)));
				}
			} else {
				return Exprs.mult(exponent, Exprs.pow(base, Exprs.minus(exponent, Exprs.C_1)));
			}
		});
		b.put("pow_der_arg1", (args) -> {
			// pow(arg0, arg1) = arg0^arg1
			// base^x = exp(log(base) * x)
			// d/dx( base^x ) = log(base) * exp(log(base) * x) = log(base) * pow(base,x)
			Expr base = args.get(0);
			Expr exponent = args.get(1);
			if (base instanceof NumberExpr) {
				double baseNum = ((NumberExpr) base).getValue();
				if (baseNum == 0.0) {
					// invalid expression !! return anyway as 'ln(0)...'
					return Exprs.mult(Exprs.ln(base), Exprs.pow(base, exponent));
				} if (baseNum == 1.0) {
					// simplify using "ln(1) = 0" 
					return Exprs.C_0;
				} else {
					// do not use numeric approximation! ... 
					// double lnBase ~= Math.log(lnBase);
					// return Exprs.mult(Exprs.of(lnBase), Exprs.pow(base, exponent));
				}
			}
			return Exprs.mult(Exprs.ln(base), Exprs.pow(base, exponent)); 
		});
		
		// (x^2)' = 2 * x 
		b.put("square", (args) -> Exprs.mult(Exprs.C_2, args.get(0)));
		
		// (sqrt(x))' = 1 / (2 * sqrt(x)) 
		b.put("sqrt", (args) -> Exprs.div(Exprs.C_0_5 , Exprs.func("sqrt", args)));
		
		// (sin(x))' = cos(x) 
		b.put("sin", (args) -> Exprs.cos(args.get(0)));
		// (cos(x))' = sin(x) 
		b.put("cos", (args) -> Exprs.unaryMinus(Exprs.sin(args.get(0))));

		// (exp(x))' = exp(x) 
		b.put("exp", (args) -> Exprs.exp(args.get(0)));
		// (ln(x))' = 1/x 
		b.put("ln", (args) -> Exprs.inv(args.get(0)));
		
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
