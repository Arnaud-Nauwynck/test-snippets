package fr.an.tests.springbootspel;

import java.util.List;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.spel.support.ReflectiveMethodResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyMethodResolver implements MethodResolver {

	ReflectiveMethodResolver delegate = new ReflectiveMethodResolver();
		
	@Override
	public MethodExecutor resolve(
			EvaluationContext context, 
			Object targetObject, String name,
			List<TypeDescriptor> argumentTypes
			) throws AccessException {
		log.info("MyMethodResolver.resolve(.., " + name + ")");
		if (targetObject.getClass() == Foo.class &&  name.equals("myCustomFunc")) {
			return delegate.resolve(context, MyStaticFuncs.class, "resolved_myCustomFoo", argumentTypes);
		}
		return delegate.resolve(context, targetObject, name, argumentTypes);
	}
	
}