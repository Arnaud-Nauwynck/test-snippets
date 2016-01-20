package fr.an.tests.groovy.dslobj;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.Script;

public abstract class AbstractDelegateScript extends Script {
    
    private GroovyObject delegate;

    protected AbstractDelegateScript() {
        super();
    }

    protected AbstractDelegateScript(Binding binding) {
        super(binding);
    }

    public void setDelegate(GroovyObject delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return delegate.invokeMethod(name,args);
    }

    @Override
    public Object getProperty(String property) {
        return delegate.getProperty(property);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        delegate.setProperty(property, newValue);
    }
}