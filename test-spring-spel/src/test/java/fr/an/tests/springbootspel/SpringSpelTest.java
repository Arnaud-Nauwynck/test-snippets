package fr.an.tests.springbootspel;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class SpringSpelTest {

	ExpressionParser parser = new SpelExpressionParser();
	StandardEvaluationContext context;
	Foo rootObject;
	Foo varFoo;
	
	
	@Autowired
	MyBean myBean;
	
	@Autowired
	ApplicationContext applicationContext;
	
	@BeforeEach
	public void setup() {
		this.rootObject = new Foo(123);
		
		this.context = new StandardEvaluationContext(rootObject);
		// SimpleEvaluationContext context = new SimpleEvaluationContext(rootObject); // ?? 
		
		context.setVariable("varInt1", 1);
		this.varFoo = new Foo(2);
		context.setVariable("varFoo", varFoo);
		
		// register functions for all static methods in MyStaticFuncs class
		context.registerMethodFilter(MyStaticFuncs.class, null);

		// explicitly register function with overwritten name
		try {
			Method customF_meth = MyStaticFuncs.class.getDeclaredMethod("customF", int.class);
			context.registerFunction("registered_customF", customF_meth);			
		} catch (NoSuchMethodException | SecurityException e) {
		}

		
		// MethodResolvers, ConstructorResolvers, PropertyAccessors ... 
		
		context.addMethodResolver(new MyMethodResolver());
		
		BeanFactoryResolver defaultBeanResolver = new BeanFactoryResolver(applicationContext);
		context.setBeanResolver(new MyBeanResolver(defaultBeanResolver, myBean));  // '@myResolvedBean' -> myBean
		
	}

	@Test
	public void testEval_int1() {
		val expressionText = "1+0"; 
		evalThenAssertEq(expressionText, 1);
	}

	@Test
	public void testEval_varInt1() {
		val expressionText = "#varInt1"; 
		evalThenAssertEq(expressionText, 1);
	}

	@Test
	public void testEval_varField() {
		val expressionText = "#varFoo.field1"; 
		evalThenAssertEq(expressionText, varFoo.field1);
	}

	@Test
	public void testEval_rootField() {
		val expressionText = "#root.field1"; 
		evalThenAssertEq(expressionText, rootObject.getField1());
	}

	@Test
	public void testEval_rootField_implicit() {
		val expressionText = "field1"; 
		evalThenAssertEq(expressionText, rootObject.getField1());
	}

	@Test
	public void testEval_fooFunc_implicitRoot() {
		val expressionText = "fooFunc(123)"; 
		int expected = rootObject.fooFunc(123);
		evalThenAssertEq(expressionText, expected);
	}

	@Test
	public void testEval_fooFunc_meth() {
		val expressionText = "#root.fooFunc(123)"; 
		int expected = rootObject.fooFunc(123);
		evalThenAssertEq(expressionText, expected);
	}

	@Test
	public void testEval_resolveFunc_myCustomFunc() {
		val expressionText = "myCustomFunc(123)"; 
		int expected = MyStaticFuncs.resolved_myCustomFoo(123);
		evalThenAssertEq(expressionText, expected);
	}
	
	@Test
	public void testEval_bean() {
		val expressionText = "@myBean.foo.field1"; 
		int expected = myBean.foo.field1;
		evalThenAssertEq(expressionText, expected);
	}
	
	@Test
	public void testEval_bean_resolved() {
		val expressionText = "@myResolvedBean.foo.field1"; 
		int expected = myBean.foo.field1;
		evalThenAssertEq(expressionText, expected);
	}
	
	@Test
	public void testEval_registeredFunctions_on_thisFuncs() {
		context.setRootObject(new MyStaticFuncs()); // work only if root object is of type MyStaticFuncs.class??
		
		val expressionText = "customF(1)"; 
		int expected = MyStaticFuncs.customF(1);
		evalThenAssertEq(expressionText, expected);
	}

	@Test
	public void testEval_registeredFunctions_asvar() {
		context.setVariable("F", new MyStaticFuncs());
		
		val expressionText = "#F.customF(1)"; // strange to call as "instance" method for "static" method
		int expected = MyStaticFuncs.customF(1);
		evalThenAssertEq(expressionText, expected);
	}

	@Test
	public void testEval_registeredFunctions_as_static() {
		val expressionText = "T(fr.an.tests.springbootspel.MyStaticFuncs).customF(1)";
		int expected = MyStaticFuncs.customF(1);
		evalThenAssertEq(expressionText, expected);
	}

	@Test
	public void testEval_registeredFunctions_as_classvar() {
		context.setVariable("F", MyStaticFuncs.class);
		val expressionText = "#F.customF(1)";
		int expected = MyStaticFuncs.customF(1);
		evalThenAssertEq(expressionText, expected);
	}

	@Test
	public void testEval_explicitly_registeredFunction_then_FAIL() {
		context.setRootObject(new MyStaticFuncs()); // work only if root object is of type MyStaticFuncs.class??
		
		val expressionText = "registered_customF(1)"; 
		int expected = MyStaticFuncs.customF(1);
		try {
			evalThenAssertEq(expressionText, expected);
			Assertions.fail();
		} catch(SpelEvaluationException ex) {
			// FAIL!!
			// org.springframework.expression.spel.SpelEvaluationException: EL1004E: Method call: Method registered_customF(java.lang.Integer) cannot be found on type fr.an.tests.springbootspel.MyStaticFuncs
			// at org.springframework.expression.spel.ast.MethodReference.findAccessorForMethod(MethodReference.java:226)
			// at org.springframework.expression.spel.ast.MethodReference.getValueInternal(MethodReference.java:135)
			// at org.springframework.expression.spel.ast.MethodReference.getValueInternal(MethodReference.java:95)
			// at org.springframework.expression.spel.ast.SpelNodeImpl.getValue(SpelNodeImpl.java:112)
		}
	}
	
	@Test
	public void testEval_explicitly_registeredFunction_asvar() {
		// context.setRootObject(new MyStaticFuncs()); // work only if root object is of type MyStaticFuncs.class??
		
		val expressionText = "#registered_customF(1)"; 
		int expected = MyStaticFuncs.customF(1);
		evalThenAssertEq(expressionText, expected);
	}

	// ------------------------------------------------------------------------
	
	protected void evalThenAssertEq(String expression, Object expectedValue) {
		Expression parsedExpr = parser.parseExpression(expression);
		Object value = parsedExpr.getValue(context);
		Assertions.assertEquals(expectedValue, value);
	}
}
