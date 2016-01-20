package fr.an.tests.groovy;

import org.junit.Test;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class GroovyShellTest {

    @Test
    public void testGroovyShell_evaluate() {
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        binding.setVariable("x", 1);              
        binding.setVariable("y", 3);
        Object res = shell.evaluate("z=2*x+y");
        System.out.println("eval => " + res);
    }
}
