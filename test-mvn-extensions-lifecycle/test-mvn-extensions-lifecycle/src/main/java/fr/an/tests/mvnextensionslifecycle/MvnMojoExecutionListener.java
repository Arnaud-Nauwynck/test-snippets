package fr.an.tests.mvnextensionslifecycle;

import org.apache.maven.execution.MojoExecutionEvent;
import org.apache.maven.execution.MojoExecutionListener;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;

@Component( role = MojoExecutionListener.class, hint = "test-lifecycle" )
public class MvnMojoExecutionListener
    implements MojoExecutionListener
{
    public void beforeMojoExecution( MojoExecutionEvent event )
        throws MojoExecutionException
    {
        System.out.println( "#### MojoExecutionListener.beforeMojoExecution" );
    }

    public void afterMojoExecutionSuccess( MojoExecutionEvent event )
        throws MojoExecutionException
    {
        System.out.println( "#### MojoExecutionListener.afterMojoExecutionSuccess" );
    }

    public void afterExecutionFailure( MojoExecutionEvent event )
    {
        System.out.println( "#### MojoExecutionListener.afterExecutionFailure" );
    }

}
