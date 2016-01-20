package fr.an.tests.groovy.dslobj;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class ObjSpecDslParser {
    
    private static final Logger LOG = LoggerFactory.getLogger(ObjSpecDslParser.class);
    
    public Object parse(String scriptText) {
        ObjSpecBuilder dslBuilder = new ObjSpecBuilder();
        
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(AbstractDelegateScript.class.getName());
        Binding binding = new Binding();
        GroovyShell sh = new GroovyShell(getClass().getClassLoader(), binding, cc);
        
        Object res;
        try {
            AbstractDelegateScript parsedScript = (AbstractDelegateScript) sh.parse(scriptText);
            parsedScript.setDelegate(dslBuilder);
            res = parsedScript.run();
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute script {{{\n" + scriptText + "\n}}}", e);
        }
        return res;
    }
    
}
