package fr.an.tests.groovy

import fr.an.tests.groovy.hello;

class hello {

    public static void main(String[] args) {
        def app = new hello();
        
        print "hello"
        def i = 0;
        i = "test"
        app.foo()
    }
    
    public void foo() {
        print "foo"
        def i = 0;
        i = "test"
    }
}
