package fr.an.tests.springbootspel;

import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyBeanResolver implements BeanResolver {

	BeanFactoryResolver delegate;
	MyBean myBean;
	
	
	public MyBeanResolver(BeanFactoryResolver delegate, MyBean myBean) {
		this.delegate = delegate;
		this.myBean = myBean;
	}


	@Override
	public Object resolve(EvaluationContext context, String beanName) throws AccessException {
		log.info("resolve(" + beanName + ")");
		Object res = null;
		if (beanName.equals("myResolvedBean")) {
			res = myBean;
		} else {
			res = delegate.resolve(context, beanName);
		}
		return res;
	}
	
}