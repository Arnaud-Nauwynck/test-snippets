package fr.an.tests.testmojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo( name = "test1", requiresProject = false )
public class Mojo1 extends AbstractMojo
{
    /**
     * test..
     */
    @org.apache.maven.plugins.annotations.Parameter( property = "param1" )
    private String param1;

    /**
     * test..
     */
    @org.apache.maven.plugins.annotations.Parameter( property = "param2", defaultValue = "value2" )
    private String param2;
    
    public String getParam1()
    {
        return param1;
    }


    public void setParam1( String param1 )
    {
        System.out.println( "################ Mojo1.setParam1 " + ExUtils.currentStackTraceShortPath());
        this.param1 = param1;
    }

    public String getParam2()
    {
        return param2;
    }


    public void setParam2( String param2 )
    {
        System.out.println( "################ Mojo1.setParam2 " + ExUtils.currentStackTraceShortPath());
        this.param2 = param2;
    }


    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        System.out.println( "#### Mojo1.execute" );
    }
 
    
}
