package fr.an.testsjdk8mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 
 */
@Mojo( name = "hello")
public class HelloMojo extends AbstractMojo {
	
	@Parameter(property = "message", defaultValue="Hello World")
	private String message;
	
    public void execute() {
    	// test for lambda in maven mojo... (need maven-plugin-plugin >= 3.3 ... default: 3.2 !!)
    	Runnable r = () -> { 
    		getLog().info(message);
    	};
    	r.run();
    }
    
}