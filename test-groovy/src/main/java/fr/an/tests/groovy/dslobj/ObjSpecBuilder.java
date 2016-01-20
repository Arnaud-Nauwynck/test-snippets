package fr.an.tests.groovy.dslobj;

import java.util.List;
import java.util.Stack;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.tests.groovy.objspec.ObjSpec;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

public class ObjSpecBuilder extends GroovyObjectSupport { // cf BuilderSupport

    
    private static final Logger LOG = LoggerFactory.getLogger(ObjSpecBuilder.ObjecSpecBuilderContext.class);
    
    public static class ObjecSpecBuilderContext {
        ObjSpec objSpec;
    }
    
    private Stack<ObjecSpecBuilderContext> contextStack = new Stack<ObjecSpecBuilderContext>();
    
//    public void push(ChildId) {
//    }
//    public void pop() {
//    }
    
    public static <T> T evalChildClosure(Object parentObj, Object childObj, Closure<T> cl) {
        Closure<T> code = cl.rehydrate(childObj, parentObj, parentObj);
        code.setResolveStrategy(Closure.DELEGATE_ONLY);
//        code.setDelegate(childObj);
        return code.call();
    }
    
    
    @Override
    public Object invokeMethod(String methodName, Object args) {
        List<?> list = InvokerHelper.asList(args);
        LOG.info("invokeMethod " + methodName + " list:" + list);
        for(int i = 0; i < list.size(); i++) {
            Object elt = list.get(i);
            if (elt instanceof Closure) {
                LOG.info("invoke Closure[" + i + "] ...");
                evalChildClosure(this, this, (Closure) elt);
                LOG.info("... done invoke Closure[" + i + "]");
            }
        }
        return this;
    }
    
    public Object getProperty(String property) {
        Object res = null;
        // Object res = super.getProperty(property);
        LOG.info("getProperty " + property + " => " + res);
        return res;
    }

    public void setProperty(String property, Object newValue) {
        LOG.info("setProperty " + property + " = " + newValue);
        // super.setProperty(property, newValue);
    }
    
    public void setField(String nsChldId, String value) {
        LOG.info("setFieldValue " + nsChldId + " = " + value);
    }
    
    public void setField(String nsChldId, Closure<?> cl) {
        LOG.info("setFieldClosure " + nsChldId + " = { " + cl + " }");
        // evalChildClosure();
    }

    public void obj(String nsChldId, Closure<?> cl) {
        LOG.info("obj " + nsChldId + " cl:" + cl);
        evalChildClosure(this, this, (Closure) cl);
    }

    public void obj(String nsChldId, String param1, Closure<?> cl) {
        LOG.info("obj " + nsChldId + ", param1:" + param1 + " cl:" + cl);
        evalChildClosure(this, this, (Closure) cl);
    }

}
